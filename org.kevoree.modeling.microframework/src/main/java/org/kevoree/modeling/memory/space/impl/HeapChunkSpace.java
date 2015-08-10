
package org.kevoree.modeling.memory.space.impl;

import org.kevoree.modeling.KConfig;
import org.kevoree.modeling.KContentKey;
import org.kevoree.modeling.memory.KChunk;
import org.kevoree.modeling.memory.chunk.KObjectChunk;
import org.kevoree.modeling.memory.chunk.impl.HeapObjectChunk;
import org.kevoree.modeling.memory.chunk.impl.ArrayLongLongMap;
import org.kevoree.modeling.memory.space.KChunkIterator;
import org.kevoree.modeling.memory.space.KChunkTypes;
import org.kevoree.modeling.memory.space.KChunkSpace;
import org.kevoree.modeling.memory.chunk.impl.ArrayLongLongTree;
import org.kevoree.modeling.memory.chunk.impl.ArrayLongTree;
import org.kevoree.modeling.meta.KMetaModel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

public class HeapChunkSpace implements KChunkSpace {

    private static final float LOAD_FACTOR = ((float) 75 / (float) 100);

    private final AtomicReference<InternalState> _state;

    private final AtomicReference<InternalDirtyState> _dirtyState;

    final class InternalState {

        public volatile boolean sparse = false;

        public final int elementDataSize;

        public final long[] elementK3;

        public final int[] elementNext;

        public final AtomicIntegerArray elementHash;

        public final KChunk[] values;

        final AtomicInteger _elementCount;

        final AtomicInteger _valuesIndex;

        int _threshold;

        public InternalState(int p_elementDataSize, long[] p_elementKE, int[] p_elementNext, int[] p_elementHash, KChunk[] p_values) {
            this.elementDataSize = p_elementDataSize;
            this.elementK3 = p_elementKE;
            this.elementNext = p_elementNext;
            this.elementHash = new AtomicIntegerArray(p_elementHash);
            this.values = p_values;
            this._elementCount = new AtomicInteger(0);
            this._valuesIndex = new AtomicInteger(0);
        }
    }

    final class InternalDirtyState {

        public volatile long[] _dirtyList;

        public final AtomicInteger _dirtyIndex;

        public InternalDirtyState() {
            this._dirtyList = new long[KConfig.CACHE_INIT_SIZE * 3];
            this._dirtyIndex = new AtomicInteger(0);
        }

        public void declareDirty(long universe, long time, long obj) {
            int nextIndex = this._dirtyIndex.getAndIncrement() * 3;
            //simple case
            if (nextIndex + 2 < this._dirtyList.length) {
                this._dirtyList[nextIndex] = universe;
                this._dirtyList[nextIndex + 1] = time;
                this._dirtyList[nextIndex + 2] = obj;
            } else {
                reallocate(nextIndex);
                this._dirtyList[nextIndex] = universe;
                this._dirtyList[nextIndex + 1] = time;
                this._dirtyList[nextIndex + 2] = obj;
            }
        }

        private synchronized void reallocate(int wantedIndex) {
            if (wantedIndex + 2 >= this._dirtyList.length) {
                int newlength = wantedIndex << 1;
                long[] previousList = this._dirtyList;
                this._dirtyList = new long[newlength];
                System.arraycopy(previousList, 0, this._dirtyList, 0, wantedIndex);
            }
        }
    }

    public HeapChunkSpace() {
        this._dirtyState = new AtomicReference<InternalDirtyState>();
        this._state = new AtomicReference<InternalState>();
        this._dirtyState.set(new InternalDirtyState());
        int initialCapacity = KConfig.CACHE_INIT_SIZE;
        InternalState newstate = new InternalState(initialCapacity, new long[initialCapacity * 3], new int[initialCapacity], new int[initialCapacity], new KChunk[initialCapacity]);
        for (int i = 0; i < initialCapacity; i++) {
            newstate.elementNext[i] = -1;
            newstate.elementHash.set(i, -1);
        }
        newstate._threshold = (int) (newstate.elementDataSize * LOAD_FACTOR);
        this._state.set(newstate);
    }

    @Override
    public final KChunk get(long universe, long time, long obj) {
        InternalState internalState = _state.get();
        if (internalState.elementDataSize == 0) {
            return null;
        }
        int index = (((int) (universe ^ time ^ obj)) & 0x7FFFFFFF) % internalState.elementDataSize;
        int m = internalState.elementHash.get(index);
        while (m != -1) {
            if (universe == internalState.elementK3[(m * 3)] && time == internalState.elementK3[((m * 3) + 1)] && obj == internalState.elementK3[((m * 3) + 2)]) {
                return internalState.values[m]; /* getValue */
            } else {
                m = internalState.elementNext[m];
            }
        }
        return null;
    }

    @Override
    public KChunk create(long universe, long time, long obj, short type) {
        KChunk newElement = internal_createElement(universe, time, obj, type);
        return internal_put(universe, time, obj, newElement);
    }

    @Override
    public KObjectChunk clone(KObjectChunk previousElement, long newUniverse, long newTime, long newObj, KMetaModel metaModel) {
        return (KObjectChunk) internal_put(newUniverse, newTime, newObj, previousElement.clone(newUniverse, newTime, newObj, metaModel));
    }

    private KChunk internal_createElement(long p_universe, long p_time, long p_obj, short type) {
        switch (type) {
            case KChunkTypes.OBJECT_CHUNK:
                return new HeapObjectChunk(p_universe, p_time, p_obj, this);
            case KChunkTypes.LONG_LONG_MAP:
                return new ArrayLongLongMap(p_universe, p_time, p_obj, this);
            case KChunkTypes.LONG_TREE:
                return new ArrayLongTree(p_universe, p_time, p_obj, this);
            case KChunkTypes.LONG_LONG_TREE:
                return new ArrayLongLongTree(p_universe, p_time, p_obj, this);
            default:
                return null;
        }
    }

    private KChunk internal_put(long universe, long time, long p_obj, KChunk payload) {
        InternalState currentState;
        InternalState nextState;
        KChunk result;
        int nbTry = 0;
        do {
            currentState = _state.get();
            int entry = -1;
            int index = -1;
            int hash = (int) (universe ^ time ^ p_obj);
            if (currentState.elementDataSize != 0) {
                index = (hash & 0x7FFFFFFF) % currentState.elementDataSize;
                entry = findNonNullKeyEntry(universe, time, p_obj, index, currentState);
            }
            if (entry == -1) {
                //value has to be really inserted
                int nextValueIndex = currentState._valuesIndex.getAndIncrement();
                if (nextValueIndex > currentState._threshold) {
                    return complex_insert(universe, time, p_obj, payload, hash, nextValueIndex);
                } else {
                    nextState = currentState;
                }
                nextState.elementK3[(nextValueIndex * 3)] = universe;
                nextState.elementK3[((nextValueIndex * 3) + 1)] = time;
                nextState.elementK3[((nextValueIndex * 3) + 2)] = p_obj;
                nextState.values[nextValueIndex] = payload;

                nextState.elementNext[nextValueIndex] = nextState.elementHash.getAndSet(index, nextValueIndex);
                nextState._elementCount.incrementAndGet();
                result = payload;
            } else {
                nextState = currentState;
                result = nextState.values[entry];
            }
            nbTry++;
            if (nbTry == KConfig.CAS_MAX_TRY) {
                throw new RuntimeException("CompareAndSwap error, failed to converge");
            }
        } while (!_state.compareAndSet(currentState, nextState));
        return result;
    }

    private synchronized KChunk complex_insert(long universe, long time, long p_obj, KChunk payload, int prehash, int nextValueIndex) {
        InternalState currentState;
        InternalState nextState;
        do {
            currentState = _state.get();
            if (nextValueIndex > currentState._threshold) {
                nextState = rehashCapacity(currentState);
            } else {
                nextState = currentState;
            }
            int index = (prehash & 0x7FFFFFFF) % nextState.elementDataSize;
            nextState.elementK3[(nextValueIndex * 3)] = universe;
            nextState.elementK3[((nextValueIndex * 3) + 1)] = time;
            nextState.elementK3[((nextValueIndex * 3) + 2)] = p_obj;
            nextState.values[nextValueIndex] = payload;
            nextState.elementNext[nextValueIndex] = nextState.elementHash.getAndSet(index, nextValueIndex);
            nextState._elementCount.incrementAndGet();
        } while (!_state.compareAndSet(currentState, nextState));
        return payload;
    }

    private InternalState rehashCapacity(InternalState previousState) {
        int length = (previousState.elementDataSize == 0 ? 1 : previousState.elementDataSize << 1);
        long[] newElementKV = new long[length * 3];
        KChunk[] newValues = new KChunk[length];
        boolean previousIsSparse = previousState.sparse;
        if (!previousIsSparse) {
            System.arraycopy(previousState.elementK3, 0, newElementKV, 0, previousState.elementDataSize * 3);
            System.arraycopy(previousState.values, 0, newValues, 0, previousState.elementDataSize);
        }
        int[] newElementNext = new int[length];
        int[] newElementHash = new int[length];
        for (int i = 0; i < length; i++) {
            newElementNext[i] = -1;
            newElementHash[i] = -1;
        }
        //rehashEveryThing
        int currentIndex = 0;
        for (int i = 0; i < previousState.elementDataSize; i++) {
            if (previousState.values[i] != null) { //there is a real value
                int hash = (int) (previousState.elementK3[(i * 3)] ^ previousState.elementK3[(i * 3) + 1] ^ previousState.elementK3[(i * 3) + 2]);
                int index = (hash & 0x7FFFFFFF) % length;
                newElementNext[i] = newElementHash[index];
                newElementHash[index] = i;
                if (previousIsSparse) {
                    newValues[currentIndex] = previousState.values[i];
                    newElementKV[(currentIndex * 3)] = previousState.elementK3[(i * 3)];
                    newElementKV[(currentIndex * 3) + 1] = previousState.elementK3[(i * 3) + 1];
                    newElementKV[(currentIndex * 3) + 2] = previousState.elementK3[(i * 3) + 2];
                    currentIndex++;
                }
            }
        }
        //setPrimitiveType value for all
        InternalState newState = new InternalState(length, newElementKV, newElementNext, newElementHash, newValues);
        newState._threshold = (int) (length * LOAD_FACTOR);
        newState._valuesIndex.set(previousState._valuesIndex.get());
        if (previousIsSparse) {
            newState._elementCount.set(currentIndex);
            newState._valuesIndex.set(currentIndex);
        } else {
            newState._elementCount.set(previousState._elementCount.get());
            newState._valuesIndex.set(previousState._valuesIndex.get());
        }
        return newState;
    }

    final int findNonNullKeyEntry(long universe, long time, long obj, int index, InternalState internalState) {
        int m = internalState.elementHash.get(index);
        while (m >= 0) {
            if (universe == internalState.elementK3[m * 3] && time == internalState.elementK3[(m * 3) + 1] && obj == internalState.elementK3[(m * 3) + 2]) {
                return m;
            }
            m = internalState.elementNext[m];
        }
        return -1;
    }

    @Override
    public final int size() {
        return this._state.get()._elementCount.get();
    }

    @Override
    public KChunkIterator detachDirties() {
        InternalDirtyState detachedState = _dirtyState.getAndSet(new InternalDirtyState());
        int maxIndex = detachedState._dirtyIndex.get();
        long[] shrinked = new long[maxIndex * 3];
        System.arraycopy(detachedState._dirtyList, 0, shrinked, 0, maxIndex * 3);
        return new ChunkIterator(shrinked, this);
    }

    @Override
    public void declareDirty(KChunk dirtyChunk) {
        int nbTry = 0;
        InternalDirtyState current;
        do {
            current = _dirtyState.get();
            current.declareDirty(dirtyChunk.universe(), dirtyChunk.time(), dirtyChunk.obj());
            nbTry++;
            if (nbTry == KConfig.CAS_MAX_TRY) {
                throw new RuntimeException("CompareAndSwap error, failed to converge");
            }
        } while (!_dirtyState.compareAndSet(current, current));

    }

    @Override
    public void remove(long universe, long time, long obj, KMetaModel p_metaModel) {
        InternalState previousState;
        int nbTry = 0;
        do {
            previousState = _state.get();
            int hash = (int) (universe ^ time ^ obj);
            int index = (hash & 0x7FFFFFFF) % previousState.elementDataSize;
            if (previousState.elementDataSize == 0) {
                return;
            }
            int m = previousState.elementHash.get(index);
            int last = -1;
            while (m >= 0) {
                if (universe == previousState.elementK3[m * 3] && time == previousState.elementK3[(m * 3) + 1] && obj == previousState.elementK3[(m * 3) + 2]) {
                    break;
                }
                last = m;
                m = previousState.elementNext[m];
            }
            if (m == -1) {
                return;
            }
            if (last == -1) {
                int previousNext = previousState.elementNext[m];
                if (!previousState.elementHash.compareAndSet(index, m, previousNext)) {
                    //complex case we are not the head, cause we have no previous just kick the object
                }
            } else {
                previousState.elementNext[last] = previousState.elementNext[m];
            }
            previousState.elementNext[m] = -1;//flag to dropped value
            previousState.values[m].free(p_metaModel);
            previousState.values[m] = null;
            previousState._elementCount.decrementAndGet();
            nbTry++;
            if (nbTry == KConfig.CAS_MAX_TRY) {
                throw new RuntimeException("CompareAndSwap error, failed to converge");
            }
        } while (!_state.compareAndSet(previousState, previousState));
    }

    @Override
    public final void clear(KMetaModel metaModel) {
        InternalState previousState = _state.get();
        if (previousState._elementCount.get() > 0) {
            for (int i = 0; i < previousState.elementDataSize; i++) {
                if (previousState.values[i] != null) {
                    previousState.values[i].free(metaModel);
                }
            }
            int initialCapacity = KConfig.CACHE_INIT_SIZE;
            InternalState newstate = new InternalState(initialCapacity, new long[initialCapacity * 3], new int[initialCapacity], new int[initialCapacity], new KChunk[initialCapacity]);
            for (int i = 0; i < initialCapacity; i++) {
                newstate.elementNext[i] = -1;
                newstate.elementHash.set(i, -1);
            }
            newstate._elementCount.set(0);
            newstate._valuesIndex.set(0);
            newstate._threshold = (int) (newstate.elementDataSize * LOAD_FACTOR);
            this._state.set(newstate);
        }
    }

    @Override
    public void delete(KMetaModel metaModel) {
        InternalState internalState = _state.getAndSet(null);
        for (int i = 0; i < internalState.elementDataSize; i++) {
            if (internalState.values[i] != null) {
                internalState.values[i].free(metaModel);
            }
        }
        internalState._elementCount.set(0);
        internalState._valuesIndex.set(0);
        internalState._threshold = 0;
    }

    @Override
    public void printDebug(KMetaModel p_metaModel) {
        try {
            InternalState state = _state.get();
            for (int i = 0; i < state.values.length; i++) {
                KChunk loopChunk = state.values[i];
                if (loopChunk != null) {
                    String content = loopChunk.serialize(p_metaModel);
                    System.err.println(state.elementK3[i * 3] + "," + state.elementK3[i * 3 + 1] + "," + state.elementK3[i * 3 + 2] + "=>" + loopChunk.type() + "(count:" + loopChunk.counter() + ",flag:" + loopChunk.getFlags() + ")" + "==>" + content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



