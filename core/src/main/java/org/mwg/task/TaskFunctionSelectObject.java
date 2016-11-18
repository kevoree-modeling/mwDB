package org.mwg.task;

@FunctionalInterface
public interface TaskFunctionSelectObject {
    boolean select(Object object, TaskContext context);
}
