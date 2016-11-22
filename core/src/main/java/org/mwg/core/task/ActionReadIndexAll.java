package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionReadIndexAll extends AbstractAction {

    private final String _indexName;

    ActionReadIndexAll(final String p_indexName) {
        super();
        if (p_indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        _indexName = p_indexName;
    }

    @Override
    public void eval(final TaskContext context) {
        /*
        String templatedINdexName = context.template(_indexName);
        context.graph().findAll(context.world(), context.time(), templatedINdexName, new Callback<Node[]>() {
            @Override
            public void on(Node[] result) {
                context.continueWith(context.wrap(result));
            }
        });*/
    }

    @Override
    public String toString() {
        return "readIndexAll(\'" + _indexName + "\')";
    }

}
