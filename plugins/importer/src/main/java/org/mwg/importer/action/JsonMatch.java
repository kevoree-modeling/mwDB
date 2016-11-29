package org.mwg.importer.action;

import org.mwg.Callback;
import org.mwg.importer.util.JsonMemberResult;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

public class JsonMatch implements Action {

    private String _name;
    private org.mwg.task.Task _then;

    public JsonMatch(String name, Task then) {
        this._name = name;
        this._then = then;
    }

    @Override
    public void eval(TaskContext context) {
        TaskResult result = context.result();
        if (result instanceof JsonMemberResult) {
            if (_name.equals(result.get(0).toString())) {
                _then.executeFrom(context, (TaskResult) result.get(1), SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult res) {
                        context.continueWith(res);
                    }
                });
            } else {
                context.continueTask();
            }
        } else {
            context.continueTask();
        }
    }

}
