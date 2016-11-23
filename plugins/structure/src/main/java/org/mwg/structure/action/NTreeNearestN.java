package org.mwg.structure.action;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.plugin.Job;
import org.mwg.structure.NTree;
import org.mwg.structure.tree.KDTree;
import org.mwg.structure.tree.NDTree;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

public class NTreeNearestN implements Action {

    public static String NAME = "nTreeNearestN";

    private final double[] _key;
    private final int _n;

    public NTreeNearestN(final double[] key, final int n) {
        this._key = key;
        this._n = n;
    }

    @Override
    public final void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResult nextResult = context.newResult();
        if (previousResult != null) {
            final DeferCounter defer = context.graph().newCounter(previousResult.size());
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
                if (iter instanceof KDTree || iter instanceof NDTree) {
                    ((NTree) iter).nearestN(_key, _n, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            for (int i = 0; i < result.length; i++) {
                                if (result[i] != null) {
                                    nextResult.add(result[i]);
                                }
                            }
                            defer.count();
                        }
                    });
                } else {
                    defer.count();
                }
                iter = previousResultIt.next();
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    context.continueWith(nextResult);
                }
            });
        } else {
            context.continueWith(nextResult);
        }
    }

    @Override
    public String toString() {
        return "nTreeNearestN(\'" + "\')";
    }

}