package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionAddToVar implements Action {

    private final String _name;

    ActionAddToVar(final String p_name) {
        if (p_name == null) {
            throw new RuntimeException("variableName should not be null");
        }
        this._name = p_name;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        if (context.isGlobal(_name)) {
            //TODO refactor this
            context.addToGlobalVariable(context.template(_name), previousResult);
        } else {
            context.addToVariable(context.template(_name), previousResult);
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        return "addToVar(\'" + _name + "\')";
    }

}
