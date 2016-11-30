package org.mwg;

import org.mwg.plugin.Job;

import java.io.Serializable;

public class HazelcastJob implements Serializable, Runnable {

    private Job job;

    public HazelcastJob(Job job) {
        this.job = job;
    }

    @Override
    public void run() {
        job.run();
    }
}
