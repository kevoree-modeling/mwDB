package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.core.utility.CoreDeferCounter;
import org.mwg.base.BaseNode;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionJump implements Action {

    private final String _time;

    ActionJump(final String time) {
        _time = time;
    }

    @Override
    public void eval(final TaskContext context) {
        final String flatTime = context.template(_time);
        long parsedTime;
        try {
            parsedTime = Long.parseLong(flatTime);
        }catch(Throwable t) {
            Double d = Double.parseDouble(flatTime);
            parsedTime = d.longValue();
        }
        final TaskResult previous = context.result();
        final DeferCounter defer = new CoreDeferCounter(previous.size());
        final int previousSize = previous.size();
        for (int i = 0; i < previousSize; i++) {
            Object loopObj = previous.get(i);
            if (loopObj instanceof BaseNode) {
                Node castedPreviousNode = (Node) loopObj;
                final int finalIndex = i;
                castedPreviousNode.jump(parsedTime, new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        castedPreviousNode.free();
                        previous.set(finalIndex, result);
                        defer.count();
                    }
                });
            } else {
                defer.count();
            }
        }
        defer.then(new Job() {
            @Override
            public void run() {
                context.continueTask();
            }
        });
    }

    @Override
    public String toString() {
        return "jump(\'" + _time + "\')";
    }
}
