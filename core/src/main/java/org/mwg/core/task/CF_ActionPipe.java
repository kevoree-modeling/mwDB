package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class CF_ActionPipe extends CF_Action {

    private final Task[] _subTasks;

    CF_ActionPipe(final Task... p_subTasks) {
        super();
        _subTasks = p_subTasks;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previous = ctx.result();
        final AtomicInteger cursor = new AtomicInteger(0);
        final int tasksSize = _subTasks.length;
        final TaskResult next;
        if (tasksSize > 1) {
            next = ctx.newResult();
            next.allocate(tasksSize);
        } else {
            next = null;
        }
        final Callback<TaskResult>[] loopcb = new Callback[1];
        loopcb[0] = new Callback<TaskResult>() {
            @Override
            public void on(final TaskResult result) {
                Exception exceptionDuringTask = null;
                final int current = cursor.getAndIncrement();
                if (result != null) {
                    if (tasksSize > 1) {
                        next.add(result);
                    }
                    if (result.output() != null) {
                        ctx.append(result.output());
                    }
                    if (result.exception() != null) {
                        exceptionDuringTask = result.exception();
                    }
                }
                if (current < tasksSize && exceptionDuringTask == null) {
                    _subTasks[current].executeFrom(ctx, previous, SchedulerAffinity.SAME_THREAD, loopcb[0]);
                } else {
                    //end
                    TaskResult nextResult;
                    if (tasksSize > 1) {
                        nextResult = next;
                    } else {
                        nextResult = result;
                    }
                    if (exceptionDuringTask != null) {
                        ctx.endTask(nextResult, exceptionDuringTask);
                    } else {
                        ctx.continueWith(nextResult);
                    }
                }
            }
        };
        final int current = cursor.getAndIncrement();
        if (current < tasksSize) {
            _subTasks[current].executeFrom(ctx, previous, SchedulerAffinity.SAME_THREAD, loopcb[0]);
        } else {
            ctx.continueWith(next);
        }
    }

    @Override
    public Task[] children() {
        return _subTasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        builder.append(ActionNames.PIPE);
        builder.append(Constants.TASK_PARAM_OPEN);
        for (int i = 0; i < _subTasks.length; i++) {
            if (i != 0) {
                builder.append(Constants.TASK_PARAM_SEP);
            }
            final CoreTask castedAction = (CoreTask) _subTasks[i];
            final int castedActionHash = castedAction.hashCode();
            if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                builder.append(Constants.SUB_TASK_OPEN);
                castedAction.serialize(builder, dagIDS);
                builder.append(Constants.SUB_TASK_CLOSE);
            } else {
                builder.append("" + dagIDS.get(castedActionHash));
            }
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
