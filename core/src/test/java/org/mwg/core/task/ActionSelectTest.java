package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionSelect;
import org.mwg.utility.HashHelper;

public class ActionSelectTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        new CoreTask()
                .fromIndexAll("nodes")
                .select(new TaskFunctionSelect() {
                    @Override
                    public boolean select(Node node, TaskContext context) {
                        return HashHelper.equals(node.get("name").toString(), "root");
                    }
                })
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.resultAsNodes().get(0).get("name"), "root");
                    }
                })
                .execute(graph,null);
        removeGraph();
    }

    @Test
    public void test2() {
        initGraph();
        new CoreTask()
                .fromIndexAll("nodes")
                .select(new TaskFunctionSelect() {
                    @Override
                    public boolean select(Node node, TaskContext context) {
                        return false;
                    }
                })
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.result().size(), 0);
                    }
                })
                .execute(graph,null);
        removeGraph();
    }

    @Test
    public void test3() {
        initGraph();
        new CoreTask()
                .fromIndexAll("nodes")
                .select(new TaskFunctionSelect() {
                    @Override
                    public boolean select(Node node, TaskContext context) {
                        return true;
                    }
                })
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Assert.assertEquals(context.result().size(), 3);
                    }
                })
                .execute(graph,null);
        removeGraph();
    }

}
