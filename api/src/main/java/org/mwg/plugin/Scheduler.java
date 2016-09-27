package org.mwg.plugin;

/**
 * In charge of the scheduling of tasks in mwDB
 */
public interface Scheduler {

    /**
     * Registers a job for execution.
     *
     * @param affinity The job thread affinity
     * @param job      The new job to execute.
     */
    void dispatch(byte affinity, Job job);

    /**
     * Starts the scheduler (i.e.: the execution of tasks).
     */
    void start();

    /**
     * Terminates the scheduler (i.e.: the execution of tasks).
     */
    void stop();

    /**
     * Return the number of parallel workers
     * @return integer representing the number of current parallel workers
     */
    int workers();

}
