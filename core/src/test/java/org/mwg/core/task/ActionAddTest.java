package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.struct.Relationship;
import org.mwg.task.ActionFunction;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.task;

public class ActionAddTest extends AbstractActionTest {

    public ActionAddTest() {
        super();
        initGraph();
    }

    @Test
    public void testWithOneNode() {
        Node relatedNode = graph.newNode(0, 0);
        final long[] id = new long[1];
        task()
                .then(createNode())
                .then(inject(relatedNode))
                .then(asGlobalVar("x"))
                .then(addToRelationship("friend", "x"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Node node = (Node) context.result().get(0);
                        Assert.assertNotNull(node);
                        Assert.assertEquals(1, ((Relationship) node.get("friend")).size());
                        id[0] = node.id();
                    }
                }).execute(graph, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                graph.lookup(0, 0, id[0], new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        Assert.assertEquals(1, ((long[]) result.get("friend")).length);
                    }
                });
            }
        });
    }

    @Test
    public void testWithArray() {
        Node relatedNode = graph.newNode(0, 0);

        final long[] ids = new long[5];
        task()
                .then(inject(relatedNode))
                .then(asGlobalVar("x"))
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
                .then(addToRelationship("friend", "x"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        Assert.assertNotNull(nodes);
                        for (int i = 0; i < 5; i++) {
                            Assert.assertEquals(1, ((Relationship) nodes.get(i).get("friend")).size());
                            ids[i] = nodes.get(i).id();
                        }
                    }
                }).execute(graph, null);

        for (int i = 0; i < ids.length; i++) {
            graph.lookup(0, 0, ids[i], new Callback<Node>() {
                @Override
                public void on(Node result) {
                    Assert.assertEquals(1, ((Relationship) result.get("friend")).size());
                }
            });
        }


    }

    @Test
    public void testWithNull() {
        Node relatedNode = graph.newNode(0, 0);

        final boolean[] nextCalled = new boolean[1];

        task()
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        context.continueWith(null);
                    }
                })
                .then(inject(relatedNode))
                .then(asGlobalVar("x"))
                .then(addToRelationship("friend", "x"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        nextCalled[0] = true;
                    }
                })
                .execute(graph, null);

        Assert.assertTrue(nextCalled[0]);
    }

}
