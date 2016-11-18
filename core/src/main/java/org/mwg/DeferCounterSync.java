package org.mwg;

/**
 * DeferCounter provides a mean to wait for an amount of events before running a method.
 */
public interface DeferCounterSync extends DeferCounter {
    
    Object waitResult();

}
