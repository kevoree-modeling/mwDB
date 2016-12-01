package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.indexNames;
import static org.mwg.core.task.Actions.newTask;

public class ActionIndexNamesTest {

    @Test
    public void testIndexNames() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node root1 = graph.newNode(0, 0);
                root1.set("name", Type.INT, 1);

                String[] indexes = new String[]{"idx1", "idx2", "idx3"};
                for (int i = 0; i < indexes.length; i++) {
                    graph.index(0, 0, indexes[i], indexNode -> {
                        indexNode.addToIndex(root1, "name");
                    });
                }

                newTask()
                        .then(indexNames())
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertArrayEquals(indexes, context.result().asArray());
                            }
                        })
                        .execute(graph, new Callback<TaskResult>() {
                            @Override
                            public void on(TaskResult result) {
                                graph.disconnect(null);
                            }
                        });
            }
        });
    }
}
