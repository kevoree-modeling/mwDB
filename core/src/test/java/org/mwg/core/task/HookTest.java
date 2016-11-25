package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;

import static org.mwg.core.task.Actions.*;

public class HookTest {

    @Test
    public void test() {
        Graph g = GraphBuilder.newBuilder().build();
        g.connect(result -> {

            int[] count = new int[1];
            count[0] = 0;

            task()
                    .addHook(new TaskHook() {
                        @Override
                        public void start(TaskContext initialContext) {
                            count[0]++;
                        }

                        @Override
                        public void beforeAction(Action action, TaskContext context) {
                            count[0]++;
                        }

                        @Override
                        public void afterAction(Action action, TaskContext context) {
                            count[0]++;
                        }

                        @Override
                        public void beforeTask(TaskContext parentContext, TaskContext context) {
                            count[0]++;
                        }

                        @Override
                        public void afterTask(TaskContext context) {
                            count[0]++;
                        }

                        @Override
                        public void end(TaskContext finalContext) {
                            count[0]++;
                        }
                    })
                    .then(inject(new int[]{1, 2, 3}))
                    .forEach(task().then(setAsVar("{{result}}")))
                    .execute(g, null);

            Assert.assertEquals(6, count[0]);

        });


    }

}
