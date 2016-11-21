package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionReadIndex extends AbstractAction {

    private final String _indexName;
    private final String _query;

    ActionReadIndex(final String p_indexName, final String p_query) {
        super();
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
        final String flatIndexName = context.template(_indexName);
        final String flatQuery = context.template(_query);
        context.graph().find(context.world(), context.time(), flatIndexName, flatQuery, new Callback<Node[]>() {
            @Override
            public void on(Node[] result) {
                context.continueWith(context.wrap(result));
            }
        });
    }

    @Override
    public String toString() {
        return "readIndex(\'" + _indexName + "\'" + Constants.QUERY_SEP + "\'" + _query + "\')";
    }

}
