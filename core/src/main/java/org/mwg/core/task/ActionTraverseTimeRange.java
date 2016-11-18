package org.mwg.core.task;

import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;

class ActionTraverseTimeRange extends AbstractAction {

    private final String _from;
    private final String _to;

    ActionTraverseTimeRange(final String from, final String to) {
        super();
        _from = from;
        _to = to;
    }

    @Override
    public void eval(final TaskContext context) {
        /*
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
        */
    }

    @Override
    public String toString() {
        return "traverseTimeRange(\'" + _from + "," + _to + "\')";
    }
}
