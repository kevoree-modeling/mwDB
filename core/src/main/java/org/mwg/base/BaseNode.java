/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.base;

import org.mwg.*;
import org.mwg.plugin.NodeDeclaration;
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
        final NodeDeclaration declaration = this.graph().nodeRegistry().declarationByHash(this._resolver.typeCode(this));
        if (declaration != null) {
            return declaration.name();
        }
        return null;
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

    @Override
    public Object getAt(int propIndex) {
        return _resolver.resolveState(this).get(propIndex);
    }

    @Override
    public Node forceSet(String name, byte type, Object value) {
        return forceSetAt(this._resolver.stringToHash(name, true), type, value);
    }

    @Override
    public Node forceSetAt(int index, byte type, Object value) {
        final NodeState preciseState = this._resolver.alignState(this);
        if (preciseState != null) {
            preciseState.set(index, type, value);
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
        return this;
    }

    @Override
    public Node setAt(int index, byte type, Object value) {
        final NodeState unPhasedState = this._resolver.resolveState(this);
        boolean isDiff = (type != unPhasedState.getType(index));
        if (!isDiff) {
            isDiff = !isEquals(unPhasedState.get(index), value, type);
        }
        if (isDiff) {
            final NodeState preciseState = this._resolver.alignState(this);
            if (preciseState != null) {
                preciseState.set(index, type, value);
            } else {
                throw new RuntimeException(Constants.CACHE_MISS_ERROR);
            }
        }
        return this;
    }

    @Override
    public Node set(String name, byte type, Object value) {
        //hash the property a single time
        final int hashed = this._resolver.stringToHash(name, true);
        return setAt(hashed, type, value);
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
            case Type.RELATION_INDEXED:
            case Type.STRING_TO_INT_MAP:
            case Type.LONG_TO_LONG_MAP:
            case Type.LONG_TO_LONG_ARRAY_MAP:
                throw new RuntimeException("Bad API usage: set can't be used with complex type, please use getOrCreate instead.");
            default:
                throw new RuntimeException("Not managed type " + type);
        }
    }

    @Override
    public final Object getOrCreate(String name, byte type) {
        return getOrCreateAt(this._resolver.stringToHash(name, true), type);
    }

    @Override
    public Object getOrCreateAt(int index, byte type) {
        final NodeState preciseState = this._resolver.alignState(this);
        if (preciseState != null) {
            return preciseState.getOrCreate(index, type);
        } else {
            throw new RuntimeException(Constants.CACHE_MISS_ERROR);
        }
    }

    @Override
    public byte type(String name) {
        final NodeState resolved = this._resolver.resolveState(this);
        if (resolved != null) {
            return resolved.getType(this._resolver.stringToHash(name, false));
        }
        return -1;
    }

    @Override
    public byte typeAt(final int index) {
        final NodeState resolved = this._resolver.resolveState(this);
        if (resolved != null) {
            return resolved.getType(index);
        }
        return -1;
    }

    @Override
    public final Node remove(String name) {
        return set(name, Type.INT, null);
    }

    @Override
    public final Node removeAt(final int index) {
        return setAt(index, Type.INT, null);
    }

    @Override
    public final void relation(String relationName, final Callback<Node[]> callback) {
        relationAt(this._resolver.stringToHash(relationName, false), callback);
    }

    @Override
    public void relationAt(int relationIndex, Callback<Node[]> callback) {
        if (callback == null) {
            return;
        }
        final NodeState resolved = this._resolver.resolveState(this);
        if (resolved != null) {
            switch (resolved.getType(relationIndex)) {
                case Type.RELATION:
                    final Relation relation = (Relation) resolved.get(relationIndex);
                    if (relation == null || relation.size() == 0) {
                        callback.on(new Node[0]);
                    } else {
                        final int relSize = relation.size();
                        final long[] ids = new long[relSize];
                        for (int i = 0; i < relSize; i++) {
                            ids[i] = relation.get(i);
                        }
                        this._resolver.lookupAll(_world, _time, ids, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result) {
                                callback.on(result);
                            }
                        });
                    }
                    break;
                case Type.RELATION_INDEXED:
                    final RelationIndexed relation_indexed = (RelationIndexed) resolved.get(relationIndex);
                    if (relation_indexed == null || relation_indexed.size() == 0) {
                        callback.on(new Node[0]);
                    } else {
                        this._resolver.lookupAll(_world, _time, relation_indexed.all(), new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result) {
                                callback.on(result);
                            }
                        });
                    }
                    break;
                default:
                    callback.on(new Node[0]);
                    break;
            }
        } else {
            callback.on(new Node[0]);
        }
    }

    @Override
    public final Node addToRelation(String relationName, Node relatedNode, String... attributes) {
        return addToRelationAt(this._resolver.stringToHash(relationName, true), relatedNode, attributes);
    }

    @Override
    public Node addToRelationAt(int relationIndex, Node relatedNode, String... attributes) {
        if (relatedNode != null) {
            NodeState preciseState = this._resolver.alignState(this);
            if (preciseState != null) {
                boolean attributesNotEmpty = (attributes != null && attributes.length > 0);
                if (attributesNotEmpty) {
                    RelationIndexed indexedRel = (RelationIndexed) preciseState.getOrCreate(relationIndex, Type.RELATION_INDEXED);
                    indexedRel.add(relatedNode, attributes);
                } else {
                    Relation relationArray = (Relation) preciseState.getOrCreate(relationIndex, Type.RELATION);
                    relationArray.add(relatedNode.id());
                }
            } else {
                throw new RuntimeException(Constants.CACHE_MISS_ERROR);
            }
        }
        return this;
    }

    @Override
    public final Node removeFromRelation(String relationName, Node relatedNode, String... attributes) {
        return removeFromRelationAt(this._resolver.stringToHash(relationName, false), relatedNode, attributes);
    }

    @Override
    public Node removeFromRelationAt(int relationIndex, Node relatedNode, String... attributes) {
        if (relatedNode != null) {
            final NodeState preciseState = this._resolver.alignState(this);
            if (preciseState != null) {
                boolean attributesNotEmpty = (attributes != null && attributes.length > 0);
                if (attributesNotEmpty) {
                    RelationIndexed indexedRel = (RelationIndexed) preciseState.getOrCreate(relationIndex, Type.RELATION_INDEXED);
                    indexedRel.remove(relatedNode, attributes);
                } else {
                    Relation relationArray = (Relation) preciseState.getOrCreate(relationIndex, Type.RELATION);
                    relationArray.remove(relatedNode.id());
                }
            } else {
                throw new RuntimeException(Constants.CACHE_MISS_ERROR);
            }
        }
        return this;
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
    public final Node rephase() {
        this._resolver.alignState(this);
        return this;
    }

    @Override
    public final void timepoints(final long beginningOfSearch, final long endOfSearch, final Callback<long[]> callback) {
        this._resolver.resolveTimepoints(this, beginningOfSearch, endOfSearch, callback);
    }

    @Override
    public final <A extends Node> void travelInTime(final long targetTime, final Callback<A> callback) {
        _resolver.lookup(_world, targetTime, _id, callback);
    }

    @Override
    public final Node setTimeSensitivity(long deltaTime, long offset) {
        _resolver.setTimeSensitivity(this, deltaTime, offset);
        return this;
    }

    @Override
    public final long[] timeSensitivity() {
        return _resolver.getTimeSensitivity(this);
    }

    /**
     * @native ts
     * return isNaN(toTest);
     */
    /**
     * Tests if an element is Not A Number
     *
     * @param toTest the element to test
     * @return false if the element is a number
     */
    public static boolean isNaN(double toTest) {
        return Double.NaN == toTest;
    }//TODO: Move to a utility class

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
                public void on(int attributeKey, byte elemType, Object elem) {
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
                                    builder.append("1");
                                } else {
                                    builder.append("0");
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
                                Relation castedRelArr = (Relation) elem;
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
                            case Type.RELATION_INDEXED:
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
                            case Type.STRING_TO_INT_MAP: {
                                builder.append(",\"");
                                builder.append(resolveName);
                                builder.append("\":");
                                builder.append("{");
                                StringIntMap castedMapS2L = (StringIntMap) elem;
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

}
