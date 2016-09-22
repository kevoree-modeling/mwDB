package org.mwg.ml.algorithm.profiling;

import org.mwg.*;
import org.mwg.ml.AbstractMLNode;
import org.mwg.ml.ProfilingNode;
import org.mwg.utility.Enforcer;
import org.mwg.plugin.NodeState;

public class GaussianSlotNode extends AbstractMLNode implements ProfilingNode {

    //Name of the algorithm to be used in the meta model
    public final static String NAME = "GaussianSlotProfiling";

    @Override
    public void learn(final Callback<Boolean> callback) {
        extractFeatures(new Callback<double[]>() {
            @Override
            public void on(double[] values) {
                learnArray(values);
                callback.on(true);
            }
        });
    }


    /**
     * @native ts
     * return Math.floor(currentTime/sensitivity)*sensitivity;
     */
    private static long timeSensitivity(long currentTime, long sensitivity) {
        return Math.floorDiv(currentTime, sensitivity) * sensitivity;
    }

    public void learnArray(double[] values) {

        final long insTime = time();
        final long periodSize = (long) this.get(PERIOD_SIZE);

        final long[] newTime = {timeSensitivity(insTime, periodSize)};

        timepoints(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME, result -> {
            if (newTime[0] < result[0]) {
                newTime[0] = result[0];
            }
        });

        this.jump(newTime[0], new Callback<Node>() {
            @Override
            public void on(Node result) {
                final NodeState resolved = result.graph().resolver().resolveState(result);
                int numOfSlot = resolved.getFromKeyWithDefault(SLOTS_NUMBER, SLOTS_NUMBER_DEF);

                int[] total;
                double[] min;
                double[] max;
                double[] sum;
                double[] sumSquare;
                int features = values.length;


                total = (int[]) resolved.getFromKey(INTERNAL_TOTAL_KEY);

                if (numOfSlot == 1 || numOfSlot == 0) {
                    if (total == null) {
                        resolved.setFromKey(INTERNAL_FEATURES_NUMBER, Type.INT, features);
                        total = new int[1];
                        min = new double[features];
                        max = new double[features];
                        sum = new double[features];
                        sumSquare = new double[features * (features + 1) / 2];
                    } else {
                        min = (double[]) resolved.getFromKey(INTERNAL_MIN_KEY);
                        max = (double[]) resolved.getFromKey(INTERNAL_MAX_KEY);
                        sum = (double[]) resolved.getFromKey(INTERNAL_SUM_KEY);
                        sumSquare = (double[]) resolved.getFromKey(INTERNAL_SUMSQUARE_KEY);
                    }
                    update(total, min, max, sum, sumSquare, values, 0, features, 0, 0);
                    return;

                } else {

                    if (total == null) {
                        resolved.setFromKey(INTERNAL_FEATURES_NUMBER, Type.INT, features);
                        total = new int[numOfSlot + 1];
                        min = new double[(numOfSlot + 1) * features];
                        max = new double[(numOfSlot + 1) * features];
                        sum = new double[(numOfSlot + 1) * features];
                        sumSquare = new double[(numOfSlot + 1) * features * (features + 1) / 2];
                    } else {
                        min = (double[]) resolved.getFromKey(INTERNAL_MIN_KEY);
                        max = (double[]) resolved.getFromKey(INTERNAL_MAX_KEY);
                        sum = (double[]) resolved.getFromKey(INTERNAL_SUM_KEY);
                        sumSquare = (double[]) resolved.getFromKey(INTERNAL_SUMSQUARE_KEY);
                    }

                    //update the profile
                    long periodSize = resolved.getFromKeyWithDefault(PERIOD_SIZE, PERIOD_SIZE_DEF);
                    int slot = getIntTime(insTime, numOfSlot, periodSize);
                    int index = slot * features;
                    int indexSquare = slot * features * (features + 1) / 2;
                    int indexTot = numOfSlot * features;
                    int indexSquareTot = numOfSlot * features * (features + 1) / 2;

                    update(total, min, max, sum, sumSquare, values, slot, features, index, indexSquare);
                    update(total, min, max, sum, sumSquare, values, numOfSlot, features, indexTot, indexSquareTot);
                }


                //Save the state
                resolved.setFromKey(INTERNAL_FEATURES_NUMBER, Type.INT, features);
                resolved.setFromKey(INTERNAL_TOTAL_KEY, Type.INT_ARRAY, total);
                resolved.setFromKey(INTERNAL_MIN_KEY, Type.DOUBLE_ARRAY, min);
                resolved.setFromKey(INTERNAL_MAX_KEY, Type.DOUBLE_ARRAY, max);
                resolved.setFromKey(INTERNAL_SUM_KEY, Type.DOUBLE_ARRAY, sum);
                resolved.setFromKey(INTERNAL_SUMSQUARE_KEY, Type.DOUBLE_ARRAY, sumSquare);
            }
        });


    }

    @Override
    public void predict(Callback<double[]> callback) {
        NodeState resolved = unphasedState();
        int features = resolved.getFromKeyWithDefault(INTERNAL_FEATURES_NUMBER, 0);
        if (features == 0) {
            callback.on(null);
            return;
        }

        int numOfSlot = resolved.getFromKeyWithDefault(SLOTS_NUMBER, SLOTS_NUMBER_DEF);
        long periodSize = resolved.getFromKeyWithDefault(PERIOD_SIZE, PERIOD_SIZE_DEF);

        int slot = getIntTime(time(), numOfSlot, periodSize);
        int index = slot * features;
        //int indexSquare = slot * features * (features + 1) / 2;

        int[] total = (int[]) resolved.getFromKey(INTERNAL_TOTAL_KEY);
        double[] sum = (double[]) resolved.getFromKey(INTERNAL_SUM_KEY);
        // double[] sumsq= (double[]) resolved.getFromKey(INTERNAL_SUMSQUARE_KEY);

        double[] result = new double[features];
        if (total != null) {
            if (total[slot] != 0) {
                for (int j = 0; j < features; j++) {
                    result[j] = sum[j + index] / total[slot];
                    //result[j] = Gaussian1D.draw(sum[j + index], sumsq[j+indexSquare], total[slot]);
                }
            }
        }

        callback.on(result);
    }

    //Machine Learning Properties and their default values with _DEF

    public static final String SLOTS_NUMBER = "SLOTS_NUMBER"; //Number of slots to create in the profile, default is 1
    public static final int SLOTS_NUMBER_DEF = 1;

    public static final String PERIOD_SIZE = "PERIOD_SIZE"; //The period over which the profile returns to the initial slot
    public static final long PERIOD_SIZE_DEF = 24 * 3600 * 1000; //By default it is 24 hours


    //Public specific getters and setters

    //Internal state keys
    private static final String INTERNAL_FEATURES_NUMBER = "_featuresNb";
    private static final String INTERNAL_TOTAL_KEY = "_total";
    private static final String INTERNAL_MIN_KEY = "_min";
    private static final String INTERNAL_MAX_KEY = "_max";
    private static final String INTERNAL_SUM_KEY = "_sum";
    private static final String INTERNAL_SUMSQUARE_KEY = "_sumSquare";

    private static final Enforcer enforcer = new Enforcer()
            .asPositiveInt(SLOTS_NUMBER)
            .asPositiveLong(PERIOD_SIZE);

    //Override default Abstract node default setters and getters
    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        enforcer.check(propertyName, propertyType, propertyValue);
        super.setProperty(propertyName, propertyType, propertyValue);
    }

    @Override
    public Object get(String attributeName) {
        final NodeState state = this._resolver.resolveState(this);
        if (attributeName.equals(SLOTS_NUMBER)) {
            return state.getFromKeyWithDefault(SLOTS_NUMBER, SLOTS_NUMBER_DEF);
        } else if (attributeName.equals(PERIOD_SIZE)) {
            return state.getFromKeyWithDefault(PERIOD_SIZE, PERIOD_SIZE_DEF);
        } else {
            return super.get(attributeName);
        }
    }


    public GaussianSlotNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }


    //get time in 15 minutes chunks
    public static int getIntTime(long time, int numOfSlot, long periodSize) {
        if (numOfSlot <= 1) {
            return 0;
        }
        long res = time % periodSize;
        res = res / (periodSize / numOfSlot);
        return (int) res;
    }


    private void update(int[] total, double[] min, double[] max, double[] sum, double[] sumSquare, double[] values, int slot, int features, int index, int indexSquare) {
        if (total[slot] == 1) {
            int count = 0;
            for (int i = 0; i < features; i++) {
                min[index + i] = values[i];
                max[index + i] = values[i];
                sum[index + i] = values[i];
                for (int j = i; j < features; j++) {
                    sumSquare[indexSquare + count] += values[i] * values[j];
                    count++;
                }
            }
        } else {
            int count = 0;
            for (int i = 0; i < features; i++) {
                if (values[i] < min[index + i]) {
                    min[index + i] = values[i];
                }
                if (values[i] > max[index + i]) {
                    max[index + i] = values[i];
                }
                sum[index + i] += values[i];
                for (int j = i; j < features; j++) {
                    sumSquare[indexSquare + count] += values[i] * values[j];
                    count++;
                }
            }
        }
        total[slot] += 1;
    }


    public double[] getMin() {
        return (double[]) unphasedState().getFromKey(INTERNAL_MIN_KEY);
    }

    public double[] getMax() {
        return (double[]) unphasedState().getFromKey(INTERNAL_MAX_KEY);
    }

    public double[] getSum() {
        return (double[]) unphasedState().getFromKey(INTERNAL_SUM_KEY);
    }


    public double[] getSumSquare() {
        return (double[]) unphasedState().getFromKey(INTERNAL_SUMSQUARE_KEY);
    }

    public int[] getTotal() {
        return (int[]) unphasedState().getFromKey(INTERNAL_TOTAL_KEY);
    }

    public double[] getAvg() {
        NodeState resolved = unphasedState();
        int numOfSlot = resolved.getFromKeyWithDefault(SLOTS_NUMBER, SLOTS_NUMBER_DEF);
        int features = resolved.getFromKeyWithDefault(INTERNAL_FEATURES_NUMBER, 0);
        if (features == 0) {
            return null;
        }

        int[] total = (int[]) resolved.getFromKey(INTERNAL_TOTAL_KEY);
        double[] sum = (double[]) resolved.getFromKey(INTERNAL_SUM_KEY);

        double[] result = new double[sum.length];
        if (total != null) {
            if (numOfSlot > 1) {
                int count = 0;
                for (int i = 0; i < (numOfSlot + 1); i++) {
                    if (total[i] != 0) {
                        for (int j = 0; j < features; j++) {
                            result[count] = sum[count] / total[i];
                            count++;
                        }
                    } else {
                        count += features;
                    }
                }
            } else {
                if (total[0] != 0) {
                    for (int j = 0; j < features; j++) {
                        result[j] = sum[j] / total[0];
                    }
                }
            }
        }
        return result;
    }


}
