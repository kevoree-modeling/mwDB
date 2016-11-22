package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionSetTime extends AbstractAction {

    private final String _varName;

    ActionSetTime(final String p_varName) {
        super();
        this._varName = p_varName;
    }

    @Override
    public void eval(final TaskContext context) {
        final String flat = context.template(_varName);
        long parsedTime;
        // TODO ugly hack to be JS transpilable, should be fixed without try/catch
        try {
            parsedTime = Long.parseLong(flat);
        } catch (NumberFormatException ex) {
            parsedTime = (long) Double.parseDouble(flat);
        }
        context.setTime(parsedTime);
        context.continueTask();
    }

    @Override
    public String toString() {
        return "setTime(\'" + _varName + "\')";
    }

}

