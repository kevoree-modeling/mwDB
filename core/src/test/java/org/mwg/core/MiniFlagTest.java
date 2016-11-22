package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;

public class MiniFlagTest {

    private long cacheSize = 10000;

    @Test
    public void heapTest() {
        flagTest(new GraphBuilder().withMemorySize(cacheSize).build());
    }

    private void flagTest(final Graph graph) {
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                long available = graph.space().available();

                Node node = graph.newNode(0, 0);
                node.set("name", Type.STRING, "hello");

                //   ((HeapChunkSpace) graph.space()).printMarked();

                graph.space().save(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {

                        // System.out.println("<=============>");

                        //   ((HeapChunkSpace) graph.space()).printMarked();

                        graph.lookup(0, 0, node.id(), new Callback<Node>() {
                            @Override
                            public void on(Node result) {
                                node.free();
                                result.free();
                                graph.save(new Callback<Boolean>() {
                                    @Override
                                    public void on(Boolean result) {
                                        long availableAfter = graph.space().available();
                                        Assert.assertEquals(available, availableAfter);
                                        graph.disconnect(new Callback<Boolean>() {
                                            @Override
                                            public void on(Boolean result) {

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
