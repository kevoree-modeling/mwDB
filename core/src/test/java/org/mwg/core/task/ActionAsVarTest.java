package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.asGlobalVar;
import static org.mwg.core.task.Actions.inject;
import static org.mwg.core.task.Actions.task;

public class ActionAsVarTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task().then(inject("hello")).then(asGlobalVar("myVar")).thenDo(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                Assert.assertEquals(context.result().get(0), "hello");
                Assert.assertEquals(context.variable("myVar").get(0), "hello");
                context.continueTask();
            }
        }).execute(graph, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                Assert.assertNotEquals(result.size(),0);
            }
        });
        removeGraph();
    }

}
