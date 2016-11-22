package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionDefineAsVar extends AbstractAction {

    private final String _name;
    private final boolean _global;

    ActionDefineAsVar(final String p_name, final boolean p_global) {
        super();
        if (p_name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = p_name;
        this._global = p_global;
    }

    @Override
    public void eval(final TaskContext context) {
        if (_global) {
            context.setGlobalVariable(context.template(_name), context.result());
        } else {
            context.defineVariable(context.template(_name), context.result());
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        if(_global){
            return "defineAsGlobalVar(\'" + _name + "\')";
        } else {
            return "defineAsVar(\'" + _name + "\')";
        }
    }

}
