package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.task;

public class ActionJumpTest extends AbstractActionTest {

    @Test
    public void testJump() {
        initGraph();

        task().then(readIndexAll("nodes"))
                .then(defineAsGlobalVar("nodes"))
                .forEach(task().thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        Assert.assertEquals(0, nodes.get(0).time());
                        context.continueWith(null);
                    }
                }))
                .then(readVar("nodes"))
                .then(jump("10"))
                .forEach(task().thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        Node it = nodes.get(0);
                        Assert.assertEquals(10, it.time());
                        context.continueWith(null);
                    }
                }))
                .execute(graph, null);


        removeGraph();
    }
}
