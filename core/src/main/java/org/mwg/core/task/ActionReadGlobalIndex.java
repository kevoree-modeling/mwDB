package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.NodeIndex;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionReadGlobalIndex implements Action {

    private final String _indexName;
    private final String _query;

    ActionReadGlobalIndex(final String p_indexName, final String p_query) {
        if (p_indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        if (p_query == null) {
            throw new RuntimeException("query should not be null");
        }
        _indexName = p_indexName;
        _query = p_query;
    }

    @Override
    public void eval(final TaskContext context) {
        final String name = context.template(_indexName);
        final String query = context.template(_query);
        context.graph().index(context.world(), context.time(), name, new Callback<NodeIndex>() {
            @Override
            public void on(NodeIndex resolvedIndex) {
                resolvedIndex.find(query, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        resolvedIndex.free();
                        context.continueWith(context.wrap(result));
                    }
                });
            }
        });
    }

    @Override
    public String toString() {
        return "readIndex(\'" + _indexName + "\'" + Constants.QUERY_SEP + "\'" + _query + "\')";
    }

}
