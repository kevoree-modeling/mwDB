package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.task;

public class ActionFromVarTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task()
                .then(readIndexAll("nodes"))
                .then(asGlobalVar("x"))
                .then(inject("uselessPayload"))
                .then(readVar("x"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.resultAsNodes().get(0).get("name"), "n0");
                        Assert.assertEquals(context.resultAsNodes().get(1).get("name"), "n1");
                        Assert.assertEquals(context.resultAsNodes().get(2).get("name"), "root");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

}
