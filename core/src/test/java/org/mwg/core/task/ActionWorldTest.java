package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.setWorld;

public class ActionWorldTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        setWorld("10")
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.world(), 10);
                    }
                })
                .execute(graph,null);
        removeGraph();
    }

}
