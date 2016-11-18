package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.concurrent.atomic.AtomicInteger;

class ActionSubTasks extends AbstractAction {

    private final Task[] _subTasks;

    ActionSubTasks(final Task... p_subTasks) {
        super();
        _subTasks = p_subTasks;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previous = context.result();
        final AtomicInteger cursor = new AtomicInteger(0);
        final int tasksSize = _subTasks.length;
        final TaskResult next = context.newResult();
        final Callback<TaskResult>[] loopcb = new Callback[1];
        loopcb[0] = new Callback<TaskResult>() {
            @Override
            public void on(final TaskResult result) {
                final int current = cursor.getAndIncrement();
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        final Object loop = result.get(i);
                        if (loop != null) {
                            next.add(loop);
                        }
                    }
                }
                if (current < tasksSize) {
                    _subTasks[current].executeFrom(context, previous, SchedulerAffinity.SAME_THREAD, loopcb[0]);
                } else {
                    //end
                    context.continueWith(next);
                }
            }
        };
        final int current = cursor.getAndIncrement();
        if (current < tasksSize) {
            _subTasks[current].executeFrom(context, previous, SchedulerAffinity.SAME_THREAD, loopcb[0]);
        } else {
            context.continueWith(next);
        }
    }

    @Override
    public String toString() {
        return "subTasks()";
    }

}
