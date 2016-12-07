package org.mwg.core.task;

import jdk.nashorn.internal.ir.BaseNode;
import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.core.utility.CoreDeferCounter;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.Job;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionTimepoints extends AbstractTaskAction {
    private final String _from;
    private final String _to;
    ActionTimepoints(String from, String to) {
        super();
        this._to = to;
        this._from = from;
    }


    @Override
    public void eval(TaskContext context) {
        final TaskResult previous  = context.result();
        String tFrom = context.template(_from);
        String tTo = context.template(_to);

        long parsedFrom;
        long parsedTo;

        try {
            parsedFrom = Long.parseLong(tFrom);
        } catch (Throwable t) {
            Double d = Double.parseDouble(tFrom);
            parsedFrom = d.longValue();
        }

        try {
            parsedTo = Long.parseLong(tTo);
        } catch (Throwable t) {
            Double d = Double.parseDouble(tTo);
            parsedTo = d.longValue();
        }

        final TaskResult next = context.newResult();

        if(previous != null) {
            DeferCounter defer = new CoreDeferCounter(previous.size());
            for(int i=0;i<previous.size();i++) {
                if(previous.get(i) instanceof BaseNode) {
                    final Node casted = (Node) previous.get(i);
                    casted.timepoints(parsedFrom, parsedTo, new Callback<long[]>() {
                        @Override
                        public void on(long[] result) {
                            for(int i=0;i<result.length;i++) {
                                next.add(result[i]);
                            }
                            casted.free();
                            defer.count();
                        }
                    });
                }
            }

            defer.then(new Job() {
                @Override
                public void run() {
                    previous.clear();
                    context.continueWith(next);
                }
            });
        } else {
            context.continueWith(next);
        }
    }
}
