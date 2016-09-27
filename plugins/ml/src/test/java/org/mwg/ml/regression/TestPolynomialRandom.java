package org.mwg.ml.regression;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.regression.PolynomialNode;

import java.util.Random;

/**
 * Created by assaad on 08/06/16.
 */
public class TestPolynomialRandom {
    @Test
    public void randomTest() {
        final Graph graph = new GraphBuilder().withPlugin(new MLPlugin()).withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                double precision = 0.001;
                int size = 1000;

                long seed = 1545436547678348l;
                //Random random = new Random(seed);
                Random random = new Random();
                final double[] values = new double[size];
                final double[] error = new double[size];
                final double[] poly = new double[size];

                PolynomialNode polynomialNode = (PolynomialNode) graph.newTypedNode(0, 1, PolynomialNode.NAME);
                polynomialNode.set(PolynomialNode.PRECISION, precision);

                long start = System.currentTimeMillis();
                for (int i = 0; i < size; i++) {
                    values[i] = random.nextDouble();
                    final int finalI = i;
                    polynomialNode.jump(i + 1, new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            PolynomialNode x = (PolynomialNode) result;
                            x.learn(values[finalI], null);
                        }
                    });
                }
                long end = System.currentTimeMillis() - start;
                //System.out.println("total time: " + end + " ms");


                final double[] res = new double[3];


                for (int i = 0; i < size; i++) {
                    final int finalI = i;
                    polynomialNode.jump(i + 1, new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            PolynomialNode x = (PolynomialNode) result;
                            x.extrapolate(new Callback<Double>() {
                                @Override
                                public void on(Double result) {
                                    poly[finalI] = result;
                                    error[finalI] = Math.abs(result - values[finalI]);
                                    if (error[finalI] > res[0]) {
                                        res[0] = error[finalI];
                                    }
                                    res[1] += error[finalI];
                                }
                            });
                        }
                    });
                }

                polynomialNode.timepoints(0, size + 3, new Callback<long[]>() {
                    @Override
                    public void on(long[] result) {
                        res[2] = result.length;
                    }
                });

                res[1] = res[1] / size;

                Assert.assertTrue(res[0] <= precision);
                Assert.assertTrue(res[2] < size);

//                System.out.println("Max error: "+res[0]);
//                System.out.println("Avg error: "+res[1]);
//                System.out.println("Created: "+res[2]+" out of "+size);
//                res[2]=(1-res[2]/size)*100;
//                System.out.println("Compression rate: "+res[2]+"%");

           /*     try {
                    PrintWriter pw=new PrintWriter(new FileWriter("polynomial.csv"));
                    for (int i = 0; i < size; i++) {
                        pw.println(values[i]+","+poly[i]+","+error[i]);
                    }
                    pw.flush();
                    pw.close();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }*/


            }
        });

    }
}
