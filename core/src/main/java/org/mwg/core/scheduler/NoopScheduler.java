package org.mwg.core.scheduler;

import org.mwg.plugin.Job;
import org.mwg.plugin.Scheduler;

public class NoopScheduler implements Scheduler {

    @Override
    public void dispatch(byte affinity, Job job) {
        job.run();
    }

    @Override
    public void start() {
        //noop
    }

    @Override
    public void stop() {
        //noop
    }

    @Override
    public int workers() {
        return 1;
    }

}
