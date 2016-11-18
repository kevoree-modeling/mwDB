package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

import static org.mwg.core.task.Actions.*;

public class ActionForeachTest extends AbstractActionTest {

    @Test
    public void testForeachWhere() {
        initGraph();
        final long[] i = {0};
        inject(new long[]{1, 2, 3})
                .foreach(then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        i[0]++;
                        Assert.assertEquals(context.result().get(0), i[0]);
                        context.continueTask();
                    }
                }))
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Long> result = context.result();
                        Assert.assertEquals(result.size(), 3);
                        Assert.assertEquals(result.get(0), (Long) 1l);
                        Assert.assertEquals(result.get(1), (Long) 2l);
                        Assert.assertEquals(result.get(2), (Long) 3l);
                    }
                })
                .execute(graph, null);

        fromIndexAll("nodes").foreach(then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                context.continueTask();
            }
        })).then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                TaskResult<Node> nodes = context.resultAsNodes();
                Assert.assertEquals(nodes.size(), 3);
                Assert.assertEquals(nodes.get(0).get("name"), "n0");
                Assert.assertEquals(nodes.get(1).get("name"), "n1");
                Assert.assertEquals(nodes.get(2).get("name"), "root");
            }
        }).execute(graph, null);

        List<String> paramIterable = new ArrayList<String>();
        paramIterable.add("n0");
        paramIterable.add("n1");
        paramIterable.add("root");
        inject(paramIterable).foreach(then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                context.continueTask();
            }
        })).then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                TaskResult<String> names = context.result();
                Assert.assertEquals(names.size(), 3);
                Assert.assertEquals(names.get(0), "n0");
                Assert.assertEquals(names.get(1), "n1");
                Assert.assertEquals(names.get(2), "root");
            }
        }).execute(graph, null);

        removeGraph();
    }

/*
    @Test
    public void testForeach() {
        initGraph();
        final long[] toTest = {1, 2, 3, 4, 5};
        final int[] index = {0};

        inject(toTest).foreachThen(new Callback<Long>() {
            @Override
            public void on(Long object) {
                Assert.assertEquals(toTest[index[0]], (long) object);
                index[0]++;
            }
        }).execute(graph,null);

        index[0] = 0;
        new CoreTask().fromIndexAll("nodes").foreachThen(new Callback<Node>() {
            @Override
            public void on(Node object) {
                object.set("name", "node" + index[0]);
                index[0]++;
            }
        }).fromIndexAll("nodes").then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                Node[] result = (Node[]) context.result();
                Assert.assertEquals(3, result.length);
                Assert.assertEquals("node0", result[0].get("name"));
                Assert.assertEquals("node1", result[1].get("name"));
                Assert.assertEquals("node2", result[2].get("name"));
            }
        }).execute(graph,null);
        removeGraph();
    }*/

    /*
    @Test
    public void testForEachMergeVariables() {
        initGraph();
        final int[] index = {0};
        org.mwg.task.Task forEachTask = new CoreTask().then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                context.setVariable("param" + index[0]++, context.result());
                context.setResult(context.result());
            }
        });

        List<String> paramIterable = new ArrayList<String>();
        paramIterable.add("n0");
        paramIterable.add("n1");
        paramIterable.add("root");
        inject(paramIterable).foreach(forEachTask).fromVar("param0").then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                Object result = (String) context.result();
                Assert.assertEquals("n0", result);
            }
        }).fromVar("param1").then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                Object result = (String) context.result();
                Assert.assertEquals("n1", result);
            }
        }).fromVar("param2").then(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                Object result = (String) context.result();
                Assert.assertEquals("root", result);
            }
        }).execute(graph, null);
        removeGraph();
    }*/

}
