package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionRemoveAttribute extends AbstractAction {

    private final String _name;

    ActionRemoveAttribute(final String name) {
        super();
        this._name = name;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        if (previousResult != null) {
            final String flatRelationName = context.template(_name);
            for (int i = 0; i < previousResult.size(); i++) {
                Object loopObj = previousResult.get(i);
                if (loopObj instanceof BaseNode) {
                    Node loopNode = (Node) loopObj;
                    loopNode.removeAttribute(flatRelationName);
                }
            }
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        return "removeAttribute(\'" + _name + "\')";
    }


}
