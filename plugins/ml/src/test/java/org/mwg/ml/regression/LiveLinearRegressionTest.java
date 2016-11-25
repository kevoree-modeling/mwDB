package org.mwg.ml.regression;

import org.mwg.*;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.RegressionNode;
import org.mwg.ml.algorithm.regression.LiveLinearRegressionNode;

import java.util.Random;

public class LiveLinearRegressionTest {
    // @Test
    public void testRegression() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                Node MultiSensor = graph.newNode(0, 0);
                MultiSensor.set("temperature", Type.INT, 20);
                MultiSensor.set("humidity", Type.INT, 5);
                MultiSensor.set("power", Type.INT, 7);

                RegressionNode learningNode = (RegressionNode) graph.newTypedNode(0, 0, LiveLinearRegressionNode.NAME);
                learningNode.addToRelation("sensor", MultiSensor);
                learningNode.set("from", Type.STRING, "sensor.temperature; sensor.humidity; sensor.power");
                learningNode.set(LiveLinearRegressionNode.ALPHA_KEY, Type.DOUBLE, 0.00001);
                learningNode.set(LiveLinearRegressionNode.LAMBDA_KEY, Type.DOUBLE, 0.0001d);
                learningNode.set(LiveLinearRegressionNode.ITERATION_KEY, Type.DOUBLE, 10);
                MultiSensor.addToRelation("regression", learningNode);

                final Random random = new Random();
                final double coef[] = {2, -3, -2, 5};
                int size = 100;

                for (int i = 0; i < size; i++) {
                    MultiSensor.travelInTime(i + 1, new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            double temp = random.nextDouble() * 10 + 15; //random between 15 and 25
                            double humidity = random.nextDouble() * 10 + 15; //random between 5 and 10
                            double power = random.nextDouble() * 10 + 15; //random between 30 and 40

                            final double corr = temp * coef[0] + humidity * coef[1] + power * coef[2] + coef[3];

                            result.set("temperature", Type.DOUBLE, temp);
                            result.set("humidity", Type.DOUBLE, humidity);
                            result.set("power", Type.DOUBLE, power);

                            result.relation("regression", new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    RegressionNode regressionNode = (RegressionNode) (result[0]);
                                    regressionNode.learn(corr, null);
                                    regressionNode.free();
                                }
                            });

                            result.free();
                        }
                    });

                }

                final double[] cumerr = new double[1];
                cumerr[0] = 0;

                int test = 1000;
                for (int i = size; i < size + test; i++) {
                    MultiSensor.travelInTime(i + 1, new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            double temp = random.nextDouble() * 10 + 15; //random between 15 and 25
                            double humidity = random.nextDouble() * 5 + 5; //random between 5 and 10
                            double power = random.nextDouble() * 600 + 200; //random between 200 and 800

                            final double corr = temp * coef[0] + humidity * coef[1] + power * coef[2] + coef[3];

                            result.set("temperature", Type.DOUBLE, temp);
                            result.set("humidity", Type.DOUBLE, humidity);
                            result.set("power", Type.DOUBLE, power);

                            result.relation("regression", new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    RegressionNode regressionNode = (RegressionNode) (result[0]);
                                    regressionNode.extrapolate(new Callback<Double>() {
                                        @Override
                                        public void on(Double result) {
                                            cumerr[0] += Math.abs(result - corr);
                                        }
                                    });
                                    regressionNode.free();
                                }
                            });

                            result.free();
                        }
                    });

                }

                cumerr[0] = cumerr[0] / test;

                // System.out.println("Avg error: " + cumerr[0]);
                graph.disconnect(null);
            }
        });

    }
}
