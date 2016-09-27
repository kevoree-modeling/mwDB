package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Type;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.memory.offheap.primary.OffHeapDoubleArray;
import org.mwg.memory.offheap.primary.OffHeapIntArray;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.memory.offheap.primary.OffHeapString;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

class OffHeapStateChunk implements StateChunk {

    private final OffHeapChunkSpace space;
    private final long index;

    private static final int DIRTY = 0;
    private static final int SIZE = 1;
    private static final int CAPACITY = 2;
    private static final int SUBHASH = 3;

    private static final int OFFSET = 4;
    private static final int ELEM_SIZE = 3;

    @Override
    public final long world() {
        return space.worldByIndex(index);
    }

    @Override
    public final long time() {
        return space.timeByIndex(index);
    }

    @Override
    public final long id() {
        return space.idByIndex(index);
    }

    OffHeapStateChunk(final OffHeapChunkSpace p_space, final long p_index) {
        index = p_index;
        space = p_space;
    }

    final void lock() {
        space.lockByIndex(index);
    }

    final void unlock() {
        space.unlockByIndex(index);
    }

    final long addrByIndex(long elemIndex) {
        return OffHeapLongArray.get(space.addrByIndex(index), OFFSET + (elemIndex * ELEM_SIZE) + 2);
    }

    final void setAddrByIndex(long elemIndex, long newAddr) {
        OffHeapLongArray.set(space.addrByIndex(index), OFFSET + (elemIndex * ELEM_SIZE) + 2, newAddr);
    }

    private static long key(final long addr, final long index) {
        return OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE));
    }

    private static void setKey(final long addr, final long index, final long insertKey) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE), insertKey);
    }

    private static byte type(final long addr, final long index) {
        return (byte) OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE) + 1);
    }

    private static void setType(final long addr, final long index, final byte insertType) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE) + 1, insertType);
    }

    private static long value(final long addr, final long index) {
        return OffHeapLongArray.get(addr, OFFSET + (index * ELEM_SIZE) + 2);
    }

    private static void setValue(final long addr, final long index, final long insertValue) {
        OffHeapLongArray.set(addr, OFFSET + (index * ELEM_SIZE) + 2, insertValue);
    }

    private static double doubleValue(final long addr, final long index) {
        return OffHeapDoubleArray.get(addr, OFFSET + (index * ELEM_SIZE) + 2);
    }

    private static void setDoubleValue(final long addr, final long index, final double insertValue) {
        OffHeapDoubleArray.set(addr, OFFSET + (index * ELEM_SIZE) + 2, insertValue);
    }

    private static long next(final long hashAddr, final long index) {
        return OffHeapLongArray.get(hashAddr, index);
    }

    private static void setNext(final long hashAddr, final long index, final long insertNext) {
        OffHeapLongArray.set(hashAddr, index, insertNext);
    }

    private static long hash(final long hashAddr, final long capacity, final long index) {
        return OffHeapLongArray.get(hashAddr, capacity + index);
    }

    private static void setHash(final long hashAddr, final long capacity, final long index, final long insertHash) {
        OffHeapLongArray.set(hashAddr, capacity + index, insertHash);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.STATE_CHUNK;
    }

    @Override
    public final long index() {
        return index;
    }

    @Override
    public final Object get(final long p_key) {
        Object result = null;
        lock();
        try {
            final long addr = space.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long foundIndex = internal_find(addr, p_key);
                result = internal_get(addr, foundIndex);
            }
        } finally {
            unlock();
        }
        return result;
    }

    @Override
    public final Object getFromKey(final String key) {
        return get(space.graph().resolver().stringToHash(key, false));
    }

    @Override
    public final void each(final NodeStateCallback callBack) {
        // lock();
        //  try {
        final long addr = space.addrByIndex(index);
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (int i = 0; i < size; i++) {
                Object resolved = internal_get(addr, i);
                if (resolved != null) {
                    callBack.on(key(addr, i), type(addr, i), resolved);
                }
            }
        }
        // } finally {
        //unlock();
        //  }
    }

    private Object internal_get(final long addr, final long index) {
        if (index >= 0) {
            final byte elemType = type(addr, index);
            final long rawValue = value(addr, index);
            switch (type(addr, index)) {
                case Type.BOOL:
                    return rawValue == 1;
                case Type.DOUBLE:
                    return doubleValue(addr, index);
                case Type.LONG:
                    return rawValue;
                case Type.INT:
                    return (int) rawValue;
                case Type.STRING:
                    return OffHeapString.asObject(rawValue);
                case Type.DOUBLE_ARRAY:
                    return OffHeapDoubleArray.asObject(rawValue);
                case Type.LONG_ARRAY:
                    return OffHeapLongArray.asObject(rawValue);
                case Type.INT_ARRAY:
                    return OffHeapIntArray.asObject(rawValue);
                case Type.RELATION:
                    return new OffHeapRelationship(this, index);
                case Type.STRING_TO_LONG_MAP:
                    return new OffHeapStringLongMap(this, index);
                case Type.LONG_TO_LONG_MAP:
                    return new OffHeapLongLongMap(this, index);
                case Type.LONG_TO_LONG_ARRAY_MAP:
                    return new OffHeapLongLongArrayMap(this, index);
                case OffHeapConstants.OFFHEAP_NULL_PTR:
                    return null;
                default:
                    throw new RuntimeException("Should never happen " + elemType);
            }
        }
        return null;
    }

    private long internal_find(final long addr, final long requestKey) {
        final long size = OffHeapLongArray.get(addr, SIZE);
        final long subhash_ptr = OffHeapLongArray.get(addr, SUBHASH);
        if (size == 0) {
            return -1;
        } else if (subhash_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            for (int i = 0; i < size; i++) {
                if (key(addr, i) == requestKey) {
                    return i;
                }
            }
            return -1;
        } else {
            final long capacity = OffHeapLongArray.get(addr, CAPACITY);
            final long hashIndex = HashHelper.longHash(requestKey, capacity * 2);
            long m = hash(subhash_ptr, capacity, hashIndex);
            while (m >= 0) {
                if (requestKey == key(addr, m)) {
                    return m;
                } else {
                    m = next(subhash_ptr, m);
                }
            }
            return -1;
        }
    }

    @Override
    public final Object getOrCreate(final long requestKey, final byte requestType) {
        Object result = null;
        lock();
        try {
            long addr = space.addrByIndex(index);
            long foundIndex = OffHeapConstants.OFFHEAP_NULL_PTR;
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                foundIndex = internal_find(addr, requestKey);
            }
            if (foundIndex == OffHeapConstants.OFFHEAP_NULL_PTR || type(addr, foundIndex) != requestType) {
                foundIndex = internal_set(requestKey, requestType, OffHeapConstants.OFFHEAP_NULL_PTR, true, false);
                addr = space.addrByIndex(index);
            }
            result = internal_get(addr, foundIndex);
        } finally {
            unlock();
        }
        return result;
    }

    @Override
    public final Object getOrCreateFromKey(final String key, final byte elemType) {
        return getOrCreate(space.graph().resolver().stringToHash(key, true), elemType);
    }

    @Override
    public final void set(final long p_elementIndex, final byte p_elemType, final Object p_unsafe_elem) {
        if (p_elemType == Type.LONG_TO_LONG_MAP || p_elemType == Type.LONG_TO_LONG_ARRAY_MAP || p_elemType == Type.STRING_TO_LONG_MAP || p_elemType == Type.RELATION) {
            throw new RuntimeException("Bad API usage ! Set are forbidden for Maps and Relationship , please use getOrCreate instead");
        }
        lock();
        try {
            internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true, false);
        } finally {
            unlock();
        }
    }

    @Override
    public final void setFromKey(final String key, final byte p_elemType, final Object p_unsafe_elem) {
        set(space.graph().resolver().stringToHash(key, true), p_elemType, p_unsafe_elem);
    }

    @Override
    public final <A> A getFromKeyWithDefault(final String key, final A defaultValue) {
        final Object result = getFromKey(key);
        if (result == null) {
            return defaultValue;
        } else {
            return (A) result;
        }
    }

    @Override
    public <A> A getWithDefault(long key, A defaultValue) {
        final Object result = get(key);
        if (result == null) {
            return defaultValue;
        } else {
            return (A) result;
        }
    }

    @Override
    public final byte getType(final long p_key) {
        byte result = -1;
        lock();
        try {
            final long addr = space.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long index = internal_find(addr, p_key);
                if (index != OffHeapConstants.OFFHEAP_NULL_PTR) {
                    result = type(addr, index);
                }
            }
        } finally {
            unlock();
        }
        return result;
    }

    @Override
    public byte getTypeFromKey(final String key) {
        return getType(space.graph().resolver().stringToHash(key, false));
    }

    final void declareDirty() {
        final long addr = space.addrByIndex(index);
        if (OffHeapLongArray.get(addr, DIRTY) != 1) {
            OffHeapLongArray.set(addr, DIRTY, 1);
            space.notifyUpdate(index);
        }
    }

    @Override
    public final void save(final Buffer buffer) {
        lock();
        try {
            final long addr = space.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long size = OffHeapLongArray.get(addr, SIZE);
                Base64.encodeLongToBuffer(size, buffer);
                for (int i = 0; i < size; i++) {
                    buffer.write(Constants.CHUNK_SEP);
                    Base64.encodeLongToBuffer(key(addr, i), buffer);
                    buffer.write(Constants.CHUNK_SUB_SEP);
                    final byte type = type(addr, i);
                    Base64.encodeIntToBuffer(type, buffer);
                    buffer.write(Constants.CHUNK_SUB_SEP);
                    final long rawValue = value(addr, i);
                    switch (type) {
                        case Type.STRING:
                            OffHeapString.save(rawValue, buffer);
                            break;
                        case Type.BOOL:
                            if (rawValue == 1) {
                                buffer.write(Constants.BOOL_TRUE);
                            } else {
                                buffer.write(Constants.BOOL_FALSE);
                            }
                            break;
                        case Type.LONG:
                            Base64.encodeLongToBuffer(rawValue, buffer);
                            break;
                        case Type.DOUBLE:
                            Base64.encodeDoubleToBuffer(doubleValue(addr, i), buffer);
                            break;
                        case Type.INT:
                            Base64.encodeIntToBuffer((int) rawValue, buffer);
                            break;
                        case Type.DOUBLE_ARRAY:
                            OffHeapDoubleArray.save(rawValue, buffer);
                            break;
                        case Type.LONG_ARRAY:
                            OffHeapLongArray.save(rawValue, buffer);
                            break;
                        case Type.INT_ARRAY:
                            OffHeapIntArray.save(rawValue, buffer);
                            break;
                        case Type.RELATION:
                            OffHeapRelationship.save(rawValue, buffer);
                            break;
                        case Type.STRING_TO_LONG_MAP:
                            OffHeapStringLongMap.save(rawValue, buffer);
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            OffHeapLongLongMap.save(rawValue, buffer);
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            OffHeapLongLongArrayMap.save(rawValue, buffer);
                            break;
                        default:
                            break;
                    }
                }
            } else {
                //we save a empty chunk
                Base64.encodeLongToBuffer(0, buffer);
            }
        } finally {
            unlock();
        }
    }

    @Override
    public void loadFrom(final StateChunk origin) {
        if (origin == null) {
            return;
        }
        lock();
        try {
            OffHeapStateChunk casted = (OffHeapStateChunk) origin;
            casted.lock();
            try {
                //clean previous if exist
                long addr = space.addrByIndex(index);
                if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                    free(addr);
                }
                //retrieve to clone address
                final long castedAddr = space.addrByIndex(casted.index);

                //nothing set yet, don't clone
                if (castedAddr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                    space.setAddrByIndex(index, OffHeapConstants.OFFHEAP_NULL_PTR);

                } else {

                    final long castedCapacity = OffHeapLongArray.get(castedAddr, CAPACITY);
                    final long castedSize = OffHeapLongArray.get(castedAddr, SIZE);
                    final long castedSubHash = OffHeapLongArray.get(castedAddr, SUBHASH);
                    addr = OffHeapLongArray.cloneArray(castedAddr, OFFSET + (castedCapacity * ELEM_SIZE));
                    //clone sub hash if needed
                    if (castedSubHash != OffHeapConstants.OFFHEAP_NULL_PTR) {
                        OffHeapLongArray.set(addr, SUBHASH, OffHeapLongArray.cloneArray(castedSubHash, castedCapacity * 3));
                    }
                    //clone complex structures
                    //TODO optimze with a flag to avoid this iteration
                    for (int i = 0; i < castedSize; i++) {
                        switch (type(castedAddr, i)) {
                            case Type.DOUBLE_ARRAY:
                                OffHeapDoubleArray.cloneObject(value(castedAddr, i));
                                break;
                            case Type.LONG_ARRAY:
                                OffHeapLongArray.cloneObject(value(castedAddr, i));
                                break;
                            case Type.INT_ARRAY:
                                OffHeapIntArray.cloneObject(value(castedAddr, i));
                                break;
                            case Type.STRING:
                                OffHeapString.clone(value(castedAddr, i));
                                break;
                            case Type.RELATION:
                                setValue(addr, i, OffHeapRelationship.clone(value(castedAddr, i)));
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                setValue(addr, i, OffHeapLongLongMap.clone(value(castedAddr, i)));
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                setValue(addr, i, OffHeapLongLongArrayMap.clone(value(castedAddr, i)));
                                break;
                            case Type.STRING_TO_LONG_MAP:
                                setValue(addr, i, OffHeapStringLongMap.clone(value(castedAddr, i)));
                                break;
                        }
                    }
                    space.setAddrByIndex(index, addr);
                }
            } finally {
                casted.unlock();
            }
        } finally {
            unlock();
        }
    }

    private long toAddr(final byte p_type, final Object p_unsafe_elem) {
        long param_elem = -1;
        if (p_unsafe_elem != null) {
            try {
                switch (p_type) {
                    case Type.BOOL:
                        param_elem = ((boolean) p_unsafe_elem) ? 1 : 0;
                        break;
                    // case Type.DOUBLE:
                    //      param_double_elem = (double) p_unsafe_elem;
                    //     break;
                    case Type.LONG:
                        if (p_unsafe_elem instanceof Integer) {
                            int preCasting = (Integer) p_unsafe_elem;
                            param_elem = (long) preCasting;
                        } else {
                            param_elem = (long) p_unsafe_elem;
                        }
                        break;
                    case Type.INT:
                        param_elem = (int) p_unsafe_elem;
                        break;
                    case Type.STRING:
                        param_elem = OffHeapString.fromObject((String) p_unsafe_elem);
                        break;
                    case Type.DOUBLE_ARRAY:
                        param_elem = OffHeapDoubleArray.fromObject((double[]) p_unsafe_elem);
                        break;
                    case Type.LONG_ARRAY:
                        param_elem = OffHeapLongArray.fromObject((long[]) p_unsafe_elem);
                        break;
                    case Type.INT_ARRAY:
                        param_elem = OffHeapIntArray.fromObject((int[]) p_unsafe_elem);
                        break;
                    case Type.RELATION:
                    case Type.STRING_TO_LONG_MAP:
                    case Type.LONG_TO_LONG_MAP:
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        //throw new RuntimeException("mwDB usage error, set method called with type " + Type.typeName(p_type) + ", is getOrCreate method instead");
                        param_elem = OffHeapConstants.OFFHEAP_NULL_PTR; //empty initial ptr
                        break;
                    default:
                        throw new RuntimeException("Internal Exception, unknown type");
                }
            } catch (Exception e) {
                throw new RuntimeException("mwDB usage error, set method called with type " + Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
            }
        }
        return param_elem;
    }

    private double toDoubleValue(final Object p_unsafe_elem) {
        return (double) p_unsafe_elem;
    }


    private long internal_set(final long p_key, final byte p_type, final Object p_unsafe_elem, boolean replaceIfPresent, boolean initial) {
        long addr = space.addrByIndex(index);
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            addr = allocate(addr, Constants.MAP_INITIAL_CAPACITY);
        }
        long entry = -1;
        long prev_entry = -1;
        long hashIndex = -1;
        long size = OffHeapLongArray.get(addr, SIZE);
        long capacity = OffHeapLongArray.get(addr, CAPACITY);
        long subhash_ptr = OffHeapLongArray.get(addr, SUBHASH);
        if (subhash_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            for (int i = 0; i < size; i++) {
                if (key(addr, i) == p_key) {
                    entry = i;
                    break;
                }
            }
        } else {
            hashIndex = HashHelper.longHash(p_key, capacity * 2);
            long m = hash(subhash_ptr, capacity, hashIndex);
            while (m != -1) {
                if (key(addr, m) == p_key) {
                    entry = m;
                    break;
                }
                prev_entry = m;
                m = next(subhash_ptr, m);
            }
        }
        //case already present
        if (entry != -1) {
            final byte found_type = type(addr, entry);
            if (replaceIfPresent || (p_type != found_type)) {
                if (p_unsafe_elem == null) {
                    /* Case: supression of a value */
                    //freeThePreviousValue
                    freeElement(value(addr, entry), found_type);
                    //then clean the acces chain
                    if (subhash_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                        //unHash previous
                        if (prev_entry != -1) {
                            setNext(subhash_ptr, prev_entry, next(subhash_ptr, entry));
                        } else {
                            setHash(subhash_ptr, capacity, hashIndex, -1);
                        }
                    }
                    long indexVictim = size - 1;
                    //just pop the last value
                    if (entry == indexVictim) {
                        setKey(addr, entry, OffHeapConstants.OFFHEAP_NULL_PTR);
                        setValue(addr, entry, OffHeapConstants.OFFHEAP_NULL_PTR);
                        setType(addr, entry, (byte) OffHeapConstants.OFFHEAP_NULL_PTR);
                    } else {
                        //we need to reHash the new last element at our place
                        setKey(addr, entry, key(addr, indexVictim));
                        final byte typeOfVictim = type(addr, indexVictim);
                        if (typeOfVictim == Type.DOUBLE) {
                            final double victimDoubleValue = doubleValue(addr, indexVictim);
                            setDoubleValue(addr, entry, victimDoubleValue);
                        } else {
                            setValue(addr, entry, value(addr, indexVictim));
                        }
                        setType(addr, entry, typeOfVictim);
                        if (subhash_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                            setNext(addr, entry, next(subhash_ptr, indexVictim));
                            long victimHash = HashHelper.longHash(key(addr, entry), capacity * 2);
                            long m = hash(subhash_ptr, capacity, victimHash);
                            if (m == indexVictim) {
                                //the victim was the head of hashing list
                                setHash(subhash_ptr, capacity, victimHash, entry);
                            } else {
                                //the victim is in the next, rechain it
                                while (m != -1) {
                                    if (next(addr, m) == indexVictim) {
                                        setNext(subhash_ptr, m, entry);
                                        break;
                                    }
                                    m = next(subhash_ptr, m);
                                }
                            }
                        }
                    }
                    OffHeapLongArray.set(addr, SIZE, size - 1);
                } else {
                    final long previous_value = value(addr, entry);
                    //freeThePreviousValue
                    if (p_type == Type.DOUBLE) {
                        setDoubleValue(addr, entry, toDoubleValue(p_unsafe_elem));
                    } else {
                        setValue(addr, entry, toAddr(p_type, p_unsafe_elem));
                    }
                    freeElement(previous_value, found_type);
                    if (found_type != p_type) {
                        setType(addr, entry, p_type);
                    }
                }
            }
            if (!initial) {
                declareDirty();
            }
            return entry;
        }
        if (size >= capacity) {
            long newCapacity = capacity * 2;
            addr = allocate(addr, newCapacity);
            subhash_ptr = OffHeapLongArray.get(addr, SUBHASH);
            capacity = newCapacity;
            hashIndex = HashHelper.longHash(p_key, capacity * 2);
        }
        final long insert_index = size;
        setKey(addr, insert_index, p_key);
        if (p_type == Type.DOUBLE) {
            setDoubleValue(addr, insert_index, toDoubleValue(p_unsafe_elem));
        } else {
            setValue(addr, insert_index, toAddr(p_type, p_unsafe_elem));
        }
        setType(addr, insert_index, p_type);
        if (subhash_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            setNext(subhash_ptr, insert_index, hash(subhash_ptr, capacity, hashIndex));
            setHash(subhash_ptr, capacity, hashIndex, insert_index);
        }
        size++;
        OffHeapLongArray.set(addr, SIZE, size);
        declareDirty();
        if (!initial) {
            declareDirty();
        }
        return insert_index;
    }

    private long allocate(final long addr, final long newCapacity) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            //nothing before, initial allocation...
            final long new_addr = OffHeapLongArray.allocate(OFFSET + (newCapacity * ELEM_SIZE));
            space.setAddrByIndex(index, new_addr);
            OffHeapLongArray.set(new_addr, CAPACITY, newCapacity);
            OffHeapLongArray.set(new_addr, DIRTY, 0);
            OffHeapLongArray.set(new_addr, SIZE, 0);
            if (newCapacity > Constants.MAP_INITIAL_CAPACITY) {
                OffHeapLongArray.set(new_addr, SUBHASH, OffHeapLongArray.allocate(newCapacity * 3));
            } else {
                OffHeapLongArray.set(new_addr, SUBHASH, OffHeapConstants.OFFHEAP_NULL_PTR);
            }
            return new_addr;
        } else {
            //reallocation or overallocation
            final long new_addr = OffHeapLongArray.reallocate(addr, OFFSET + (newCapacity * ELEM_SIZE));
            space.setAddrByIndex(index, new_addr);
            OffHeapLongArray.set(new_addr, CAPACITY, newCapacity);
            long subHash_ptr = OffHeapLongArray.get(new_addr, SUBHASH);
            if (subHash_ptr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                subHash_ptr = OffHeapLongArray.allocate(newCapacity * 3);
            } else {
                subHash_ptr = OffHeapLongArray.reallocate(subHash_ptr, newCapacity * 3);
                OffHeapLongArray.reset(subHash_ptr, newCapacity * 3);
            }
            OffHeapLongArray.set(new_addr, SUBHASH, subHash_ptr);
            //reHash
            final long size = OffHeapLongArray.get(new_addr, SIZE);
            final long hash_capacity = newCapacity * 2;
            for (long i = 0; i < size; i++) {
                long keyHash = HashHelper.longHash(key(new_addr, i), hash_capacity);
                setNext(subHash_ptr, i, hash(subHash_ptr, newCapacity, keyHash));
                setHash(subHash_ptr, newCapacity, keyHash, i);
            }
            return new_addr;
        }
    }

    @Override
    public final void load(final Buffer buffer) {
        if (buffer == null || buffer.length() == 0) {
            return;
        }
        lock();
        try {
            long addr = space.addrByIndex(index);
            final boolean initial = (addr == OffHeapConstants.OFFHEAP_NULL_PTR);
            long capacity = 0;
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                capacity = OffHeapLongArray.get(addr, CAPACITY);
            }
            int cursor = 0;
            long payloadSize = buffer.length();
            int previousStart = -1;
            long currentChunkElemKey = Constants.NULL_LONG;
            byte currentChunkElemType = -1;
            //init detections
            boolean isFirstElem = true;
            //array sub creation variable
            double[] currentDoubleArr = null;
            long[] currentLongArr = null;
            int[] currentIntArr = null;
            //map sub creation variables
            OffHeapRelationship currentRelation = null;
            OffHeapStringLongMap currentStringLongMap = null;
            OffHeapLongLongMap currentLongLongMap = null;
            OffHeapLongLongArrayMap currentLongLongArrayMap = null;
            //array variables
            long currentSubSize = -1;
            int currentSubIndex = 0;
            //map key variables
            long currentMapLongKey = Constants.NULL_LONG;
            String currentMapStringKey = null;
            while (cursor < payloadSize) {
                byte current = buffer.read(cursor);
                if (current == Constants.CHUNK_SEP) {
                    if (isFirstElem) {
                        //initial the map
                        isFirstElem = false;
                        final int stateChunkSize = Base64.decodeToIntWithBounds(buffer, 0, cursor);
                        final int closePowerOfTwo = (int) Math.pow(2, Math.ceil(Math.log(stateChunkSize) / Math.log(2)));
                        if (capacity < closePowerOfTwo) {
                            addr = allocate(addr, closePowerOfTwo);
                            capacity = closePowerOfTwo;
                        }
                        previousStart = cursor + 1;
                    } else {
                        if (currentChunkElemType != -1) {
                            Object toInsert = null;
                            switch (currentChunkElemType) {
                                case Type.BOOL:
                                    if (buffer.read(previousStart) == Constants.BOOL_FALSE) {
                                        toInsert = false;
                                    } else if (buffer.read(previousStart) == Constants.BOOL_TRUE) {
                                        toInsert = true;
                                    }
                                    break;
                                case Type.STRING:
                                    toInsert = Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
                                    break;
                                case Type.DOUBLE:
                                    toInsert = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                                    break;
                                case Type.LONG:
                                    toInsert = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                    break;
                                case Type.INT:
                                    toInsert = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                                    break;
                                case Type.DOUBLE_ARRAY:
                                    if (currentDoubleArr == null) {
                                        currentDoubleArr = new double[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                                    } else {
                                        currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                                    }
                                    toInsert = currentDoubleArr;
                                    break;
                                case Type.LONG_ARRAY:
                                    if (currentLongArr == null) {
                                        currentLongArr = new long[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                                    } else {
                                        currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                    }
                                    toInsert = currentLongArr;
                                    break;
                                case Type.INT_ARRAY:
                                    if (currentIntArr == null) {
                                        currentIntArr = new int[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                                    } else {
                                        currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                                    }
                                    toInsert = currentIntArr;
                                    break;
                                case Type.RELATION:
                                    if (currentRelation == null) {
                                        currentRelation = new OffHeapRelationship(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                                        currentRelation.allocate(Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                                    } else {
                                        currentRelation.internal_add(Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                    }
                                    //toInsert = currentRelation;
                                    break;
                                case Type.STRING_TO_LONG_MAP:
                                    if (currentMapStringKey != null) {
                                        currentStringLongMap.internal_put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                    }
                                    //toInsert = currentStringLongMap;
                                    break;
                                case Type.LONG_TO_LONG_MAP:
                                    if (currentMapLongKey != Constants.NULL_LONG) {
                                        currentLongLongMap.internal_put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                    }
                                    //toInsert = currentLongLongMap;
                                    break;
                                case Type.LONG_TO_LONG_ARRAY_MAP:
                                    if (currentMapLongKey != Constants.NULL_LONG) {
                                        currentLongLongArrayMap.internal_put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                    }
                                    //toInsert = currentLongLongArrayMap;
                                    break;
                            }
                            if (toInsert != null) {
                                //insert K/V
                                internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true, initial); //enhance this with boolean array
                            }
                        }
                        //next round, reset all variables...
                        previousStart = cursor + 1;
                        currentChunkElemKey = Constants.NULL_LONG;
                        currentChunkElemType = -1;
                        currentSubSize = -1;
                        currentSubIndex = 0;
                        currentMapLongKey = Constants.NULL_LONG;
                        currentMapStringKey = null;
                    }
                } else if (current == Constants.CHUNK_SUB_SEP) { //SEPARATION BETWEEN KEY,TYPE,VALUE
                    if (currentChunkElemKey == Constants.NULL_LONG) {
                        currentChunkElemKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        previousStart = cursor + 1;
                    } else if (currentChunkElemType == -1) {
                        currentChunkElemType = (byte) Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                        previousStart = cursor + 1;
                    }
                } else if (current == Constants.CHUNK_SUB_SUB_SEP) { //SEPARATION BETWEEN ARRAY VALUES AND MAP KEY/VALUE TUPLES
                    if (currentSubSize == -1) {
                        currentSubSize = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        //init array or maps
                        switch (currentChunkElemType) {
                            case Type.DOUBLE_ARRAY:
                                currentDoubleArr = new double[(int) currentSubSize];
                                break;
                            case Type.LONG_ARRAY:
                                currentLongArr = new long[(int) currentSubSize];
                                break;
                            case Type.INT_ARRAY:
                                currentIntArr = new int[(int) currentSubSize];
                                break;
                            case Type.RELATION:
                                currentRelation = new OffHeapRelationship(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                                currentRelation.allocate((int) currentSubSize);
                                break;
                            case Type.STRING_TO_LONG_MAP:
                                currentStringLongMap = new OffHeapStringLongMap(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                                currentStringLongMap.preAllocate(currentSubSize);
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                currentLongLongMap = new OffHeapLongLongMap(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                                currentLongLongMap.preAllocate(currentSubSize);
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                currentLongLongArrayMap = new OffHeapLongLongArrayMap(this, internal_set(currentChunkElemKey, currentChunkElemType, null, true, initial));
                                currentLongLongArrayMap.preAllocate(currentSubSize);
                                break;
                        }
                    } else {
                        switch (currentChunkElemType) {
                            case Type.DOUBLE_ARRAY:
                                currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                                currentSubIndex++;
                                break;
                            case Type.RELATION:
                                currentRelation.add(Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                break;
                            case Type.LONG_ARRAY:
                                currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                                currentSubIndex++;
                                break;
                            case Type.INT_ARRAY:
                                currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                                currentSubIndex++;
                                break;
                            case Type.STRING_TO_LONG_MAP:
                                if (currentMapStringKey != null) {
                                    currentStringLongMap.internal_put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                    currentMapStringKey = null;
                                }
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                if (currentMapLongKey != Constants.NULL_LONG) {
                                    currentLongLongMap.internal_put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                    currentMapLongKey = Constants.NULL_LONG;
                                }
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                if (currentMapLongKey != Constants.NULL_LONG) {
                                    currentLongLongArrayMap.internal_put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                    currentMapLongKey = Constants.NULL_LONG;
                                }
                                break;
                        }
                    }
                    previousStart = cursor + 1;
                } else if (current == Constants.CHUNK_SUB_SUB_SUB_SEP) {
                    switch (currentChunkElemType) {
                        case Type.STRING_TO_LONG_MAP:
                            if (currentMapStringKey == null) {
                                currentMapStringKey = Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
                            } else {
                                currentStringLongMap.internal_put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                //reset key for next loop
                                currentMapStringKey = null;
                            }
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            if (currentMapLongKey == Constants.NULL_LONG) {
                                currentMapLongKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                            } else {
                                currentLongLongMap.internal_put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                //reset key for next loop
                                currentMapLongKey = Constants.NULL_LONG;
                            }
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            if (currentMapLongKey == Constants.NULL_LONG) {
                                currentMapLongKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                            } else {
                                currentLongLongArrayMap.internal_put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                //reset key for next loop
                                currentMapLongKey = Constants.NULL_LONG;
                            }
                            break;
                    }
                    previousStart = cursor + 1;
                }
                cursor++;
            }
            //take the last element
            if (currentChunkElemType != -1) {
                Object toInsert = null;
                switch (currentChunkElemType) {
                    case Type.BOOL:
                        if (buffer.read(previousStart) == Constants.BOOL_FALSE) {
                            toInsert = false;
                        } else if (buffer.read(previousStart) == Constants.BOOL_TRUE) {
                            toInsert = true;
                        }
                        break;
                    case Type.STRING:
                        toInsert = Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
                        break;
                    case Type.DOUBLE:
                        toInsert = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                        break;
                    case Type.LONG:
                        toInsert = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        break;
                    case Type.INT:
                        toInsert = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                        break;
                    case Type.DOUBLE_ARRAY:
                        if (currentDoubleArr == null) {
                            currentDoubleArr = new double[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                        } else {
                            currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor);
                        }
                        toInsert = currentDoubleArr;
                        break;
                    case Type.LONG_ARRAY:
                        if (currentLongArr == null) {
                            currentLongArr = new long[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                        } else {
                            currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        }
                        toInsert = currentLongArr;
                        break;
                    case Type.INT_ARRAY:
                        if (currentIntArr == null) {
                            currentIntArr = new int[Base64.decodeToIntWithBounds(buffer, previousStart, cursor)];
                        } else {
                            currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                        }
                        toInsert = currentIntArr;
                        break;
                    case Type.RELATION:
                        if (currentRelation != null) {
                            currentRelation.add(Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                        }
                        //toInsert = currentRelation;
                        break;
                    case Type.STRING_TO_LONG_MAP:
                        if (currentMapStringKey != null) {
                            currentStringLongMap.internal_put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        }
                        //toInsert = currentStringLongMap;
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        if (currentMapLongKey != Constants.NULL_LONG) {
                            currentLongLongMap.internal_put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        }
                        //toInsert = currentLongLongMap;
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        if (currentMapLongKey != Constants.NULL_LONG) {
                            currentLongLongArrayMap.internal_put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                        }
                        //toInsert = currentLongLongArrayMap;
                        break;
                }
                if (toInsert != null) {
                    internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true, initial); //enhance this with boolean array
                }
            }
        } finally {
            unlock();
        }
    }

    static void free(final long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            final long subhash_ptr = OffHeapLongArray.get(addr, SUBHASH);
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (long i = 0; i < size; i++) {
                freeElement(value(addr, i), type(addr, i));
            }
            if (subhash_ptr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(subhash_ptr);
            }
            OffHeapLongArray.free(addr);
        }
    }

    private static void freeElement(final long addr, final byte elemType) {
        switch (elemType) {
            case Type.STRING:
                OffHeapString.free(addr);
                break;
            case Type.DOUBLE_ARRAY:
                OffHeapDoubleArray.freeObject(addr);
                break;
            case Type.RELATION:
                OffHeapRelationship.free(addr);
                break;
            case Type.LONG_ARRAY:
                OffHeapLongArray.freeObject(addr);
                break;
            case Type.INT_ARRAY:
                OffHeapIntArray.freeObject(addr);
                break;
            case Type.STRING_TO_LONG_MAP:
                OffHeapStringLongMap.free(addr);
                break;
            case Type.LONG_TO_LONG_MAP:
                OffHeapLongLongMap.free(addr);
                break;
            case Type.LONG_TO_LONG_ARRAY_MAP:
                OffHeapLongLongArrayMap.free(addr);
                break;
        }
    }

}
