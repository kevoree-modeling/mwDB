package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.Type;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.core.CoreConstants;
import org.mwg.base.AbstractExternalAttribute;
import org.mwg.plugin.ExternalAttributeFactory;
import org.mwg.utility.HashHelper;
import org.mwg.utility.Base64;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.struct.*;

import java.util.Arrays;

class HeapStateChunk implements StateChunk {

    private final long _index;
    private final HeapChunkSpace _space;

    private int _capacity;
    private volatile int _size;
    private long[] _k;
    private Object[] _v;

    private int[] _next;
    private int[] _hash;
    private byte[] _type;

    private boolean _dirty;

    HeapStateChunk(final HeapChunkSpace p_space, final long p_index) {
        _space = p_space;
        _index = p_index;
        //null hash function
        _next = null;
        _hash = null;
        _type = null;
        //init to empty size
        _size = 0;
        _capacity = 0;
        _dirty = false;
    }

    @Override
    public final long world() {
        return _space.worldByIndex(_index);
    }

    @Override
    public final long time() {
        return _space.timeByIndex(_index);
    }

    @Override
    public final long id() {
        return _space.idByIndex(_index);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.STATE_CHUNK;
    }

    @Override
    public final long index() {
        return _index;
    }

    @Override
    public synchronized final Object get(final long p_key) {
        return internal_get(p_key);
    }

    private int internal_find(final long p_key) {
        if (_size == 0) {
            return -1;
        } else if (_hash == null) {
            for (int i = 0; i < _size; i++) {
                if (_k[i] == p_key) {
                    return i;
                }
            }
            return -1;
        } else {
            final int hashIndex = (int) HashHelper.longHash(p_key, _capacity * 2);
            int m = _hash[hashIndex];
            while (m >= 0) {
                if (p_key == _k[m]) {
                    return m;
                } else {
                    m = _next[m];
                }
            }
            return -1;
        }
    }

    private Object internal_get(final long p_key) {
        //empty chunk, we return immediately
        if (_size == 0) {
            return null;
        }
        int found = internal_find(p_key);
        Object result;
        if (found != -1) {
            result = _v[found];
            //TODO optimize this
            if (result != null) {
                switch (_type[found]) {
                    case Type.DOUBLE_ARRAY:
                        double[] castedResultD = (double[]) result;
                        double[] copyD = new double[castedResultD.length];
                        System.arraycopy(castedResultD, 0, copyD, 0, castedResultD.length);
                        return copyD;
                    case Type.LONG_ARRAY:
                        long[] castedResultL = (long[]) result;
                        long[] copyL = new long[castedResultL.length];
                        System.arraycopy(castedResultL, 0, copyL, 0, castedResultL.length);
                        return copyL;
                    case Type.INT_ARRAY:
                        int[] castedResultI = (int[]) result;
                        int[] copyI = new int[castedResultI.length];
                        System.arraycopy(castedResultI, 0, copyI, 0, castedResultI.length);
                        return copyI;
                    default:
                        return result;
                }
            }
        }
        return null;
    }

    /**
     * {@native ts
     * if(p_unsafe_elem != null){
     * if(p_elemType == org.mwg.Type.STRING){ if(!(typeof p_unsafe_elem === 'string')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.BOOL){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.DOUBLE || p_elemType == org.mwg.Type.LONG || p_elemType == org.mwg.Type.INT){ if(!(typeof p_unsafe_elem === 'number')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.DOUBLE_ARRAY){ if(!(p_unsafe_elem instanceof Float64Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.LONG_ARRAY){ if(!(p_unsafe_elem instanceof Float64Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.INT_ARRAY){ if(!(p_unsafe_elem instanceof Int32Array)){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.STRING_TO_LONG_MAP){ if(!(typeof p_unsafe_elem === 'object')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.LONG_TO_LONG_MAP){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * if(p_elemType == org.mwg.Type.LONG_TO_LONG_ARRAY_MAP){ if(!(typeof p_unsafe_elem === 'boolean')){ throw new Error("mwDB usage error, set method called with type " + org.mwg.Type.typeName(p_elemType) + " while param object is " + p_unsafe_elem); } }
     * }
     * this.internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true, false);
     * }
     */
    @Override
    public synchronized final void set(final long p_elementIndex, final byte p_elemType, final Object p_unsafe_elem) {
        internal_set(p_elementIndex, p_elemType, p_unsafe_elem, true, false);
    }

    @Override
    public synchronized final void setFromKey(final String key, final byte p_elemType, final Object p_unsafe_elem) {
        internal_set(_space.graph().resolver().stringToHash(key, true), p_elemType, p_unsafe_elem, true, false);
    }


    @Override
    public synchronized final Object getFromKey(final String key) {
        return internal_get(_space.graph().resolver().stringToHash(key, false));
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
    public final <A> A getWithDefault(final long key, final A defaultValue) {
        final Object result = get(key);
        if (result == null) {
            return defaultValue;
        } else {
            return (A) result;
        }
    }

    @Override
    public synchronized final byte getType(final long p_key) {
        if (_size == 0) {
            return -1;
        }
        if (_hash == null) {
            for (int i = 0; i < _capacity; i++) {
                if (_k[i] == p_key) {
                    return _type[i];
                }
            }
        } else {
            int hashIndex = (int) HashHelper.longHash(p_key, _capacity * 2);
            int m = _hash[hashIndex];
            while (m >= 0) {
                if (p_key == _k[m]) {
                    return _type[m];
                } else {
                    m = _next[m];
                }
            }
        }
        return -1;
    }

    @Override
    public byte getTypeFromKey(final String key) {
        return getType(_space.graph().resolver().stringToHash(key, false));
    }

    @Override
    public synchronized final Object getOrCreate(final long p_key, final byte p_type) {
        final int found = internal_find(p_key);
        if (found != -1) {
            if (_type[found] == p_type) {
                return _v[found];
            }
        }
        Object toSet = null;
        switch (p_type) {
            case Type.RELATION:
                toSet = new HeapRelationship(this, null);
                break;
            case Type.MATRIX:
                toSet = new HeapMatrix(this, null);
                break;
            case Type.STRING_TO_LONG_MAP:
                toSet = new HeapStringLongMap(this);
                break;
            case Type.LONG_TO_LONG_MAP:
                toSet = new HeapLongLongMap(this);
                break;
            case Type.LONG_TO_LONG_ARRAY_MAP:
                toSet = new HeapLongLongArrayMap(this);
                break;
        }
        internal_set(p_key, p_type, toSet, true, false);
        return toSet;
    }

    @Override
    public synchronized Object getOrCreateExternal(long p_key, String externalTypeName) {
        final int found = internal_find(p_key);
        if (found != -1) {
            if (_type[found] == Type.EXTERNAL) {
                return _v[found];
            }
        }
        AbstractExternalAttribute toSet = null;
        final ExternalAttributeFactory factory = _space.graph().externalAttribute(externalTypeName);
        if (factory != null) {
            toSet = factory.create();
        }
        internal_set(p_key, Type.EXTERNAL, toSet, true, false);
        toSet.notifyDirty(() -> declareDirty());
        return toSet;
    }

    @Override
    public final Object getOrCreateFromKey(final String key, final byte elemType) {
        return getOrCreate(_space.graph().resolver().stringToHash(key, true), elemType);
    }

    final void declareDirty() {
        if (_space != null && !_dirty) {
            _dirty = true;
            _space.notifyUpdate(_index);
        }
    }

    @Override
    public synchronized final void save(final Buffer buffer) {
        Base64.encodeIntToBuffer(_size, buffer);
        for (int i = 0; i < _size; i++) {
            if (_v[i] != null) { //there is a real value
                final Object loopValue = _v[i];
                if (loopValue != null) {
                    buffer.write(CoreConstants.CHUNK_SEP);
                    Base64.encodeLongToBuffer(_k[i], buffer);
                    buffer.write(CoreConstants.CHUNK_SUB_SEP);
                    Base64.encodeIntToBuffer(_type[i], buffer);
                    buffer.write(CoreConstants.CHUNK_SUB_SEP);
                    switch (_type[i]) {
                        case Type.STRING:
                            Base64.encodeStringToBuffer((String) loopValue, buffer);
                            break;
                        case Type.BOOL:
                            if ((Boolean) _v[i]) {
                                buffer.write(CoreConstants.BOOL_TRUE);
                            } else {
                                buffer.write(CoreConstants.BOOL_FALSE);
                            }
                            break;
                        case Type.LONG:
                            Base64.encodeLongToBuffer((Long) loopValue, buffer);
                            break;
                        case Type.DOUBLE:
                            Base64.encodeDoubleToBuffer((Double) loopValue, buffer);
                            break;
                        case Type.INT:
                            Base64.encodeIntToBuffer((Integer) loopValue, buffer);
                            break;
                        case Type.DOUBLE_ARRAY:
                            double[] castedDoubleArr = (double[]) loopValue;
                            Base64.encodeIntToBuffer(castedDoubleArr.length, buffer);
                            for (int j = 0; j < castedDoubleArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                            }
                            break;
                        case Type.EXTERNAL:
                            AbstractExternalAttribute externalAttribute = (AbstractExternalAttribute) loopValue;
                            final long encodedName = _space.graph().resolver().stringToHash(externalAttribute.name(), false);
                            Base64.encodeLongToBuffer(encodedName, buffer);
                            buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                            String saved = externalAttribute.save();
                            if (saved != null) {
                                Base64.encodeStringToBuffer(saved, buffer);
                            }
                            break;
                        case Type.RELATION:
                            HeapRelationship castedLongArrRel = (HeapRelationship) loopValue;
                            Base64.encodeIntToBuffer(castedLongArrRel.size(), buffer);
                            for (int j = 0; j < castedLongArrRel.size(); j++) {
                                buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeLongToBuffer(castedLongArrRel.unsafe_get(j), buffer);
                            }
                            break;
                        case Type.LONG_ARRAY:
                            long[] castedLongArr = (long[]) loopValue;
                            Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                            for (int j = 0; j < castedLongArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                            }
                            break;
                        case Type.INT_ARRAY:
                            int[] castedIntArr = (int[]) loopValue;
                            Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                            for (int j = 0; j < castedIntArr.length; j++) {
                                buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeIntToBuffer(castedIntArr[j], buffer);
                            }
                            break;
                        case Type.MATRIX:
                            HeapMatrix castedMatrix = (HeapMatrix) loopValue;
                            final double[] unsafeContent = castedMatrix.unsafe_data();
                            if (unsafeContent != null) {
                                Base64.encodeIntToBuffer(unsafeContent.length, buffer);
                                for (int j = 0; j < unsafeContent.length; j++) {
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeDoubleToBuffer(unsafeContent[j], buffer);
                                }
                            }
                            break;
                        case Type.STRING_TO_LONG_MAP:
                            HeapStringLongMap castedStringLongMap = (HeapStringLongMap) loopValue;
                            Base64.encodeLongToBuffer(castedStringLongMap.size(), buffer);
                            castedStringLongMap.unsafe_each(new StringLongMapCallBack() {
                                @Override
                                public void on(final String key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeStringToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            HeapLongLongMap castedLongLongMap = (HeapLongLongMap) loopValue;
                            Base64.encodeLongToBuffer(castedLongLongMap.size(), buffer);
                            castedLongLongMap.unsafe_each(new LongLongMapCallBack() {
                                @Override
                                public void on(final long key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            HeapLongLongArrayMap castedLongLongArrayMap = (HeapLongLongArrayMap) loopValue;
                            Base64.encodeLongToBuffer(castedLongLongArrayMap.size(), buffer);
                            castedLongLongArrayMap.unsafe_each(new LongLongArrayMapCallBack() {
                                @Override
                                public void on(final long key, final long value) {
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(key, buffer);
                                    buffer.write(CoreConstants.CHUNK_SUB_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        _dirty = false;
    }

    @Override
    public synchronized final void each(final NodeStateCallback callBack) {
        for (int i = 0; i < _size; i++) {
            if (_v[i] != null) {
                callBack.on(_k[i], _type[i], _v[i]);
            }
        }
    }

    @Override
    public synchronized void loadFrom(final StateChunk origin) {
        if (origin == null) {
            return;
        }
        HeapStateChunk casted = (HeapStateChunk) origin;
        _capacity = casted._capacity;
        _size = casted._size;
        //copy keys
        if (casted._k != null) {
            long[] cloned_k = new long[_capacity];
            System.arraycopy(casted._k, 0, cloned_k, 0, _capacity);
            _k = cloned_k;
        }
        //copy values
        /*
        Object[] cloned_v = new Object[_capacity];
        System.arraycopy(casted._v, 0, cloned_v, 0, _capacity);
        _v = cloned_v;
        */
        //copy types
        if (casted._type != null) {
            byte[] cloned_type = new byte[_capacity];
            System.arraycopy(casted._type, 0, cloned_type, 0, _capacity);
            _type = cloned_type;
        }
        //copy next if not empty
        if (casted._next != null) {
            int[] cloned_next = new int[_capacity];
            System.arraycopy(casted._next, 0, cloned_next, 0, _capacity);
            _next = cloned_next;
        }
        if (casted._hash != null) {
            int[] cloned_hash = new int[_capacity * 2];
            System.arraycopy(casted._hash, 0, cloned_hash, 0, _capacity * 2);
            _hash = cloned_hash;
        }
        if (casted._v != null) {
            _v = new Object[_capacity];
            for (int i = 0; i < _size; i++) {
                switch (casted._type[i]) {
                    case Type.LONG_TO_LONG_MAP:
                        if (casted._v[i] != null) {
                            _v[i] = ((HeapLongLongMap) casted._v[i]).cloneFor(this);
                        }
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        if (casted._v[i] != null) {
                            _v[i] = ((HeapLongLongArrayMap) casted._v[i]).cloneFor(this);
                        }
                        break;
                    case Type.STRING_TO_LONG_MAP:
                        if (casted._v[i] != null) {
                            _v[i] = ((HeapStringLongMap) casted._v[i]).cloneFor(this);
                        }
                        break;
                    case Type.RELATION:
                        if (casted._v[i] != null) {
                            _v[i] = new HeapRelationship(this, (HeapRelationship) casted._v[i]);
                        }
                        break;
                    case Type.MATRIX:
                        if (casted._v[i] != null) {
                            _v[i] = new HeapMatrix(this, (HeapMatrix) casted._v[i]);
                        }
                        break;
                    case Type.EXTERNAL:
                        if (casted._v[i] != null) {
                            _v[i] = ((AbstractExternalAttribute) casted._v[i]).copy();
                        }
                        break;
                    default:
                        _v[i] = casted._v[i];
                        break;
                }
            }
        }
    }

    private void internal_set(final long p_key, final byte p_type, final Object p_unsafe_elem, boolean replaceIfPresent, boolean initial) {
        Object param_elem = null;
        //check the param type
        if (p_unsafe_elem != null) {
            try {
                switch (p_type) {
                    case Type.BOOL:
                        param_elem = (boolean) p_unsafe_elem;
                        break;
                    case Type.DOUBLE:
                        param_elem = (double) p_unsafe_elem;
                        break;
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
                        param_elem = (String) p_unsafe_elem;
                        break;
                    case Type.MATRIX:
                        param_elem = (Matrix) p_unsafe_elem;
                        break;
                    case Type.RELATION:
                        param_elem = (Relationship) p_unsafe_elem;
                        break;
                    case Type.EXTERNAL:
                        param_elem = p_unsafe_elem;
                        break;
                    case Type.DOUBLE_ARRAY:
                        double[] castedParamDouble = (double[]) p_unsafe_elem;
                        double[] clonedDoubleArray = new double[castedParamDouble.length];
                        System.arraycopy(castedParamDouble, 0, clonedDoubleArray, 0, castedParamDouble.length);
                        param_elem = clonedDoubleArray;
                        break;
                    case Type.LONG_ARRAY:
                        long[] castedParamLong = (long[]) p_unsafe_elem;
                        long[] clonedLongArray = new long[castedParamLong.length];
                        System.arraycopy(castedParamLong, 0, clonedLongArray, 0, castedParamLong.length);
                        param_elem = clonedLongArray;
                        break;
                    case Type.INT_ARRAY:
                        int[] castedParamInt = (int[]) p_unsafe_elem;
                        int[] clonedIntArray = new int[castedParamInt.length];
                        System.arraycopy(castedParamInt, 0, clonedIntArray, 0, castedParamInt.length);
                        param_elem = clonedIntArray;
                        break;
                    case Type.STRING_TO_LONG_MAP:
                        param_elem = (StringLongMap) p_unsafe_elem;
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        param_elem = (LongLongMap) p_unsafe_elem;
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        param_elem = (LongLongArrayMap) p_unsafe_elem;
                        break;
                    default:
                        throw new RuntimeException("Internal Exception, unknown type");
                }
            } catch (Exception e) {
                throw new RuntimeException("mwDB usage error, set method called with type " + Type.typeName(p_type) + " while param object is " + p_unsafe_elem);
            }
        }
        //first value
        if (_k == null) {
            //we do not allocate for empty element
            if (param_elem == null) {
                return;
            }
            _capacity = Constants.MAP_INITIAL_CAPACITY;
            _k = new long[_capacity];
            _v = new Object[_capacity];
            _type = new byte[_capacity];
            _k[0] = p_key;
            _v[0] = param_elem;
            _type[0] = p_type;
            _size = 1;
            if (!initial) {
                declareDirty();
            }
            return;
        }
        int entry = -1;
        int p_entry = -1;
        int hashIndex = -1;
        if (_hash == null) {
            for (int i = 0; i < _size; i++) {
                if (_k[i] == p_key) {
                    entry = i;
                    break;
                }
            }
        } else {
            hashIndex = (int) HashHelper.longHash(p_key, _capacity * 2);
            int m = _hash[hashIndex];
            while (m != -1) {
                if (_k[m] == p_key) {
                    entry = m;
                    break;
                }
                p_entry = m;
                m = _next[m];
            }
        }
        //case already present
        if (entry != -1) {
            if (replaceIfPresent || (p_type != _type[entry])) {
                if (param_elem == null) {
                    if (_hash != null) {
                        //unHash previous
                        if (p_entry != -1) {
                            _next[p_entry] = _next[entry];
                        } else {
                            _hash[hashIndex] = -1;
                        }
                    }
                    int indexVictim = _size - 1;
                    //just pop the last value
                    if (entry == indexVictim) {
                        _k[entry] = -1;
                        _v[entry] = null;
                        _type[entry] = -1;
                    } else {
                        //we need to reHash the new last element at our place
                        _k[entry] = _k[indexVictim];
                        _v[entry] = _v[indexVictim];
                        _type[entry] = _type[indexVictim];
                        if (_hash != null) {
                            _next[entry] = _next[indexVictim];
                            int victimHash = (int) HashHelper.longHash(_k[entry], _capacity * 2);
                            int m = _hash[victimHash];
                            if (m == indexVictim) {
                                //the victim was the head of hashing list
                                _hash[victimHash] = entry;
                            } else {
                                //the victim is in the next, reChain it
                                while (m != -1) {
                                    if (_next[m] == indexVictim) {
                                        _next[m] = entry;
                                        break;
                                    }
                                    m = _next[m];
                                }
                            }
                        }
                    }
                    _size--;
                } else {
                    _v[entry] = param_elem;
                    if (_type[entry] != p_type) {
                        _type[entry] = p_type;
                    }
                }
            }
            if (!initial) {
                declareDirty();
            }
            return;
        }
        if (_size < _capacity) {
            _k[_size] = p_key;
            _v[_size] = param_elem;
            _type[_size] = p_type;
            if (_hash != null) {
                _next[_size] = _hash[hashIndex];
                _hash[hashIndex] = _size;
            }
            _size++;
            declareDirty();
            return;
        }
        //extend capacity
        int newCapacity = _capacity * 2;
        long[] ex_k = new long[newCapacity];
        System.arraycopy(_k, 0, ex_k, 0, _capacity);
        _k = ex_k;
        Object[] ex_v = new Object[newCapacity];
        System.arraycopy(_v, 0, ex_v, 0, _capacity);
        _v = ex_v;
        byte[] ex_type = new byte[newCapacity];
        System.arraycopy(_type, 0, ex_type, 0, _capacity);
        _type = ex_type;
        _capacity = newCapacity;
        //insert the next
        _k[_size] = p_key;
        _v[_size] = param_elem;
        _type[_size] = p_type;
        _size++;
        //reHash
        _hash = new int[_capacity * 2];
        Arrays.fill(_hash, 0, _capacity * 2, -1);
        _next = new int[_capacity];
        Arrays.fill(_next, 0, _capacity, -1);
        for (int i = 0; i < _size; i++) {
            int keyHash = (int) HashHelper.longHash(_k[i], _capacity * 2);
            _next[i] = _hash[keyHash];
            _hash[keyHash] = i;
        }
        if (!initial) {
            declareDirty();
        }
    }

    private void allocate(int newCapacity) {
        if (newCapacity <= _capacity) {
            return;
        }
        long[] ex_k = new long[newCapacity];
        if (_k != null) {
            System.arraycopy(_k, 0, ex_k, 0, _capacity);
        }
        _k = ex_k;
        Object[] ex_v = new Object[newCapacity];
        if (_v != null) {
            System.arraycopy(_v, 0, ex_v, 0, _capacity);
        }
        _v = ex_v;
        byte[] ex_type = new byte[newCapacity];
        if (_type != null) {
            System.arraycopy(_type, 0, ex_type, 0, _capacity);
        }
        _type = ex_type;
        _capacity = newCapacity;

        _hash = new int[_capacity * 2];
        Arrays.fill(_hash, 0, _capacity * 2, -1);
        _next = new int[_capacity];
        Arrays.fill(_next, 0, _capacity, -1);
        for (int i = 0; i < _size; i++) {
            int keyHash = (int) HashHelper.longHash(_k[i], _capacity * 2);
            _next[i] = _hash[keyHash];
            _hash[keyHash] = i;
        }
    }

    @Override
    public final synchronized void load(final Buffer buffer) {
        if (buffer == null || buffer.length() == 0) {
            return;
        }
        final boolean initial = _k == null;
        //reset size
        int cursor = 0;
        long payloadSize = buffer.length();
        int previousStart = -1;
        long currentChunkElemKey = CoreConstants.NULL_LONG;
        byte currentChunkElemType = -1;
        //init detections
        boolean isFirstElem = true;
        //array sub creation variable
        double[] currentDoubleArr = null;
        long[] currentLongArr = null;
        int[] currentIntArr = null;
        //map sub creation variables
        HeapMatrix currentMatrix = null;
        HeapRelationship currentRelation = null;
        HeapStringLongMap currentStringLongMap = null;
        HeapLongLongMap currentLongLongMap = null;
        HeapLongLongArrayMap currentLongLongArrayMap = null;
        //array variables
        long currentSubSize = -1;
        int currentSubIndex = 0;
        //map key variables
        long currentMapLongKey = CoreConstants.NULL_LONG;
        String currentMapStringKey = null;
        while (cursor < payloadSize) {
            byte current = buffer.read(cursor);
            if (current == CoreConstants.CHUNK_SEP) {
                if (isFirstElem) {
                    //initial the map
                    isFirstElem = false;
                    final int stateChunkSize = Base64.decodeToIntWithBounds(buffer, 0, cursor);
                    final int closePowerOfTwo = (int) Math.pow(2, Math.ceil(Math.log(stateChunkSize) / Math.log(2)));
                    allocate(closePowerOfTwo);
                    previousStart = cursor + 1;
                } else {
                    if (currentChunkElemType != -1) {
                        Object toInsert = null;
                        switch (currentChunkElemType) {
                            case Type.BOOL:
                                if (buffer.read(previousStart) == CoreConstants.BOOL_FALSE) {
                                    toInsert = false;
                                } else if (buffer.read(previousStart) == CoreConstants.BOOL_TRUE) {
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
                                    currentRelation = new HeapRelationship(this, null);
                                    currentRelation.allocate(Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                                } else {
                                    currentRelation.add(Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                toInsert = currentRelation;
                                break;
                            case Type.MATRIX:
                                if (currentMatrix == null) {
                                    currentMatrix = new HeapMatrix(this, null);
                                    currentMatrix.unsafe_init(Base64.decodeToIntWithBounds(buffer, previousStart, cursor));
                                } else {
                                    currentMatrix.unsafe_set(currentSubIndex, Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor));
                                }
                                toInsert = currentMatrix;
                                break;
                            case Type.STRING_TO_LONG_MAP:
                                if (currentMapStringKey != null) {
                                    currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                toInsert = currentStringLongMap;
                                break;
                            case Type.LONG_TO_LONG_MAP:
                                if (currentMapLongKey != CoreConstants.NULL_LONG) {
                                    currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                toInsert = currentLongLongMap;
                                break;
                            case Type.LONG_TO_LONG_ARRAY_MAP:
                                if (currentMapLongKey != CoreConstants.NULL_LONG) {
                                    currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                }
                                toInsert = currentLongLongArrayMap;
                                break;
                        }
                        if (toInsert != null) {
                            //insert K/V
                            internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true, initial); //enhance this with boolean array
                        }
                    }
                    //next round, reset all variables...
                    previousStart = cursor + 1;
                    currentChunkElemKey = CoreConstants.NULL_LONG;
                    currentChunkElemType = -1;
                    currentSubSize = -1;
                    currentSubIndex = 0;
                    currentMapLongKey = CoreConstants.NULL_LONG;
                    currentMapStringKey = null;
                }
            } else if (current == CoreConstants.CHUNK_SUB_SEP) { //SEPARATION BETWEEN KEY,TYPE,VALUE
                if (currentChunkElemKey == CoreConstants.NULL_LONG) {
                    currentChunkElemKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                } else if (currentChunkElemType == -1) {
                    currentChunkElemType = (byte) Base64.decodeToIntWithBounds(buffer, previousStart, cursor);
                    previousStart = cursor + 1;
                }
            } else if (current == CoreConstants.CHUNK_SUB_SUB_SEP) { //SEPARATION BETWEEN ARRAY VALUES AND MAP KEY/VALUE TUPLES
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
                            currentRelation = new HeapRelationship(this, null);
                            currentRelation.allocate((int) currentSubSize);
                            break;
                        case Type.MATRIX:
                            currentMatrix = new HeapMatrix(this, null);
                            currentMatrix.unsafe_init((int) currentSubSize);
                            break;
                        case Type.STRING_TO_LONG_MAP:
                            currentStringLongMap = new HeapStringLongMap(this);
                            currentStringLongMap.reallocate((int) currentSubSize);
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            currentLongLongMap = new HeapLongLongMap(this);
                            currentLongLongMap.reallocate((int) currentSubSize);
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            currentLongLongArrayMap = new HeapLongLongArrayMap(this);
                            currentLongLongArrayMap.reallocate((int) currentSubSize);
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
                        case Type.MATRIX:
                            currentMatrix.unsafe_set(currentSubIndex, Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor));
                            currentSubIndex++;
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
                                currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                currentMapStringKey = null;
                            }
                            break;
                        case Type.LONG_TO_LONG_MAP:
                            if (currentMapLongKey != CoreConstants.NULL_LONG) {
                                currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                currentMapLongKey = CoreConstants.NULL_LONG;
                            }
                            break;
                        case Type.LONG_TO_LONG_ARRAY_MAP:
                            if (currentMapLongKey != CoreConstants.NULL_LONG) {
                                currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                                currentMapLongKey = CoreConstants.NULL_LONG;
                            }
                            break;
                    }
                }
                previousStart = cursor + 1;
            } else if (current == CoreConstants.CHUNK_SUB_SUB_SUB_SEP) {
                switch (currentChunkElemType) {
                    case Type.STRING_TO_LONG_MAP:
                        if (currentMapStringKey == null) {
                            currentMapStringKey = Base64.decodeToStringWithBounds(buffer, previousStart, cursor);
                        } else {
                            currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            //reset key for next loop
                            currentMapStringKey = null;
                        }
                        break;
                    case Type.LONG_TO_LONG_MAP:
                        if (currentMapLongKey == CoreConstants.NULL_LONG) {
                            currentMapLongKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        } else {
                            currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            //reset key for next loop
                            currentMapLongKey = CoreConstants.NULL_LONG;
                        }
                        break;
                    case Type.LONG_TO_LONG_ARRAY_MAP:
                        if (currentMapLongKey == CoreConstants.NULL_LONG) {
                            currentMapLongKey = Base64.decodeToLongWithBounds(buffer, previousStart, cursor);
                        } else {
                            currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                            //reset key for next loop
                            currentMapLongKey = CoreConstants.NULL_LONG;
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
                    if (buffer.read(previousStart) == CoreConstants.BOOL_FALSE) {
                        toInsert = false;
                    } else if (buffer.read(previousStart) == CoreConstants.BOOL_TRUE) {
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
                        currentRelation.add(Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentRelation;
                    break;
                case Type.MATRIX:
                    if (currentMatrix != null) {
                        currentMatrix.unsafe_set(currentSubIndex, Base64.decodeToDoubleWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentMatrix;
                    break;
                case Type.STRING_TO_LONG_MAP:
                    if (currentMapStringKey != null) {
                        currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentStringLongMap;
                    break;
                case Type.LONG_TO_LONG_MAP:
                    if (currentMapLongKey != CoreConstants.NULL_LONG) {
                        currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentLongLongMap;
                    break;
                case Type.LONG_TO_LONG_ARRAY_MAP:
                    if (currentMapLongKey != CoreConstants.NULL_LONG) {
                        currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(buffer, previousStart, cursor));
                    }
                    toInsert = currentLongLongArrayMap;
                    break;
            }
            if (toInsert != null) {
                internal_set(currentChunkElemKey, currentChunkElemType, toInsert, true, initial); //enhance this with boolean array
            }
        }
    }

}
