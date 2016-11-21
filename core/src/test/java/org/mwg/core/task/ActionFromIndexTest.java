package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.readIndex;
import static org.mwg.core.task.Actions.inject;
import static org.mwg.core.task.Actions.task;

public class ActionFromIndexTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task()
                .then(inject("uselessPayload"))
                .then(readIndex("nodes", "name=n0"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.resultAsNodes().get(0).get("name"), "n0");
                        Assert.assertEquals(context.result().size(), 1);
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

}
