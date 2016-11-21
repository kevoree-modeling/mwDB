package org.mwg.core;

import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.plugin.Job;

public class Benchmark2Test {

    /**
     * @ignore ts
     */
    //@Test
    public void heapTest() {
        final Graph graph = new GraphBuilder().withScheduler(new NoopScheduler()).withMemorySize(100).build();
        test(graph, new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                graph.disconnect(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        System.out.println("Graph disconnected");
                    }
                });
            }
        });

    }

    /**
     * @ignore ts
     */
    //@Test
    /*
    public void offHeapTest() {
        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        final Graph graph = new GraphBuilder().withScheduler(new NoopScheduler()).withOffHeapMemory().withMemorySize(100).saveEvery(20).build();

        test(graph, new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                graph.disconnect(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        System.out.println("Graph disconnected");

                        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
                        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
                        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
                        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
                    }
                });
            }
        });
    }*/

    //final int valuesToInsert = 10000000;
    final int valuesToInsert = 500;

    final long timeOrigin = 1000;

    /**
     * @ignore ts
     */
    private void test(final Graph graph, final Callback<Boolean> testEnd) {
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                final long before = System.currentTimeMillis();

                org.mwg.Node temp_node = graph.newNode(0, 0);
                long nodeID = temp_node.id();
                temp_node.free();

                //Node node = graph.newNode(0, 0);
                final DeferCounter counter = graph.newCounter(valuesToInsert);
                for (long i = 0; i < valuesToInsert; i++) {

                    if (i % 10 == 0) {
                        //node.free();
                        //node = graph.newNode(0, 0);

                        temp_node = graph.newNode(0, 0);
                        nodeID = temp_node.id();
                        temp_node.free();

                        System.out.println(i + " node>" + nodeID);
                    }

                    if (i % 1000000 == 0) {
                        System.out.println("<insert til " + i + " in " + (System.currentTimeMillis() - before) / 1000 + "s");
                    }

                    final double value = i * 0.3;
                    final long time = timeOrigin + i;
                    final long finalNodeID = nodeID;
                    graph.lookup(0, time, nodeID, new Callback<org.mwg.Node>() {
                        @Override
                        public void on(org.mwg.Node timedNode) {

                            if (timedNode == null) {
                                graph.lookup(0, time, finalNodeID, new Callback<org.mwg.Node>() {
                                    @Override
                                    public void on(Node timedNode) {
                                        timedNode.setAttribute("value", Type.DOUBLE, value);
                                        counter.count();
                                        timedNode.free();//free the node, for cache management
                                    }
                                });
                            }


                            timedNode.setAttribute("value", Type.DOUBLE, value);
                            counter.count();
                            timedNode.free();//free the node, for cache management
                        }
                    });
                }
                // node.free();


                counter.then(new Job() {
                    @Override
                    public void run() {

                        long beforeRead = System.currentTimeMillis();

                        //System.out.println("<end insert phase>" + " " + (System.currentTimeMillis() - before) / 1000 + "s");
                        //System.out.println("result: " + (valuesToInsert / ((System.currentTimeMillis() - before) / 1000) / 1000) + "kv/s");

                        /*
                        final CoreDeferCounter counterRead = graph.counter(valuesToInsert);
                        for (long i = 0; i < valuesToInsert; i++) {
                            final double value = i * 0.3;
                            final long time = timeOrigin + i;

                            graph.lookup(0, time, node.id(), new Callback<Node>() {
                                @Override
                                public void on(Node timedNode) {
                                    Assert.assertTrue((double) timedNode.get("value") == value);
                                    counterRead.count();
                                    timedNode.free();//free the node, for cache management
                                }
                            });
                        }
                        counterRead.then(new Callback() {
                            @Override
                            public void on(Object result) {
                                long afterRead = System.currentTimeMillis();
                                System.out.println("<end read phase>" + " " + (afterRead - beforeRead) / 1000 + "s ");
                                System.out.println("result: " + (valuesToInsert / ((afterRead - beforeRead) / 1000) / 1000) + "kv/s");

                            }
                        });
*/

                        testEnd.on(true);


                    }
                });

            }
        });
    }

}
