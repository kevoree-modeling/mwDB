package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

class ActionForEach extends AbstractAction {

    private final Task _subTask;

    ActionForEach(final Task p_subTask) {
        super();
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext context) {
        final ActionForEach selfPointer = this;
        final TaskResult previousResult = context.result();
        if (previousResult == null) {
            context.continueTask();
        } else {
            final TaskResultIterator it = previousResult.iterator();
            final Callback[] recursiveAction = new Callback[1];
            recursiveAction[0] = new Callback<TaskResult>() {
                @Override
                public void on(final TaskResult res) {
                    //we don't keep result
                    if (res != null) {
                        res.free();
                    }
                    final Tuple<Integer, Object> nextResult = it.nextWithIndex();
                    if (nextResult == null) {
                        context.continueTask();
                    } else {
                        selfPointer._subTask.executeFromUsing(context, context.wrap(nextResult.right()), SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                            @Override
                            public void on(TaskContext result) {
                                result.defineVariable("i", nextResult.left());
                            }
                        }, recursiveAction[0]);
                    }
                }
            };
            final Tuple<Integer, Object> nextRes = it.nextWithIndex();
            if (nextRes != null) {
                _subTask.executeFromUsing(context, context.wrap(nextRes.right()), SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                    @Override
                    public void on(TaskContext result) {
                        result.defineVariable("i", nextRes.left());
                    }
                }, recursiveAction[0]);
            } else {
                context.continueTask();
            }
        }
    }

    @Override
    public String toString() {
        return "foreach()";
    }


}
