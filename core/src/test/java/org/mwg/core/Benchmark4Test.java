package org.mwg.core;

import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.chunk.Chunk;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.core.chunk.heap.HeapChunkSpace;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.utility.HashHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @ignore ts
 */
public class Benchmark4Test {

   // @Test
    public void test() {
        int nb = 3000000;
        long init = System.currentTimeMillis();
        HeapChunkSpace space = new HeapChunkSpace(nb * 2, null);
        for (int i = 0; i < nb; i++) {
            Chunk c = space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, i);
            //space.putAndMark(ChunkType.STATE_CHUNK, 0, 0, i, c);
            //temp.put(i, c);

        }
        long begin = System.currentTimeMillis();
        for (int i = 0; i < nb; i++) {
            Chunk c = space.getAndMark(ChunkType.STATE_CHUNK, 0, 0, i);
            space.unmark(c.index());
        }
        long after = System.currentTimeMillis();
        System.out.println("total " + (after - init) + "ms, " + ((nb / 1000) / ((double) (after - init) / 1000d)) + " k chunk/s");
        System.out.println("insert " + (begin - init) + "ms, " + ((nb / 1000) / ((double) (begin - init) / 1000d)) + " k chunk/s");
        System.out.println("lookup " + (after - begin) + "ms, " + ((nb / 1000) / ((double) (after - begin) / 1000d)) + " k chunk/s");
    }

    // @Test
    public void test2() {
        /*
        int nb = 3000000;
        long init = System.currentTimeMillis();
        OffHeapChunkSpace space = new OffHeapChunkSpace(nb * 2, nb * 2);
        for (int i = 0; i < nb; i++) {
            StateChunk c = (StateChunk) space.create(ChunkType.STATE_CHUNK, 0, 0, i, null, null);
            space.putAndMark(ChunkType.STATE_CHUNK, 0, 0, i, c);
        }
        long begin = System.currentTimeMillis();
        for (int i = 0; i < nb; i++) {
            StateChunk c = (StateChunk) space.getAndMark(ChunkType.STATE_CHUNK, 0, 0, i);
            if (c != null) {
                space.unmarkChunk(c);
            }
        }
        long after = System.currentTimeMillis();
        System.out.println("total " + (after - init) + "ms, " + ((nb / 1000) / ((double) (after - init) / 1000d)) + " k chunk/s");
        System.out.println("insert " + (begin - init) + "ms, " + ((nb / 1000) / ((double) (begin - init) / 1000d)) + " k chunk/s");
        System.out.println("lookup " + (after - begin) + "ms, " + ((nb / 1000) / ((double) (after - begin) / 1000d)) + " k chunk/s");
        */
    }

    //  @Test
    public void test3() {
        int nb = 1000000;
        HeapChunkSpace space = new HeapChunkSpace(nb, null);
        Map<Long, Chunk> map = new HashMap<Long, Chunk>();
        for (int i = 0; i < nb; i++) {
            long hashed = HashHelper.tripleHash(ChunkType.STATE_CHUNK, 0, 0, i, nb);
            map.put(hashed, space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, i));
        }
        long begin = System.currentTimeMillis();
        for (int i = 0; i < nb; i++) {
            long hashed = HashHelper.tripleHash(ChunkType.STATE_CHUNK, 0, 0, i, nb);
            Chunk o = map.get(hashed);
            if (o instanceof StateChunk) {

            }
        }
        long after = System.currentTimeMillis();
        long diff = after - begin;
        double diffSecond = diff / 1000d;
        System.out.println((nb / 1000) / diffSecond);
    }

    // @Test
    public void testlookup() {
        Graph graph = new GraphBuilder()
                .withMemorySize(5000000)
                .withScheduler(new NoopScheduler())
                .build();

        final int nb = 1000000;
        graph.connect(result -> {
            Node root = graph.newNode(0, 0);
            Random rand = new Random();
            long begin = System.currentTimeMillis();
            for (int i = 0; i < nb; i++) {
                Node x = graph.newNode(0, 0);
                x.set("value", Type.DOUBLE, rand.nextDouble());
                root.addToRelation("children", x);
                x.free();
            }
            long after = System.currentTimeMillis();
            long diff = after - begin;
            double diffSecond = diff / 1000d;
            System.out.println((nb / 1000) / diffSecond);

        });
    }

}
