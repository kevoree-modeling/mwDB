package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

class ActionAdd extends AbstractAction {

    private final String _relationName;
    private final String _variableNameToAdd;

    ActionAdd(final String relationName, final String variableNameToAdd) {
        super();
        this._relationName = relationName;
        this._variableNameToAdd = variableNameToAdd;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResult savedVar = context.variable(context.template(_variableNameToAdd));
        if (previousResult != null && savedVar != null) {
            final String relName = context.template(_relationName);
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
                if (iter instanceof BaseNode) {
                    final TaskResultIterator savedVarIt = savedVar.iterator();
                    Object toAddIter = savedVarIt.next();
                    while (toAddIter != null) {
                        if (toAddIter instanceof BaseNode) {
                            ((Node) iter).add(relName, (Node) toAddIter);
                        }
                        toAddIter = savedVarIt.next();
                    }
                }
                iter = previousResultIt.next();
            }
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        return "add(\'" + _relationName + "\'" + Constants.QUERY_SEP + "\'" + _variableNameToAdd + "\')";
    }

}
