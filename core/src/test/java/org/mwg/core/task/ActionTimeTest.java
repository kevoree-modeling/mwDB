package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.inject;

public class ActionTimeTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        inject(10).asGlobalVar("time").setTime("{{time}}")
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.time(), 10);
                    }
                })
                .execute(graph, null);
        removeGraph();
    }


}
