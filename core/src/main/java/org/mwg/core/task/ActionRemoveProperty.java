package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionRemoveProperty extends AbstractAction {

    private final String _propertyName;

    ActionRemoveProperty(final String propertyName) {
        super();
        this._propertyName = propertyName;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        if (previousResult != null) {
            final String flatRelationName = context.template(_propertyName);
            for (int i = 0; i < previousResult.size(); i++) {
                Object loopObj = previousResult.get(i);
                if (loopObj instanceof BaseNode) {
                    Node loopNode = (Node) loopObj;
                    loopNode.removeProperty(flatRelationName);
                }
            }
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        return "removeProperty(\'" + _propertyName + "\')";
    }


}
