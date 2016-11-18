package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionSave extends AbstractAction {

    @Override
    public void eval(final TaskContext context) {
        context.graph().save(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                context.continueTask();
            }
        });
    }

    @Override
    public String toString() {
        return "save()";
    }

}
