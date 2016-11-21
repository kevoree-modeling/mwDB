package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.task;

public class ActionSetTest extends ActionNewNodeTest {

    public ActionSetTest() {
        super();
        initGraph();
    }

    @Test
    public void testWithOneNode() {
        final long[] id = new long[1];
        task().then(inject("node")).then(asGlobalVar("nodeName"))
                .then(createNode())
                .then(setAttribute("name", Type.STRING, "{{nodeName}}"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Node node = (Node) context.result().get(0);
                        Assert.assertNotNull(node);
                        Assert.assertEquals("node", node.get("name"));

                        id[0] = node.id();
                    }
                }).execute(graph, null);

        graph.lookup(0, 0, id[0], new Callback<Node>() {
            @Override
            public void on(Node result) {
                Assert.assertEquals("node", result.get("name"));
            }
        });
    }

    @Test
    public void testWithArray() {
        final long[] ids = new long[5];
        task()
                .then(inject("node"))
                .then(asGlobalVar("nodeName"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Node[] nodes = new Node[5];
                        for (int i = 0; i < 5; i++) {
                            nodes[i] = graph.newNode(0, 0);
                        }
                        context.continueWith(context.wrap(nodes));
                    }
                })
                .then(setAttribute("name", Type.STRING, "{{nodeName}}"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        Assert.assertNotNull(nodes);
                        for (int i = 0; i < 5; i++) {
                            Assert.assertEquals("node", nodes.get(i).get("name"));
                            ids[i] = nodes.get(i).id();
                        }
                    }
                }).execute(graph, null);

        for (int i = 0; i < ids.length; i++) {
            graph.lookup(0, 0, ids[i], new Callback<Node>() {
                @Override
                public void on(Node result) {
                    Assert.assertEquals("node", result.get("name"));
                }
            });
        }
    }

    @Test
    public void testWithNull() {
        final boolean[] nextCalled = new boolean[1];
        task()
                .then(inject("node"))
                .then(asGlobalVar("nodeName"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        context.continueWith(null);
                    }
                })
                .then(setAttribute("name", Type.STRING, "node"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        nextCalled[0] = true;
                    }
                }).execute(graph, null);

        Assert.assertTrue(nextCalled[0]);
    }

}
