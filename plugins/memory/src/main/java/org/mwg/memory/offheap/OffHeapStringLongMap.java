
package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.memory.offheap.primary.OffHeapString;
import org.mwg.struct.Buffer;
import org.mwg.struct.StringLongMap;
import org.mwg.struct.StringLongMapCallBack;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

@SuppressWarnings("ALL")
class OffHeapStringLongMap implements StringLongMap {

    private static int SIZE = 0;
    private static int CAPACITY = 1;
    private static int SUBHASH = 2;

    private static int HEADER = 3;
    private static int ELEM_SIZE = 3;

    private final long index;
    private final OffHeapStateChunk chunk;

    OffHeapStringLongMap(final OffHeapStateChunk p_chunk, final long p_index) {
        chunk = p_chunk;
        index = p_index;
    }

    private static long key(final long addr, final long elemIndex) {
        return OffHeapLongArray.get(addr, HEADER + (elemIndex * ELEM_SIZE));
    }

    private void setKey(final long addr, final long elemIndex, long newValue) {
        OffHeapLongArray.set(addr, HEADER + (elemIndex * ELEM_SIZE), newValue);
    }

    private static long keyHash(final long addr, final long elemIndex) {
        return OffHeapLongArray.get(addr, HEADER + (elemIndex * ELEM_SIZE) + 1);
    }

    private void setKeyHash(final long addr, final long elemIndex, long newValue) {
        OffHeapLongArray.set(addr, HEADER + (elemIndex * ELEM_SIZE) + 1, newValue);
    }

    private static long value(final long addr, final long elemIndex) {
        return OffHeapLongArray.get(addr, HEADER + (elemIndex * ELEM_SIZE) + 2);
    }

    private void setValue(final long addr, final long elemIndex, long newValue) {
        OffHeapLongArray.set(addr, HEADER + (elemIndex * ELEM_SIZE) + 2, newValue);
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
            addr = OffHeapLongArray.allocate(HEADER + (wantedCapacity * ELEM_SIZE));
            chunk.setAddrByIndex(index, addr);
            OffHeapLongArray.set(addr, SIZE, 0);
            OffHeapLongArray.set(addr, CAPACITY, wantedCapacity);
            long subHash = OffHeapLongArray.allocate(wantedCapacity * 3);
            OffHeapLongArray.set(addr, SUBHASH, subHash);
        } else {
            long currentCapacity = OffHeapLongArray.get(addr, CAPACITY);
            if (wantedCapacity > currentCapacity) {
                addr = OffHeapLongArray.reallocate(addr, HEADER + (wantedCapacity * ELEM_SIZE));
                chunk.setAddrByIndex(index, addr);
                OffHeapLongArray.set(addr, CAPACITY, wantedCapacity);
                long subHash = OffHeapLongArray.get(addr, SUBHASH);
                subHash = OffHeapLongArray.reallocate(subHash, wantedCapacity * 3);
                OffHeapLongArray.set(addr, SUBHASH, subHash);
                OffHeapLongArray.reset(subHash, wantedCapacity * 3);
                long size = OffHeapLongArray.get(addr, SIZE);
                for (long i = 0; i < size; i++) {
                    long new_key_hash = HashHelper.longHash(keyHash(addr, i), wantedCapacity * 2);
                    setNext(subHash, i, hash(subHash, wantedCapacity, new_key_hash));
                    setHash(subHash, wantedCapacity, new_key_hash, i);
                }
            }
        }
    }

    @Override
    public final long getValue(final String requestStringKey) {
        long result = Constants.NULL_LONG;
        chunk.lock();
        try {
            final long keyHash = HashHelper.hash(requestStringKey);
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                final long subHash = OffHeapLongArray.get(addr, SUBHASH);
                final long hashIndex = HashHelper.longHash(keyHash, capacity * 2);
                long m = hash(subHash, capacity, hashIndex);
                while (m >= 0) {
                    if (keyHash == keyHash(addr, m)) {
                        result = value(addr, m);
                        break;
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
    public final String getByHash(final long keyHash) {
        long result = Constants.NULL_LONG;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                final long subHash = OffHeapLongArray.get(addr, SUBHASH);
                final long hashIndex = HashHelper.longHash(keyHash, capacity * 2);
                long m = hash(subHash, capacity, hashIndex);
                while (m >= 0) {
                    if (keyHash == keyHash(addr, m)) {
                        result = key(addr, m);
                        break;
                    }
                    m = next(subHash, m);
                }
            }
        } finally {
            chunk.unlock();
        }
        return OffHeapString.asObject(result);
    }

    @Override
    public final boolean containsHash(final long keyHash) {
        boolean result = false;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                final long subHash = OffHeapLongArray.get(addr, SUBHASH);
                final long hashIndex = HashHelper.longHash(keyHash, capacity * 2);
                long m = hash(subHash, capacity, hashIndex);
                while (m >= 0) {
                    if (keyHash == keyHash(addr, m)) {
                        result = true;
                        break;
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
    public final void each(StringLongMapCallBack callback) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long mapSize = OffHeapLongArray.get(addr, SIZE);
                for (long i = 0; i < mapSize; i++) {
                    callback.on(OffHeapString.asObject(key(addr, i)), value(addr, i));
                }
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
    public final void remove(final String requestStringKey) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long keyHash = HashHelper.hash(requestStringKey);
                long mapSize = OffHeapLongArray.get(addr, SIZE);
                if (mapSize != 0) {
                    long capacity = OffHeapLongArray.get(addr, CAPACITY);
                    long subHash = OffHeapLongArray.get(addr, SUBHASH);
                    long hashCapacity = capacity * 2;
                    long hashIndex = HashHelper.longHash(keyHash, hashCapacity);
                    long m = hash(subHash, capacity, hashIndex);
                    long found = -1;
                    while (m >= 0) {
                        if (keyHash == keyHash(addr, m)) {
                            found = m;
                            break;
                        }
                        m = next(subHash, m);
                    }
                    if (found != -1) {
                        //first remove currentKey from hashChain
                        long toRemoveHash = HashHelper.longHash(keyHash, hashCapacity);
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
                            OffHeapLongArray.set(addr, SIZE, mapSize - 1);
                        } else {
                            //less cool, we have to unchain the last value of the map
                            final long lastKey = key(addr, lastIndex);
                            final long lastKeyHash = keyHash(addr, lastIndex);
                            setKey(addr, found, lastKey);
                            setKeyHash(addr, found, lastKeyHash);
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
                            OffHeapLongArray.set(addr, SIZE, mapSize - 1);
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
    public final void put(final String insertKey, final long insertValue) {
        chunk.lock();
        try {
            internal_put(insertKey, insertValue);
        } finally {
            chunk.unlock();
        }
    }

    void internal_put(final String insertStringKey, final long insertValue) {
        final long keyHash = HashHelper.hash(insertStringKey);
        long addr = chunk.addrByIndex(index);
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            //initial allocation
            final long capacity = Constants.MAP_INITIAL_CAPACITY;
            addr = OffHeapLongArray.allocate(HEADER + (capacity * ELEM_SIZE));
            chunk.setAddrByIndex(index, addr);
            final long subHash = OffHeapLongArray.allocate(capacity * 3);
            OffHeapLongArray.set(addr, SIZE, 1);
            OffHeapLongArray.set(addr, CAPACITY, capacity);
            OffHeapLongArray.set(addr, SUBHASH, subHash);
            final long keyAddr = OffHeapString.fromObject(insertStringKey);
            setKey(addr, 0, keyAddr);
            setKeyHash(addr, 0, keyHash);
            setValue(addr, 0, insertValue);
            setHash(subHash, capacity, (int) HashHelper.longHash(keyHash, capacity * 2), 0);
            setNext(subHash, 0, -1);
        } else {
            long mapSize = OffHeapLongArray.get(addr, SIZE);
            long capacity = OffHeapLongArray.get(addr, CAPACITY);
            long subHash = OffHeapLongArray.get(addr, SUBHASH);
            long m = hash(subHash, capacity, HashHelper.longHash(keyHash, capacity * 2));
            long found = -1;
            while (m >= 0) {
                if (keyHash == keyHash(addr, m)) {
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
                    addr = OffHeapLongArray.reallocate(addr, HEADER + (capacity * ELEM_SIZE));
                    chunk.setAddrByIndex(index, addr);
                    OffHeapLongArray.set(addr, CAPACITY, capacity);
                    subHash = OffHeapLongArray.reallocate(subHash, capacity * 3);
                    OffHeapLongArray.reset(subHash, capacity * 3);
                    OffHeapLongArray.set(addr, SUBHASH, subHash);
                    //reHash previous stored content
                    long size = OffHeapLongArray.get(addr, SIZE);
                    for (long i = 0; i < size; i++) {
                        long new_key_hash = HashHelper.longHash(keyHash(addr, i), capacity * 2);
                        setNext(subHash, i, hash(subHash, capacity, new_key_hash));
                        setHash(subHash, capacity, new_key_hash, i);
                    }
                }
                setKey(addr, lastIndex, OffHeapString.fromObject(insertStringKey));
                setKeyHash(addr, lastIndex, keyHash);
                setValue(addr, lastIndex, insertValue);
                final long hashedKey = HashHelper.longHash(keyHash, capacity * 2);
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
                Base64.encodeStringToBuffer(OffHeapString.asObject(key(addr, i)), buffer);
                buffer.write(Constants.CHUNK_SUB_SUB_SUB_SEP);
                Base64.encodeLongToBuffer(value(addr, i), buffer);
            }
        }
    }

    static void free(final long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (long i = 0; i < size; i++) {
                final long keyAddr = key(addr, i);
                if (keyAddr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                    OffHeapString.free(keyAddr);
                }
            }
            final long previousHash = OffHeapLongArray.get(addr, SUBHASH);
            if (previousHash != OffHeapConstants.OFFHEAP_NULL_PTR) {
                if (OffHeapConstants.DEBUG_MODE) {
                    if (!OffHeapConstants.SEGMENTS.containsKey(previousHash)) {
                        throw new RuntimeException("Bad ADDR!");
                    }
                    OffHeapConstants.SEGMENTS.remove(previousHash);
                }
                OffHeapLongArray.free(previousHash);
            }
            if (OffHeapConstants.DEBUG_MODE) {
                if (!OffHeapConstants.SEGMENTS.containsKey(addr)) {
                    throw new RuntimeException("Bad ADDR!");
                }
                OffHeapConstants.SEGMENTS.remove(addr);
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
            final long new_addr = OffHeapLongArray.cloneArray(addr, HEADER + (capacity * ELEM_SIZE));
            final long previousHash = OffHeapLongArray.get(addr, SUBHASH);
            if (previousHash != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long newHash = OffHeapLongArray.cloneArray(previousHash, (capacity * 3));
                OffHeapLongArray.set(new_addr, SUBHASH, newHash);
            }
            //increase cow counters of OffHeapStrings
            long size = OffHeapLongArray.get(addr, SIZE);
            for (long i = 0; i < size; i++) {
                // not necessary to set the cloned value
                // OffHeapString.clone always returns the same address, it just increments the cow counter
                /*long newKey = */
                OffHeapString.clone(key(addr, i));
                // OffHeapLongArray.set(addr, HEADER + (i * ELEM_SIZE), newKey);
            }
            return new_addr;
        }
    }

}



