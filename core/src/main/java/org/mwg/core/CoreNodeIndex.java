package org.mwg.core;

import org.mwg.*;
import org.mwg.base.BaseNode;
import org.mwg.struct.IndexedRelationship;

class CoreNodeIndex extends BaseNode implements NodeIndex {

    static final String NAME = "NodeIndex";

    CoreNodeIndex(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    @Override
    public void init() {
        getOrCreate(CoreConstants.INDEX_ATTRIBUTE, Type.INDEXED_RELATION);
    }

    @Override
    public long size() {
        return ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).size();
    }

    @Override
    public long[] all() {
        return ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).all();
    }

    @Override
    public NodeIndex add(Node node, String... attributeNames) {
        ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).add(node, attributeNames);
        return this;
    }

    @Override
    public NodeIndex remove(Node node, String... attributeNames) {
        ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).remove(node, attributeNames);
        return this;
    }

    @Override
    public NodeIndex clear() {
        ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).clear();
        return this;
    }

    @Override
    public void findAll(Callback<Node[]> callback) {
        long[] flat = ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).all();
        graph().lookupAll(world(), time(), flat, callback);
    }

    @Override
    public void find(String query, Callback<Node[]> callback) {
        ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).find(query, callback);
    }

    @Override
    public void findUsing(Callback<Node[]> callback, String... params) {
        ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).findUsing(callback, params);
    }

    @Override
    public void findByQuery(Query query, Callback<Node[]> callback) {
        ((IndexedRelationship) get(CoreConstants.INDEX_ATTRIBUTE)).findByQuery(query, callback);
    }

}
