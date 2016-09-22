package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionSelectNew;
import org.mwg.task.TaskResult;

/**
 * Created by ludovicmouline on 22/09/16.
 */
class ActionSelectNew extends AbstractTaskAction {
    private final TaskFunctionSelectNew _filter;
    ActionSelectNew(TaskFunctionSelectNew filter) {
        super();
        this._filter = filter;
    }

    @Override
    public void eval(TaskContext context) {
        final TaskResult previous = context.result();
        final TaskResult<Object> next = context.newResult();
        final int previousSize = previous.size();
        for (int i = 0; i < previousSize; i++) {
            final Object obj = previous.get(i);
            if (obj instanceof AbstractNode) {
                final Node casted = (Node) obj;
                if (_filter.select(casted,context)) {
                    next.add(casted);
                } else {
                    casted.free();
                }
            } else {
                next.add(obj);
            }
        }
        //optimization to avoid the need to clone selected nodes
        previous.clear();
        context.continueWith(next);
    }
}
