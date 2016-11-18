package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionInject extends AbstractAction {

    private final Object _value;

    ActionInject(final Object value) {
        super();
        if (value == null) {
            throw new RuntimeException("inputValue should not be null");
        }
        this._value = value;
    }

    @Override
    public void eval(final TaskContext context) {
        context.continueWith(context.wrap(_value).clone());
    }

    @Override
    public String toString() {
        return "inject()";
    }

}
