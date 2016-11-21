package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.task.Task;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.asVar;
import static org.mwg.core.task.Actions.fromVar;
import static org.mwg.core.task.CoreTask.task;

public class DFSTest {

    private void baseGrap(Callback<Node> callback) {
        Graph graph = new GraphBuilder()
                .withMemorySize(30000)
                .build();

        graph.connect(result -> {
            Node n1 = graph.newNode(0, 0);
            n1.set("name", "n1");

            graph.save(null);
            long initcache = graph.space().available();

            Node n2 = graph.newNode(0, 0);
            n2.set("name", "n2");

            Node n3 = graph.newNode(0, 0);
            n3.set("name", "n3");

            n1.add("left", n2);
            n1.add("right", n3);

            Node n4 = graph.newNode(0, 0);
            n4.set("name", "n4");
            n2.add("left", n4);


            Node n5 = graph.newNode(0, 0);
            n5.set("name", "n5");
            n3.add("left", n5);

            Node n6 = graph.newNode(0, 0);
            n6.set("name", "n6");
            n3.add("right", n6);


            Node n7 = graph.newNode(0, 0);
            n7.set("name", "n7");
            n6.add("left", n7);

            Node n8 = graph.newNode(0, 0);
            n8.set("name", "n8");
            n6.add("right", n8);

            n2.free();
            n3.free();
            n4.free();
            n5.free();
            n6.free();
            n7.free();
            n8.free();
            graph.save(null);
            Assert.assertTrue(graph.space().available() == initcache);

            callback.on(n1);
        });
    }


    @Test
    public void traverse() {
        baseGrap(n1 -> {

            if (n1 != null) {
                //DO BFS from n1
                Task dfs = task();
                dfs.forEach(
                        task()
                                .then(asVar("parent"))
                                .then(Actions.traverse("left"))
                                .then(asVar("left"))
                                .then(fromVar("parent"))
                                .then(Actions.traverse("right"))
                                .then(asVar("right"))
                                .thenDo(context -> {
                                    Node left = null;
                                    if (context.variable("left").size() > 0) {
                                        left = (Node) context.variable("left").get(0);
                                    }
                                    Node right = null;
                                    if (context.variable("right").size() > 0) {
                                        right = (Node) context.variable("right").get(0);
                                    }
                                    TaskResult<Node> nextStep = context.newResult();
                                    if (left != null && right != null) {
                                        if (left.id() < right.id()) {
                                            nextStep.add(left.graph().cloneNode(left));
                                            nextStep.add(right.graph().cloneNode(right));
                                        } else {
                                            nextStep.add(left.graph().cloneNode(left));
                                            nextStep.add(right.graph().cloneNode(right));
                                        }
                                    } else if (left != null) {
                                        nextStep.add(left.graph().cloneNode(left));
                                    }
                                    if (left != null) {
                                        context.addToGlobalVariable("nnl", context.wrap(left.id()));
                                        context.addToGlobalVariable("nnld", context.wrap(left.id() / 2));
                                    }
                                    context.continueWith(nextStep);
                                }).ifThen(context -> (context.result().size() > 0), dfs).thenDo(context -> context.continueTask())).then(fromVar("nnl"));

                TaskResult initialResult = task().emptyResult();
                initialResult.add(n1);

                dfs/*.hook(VerboseHook.instance())/*/ /*.hook(VerboseHook.instance())/*.hook(new TaskHook() {
                    @Override
                    public void on(Action previous, Action next, TaskContext context) {
                        System.out.println(next);
                    }
                })*/.executeWith(n1.graph(), initialResult, result -> Assert.assertEquals(result.toString(), "[2,4,5,7]"));

            }

        });
    }


}
