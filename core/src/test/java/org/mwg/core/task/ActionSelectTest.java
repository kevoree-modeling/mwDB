package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionSelect;
import org.mwg.utility.HashHelper;

import static org.mwg.core.task.Actions.readGlobalIndexAll;
import static org.mwg.core.task.Actions.select;
import static org.mwg.core.task.Actions.task;

public class ActionSelectTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        task()
                .then(readGlobalIndexAll("nodes"))
                .then(select(new TaskFunctionSelect() {
                    @Override
                    public boolean select(Node node, TaskContext context) {
                        return HashHelper.equals(node.get("name").toString(), "root");
                    }
                }))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
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
                .then(select((node, context) -> false))
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
                .then(select((node, context) -> true))
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
