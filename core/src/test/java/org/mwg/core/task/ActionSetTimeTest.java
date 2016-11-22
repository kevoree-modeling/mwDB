package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.*;

public class ActionSetTimeTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task()
                .then(inject(10))
                .then(defineAsGlobalVar("time"))
                .then(setTime("{{time}}"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.time(), 10);
                    }
                })
                .execute(graph, null);
        removeGraph();
    }


}
