package org.mwg.task;

public interface TaskHook {

    void start(TaskContext initialContext);

    void beforeAction(Action action, TaskContext context);

    void afterAction(Action action, TaskContext context);

    void beforeTask(TaskContext parentContext, TaskContext context);

    void afterTask(TaskContext context);

    void end(TaskContext finalContext);

}
