package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

class ActionAddRemoveVarToRelation extends AbstractAction {

    private final String _name;
    private final String _varFrom;
    private final String[] _attributes;
    private final boolean _isAdd;

    ActionAddRemoveVarToRelation(final boolean isAdd, final String name, final String varFrom, final String... attributes) {
        super();
        this._isAdd = isAdd;
        this._name = name;
        this._varFrom = varFrom;
        this._attributes = attributes;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResult savedVar = context.variable(context.template(_varFrom));
        if (previousResult != null && savedVar != null) {
            final String relName = context.template(_name);
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
                if (iter instanceof BaseNode) {
                    final TaskResultIterator savedVarIt = savedVar.iterator();
                    Object toAddIter = savedVarIt.next();
                    while (toAddIter != null) {
                        if (toAddIter instanceof BaseNode) {
                            final Node castedToAddIter = (Node) toAddIter;
                            final Node castedIter = (Node) iter;
                            /*
                            if (_isAdd) {
                                castedIter.addToRelation(relName, castedToAddIter, _attributes);
                            } else {
                                castedIter.removeFromRelation(relName, castedToAddIter, _attributes);
                            }
                            */
                            internal(_isAdd,relName, castedIter, castedToAddIter, _attributes);
                        }
                        toAddIter = savedVarIt.next();
                    }
                }
                iter = previousResultIt.next();
            }
        }
        context.continueTask();
    }

    /**
     * @native ts
     * if (this._isAdd) {
     * src.addToRelation(relName, target, ...this._attributes);
     * } else {
     * src.removeFromRelation(relName, target, ...this._attributes);
     * }
     */
    private void internal(boolean isAdd, String relName, Node src, Node target, String[] attributes) {
        if (isAdd) {
            src.addToRelation(relName, target, _attributes);
        } else {
            src.removeFromRelation(relName, target, _attributes);
        }
    }

    @Override
    public String toString() {
        return "add()";
    }

}
