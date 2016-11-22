package org.mwg.core.chunk.heap;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.Query;
import org.mwg.plugin.NodeState;
import org.mwg.struct.IndexedRelationship;
import org.mwg.struct.LongLongArrayMapCallBack;

public class HeapIndexedRelationship extends HeapLongLongArrayMap implements IndexedRelationship {

    HeapIndexedRelationship(HeapStateChunk p_listener) {
        super(p_listener);
    }

    @Override
    public IndexedRelationship add(Node node, String... attributeNames) {
        internal_add_remove(false, node, attributeNames);

        return this;
    }

    @Override
    public IndexedRelationship remove(Node node, String... attributeNames) {
        internal_add_remove(true, node, attributeNames);
        return this;
    }

    private void internal_add_remove(boolean isIndex, Node node, String... attributeNames) {
        Query flatQuery = node.graph().newQuery();
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
            remove(flatQuery.hash(), node.id());
        }
    }

    @Override
    public IndexedRelationship clear() {
        //TODO
        return this;
    }

    @Override
    public IndexedRelationship find(String query, Callback<Node[]> callback) {
        //TODO
        return null;
    }

    @Override
    public long[] all() {
        long[] flat = new long[(int) size()];
        final int[] i = {0};
        this.each(new LongLongArrayMapCallBack() {
            @Override
            public void on(long key, long value) {
                flat[i[0]] = key;
                i[0]++;
            }
        });
        return flat;
    }

    HeapIndexedRelationship cloneIRelFor(HeapStateChunk newParent) {
        HeapIndexedRelationship cloned = new HeapIndexedRelationship(newParent);
        cloned.mapSize = mapSize;
        cloned.capacity = capacity;
        if (keys != null) {
            long[] cloned_keys = new long[capacity];
            System.arraycopy(keys, 0, cloned_keys, 0, capacity);
            cloned.keys = cloned_keys;
        }
        if (values != null) {
            long[] cloned_values = new long[capacity];
            System.arraycopy(values, 0, cloned_values, 0, capacity);
            cloned.values = cloned_values;
        }
        if (nexts != null) {
            int[] cloned_nexts = new int[capacity];
            System.arraycopy(nexts, 0, cloned_nexts, 0, capacity);
            cloned.nexts = cloned_nexts;
        }
        if (hashs != null) {
            int[] cloned_hashs = new int[capacity * 2];
            System.arraycopy(hashs, 0, cloned_hashs, 0, capacity * 2);
            cloned.hashs = cloned_hashs;
        }
        return cloned;
    }

}
