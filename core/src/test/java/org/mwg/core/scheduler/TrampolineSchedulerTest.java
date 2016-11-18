package org.mwg.core.scheduler;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.loopPar;
import static org.mwg.core.task.Actions.print;

/**
 * @ignore ts
 */
public class TrampolineSchedulerTest {

  //  @Test
    public void test() {
        Graph g = new GraphBuilder().withScheduler(new TrampolineScheduler()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                loopPar("0","99", print("{{result}}")).execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        System.out.println();
                    }
                });
            }
        });
    }

}
