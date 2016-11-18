package org.mwg.plugin;

import org.mwg.Graph;
import org.mwg.Node;

/**
 * NodeFactory plugin allows to propose alternative implementations for {@link Node}.<br>
 * This specialization allows ot inject particular behavior into {@link Node} such as machine learning, extrapolation function.
 */
@FunctionalInterface
public interface NodeFactory {

    /**
     * Create a new Node
     *
     * @param world current world
     * @param time  current time
     * @param id    current node id
     * @param graph current graph
     * @return newly created Node object
     */
    Node create(long world, long time, long id, Graph graph);

}
