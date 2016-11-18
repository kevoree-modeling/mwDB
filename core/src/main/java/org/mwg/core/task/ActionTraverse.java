package org.mwg.core.task;

import org.mwg.*;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.Job;
import org.mwg.struct.Relationship;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.ArrayList;

class ActionTraverse extends AbstractAction {

    private final String _name;

    ActionTraverse(final String p_name) {
        super();
        this._name = p_name;
    }


    @Override
    public final void eval(final TaskContext context) {

        final TaskResult previousResult = context.result();

        if (previousResult != null) {
            final int previousSize = previousResult.size();
            boolean isConsistent = true;
            long world = Constants.NULL_LONG;
            long time = Constants.NULL_LONG;
            for (int i = 0; i < previousSize; i++) {
                final Object loop = previousResult.get(i);
                if (loop instanceof BaseNode) {
                    final Node casted = (Node) loop;
                    if (time == Constants.NULL_LONG) {
                        time = casted.time();
                        world = casted.world();
                    } else {
                        if (casted.world() != world || casted.time() != time) {
                            isConsistent = false;
                            break;
                        }
                    }

                }
            }
            final String flatName = context.template(_name);
            if (isConsistent) {
                //lookupAll usage
                final ArrayList<Long> collected = new ArrayList<Long>();
                for (int i = 0; i < previousSize; i++) {
                    final Object loop = previousResult.get(i);
                    if (loop instanceof BaseNode) {
                        final Node casted = (Node) loop;
                        if (casted.type(flatName) == Type.RELATION) {
                            Relationship flatRel = (Relationship) casted.get(flatName);
                            if (flatRel != null) {
                                for (int j = 0; j < flatRel.size(); j++) {
                                    collected.add(flatRel.get(j));
                                }
                            }
                        }
                        casted.free();
                    }
                }
                previousResult.clear();
                long[] flatCollected = new long[collected.size()];
                for (int i = 0; i < collected.size(); i++) {
                    flatCollected[i] = collected.get(i);
                }
                context.graph().lookupAll(world, time, flatCollected, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        context.continueWith(context.wrap(result));
                    }
                });
            } else {
                //many loopup usage
                final TaskResult finalResult = context.newResult();
                final DeferCounter defer = context.graph().newCounter(previousSize);
                for (int i = 0; i < previousSize; i++) {
                    final Object loop = previousResult.get(i);
                    if (loop instanceof BaseNode) {
                        final Node casted = (Node) loop;
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
                        finalResult.add(loop);
                        defer.count();
                    }
                }
                defer.then(new Job() {
                    @Override
                    public void run() {
                        //optimization to avoid agin iteration on the previous result set
                        previousResult.clear();
                        context.continueWith(finalResult);
                    }
                });
            }
        } else {
            context.continueTask();
        }
    }

    @Override
    public String toString() {
        return "traverse(\'" + _name + "\')";
    }

}