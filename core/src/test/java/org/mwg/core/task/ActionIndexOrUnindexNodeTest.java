package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.*;

public class ActionIndexOrUnindexNodeTest {

    @Test
    public void testIndexOneNode() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                task()
                        .then(createNode())
                        .then(Actions.setAttribute("name", Type.STRING, "root"))
                        .then(Actions.indexNode("indexName", "name"))
                        .then(Actions.defineAsGlobalVar("nodeIndexed"))
                        .then(Actions.readIndexAll("indexName"))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertNotNull(context.result());
                                Node indexedNode = (Node) context.variable("nodeIndexed").get(0);
                                Assert.assertEquals(1, context.result().size());
                                Assert.assertEquals(indexedNode.id(), context.resultAsNodes().get(0).id());
                                context.continueTask();
                            }
                        })
                        .then(Actions.unindexNode("indexName", "name"))
                        .then(Actions.readIndexAll("indexName"))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertNotNull(context.result());
                                Assert.assertEquals(0, context.result().size());
                                context.continueWith(null);
                            }
                        })
                        .execute(graph, null);
            }
        });
    }

    /*
    @Test
    public void testIndexComplexArrayOfNodes() {
        Graph graph = new GraphBuilder().build();

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Object complexArray = new Object[3];

                for (int i = 0; i < 3; i++) {
                    Object[] inner = new Node[2];
                    for (int j = 0; j < 2; j++) {
                        inner[j] = graph.newNode(0, 0);
                        ((Node) inner[j]).set("name", "node" + i + j);
                    }
                    ((Object[]) complexArray)[i] = inner;
                }

                newTask()
                        .inject(complexArray)
                        .indexNode("indexName", "name")
                        .asVar("nodeIndexed")
                        .readIndexAll("indexName")
                        .then(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertNotNull(context.result());
                                Assert.assertEquals(6, context.result().size());
                                for (int i = 0; i < 3; i++) {
                                    Object inner = ((Object[]) complexArray)[i];
                                    for (int j = 0; j < 2; j++) {
                                        Assert.assertEquals(((Node[]) inner)[j].get("name"), "node" + i + j);
                                    }
                                }
                                context.continueTask();
                            }
                        })
                        .unindexNode("indexName", "name")
                        .readIndexAll("indexName")
                        .then(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertNotNull(context.result());
                                Assert.assertEquals(0, context.result().size());
                                context.continueWith(null);
                            }
                        })
                        .execute(graph, null);
            }
        });
    }


    @Test
    public void testIndexNodeIncorrectInput() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Task indexWithOneIncoorectInput = Actions.newTask()
                        .inject(55)
                        .indexNode("indexName", "name");

                Task unindexWithOneIncoorectInput = Actions.newTask()
                        .inject(55)
                        .unindexNode("indexName", "name");

                Object complexArray = new Object[3];

                for (int i = 0; i < 3; i++) {
                    Object[] inner = new Object[2];
                    for (int j = 0; j < 2; j++) {
                        if (i == 2 && j == 0) {
                            inner[j] = 55;
                        } else {
                            inner[j] = graph.newNode(0, 0);
                            ((Node) inner[j]).set("name", "node" + i + j);
                        }
                    }
                    ((Object[]) complexArray)[i] = inner;
                }


                Task indexwithIncorrectArray = newTask()
                        .inject(complexArray)
                        .indexNode("indexName", "name");

                Task unindexwithIncorrectArray = newTask()
                        .inject(complexArray)
                        .unindexNode("indexName", "name");

                boolean exceptionCaught = false;
                try {
                    indexWithOneIncoorectInput.execute(graph, null);
                } catch (RuntimeException ex) {
                    exceptionCaught = true;
                }
                Assert.assertTrue(exceptionCaught);

                exceptionCaught = false;
                try {
                    unindexWithOneIncoorectInput.execute(graph, null);
                } catch (RuntimeException ex) {
                    exceptionCaught = true;
                }
                Assert.assertTrue(exceptionCaught);

                exceptionCaught = false;
                try {
                    indexwithIncorrectArray.execute(graph, null);
                } catch (RuntimeException ex) {
                    exceptionCaught = true;
                }
                Assert.assertTrue(exceptionCaught);

                exceptionCaught = false;
                try {
                    unindexwithIncorrectArray.execute(graph, null);
                } catch (RuntimeException ex) {
                    exceptionCaught = true;
                }
                Assert.assertTrue(exceptionCaught);

            }
        });
    }
    */

}
