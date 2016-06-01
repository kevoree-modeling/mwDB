package org.mwg.plugin;

import org.mwg.*;
import org.mwg.struct.LongLongArrayMap;
import org.mwg.struct.LongLongArrayMapCallBack;
import org.mwg.struct.Map;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Base implementation to develop NodeFactory plugins without overriding every methods
 */
public abstract class AbstractNode implements Node {

    private final long _world;

    private final long _time;

    private final long _id;

    private final Graph _graph;

    protected final Resolver _resolver;

    public final AtomicReference<long[]> _previousResolveds;

    public AbstractNode(long p_world, long p_time, long p_id, Graph p_graph, long[] currentResolution) {
        this._world = p_world;
        this._time = p_time;
        this._id = p_id;
        this._graph = p_graph;
        this._resolver = p_graph.resolver();
        this._previousResolveds = new AtomicReference<long[]>();
        this._previousResolveds.set(currentResolution);
    }

    protected NodeState unphasedState() {
        return this._resolver.resolveState(this, true);
    }

    protected NodeState phasedState() {
        return this._resolver.resolveState(this, false);
    }

    protected NodeState newState(long time) {
        return this._resolver.newState(this, _world, time);
    }

    @Override
    public Graph graph() {
        return _graph;
    }

    @Override
    public long world() {
        return this._world;
    }

    @Override
    public long time() {
        return this._time;
    }

    @Override
    public long id() {
        return this._id;
    }

    @Override
    public Object get(String propertyName) {
        NodeState resolved = this._resolver.resolveState(this, true);
        if (resolved != null) {
            return resolved.get(this._resolver.stringToHash(propertyName, false));
        }
        return null;
    }

    /**
     * @native ts
     * if (typeof propertyValue === 'string' || propertyValue instanceof String) {
     * this.setProperty(propertyName, org.mwg.Type.STRING, propertyValue);
     * } else if(typeof propertyValue === 'number' || propertyValue instanceof Number) {
     * if(propertyValue % 1 != 0) {
     * this.setProperty(propertyName, org.mwg.Type.DOUBLE, propertyValue);
     * } else {
     * this.setProperty(propertyName, org.mwg.Type.LONG, propertyValue);
     * }
     * } else if(typeof propertyValue === 'boolean' || propertyValue instanceof Boolean) {
     * this.setProperty(propertyName, org.mwg.Type.BOOL, propertyValue);
     * } else if (propertyValue instanceof Int32Array) {
     * this.setProperty(propertyName, org.mwg.Type.LONG_ARRAY, propertyValue);
     * } else if (propertyValue instanceof Float64Array) {
     * this.setProperty(propertyName, org.mwg.Type.DOUBLE_ARRAY, propertyValue);
     * } else {
     * throw new Error("Invalid property type: " + propertyValue + ", please use a Type listed in org.mwg.Type");
     * }
     */
    @Override
    public final void set(String propertyName, Object propertyValue) {
        if (propertyValue instanceof String) {
            setProperty(propertyName, Type.STRING, propertyValue);
        } else if (propertyValue instanceof Double) {
            setProperty(propertyName, Type.DOUBLE, propertyValue);
        } else if (propertyValue instanceof Long) {
            setProperty(propertyName, Type.LONG, propertyValue);
        } else if (propertyValue instanceof Float) {
            setProperty(propertyName, Type.DOUBLE, (double) ((float) propertyValue));
        } else if (propertyValue instanceof Integer) {
            setProperty(propertyName, Type.INT, propertyValue);
        } else if (propertyValue instanceof Boolean) {
            setProperty(propertyName, Type.BOOL, propertyValue);
        } else if (propertyValue instanceof int[]) {
            setProperty(propertyName, Type.INT_ARRAY, propertyValue);
        } else if (propertyValue instanceof double[]) {
            setProperty(propertyName, Type.DOUBLE_ARRAY, propertyValue);
        } else if (propertyValue instanceof long[]) {
            setProperty(propertyName, Type.LONG_ARRAY, propertyValue);
        } else {
            throw new RuntimeException("Invalid property type: " + propertyValue + ", please use a Type listed in org.mwg.Type");
        }
    }

    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        NodeState preciseState = this._resolver.resolveState(this, false);
        if (preciseState != null) {
            preciseState.set(this._resolver.stringToHash(propertyName, true), propertyType, propertyValue);
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public Map map(String propertyName, byte propertyType) {
        NodeState preciseState = this._resolver.resolveState(this, false);
        if (preciseState != null) {
            return (Map) preciseState.getOrCreate(this._resolver.stringToHash(propertyName, true), propertyType);
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public byte type(String propertyName) {
        NodeState resolved = this._resolver.resolveState(this, true);
        if (resolved != null) {
            return resolved.getType(this._resolver.stringToHash(propertyName, false));
        }
        return -1;
    }

    @Override
    public void removeProperty(String attributeName) {
        setProperty(attributeName, Type.INT, null);
    }

    @Override
    public void rel(String relationName, Callback<Node[]> callback) {
        if (callback == null) {
            return;
        }
        final NodeState resolved = this._resolver.resolveState(this, true);
        if (resolved != null) {
            final long[] flatRefs = (long[]) resolved.get(this._resolver.stringToHash(relationName, false));
            if (flatRefs == null || flatRefs.length == 0) {
                callback.on(new Node[0]);
            } else {
                final Node[] result = new Node[flatRefs.length];
                final DeferCounter counter = _graph.counter(flatRefs.length);
                final int[] resultIndex = new int[1];
                for (int i = 0; i < flatRefs.length; i++) {
                    this._resolver.lookup(_world, _time, flatRefs[i], new Callback<Node>() {
                        @Override
                        public void on(Node kNode) {
                            if (kNode != null) {
                                result[resultIndex[0]] = kNode;
                                resultIndex[0]++;
                            }
                            counter.count();
                        }
                    });
                }
                counter.then(new Callback() {
                    @Override
                    public void on(Object o) {
                        if (resultIndex[0] == result.length) {
                            callback.on(result);
                        } else {
                            Node[] toSend = new Node[resultIndex[0]];
                            System.arraycopy(result, 0, toSend, 0, toSend.length);
                            callback.on(toSend);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void add(String relationName, Node relatedNode) {
        NodeState preciseState = this._resolver.resolveState(this, false);
        long relationKey = this._resolver.stringToHash(relationName, true);
        if (preciseState != null) {
            long[] previous = (long[]) preciseState.get(relationKey);
            if (previous == null) {
                previous = new long[1];
                previous[0] = relatedNode.id();
            } else {
                long[] incArray = new long[previous.length + 1];
                System.arraycopy(previous, 0, incArray, 0, previous.length);
                incArray[previous.length] = relatedNode.id();
                previous = incArray;
            }
            preciseState.set(relationKey, Type.LONG_ARRAY, previous);
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public void remove(String relationName, Node relatedNode) {
        NodeState preciseState = this._resolver.resolveState(this, false);
        long relationKey = this._resolver.stringToHash(relationName, false);
        if (preciseState != null) {
            long[] previous = (long[]) preciseState.get(relationKey);
            if (previous != null) {
                int indexToRemove = -1;
                for (int i = 0; i < previous.length; i++) {
                    if (previous[i] == relatedNode.id()) {
                        indexToRemove = i;
                        break;
                    }
                }
                if (indexToRemove != -1) {
                    if ((previous.length - 1) == 0) {
                        preciseState.set(relationKey, Type.LONG_ARRAY, null);
                    } else {
                        long[] newArray = new long[previous.length - 1];
                        System.arraycopy(previous, 0, newArray, 0, indexToRemove);
                        System.arraycopy(previous, indexToRemove + 1, newArray, indexToRemove, previous.length - indexToRemove - 1);
                        preciseState.set(relationKey, Type.LONG_ARRAY, newArray);
                    }
                }
            }
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public void free() {
        this._resolver.freeNode(this);
    }

    @Override
    public long timeDephasing() {
        NodeState state = this._resolver.resolveState(this, true);
        if (state != null) {
            return (this._time - state.time());
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public void forcePhase() {
        this._resolver.resolveState(this, false);
    }

    @Override
    public void timepoints(long beginningOfSearch, long endOfSearch, Callback<long[]> callback) {
        this._resolver.resolveTimepoints(this, beginningOfSearch, endOfSearch, callback);
    }

    @Override
    public <A extends Node> void jump(long targetTime, Callback<A> callback) {
        _resolver.lookup(_world, targetTime, _id, callback);
    }

    @Override
    public void findAt(String indexName, long world, long time, String query, Callback<Node[]> callback) {
        final Query flatquery = Query.parseQuery(query, this._resolver);
        findAt(indexName, world, time, flatquery, callback);
    }

    public void findAt(String indexName, long world, long time, java.util.Map<String, String> query, Callback<Node[]> callback) {
        final Query flatquery = Query.parseQuery(query, this._resolver);
        findAt(indexName, world, time, flatquery, callback);
    }

    private void findAt(String indexName, long world, long time, final Query flatQuery, final Callback<Node[]> callback) {
        NodeState currentNodeState = this._resolver.resolveState(this, false);
        if (currentNodeState == null) {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
        LongLongArrayMap indexMap = (LongLongArrayMap) currentNodeState.get(this._resolver.stringToHash(indexName, false));
        if (indexMap != null) {
            final AbstractNode selfPointer = this;
            final long[] foundId = indexMap.get(flatQuery.hash());
            if (foundId == null) {
                callback.on(new AbstractNode[0]);
                return;
            }
            final Node[] resolved = new AbstractNode[foundId.length];
            final DeferCounter waiter = _graph.counter(foundId.length);
            //TODO replace by a par lookup
            final AtomicInteger nextResolvedTabIndex = new AtomicInteger(0);

            for (int i = 0; i < foundId.length; i++) {
                selfPointer._resolver.lookup(world, time, foundId[i], new Callback<Node>() {
                    @Override
                    public void on(Node resolvedNode) {
                        if (resolvedNode != null) {
                            resolved[nextResolvedTabIndex.getAndIncrement()] = resolvedNode;
                        }
                        waiter.count();
                    }
                });
            }
            waiter.then(new Callback() {
                @Override
                public void on(Object o) {
                    //select
                    Node[] resultSet = new AbstractNode[nextResolvedTabIndex.get()];
                    int resultSetIndex = 0;

                    for (int i = 0; i < resultSet.length; i++) {
                        Node resolvedNode = resolved[i];
                        NodeState resolvedState = selfPointer._resolver.resolveState(resolvedNode, true);
                        boolean exact = true;
                        for (int j = 0; j < flatQuery.size; j++) {
                            Object obj = resolvedState.get(flatQuery.attributes[j]);
                            if (flatQuery.values[j] == null) {
                                if (obj != null) {
                                    exact = false;
                                    break;
                                }
                            } else {
                                if (obj == null) {
                                    exact = false;
                                    break;
                                } else {
                                    if (!Constants.equals(flatQuery.values[j], obj.toString())) {
                                        exact = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (exact) {
                            resultSet[resultSetIndex] = resolvedNode;
                            resultSetIndex++;
                        }
                    }
                    if (resultSet.length == resultSetIndex) {
                        callback.on(resultSet);
                    } else {
                        Node[] trimmedResultSet = new AbstractNode[resultSetIndex];
                        System.arraycopy(resultSet, 0, trimmedResultSet, 0, resultSetIndex);
                        callback.on(trimmedResultSet);
                    }
                }
            });
        } else {
            callback.on(new AbstractNode[0]);
        }
    }

    @Override
    public void find(String indexName, String query, Callback<Node[]> callback) {
        findAt(indexName, time(), world(), query, callback);
    }

    public void find(String indexName, HashMap<String, String> query, Callback<Node[]> callback) {
        findAt(indexName, time(), world(), query, callback);
    }

    @Override
    public void allAt(String indexName, long world, long time, Callback<Node[]> callback) {
        NodeState currentNodeState = this._resolver.resolveState(this, false);
        if (currentNodeState == null) {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
        LongLongArrayMap indexMap = (LongLongArrayMap) currentNodeState.get(this._resolver.stringToHash(indexName, false));
        if (indexMap != null) {
            final AbstractNode selfPointer = this;
            int mapSize = (int) indexMap.size();
            final Node[] resolved = new org.mwg.plugin.AbstractNode[mapSize];
            DeferCounter waiter = _graph.counter(mapSize);
            //TODO replace by a parralel lookup
            final AtomicInteger loopInteger = new AtomicInteger(0);
            indexMap.each(new LongLongArrayMapCallBack() {
                @Override
                public void on(final long hash, final long nodeId) {
                    selfPointer._resolver.lookup(world, time, nodeId, new Callback<org.mwg.Node>() {
                        @Override
                        public void on(org.mwg.Node resolvedNode) {
                            resolved[loopInteger.getAndIncrement()] = resolvedNode;
                            waiter.count();
                        }
                    });
                }
            });
            waiter.then(new Callback() {
                @Override
                public void on(Object o) {
                    if (loopInteger.get() == resolved.length) {
                        callback.on(resolved);
                    } else {
                        Node[] toSend = new org.mwg.plugin.AbstractNode[loopInteger.get()];
                        System.arraycopy(resolved, 0, toSend, 0, toSend.length);
                        callback.on(toSend);
                    }
                }
            });
        } else {
            callback.on(new org.mwg.plugin.AbstractNode[0]);
        }
    }

    @Override
    public void all(String indexName, Callback<Node[]> callback) {
        allAt(indexName, world(), time(), callback);
    }

    @Override
    public void index(String indexName, org.mwg.Node nodeToIndex, String flatKeyAttributes, Callback<Boolean> callback) {
        final String[] keyAttributes = flatKeyAttributes.split(Constants.QUERY_SEP + "");
        NodeState currentNodeState = this._resolver.resolveState(this, true);
        if (currentNodeState == null) {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
        LongLongArrayMap indexMap = (LongLongArrayMap) currentNodeState.getOrCreate(this._resolver.stringToHash(indexName, true), Type.LONG_LONG_ARRAY_MAP);
        Query flatQuery = new Query();
        NodeState toIndexNodeState = this._resolver.resolveState(nodeToIndex, true);
        for (int i = 0; i < keyAttributes.length; i++) {
            long attKey = this._resolver.stringToHash(keyAttributes[i], true);
            Object attValue = toIndexNodeState.get(attKey);
            if (attValue != null) {
                flatQuery.add(attKey, attValue.toString());
            } else {
                flatQuery.add(attKey, null);
            }
        }
        flatQuery.compute();
        //TODO AUTOMATIC UPDATE
        indexMap.put(flatQuery.hash(), nodeToIndex.id());

        if (Constants.isDefined(callback)) {
            callback.on(true);
        }
    }

    @Override
    public void unindex(String indexName, org.mwg.Node nodeToIndex, String flatKeyAttributes, Callback<Boolean> callback) {
        final String[] keyAttributes = flatKeyAttributes.split(Constants.QUERY_SEP + "");
        NodeState currentNodeState = this._resolver.resolveState(this, true);
        if (currentNodeState == null) {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
        LongLongArrayMap indexMap = (LongLongArrayMap) currentNodeState.get(this._resolver.stringToHash(indexName, false));
        if (indexMap != null) {
            Query flatQuery = new Query();
            NodeState toIndexNodeState = this._resolver.resolveState(nodeToIndex, true);
            for (int i = 0; i < keyAttributes.length; i++) {
                long attKey = this._resolver.stringToHash(keyAttributes[i], false);
                Object attValue = toIndexNodeState.get(attKey);
                if (attValue != null) {
                    flatQuery.add(attKey, attValue.toString());
                } else {
                    flatQuery.add(attKey, null);
                }
            }
            flatQuery.compute();
            //TODO AUTOMATIC UPDATE
            indexMap.remove(flatQuery.hash(), nodeToIndex.id());
        }
        if (Constants.isDefined(callback)) {
            callback.on(true);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"world\":");
        builder.append(world());
        builder.append(",\"time\":");
        builder.append(time());
        builder.append(",\"id\":");
        builder.append(id());
        NodeState state = this._resolver.resolveState(this, true);
        if (state != null) {
            state.each(new NodeStateCallback() {
                @Override
                public void on(long attributeKey, int elemType, Object elem) {
                    if (elem != null) {
                        builder.append(",\"");
                        builder.append(_resolver.hashToString(attributeKey));
                        builder.append("\":");
                        switch (elemType) {
                            /** Primitive types */
                            case Type.BOOL: {
                                if ((boolean) elem) {
                                    builder.append("0");
                                } else {
                                    builder.append("1");
                                }
                                break;
                            }
                            case Type.STRING: {
                                builder.append("\"");
                                builder.append(elem);
                                builder.append("\"");
                                break;
                            }
                            case Type.LONG: {
                                builder.append(elem);
                                break;
                            }
                            case Type.INT: {
                                builder.append(elem);
                                break;
                            }
                            case Type.DOUBLE: {
                                builder.append(elem);
                                break;
                            }
                            /** Array types */
                            case Type.DOUBLE_ARRAY: {
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
                            case Type.LONG_ARRAY: {
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
                        }
                    }
                }
            });
            builder.append("}");
        }
        return builder.toString();
    }


    public void setPropertyWithType(String propertyName, byte propertyType, Object propertyValue, byte propertyTargetType) {
        if (propertyType != propertyTargetType) {
            throw new RuntimeException("Property " + propertyName + " has a type mismatch, provided " + Type.typeName(propertyType) + " expected: " + Type.typeName(propertyTargetType));
        } else {
            NodeState preciseState = this._resolver.resolveState(this, false);
            if (preciseState != null) {
                preciseState.set(this._resolver.stringToHash(propertyName, true), propertyType, propertyValue);
            } else {
                throw new RuntimeException(Constants.CACHE_MISS_ERROR);
            }
        }
    }

}
