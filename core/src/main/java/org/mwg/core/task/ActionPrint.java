package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionPrint extends AbstractAction {

    private final String _name;

    private final boolean _withLineBreak;

    ActionPrint(final String p_name, boolean withLineBreak) {
        super();
        this._name = p_name;
        this._withLineBreak = withLineBreak;
    }

    @Override
    public void eval(final TaskContext context) {
        if (_withLineBreak) {
            System.out.println(context.template(_name));
        } else {
            System.out.print(context.template(_name));
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        return "print(\'" + _name + "\')";
    }

}
