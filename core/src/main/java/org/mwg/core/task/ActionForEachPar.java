package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

class ActionForEachPar extends AbstractAction {

    private final Task _subTask;

    ActionForEachPar(final Task p_subTask) {
        super();
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResultIterator it = previousResult.iterator();
        final int previousSize = previousResult.size();
        if (previousSize == -1) {
            throw new RuntimeException("Foreach on non array structure are not supported yet!");
        }
        final DeferCounter waiter = context.graph().newCounter(previousSize);
        final Job[] dequeueJob = new Job[1];
        dequeueJob[0] = new Job() {
            @Override
            public void run() {
                final Tuple<Integer, Object> loop = it.nextWithIndex();
                if (loop != null) {
                    _subTask.executeFromUsing(context, context.wrap(loop.right()), SchedulerAffinity.ANY_LOCAL_THREAD, new Callback<TaskContext>() {
                        @Override
                        public void on(TaskContext result) {
                            result.defineVariable("i", loop.left());
                        }
                    }, new Callback<TaskResult>() {
                        @Override
                        public void on(TaskResult result) {
                            if (result != null) {
                                result.free();
                            }
                            waiter.count();
                            dequeueJob[0].run();
                        }
                    });
                }
            }
        };
        final int nbThread = context.graph().scheduler().workers();
        for (int i = 0; i < nbThread; i++) {
            dequeueJob[0].run();
        }
        waiter.then(new Job() {
            @Override
            public void run() {
                context.continueTask();
            }
        });
    }

    @Override
    public String toString() {
        return "foreachPar()";
    }

}
