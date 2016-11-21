package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.TimeTreeChunk;
import org.mwg.core.scheduler.NoopScheduler;

public class SuperTimeTest {

    @Test
    public void heapTest() {
        test(new GraphBuilder().withScheduler(new NoopScheduler()).build());
    }

    /**
     * @ignore ts
     */
    @Test
    public void offHeapTest() {
        /*
        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        test(new GraphBuilder().withScheduler(new NoopScheduler()).withOffHeapMemory().withMemorySize(10000).saveEvery(20).build());

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
        */
    }

    private void test(final org.mwg.Graph graph) {
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean o) {

                org.mwg.Node node_t0 = graph.newNode(0, 0);
                for (int i = 0; i < CoreConstants.SCALE_1; i++) {
                    final int finalI = i;
                    graph.lookup(0, i, node_t0.id(), new Callback<org.mwg.Node>() {
                        @Override
                        public void on(org.mwg.Node result) {
                            result.setAttribute("time", Type.INT, finalI);
                        }
                    });
                }

                ChunkSpace space = (graph).space();

                TimeTreeChunk superTimeTree = (TimeTreeChunk) space.getAndMark(ChunkType.TIME_TREE_CHUNK, 0, Constants.NULL_LONG, node_t0.id());
                Assert.assertTrue(superTimeTree != null);
                long superTimeTreeSize = superTimeTree.size();
                Assert.assertTrue(superTimeTreeSize == 1); // TimeTree is not splitted

                //insert a new timePoint
                graph.lookup(0, CoreConstants.SCALE_1, node_t0.id(), new Callback<org.mwg.Node>() {
                    @Override
                    public void on(org.mwg.Node result) {
                        result.setAttribute("time", Type.INT, (int) CoreConstants.SCALE_1);
                    }
                });

                long superTimeTreeSize2 = superTimeTree.size();
                Assert.assertTrue(superTimeTreeSize2 == 1); // TimeTree is still not splitted (split at 2n)

                //insert the rest
                for (int i = ((int) CoreConstants.SCALE_1 + 1); i < (CoreConstants.SCALE_1 * 2); i++) {
                    final int finalI = i;
                    graph.lookup(0, i, node_t0.id(), new Callback<org.mwg.Node>() {
                        @Override
                        public void on(org.mwg.Node result) {
                            result.setAttribute("time", Type.INT, finalI);
                        }
                    });
                }

                long superTimeTreeSize3 = superTimeTree.size();
                Assert.assertTrue(superTimeTreeSize3 == 2);

                for (int i = ((int) CoreConstants.SCALE_1 * 2); i < (CoreConstants.SCALE_1 * 5); i++) {
                    final int finalI = i;
                    graph.lookup(0, i, node_t0.id(), new Callback<org.mwg.Node>() {
                        @Override
                        public void on(org.mwg.Node result) {
                            result.setAttribute("time", Type.INT, finalI);
                        }
                    });
                }

                long superTimeTreeSize5 = superTimeTree.size();
                Assert.assertTrue(superTimeTreeSize5 == 5); // TimeTree is splitted in two

                //unmark the superTimeTree
                space.unmark(superTimeTree.index());

                //test that lookup are corrects
                for (int i = 0; i < (CoreConstants.SCALE_1 * 5); i++) {
                    final int finalI = i;
                    graph.lookup(0, i, node_t0.id(), new Callback<org.mwg.Node>() {
                        @Override
                        public void on(Node result) {
                            Assert.assertTrue((Integer) result.get("time") == finalI);
                        }
                    });
                }

                //test the range now
                node_t0.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
                    @Override
                    public void on(long[] result) {
                        for (int i = 0; i < (CoreConstants.SCALE_1 * 5); i++) {
                            result[i] = (CoreConstants.SCALE_1 * 5) + 1 - i;
                        }
                    }
                });

                graph.disconnect(null);

            }
        });
    }

}
