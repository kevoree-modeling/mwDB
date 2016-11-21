package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.task;

public class ActionGetTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task()
                .then(readIndexAll("nodes"))
                .then(get("children"))
                .then(get("name"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.result().get(0), "n0");
                        Assert.assertEquals(context.result().get(1), "n1");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

    @Test
    public void testDefaultSynthax() {
        initGraph();
        task()
                .then(readIndexAll("nodes"))
                .parse("children.name")
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.result().get(0), "n0");
                        Assert.assertEquals(context.result().get(1), "n1");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }


    @Test
    public void testParse() {
        initGraph();
        task()
                .parse("readIndexAll(nodes).traverse(children)")
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.resultAsNodes().get(0).get("name"), "n0");
                        Assert.assertEquals(context.resultAsNodes().get(1).get("name"), "n1");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

    @Test
    public void testTraverseIndex() {
        initGraph();
        Node node1 = graph.newNode(0, 0);
        node1.setProperty("name", Type.STRING, "node1");
        node1.setProperty("value", Type.INT, 1);

        Node node2 = graph.newNode(0, 0);
        node2.setProperty("name", Type.STRING, "node2");
        node2.setProperty("value", Type.INT, 2);

        Node node3 = graph.newNode(0, 12);
        node3.setProperty("name", Type.STRING, "node3");
        node3.setProperty("value", Type.INT, 3);

        Node root = graph.newNode(0, 0);
        root.setProperty("name", Type.STRING, "root2");

        graph.index("rootIndex", root, "name", new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                root.index("childrenIndexed", node1, "name", null);
                root.index("childrenIndexed", node2, "name", null);
                root.index("childrenIndexed", node3, "name", null);

                root.jump(12, new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        root.index("childrenIndexed", node3, "name", null);
                    }
                });

            }
        });

        /*
        readIndex("rootIndex", "name=root2")
                .traverseIndex("childrenIndexed", "name","node2")
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(1, context.result().size());
                        Assert.assertEquals("node2", context.resultAsNodes().get(0).get("name"));
                    }
                }).execute(graph, null);
*/

        task()
                .then(readIndex("rootIndex", "name=root2"))
                .then(traverseIndex("childrenIndexed", "name", "node3"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(0, context.result().size());
                    }
                }).execute(graph, null);

        /*
        inject(12).asGlobalVar("time").setTime("{{time}}")
                .readIndex("rootIndex", "name=root2")
                .traverseIndex("childrenIndexed", "name=node2")
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(1, context.result().size());
                        Assert.assertEquals("node2", context.resultAsNodes().get(0).get("name"));
                    }
                }).execute(graph, null);

        readIndex("rootIndex", "name=root2")
                .traverseIndexAll("childrenIndexed")
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(2, context.result().size());
                        Assert.assertEquals("node1", context.resultAsNodes().get(0).get("name"));
                        Assert.assertEquals("node2", context.resultAsNodes().get(1).get("name"));
                    }
                }).execute(graph, null);

        inject(13).asGlobalVar("time").setTime("{{time}}")
                .readIndex("rootIndex", "name=root2")
                .traverseIndexAll("childrenIndexed")
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(3, context.result().size());
                        Assert.assertEquals("node1", context.resultAsNodes().get(0).get("name"));
                        Assert.assertEquals("node2", context.resultAsNodes().get(1).get("name"));
                        Assert.assertEquals("node3", context.resultAsNodes().get(2).get("name"));
                    }
                }).execute(graph, null);

                */

        removeGraph();
    }

}
