package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.task.*;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.task;

public class FlagTest {
    @Test
    public void traverse() {
        Graph graph = new GraphBuilder()
                .withMemorySize(30000)
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {

            String relName = "children";

            Node n1 = graph.newNode(0, 13);
            graph.save(null);
            long initcache = graph.space().available();


            Node n2 = graph.newNode(0, 13);
            Node n3 = graph.newNode(0, 13);
            Node n4 = graph.newNode(0, 13);

            n1.addToRelation(relName, n2);
            n1.addToRelation(relName, n3);
            n1.addToRelation(relName, n4);


            Node n5 = graph.newNode(0, 13);
            Node n6 = graph.newNode(0, 13);
            n2.addToRelation(relName, n5);
            n2.addToRelation(relName, n6);

            Node n7 = graph.newNode(0, 13);
            Node n8 = graph.newNode(0, 13);
            n3.addToRelation(relName, n7);
            n3.addToRelation(relName, n8);

            Node n9 = graph.newNode(0, 13);
            Node n10 = graph.newNode(0, 13);
            n4.addToRelation(relName, n9);
            n4.addToRelation(relName, n10);

            n2.free();
            n3.free();
            n4.free();
            n5.free();
            n6.free();
            n7.free();
            n8.free();
            n9.free();
            n10.free();

            graph.save(null);
            Assert.assertTrue(graph.space().available() == initcache);

            Task traverse = task();
            traverse.then(defineAsGlobalVar("parent")).then(Actions.traverse(relName)).thenDo(new ActionFunction() {
                @Override
                public void eval(TaskContext context) {

                    TaskResult<Node> children = context.resultAsNodes();
                    if (children.size() != 0) {
                        context.continueWith(context.wrap(graph.cloneNode(children.get(0))));
                    } else {
                        context.continueWith(null);
                    }
                }
            }).ifThen(new ConditionalFunction() {
                @Override
                public boolean eval(TaskContext context) {
                    return (context.result() != null);
                }
            }, traverse);


            Task mainTask = task().then(setTime("13")).then(setWorld("0")).then(inject(n1)).map(traverse);
            mainTask.execute(graph, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult result) {
                    graph.save(null);
                    Assert.assertTrue(graph.space().available() == initcache);
                    if (result != null) {
                        result.free();
                    }
                }

            });

            graph.save(null);
            Assert.assertTrue(graph.space().available() == initcache);
        });
    }


    /*
    @Test
    public void traverseOrKeep() {
        Graph graph = new GraphBuilder()
                .withMemorySize(30000)
                .withScheduler(new NoopScheduler())
                .build();

        graph.connect(result -> {

            String relName = "children";

            Node n1 = graph.newNode(0, 13);
            graph.save(null);
            long initcache = graph.space().available();


            Node n2 = graph.newNode(0, 13);
            Node n3 = graph.newNode(0, 13);
            Node n4 = graph.newNode(0, 13);

            n1.addToRelation(relName, n2);
            n1.addToRelation(relName, n3);
            n1.addToRelation(relName, n4);


            Node n5 = graph.newNode(0, 13);
            Node n6 = graph.newNode(0, 13);
            n2.addToRelation(relName, n5);
            n2.addToRelation(relName, n6);

            Node n7 = graph.newNode(0, 13);
            Node n8 = graph.newNode(0, 13);
            n3.addToRelation(relName, n7);
            n3.addToRelation(relName, n8);

            Node n9 = graph.newNode(0, 13);
            Node n10 = graph.newNode(0, 13);
            n4.addToRelation(relName, n9);
            n4.addToRelation(relName, n10);

            n2.free();
            n3.free();
            n4.free();
            n5.free();
            n6.free();
            n7.free();
            n8.free();
            n9.free();
            n10.free();

            graph.save(null);
            Assert.assertTrue(graph.space().available() == initcache);


            Task traverse = task();

            traverse
                    .then(defineAsGlobalVar("parent"))
                    .then(new ActionTraverseOrKeep(relName))
                    .thenDo(new ActionFunction() {
                        @Override
                        public void eval(TaskContext context) {
                            TaskResult<Integer> count = context.variable("count");
                            int c = 0;
                            if (count != null) {
                                c = count.get(0) + 1;
                            }
                            context.setGlobalVariable("count", context.wrap(c));

                            TaskResult<Node> children = context.resultAsNodes();
                            if (children != null && children.size() != 0) {
                                context.continueWith(context.wrapClone(children.get(0)));
                            } else {
                                context.continueWith(null);
                            }
                        }
                    }).ifThen(new ConditionalFunction() {
                @Override
                public boolean eval(TaskContext context) {
                    int x = (int) context.variable("count").get(0);
                    return (x != 3);
                }
            }, traverse);

            Task mainTask = task().then(setTime("13")).then(setWorld("0")).then(inject(n1)).map(traverse);
            mainTask.execute(graph, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult result) {
                    if (result != null) {
                        result.free();
                    }
                    graph.save(null);
                    Assert.assertEquals(graph.space().available(), initcache);
                }

            });
            graph.save(null);
            Assert.assertTrue(graph.space().available() == initcache);
        });
    }*/

}
