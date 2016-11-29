
package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.LongLongArrayMap;
import org.mwg.struct.LongLongArrayMapCallBack;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

@SuppressWarnings("Duplicates")
class OffHeapLongLongArrayMap implements LongLongArrayMap {

    private static int SIZE = 0;
    private static int CAPACITY = 1;
    private static int SUBHASH = 2;
    private static int HEADER = 3;
    private static int ELEM_SIZE = 2;

    private final long index;
    final OffHeapStateChunk chunk;

    OffHeapLongLongArrayMap(final OffHeapStateChunk p_chunk, final long p_index) {
        chunk = p_chunk;
        index = p_index;
    }

    private static long key(final long addr, final long elemIndex) {
        return OffHeapLongArray.get(addr, HEADER + (elemIndex * ELEM_SIZE));
    }

    private void setKey(final long addr, final long elemIndex, long newValue) {
        OffHeapLongArray.set(addr, HEADER + (elemIndex * ELEM_SIZE), newValue);
    }

    private static long value(final long addr, final long elemIndex) {
        return OffHeapLongArray.get(addr, HEADER + (elemIndex * ELEM_SIZE) + 1);
    }

    private void setValue(final long addr, final long elemIndex, long newValue) {
        OffHeapLongArray.set(addr, HEADER + (elemIndex * ELEM_SIZE) + 1, newValue);
    }

    private long next(final long subHashAddr, final long elemIndex) {
        return OffHeapLongArray.get(subHashAddr, elemIndex);
    }

    private void setNext(final long subHashAddr, final long elemIndex, final long newValue) {
        OffHeapLongArray.set(subHashAddr, elemIndex, newValue);
    }

    private long hash(final long subHashAddr, final long capacity, final long elemIndex) {
        return OffHeapLongArray.get(subHashAddr, (capacity + elemIndex));
    }

    private void setHash(final long subHashAddr, final long capacity, final long elemIndex, final long newValue) {
        OffHeapLongArray.set(subHashAddr, (capacity + elemIndex), newValue);
    }

    void preAllocate(long wantedCapacity) {
        long addr = chunk.addrByIndex(index);
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            addr = OffHeapLongArray.allocate(HEADER + wantedCapacity * 2);
            chunk.setAddrByIndex(index, addr);
            OffHeapLongArray.set(addr, SIZE, 0);
            OffHeapLongArray.set(addr, CAPACITY, wantedCapacity);
            long subHash = OffHeapLongArray.allocate(wantedCapacity * 3);
            OffHeapLongArray.set(addr, SUBHASH, subHash);
        } else {
            long currentCapacity = OffHeapLongArray.get(addr, CAPACITY);
            if (wantedCapacity > currentCapacity) {
                addr = OffHeapLongArray.reallocate(addr, HEADER + wantedCapacity * 2);
                chunk.setAddrByIndex(index, addr);
                OffHeapLongArray.set(addr, CAPACITY, wantedCapacity);
                long subHash = OffHeapLongArray.get(addr, SUBHASH);
                subHash = OffHeapLongArray.reallocate(subHash, wantedCapacity * 3);
                OffHeapLongArray.set(addr, SUBHASH, subHash);
                OffHeapLongArray.reset(subHash, wantedCapacity * 3);
                long size = OffHeapLongArray.get(addr, SIZE);
                for (long i = 0; i < size; i++) {
                    long new_key_hash = HashHelper.longHash(key(addr, i), wantedCapacity * 2);
                    setNext(subHash, i, hash(subHash, wantedCapacity, new_key_hash));
                    setHash(subHash, wantedCapacity, new_key_hash, i);
                }
            }
        }
    }

    @Override
    public final long[] get(final long requestKey) {
        long[] result = new long[0];
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {

                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                final long subHash = OffHeapLongArray.get(addr, SUBHASH);
                final long hashIndex = HashHelper.longHash(requestKey, capacity * 2);

                int resultCapacity = 0;
                int resultIndex = 0;
                long m = hash(subHash, capacity, hashIndex);
                while (m >= 0) {
                    if (requestKey == key(addr, m)) {
                        if (resultIndex == resultCapacity) {
                            int newCapacity;
                            if (resultCapacity == 0) {
                                newCapacity = 1;
                            } else {
                                newCapacity = resultCapacity * 2;
                            }
                            long[] tempResult = new long[newCapacity];
                            System.arraycopy(result, 0, tempResult, 0, result.length);
                            result = tempResult;
                            resultCapacity = newCapacity;
                        }
                        result[resultIndex] = value(addr, m);
                        resultIndex++;
                    }
                    m = next(subHash, m);
                }
                if (resultIndex != resultCapacity) {
                    //shrink result
                    long[] shrinkedResult = new long[resultIndex];
                    System.arraycopy(result, 0, shrinkedResult, 0, resultIndex);
                    result = shrinkedResult;
                }
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final boolean contains(final long requestKey, final long requestValue) {
        boolean result = false;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {

                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                final long subHash = OffHeapLongArray.get(addr, SUBHASH);
                final long hashIndex = HashHelper.longHash(requestKey, capacity * 2);

                long m = hash(subHash, capacity, hashIndex);
                while (m >= 0 && !result) {
                    if (requestKey == key(addr, m) && requestKey == key(addr, m)) {
                        result = true;
                    }
                    m = next(subHash, m);
                }
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final void each(final LongLongArrayMapCallBack callback) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            final long mapSize = OffHeapLongArray.get(addr, SIZE);
            for (long i = 0; i < mapSize; i++) {
                callback.on(key(addr, i), value(addr, i));
            }
        } finally {
            chunk.unlock();
        }
    }

    @Override
    public long size() {
        long result = 0;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                result = OffHeapLongArray.get(addr, SIZE);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final void delete(final long requestKey, final long requestValue) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long mapSize = OffHeapLongArray.get(addr, SIZE);
                if (mapSize != 0) {
                    long capacity = OffHeapLongArray.get(addr, CAPACITY);
                    long subHash = OffHeapLongArray.get(addr, SUBHASH);
                    long hashCapacity = capacity * 2;
                    long hashIndex = HashHelper.longHash(requestKey, hashCapacity);
                    long m = hash(subHash, capacity, hashIndex);
                    long found = -1;
                    while (m >= 0) {
                        if (requestKey == key(addr, m) && requestValue == value(addr, m)) {
                            found = m;
                            break;
                        }
                        m = next(subHash, m);
                    }
                    if (found != -1) {
                        //first remove currentKey from hashChain
                        long toRemoveHash = HashHelper.longHash(requestKey, hashCapacity);
                        m = hash(subHash, capacity, toRemoveHash);
                        if (m == found) {
                            setHash(subHash, capacity, toRemoveHash, next(subHash, m));
                        } else {
                            while (m != -1) {
                                long next_of_m = next(subHash, m);
                                if (next_of_m == found) {
                                    setNext(subHash, m, next(subHash, next_of_m));
                                    break;
                                }
                                m = next_of_m;
                            }
                        }
                        final long lastIndex = mapSize - 1;
                        if (lastIndex == found) {
                            //easy, was the last element
                            mapSize--;
                            OffHeapLongArray.set(addr, SIZE, mapSize);
                        } else {
                            //less cool, we have to unchain the last value of the map
                            final long lastKey = key(addr, lastIndex);
                            setKey(addr, found, lastKey);
                            setValue(addr, found, value(addr, lastIndex));
                            setNext(subHash, found, next(subHash, lastIndex));
                            long victimHash = HashHelper.longHash(lastKey, hashCapacity);
                            m = hash(subHash, capacity, victimHash);
                            if (m == lastIndex) {
                                //the victim was the head of hashing list
                                setHash(subHash, capacity, victimHash, found);
                            } else {
                                //the victim is in the next, reChain it
                                while (m != -1) {
                                    long next_of_m = next(subHash, m);
                                    if (next_of_m == lastIndex) {
                                        setNext(subHash, m, found);
                                        break;
                                    }
                                    m = next_of_m;
                                }
                            }
                            mapSize--;
                            OffHeapLongArray.set(addr, SIZE, mapSize);
                        }
                        chunk.declareDirty();
                    }
                }
            }
        } finally {
            chunk.unlock();
        }
    }

    @Override
    public final void put(final long insertKey, final long insertValue) {
        chunk.lock();
        try {
            internal_put(insertKey, insertValue);
        } finally {
            chunk.unlock();
        }
    }

    void internal_put(final long insertKey, final long insertValue) {
        long addr = chunk.addrByIndex(index);
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            //initial allocation
            final long capacity = Constants.MAP_INITIAL_CAPACITY;
            addr = OffHeapLongArray.allocate(HEADER + capacity * 2);
            chunk.setAddrByIndex(index, addr);
            final long subHash = OffHeapLongArray.allocate(capacity * 3);
            OffHeapLongArray.set(addr, SIZE, 1);
            OffHeapLongArray.set(addr, CAPACITY, capacity);
            OffHeapLongArray.set(addr, SUBHASH, subHash);
            setKey(addr, 0, insertKey);
            setValue(addr, 0, insertValue);
            setHash(subHash, capacity, (int) HashHelper.longHash(insertKey, capacity * 2), 0);
            setNext(subHash, 0, -1);
        } else {
            long mapSize = OffHeapLongArray.get(addr, SIZE);
            long capacity = OffHeapLongArray.get(addr, CAPACITY);
            long subHash = OffHeapLongArray.get(addr, SUBHASH);
            long m = hash(subHash, capacity, HashHelper.longHash(insertKey, capacity * 2));
            long found = -1;
            while (m >= 0) {
                if (insertKey == key(addr, m) && insertValue == value(addr, m)) {
                    found = m;
                    break;
                }
                m = next(subHash, m);
            }
            if (found == -1) {
                final long lastIndex = mapSize;
                if (lastIndex == capacity) {
                    //extend capacity
                    capacity = capacity * 2;
                    addr = OffHeapLongArray.reallocate(addr, HEADER + (capacity * 2));
                    chunk.setAddrByIndex(index, addr);
                    OffHeapLongArray.set(addr, CAPACITY, capacity);
                    subHash = OffHeapLongArray.reallocate(subHash, capacity * 3);
                    OffHeapLongArray.reset(subHash, capacity * 3);
                    OffHeapLongArray.set(addr, SUBHASH, subHash);
                    //reHash previous stored content
                    long size = OffHeapLongArray.get(addr, SIZE);
                    for (long i = 0; i < size; i++) {
                        long new_key_hash = HashHelper.longHash(key(addr, i), capacity * 2);
                        setNext(subHash, i, hash(subHash, capacity, new_key_hash));
                        setHash(subHash, capacity, new_key_hash, i);
                    }
                }
                setKey(addr, lastIndex, insertKey);
                setValue(addr, lastIndex, insertValue);
                final long hashedKey = HashHelper.longHash(insertKey, capacity * 2);
                setNext(subHash, lastIndex, hash(subHash, capacity, hashedKey));
                setHash(subHash, capacity, hashedKey, lastIndex);
                OffHeapLongArray.set(addr, SIZE, mapSize + 1);
                chunk.declareDirty();
            } else {
                if (value(addr, found) != insertValue) {
                    setValue(addr, found, insertValue);
                    chunk.declareDirty();
                }
            }
        }
    }

    static void save(final long addr, final Buffer buffer) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long size = OffHeapLongArray.get(addr, SIZE);
            Base64.encodeLongToBuffer(size, buffer);
            for (long i = 0; i < size; i++) {
                buffer.write(Constants.CHUNK_SUB_SUB_SEP);
                Base64.encodeLongToBuffer(key(addr, i), buffer);
                buffer.write(Constants.CHUNK_SUB_SUB_SUB_SEP);
                Base64.encodeLongToBuffer(value(addr, i), buffer);
            }
        }
    }

    static void free(final long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long previousHash = OffHeapLongArray.get(addr, SUBHASH);
            if (previousHash != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(previousHash);
            }
            OffHeapLongArray.free(addr);
        }
    }

    static long clone(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return OffHeapConstants.OFFHEAP_NULL_PTR;
        } else {
            final long capacity = OffHeapLongArray.get(addr, CAPACITY);
            //copy main array
            final long new_addr = OffHeapLongArray.cloneArray(addr, HEADER + capacity * 2);
            final long previousHash = OffHeapLongArray.get(addr, SUBHASH);
            if (previousHash != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long newHash = OffHeapLongArray.cloneArray(previousHash, capacity * 3);
                OffHeapLongArray.set(new_addr, SUBHASH, newHash);
            }
            return new_addr;
        }
    }

}



