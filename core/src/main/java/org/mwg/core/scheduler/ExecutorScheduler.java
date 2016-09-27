package org.mwg.core.scheduler;

import org.mwg.plugin.Job;
import org.mwg.plugin.Scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ignore ts
 */
public class ExecutorScheduler implements Scheduler {

    private ExecutorService service;
    private int _workers = -1;

    @Override
    public void dispatch(final byte affinity, final Job job) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    job.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void start() {
        if (_workers == -1) {
            this.service = Executors.newCachedThreadPool();
        } else {
            this.service = Executors.newWorkStealingPool(_workers);
        }
    }

    @Override
    public void stop() {
        this.service.shutdown();
        this.service = null;
    }

    @Override
    public int workers() {
        return _workers;
    }

}
