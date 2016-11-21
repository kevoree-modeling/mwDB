package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionDeclareVar extends AbstractAction {

    private final String _name;

    ActionDeclareVar(final String p_name) {
        super();
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
