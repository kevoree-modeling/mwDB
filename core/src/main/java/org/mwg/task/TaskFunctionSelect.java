package org.mwg.task;

import org.mwg.Node;

/**
 * Task closure function to select nodes for next action
 */
@FunctionalInterface
public interface TaskFunctionSelect {

    /**
     * Selection function called that specify if a specified node
     * will be in the result set or not
     *
     * @param node node to select or not for next action
     * @param context current context
     * @return true to keep this node for the next action
     */
    boolean select(Node node, TaskContext context);
}
