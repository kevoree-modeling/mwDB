package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.readGlobalIndexAll;
import static org.mwg.core.task.Actions.selectWith;
import static org.mwg.core.task.Actions.task;

public class ActionWithTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task()
                .then(readGlobalIndexAll("nodes"))
                .then(selectWith("name", "n0"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.resultAsNodes().get(0).get("name"), "n0");
                    }
                })
                .execute(graph, null);

        task().then(readGlobalIndexAll("nodes"))
                .then(selectWith("name", "n.*"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.resultAsNodes().get(0).get("name"), "n0");
                        Assert.assertEquals(context.resultAsNodes().get(1).get("name"), "n1");
                    }
                })
                .execute(graph, null);
        removeGraph();

    }

}
