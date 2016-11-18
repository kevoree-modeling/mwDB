package org.mwg.task;

import org.mwg.Node;

@FunctionalInterface
public interface TaskFunctionGroup {
    long group(Node nodes);
}
