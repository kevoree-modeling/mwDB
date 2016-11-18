package org.mwg.base;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;

public abstract class AbstractAction implements Action {

    private AbstractAction _next = null;

    public void setNext(AbstractAction p_next) {
        this._next = p_next;
    }

    public AbstractAction next() {
        return this._next;
    }

    public abstract void eval(TaskContext context);

}
