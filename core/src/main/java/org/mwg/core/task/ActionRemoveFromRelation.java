package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

class ActionRemoveFromRelation extends AbstractAction {

    private final String _relationName;
    private final String _variableNameToRemove;

    ActionRemoveFromRelation(final String relationName, final String variableNameToRemove) {
        super();
        this._relationName = relationName;
        this._variableNameToRemove = variableNameToRemove;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResult savedVar = context.variable(context.template(_variableNameToRemove));
        if (previousResult != null && savedVar != null) {
            final String relName = context.template(_relationName);
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
                if (iter instanceof BaseNode) {
                    final TaskResultIterator savedVarIt = savedVar.iterator();
                    Object toRemoveIter = savedVarIt.next();
                    while (toRemoveIter != null) {
                        if (toRemoveIter instanceof BaseNode) {
                            ((Node) iter).remove(relName, (Node) toRemoveIter);
                        }
                        toRemoveIter = savedVarIt.next();
                    }
                }
                iter = previousResultIt.next();
            }
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        return "remove(\'" + _relationName + "\'" + Constants.QUERY_SEP + "\'" + _variableNameToRemove + "\')";
    }
    
}
