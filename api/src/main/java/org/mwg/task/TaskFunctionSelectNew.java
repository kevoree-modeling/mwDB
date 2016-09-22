package org.mwg.task;

import org.mwg.Node;

@FunctionalInterface
public interface TaskFunctionSelectNew {
    boolean select(Node node, TaskContext context);
}
