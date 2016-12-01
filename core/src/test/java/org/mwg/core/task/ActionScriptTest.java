package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;

public class ActionScriptTest extends AbstractActionTest {

    @Test
    public void testLookup() {
        initGraph();

        task()
                .then(readGlobalIndexAll("nodes"))
                .then(script("context.setVariable(\"val1\",55); context.continueTask();"))
                .then(script("context.setVariable(\"val2\",56); context.continueTask();"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(55,context.variable("val1").get(0));
                        Assert.assertEquals(56,context.variable("val2").get(0));
                        context.continueWith(context.wrap(true));
                    }
                })
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        Assert.assertEquals(true,result.get(0));
                    }
                });

        removeGraph();
    }
}
