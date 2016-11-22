package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionSetAsVar extends AbstractAction {

    private final String _name;

    ActionSetAsVar(final String p_name) {
        super();
        if (p_name == null) {
            throw new RuntimeException("variableName should not be null");
        }
        this._name = p_name;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        if (context.isGlobal(_name)) {
            context.setGlobalVariable(context.template(_name), previousResult);
        } else {
            context.setVariable(context.template(_name), previousResult);
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        return "setAsVar(\'" + _name + "\')";
    }

}
