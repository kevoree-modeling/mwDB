package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class CF_ActionIsolate extends AbstractAction {

    private final Task _subTask;

    CF_ActionIsolate(final Task p_subTask) {
        super();
        if (p_subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previous = context.result();
        _subTask.executeFrom(context, previous, SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult subTaskResult) {
                if(subTaskResult != null){
                    subTaskResult.free();
                }
                context.continueWith(previous);
            }
        });
    }

    @Override
    public String toString() {
        return "subTask()";
    }

}
