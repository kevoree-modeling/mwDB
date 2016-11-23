package org.mwg.task;

@FunctionalInterface
public interface ConditionalFunction {
    boolean eval(TaskContext context);
}
