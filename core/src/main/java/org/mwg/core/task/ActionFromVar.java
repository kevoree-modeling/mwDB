package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionFromVar extends AbstractAction {

    private final String _name;
    private final int _index;

    ActionFromVar(String p_name, int p_index) {
        super();
        this._name = p_name;
        this._index = p_index;
    }

    @Override
    public void eval(final TaskContext context) {
        final String evaluatedName = context.template(_name);
        TaskResult varResult;
        if(_index != -1) {
            varResult = context.wrap(context.variable(evaluatedName).get(_index));
        } else {
            varResult = context.variable(evaluatedName);
        }
        if(varResult != null){
            varResult = varResult.clone();
        }
        context.continueWith(varResult);
    }

    @Override
    public String toString() {
        return "fromVar(\'" + _name + "\')";
    }

}
