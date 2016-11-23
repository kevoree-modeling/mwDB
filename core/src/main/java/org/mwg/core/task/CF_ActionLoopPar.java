package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class CF_ActionLoopPar implements Action {

    private final Task _subTask;

    private final String _lower;
    private final String _upper;

    CF_ActionLoopPar(final String p_lower, final String p_upper, final Task p_subTask) {
        this._subTask = p_subTask;
        this._lower = p_lower;
        this._upper = p_upper;
    }

    @Override
    public void eval(final TaskContext context) {
        final String lowerString = context.template(_lower);
        final String upperString = context.template(_upper);
        final int lower = (int) Double.parseDouble(context.template(lowerString));
        final int upper = (int) Double.parseDouble(context.template(upperString));
        final TaskResult previous = context.result();
        final TaskResult next = context.newResult();
        if ((upper - lower) > 0) {
            DeferCounter waiter = context.graph().newCounter((upper - lower) + 1);
            for (int i = lower; i <= upper; i++) {
                final int finalI = i;
                _subTask.executeFromUsing(context, previous, SchedulerAffinity.ANY_LOCAL_THREAD, new Callback<TaskContext>() {
                    @Override
                    public void on(TaskContext result) {
                        result.defineVariable("i", finalI);
                    }
                }, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        if (result != null && result.size() > 0) {
                            for (int i = 0; i < result.size(); i++) {
                                next.add(result.get(i));
                            }
                        }
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
        } else {
            context.continueWith(next);
        }
    }

    @Override
    public String toString() {
        return "loopPar(\'" + _lower + "\',\'" + _upper + "\')";
    }

}
