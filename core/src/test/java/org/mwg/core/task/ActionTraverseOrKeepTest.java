package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

public class ActionTraverseOrKeepTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        new CoreTask()
                .fromIndexAll("nodes")
                .traverseOrKeep("children")
                .traverseOrKeep("children")
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        Assert.assertEquals(nodes.get(0).get("name"), "n0");
                        Assert.assertEquals(nodes.get(1).get("name"), "n1");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

}
