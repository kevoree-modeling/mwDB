package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionSelectObject;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

class ActionSelectObject extends AbstractAction {

    private final TaskFunctionSelectObject _filter;

    ActionSelectObject(final TaskFunctionSelectObject filterFunction) {
        super();
        if (filterFunction == null) {
            throw new RuntimeException("filterFunction should not be null");
        }
        this._filter = filterFunction;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previous = context.result();
        final TaskResult next = context.wrap(null);
        final TaskResultIterator iterator = previous.iterator();
        Object nextElem = iterator.next();
        while(nextElem != null) {
            if(_filter.select(nextElem,context)) {
                if(nextElem instanceof BaseNode) {
                    Node casted = (Node) nextElem;
                    next.add(casted.graph().cloneNode(casted));
                } else {
                    next.add(nextElem);
                }
            }
            nextElem = iterator.next();
        }
        context.continueWith(next);
    }

    @Override
    public String toString() {
        return "selectObject()";
    }
}
