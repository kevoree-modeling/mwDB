package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class CF_ActionMapPar extends AbstractAction {

    private final Task[] _subTasks;

    CF_ActionMapPar(final Task... p_subTasks) {
        super();
        _subTasks = p_subTasks;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previous = context.result();
        final TaskResult next = context.newResult();
        final int subTasksSize = _subTasks.length;
        next.allocate(subTasksSize);
        final DeferCounter waiter = context.graph().newCounter(subTasksSize);
        for (int i = 0; i < subTasksSize; i++) {
            int finalI = i;
            _subTasks[i].executeFrom(context, previous, SchedulerAffinity.ANY_LOCAL_THREAD, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult subTaskResult) {
                    next.set(finalI, subTaskResult);
                    waiter.count();
                }
            });
        }
        waiter.then(new Job() {
            @Override
            public void run() {
                context.continueWith(next);
            }
        });
    }

    @Override
    public String toString() {
        return "subTasksPar()";
    }

}
