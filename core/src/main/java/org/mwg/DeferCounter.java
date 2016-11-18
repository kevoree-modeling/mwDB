package org.mwg;

import org.mwg.plugin.Job;

/**
 * DeferCounter provides a mean to wait for an amount of events before running a method.
 */
public interface DeferCounter {

    /**
     * Notifies the counter that an awaited event has occurred.<br>
     * If the total amount of awaited events is reached, the task registered by the {@link #then(Job) then} method is executed.
     */
    void count();

    /**
     * Get the number of events still expected
     * @return the number of events still expected
     */
    int getCount();

    /**
     * Registers the task, in form of a {@link Job}, to be called when all awaited events have occurred.
     * @param job The task to be executed
     */
    void then(Job job);

    /**
     * Wrap into a callback.
     * @return the callback
     */
    Callback wrap();

}
