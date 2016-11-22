package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.NodeIndex;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionReadGlobalIndexAll extends AbstractAction {

    private final String _indexName;

    ActionReadGlobalIndexAll(final String p_indexName) {
        super();
        if (p_indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        _indexName = p_indexName;
    }

    @Override
    public void eval(final TaskContext context) {
        String name = context.template(_indexName);
        context.graph().index(context.world(), context.time(), name, new Callback<NodeIndex>() {
            @Override
            public void on(NodeIndex resolvedIndex) {
                resolvedIndex.findAll(new Callback<Node[]>() {
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
        return "readIndexAll(\'" + _indexName + "\')";
    }

}
