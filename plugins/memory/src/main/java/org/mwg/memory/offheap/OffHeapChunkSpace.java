
package org.mwg.memory.offheap;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.base.AbstractExternalAttribute;
import org.mwg.chunk.Chunk;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.Stack;
import org.mwg.memory.offheap.primary.OffHeapByteArray;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;
import org.mwg.utility.HashHelper;
import org.mwg.utility.KeyHelper;

import java.util.HashMap;
import java.util.Map;

import static org.mwg.Constants.BUFFER_SEP;

class OffHeapChunkSpace implements ChunkSpace {

    private static final long HASH_LOAD_FACTOR = 4;

    private final long _maxEntries;
    private final long _hashEntries;

    private final Stack _lru;
    private final Stack _dirtiesStack;
    private final Graph _graph;

    private final long locks;
    private final long hashNext;
    private final long hash;
    private final long worlds;
    private final long times;
    private final long ids;
    private final long types;
    private final long marks;
    private final long addrs;

    private final Map<Long, AbstractExternalAttribute> heapAttributes = new HashMap<Long, AbstractExternalAttribute>();

    public final void mountHeapAttribute(long adr, AbstractExternalAttribute obj) {
        heapAttributes.put(adr, obj);
    }

    public final AbstractExternalAttribute heapAttribute(long adr) {
        return heapAttributes.get(adr);
    }

    public final void umountHeapAttribute(long adr) {
        heapAttributes.remove(adr);
    }

    @Override
    public final Graph graph() {
        return this._graph;
    }

    final long worldByIndex(long index) {
        return OffHeapLongArray.get(worlds, index);
    }

    final long timeByIndex(long index) {
        return OffHeapLongArray.get(times, index);
    }

    final long idByIndex(long index) {
        return OffHeapLongArray.get(ids, index);
    }

    final long addrByIndex(long index) {
        return OffHeapLongArray.get(addrs, index);
    }

    final void setAddrByIndex(long index, long addr) {
        OffHeapLongArray.set(addrs, index, addr);
    }

    final void lockByIndex(final long index) {
        while (!OffHeapLongArray.compareAndSwap(locks, index, -1, 1)) ;
    }

    final void unlockByIndex(final long index) {
        if (!OffHeapLongArray.compareAndSwap(locks, index, 1, -1)) {
            System.out.println("CAS error !!!");
        }
    }

    OffHeapChunkSpace(final long initialCapacity, final Graph p_graph) {
        _graph = p_graph;
        _maxEntries = initialCapacity;
        _hashEntries = initialCapacity * HASH_LOAD_FACTOR;
        _lru = new OffHeapFixedStack(initialCapacity, true);
        _dirtiesStack = new OffHeapFixedStack(initialCapacity, false);
        locks = OffHeapLongArray.allocate(initialCapacity);
        hashNext = OffHeapLongArray.allocate(initialCapacity);
        hash = OffHeapLongArray.allocate(_hashEntries);
        addrs = OffHeapLongArray.allocate(initialCapacity);
        worlds = OffHeapLongArray.allocate(_maxEntries);
        times = OffHeapLongArray.allocate(_maxEntries);
        ids = OffHeapLongArray.allocate(_maxEntries);
        types = OffHeapByteArray.allocate(_maxEntries);
        marks = OffHeapLongArray.allocate(_maxEntries);
        for (long i = 0; i < _maxEntries; i++) {
            OffHeapLongArray.set(marks, i, 0);
        }
    }

    @Override
    public final void freeAll() {
        _lru.free();
        _dirtiesStack.free();
        OffHeapLongArray.free(hashNext);
        OffHeapLongArray.free(hash);
        OffHeapLongArray.free(addrs);
        OffHeapLongArray.free(worlds);
        OffHeapLongArray.free(times);
        OffHeapLongArray.free(ids);
        OffHeapByteArray.free(types);
        OffHeapLongArray.free(marks);
        OffHeapLongArray.free(locks);
    }

    @Override
    public final Chunk getAndMark(final byte type, final long world, final long time, final long id) {
        final long index = HashHelper.tripleHash(type, world, time, id, this._hashEntries);
        long m = OffHeapLongArray.get(hash, index);
        long found = -1;
        while (m != -1) {
            if (OffHeapByteArray.get(types, m) == type
                    && OffHeapLongArray.get(worlds, m) == world
                    && OffHeapLongArray.get(times, m) == time
                    && OffHeapLongArray.get(ids, m) == id) {
                if (mark(m) > 0) {
                    found = m;
                }
                break;
            } else {
                m = OffHeapLongArray.get(hashNext, m);
            }
        }
        if (found != -1) {
            return get(found);
        } else {
            return null;
        }
    }

    @Override
    public final Chunk get(final long index) {
        switch (OffHeapByteArray.get(types, index)) {
            case ChunkType.STATE_CHUNK:
                return new OffHeapStateChunk(this, index);
            case ChunkType.WORLD_ORDER_CHUNK:
                return new OffHeapWorldOrderChunk(this, index);
            case ChunkType.TIME_TREE_CHUNK:
                return new OffHeapTimeTreeChunk(this, index);
            case ChunkType.GEN_CHUNK:
                return new OffHeapGenChunk(this, OffHeapLongArray.get(ids, index), index);
        }
        return null;
    }

    @Override
    public final void getOrLoadAndMark(final byte type, final long world, final long time, final long id, final Callback<Chunk> callback) {
        final Chunk fromMemory = getAndMark(type, world, time, id);
        if (fromMemory != null) {
            callback.on(fromMemory);
        } else {
            final Buffer keys = graph().newBuffer();
            KeyHelper.keyToBuffer(keys, type, world, time, id);
            graph().storage().get(keys, new Callback<Buffer>() {
                @Override
                public void on(final Buffer result) {
                    if (result != null && result.length() > 0) {
                        Chunk loadedChunk = createAndMark(type, world, time, id);
                        loadedChunk.load(result);
                        result.free();
                        callback.on(loadedChunk);
                    } else {
                        keys.free();
                        callback.on(null);
                    }
                }
            });
        }
    }

    @Override
    public final void getOrLoadAndMarkAll(final long[] keys, final Callback<Chunk[]> callback) {
        final int querySize = keys.length / Constants.KEY_SIZE;
        final Chunk[] finalResult = new Chunk[querySize];
        int[] reverse = null;
        int reverseIndex = 0;
        Buffer toLoadKeys = null;
        for (int i = 0; i < querySize; i++) {
            final int offset = i * Constants.KEY_SIZE;
            final byte loopType = (byte) keys[offset];
            if (loopType != -1) {
                final Chunk fromMemory = getAndMark((byte) keys[offset], keys[offset + 1], keys[offset + 2], keys[offset + 3]);
                if (fromMemory != null) {
                    finalResult[i] = fromMemory;
                } else {
                    if (reverse == null) {
                        reverse = new int[querySize];
                        toLoadKeys = graph().newBuffer();
                    }
//                    reverse[i] = reverseIndex;
                    reverse[reverseIndex] = i;
                    if (reverseIndex != 0) {
                        toLoadKeys.write(BUFFER_SEP);
                    }
                    KeyHelper.keyToBuffer(toLoadKeys, (byte) keys[offset], keys[offset + 1], keys[offset + 2], keys[offset + 3]);
                    reverseIndex++;
                }
            } else {
                finalResult[i] = null;
            }
        }
        if (reverse != null) {
            final int[] finalReverse = reverse;
            graph().storage().get(toLoadKeys, new Callback<Buffer>() {
                @Override
                public void on(final Buffer loadAllResult) {
                    BufferIterator it = loadAllResult.iterator();
                    int i = 0;
                    while (it.hasNext()) {
                        Buffer view = it.next();
                        int reversedIndex = finalReverse[i];
                        int reversedOffset = reversedIndex * Constants.KEY_SIZE;
                        if (view.length() > 0) {
                            Chunk loadedChunk = createAndMark((byte) keys[reversedOffset], keys[reversedOffset + 1], keys[reversedOffset + 2], keys[reversedOffset + 3]);
                            loadedChunk.load(view);
                            finalResult[reversedIndex] = loadedChunk;
                        } else {
                            finalResult[reversedIndex] = null;
                        }
                        i++;
                    }
                    loadAllResult.free();
                    callback.on(finalResult);
                }
            });
        } else {
            callback.on(finalResult);
        }
    }

    @Override
    public final long mark(final long index) {
        long before;
        long after;
        do {
            before = OffHeapLongArray.get(marks, index);
            if (before != -1) {
                after = before + 1;
            } else {
                after = before;
            }
        } while (!OffHeapLongArray.compareAndSwap(marks, index, before, after));
        if (before == 0 && after == 1) {
            //was at zero before, risky operation, check selectWith LRU
            this._lru.dequeue(index);
        }
        return after;
    }

    @Override
    public final void unmark(final long index) {
        long before;
        long after;
        do {
            before = OffHeapLongArray.get(marks, index);
            if (before > 0) {
                after = before - 1;
            } else {
                System.err.println("WARNING: DOUBLE UNMARK");
                after = before;
            }
        } while (!OffHeapLongArray.compareAndSwap(marks, index, before, after));
        if (before == 1 && after == 0) {
            //was at zero before, risky operation, check selectWith LRU
            this._lru.enqueue(index);
        }
    }

    @Override
    public final void free(Chunk chunk) {
        freeByIndex(chunk.index());
    }

    private void freeByIndex(long index) {
        final long rawValue = OffHeapLongArray.get(addrs, index);
        switch (OffHeapByteArray.get(types, index)) {
            case ChunkType.STATE_CHUNK:
                OffHeapStateChunk.free(rawValue, this);
                break;
            case ChunkType.WORLD_ORDER_CHUNK:
                OffHeapWorldOrderChunk.free(rawValue);
                break;
            case ChunkType.TIME_TREE_CHUNK:
                OffHeapTimeTreeChunk.free(rawValue);
                break;
            case ChunkType.GEN_CHUNK:
                OffHeapGenChunk.free(rawValue);
                break;
        }
        OffHeapLongArray.set(addrs, index, OffHeapConstants.OFFHEAP_NULL_PTR);
    }

    @Override
    public final synchronized Chunk createAndMark(final byte type, final long world, final long time, final long id) {
        //first mark the object
        long entry = -1;
        long hashIndex = HashHelper.tripleHash(type, world, time, id, this._hashEntries);
        long m = OffHeapLongArray.get(hash, hashIndex);
        while (m >= 0) {
            if (type == OffHeapByteArray.get(types, m) && world == OffHeapLongArray.get(worlds, m) && time == OffHeapLongArray.get(times, m) && id == OffHeapLongArray.get(ids, m)) {
                entry = m;
                break;
            }
            m = OffHeapLongArray.get(hashNext, m);
        }
        if (entry != -1) {
            long previous;
            long after;
            do {
                previous = OffHeapLongArray.get(marks, entry);
                if (previous != -1) {
                    after = previous + 1;
                } else {
                    after = previous;
                }
            } while (!OffHeapLongArray.compareAndSwap(marks, entry, previous, after));
            if (after == (previous + 1)) {
                return get(entry);
            }
        }
        long currentVictimIndex = -1;
        while (currentVictimIndex == -1) {
            long temp_victim = this._lru.dequeueTail();
            if (temp_victim == -1) {
                break;
            } else {
                if (OffHeapLongArray.compareAndSwap(marks, temp_victim, 0, -1)) {
                    currentVictimIndex = temp_victim;
                }
            }
        }
        if (currentVictimIndex == -1) {
            throw new RuntimeException("mwDB crashed, cache is full, please avoid to much retention of nodes or augment cache capacity! available:" + available());
        }
        if (OffHeapLongArray.get(addrs, currentVictimIndex) != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long victimWorld = OffHeapLongArray.get(worlds, currentVictimIndex);
            final long victimTime = OffHeapLongArray.get(times, currentVictimIndex);
            final long victimObj = OffHeapLongArray.get(ids, currentVictimIndex);
            final byte victimType = OffHeapByteArray.get(types, currentVictimIndex);
            final long indexVictim = HashHelper.tripleHash(victimType, victimWorld, victimTime, victimObj, this._hashEntries);
            m = OffHeapLongArray.get(hash, indexVictim);
            long last = -1;
            while (m >= 0) {
                if (victimType == OffHeapByteArray.get(types, m) && victimWorld == OffHeapLongArray.get(worlds, m) && victimTime == OffHeapLongArray.get(times, m) && victimObj == OffHeapLongArray.get(ids, m)) {
                    break;
                }
                last = m;
                m = OffHeapLongArray.get(hashNext, m);
            }
            //POP THE VALUE FROM THE NEXT LIST
            if (last == -1) {
                long previousNext = OffHeapLongArray.get(hashNext, m);
                OffHeapLongArray.set(hash, indexVictim, previousNext);
            } else {
                if (m == -1) {
                    OffHeapLongArray.set(hashNext, last, -1);
                } else {
                    OffHeapLongArray.set(hashNext, last, OffHeapLongArray.get(hashNext, m));
                }
            }
            OffHeapLongArray.set(hashNext, m, -1);
            freeByIndex(currentVictimIndex);
        }
        //will be registered by the chunk itself
        OffHeapLongArray.set(marks, currentVictimIndex, 1);
        OffHeapByteArray.set(types, currentVictimIndex, type);
        OffHeapLongArray.set(worlds, currentVictimIndex, world);
        OffHeapLongArray.set(times, currentVictimIndex, time);
        OffHeapLongArray.set(ids, currentVictimIndex, id);
        //negociate the lock to write on hashIndex
        OffHeapLongArray.set(hashNext, currentVictimIndex, OffHeapLongArray.get(hash, hashIndex));
        OffHeapLongArray.set(hash, hashIndex, currentVictimIndex);
        //free the lock
        return get(currentVictimIndex);
    }

    @Override
    public void notifyUpdate(long index) {
        if (_dirtiesStack.enqueue(index)) {
            mark(index);
        }
    }

    @Override
    public synchronized void save(final Callback<Boolean> callback) {
        final Buffer stream = this._graph.newBuffer();
        boolean isFirst = true;
        while (_dirtiesStack.size() != 0) {
            long tail = _dirtiesStack.dequeueTail();
            //Save chunk Key
            if (isFirst) {
                isFirst = false;
            } else {
                stream.write(Constants.BUFFER_SEP);
            }
            KeyHelper.keyToBuffer(stream, OffHeapByteArray.get(types, tail), OffHeapLongArray.get(worlds, tail), OffHeapLongArray.get(times, tail), OffHeapLongArray.get(ids, tail));
            //Save chunk payload
            stream.write(Constants.BUFFER_SEP);
            try {
                Chunk loopChunk = get(tail);
                if (loopChunk != null) {
                    loopChunk.save(stream);
                }
                unmark(tail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //shrink in case of i != full size
        this.graph().storage().put(stream, new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                //free all value
                stream.free();
                if (callback != null) {
                    callback.on(result);
                }
            }
        });
    }

    @Override
    public final void clear() {
        //TODO reset everything
    }

    @Override
    public final long available() {
        return _lru.size();
    }

}



