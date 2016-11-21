package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionConditional;
import org.mwg.task.TaskResult;

class CF_ActionIfThen extends AbstractAction {

    private TaskFunctionConditional _condition;
    private org.mwg.task.Task _action;

    CF_ActionIfThen(final TaskFunctionConditional cond, final org.mwg.task.Task action) {
        super();
        if (cond == null) {
            throw new RuntimeException("condition should not be null");
        }
        if (action == null) {
            throw new RuntimeException("subTask should not be null");
        }
        this._condition = cond;
        this._action = action;
    }

    @Override
    public void eval(final TaskContext context) {
        if (_condition.eval(context)) {
            _action.executeFrom(context, context.result(),SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult res) {
                    context.continueWith(res);
                }
            });
        } else {
            context.continueTask();
        }
    }

    @Override
    public String toString() {
        return "ifThen()";
    }

}
