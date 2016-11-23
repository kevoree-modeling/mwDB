package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionSetWorld implements Action {

    private final String _varName;

    ActionSetWorld(final String p_varName) {
        this._varName = p_varName;
    }

    @Override
    public void eval(final TaskContext context) {
        final String flat = context.template(_varName);
        context.setWorld(Long.parseLong(flat));
        context.continueTask();
    }

    @Override
    public String toString() {
        return "setWorld(\'" + _varName + "\')";
    }

}
