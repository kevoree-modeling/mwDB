package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.inject;

public class ActionNewNodeTest extends AbstractActionTest {

    public ActionNewNodeTest() {
        super();
        initGraph();
    }

    @Test
    public void testCreateNode() {
        final long id[] = new long[1];
        inject(15).asGlobalVar("world").setWorld("{{world}}").
                inject(587).asGlobalVar("time").setTime("{{time[0]}}").newNode()
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertNotNull(context.result());
                        TaskResult<Node> n = context.resultAsNodes();
                        id[0] = n.get(0).id();
                        Assert.assertEquals(15, n.get(0).world());
                        Assert.assertEquals(587, n.get(0).time());
                    }
                }).execute(graph, null);
        graph.lookup(15, 587, id[0], new Callback<Node>() {
            @Override
            public void on(Node result) {
                Assert.assertNotEquals(null, result);
            }
        });
    }

}
