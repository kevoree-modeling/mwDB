package org.kevoree.modeling.extrapolation;

import org.junit.Assert;
import org.junit.Test;
import org.kevoree.modeling.KCallback;
import org.kevoree.modeling.KObject;
import org.kevoree.modeling.cloudmodel.CloudUniverse;
import org.kevoree.modeling.cloudmodel.CloudModel;
import org.kevoree.modeling.cloudmodel.CloudView;
import org.kevoree.modeling.cloudmodel.Node;
import org.kevoree.modeling.cloudmodel.Element;
import org.kevoree.modeling.cloudmodel.meta.MetaElement;
import org.kevoree.modeling.defer.KDefer;
import org.kevoree.modeling.memory.manager.DataManagerBuilder;
import org.kevoree.modeling.scheduler.impl.DirectScheduler;

import java.util.concurrent.CountDownLatch;

public class PolynomialKMFTest {

    @Test
    public void test() throws InterruptedException {
        final int[] nbAssert = new int[1];
        nbAssert[0] = 0;
        CloudModel model = new CloudModel(DataManagerBuilder.create().withScheduler(new DirectScheduler()).build());
        model.connect(new KCallback() {
            @Override
            public void on(Object o) {
                CloudUniverse dimension0 = model.newUniverse();
                final double[] val = new double[1000];
                double[] coef = {2, 2, 3};
                CloudView t0 = dimension0.time(0l);
                Node node = t0.createNode();
                node.setName("n0");
                t0.setRoot(node, null);
                final Element element = t0.createElement();
                element.setName("e0");
                node.addElement(element);
                KDefer waiter = model.defer();
                for (int i = 200; i < 1000; i++) {
                    dimension0.time(i).lookup(element.uuid(), waiter.waitResult());
                }
                waiter.then(new KCallback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        double val[] = new double[objects.length];
                        for (int i = 200; i < 1000; i++) {
                            long temp = 1;
                            val[i - 200] = 0;
                            for (int j = 0; j < coef.length; j++) {
                                val[i - 200] = val[i - 200] + coef[j] * temp;
                                temp = temp * i;
                            }
                            ((Element) objects[i - 200]).setValue(val[i - 200]);
                        }
                        element.allTimes(new KCallback<long[]>() {
                            @Override
                            public void on(long[] collected) {
                                Assert.assertEquals(2, collected.length);
                                nbAssert[0]++;
                                KDefer defer2 = model.defer();
                                for (int i = 200; i < 1000; i++) {
                                    element.jump(i, defer2.waitResult());
                                }
                                defer2.then(new KCallback<Object[]>() {
                                    @Override
                                    public void on(Object[] objects) {

                                        for (int i = 200; i < 1000; i++) {
                                            nbAssert[0]++;
                                            Assert.assertTrue(Math.abs((((Element) objects[i - 200]).getValue() - val[i - 200])) < 5);
                                        }
                                        Assert.assertEquals(nbAssert[0], 801);
                                        element.allTimes(new KCallback<long[]>() {
                                            @Override
                                            public void on(long[] collected2) {
                                                Assert.assertEquals(2, collected2.length);
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


    @Test
    public void test2() {
        final int[] nbAssert = new int[1];
        nbAssert[0] = 0;
        int max = 1000;
        // int max = 25;
        CloudModel model = new CloudModel(DataManagerBuilder.create().withScheduler(new DirectScheduler()).build());
        model.connect(new KCallback() {
            @Override
            public void on(Object o) {

                CloudUniverse dimension0 = model.newUniverse();
                final double[] val = {787, 301, 298, 254, 755, 780, 788, 782, 273, 301, 310, 296, 272, 309, 277, 782, 22, 27, 785, 785, 300, 782, 302, 783, 308, 782, 786, 302, 785, 301, 784, 303, 262, 298, 78, 302, 785, 779, 787, 310, 299, 302, 305, 303, 788, 783, 296, 788, 300, 289, 42, 783, 42, 296, 783, 782, 783, 786, 782, 787, 787, 243, 785, 310, 277, 786, 61, 22, 784, 305, 783, 787, 786, 785, 733, 782, 781, 784, 311, 786, 28, 782, 781, 84, 301, 787, 267, 43, 783, 783, 787, 786, 44, 787, 783, 781, 309, 782, 310, 785, 300, 36, 308, 788, 306, 92, 785, 781, 781, 785, 787, 782, 786, 298, 309, 787, 788, 784, 784, 333, 27, 297, 306, 309, 782, 787, 782, 275, 784, 786, 308, 784, 782, 295, 310, 292, 783, 306, 305, 42, 300, 786, 296, 301, 784, 306, 786, 71, 301, 784, 299, 300, 787, 301, 310, 783, 309, 782, 43, 784, 298, 309, 785, 788, 300, 782, 780, 784, 48, 787, 786, 72, 309, 787, 302, 309, 785, 272, 787, 307, 278, 25, 301, 786, 785, 301, 785, 786, 297, 305, 296, 299, 784, 27, 21, 784, 787, 310, 783, 787, 296, 304, 788, 299, 298, 783, 787, 784, 783, 788, 787, 306, 783, 309, 298, 782, 304, 781, 49, 295, 773, 45, 306, 287, 308, 81, 278, 310, 304, 782, 782, 300, 38, 309, 305, 298, 785, 787, 304, 309, 301, 61, 785, 785, 296, 291, 309, 299, 283, 304, 784, 783, 783, 788, 306, 784, 783, 283, 788, 31, 272, 784, 786, 310, 786, 299, 296, 308, 788, 305, 299, 306, 783, 783, 296, 299, 783, 277, 784, 785, 300, 306, 784, 753, 301, 787, 785, 788, 299, 782, 785, 304, 290, 296, 310, 788, 310, 788, 308, 782, 785, 784, 296, 781, 298, 786, 304, 306, 788, 782, 775, 296, 21, 310, 782, 56, 782, 781, 270, 307, 63, 304, 775, 310, 787, 299, 310, 298, 784, 300, 309, 271, 784, 271, 783, 297, 301, 783, 302, 308, 71, 788, 787, 310, 27, 299, 299, 786, 781, 310, 300, 299, 787, 90, 308, 273, 782, 786, 28, 21, 268, 787, 310, 309, 782, 299, 299, 788, 787, 783, 297, 784, 780, 782, 786, 784, 298, 300, 303, 782, 291, 785, 301, 781, 785, 787, 292, 782, 266, 303, 788, 788, 305, 299, 296, 76, 786, 277, 309, 785, 300, 305, 785, 300, 784, 786, 781, 305, 781, 783, 784, 273, 298, 785, 292, 786, 299, 781, 296, 299, 310, 781, 786, 783, 273, 782, 306, 310, 304, 297, 782, 784, 784, 310, 267, 786, 278, 304, 32, 308, 305, 278, 725, 28, 786, 309, 310, 300, 309, 782, 782, 278, 100, 301, 301, 298, 304, 299, 787, 788, 298, 31, 298, 784, 297, 27, 308, 779, 786, 782, 298, 305, 783, 302, 309, 785, 787, 784, 302, 271, 269, 296, 299, 307, 782, 781, 38, 310, 784, 298, 295, 303, 781, 305, 784, 296, 788, 271, 782, 299, 782, 784, 302, 310, 298, 784, 309, 297, 304, 781, 785, 303, 309, 300, 29, 77, 306, 782, 780, 306, 301, 306, 309, 785, 786, 308, 305, 787, 786, 43, 785, 787, 306, 786, 310, 308, 305, 784, 23, 292, 787, 296, 782, 41, 784, 781, 787, 300, 781, 784, 781, 298, 29, 785, 297, 788, 787, 301, 783, 788, 786, 788, 70, 785, 310, 783, 305, 785, 298, 35, 299, 788, 786, 310, 302, 787, 783, 298, 786, 787, 21, 306, 295, 305, 788, 784, 298, 783, 308, 787, 296, 786, 783, 782, 45, 302, 781, 787, 276, 21, 783, 300, 60, 301, 784, 782, 784, 298, 782, 298, 781, 73, 784, 288, 787, 301, 303, 781, 296, 305, 784, 305, 786, 306, 293, 273, 274, 783, 304, 781, 782, 309, 787, 787, 65, 305, 781, 784, 785, 28, 784, 15, 309, 783, 786, 309, 265, 301, 301, 788, 784, 782, 784, 783, 784, 310, 303, 310, 24, 299, 780, 308, 786, 733, 774, 274, 299, 298, 782, 786, 787, 782, 787, 788, 785, 303, 783, 305, 300, 307, 45, 300, 304, 780, 309, 71, 309, 786, 786, 302, 304, 785, 786, 296, 299, 788, 310, 783, 779, 275, 783, 293, 782, 787, 296, 306, 307, 785, 784, 786, 41, 299, 292, 782, 304, 782, 782, 296, 782, 785, 273, 782, 784, 306, 786, 68, 782, 305, 785, 787, 784, 106, 298, 309, 273, 787, 310, 309, 784, 296, 783, 786, 301, 787, 299, 310, 304, 783, 782, 300, 788, 781, 786, 303, 50, 309, 781, 131, 309, 787, 788, 785, 298, 299, 305, 308, 785, 310, 298, 787, 782, 784, 296, 272, 786, 787, 788, 783, 709, 782, 301, 273, 68, 310, 25, 305, 33, 280, 781, 785, 783, 273, 784, 305, 782, 301, 784, 41, 787, 309, 299, 787, 780, 299, 782, 306, 32, 299, 784, 305, 302, 270, 786, 305, 782, 16, 786, 310, 301, 302, 781, 302, 782, 783, 786, 302, 773, 296, 786, 785, 29, 781, 301, 299, 38, 786, 788, 780, 167, 785, 308, 787, 302, 788, 308, 783, 44, 785, 309, 300, 305, 296, 310, 301, 773, 304, 784, 788, 786, 301, 787, 296, 782, 787, 23, 784, 781, 78, 278, 297, 785, 147, 783, 297, 781, 277, 787, 781, 787, 309, 782, 784, 268, 30, 299, 57, 787, 785, 310, 306, 783, 309, 305, 79, 784, 300, 309, 299, 787, 304, 274, 294, 296, 787, 23, 786, 27, 309, 780, 785, 787, 41, 296, 308, 22, 297, 780, 300, 277, 788, 302, 309, 15, 296, 784, 306, 29, 781, 782, 787, 23, 15, 43, 786, 309, 788, 304, 784, 302, 788, 46, 787, 788, 782, 308, 785, 308, 785, 788, 788, 787, 782, 310, 787, 308, 771, 305, 781, 732, 22, 788, 304, 788, 781, 302, 787, 306, 784, 781, 307, 29, 308, 788, 303, 305, 309, 783, 783, 298, 785, 299, 310, 308, 302, 779, 22, 310, 787, 735, 281, 309, 787, 298, 788, 785, 51, 299, 305, 782, 309, 787, 788, 785, 781, 30, 297, 787, 279, 785, 784, 304, 306, 787, 296, 298, 307, 308, 787, 305, 304, 781, 309, 310, 784, 309, 299, 785, 306, 782, 783, 43, 309, 302, 298, 271, 782, 31, 783, 300, 787, 787, 298, 787, 70, 780, 786, 783, 784, 26, 785, 301, 77, 298, 309, 273, 274, 782, 309, 306, 305, 785, 308, 301, 300, 296, 305, 308, 296, 302, 302, 788, 302, 300, 296, 299, 301, 297, 785, 787, 268, 786, 300, 304, 782, 285, 780, 42, 785, 310, 298, 276, 782, 787, 296, 270, 302, 15, 301, 298, 784, 299, 309, 291, 42, 294, 298, 787, 309, 306, 296, 305, 781, 788, 70, 301, 304, 305, 309, 785, 297, 617, 299, 301, 781, 308, 25, 298, 302, 788, 784, 786, 305, 784, 305, 783, 307, 784, 786, 296, 785, 787, 301, 309, 782, 784, 785, 786, 302, 787, 296, 309, 781, 783, 310, 308, 782, 305, 782, 304, 310, 304, 308, 782, 785, 234, 783, 784, 782, 786, 309, 301, 79, 299, 783, 296, 301, 38, 280, 298, 305, 783, 787, 310, 783, 308, 308, 273, 295, 299, 304, 310, 783, 788, 784, 298, 308, 785, 301, 309, 786, 780, 782, 273, 278, 292, 23, 241, 310, 783, 785, 278, 782, 787, 784, 310, 308, 784, 308, 781, 786, 780, 277, 289, 306, 788, 783, 48, 30, 782, 306, 779, 782, 782, 303, 788, 299, 300, 783, 784, 785, 788, 278, 256, 272, 782, 94, 782, 787, 298, 300, 784, 291, 781, 774, 310, 43, 37, 299, 786, 785, 308, 302, 785, 277, 784, 782, 47, 304, 305, 302, 23, 781, 784, 309, 300, 306, 306, 279, 785, 783, 786, 785, 784, 787, 787, 783, 783, 298, 308, 305, 785, 94, 783, 275, 299, 309, 783, 784, 296, 785, 20, 785, 780, 271, 786, 296, 270, 291, 787, 305, 304, 309, 305, 784, 784, 305, 299, 784, 298, 302, 781, 788, 297, 301, 785, 300, 303, 302, 785, 785, 303, 310, 299, 787, 787, 787, 299, 785, 298, 787, 781, 785, 751, 17, 786, 270, 785, 309, 296, 299, 782, 785, 785, 781, 786, 785, 787, 785, 300, 299, 53, 309, 298, 299, 784, 782, 782, 272, 782, 787, 303, 301, 783, 292, 305, 787, 85, 309, 787, 309, 783, 307, 781, 301, 782, 303, 785, 310, 308, 305, 301, 299, 309, 267, 101, 310, 788, 308, 785, 304, 784, 783, 787, 22, 309, 27, 277, 779, 31, 304, 310, 106, 304, 785, 782, 302, 280, 788, 276, 305, 301, 267, 306, 785, 273, 785, 308, 788, 785, 784, 28, 787, 299, 297, 301, 788, 787, 310, 781, 783, 301, 785, 300, 784, 782, 80, 786, 304, 73, 310, 787, 274, 788, 309, 309, 305, 781, 305, 77, 788, 784, 786, 787, 781, 21, 787, 305, 783, 783, 783, 74, 787, 781, 785, 787, 27, 295, 309, 310, 310, 783, 298, 783, 782, 28, 308, 736, 787, 292, 300, 303, 28, 309, 297, 782, 310, 42, 296, 786, 782, 787, 305, 21, 40, 300, 783, 310, 299, 65, 391, 299, 785, 298, 783, 786, 301, 308, 787, 300, 297, 309, 779, 788, 779, 296, 298, 301, 782, 310, 273, 783, 786, 784, 48, 296, 298, 24, 782, 56, 47, 782, 782, 276, 298, 783, 788, 784, 299, 305, 289, 783, 306, 782, 309, 784, 787, 304, 296, 20, 782, 781, 788, 301, 784, 299, 782, 309, 787, 306, 305, 306, 784, 785, 780, 310, 310, 786, 783, 782, 784, 787, 22, 784, 295, 301, 298, 43, 787, 781, 781, 301, 779, 299, 299, 301, 787, 785, 72, 783, 787, 782, 784, 24, 783, 781, 787, 784, 306, 784, 783, 788, 782, 786, 783, 309, 298, 785, 301, 295, 786, 304, 787, 27, 784, 310, 297, 302, 310, 301, 295, 27, 308, 785, 784, 300, 278, 303, 775, 787, 305, 298, 786, 267, 784, 787, 309, 782, 303, 773, 787, 301, 298, 75, 306, 787, 781, 27, 782, 304, 781, 787, 310, 781, 783, 784, 785, 785, 295, 68, 787, 307, 783, 785, 781, 291, 298, 783, 49, 788, 305, 309, 304, 31, 785, 787, 783, 783, 74, 782, 782, 310, 310, 301, 309, 783, 301, 780, 296, 785, 781, 298, 310, 781, 273, 787, 302, 30, 309, 310, 296, 269, 48, 292, 305, 783, 785, 785, 784, 788, 781, 787, 787, 787, 257, 753, 781, 32, 296, 785, 308, 307, 53, 782, 782, 305, 305, 785, 296, 775, 783, 787, 785, 309, 784, 299, 787, 23, 782, 299, 783, 71, 298, 308, 302, 305, 785, 309, 306, 309, 782, 310, 299, 787, 24, 780, 784, 786, 277, 309, 309, 782, 783, 305, 783, 301, 301, 305, 788, 299, 301, 786, 79, 785, 784, 298, 786, 308, 786, 787, 298, 302, 304, 782, 783, 781, 787, 303, 303, 310, 302, 783, 787, 787, 301, 784, 785, 782, 295, 781, 781, 301, 783, 310, 310, 308, 305, 309, 300, 786, 787, 781, 785, 785, 786, 784, 786, 787, 276, 787, 788, 299, 305, 788, 275, 296, 782, 785, 784, 784, 305, 787, 39, 23, 781, 784, 279, 628, 783, 305, 273, 306, 298, 783, 786, 786, 298, 309, 298, 297, 303, 296, 787, 783, 550, 781, 784, 782, 782, 295, 306, 277, 278, 787, 782, 303, 781, 785, 305, 295, 308, 783, 780, 43, 310, 306, 309, 785, 787, 81, 299, 785, 299, 788, 299, 784, 783, 783, 783, 784, 783, 783, 783, 783, 298, 297, 299, 298, 783, 299, 295, 783, 784, 308, 296, 299, 785, 88, 785, 304, 310, 310, 308, 786, 783, 786, 786, 782, 786, 310, 273, 298, 276, 783, 786, 784, 787, 299, 296, 273, 309, 296, 763, 298, 302, 784, 310, 310, 300, 786, 22, 787, 780, 301, 782, 785, 786, 310, 310, 310, 309, 787, 782, 30, 309, 308, 782, 787, 786, 782, 296, 782, 278, 292, 309, 301, 785, 786, 783, 780, 298, 73, 785, 273, 98, 784, 780, 787, 310, 783, 784, 298, 787, 784, 305, 787, 786, 787, 785, 27, 57};
                CloudView t0 = dimension0.time(0l);
                Node node = t0.createNode();
                node.setName("n0");
                t0.setRoot(node, null);
                final Element element = t0.createElement();
                element.setName("e0");
                node.addElement(element);
                //insert 20 variations in time

                KDefer defer = model.defer();
                for (int i = 0; i < max; i++) {
                    element.jump(i, defer.waitResult());
                }
                defer.then(new KCallback<Object[]>() {
                    @Override
                    public void on(Object[] objects) {
                        for (int i = 0; i < max; i++) {
                            ((Element) objects[i]).setValue(val[i]);
                        }

                        element.allTimes(new KCallback<long[]>() {
                            @Override
                            public void on(long[] collectedTimes) {
                                Assert.assertEquals(87, collectedTimes.length);

                                KDefer defer2 = model.defer();
                                for (int i = 0; i < max; i++) {
                                    element.jump(i, defer2.waitResult());
                                }
                                nbAssert[0]++;
                                defer2.then(new KCallback<Object[]>() {
                                    @Override
                                    public void on(Object[] objects) {
                                        for (int i = 0; i < max; i++) {
                                            nbAssert[0]++;
                                            double valss = ((Element) objects[i]).getValue();
                                            Assert.assertTrue(Math.abs(valss - val[i]) < MetaElement.ATT_VALUE.precision());
                                        }
                                        Assert.assertEquals(nbAssert[0], max + 1);
                                        element.allTimes(new KCallback<long[]>() {
                                            @Override
                                            public void on(long[] collected2) {
                                                Assert.assertEquals(87, collected2.length);
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
