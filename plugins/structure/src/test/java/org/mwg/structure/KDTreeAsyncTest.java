package org.mwg.structure;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.structure.distance.EuclideanDistance;
import org.mwg.structure.tree.KDTree;

import java.util.Random;

/**
 * @ignore ts
 */
public class KDTreeAsyncTest {
    @Test
    public void KDInsertTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                //.withScheduler(new NoopScheduler())
                .withMemorySize(100000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                KDTree testTask = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);
                testTask.set(KDTree.DISTANCE_THRESHOLD, Type.DOUBLE, 1e-30);

                KDTreeJava testjava = new KDTreeJava();
                testjava.setDistance(EuclideanDistance.instance());
                testjava.setThreshold(1e-30);

                final long initalcache = graph.space().available();


                int dim = 4;
                double[] vec = new double[dim];
                Random rand = new Random(125365L);
                int num = 100;
                graph.save(null);

                for (int i = 0; i < num; i++) {
                    double[] valuecop = new double[vec.length];
                    for (int j = 0; j < dim; j++) {
                        vec[j] = rand.nextDouble();
                        vec[j] = rand.nextDouble();
                        valuecop[j] = vec[j];
                    }

                    Node value = graph.newNode(0, 0);
                    value.set("value", Type.DOUBLE_ARRAY, valuecop);

                    testTask.insertWith(vec, value, null);
                    testjava.insert(vec, value, null);
                    value.free();
                }


                graph.save(null);
                long finalcache = graph.space().available();


                double[] key = new double[dim];
                for (int i = 0; i < dim; i++) {
                    key[i] = 0.1 * (i + 1);
                }

                testjava.nearestN(key, 8, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] result) {

                        testTask.nearestN(key, 8, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result3) {
                                for (int i = 0; i < result3.length; i++) {
                                    Assert.assertTrue(((Node) result[i]).id() == result3[i].id());
                                    result3[i].free();
                                }
                                graph.save(null);
                                long finalcache = graph.space().available();
                                // System.out.println("init " + initalcache + " end cache: " + finalcache);
                                Assert.assertTrue(finalcache == initalcache);
                                //System.out.println();
                            }
                        });
                    }
                });

                /*
                testTask.nearestN(key, 10, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result3) {
                        print(result3, key);
                    }
                });


                testTask.nearestNWithinRadius(key, 10, 0.4, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result3) {
                        print(result3, key);
                    }
                });


                testTask.nearestWithinRadius(key, 0.4, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result3) {
                        print(result3, key);
                    }
                });
                */


            }
        });
    }

/*
    public static void print(Node[] res, double[] key) {
        NumberFormat formatter = new DecimalFormat("#0.0000");
        EuclideanDistance ed = EuclideanDistance.INSTANCE;
        for (int i = 0; i < res.length; i++) {
            double[] val = (double[]) res[i].get("value");
            System.out.println(formatter.format(val[0]) + "," + formatter.format(val[1]) + "," + formatter.format(val[2]) + "," + formatter.format(val[3]) + "  dist: " + formatter.format(ed.measure(key, val)));
            res[i].free();
        }
        System.out.println();
    }*/

}
