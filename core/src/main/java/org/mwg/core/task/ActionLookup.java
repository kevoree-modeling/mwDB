package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionLookup extends AbstractAction {

    private final String _id;

    ActionLookup(final String p_id) {
        super();
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
