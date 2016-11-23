package org.mwg.structure.action;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.plugin.Job;
import org.mwg.structure.NTree;
import org.mwg.structure.tree.KDTree;
import org.mwg.structure.tree.NDTree;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

public class NTreeInsertTo implements Action {

    public static String NAME = "nTreeInsertTo";

    private final String _variableName;

    public NTreeInsertTo(final String variableName) {
        this._variableName = variableName;
    }

    @Override
    public final void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResult savedVar = context.variable(context.template(_variableName));
        if (previousResult != null && savedVar != null) {
            final DeferCounter defer = context.graph().newCounter(previousResult.size());
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
                if (iter instanceof BaseNode) {
                    final TaskResultIterator savedVarIt = savedVar.iterator();
                    Object toAddIter = savedVarIt.next();
                    while (toAddIter != null) {
                        if (toAddIter instanceof KDTree || toAddIter instanceof NDTree) {
                            ((NTree) toAddIter).insert((Node) iter, new Callback<Boolean>() {
                                @Override
                                public void on(Boolean result) {
                                    defer.count();
                                }
                            });
                        } else {
                            defer.count();
                        }
                        toAddIter = savedVarIt.next();
                    }
                } else {
                    defer.count();
                }
                iter = previousResultIt.next();
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    context.continueTask();
                }
            });
        } else {
            context.continueTask();
        }
    }

    @Override
    public String toString() {
        return "nTreeInsertTo(\'" + _variableName + "\')";
    }

}