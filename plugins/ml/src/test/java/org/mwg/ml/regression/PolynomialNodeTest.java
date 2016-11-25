package org.mwg.ml.regression;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.regression.PolynomialNode;

public class PolynomialNodeTest {
    private static final int size = 100;
    private static final double precision = 0.5;

    @Test
    public void testConstant() {
        final Graph graph = new GraphBuilder().withPlugin(new MLPlugin()).withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                /*
                try {
                    BlasMatrixEngine bme = (BlasMatrixEngine) Matrix.defaultEngine();
                    bme.setBlas(new F2JBlas());
                } catch (Exception ignored) {
                }*/
                long[] times = new long[size];
                double[] values = new double[size];
                //test degree 0
                for (int i = 0; i < size; i++) {
                    times[i] = i * 10 + 5000;
                    values[i] = 42.0;
                }
                testPoly(times, values, 1, graph);

                //test degree 1
                for (int i = 0; i < size; i++) {
                    values[i] = 3 * i - 20;
                }
                testPoly(times, values, 1, graph);

                //test degree 2
                for (int i = 0; i < size; i++) {
                    values[i] = 3 * i * i - 99 * i - 20;

                }
                testPoly(times, values, 1, graph);

                //test degree 5
                for (int i = 0; i < size; i++) {
                    values[i] = 2 * i * i * i * i * i - 1000 * i - 100000;
                }
                testPoly(times, values, 8, graph);

            }
        });
    }


    public static void testPoly(long[] times, final double[] values, final int numOfPoly, final Graph graph) {
        PolynomialNode polynomialNode = (PolynomialNode) graph.newTypedNode(0, times[0], PolynomialNode.NAME);
        polynomialNode.set(PolynomialNode.PRECISION, Type.DOUBLE, precision);

        for (int i = 0; i < size; i++) {
            final int ia = i;
            polynomialNode.travelInTime(times[ia], new Callback<PolynomialNode>() {
                @Override
                public void on(PolynomialNode result) {
                    result.learn(values[ia], new Callback<Boolean>() {
                        @Override
                        public void on(Boolean result) {

                        }
                    });
                }
            });
        }

        //System.out.println(polynomialNode);

        for (int i = 0; i < size; i++) {
            final int ia = i;
            polynomialNode.travelInTime(times[ia], new Callback<PolynomialNode>() {
                @Override
                public void on(PolynomialNode result) {
                    result.extrapolate(new Callback<Double>() {
                        @Override
                        public void on(Double v) {
                            Assert.assertTrue(Math.abs(values[ia] - v) <= precision);
                        }
                    });
                }
            });
        }

        polynomialNode.timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, new Callback<long[]>() {
            @Override
            public void on(long[] result) {
                Assert.assertTrue(result.length <= numOfPoly);
            }
        });
    }

}
