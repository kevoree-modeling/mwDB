package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;

public class FlagTest {

    private long cacheSize = 10000;

    @Test
    public void heapTest() {
        manyWorldTest(new GraphBuilder().withMemorySize(cacheSize).withScheduler(new NoopScheduler()).build());
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

        manyWorldTest(new GraphBuilder().withScheduler(new NoopScheduler()).withOffHeapMemory().withMemorySize(cacheSize).saveEvery(cacheSize - 100).build());

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
        */
    }

    private void manyWorldTest(final Graph graph) {
        final FlagTest selfPointer = this;

        final int[] counter = {0};
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean o) {

                final long av_init = graph.space().available();

                org.mwg.Node node_t0 = graph.newNode(0, 0);
                long node_id = node_t0.id();
                node_t0.set("name", Type.STRING, "MyName");

                Assert.assertEquals(graph.space().available(), selfPointer.cacheSize - (4 + (1 * 4)));

                node_t0.free();

                Assert.assertEquals(graph.space().available(), selfPointer.cacheSize - (4 + (1 * 4)));

                graph.save(null);

                Assert.assertEquals(graph.space().available(), selfPointer.cacheSize - (4 + (0 * 4)));

                long newWorld = graph.fork(0);
                graph.lookup(newWorld, 0, node_id, new Callback<Node>() {
                    @Override
                    public void on(Node n0_w1) {
                        Assert.assertEquals(graph.space().available(), selfPointer.cacheSize - (4 + (1 * 4))); //chunk should be tagged again
                        counter[0]++;

                        n0_w1.free();
                        Assert.assertEquals(graph.space().available(), selfPointer.cacheSize - (4 + (0 * 4))); //immediatly free because transient modification

                    }
                });

                graph.lookup(newWorld, 0, node_id, new Callback<Node>() {
                    @Override
                    public void on(Node n0_w1_bis) {
                        Assert.assertEquals(graph.space().available(), selfPointer.cacheSize - (4 + (1 * 4))); //chunk should be tagged again
                        counter[0]++;

                        n0_w1_bis.set("name", Type.STRING, "MyDivergedName");

                        n0_w1_bis.free();

                        Assert.assertEquals(graph.space().available(), selfPointer.cacheSize - (4 + (1 * 4)));

                        graph.save(null);
                        long av_final = graph.space().available();
                        Assert.assertEquals(av_init, av_final);

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
        Assert.assertTrue(counter[0] == 2);
    }

}
