package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionLookup implements Action {

    private final String _id;

    ActionLookup(final String p_id) {
        this._id = p_id;
    }

    @Override
    public void eval(final TaskContext context) {
        final long idL = Long.parseLong(context.template(_id));
        context.graph().lookup(context.world(), context.time(), idL, new Callback<Node>() {
            @Override
            public void on(Node result) {
                context.continueWith(context.wrap(result));
            }
        });
    }

    @Override
    public String toString() {
        return "lookup(\'" + _id + "\")";
    }

}
