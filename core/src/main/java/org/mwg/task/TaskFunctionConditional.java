package org.mwg.task;

@FunctionalInterface
public interface TaskFunctionConditional {
    boolean eval(TaskContext context);
}
