package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.indexesNames;
import static org.mwg.core.task.Actions.task;

public class ActionIndexesNamesTest {


    @Test
    public void testIndexesNames() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node root1 = graph.newNode(0, 0);
                root1.set("name", 1);

                String[] indexes = new String[]{"idx1", "idx2", "idx3"};

                graph.index(indexes[0], root1, "name", null);
                graph.index(indexes[1], root1, "name", null);
                graph.index(indexes[2], root1, "name", null);

                task()
                        .then(indexesNames())
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
