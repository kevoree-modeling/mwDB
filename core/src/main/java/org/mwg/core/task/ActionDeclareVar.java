package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionDeclareVar implements Action {

    private final String _name;

    ActionDeclareVar(final String p_name) {
        if (p_name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = p_name;
    }

    @Override
    public void eval(final TaskContext context) {
        context.declareVariable(context.template(_name));
        context.continueTask();
    }

    @Override
    public String toString() {
        return "declareVar(\'" + _name + "\')";
    }

}
