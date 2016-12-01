package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.newTask;

public class ActionLookupTest extends AbstractActionTest {

    @Test
    public void testLookup() {
        initGraph();

        newTask()
                .then(readGlobalIndexAll("nodes"))
                .thenDo(context -> {
                    TaskResult<Node> nodes = context.resultAsNodes();
                    for (int i = 0; i < nodes.size(); i++) {
                        context.addToVariable("ids", nodes.get(i).id());
                    }
                    context.continueTask();
                })
                .then(setTime("10"))
                .then(lookupAll("{{ids}}"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        Assert.assertEquals(nodes.get(0).toString(), "{\"world\":0,\"time\":10,\"id\":1,\"name\":\"n0\",\"value\":8}");
                        Assert.assertEquals(nodes.get(1).toString(), "{\"world\":0,\"time\":10,\"id\":2,\"name\":\"n1\",\"value\":3}");
                        Assert.assertEquals(nodes.get(2).toString(), "{\"world\":0,\"time\":10,\"id\":3,\"name\":\"root\",\"children\":[1,2]}");

                    }
                })
                .then(print("{{result}}"))
                .execute(graph, null);

        removeGraph();
    }
}
