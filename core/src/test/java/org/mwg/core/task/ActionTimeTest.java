package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.asGlobalVar;
import static org.mwg.core.task.Actions.inject;
import static org.mwg.core.task.Actions.setTime;
import static org.mwg.core.task.Actions.task;

public class ActionTimeTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task()
                .then(inject(10))
                .then(asGlobalVar("time"))
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
