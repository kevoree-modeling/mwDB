package org.mwg.core.task;


import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.task.*;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.newTask;

public class ActionWhileDoTest extends AbstractActionTest {

    @Test
    public void testwhileDo() {
        initComplexGraph(new Callback<Node>() {
            @Override
            public void on(Node root) {

                final long cache1 = graph.space().available();

                Task whiletask = newTask()
                        .then(inject(root))
                        .whileDo(context -> context.result().size() != 0,
                                newTask().flatMap(
                                        newTask().ifThenElse(context -> context.resultAsNodes().get(0).get("child") != null,
                                                newTask().then(traverse("child")),
                                                newTask().thenDo(context -> {
                                                    //System.out.println("if is false");
                                                    context.addToGlobalVariable("leaves", context.wrap(context.resultAsNodes().get(0).id()));
                                                    context.continueWith(null);
                                                })
                                        )
                                )
                        ).then(readVar("leaves"));

                whiletask.execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        //System.out.println(result.toString());
                        Assert.assertEquals(result.toString(), "[4,5,7,8]");
                        graph.save(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {
                                long cache2 = graph.space().available();
                                Assert.assertTrue(cache1 == cache2);
                            }
                        });
                    }
                });


            }
        });
    }


    @Test
    public void testdoWhile() {
        initComplexGraph(new Callback<Node>() {
            @Override
            public void on(Node root) {

                final long cache1 = graph.space().available();
                Task whiletask = newTask().then(inject(root)).doWhile(
                        newTask().flatMap(newTask().ifThenElse(new ConditionalFunction() {
                            @Override
                            public boolean eval(TaskContext context) {
                                return context.resultAsNodes().get(0).get("child") != null;
                            }
                        }, newTask().then(traverse("child")), newTask().thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                //System.out.println("if is false");
                                context.addToGlobalVariable("leaves", context.wrap(context.resultAsNodes().get(0).id()));
                                context.continueWith(null);
                            }
                        }))),
                        new ConditionalFunction() {
                            @Override
                            public boolean eval(TaskContext context) {
                                //System.out.println("condition while");
                                return context.result().size() != 0;
                            }
                        }
                ).then(readVar("leaves"));


                whiletask.execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        //System.out.println(result.toString());
                        Assert.assertEquals(result.toString(), "[4,5,7,8]");
                        graph.save(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {
                                long cache2 = graph.space().available();
                                Assert.assertTrue(cache1 == cache2);
                            }
                        });
                    }
                });


            }
        });
    }


}
