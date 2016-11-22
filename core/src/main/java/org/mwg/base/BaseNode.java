package org.mwg.base;

import org.mwg.*;
import org.mwg.plugin.NodeState;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.plugin.Resolver;
import org.mwg.struct.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Base implementation to develop NodeFactory plugins without overriding every methods
 */
public class BaseNode implements Node {

    /**
     * @ignore ts
     */
    private static sun.misc.Unsafe unsafe;

    private final long _world;
    private final long _time;
    private final long _id;
    private final Graph _graph;
    protected final Resolver _resolver;

    //cache to enhance the resolving process
    public volatile long _index_worldOrder = -1;
    public volatile long _index_superTimeTree = -1;
    public volatile long _index_timeTree = -1;
    public volatile long _index_stateChunk = -1;
    public volatile long _world_magic = -1;
    public volatile long _super_time_magic = -1;
    public volatile long _time_magic = -1;
    public volatile boolean _dead = false;
    private volatile int _lock;

    public BaseNode(long p_world, long p_time, long p_id, Graph p_graph) {
        this._world = p_world;
        this._time = p_time;
        this._id = p_id;
        this._graph = p_graph;
        this._resolver = p_graph.resolver();
    }

    /**
     * @ignore ts
     */
    private static final long _lockOffset;

    /**
     * @ignore ts
     */
    static {
        if (unsafe == null) {
            try {
                Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                unsafe = (sun.misc.Unsafe) theUnsafe.get(null);
            } catch (Exception e) {
                throw new RuntimeException("ERROR: unsafe operations are not available");
            }
        }
        try {
            _lockOffset = unsafe.objectFieldOffset(BaseNode.class.getDeclaredField("_lock"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    /**
     * @native ts
     */
    public final void cacheLock() {
        while (!unsafe.compareAndSwapInt(this, _lockOffset, 0, 1)) ;
    }

    /**
     * @native ts
     */
    public final void cacheUnlock() {
        _lock = 0;
    }

    /**
     * This method should be overridden to init the object
     */
    public void init() {
        //noop
    }

    @Override
    public final String nodeTypeName() {
        return this._resolver.typeName(this);
    }

    protected final NodeState unphasedState() {
        return this._resolver.resolveState(this);
    }

    protected final NodeState phasedState() {
        return this._resolver.alignState(this);
    }

    protected final NodeState newState(long time) {
        return this._resolver.newState(this, _world, time);
    }

    @Override
    public final Graph graph() {
        return _graph;
    }

    @Override
    public final long world() {
        return this._world;
    }

    @Override
    public final long time() {
        return this._time;
    }

    @Override
    public final long id() {
        return this._id;
    }

    @Override
    public Object get(String name) {
        final NodeState resolved = this._resolver.resolveState(this);
        if (resolved != null) {
            return resolved.get(this._resolver.stringToHash(name, false));
        }
        return null;
    }

    /**
     * {@native ts
     * if (typeof value === 'string' || value instanceof String) {
     * this.setAttribute(name, org.mwg.Type.STRING, value);
     * } else if(typeof value === 'number' || value instanceof Number) {
     * if(((<number>value) % 1) != 0) {
     * this.setAttribute(name, org.mwg.Type.DOUBLE, value);
     * } else {
     * this.setAttribute(name, org.mwg.Type.LONG, value);
     * }
     * } else if(typeof value === 'boolean' || value instanceof Boolean) {
     * this.setAttribute(name, org.mwg.Type.BOOL, value);
     * } else if (value instanceof Int32Array) {
     * this.setAttribute(name, org.mwg.Type.LONG_ARRAY, value);
     * } else if (value instanceof Float64Array) {
     * this.setAttribute(name, org.mwg.Type.DOUBLE_ARRAY, value);
     * } else {
     * throw new Error("Invalid property type: " + value + ", please use a Type listed in org.mwg.Type");
     * }
     * }
     */
    @Override
    public final void set(String name, Object value) {
        if (value instanceof String) {
            setAttribute(name, Type.STRING, value);
        } else if (value instanceof Double) {
            setAttribute(name, Type.DOUBLE, value);
        } else if (value instanceof Long) {
            setAttribute(name, Type.LONG, value);
        } else if (value instanceof Float) {
            setAttribute(name, Type.DOUBLE, (double) ((Float) value));
        } else if (value instanceof Integer) {
            setAttribute(name, Type.INT, value);
        } else if (value instanceof Boolean) {
            setAttribute(name, Type.BOOL, value);
        } else if (value instanceof int[]) {
            setAttribute(name, Type.INT_ARRAY, value);
        } else if (value instanceof double[]) {
            setAttribute(name, Type.DOUBLE_ARRAY, value);
        } else if (value instanceof long[]) {
            setAttribute(name, Type.LONG_ARRAY, value);
        } else {
            throw new RuntimeException("Invalid property type: " + value + ", please use a Type listed in org.mwg.Type");
        }
    }

    @Override
    public void forceAttribute(String name, byte type, Object value) {
        final long hashed = this._resolver.stringToHash(name, true);
        final NodeState preciseState = this._resolver.alignState(this);
        if (preciseState != null) {
            preciseState.set(hashed, type, value);
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public void setAttribute(String name, byte type, Object value) {
        //hash the property a single time
        final long hashed = this._resolver.stringToHash(name, true);
        final NodeState unPhasedState = this._resolver.resolveState(this);
        boolean isDiff = (type != unPhasedState.getType(hashed));
        if (!isDiff) {
            isDiff = !isEquals(unPhasedState.get(hashed), value, type);
        }
        if (isDiff) {
            final NodeState preciseState = this._resolver.alignState(this);
            if (preciseState != null) {
                preciseState.set(hashed, type, value);
            } else {
                throw new RuntimeException(Constants.CACHE_MISS_ERROR);
            }
        }
    }

    private boolean isEquals(Object obj1, Object obj2, byte type) {
        switch (type) {
            case Type.BOOL:
                return (((boolean) obj1) == ((boolean) obj2));
            case Type.DOUBLE:
                return (((double) obj1) == ((double) obj2));
            case Type.INT:
                return (((int) obj1) == ((int) obj2));
            case Type.LONG:
                return (((long) obj1) == ((long) obj2));
            case Type.STRING:
                return (((String) obj1).equals((String) obj2));
            case Type.DOUBLE_ARRAY:
                double[] obj1_ar_d = (double[]) obj1;
                double[] obj2_ar_d = (double[]) obj2;
                if (obj1_ar_d.length != obj2_ar_d.length) {
                    return false;
                } else {
                    for (int i = 0; i < obj1_ar_d.length; i++) {
                        if (obj1_ar_d[i] != obj2_ar_d[i]) {
                            return false;
                        }
                    }
                }
                return true;
            case Type.INT_ARRAY:
                int[] obj1_ar_i = (int[]) obj1;
                int[] obj2_ar_i = (int[]) obj2;
                if (obj1_ar_i.length != obj2_ar_i.length) {
                    return false;
                } else {
                    for (int i = 0; i < obj1_ar_i.length; i++) {
                        if (obj1_ar_i[i] != obj2_ar_i[i]) {
                            return false;
                        }
                    }
                }
                return true;
            case Type.LONG_ARRAY:
                long[] obj1_ar_l = (long[]) obj1;
                long[] obj2_ar_l = (long[]) obj2;
                if (obj1_ar_l.length != obj2_ar_l.length) {
                    return false;
                } else {
                    for (int i = 0; i < obj1_ar_l.length; i++) {
                        if (obj1_ar_l[i] != obj2_ar_l[i]) {
                            return false;
                        }
                    }
                }
                return true;
            case Type.RELATION:
            case Type.INDEXED_RELATION:
            case Type.MULTI_INDEXED_RELATION:
            case Type.STRING_TO_LONG_MAP:
            case Type.LONG_TO_LONG_MAP:
            case Type.LONG_TO_LONG_ARRAY_MAP:
                throw new RuntimeException("Bad API usage: set can't be used with complex type, please use getOrCreate instead.");
            default:
                throw new RuntimeException("Not managed type " + type);
        }
    }

    @Override
    public final Object getOrCreate(String name, byte type) {
        final NodeState preciseState = this._resolver.alignState(this);
        if (preciseState != null) {
            return preciseState.getOrCreate(this._resolver.stringToHash(name, true), type);
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public final Object getOrCreateExternal(String name, String externalAttributeType) {
        final NodeState preciseState = this._resolver.alignState(this);
        if (preciseState != null) {
            return preciseState.getOrCreateExternal(this._resolver.stringToHash(name, true), externalAttributeType);
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public final byte type(String name) {
        final NodeState resolved = this._resolver.resolveState(this);
        if (resolved != null) {
            return resolved.getType(this._resolver.stringToHash(name, false));
        }
        return -1;
    }

    @Override
    public final void removeAttribute(String name) {
        setAttribute(name, Type.INT, null);
    }

    @Override
    public final void rel(String relationName, final Callback<Node[]> callback) {
        relByIndex(this._resolver.stringToHash(relationName, false), callback);
    }

    @Override
    public void relByIndex(long relationIndex, Callback<Node[]> callback) {
        if (callback == null) {
            return;
        }
        final NodeState resolved = this._resolver.resolveState(this);
        if (resolved != null) {
            final Relationship relationArray = (Relationship) resolved.get(relationIndex);
            if (relationArray == null || relationArray.size() == 0) {
                callback.on(new Node[0]);
            } else {
                final int relSize = relationArray.size();
                final long[] ids = new long[relSize];
                for (int i = 0; i < relSize; i++) {
                    ids[i] = relationArray.get(i);
                }
                this._resolver.lookupAll(_world, _time, ids, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        callback.on(result);
                    }
                });
            }
        } else {
            callback.on(new Node[0]);
        }
    }

    @Override
    public final void add(String relationName, Node relatedNode) {
        if (relatedNode != null) {
            NodeState preciseState = this._resolver.alignState(this);
            final long relHash = this._resolver.stringToHash(relationName, true);
            if (preciseState != null) {
                Relationship relationArray = (Relationship) preciseState.getOrCreate(relHash, Type.RELATION);
                relationArray.add(relatedNode.id());
            } else {
                throw new RuntimeException(Constants.CACHE_MISS_ERROR);
            }
        }
    }

    @Override
    public final void remove(String relationName, Node relatedNode) {
        if (relatedNode != null) {
            final NodeState preciseState = this._resolver.alignState(this);
            final long relHash = this._resolver.stringToHash(relationName, false);
            if (preciseState != null) {
                Relationship relationArray = (Relationship) preciseState.get(relHash);
                if (relationArray != null) {
                    relationArray.remove(relatedNode.id());
                }
            } else {
                throw new RuntimeException(Constants.CACHE_MISS_ERROR);
            }
        }
    }

    @Override
    public final void free() {
        this._resolver.freeNode(this);
    }

    @Override
    public final long timeDephasing() {
        final NodeState state = this._resolver.resolveState(this);
        if (state != null) {
            return (this._time - state.time());
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public final long lastModification() {
        final NodeState state = this._resolver.resolveState(this);
        if (state != null) {
            return state.time();
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public final void rephase() {
        this._resolver.alignState(this);
    }

    @Override
    public final void timepoints(final long beginningOfSearch, final long endOfSearch, final Callback<long[]> callback) {
        this._resolver.resolveTimepoints(this, beginningOfSearch, endOfSearch, callback);
    }

    @Override
    public final <A extends Node> void jump(final long targetTime, final Callback<A> callback) {
        _resolver.lookup(_world, targetTime, _id, callback);
    }

    @Override
    public final void findByQuery(final Query query, final Callback<Node[]> callback) {
        final NodeState currentNodeState = this._resolver.resolveState(this);
        if (currentNodeState == null) {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
        final String indexName = query.indexName();
        if (indexName == null) {
            throw new RuntimeException("Please specify indexName in query before first use!");
        }
        long queryWorld = query.world();
        if (queryWorld == Constants.NULL_LONG) {
            queryWorld = world();
        }
        long queryTime = query.time();
        if (queryTime == Constants.NULL_LONG) {
            queryTime = time();
        }
        final LongLongArrayMap indexMap = (LongLongArrayMap) currentNodeState.get(this._resolver.stringToHash(indexName, false));
        if (indexMap != null) {
            final BaseNode selfPointer = this;
            final long[] foundIds = indexMap.get(query.hash());
            if (foundIds == null) {
                callback.on(new BaseNode[0]);
                return;
            }
            selfPointer._resolver.lookupAll(queryWorld, queryTime, foundIds, new Callback<Node[]>() {
                @Override
                public void on(Node[] resolved) {
                    //select
                    Node[] resultSet = new BaseNode[foundIds.length];
                    int resultSetIndex = 0;
                    for (int i = 0; i < resultSet.length; i++) {
                        final org.mwg.Node resolvedNode = resolved[i];
                        if (resolvedNode != null) {
                            final NodeState resolvedState = selfPointer._resolver.resolveState(resolvedNode);
                            boolean exact = true;
                            for (int j = 0; j < query.attributes().length; j++) {
                                Object obj = resolvedState.get(query.attributes()[j]);
                                if (query.values()[j] == null) {
                                    if (obj != null) {
                                        exact = false;
                                        break;
                                    }
                                } else {
                                    if (obj == null) {
                                        exact = false;
                                        break;
                                    } else {
                                        if (obj instanceof long[]) {
                                            if (query.values()[j] instanceof long[]) {
                                                if (!Constants.longArrayEquals((long[]) query.values()[j], (long[]) obj)) {
                                                    exact = false;
                                                    break;
                                                }
                                            } else {
                                                exact = false;
                                                break;
                                            }
                                        } else {
                                            if (!Constants.equals(query.values()[j].toString(), obj.toString())) {
                                                exact = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (exact) {
                                resultSet[resultSetIndex] = resolvedNode;
                                resultSetIndex++;
                            }
                        }
                    }
                    if (resultSet.length == resultSetIndex) {
                        callback.on(resultSet);
                    } else {
                        Node[] trimmedResultSet = new BaseNode[resultSetIndex];
                        System.arraycopy(resultSet, 0, trimmedResultSet, 0, resultSetIndex);
                        callback.on(trimmedResultSet);
                    }
                }
            });
        } else {
            callback.on(new BaseNode[0]);
        }
    }

    @Override
    public final void find(String indexName, String query, Callback<Node[]> callback) {
        final Query queryObj = _graph.newQuery();
        queryObj.setWorld(world());
        queryObj.setTime(time());
        queryObj.setIndexName(indexName);
        queryObj.parse(query);
        findByQuery(queryObj, callback);
    }

    @Override
    public final void findAll(final String indexName, final Callback<Node[]> callback) {
        final NodeState currentNodeState = this._resolver.resolveState(this);
        if (currentNodeState == null) {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
        final LongLongArrayMap indexMap = (LongLongArrayMap) currentNodeState.get(this._resolver.stringToHash(indexName, false));
        if (indexMap != null) {
            final long[] ids = new long[(int) indexMap.size()];
            final int[] idIndex = {0};
            indexMap.each(new LongLongArrayMapCallBack() {
                @Override
                public void on(final long hash, final long nodeId) {
                    ids[idIndex[0]] = nodeId;
                    idIndex[0]++;
                }
            });
            _resolver.lookupAll(world(), time(), ids, new Callback<Node[]>() {
                @Override
                public void on(Node[] result) {
                    //TODO shrink result
                    callback.on(result);
                }
            });
        } else {
            callback.on(new BaseNode[0]);
        }
    }

    @Override
    public final void index(String indexName, org.mwg.Node nodeToIndex, String flatKeyAttributes, Callback<Boolean> callback) {
        final String[] keyAttributes = flatKeyAttributes.split(Constants.QUERY_SEP + "");
        final long hashName = this._resolver.stringToHash(indexName, true);
        Query flatQuery = _graph.newQuery();
        final NodeState toIndexNodeState = this._resolver.resolveState(nodeToIndex);
        for (int i = 0; i < keyAttributes.length; i++) {
            String attKey = keyAttributes[i];
            Object attValue = toIndexNodeState.getFromKey(attKey);
            if (attValue != null) {
                flatQuery.add(attKey, attValue.toString());
            } else {
                flatQuery.add(attKey, null);
            }
        }
        boolean alreadyIndexed = false;
        final NodeState previousState = this._resolver.resolveState(this);
        if (previousState != null) {
            LongLongArrayMap previousMap = (LongLongArrayMap) previousState.get(hashName);
            if (previousMap != null) {
                alreadyIndexed = previousMap.contains(flatQuery.hash(), nodeToIndex.id());
            }
        }
        if (!alreadyIndexed) {
            final NodeState currentNodeState = this._resolver.alignState(this);
            if (currentNodeState == null) {
                throw new RuntimeException(Constants.CACHE_MISS_ERROR);
            }
            LongLongArrayMap indexMap = (LongLongArrayMap) currentNodeState.getOrCreate(hashName, Type.LONG_TO_LONG_ARRAY_MAP);
            indexMap.put(flatQuery.hash(), nodeToIndex.id());
        }
        //TODO AUTOMATIC UPDATE
        if (Constants.isDefined(callback)) {
            callback.on(true);
        }
    }

    @Override
    public final void unindex(String indexName, org.mwg.Node nodeToIndex, String flatKeyAttributes, Callback<Boolean> callback) {
        final String[] keyAttributes = flatKeyAttributes.split(Constants.QUERY_SEP + "");
        final NodeState currentNodeState = this._resolver.alignState(this);
        if (currentNodeState == null) {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
        LongLongArrayMap indexMap = (LongLongArrayMap) currentNodeState.get(this._resolver.stringToHash(indexName, false));
        if (indexMap != null) {
            Query flatQuery = _graph.newQuery();
            final NodeState toIndexNodeState = this._resolver.resolveState(nodeToIndex);
            for (int i = 0; i < keyAttributes.length; i++) {
                String attKey = keyAttributes[i];
                Object attValue = toIndexNodeState.getFromKey(attKey);
                if (attValue != null) {
                    flatQuery.add(attKey, attValue.toString());
                } else {
                    flatQuery.add(attKey, null);
                }
            }
            //TODO AUTOMATIC UPDATE
            indexMap.remove(flatQuery.hash(), nodeToIndex.id());
        }
        if (Constants.isDefined(callback)) {
            callback.on(true);
        }
    }

    /**
     * @native ts
     * return isNaN(toTest);
     */
    private boolean isNaN(double toTest) {
        return Double.NaN == toTest;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        final boolean[] isFirst = {true};
        builder.append("{\"world\":");
        builder.append(world());
        builder.append(",\"time\":");
        builder.append(time());
        builder.append(",\"id\":");
        builder.append(id());
        final NodeState state = this._resolver.resolveState(this);
        if (state != null) {
            state.each(new NodeStateCallback() {
                @Override
                public void on(long attributeKey, byte elemType, Object elem) {
                    if (elem != null) {
                        String resolveName = _resolver.hashToString(attributeKey);
                        if (resolveName == null) {
                            resolveName = attributeKey + "";
                        }
                        switch (elemType) {
                            case Type.BOOL: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                if ((Boolean) elem) {
                                    builder.append("0");
                                } else {
                                    builder.append("1");
                                }
                                break;
                            }
                            case Type.STRING: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("\"");
                                builder.append(elem);
                                builder.append("\"");
                                break;
                            }
                            case Type.LONG: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append(elem);
                                break;
                            }
                            case Type.INT: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append(elem);
                                break;
                            }
                            case Type.DOUBLE: {
                                if (!isNaN((double) elem)) {
                                    builder.append(",\"");
                                    builder.append(resolveName);
                                    builder.append("\":");
                                    builder.append(elem);
                                }
                                break;
                            }
                            case Type.DOUBLE_ARRAY: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("[");
                                double[] castedArr = (double[]) elem;
                                for (int j = 0; j < castedArr.length; j++) {
                                    if (j != 0) {
                                        builder.append(",");
                                    }
                                    builder.append(castedArr[j]);
                                }
                                builder.append("]");
                                break;
                            }
                            case Type.RELATION:
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("[");
                                Relationship castedRelArr = (Relationship) elem;
                                for (int j = 0; j < castedRelArr.size(); j++) {
                                    if (j != 0) {
                                        builder.append(",");
                                    }
                                    builder.append(castedRelArr.get(j));
                                }
                                builder.append("]");
                                break;
                            case Type.LONG_ARRAY: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("[");
                                long[] castedArr2 = (long[]) elem;
                                for (int j = 0; j < castedArr2.length; j++) {
                                    if (j != 0) {
                                        builder.append(",");
                                    }
                                    builder.append(castedArr2[j]);
                                }
                                builder.append("]");
                                break;
                            }
                            case Type.INT_ARRAY: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("[");
                                int[] castedArr3 = (int[]) elem;
                                for (int j = 0; j < castedArr3.length; j++) {
                                    if (j != 0) {
                                        builder.append(",");
                                    }
                                    builder.append(castedArr3[j]);
                                }
                                builder.append("]");
                                break;
                            }
                            case Type.LONG_TO_LONG_MAP: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("{");
                                LongLongMap castedMapL2L = (LongLongMap) elem;
                                isFirst[0] = true;
                                castedMapL2L.each(new LongLongMapCallBack() {
                                    @Override
                                    public void on(long key, long value) {
                                        if (!isFirst[0]) {
                                            builder.append(",");
                                        } else {
                                            isFirst[0] = false;
                                        }
                                        builder.append("\"");
                                        builder.append(key);
                                        builder.append("\":");
                                        builder.append(value);
                                    }
                                });
                                builder.append("}");
                                break;
                            }
                            case Type.INDEXED_RELATION:
                            case Type.LONG_TO_LONG_ARRAY_MAP: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("{");
                                LongLongArrayMap castedMapL2LA = (LongLongArrayMap) elem;
                                isFirst[0] = true;
                                Set<Long> keys = new HashSet<Long>();
                                castedMapL2LA.each(new LongLongArrayMapCallBack() {
                                    @Override
                                    public void on(long key, long value) {
                                        keys.add(key);
                                    }
                                });
                                final Long[] flatKeys = keys.toArray(new Long[keys.size()]);
                                for (int i = 0; i < flatKeys.length; i++) {
                                    long[] values = castedMapL2LA.get(flatKeys[i]);
                                    if (!isFirst[0]) {
                                        builder.append(",");
                                    } else {
                                        isFirst[0] = false;
                                    }
                                    builder.append("\"");
                                    builder.append(flatKeys[i]);
                                    builder.append("\":[");
                                    for (int j = 0; j < values.length; j++) {
                                        if (j != 0) {
                                            builder.append(",");
                                        }
                                        builder.append(values[j]);
                                    }
                                    builder.append("]");
                                }
                                builder.append("}");
                                break;
                            }
                            case Type.STRING_TO_LONG_MAP: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("{");
                                StringLongMap castedMapS2L = (StringLongMap) elem;
                                isFirst[0] = true;
                                castedMapS2L.each(new StringLongMapCallBack() {
                                    @Override
                                    public void on(String key, long value) {
                                        if (!isFirst[0]) {
                                            builder.append(",");
                                        } else {
                                            isFirst[0] = false;
                                        }
                                        builder.append("\"");
                                        builder.append(key);
                                        builder.append("\":");
                                        builder.append(value);
                                    }
                                });
                                builder.append("}");
                                break;
                            }

                        }
                    }
                }
            });
            builder.append("}");
        }
        return builder.toString();
    }

    @Override
    public Object getByIndex(long propIndex) {
        return _resolver.resolveState(this).get(propIndex);
    }

    @Override
    public void setAttributeByIndex(final long index, final byte type, final Object value) {
        _resolver.alignState(this).set(index, type, value);
    }

}
