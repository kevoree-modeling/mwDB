package org.mwg.task;

@FunctionalInterface
public interface TaskActionFactory {

    Action create(String[] params);

}
