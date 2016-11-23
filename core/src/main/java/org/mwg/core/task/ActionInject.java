package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionInject implements Action {

    private final Object _value;

    ActionInject(final Object value) {
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
