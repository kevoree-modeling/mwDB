package org.mwg.core.scheduler;

import org.mwg.plugin.Job;
import org.mwg.plugin.Scheduler;

import java.util.Deque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * boing,boing,boing....
 */
public class TrampolineScheduler implements Scheduler {

    /**
     * @native ts
     * private queue = new org.mwg.core.scheduler.JobQueue();
     */
    private final Deque<Job> queue = new ConcurrentLinkedDeque<Job>();
    private final AtomicInteger wip = new AtomicInteger(0);

    @Override
    public void dispatch(final byte affinity, Job job) {
        queue.add(job);
        if (wip.getAndIncrement() == 0) {
            do {
                final Job polled = queue.poll();
                if (polled != null) {
                    polled.run();
                }
            } while (wip.decrementAndGet() > 0);
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }

    @Override
    public int workers() {
        return 1;
    }

}
