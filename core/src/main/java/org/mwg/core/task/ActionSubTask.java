package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionSubTask implements Action {

    private final Task _subTask;

    ActionSubTask(final Task p_subTask) {
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
                context.continueWith(subTaskResult);
            }
        });
    }

    public String serialize() {
        //todo DAG is not managed!!
        return "subTask(" + _subTask.toString() + ")";
    }

    @Override
    public String toString() {
        return "subTask()";
    }

}
