package org.mwdb.chunk.offheap;

import org.mwdb.Constants;
import org.mwdb.KType;
import org.mwdb.chunk.*;
import org.mwdb.plugin.KResolver;
import org.mwdb.utility.Base64;
import org.mwdb.utility.PrimitiveHelper;

/**
 * @ignore ts
 * Memory layout: all structures are memory blocks of either primitive values (as longs)
 * or pointers to memory blocks
 */
// TODO check synchronization
public class OffHeapStateChunk implements KStateChunk, KChunkListener {
    // keys
    private static final int INDEX_WORLD = Constants.OFFHEAP_CHUNK_INDEX_WORLD;
    private static final int INDEX_TIME = Constants.OFFHEAP_CHUNK_INDEX_TIME;
    private static final int INDEX_ID = Constants.OFFHEAP_CHUNK_INDEX_ID;
    private static final int INDEX_TYPE = Constants.OFFHEAP_CHUNK_INDEX_TYPE;
    private static final int INDEX_FLAGS = Constants.OFFHEAP_CHUNK_INDEX_FLAGS;
    private static final int INDEX_MARKS = Constants.OFFHEAP_CHUNK_INDEX_MARKS;


    // long arrays
    private static final int INDEX_ELEMENT_K = 6;
    private static final int INDEX_ELEMENT_V = 7;
    private static final int INDEX_ELEMENT_NEXT = 8;
    private static final int INDEX_ELEMENT_HASH = 9;
    private static final int INDEX_ELEMENT_TYPE = 10;

    // long values
    private static final int INDEX_COUNTER = 11;
    private static final int INDEX_IN_LOAD_MODE = 12;
    private static final int INDEX_ELEMENT_DATA_SIZE = 13;
    private static final int INDEX_ELEMENT_THRESHOLD = 14;
    private static final int INDEX_ELEMENT_COUNT = 15;

    //pointer values
    private final KChunkListener _listener;
    private long elementK_ptr;
    private long elementV_ptr;
    private long elementNext_ptr;
    private long elementHash_ptr;
    private long elementType_ptr;
    private final long root_array_ptr;

    public OffHeapStateChunk(KChunkListener listener, String initialPayload, KChunk origin, long previousAddr) {
        _listener = listener;
        if (previousAddr == Constants.OFFHEAP_NULL_PTR) {
            root_array_ptr = OffHeapLongArray.allocate(16);
            OffHeapLongArray.set(root_array_ptr, INDEX_IN_LOAD_MODE, 0);
            OffHeapLongArray.set(root_array_ptr, INDEX_FLAGS, 0);
            OffHeapLongArray.set(root_array_ptr, INDEX_COUNTER, 0);
            long initialCapacity = Constants.MAP_INITIAL_CAPACITY;
            OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_DATA_SIZE, initialCapacity);
            OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_COUNT, 0);
            long threshold = (long) (initialCapacity * Constants.MAP_LOAD_FACTOR);
            OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_THRESHOLD, threshold);

            if (initialPayload != null) {
                load(initialPayload);

            } else if (origin != null) {
                OffHeapStateChunk castedOrigin = (OffHeapStateChunk) origin;
                cloneFrom(castedOrigin);
            } else {
                /** init long[] variables */
                elementK_ptr = OffHeapLongArray.allocate(initialCapacity);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_K, elementK_ptr);
                elementV_ptr = OffHeapLongArray.allocate(initialCapacity);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_V, elementV_ptr);
                elementNext_ptr = OffHeapLongArray.allocate(initialCapacity);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_NEXT, elementNext_ptr);
                elementHash_ptr = OffHeapLongArray.allocate(initialCapacity);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_HASH, elementHash_ptr);
                elementType_ptr = OffHeapLongArray.allocate(initialCapacity);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_TYPE, elementType_ptr);
            }

        } else {
            root_array_ptr = previousAddr;
            elementK_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_K);
            elementV_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_V);
            elementNext_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_NEXT);
            elementHash_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_HASH);
            elementType_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_TYPE);
        }

    }

    private void cloneFrom(OffHeapStateChunk origin) {
        long elementDataSize = OffHeapLongArray.get(origin.root_array_ptr, INDEX_ELEMENT_DATA_SIZE);
        long elementCount = OffHeapLongArray.get(origin.root_array_ptr, INDEX_ELEMENT_COUNT);

        long clonedElementK_ptr = OffHeapLongArray.allocate(elementDataSize);
        OffHeapLongArray.copy(origin.elementK_ptr, clonedElementK_ptr, elementDataSize);
        long clonedElementV_ptr = OffHeapLongArray.allocate(elementDataSize);
        OffHeapLongArray.copy(origin.elementV_ptr, clonedElementV_ptr, elementDataSize);
        long clonedElementNext_ptr = OffHeapLongArray.allocate(elementDataSize);
        OffHeapLongArray.copy(origin.elementNext_ptr, clonedElementNext_ptr, elementDataSize);
        long clonedElementHash_ptr = OffHeapLongArray.allocate(elementDataSize);
        OffHeapLongArray.copy(origin.elementHash_ptr, clonedElementHash_ptr, elementDataSize);
        long clonedElementType_ptr = OffHeapLongArray.allocate(elementDataSize);
        OffHeapLongArray.copy(origin.elementType_ptr, clonedElementType_ptr, elementDataSize);

        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_DATA_SIZE, elementDataSize);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_COUNT, elementCount);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_K, clonedElementK_ptr);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_V, clonedElementV_ptr);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_NEXT, clonedElementNext_ptr);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_HASH, clonedElementHash_ptr);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_TYPE, clonedElementType_ptr);

        elementK_ptr = clonedElementK_ptr;
        elementV_ptr = clonedElementV_ptr;
        elementNext_ptr = clonedElementNext_ptr;
        elementHash_ptr = clonedElementHash_ptr;
        elementType_ptr = clonedElementType_ptr;

    }

    @Override
    public void each(KStateChunkCallBack callBack, KResolver resolver) {
        for (int i = 0; i < OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_COUNT); i++) {
            if (OffHeapLongArray.get(elementV_ptr, i) != Constants.OFFHEAP_NULL_PTR) {
                callBack.on(resolver.longKeyToString(OffHeapLongArray.get(elementK_ptr, i)),
                        (int) OffHeapLongArray.get(elementType_ptr, i),
                        OffHeapLongArray.get(elementV_ptr, i));
            }
        }
    }

    @Override
    public long world() {
        return OffHeapLongArray.get(root_array_ptr, INDEX_WORLD);
    }

    @Override
    public long time() {
        return OffHeapLongArray.get(root_array_ptr, INDEX_TIME);
    }

    @Override
    public long id() {
        return OffHeapLongArray.get(root_array_ptr, INDEX_ID);
    }

    @Override
    public String save() {
        final StringBuilder buffer = new StringBuilder();
        long elementCount = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_COUNT);
        Base64.encodeLongToBuffer(elementCount, buffer);
        for (int i = 0; i < elementCount; i++) {
            if (OffHeapLongArray.get(elementV_ptr, i) != Constants.OFFHEAP_NULL_PTR) { //there is a real value
                long loopKey = OffHeapLongArray.get(elementK_ptr, i);
                Object loopValue = internal_getElementV(i);
                if (loopValue != null) {
                    buffer.append(Constants.CHUNK_SEP);
                    Base64.encodeLongToBuffer(loopKey, buffer);
                    buffer.append(Constants.CHUNK_SUB_SEP);
                    /** Encode to type of elem, for unSerialization */
                    byte elementType = (byte) OffHeapLongArray.get(elementType_ptr, i); // can be safely casted
                    Base64.encodeIntToBuffer(elementType, buffer);
                    buffer.append(Constants.CHUNK_SUB_SEP);
                    switch (elementType) {
                        /** Primitive Types */
                        case KType.STRING:
                            Base64.encodeStringToBuffer((String) loopValue, buffer);
                            break;
                        case KType.BOOL:
                            if ((boolean) loopValue) {
                                buffer.append("1");
                            } else {
                                buffer.append("0");
                            }
                            break;
                        case KType.LONG:
                            Base64.encodeLongToBuffer((long) loopValue, buffer);
                            break;
                        case KType.DOUBLE:
                            Base64.encodeDoubleToBuffer((double) loopValue, buffer);
                            break;
                        case KType.INT:
                            Base64.encodeIntToBuffer((int) loopValue, buffer);
                            break;
                        /** Arrays */
                        case KType.DOUBLE_ARRAY:
                            double[] castedDoubleArr = (double[]) loopValue;
                            Base64.encodeIntToBuffer(castedDoubleArr.length, buffer);
                            for (int j = 0; j < castedDoubleArr.length; j++) {
                                buffer.append(Constants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeDoubleToBuffer(castedDoubleArr[j], buffer);
                            }
                            break;
                        case KType.LONG_ARRAY:
                            long[] castedLongArr = (long[]) loopValue;
                            Base64.encodeIntToBuffer(castedLongArr.length, buffer);
                            for (int j = 0; j < castedLongArr.length; j++) {
                                buffer.append(Constants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeLongToBuffer(castedLongArr[j], buffer);
                            }
                            break;
                        case KType.INT_ARRAY:
                            int[] castedIntArr = (int[]) loopValue;
                            Base64.encodeIntToBuffer(castedIntArr.length, buffer);
                            for (int j = 0; j < castedIntArr.length; j++) {
                                buffer.append(Constants.CHUNK_SUB_SUB_SEP);
                                Base64.encodeIntToBuffer(castedIntArr[j], buffer);
                            }
                            break;
                        /** Maps */
                        case KType.STRING_LONG_MAP:
                            KStringLongMap castedStringLongMap = (KStringLongMap) loopValue;
                            Base64.encodeLongToBuffer(castedStringLongMap.size(), buffer);
                            castedStringLongMap.each(new KStringLongMapCallBack() {
                                @Override
                                public void on(final String key, final long value) {
                                    buffer.append(Constants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeStringToBuffer(key, buffer);
                                    buffer.append(Constants.CHUNK_SUB_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        case KType.LONG_LONG_MAP:
                            KLongLongMap castedLongLongMap = (KLongLongMap) loopValue;
                            Base64.encodeLongToBuffer(castedLongLongMap.size(), buffer);
                            castedLongLongMap.each(new KLongLongMapCallBack() {
                                @Override
                                public void on(final long key, final long value) {
                                    buffer.append(Constants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(key, buffer);
                                    buffer.append(Constants.CHUNK_SUB_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(value, buffer);
                                }
                            });
                            break;
                        case KType.LONG_LONG_ARRAY_MAP:
                            KLongLongArrayMap castedLongLongArrayMap = (KLongLongArrayMap) loopValue;
                            Base64.encodeLongToBuffer(castedLongLongArrayMap.size(), buffer);
                            castedLongLongArrayMap.each(new KLongLongArrayMapCallBack() {
                                @Override
                                public void on(final long key, final long value) {
                                    buffer.append(Constants.CHUNK_SUB_SUB_SEP);
                                    Base64.encodeLongToBuffer(key, buffer);
                                    buffer.append(Constants.CHUNK_SUB_SUB_SUB_SEP);
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
        return buffer.toString();
    }

    private void load(String payload) {
        if (payload == null || payload.length() == 0) {
            return;
        }
        OffHeapLongArray.set(root_array_ptr, INDEX_IN_LOAD_MODE, 1);

        //future map elements
        long newElementK_ptr = Constants.OFFHEAP_NULL_PTR;
        long newElementV_ptr = Constants.OFFHEAP_NULL_PTR;
        long newElementType_ptr = Constants.OFFHEAP_NULL_PTR;
        long newElementNext_ptr = Constants.OFFHEAP_NULL_PTR;
        long newElementHash_ptr = Constants.OFFHEAP_NULL_PTR;
        long newNumberElement = 0;
        long newStateCapacity = 0;
        //reset size
        long currentElemIndex = 0;

        int cursor = 0;
        long payloadSize = payload.length();

        int previousStart = -1;
        long currentChunkElemKey = Constants.NULL_LONG;
        int currentChunkElemType = -1;

        //init detections
        boolean isFirstElem = true;

        //array sub creation variable
        double[] currentDoubleArr = null;
        long[] currentLongArr = null;
        int[] currentIntArr = null;

        //map sub creation variables
        KStringLongMap currentStringLongMap = null;
        KLongLongMap currentLongLongMap = null;
        KLongLongArrayMap currentLongLongArrayMap = null;

        //array variables
        int currentSubSize = -1;
        int currentSubIndex = 0;

        //map key variables
        long currentMapLongKey = Constants.NULL_LONG;
        String currentMapStringKey = null;

        while (cursor < payloadSize) {
            if (payload.charAt(cursor) == Constants.CHUNK_SEP) {
                if (isFirstElem) {
                    //initial the map
                    isFirstElem = false;
                    long stateChunkSize = Base64.decodeToLongWithBounds(payload, 0, cursor);
                    newNumberElement = stateChunkSize;
                    long newStateChunkSize = (stateChunkSize == 0 ? 1 : stateChunkSize << 1);
                    //init map element
                    newElementK_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                    newElementV_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                    newElementType_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                    newStateCapacity = newStateChunkSize;
                    //init hash and chaining
                    newElementNext_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                    newElementHash_ptr = OffHeapLongArray.allocate(newStateChunkSize);
                    previousStart = cursor + 1;
                } else {
                    //beginning of the Chunk elem
                    //check if something is still in buffer
                    if (currentChunkElemType != -1) {
                        Object toInsert = null;
                        switch (currentChunkElemType) {
                            /** Primitive Object */
                            case KType.BOOL:
                                if (payload.charAt(previousStart) == '0') {
                                    toInsert = false;
                                } else if (payload.charAt(previousStart) == '1') {
                                    toInsert = true;
                                }
                                break;
                            case KType.STRING:
                                toInsert = Base64.decodeToStringWithBounds(payload, previousStart, cursor);
                                break;

                            case KType.DOUBLE:
                                toInsert = Base64.decodeToDoubleWithBounds(payload, previousStart, cursor);
                                break;

                            case KType.LONG:
                                toInsert = Base64.decodeToLongWithBounds(payload, previousStart, cursor);
                                break;

                            case KType.INT:
                                toInsert = Base64.decodeToIntWithBounds(payload, previousStart, cursor);
                                break;
                            /** Arrays */
                            case KType.DOUBLE_ARRAY:
                                currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(payload, previousStart, cursor);
                                toInsert = currentDoubleArr;
                                break;

                            case KType.LONG_ARRAY:
                                currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(payload, previousStart, cursor);
                                toInsert = currentLongArr;
                                break;

                            case KType.INT_ARRAY:
                                currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(payload, previousStart, cursor);
                                toInsert = currentIntArr;
                                break;
                            /** Maps */
                            case KType.STRING_LONG_MAP:
                                if (currentMapStringKey != null) {
                                    currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                                }
                                toInsert = currentStringLongMap;
                                break;
                            case KType.LONG_LONG_MAP:
                                if (currentMapLongKey != Constants.NULL_LONG) {
                                    currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                                }
                                toInsert = currentLongLongMap;
                                break;
                            case KType.LONG_LONG_ARRAY_MAP:
                                if (currentMapLongKey != Constants.NULL_LONG) {
                                    currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                                }
                                toInsert = currentLongLongArrayMap;
                                break;
                        }
                        if (toInsert != null) {
                            //insert K/V
                            long newIndex = currentElemIndex;
                            OffHeapLongArray.set(newElementK_ptr, newIndex, currentChunkElemKey);
                            internal_setElementV(newElementV_ptr, newIndex, (byte) currentChunkElemType, toInsert);
                            OffHeapLongArray.set(newElementType_ptr, newIndex, currentChunkElemType);

                            long hashIndex = PrimitiveHelper.longHash(currentChunkElemKey, newStateCapacity);
                            long currentHashedIndex = OffHeapLongArray.get(newElementHash_ptr, hashIndex);
                            if (currentHashedIndex != -1) {
                                OffHeapLongArray.set(newElementNext_ptr, newIndex, currentHashedIndex);
                            }
                            OffHeapLongArray.set(newElementHash_ptr, hashIndex, newIndex);
                            currentElemIndex++;
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
            } else if (payload.charAt(cursor) == Constants.CHUNK_SUB_SEP) { //SEPARATION BETWEEN KEY,TYPE,VALUE
                if (currentChunkElemKey == Constants.NULL_LONG) {
                    currentChunkElemKey = Base64.decodeToLongWithBounds(payload, previousStart, cursor);
                    previousStart = cursor + 1;
                } else if (currentChunkElemType == -1) {
                    currentChunkElemType = Base64.decodeToIntWithBounds(payload, previousStart, cursor);
                    previousStart = cursor + 1;
                }
            } else if (payload.charAt(cursor) == Constants.CHUNK_SUB_SUB_SEP) { //SEPARATION BETWEEN ARRAY VALUES AND MAP KEY/VALUE TUPLES
                if (currentSubSize == -1) {
                    currentSubSize = Base64.decodeToIntWithBounds(payload, previousStart, cursor);
                    //init array or maps
                    switch (currentChunkElemType) {
                        /** Arrays */
                        case KType.DOUBLE_ARRAY:
                            currentDoubleArr = new double[currentSubSize];
                            break;
                        case KType.LONG_ARRAY:
                            currentLongArr = new long[currentSubSize];
                            break;
                        case KType.INT_ARRAY:
                            currentIntArr = new int[currentSubSize];
                            break;
                        /** Maps */
                        case KType.STRING_LONG_MAP:
                            currentStringLongMap = new ArrayStringLongMap(this, (int) currentSubSize, Constants.OFFHEAP_NULL_PTR);
                            break;
                        case KType.LONG_LONG_MAP:
                            currentLongLongMap = new ArrayLongLongMap(this, (int) currentSubSize, Constants.OFFHEAP_NULL_PTR);
                            break;
                        case KType.LONG_LONG_ARRAY_MAP:
                            currentLongLongArrayMap = new ArrayLongLongArrayMap(this, (int) currentSubSize, Constants.OFFHEAP_NULL_PTR);
                            break;
                    }
                } else {
                    switch (currentChunkElemType) {
                        /** Arrays */
                        case KType.DOUBLE_ARRAY:
                            currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(payload, previousStart, cursor);
                            currentSubIndex++;
                            break;
                        case KType.LONG_ARRAY:
                            currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(payload, previousStart, cursor);
                            currentSubIndex++;
                            break;
                        case KType.INT_ARRAY:
                            currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(payload, previousStart, cursor);
                            currentSubIndex++;
                            break;
                        /** Maps */
                        case KType.STRING_LONG_MAP:
                            if (currentMapStringKey != null) {
                                currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                                currentMapStringKey = null;
                            }
                            break;
                        case KType.LONG_LONG_MAP:
                            if (currentMapLongKey != Constants.NULL_LONG) {
                                currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                                currentMapLongKey = Constants.NULL_LONG;
                            }
                            break;
                        case KType.LONG_LONG_ARRAY_MAP:
                            if (currentMapLongKey != Constants.NULL_LONG) {
                                currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                                currentMapLongKey = Constants.NULL_LONG;
                            }
                            break;

                    }
                }
                previousStart = cursor + 1;
            } else if (payload.charAt(cursor) == Constants.CHUNK_SUB_SUB_SUB_SEP) {
                switch (currentChunkElemType) {
                    case KType.STRING_LONG_MAP:
                        if (currentMapStringKey == null) {
                            currentMapStringKey = Base64.decodeToStringWithBounds(payload, previousStart, cursor);
                        } else {
                            currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                            //reset key for next loop
                            currentMapStringKey = null;
                        }
                        break;
                    case KType.LONG_LONG_MAP:
                        if (currentMapLongKey == Constants.NULL_LONG) {
                            currentMapLongKey = Base64.decodeToLongWithBounds(payload, previousStart, cursor);
                        } else {
                            currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                            //reset key for next loop
                            currentMapLongKey = Constants.NULL_LONG;
                        }
                        break;
                    case KType.LONG_LONG_ARRAY_MAP:
                        if (currentMapLongKey == Constants.NULL_LONG) {
                            currentMapLongKey = Base64.decodeToLongWithBounds(payload, previousStart, cursor);
                        } else {
                            currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
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
                /** Primitive Object */
                case KType.BOOL:
                    if (payload.charAt(previousStart) == '0') {
                        toInsert = false;
                    } else if (payload.charAt(previousStart) == '1') {
                        toInsert = true;
                    }
                    break;
                case KType.STRING:
                    toInsert = Base64.decodeToStringWithBounds(payload, previousStart, cursor);
                    break;
                case KType.DOUBLE:
                    toInsert = Base64.decodeToDoubleWithBounds(payload, previousStart, cursor);
                    break;
                case KType.LONG:
                    toInsert = Base64.decodeToLongWithBounds(payload, previousStart, cursor);
                    break;
                case KType.INT:
                    toInsert = Base64.decodeToIntWithBounds(payload, previousStart, cursor);
                    break;
                /** Arrays */
                case KType.DOUBLE_ARRAY:
                    currentDoubleArr[currentSubIndex] = Base64.decodeToDoubleWithBounds(payload, previousStart, cursor);
                    toInsert = currentDoubleArr;
                    break;
                case KType.LONG_ARRAY:
                    currentLongArr[currentSubIndex] = Base64.decodeToLongWithBounds(payload, previousStart, cursor);
                    toInsert = currentLongArr;
                    break;
                case KType.INT_ARRAY:
                    currentIntArr[currentSubIndex] = Base64.decodeToIntWithBounds(payload, previousStart, cursor);
                    toInsert = currentIntArr;
                    break;
                /** Maps */
                case KType.STRING_LONG_MAP:
                    if (currentMapStringKey != null) {
                        currentStringLongMap.put(currentMapStringKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                    }
                    toInsert = currentStringLongMap;
                    break;
                case KType.LONG_LONG_MAP:
                    if (currentMapLongKey != Constants.NULL_LONG) {
                        currentLongLongMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                    }
                    toInsert = currentLongLongMap;
                    break;
                case KType.LONG_LONG_ARRAY_MAP:
                    if (currentMapLongKey != Constants.NULL_LONG) {
                        currentLongLongArrayMap.put(currentMapLongKey, Base64.decodeToLongWithBounds(payload, previousStart, cursor));
                    }
                    toInsert = currentLongLongArrayMap;
                    break;


            }
            if (toInsert != null) {
                //insert K/V
                OffHeapLongArray.set(newElementK_ptr, currentElemIndex, currentChunkElemKey);
                internal_setElementV(newElementV_ptr, currentElemIndex, (byte) currentChunkElemType, toInsert);
                OffHeapLongArray.set(newElementType_ptr, currentElemIndex, currentChunkElemType);

                long hashIndex = PrimitiveHelper.longHash(currentChunkElemKey, newStateCapacity);
                long currentHashedIndex = OffHeapLongArray.get(newElementHash_ptr, hashIndex);
                if (currentHashedIndex != -1) {
                    OffHeapLongArray.set(newElementNext_ptr, currentElemIndex, currentHashedIndex);
                }
                OffHeapLongArray.set(newElementHash_ptr, hashIndex, currentElemIndex);
            }
        }

        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_K, newElementK_ptr);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_V, newElementV_ptr);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_NEXT, newElementNext_ptr);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_HASH, newElementHash_ptr);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_TYPE, newElementType_ptr);

        elementK_ptr = newElementK_ptr;
        elementV_ptr = newElementV_ptr;
        elementNext_ptr = newElementNext_ptr;
        elementHash_ptr = newElementHash_ptr;
        elementType_ptr = newElementType_ptr;

        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_DATA_SIZE, newStateCapacity);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_COUNT, newNumberElement);
        long threshold = (long) (newStateCapacity * Constants.MAP_LOAD_FACTOR);
        OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_THRESHOLD, threshold);

        OffHeapLongArray.set(root_array_ptr, INDEX_IN_LOAD_MODE, 0);
    }

    @Override
    public long marks() {
        return OffHeapLongArray.get(root_array_ptr, INDEX_COUNTER);
    }

    public void free() {
        long elementDataSize = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_DATA_SIZE);
        for (int i = 0; i < elementDataSize; i++) {
            long elemV_ptr = OffHeapLongArray.get(elementV_ptr, i);
            if (elemV_ptr != Constants.OFFHEAP_NULL_PTR) {
                byte elemType = (byte) OffHeapLongArray.get(elementType_ptr, i);
                switch (elemType) {
                    /** Primitive Object */
                    case KType.BOOL:
                        break;
                    case KType.STRING:
                        OffHeapStringArray.free(elemV_ptr, 0);
                        break;
                    case KType.DOUBLE:
                        break;
                    case KType.LONG:
                        break;
                    case KType.INT:
                        break;
                    /** Arrays */
                    case KType.DOUBLE_ARRAY:
                        OffHeapDoubleArray.free(elemV_ptr);
                        break;
                    case KType.LONG_ARRAY:
                        OffHeapLongArray.free(elementV_ptr);
                        break;
                    case KType.INT_ARRAY:
                        OffHeapLongArray.free(elementV_ptr);
                        break;
                }
            }
        }

        OffHeapLongArray.free(elementV_ptr);
        OffHeapLongArray.free(elementK_ptr);
        OffHeapLongArray.free(elementNext_ptr);
        OffHeapLongArray.free(elementHash_ptr);
        OffHeapLongArray.free(elementType_ptr);

        OffHeapLongArray.free(root_array_ptr);
    }

    @Override
    public long flags() {
        return OffHeapLongArray.get(root_array_ptr, INDEX_FLAGS);
    }

    @Override
    public byte chunkType() {
        return Constants.STATE_CHUNK;
    }

    @Override
    public void declareDirty(KChunk chunk) {
        if (OffHeapLongArray.get(root_array_ptr, INDEX_IN_LOAD_MODE) == 0) {
            internal_set_dirty();
        }
    }

    @Override
    public void set(long index, byte elemType, Object elem) {
        internal_set(index, elemType, elem, true);

    }

    // TODO synchronize method
    private synchronized void internal_set(final long p_elementIndex, final byte p_elemType, final Object p_unsafe_elem, boolean replaceIfPresent) {
        Object param_elem = null;
        //check the param type
        try {
            switch (p_elemType) {
                /** Primitives */
                case KType.BOOL:
                    param_elem = (boolean) p_unsafe_elem;
                    break;
                case KType.DOUBLE:
                    param_elem = (double) p_unsafe_elem;
                    break;
                case KType.LONG:
                    param_elem = (long) p_unsafe_elem;
                    break;
                case KType.INT:
                    param_elem = (int) p_unsafe_elem;
                    break;
                case KType.STRING:
                    param_elem = (String) p_unsafe_elem;
                    break;
                /** Arrays */
                case KType.DOUBLE_ARRAY:
                    param_elem = (double[]) p_unsafe_elem;
                    break;
                case KType.LONG_ARRAY:
                    param_elem = (long[]) p_unsafe_elem;
                    break;
                case KType.INT_ARRAY:
                    param_elem = (int[]) p_unsafe_elem;
                    break;
                default:
                    throw new RuntimeException("mwDB usage error, set method called with an unknown type " + p_elemType);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("mwDB usage error, set method called with type " + p_elemType + " while param object is " + param_elem);
        }
        long entry = -1;
        long hashIndex = -1;
        long elementDataSize = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_DATA_SIZE);
        if (elementDataSize > 0) {
            hashIndex = PrimitiveHelper.longHash(p_elementIndex, elementDataSize);
            long m = OffHeapLongArray.get(elementHash_ptr, hashIndex);
            while (m != -1) {
                if (p_elementIndex == OffHeapLongArray.get(elementK_ptr, m)) {
                    entry = m;
                    break;
                }
                m = OffHeapLongArray.get(elementNext_ptr, m);
            }
        }
        if (entry == -1) {
            long elementCount = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_COUNT);
            long threshold = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_THRESHOLD);
            if (elementCount + 1 > threshold) {
                long newLength = (elementDataSize == 0 ? 1 : elementDataSize << 1);
                long newElementK_ptr = OffHeapLongArray.allocate(newLength);
                long newElementV_ptr = OffHeapLongArray.allocate(newLength);
                long newElementType_ptr = OffHeapLongArray.allocate(newLength);

                OffHeapLongArray.copy(OffHeapLongArray.get(root_array_ptr, elementK_ptr), newElementK_ptr, elementDataSize);
                OffHeapLongArray.copy(OffHeapLongArray.get(root_array_ptr, elementV_ptr), newElementV_ptr, elementDataSize);
                OffHeapLongArray.copy(OffHeapLongArray.get(root_array_ptr, elementType_ptr), newElementType_ptr, elementDataSize);
                long newElementNext_ptr = OffHeapLongArray.allocate(newLength);
                long newElementHash_ptr = OffHeapLongArray.allocate(newLength);

                //rehashEveryThing
                for (int i = 0; i < elementDataSize; i++) {
                    if (OffHeapLongArray.get(root_array_ptr, elementV_ptr) != Constants.OFFHEAP_NULL_PTR) { //there is a real value
                        long keyHash = PrimitiveHelper.longHash(OffHeapLongArray.get(elementK_ptr, i), newLength);
                        long currentHashedIndex = OffHeapLongArray.get(newElementHash_ptr, keyHash);
                        if (currentHashedIndex != -1) {
                            OffHeapLongArray.set(newElementNext_ptr, i, currentHashedIndex);
                        }
                        OffHeapLongArray.set(newElementHash_ptr, keyHash, i);
                    }
                }

                OffHeapLongArray.free(OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_K));
                OffHeapLongArray.free(OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_V));
                OffHeapLongArray.free(OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_NEXT));
                OffHeapLongArray.free(OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_HASH));
                OffHeapLongArray.free(OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_TYPE));

                //setPrimitiveType value for all
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_DATA_SIZE, newLength);
                // elementCount stays the same
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_THRESHOLD, (long) (newLength * Constants.MAP_LOAD_FACTOR));
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_K, newElementK_ptr);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_V, newElementV_ptr);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_NEXT, newElementNext_ptr);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_HASH, newElementHash_ptr);
                OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_TYPE, newElementType_ptr);

                elementK_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_K);
                elementV_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_V);
                elementNext_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_NEXT);
                elementHash_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_HASH);
                elementType_ptr = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_TYPE);

                hashIndex = PrimitiveHelper.longHash(p_elementIndex, OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_DATA_SIZE));
            }
            long newIndex = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_COUNT);
            OffHeapLongArray.set(root_array_ptr, INDEX_ELEMENT_COUNT, OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_COUNT) + 1);
            OffHeapLongArray.set(elementK_ptr, newIndex, p_elementIndex);
            internal_setElementV(elementV_ptr, newIndex, p_elemType, param_elem);
            OffHeapLongArray.set(elementType_ptr, newIndex, p_elemType);

            long currentHashedIndex = OffHeapLongArray.get(elementHash_ptr, hashIndex);
            if (currentHashedIndex != -1) {
                OffHeapLongArray.set(elementNext_ptr, newIndex, currentHashedIndex);
            }
            //now the object is reachable to other thread everything should be ready
            OffHeapLongArray.set(elementHash_ptr, hashIndex, newIndex);
        } else {
            if (replaceIfPresent) {
                internal_setElementV(elementV_ptr, entry, p_elemType, param_elem); /*setValue*/
                OffHeapLongArray.set(elementType_ptr, entry, p_elemType);
            }
        }
        internal_set_dirty();
    }

    // TODO before we set something, we first have to check what was there before and free the memory in case
    private void internal_setElementV(long addr, long index, byte elemType, Object elem) {
        // no additional check needed, we are sure it is one of these types
        switch (elemType) {
            /** Primitives */
            case KType.BOOL:
                OffHeapLongArray.set(addr, index, ((boolean) elem) ? 1 : 0);
                break;
            case KType.DOUBLE:
                OffHeapDoubleArray.set(addr, index, ((double) elem));
                break;
            case KType.LONG:
                OffHeapLongArray.set(addr, index, ((long) elem));
                break;
            case KType.INT:
                OffHeapLongArray.set(addr, index, ((int) elem));
                break;
            case KType.STRING:
                String stringToInsert = (String) elem;
                long stringToInsert_ptr = OffHeapStringArray.allocate(stringToInsert.length());
                OffHeapStringArray.set(stringToInsert_ptr, 0, stringToInsert);
                OffHeapLongArray.set(addr, index, stringToInsert_ptr);
                break;
            /** Arrays */
            case KType.DOUBLE_ARRAY:
                double[] doubleArrayToInsert = (double[]) elem;
                long doubleArrayToInsert_ptr = OffHeapDoubleArray.allocate(1 + doubleArrayToInsert.length); // length + content of the array
                OffHeapLongArray.set(doubleArrayToInsert_ptr, 0, doubleArrayToInsert.length);// set length
                for (int i = 0; i < doubleArrayToInsert.length; i++) {
                    OffHeapDoubleArray.set(doubleArrayToInsert_ptr, 1 + i, doubleArrayToInsert[i]);
                }
                OffHeapLongArray.set(addr, index, doubleArrayToInsert_ptr);
                break;
            case KType.LONG_ARRAY:
                long[] longArrayToInsert = (long[]) elem;
                long longArrayToInsert_ptr = OffHeapLongArray.allocate(1 + longArrayToInsert.length); // length + content of the array
                OffHeapLongArray.set(longArrayToInsert_ptr, 0, longArrayToInsert.length);// set length
                for (int i = 0; i < longArrayToInsert.length; i++) {
                    OffHeapLongArray.set(longArrayToInsert_ptr, 1 + i, longArrayToInsert[i]);
                }
                OffHeapLongArray.set(addr, index, longArrayToInsert_ptr);
                break;
            case KType.INT_ARRAY:
                int[] intArrayToInsert = (int[]) elem;
                long intArrayToInsert_ptr = OffHeapLongArray.allocate(1 + intArrayToInsert.length); // length + content of the array
                OffHeapLongArray.set(intArrayToInsert_ptr, 0, intArrayToInsert.length);// set length
                for (int i = 0; i < intArrayToInsert.length; i++) {
                    OffHeapLongArray.set(intArrayToInsert_ptr, 1 + i, intArrayToInsert[i]);
                }
                OffHeapLongArray.set(addr, index, intArrayToInsert_ptr);
                break;
            /** Maps */
            case KType.STRING_LONG_MAP:
                long stringLongMap_ptr = ((ArrayStringLongMap) elem).rootAddress();
                OffHeapLongArray.set(addr, index, stringLongMap_ptr);
                break;
            case KType.LONG_LONG_MAP:
                long longLongMap_ptr = ((ArrayLongLongMap) elem).rootAddress();
                OffHeapLongArray.set(addr, index, longLongMap_ptr);
                break;
            case KType.LONG_LONG_ARRAY_MAP:
                long longLongArrayMap_ptr = ((ArrayLongLongArrayMap) elem).rootAddress();
                OffHeapLongArray.set(addr, index, longLongArrayMap_ptr);
                break;
            default:
                throw new RuntimeException("Should never happen...");
        }
    }

    private void internal_set_dirty() {
        if (this._listener != null) {
            if ((OffHeapLongArray.get(root_array_ptr, INDEX_FLAGS) & Constants.DIRTY_BIT) != Constants.DIRTY_BIT) {
                this._listener.declareDirty(this);
            }
        }
    }


    @Override
    public Object get(long index) {
        long elementDataSize = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_DATA_SIZE);
        if (elementDataSize == 0) {
            return null;
        }
        long hashIndex = PrimitiveHelper.longHash(index, elementDataSize);
        long m = OffHeapLongArray.get(elementHash_ptr, hashIndex);
        while (m >= 0) {
            if (index == OffHeapLongArray.get(elementK_ptr, m) /* getKey */) {
                return internal_getElementV(index); /* getValue */
            } else {
                m = OffHeapLongArray.get(elementNext_ptr, m);
            }
        }
        return null;
    }

    public Object internal_getElementV(long index) {
        byte elemType = (byte) OffHeapLongArray.get(elementType_ptr, index); // can be safely casted
        switch (elemType) {
            /** Primitives */
            case KType.BOOL:
                return OffHeapLongArray.get(elementType_ptr, index) == 1 ? true : false;
            case KType.DOUBLE:
                return OffHeapDoubleArray.get(elementV_ptr, index); // no indirection, value is directly inside
            case KType.LONG:
                return OffHeapLongArray.get(elementV_ptr, index);  // no indirection, value is directly inside
            case KType.INT:
                return (int) OffHeapLongArray.get(elementV_ptr, index); // no indirection, value is directly inside
            case KType.STRING:
                long elemStringPtr = OffHeapLongArray.get(elementV_ptr, index);
                if (elemStringPtr == Constants.OFFHEAP_NULL_PTR) {
                    return null;
                }
                return OffHeapStringArray.get(elemStringPtr, 0);
            /** Arrays */
            case KType.DOUBLE_ARRAY:
                long elemDoublePtr = OffHeapLongArray.get(elementV_ptr, index);
                if (elemDoublePtr == Constants.OFFHEAP_NULL_PTR) {
                    return null;
                }
                int doubleArrayLength = (int) OffHeapLongArray.get(elemDoublePtr, 0); // can be safely casted
                double[] doubleArray = new double[doubleArrayLength];
                for (int i = 0; i < doubleArrayLength; i++) {
                    doubleArray[i] = OffHeapDoubleArray.get(elemDoublePtr, 1 + i);
                }
                return doubleArray;
            case KType.LONG_ARRAY:
                long elemLongPtr = OffHeapLongArray.get(elementV_ptr, index);
                if (elemLongPtr == Constants.OFFHEAP_NULL_PTR) {
                    return null;
                }
                int longArrayLength = (int) OffHeapLongArray.get(elemLongPtr, 0); // can be safely casted
                long[] longArray = new long[longArrayLength];
                for (int i = 0; i < longArrayLength; i++) {
                    longArray[i] = OffHeapLongArray.get(elemLongPtr, 1 + i);
                }
                return longArray;
            case KType.INT_ARRAY:
                long elemIntPtr = OffHeapLongArray.get(elementV_ptr, index);
                if (elemIntPtr == Constants.OFFHEAP_NULL_PTR) {
                    return null;
                }
                int intArrayLength = (int) OffHeapLongArray.get(elemIntPtr, 0); // can be safely casted
                int[] intArray = new int[intArrayLength];
                for (int i = 0; i < intArrayLength; i++) {
                    intArray[i] = (int) OffHeapLongArray.get(elemIntPtr, 1 + i);
                }
                return intArray;
            /** Maps */
            case KType.STRING_LONG_MAP:
                long elemStringLongMapPtr = OffHeapLongArray.get(elementV_ptr, index);
                return new ArrayStringLongMap(_listener, Constants.MAP_INITIAL_CAPACITY, elemStringLongMapPtr);
            case KType.LONG_LONG_MAP:
                long elemLongLongMapPtr = OffHeapLongArray.get(elementV_ptr, index);
                return new ArrayLongLongMap(_listener, Constants.MAP_INITIAL_CAPACITY, elemLongLongMapPtr);
            case KType.LONG_LONG_ARRAY_MAP:
                long elemLongLongArrayMapPtr = OffHeapLongArray.get(elementV_ptr, index);
                return new ArrayLongLongArrayMap(_listener, Constants.MAP_INITIAL_CAPACITY, elemLongLongArrayMapPtr);
            default:
                throw new RuntimeException("Should never happen");
        }
    }

    @Override
    public Object getOrCreate(long index, byte elemType) {
        Object previousObject = get(index);
        if (previousObject != null) {
            return previousObject;
        }
        switch (elemType) {
            case KType.STRING_LONG_MAP:
                internal_set(index, elemType, new ArrayStringLongMap(this, Constants.MAP_INITIAL_CAPACITY, Constants.OFFHEAP_NULL_PTR), false);
                break;
            case KType.LONG_LONG_MAP:
                internal_set(index, elemType, new ArrayLongLongMap(this, Constants.MAP_INITIAL_CAPACITY, Constants.OFFHEAP_NULL_PTR), false);
                break;
            case KType.LONG_LONG_ARRAY_MAP:
                internal_set(index, elemType, new ArrayLongLongArrayMap(this, Constants.MAP_INITIAL_CAPACITY, Constants.OFFHEAP_NULL_PTR), false);
                break;
        }
        return get(index);

    }


    @Override
    public int getType(long index) {
        long elementDataSize = OffHeapLongArray.get(root_array_ptr, INDEX_ELEMENT_DATA_SIZE);
        if (elementDataSize == 0) {
            return -1;
        }
        long hashIndex = PrimitiveHelper.longHash(index, elementDataSize);
        long m = OffHeapLongArray.get(elementHash_ptr, hashIndex);
        while (m >= 0) {
            if (index == OffHeapLongArray.get(elementK_ptr, m) /* getKey */) {
                return (int) OffHeapLongArray.get(elementType_ptr, m); /* getValue */
            } else {
                m = OffHeapLongArray.get(elementNext_ptr, m);
            }
        }
        return -1;
    }
}
