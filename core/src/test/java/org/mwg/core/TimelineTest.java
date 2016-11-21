package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.utility.HashHelper;

public class TimelineTest {

    @Test
    public void heapTest() {
        test(new GraphBuilder().withScheduler(new NoopScheduler()).build());
        test2(new GraphBuilder().withScheduler(new NoopScheduler()).build());
    }

    private void test2(final Graph g) {
        g.connect(result -> {
            Node n = g.newNode(0, 0);
            n.setAttribute("name", Type.STRING, "name");

            n.jump(1, n_t1 -> {
                //should be effect less
                n_t1.setAttribute("name", Type.STRING, "name");
                Assert.assertEquals(n_t1.timeDephasing(), 1);
                n_t1.setAttribute("name", Type.STRING, "newName");
                Assert.assertEquals(n_t1.timeDephasing(), 0);
            });
        });
    }

    private void test(final Graph graph) {
        final int[] counter = {0};
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean o) {


                final org.mwg.Node node_t0 = graph.newNode(0, 0);
                //timeTree should be already filled
                node_t0.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
                    @Override
                    public void on(long[] longs) {
                        counter[0]++;
                        Assert.assertTrue(longs.length == 1);
                        Assert.assertTrue(longs[0] == 0);
                    }
                });
                //do a simple modification
                node_t0.setAttribute("name", Type.STRING, "MyName");
                Assert.assertTrue(node_t0.timeDephasing() == 0);
                //check the unmodified time tree
                node_t0.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
                    @Override
                    public void on(long[] longs) {
                        counter[0]++;
                        Assert.assertTrue(longs.length == 1);
                        Assert.assertTrue(longs[0] == 0);
                    }
                });

                graph.lookup(node_t0.world(), 1, node_t0.id(), new Callback<org.mwg.Node>() {
                    @Override
                    public void on(final org.mwg.Node node_t1) {
                        counter[0]++;
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":1,\"id\":1,\"name\":\"MyName\"}", node_t1.toString()));
                        Assert.assertTrue(node_t1.timeDephasing() == 1); //node hasField a dephasing of 1 selectWith last known state
                        node_t1.rephase(); // force the object to move to timepoint 1
                        Assert.assertTrue(node_t1.timeDephasing() == 0); //node should be in phase now
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":1,\"id\":1,\"name\":\"MyName\"}", node_t1.toString()));

                        node_t1.setAttribute("name", Type.STRING, "MyName@t1");
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":1,\"id\":1,\"name\":\"MyName@t1\"}", node_t1.toString()));
                        Assert.assertTrue(HashHelper.equals("{\"world\":0,\"time\":0,\"id\":1,\"name\":\"MyName\"}", node_t0.toString()));

                        node_t1.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
                            @Override
                            public void on(long[] longs) {
                                counter[0]++;
                                Assert.assertTrue(longs.length == 2);
                                Assert.assertTrue(longs[0] == 1);
                                Assert.assertTrue(longs[1] == 0);
                            }
                        });

                        node_t1.timepoints(1, Constants.END_OF_TIME, new Callback<long[]>() {
                            @Override
                            public void on(long[] longs) {
                                counter[0]++;
                                Assert.assertTrue(longs.length == 1);
                                Assert.assertTrue(longs[0] == 1);
                            }
                        });

                        //now try to diverge the world
                        long newWorld = graph.fork(0);
                        graph.lookup(newWorld, 2, node_t0.id(), new Callback<org.mwg.Node>() {
                            @Override
                            public void on(Node node_t1_w0) {
                                Assert.assertTrue(HashHelper.equals("{\"world\":1,\"time\":2,\"id\":1,\"name\":\"MyName@t1\"}", node_t1_w0.toString()));
                                Assert.assertTrue(node_t1_w0.timeDephasing() == 1);

                                node_t1_w0.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
                                    @Override
                                    public void on(long[] longs) {
                                        counter[0]++;
                                        Assert.assertTrue(longs.length == 2);
                                        Assert.assertTrue(longs[0] == 1);
                                        Assert.assertTrue(longs[1] == 0);
                                    }
                                });
                                node_t1_w0.setAttribute("name", Type.STRING, "MyName@t1@w1");
                                Assert.assertTrue(HashHelper.equals("{\"world\":1,\"time\":2,\"id\":1,\"name\":\"MyName@t1@w1\"}", node_t1_w0.toString()));
                                //test the new timeline
                                node_t1_w0.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
                                    @Override
                                    public void on(long[] longs) {
                                        counter[0]++;
                                        Assert.assertTrue(longs.length == 3);
                                        Assert.assertTrue(longs[0] == 2);
                                        Assert.assertTrue(longs[1] == 1);
                                        Assert.assertTrue(longs[2] == 0);
                                    }
                                });
                                //test the old timeline
                                node_t1.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
                                    @Override
                                    public void on(long[] longs) {
                                        counter[0]++;
                                        Assert.assertTrue(longs.length == 2);
                                        Assert.assertTrue(longs[0] == 1);
                                        Assert.assertTrue(longs[1] == 0);
                                    }
                                });


                            }
                        });


                    }
                });


                //end of the test
                graph.disconnect(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        //test end
                    }
                });

            }
        });
        Assert.assertTrue(counter[0] == 8);
    }

}
