package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionDefineAsVar extends AbstractAction {

    private final String _name;

    ActionDefineAsVar(final String p_name) {
        super();
        if (p_name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = p_name;
    }

    @Override
    public void eval(final TaskContext context) {
        context.defineVariable(context.template(_name), context.result());
        context.continueTask();
    }

    @Override
    public String toString() {
        return "defineAsVar(\'" + _name + "\')";
    }

}
