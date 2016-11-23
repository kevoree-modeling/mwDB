package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionIndexNames implements Action {

    @Override
    public void eval(TaskContext context) {
        context.graph().indexNames(context.world(), context.time(), new Callback<String[]>() {
            @Override
            public void on(String[] result) {
                context.continueWith(context.wrap(result));
            }
        });
    }

    @Override
    public String toString() {
        return "indexNames()";
    }
}
