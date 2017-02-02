/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.memory.offheap;

import org.mwg.*;
import org.mwg.base.BaseNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.LongLongArrayMapCallBack;
import org.mwg.struct.RelationIndexed;

public class OffHeapRelationIndexed extends OffHeapLongLongArrayMap implements RelationIndexed {
    private final Graph _graph;

    OffHeapRelationIndexed(OffHeapContainer p_container, long p_index, Graph p_graph) {
        super(p_container, p_index);
        _graph = p_graph;
    }

    @Override
    public RelationIndexed add(Node node, String... attributeNames) {
        internal_add_remove(true, node, attributeNames);
        return this;
    }

    @Override
    public RelationIndexed remove(Node node, String... attributeNames) {
        internal_add_remove(false, node, attributeNames);
        return this;
    }

    private void internal_add_remove(boolean isIndex, Node node, String... attributeNames) {
        final Query flatQuery = node.graph().newQuery();
        final NodeState toIndexNodeState = node.graph().resolver().resolveState(node);
        for (int i = 0; i < attributeNames.length; i++) {
            final String attKey = attributeNames[i];
            final Object attValue = toIndexNodeState.getFromKey(attKey);
            if (attValue != null) {
                flatQuery.add(attKey, attValue.toString());
            } else {
                flatQuery.add(attKey, null);
            }
        }
        if (isIndex) {
            put(flatQuery.hash(), node.id());
        } else {
            delete(flatQuery.hash(), node.id());
        }
    }

    @Override
    public RelationIndexed clear() {
        //TODO
        return this;
    }

    @Override
    public void find(Callback<Node[]> callback, final long world, final long time, String... params) {
        Query queryObj = _graph.newQuery().setWorld(world).setTime(time);
        String previous = null;
        for (int i = 0; i < params.length; i++) {
            if (previous != null) {
                queryObj.add(previous, params[i]);
                previous = null;
            } else {
                previous = params[i];
            }
        }
        findByQuery(queryObj, callback);
    }

    @Override
    public void findByQuery(Query query, Callback<Node[]> callback) {
        final long[] foundIds = get(query.hash());
        if (foundIds == null) {
            callback.on(new BaseNode[0]);
        } else {
            _graph.resolver().lookupAll(query.world(), query.time(), foundIds, new Callback<Node[]>() {
                @Override
                public void on(Node[] resolved) {
                    //select
                    Node[] resultSet = new BaseNode[foundIds.length];
                    int resultSetIndex = 0;
                    for (int i = 0; i < resultSet.length; i++) {
                        final org.mwg.Node resolvedNode = resolved[i];
                        if (resolvedNode != null) {
                            final NodeState resolvedState = _graph.resolver().resolveState(resolvedNode);
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
        }
    }

    @Override
    public long[] all() {
        long[] flat = new long[(int) size()];
        final int[] i = {0};
        this.each(new LongLongArrayMapCallBack() {
            @Override
            public void on(long key, long value) {
                flat[i[0]] = value;
                i[0]++;
            }
        });
        return flat;
    }


}
