package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.fromIndexAll;
import static org.mwg.core.task.Actions.traverseOrKeep;
import static org.mwg.core.task.CoreTask.task;

public class ActionTraverseOrKeepTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task().then(fromIndexAll("nodes"))
                .then(traverseOrKeep("children"))
                .then(traverseOrKeep("children"))
                .thenDo(new ActionFunction() {
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
