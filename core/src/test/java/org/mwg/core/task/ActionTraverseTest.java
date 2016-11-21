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

public class ActionTraverseTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task().then(readIndexAll("nodes"))
                .then(traverse("children"))
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
    public void testParse() {
        initGraph();
        task().parse("readIndexAll(nodes).traverse(children)")
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
        final Node node1 = graph.newNode(0, 0);
        node1.setAttribute("name", Type.STRING, "node1");
        node1.setAttribute("value", Type.INT, 1);

        final Node node2 = graph.newNode(0, 0);
        node2.setAttribute("name", Type.STRING, "node2");
        node2.setAttribute("value", Type.INT, 2);

        final Node node3 = graph.newNode(0, 12);
        node3.setAttribute("name", Type.STRING, "node3");
        node3.setAttribute("value", Type.INT, 3);

        final Node root = graph.newNode(0, 0);
        root.setAttribute("name", Type.STRING, "root2");
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

        task().then(readIndex("rootIndex", "name=root2"))
                .then(traverseIndex("childrenIndexed", "name","node2"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(1, context.result().size());
                        Assert.assertEquals("node2", context.resultAsNodes().get(0).get("name"));
                    }
                }).execute(graph, null);

        task().then(readIndex("rootIndex", "name=root2"))
                .then(traverseIndex("childrenIndexed", "name","node3"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(0, context.result().size());
                    }
                }).execute(graph, null);

        task()
                .then(inject(12))
                .then(asGlobalVar("time"))
                .then(setTime("{{time}}"))
                .then(readIndex("rootIndex", "name=root2"))
                .then(traverseIndex("childrenIndexed", "name","node2"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(1, context.result().size());
                        Assert.assertEquals("node2", context.resultAsNodes().get(0).get("name"));
                    }
                }).execute(graph, null);

        task().then(readIndex("rootIndex", "name=root2"))
                .then(traverseIndexAll("childrenIndexed"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(2, context.result().size());
                        Assert.assertEquals("node1", context.resultAsNodes().get(0).get("name"));
                        Assert.assertEquals("node2", context.resultAsNodes().get(1).get("name"));
                    }
                }).execute(graph, null);

        task().then(inject(13)).then(asGlobalVar("time")).then(setTime("{{time}}"))
                .then(readIndex("rootIndex", "name=root2"))
                .then(traverseIndexAll("childrenIndexed"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(3, context.result().size());
                        Assert.assertEquals("node1", context.resultAsNodes().get(0).get("name"));
                        Assert.assertEquals("node2", context.resultAsNodes().get(1).get("name"));
                        Assert.assertEquals("node3", context.resultAsNodes().get(2).get("name"));
                    }
                }).execute(graph, null);
        removeGraph();
    }

}
