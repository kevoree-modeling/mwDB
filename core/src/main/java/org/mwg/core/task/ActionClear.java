package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionClear extends AbstractAction {

    @Override
    public void eval(final TaskContext context) {
        context.continueWith(context.newResult());
    }

    @Override
    public String toString() {
        return "clear()";
    }

}
