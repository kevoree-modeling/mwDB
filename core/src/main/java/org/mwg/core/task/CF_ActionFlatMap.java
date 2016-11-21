package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

class CF_ActionFlatMap extends AbstractAction {

    private final Task _subTask;

    CF_ActionFlatMap(final Task p_subTask) {
        super();
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext context) {
        final CF_ActionFlatMap selfPointer = this;
        final TaskResult previousResult = context.result();
        if (previousResult == null) {
            context.continueTask();
        } else {
            final TaskResultIterator it = previousResult.iterator();
            final TaskResult finalResult = context.newResult();
            finalResult.allocate(previousResult.size());
            final Callback[] recursiveAction = new Callback[1];
            final TaskResult[] loopRes = new TaskResult[1];
            recursiveAction[0] = new Callback<TaskResult>() {
                @Override
                public void on(final TaskResult res) {
                    if (res != null) {
                        for (int i = 0; i < res.size(); i++) {
                            finalResult.add(res.get(i));
                        }
                    }
                    loopRes[0].free();
                    final Tuple<Integer, Object> nextResult = it.nextWithIndex();
                    if (nextResult != null) {
                        loopRes[0] = context.wrap(nextResult.right());
                    } else {
                        loopRes[0] = null;
                    }
                    if (nextResult == null) {
                        context.continueWith(finalResult);
                    } else {
                        selfPointer._subTask.executeFromUsing(context, loopRes[0], SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
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
                loopRes[0] = context.wrap(nextRes.right());
                context.graph().scheduler().dispatch(SchedulerAffinity.SAME_THREAD, new Job() {
                    @Override
                    public void run() {
                        _subTask.executeFromUsing(context, loopRes[0], SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                            @Override
                            public void on(TaskContext result) {
                                result.defineVariable("i", nextRes.left());
                            }
                        }, recursiveAction[0]);
                    }
                });
            } else {
                context.continueWith(finalResult);
            }
        }
    }

    @Override
    public String toString() {
        return "flatMap()";
    }


}
