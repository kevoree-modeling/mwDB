package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.concurrent.atomic.AtomicInteger;

class CF_ActionLoop extends AbstractAction {

    private final Task _subTask;

    private final String _lower;
    private final String _upper;

    CF_ActionLoop(final String p_lower, final String p_upper, final Task p_subTask) {
        super();
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
        final CF_ActionLoop selfPointer = this;
        final AtomicInteger cursor = new AtomicInteger(lower);
        if ((upper - lower) >= 0) {
            final Callback[] recursiveAction = new Callback[1];
            recursiveAction[0] = new Callback<TaskResult>() {
                @Override
                public void on(final TaskResult res) {
                    final int current = cursor.getAndIncrement();
                    if (res != null) {
                        res.free();
                    }
                    if (current > upper) {
                        context.continueTask();
                    } else {
                        //recursive call
                        selfPointer._subTask.executeFromUsing(context, previous, SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                            @Override
                            public void on(TaskContext result) {
                                result.defineVariable("i", current);
                            }
                        }, recursiveAction[0]);
                    }
                }
            };
            _subTask.executeFromUsing(context, previous, SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                @Override
                public void on(TaskContext result) {
                    result.defineVariable("i", cursor.getAndIncrement());
                }
            }, recursiveAction[0]);
        } else {
            context.continueTask();
        }
    }

    @Override
    public String toString() {
        return "loop(\'" + _lower + "\',\'" + _upper + "\')";
    }

}
