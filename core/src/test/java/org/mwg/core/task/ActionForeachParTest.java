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
import static org.mwg.core.task.Actions.task;

public class ActionForeachParTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        final long[] i = {0};
        task()
                .then(inject(new long[]{1, 2, 3}))
                .forEachPar(
                        task().thenDo(context -> {
                            i[0]++;
                            Assert.assertEquals(context.result().get(0), i[0]);
                            context.continueTask();
                        })
                )
                .thenDo(context -> {
                    TaskResult<Long> longs = context.result();
                    Assert.assertEquals(longs.size(), 3);
                    Assert.assertEquals(longs.get(0), (Long) 1l);
                    Assert.assertEquals(longs.get(1), (Long) 2l);
                    Assert.assertEquals(longs.get(2), (Long) 3l);
                })
                .execute(graph, null);

        task().then(readIndexAll("nodes"))
                .forEachPar(
                        task().thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                context.continueTask();
                            }
                        })
                )
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        Assert.assertEquals(nodes.size(), 3);
                        Assert.assertEquals(nodes.get(0).get("name"), "n0");
                        Assert.assertEquals(nodes.get(1).get("name"), "n1");
                        Assert.assertEquals(nodes.get(2).get("name"), "root");
                    }
                })
                .execute(graph, null);

        List<String> paramIterable = new ArrayList<String>();
        paramIterable.add("n0");
        paramIterable.add("n1");
        paramIterable.add("root");
        task().then(inject(paramIterable))
                .forEachPar(task().thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                context.continueTask();
                            }
                        })
                )
                .thenDo(new ActionFunction() {
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

}
