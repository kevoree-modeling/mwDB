package org.mwg.task;


/**
 * Task closure function to transform nodes for next action
 */
@FunctionalInterface
public interface TaskFunctionMap<A, B> {
    /**
     * Convert a node to any kind of object for next action
     *
     * @param node current node to convert
     * @return converted results, can be any object
     */
    B map(A node);

}
