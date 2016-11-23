package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.struct.Relation;
import org.mwg.utility.HashHelper;

public class HelloWorldTest {

    @Test
    public void heapTest() {
        test(new GraphBuilder()/*.withScheduler(new NoopScheduler())*/.build());
    }

    public void test(final Graph graph) {
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean o) {

                final long available = graph.space().available();

                final org.mwg.Node node0 = graph.newNode(0, 0);

                //do something selectWith the node
                graph.lookup(0, 0, node0.id(), new Callback<org.mwg.Node>() {
                    @Override
                    public void on(org.mwg.Node result) {
                        //check that the lookup return the same
                        Assert.assertTrue(result.id() == node0.id());
                        result.free();

                        node0.set("name", Type.STRING, "MyName");
                        Assert.assertTrue(HashHelper.equals("MyName", node0.get("name").toString()));

                        node0.remove("name");
                        Assert.assertTrue(node0.get("name") == null);
                        node0.set("name", Type.STRING, "MyName");

                        node0.set("value", Type.STRING, "MyValue");
                        Assert.assertTrue(HashHelper.equals("MyValue", node0.get("value").toString()));
                        //check that other attribute name is not affected
                        Assert.assertTrue(HashHelper.equals("MyName", node0.get("name").toString()));

                        node0.set("name", Type.STRING, "MyName2");
                        Assert.assertTrue(HashHelper.equals("MyName2", node0.get("name").toString()));
                        Assert.assertTrue(HashHelper.equals("MyValue", node0.get("value").toString()));

                        //check the simple json print

                        String flatNode0 = "{\"world\":0,\"time\":0,\"id\":1,\"name\":\"MyName2\",\"value\":\"MyValue\"}";

                        Assert.assertTrue(flatNode0.length() == node0.toString().length());
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":1,\"name\":\"MyName2\",\"value\":\"MyValue\"}", node0.toString()));

                        //Create a new node
                        org.mwg.Node node1 = graph.newNode(0, 0);
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":2}", node1.toString()));

                        //attach the new node
                        node1.addToRelation("children", node0);
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":2,\"children\":[1]}", node1.toString()));

                        node1.addToRelation("children", node0);
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":2,\"children\":[1,1]}", node1.toString()));

                        org.mwg.Node node2 = graph.newNode(0, 0);
                        node1.addToRelation("children", node2);
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":2,\"children\":[1,1,3]}", node1.toString()));

                        Relation refValuesThree = (Relation) node1.get("children");
                        Assert.assertTrue(refValuesThree.size() == 3);
                        Assert.assertTrue(refValuesThree.get(0) == 1);
                        Assert.assertTrue(refValuesThree.get(1) == 1);
                        Assert.assertTrue(refValuesThree.get(2) == 3);

                        node1.relation("children", new Callback<org.mwg.Node[]>() {
                            @Override
                            public void on(Node[] resolvedNodes) {
                                Assert.assertTrue(resolvedNodes[0].id() == 1);
                                Assert.assertTrue(resolvedNodes[1].id() == 1);
                                Assert.assertTrue(resolvedNodes[2].id() == 3);

                                graph.freeNodes(resolvedNodes);

                                node1.removeFromRelation("children", node0);
                                Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":2,\"children\":[1,3]}", node1.toString()));

                                node1.removeFromRelation("children", node0);
                                Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":2,\"children\":[3]}", node1.toString()));

                                node1.removeFromRelation("children", node2);
                                Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":2,\"children\":[]}", node1.toString()));

                                //destroy the node explicitly selectWithout waiting GC
                                node0.free();
                                node1.free();
                                node2.free();


                                graph.save(new Callback<Boolean>() {
                                    @Override
                                    public void on(Boolean result) {
                                        long availableAfter = graph.space().available();
                                        Assert.assertEquals(available, availableAfter);

                                        graph.disconnect(new Callback<Boolean>() {
                                            @Override
                                            public void on(Boolean result) {
                                                //end of test

                                            }
                                        });
                                    }
                                });


                            }
                        });

                    }
                });

            }
        });

    }

}

