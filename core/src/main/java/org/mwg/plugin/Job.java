package org.mwg.plugin;

/**
 * Unit of computation of Many World Graph
 */
@FunctionalInterface
public interface Job {
    /**
     * Trigger the unit of computation
     */
    void run();
}
