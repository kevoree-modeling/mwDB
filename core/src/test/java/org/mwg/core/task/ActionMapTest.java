package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.fromIndexAll;

public class ActionMapTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        fromIndexAll("nodes")
                .map(node -> ((Node)node).get("name"))
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> names = context.resultAsStrings();
                        Assert.assertEquals(names.get(0), "n0");
                        Assert.assertEquals(names.get(1), "n1");
                        Assert.assertEquals(names.get(2), "root");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

}
