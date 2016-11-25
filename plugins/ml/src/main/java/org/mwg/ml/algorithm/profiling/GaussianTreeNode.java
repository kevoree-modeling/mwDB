package org.mwg.ml.algorithm.profiling;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.ml.ProfilingNode;
import org.mwg.plugin.NodeState;
import org.mwg.structure.tree.KDTree;

public class GaussianTreeNode extends GaussianNode implements ProfilingNode {

    public static String NAME = "GaussianTreeNode";

    public static final String THRESHOLD = "_threshold";  //Factor of distance before check inside fail
    public static final double THRESHOLD_DEF = 1.01;
    private static String INTERNAL_KDROOT = "kdroot";

    public GaussianTreeNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    @Override
    public void learn(Callback<Boolean> callback) {
        extractFeatures(new Callback<double[]>() {
            @Override
            public void on(double[] values) {
                double[] features = new double[values.length - 1];
                System.arraycopy(values, 0, features, 0, values.length - 1);
                internalLearn(values, features, callback);
            }
        });
    }

    public void internalLearn(final double[] values, final double[] features, final Callback<Boolean> callback) {
        final NodeState resolved = this._resolver.alignState(this);
        super.learnVector(values, new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                final double threshold = resolved.getFromKeyWithDefault(THRESHOLD, THRESHOLD_DEF);
                final double[] precisions = (double[]) resolved.getFromKey(PRECISION);

                if (resolved.getFromKey(INTERNAL_KDROOT) == null) {
                    KDTree root = (KDTree) graph().newTypedNode(world(), time(), KDTree.NAME);
                    // root.setProperty(KDTree.DISTANCE_TYPE, Type.INT, DistanceEnum.GAUSSIAN);
                    root.set(KDTree.DISTANCE_THRESHOLD, Type.DOUBLE, threshold);
                    //  root.setProperty(KDTree.GAUSSIAN_PRECISION, Type.DOUBLE_ARRAY, precisions);
                    addToRelation(INTERNAL_KDROOT, root);
                    GaussianNode profile = (GaussianNode) graph().newTypedNode(world(), time(), GaussianNode.NAME);

                    profile.learnVector(values, new Callback<Boolean>() {
                        @Override
                        public void on(Boolean result) {
                            root.insertWith(features, profile, new Callback<Boolean>() {
                                @Override
                                public void on(Boolean result) {

                                    root.free();
                                    profile.free();
                                    if (callback != null) {
                                        callback.on(true);
                                    }
                                }
                            });
                        }
                    });

                } else {
                    relation(INTERNAL_KDROOT, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            KDTree root = (KDTree) result[0];
                            root.nearestNWithinRadius(features, 1, threshold, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    if (result != null && result.length > 0) {
                                        GaussianNode profile = (GaussianNode) result[0];
                                        profile.learnVector(values, new Callback<Boolean>() {
                                            @Override
                                            public void on(Boolean result) {
                                                root.free();
                                                profile.free();

                                                if (callback != null) {
                                                    callback.on(true);
                                                }
                                            }
                                        });
                                    } else {
                                        GaussianNode profile = (GaussianNode) graph().newTypedNode(world(), time(), GaussianNode.NAME);
                                        profile.learnVector(values, new Callback<Boolean>() {
                                            @Override
                                            public void on(Boolean result) {
                                                root.insertWith(features, profile, new Callback<Boolean>() {
                                                    @Override
                                                    public void on(Boolean result) {
                                                        root.free();
                                                        profile.free();

                                                        if (callback != null) {
                                                            callback.on(true);
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });
    }


    public int getNumNodes() {
        int[] res = new int[1];
        relation(INTERNAL_KDROOT, new Callback<Node[]>() {
            @Override
            public void on(Node[] result) {
                if (result == null || result.length == 0) {
                    res[0] = 0;
                } else {
                    KDTree root = (KDTree) result[0];
                    res[0] = (Integer) root.get(KDTree.SIZE);
                }
            }
        });
        return res[0];
    }

    @Override
    public void predict(Callback<double[]> callback) {

    }

    public void predictValue(double[] values, Callback<Double> callback) {
        if (callback == null) {
            return;
        }
        double[] features = new double[values.length - 1];
        System.arraycopy(values, 0, features, 0, values.length - 1);
        final NodeState resolved = this._resolver.resolveState(this);
        if (resolved.getFromKey(INTERNAL_KDROOT) == null) {
            callback.on(null);
        }
        final double threshold = resolved.getFromKeyWithDefault(THRESHOLD, THRESHOLD_DEF);
        relation(INTERNAL_KDROOT, new Callback<Node[]>() {
            @Override
            public void on(Node[] result) {
                KDTree root = (KDTree) result[0];
                root.nearestNWithinRadius(features, 1, threshold, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        if (result != null && result.length > 0) {
                            GaussianNode profile = (GaussianNode) result[0];
                            double[] avg = profile.getAvg();
                            Double res = avg[avg.length - 1];
                            profile.free();
                            root.free();
                            callback.on(res);
                        } else {
                            double[] avg = getAvg();
                            Double res = avg[avg.length - 1];
                            root.free();
                            callback.on(res);
                        }
                    }
                });
            }
        });

    }
}
