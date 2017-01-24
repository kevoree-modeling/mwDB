package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.task.Tasks.newTask;

public class CoreTaskContextTests {

    @Test
    public void testArrayInTemplate() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                newTask()
                        .then(inject(4))
                        .then(defineAsGlobalVar("i"))
                        .then(inject(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}))
                        .then(defineAsGlobalVar("array"))
                        .then(readVar("array"))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext ctx) {
                                Assert.assertEquals("5", ctx.template("{{array[4]}}"));
                                Assert.assertEquals("9", ctx.template("{{result[8]}}"));
                                Assert.assertEquals("[1,2,3,4,5,6,7,8,9]", ctx.template("{{result}}"));
                                Assert.assertEquals("[1,2,3,4,5,6,7,8,9]", ctx.template("{{array}}"));

                                /*
                                boolean exceptionCaught = false;
                                try {
                                    context.template("{{result[]}}");
                                } catch (RuntimeException e) {
                                    exceptionCaught = true;
                                }
                                Assert.assertTrue(exceptionCaught);

                                exceptionCaught = false;
                                try {
                                    System.out.println(context.template("{{result[9]}}"));;
                                } catch (RuntimeException e) {
                                    exceptionCaught = true;
                                }
                                Assert.assertTrue(exceptionCaught);
*/
                                Assert.assertEquals("9.1", ctx.template("{{=((1 + 2) + (array[6] - 4) * 2) + 0.1 }}"));
                            }
                        })
                        .execute(graph, null);
            }
        });
    }


    @Test
    public void testVariableAsIndex() {
        Graph graph = new GraphBuilder().build();

        final String nodeVar = "node";
        final String intArrayVar = "intArray";
        final String indexVar = "index";

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                int[] intArray = new int[]{0,1,2,3,4,5,6,7,8,9};
                int index = 5;

                newTask()
                        .inject(intArray)
                        .setAsVar(intArrayVar)
                        .inject(index)
                        .setAsVar(indexVar)
                        .createNode()
                        .setAsVar(nodeVar)
                        .setAttribute("attribute", Type.INT,"{{" + intArrayVar + "[" + indexVar +"]}}")
                        .setAttribute("attribute2", Type.INT,"{{" + intArrayVar + "[" + index +"]}}")
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext ctx) {
                                Node n = (Node) ctx.variable(nodeVar).get(0);
                                Assert.assertEquals(intArray[index],n.get("attribute"));
                                Assert.assertEquals(intArray[index],n.get("attribute2"));
                                ctx.continueTask();
                            }
                        })
                        .execute(graph,null);


            }
        });
    }
}
