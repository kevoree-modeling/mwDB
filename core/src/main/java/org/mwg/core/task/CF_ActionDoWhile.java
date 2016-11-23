package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

class CF_ActionDoWhile implements Action {

    private final TaskFunctionConditional _cond;
    private final Task _then;

    CF_ActionDoWhile(final Task p_then, final TaskFunctionConditional p_cond) {
        this._cond = p_cond;
        this._then = p_then;
    }

    @Override
    public void eval(final TaskContext context) {
        final CoreTaskContext coreTaskContext = (CoreTaskContext) context;
        final CF_ActionDoWhile selfPointer = this;
        final Callback[] recursiveAction = new Callback[1];
        recursiveAction[0] = new Callback<TaskResult>() {
            @Override
            public void on(final TaskResult res) {
                final TaskResult previous = coreTaskContext._result;
                coreTaskContext._result = res;
                if (_cond.eval(context)) {
                    if (previous != null) {
                        previous.free();
                    }
                    selfPointer._then.executeFrom(context, ((CoreTaskContext) context)._result, SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                } else {
                    if (previous != null) {
                        previous.free();
                    }
                    context.continueWith(res);
                }
            }
        };
        _then.executeFrom(context, coreTaskContext._result, SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
    }

    @Override
    public String toString() {
        return "doWhile()";
    }

}
