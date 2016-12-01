package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.*;

public class ActionSelectScriptTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task()
                .then(readGlobalIndexAll("nodes"))
                .then(selectScript("node.get('name') == 'root'"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(1,context.result().size());
                        Assert.assertEquals(context.resultAsNodes().get(0).get("name"), "root");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

    @Test
    public void test2() {
        initGraph();
        task()
                .then(readGlobalIndexAll("nodes"))
                .then(selectScript("false"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.result().size(), 0);
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

    @Test
    public void test3() {
        initGraph();
        task()
                .then(readGlobalIndexAll("nodes"))
                .then(selectScript("true"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.result().size(), 3);
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

}
