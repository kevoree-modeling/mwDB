package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.Job;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionGet extends AbstractAction {

    private final String _name;

    ActionGet(final String p_name) {
        super();
        this._name = p_name;
    }

    @Override
    public final void eval(final TaskContext context) {
        final TaskResult finalResult = context.newResult();
        final String flatName = context.template(_name);
        final TaskResult previousResult = context.result();
        if (previousResult != null) {
            final int previousSize = previousResult.size();
            final DeferCounter defer = context.graph().newCounter(previousSize);
            for (int i = 0; i < previousSize; i++) {
                final Object loop = previousResult.get(i);
                if (loop instanceof BaseNode) {
                    final Node casted = (Node) loop;
                    if (casted.type(flatName) == Type.RELATION) {
                        casted.rel(flatName, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result) {
                                if (result != null) {
                                    for (int j = 0; j < result.length; j++) {
                                        finalResult.add(result[j]);
                                    }
                                }
                                casted.free();
                                defer.count();
                            }
                        });
                    } else {
                        Object resolved = casted.get(flatName);
                        if (resolved != null) {
                            finalResult.add(resolved);
                        }
                        casted.free();
                        defer.count();
                    }
                } else {
                    //TODO add closable management
                    finalResult.add(loop);
                    defer.count();
                }
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    //optimization to avoid iterating again on previous result set
                    previousResult.clear();
                    context.continueWith(finalResult);
                }
            });
        } else {
            context.continueTask();
        }
    }

    @Override
    public String toString() {
        return "get(\'" + _name + "\')";
    }
}