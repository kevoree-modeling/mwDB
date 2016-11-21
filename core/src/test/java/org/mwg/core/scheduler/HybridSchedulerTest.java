package org.mwg.core.scheduler;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.CoreTask.task;

/**
 * @ignore ts
 */
public class HybridSchedulerTest {

  //  @Test
    public void test() {
        Graph g = new GraphBuilder().withScheduler(new HybridScheduler()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                task().loopPar("0","99", task().loop("0","99", task().then(print("{{result}}")))).execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        System.out.println();

                    }
                });
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
