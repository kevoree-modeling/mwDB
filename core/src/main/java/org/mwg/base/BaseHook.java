package org.mwg.base;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;

public class BaseHook implements TaskHook {

    @Override
    public void start(TaskContext initialContext) {
        //NOOP
    }

    @Override
    public void beforeAction(Action action, TaskContext context) {
        //NOOP
    }

    @Override
    public void afterAction(Action action, TaskContext context) {
        //NOOP
    }

    @Override
    public void beforeTask(TaskContext parentContext, TaskContext context) {
        //NOOP
    }

    @Override
    public void afterTask(TaskContext context) {
        //NOOP
    }

    @Override
    public void end(TaskContext finalContext) {
        //NOOP
    }

}
