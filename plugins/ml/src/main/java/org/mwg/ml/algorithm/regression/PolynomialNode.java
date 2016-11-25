package org.mwg.ml.algorithm.regression;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.ml.BaseMLNode;
import org.mwg.ml.RegressionNode;
import org.mwg.ml.common.matrix.operation.PolynomialFit;
import org.mwg.plugin.NodeState;
import org.mwg.utility.Enforcer;

public class PolynomialNode extends BaseMLNode implements RegressionNode {

    /**
     * Tolerated error that can be configure per node to drive the learning process
     */
    public static final String PRECISION = "precision";
    public static final double PRECISION_DEF = 1;
    public static final String VALUE = "value";

    /**
     * Name of the algorithm to be used in the meta model
     */
    public final static String NAME = "PolynomialNode";

    //Internal state variables private and starts with _
    public static final String INTERNAL_WEIGHT_KEY = "weight";
    public static final String INTERNAL_STEP_KEY = "step";

    private static final String INTERNAL_TIME_BUFFER = "times";
    private static final String INTERNAL_VALUES_BUFFER = "values";
    private static final String INTERNAL_NB_PAST_KEY = "nb";
    private static final String INTERNAL_LAST_TIME_KEY = "lastTime";

    //Other default parameters that should not be changed externally:
    public static final String MAX_DEGREE = "maxdegree";
    public static final int MAX_DEGREE_DEF = 20; // maximum polynomial degree

    private final static String NOT_MANAGED_ATT_ERROR = "Polynomial node can only handle value attribute, please use a super node to store other data";
    private static final Enforcer enforcer = new Enforcer().asPositiveDouble(PRECISION);
    private static final Enforcer degenforcer = new Enforcer().asPositiveInt(MAX_DEGREE);


    public PolynomialNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }


    //Override default Abstract node default setters and getters
    @Override
    public Node set(String propertyName, byte propertyType, Object propertyValue) {
        if (propertyName.equals(VALUE)) {
            learn(Double.parseDouble(propertyValue.toString()), null);
        } else if (propertyName.equals(PRECISION)) {
            enforcer.check(propertyName, propertyType, propertyValue);
            super.set(propertyName, Type.DOUBLE, propertyValue);
        } else if (propertyName.equals(MAX_DEGREE)){
            degenforcer.check(propertyName, propertyType, propertyValue);
            super.set(propertyName, Type.INT, (int) propertyValue);
        }
        else {
            throw new RuntimeException(NOT_MANAGED_ATT_ERROR);
        }
        return this;
    }

    @Override
    public Object get(String propertyName) {
        if (propertyName.equals(VALUE)) {
            final Double[] res = {null};
            //ToDo fix callback - return
            extrapolate(new Callback<Double>() {
                @Override
                public void on(Double result) {
                    res[0] = result;
                }
            });
            return res[0];
        } else {
            return super.get(propertyName);
        }
    }

    @Override
    public void learn(double value, Callback<Boolean> callback) {
        NodeState previousState = unphasedState(); //past state, not cloned

        long timeOrigin = previousState.time();
        long nodeTime = time();
        double precision = previousState.getFromKeyWithDefault(PRECISION, PRECISION_DEF);
        double[] weight = (double[]) previousState.getFromKey(INTERNAL_WEIGHT_KEY);

        //Initial feed for the very first time, the weight is set directly with the first value that arrives
        if (weight == null) {
            weight = new double[1];
            weight[0] = value;
            previousState.setFromKey(INTERNAL_WEIGHT_KEY, Type.DOUBLE_ARRAY, weight);
            previousState.setFromKey(INTERNAL_NB_PAST_KEY, Type.INT, 1);
            previousState.setFromKey(INTERNAL_STEP_KEY, Type.LONG, 0l);
            previousState.setFromKey(INTERNAL_LAST_TIME_KEY, Type.LONG, 0l);
            previousState.setFromKey(INTERNAL_TIME_BUFFER, Type.DOUBLE_ARRAY, new double[]{0});
            previousState.setFromKey(INTERNAL_VALUES_BUFFER, Type.DOUBLE_ARRAY, new double[]{value});
            if (callback != null) {
                callback.on(true);
            }
            return;
        }
        //Check if we are inserting in the past:
        long previousTime = timeOrigin + (Long) previousState.getFromKey(INTERNAL_LAST_TIME_KEY);
        if (nodeTime > previousTime) {
            // For the second time point, test and check for the step in time
            Long stp = (Long) previousState.getFromKey(INTERNAL_STEP_KEY);
            long lastTime = nodeTime - timeOrigin;
            if (stp == null || stp == 0) {
                if (lastTime == 0) {
                    weight = new double[1];
                    weight[0] = value;
                    previousState.setFromKey(INTERNAL_WEIGHT_KEY, Type.DOUBLE_ARRAY, weight);
                    previousState.setFromKey(INTERNAL_TIME_BUFFER, Type.DOUBLE_ARRAY, new double[]{0});
                    previousState.setFromKey(INTERNAL_VALUES_BUFFER, Type.DOUBLE_ARRAY, new double[]{value});
                    if (callback != null) {
                        callback.on(true);
                    }
                    return;
                } else {
                    stp = lastTime;
                    previousState.setFromKey(INTERNAL_STEP_KEY, Type.LONG, stp);
                }
            }

            //Then, first step, check if the current model already fits the new value:
            int deg = weight.length - 1;
            Integer num = (Integer) previousState.getFromKey(INTERNAL_NB_PAST_KEY);

            double t = (nodeTime - timeOrigin);
            t = t / stp;
            double maxError = maxErr(precision, deg);

            int maxd = previousState.getFromKeyWithDefault(MAX_DEGREE, MAX_DEGREE_DEF);

            double[] times = updateBuffer(previousState, t, maxd, INTERNAL_TIME_BUFFER);
            double[] values = updateBuffer(previousState, value, maxd, INTERNAL_VALUES_BUFFER);


            //If yes, update some states parameters and return
            if (Math.abs(PolynomialFit.extrapolate(t, weight) - value) <= maxError) {
                previousState.setFromKey(INTERNAL_NB_PAST_KEY, Type.INT, num + 1);
                previousState.setFromKey(INTERNAL_LAST_TIME_KEY, Type.LONG, lastTime);
                if (callback != null) {
                    callback.on(true);
                }
                return;
            }

            //If not increase polynomial degrees
            int newdeg = Math.min(times.length, maxd);
            while (deg < newdeg && times.length < maxd * 4) {
                maxError = maxErr(precision, deg);
                PolynomialFit pf = new PolynomialFit(deg);
                pf.fit(times, values);
                if (tempError(pf.getCoef(), times, values) <= maxError) {
                    weight = pf.getCoef();
                    previousState.setFromKey(INTERNAL_NB_PAST_KEY, Type.INT, num + 1);
                    previousState.setFromKey(INTERNAL_WEIGHT_KEY, Type.DOUBLE_ARRAY, weight);
                    previousState.setFromKey(INTERNAL_LAST_TIME_KEY, Type.LONG, lastTime);
                    if (callback != null) {
                        callback.on(true);
                    }
                    return;
                }
                deg++;
            }


            //It does not fit, create a new state and split the polynomial, different splits if we are dealing with the future or with the past

            long newstep = nodeTime - previousTime;
            NodeState phasedState = newState(previousTime); //force clone
            double[] nvalues = new double[2];
            double[] ntimes = new double[2];

            ntimes[0] = 0;
            ntimes[1] = 1;
            nvalues[0] = values[values.length - 2];
            nvalues[1] = value;

            //Test if the newly created polynomial is of degree 0 or 1.
            maxError = maxErr(precision, 0);
            if (Math.abs(nvalues[1] - nvalues[0]) <= maxError) {
                // Here it's a degree 0
                weight = new double[1];
                weight[0] = nvalues[0];
            } else {
                //Here it's a degree 1
                weight = new double[2];
                weight[0] = nvalues[0];
                weight[1] = nvalues[1] - nvalues[0];
            }

            previousState.setFromKey(INTERNAL_TIME_BUFFER, Type.DOUBLE_ARRAY, null);
            previousState.setFromKey(INTERNAL_VALUES_BUFFER, Type.DOUBLE_ARRAY, null);
            //create and set the phase set
            phasedState.setFromKey(INTERNAL_TIME_BUFFER, Type.DOUBLE_ARRAY, ntimes);
            phasedState.setFromKey(INTERNAL_VALUES_BUFFER, Type.DOUBLE_ARRAY, nvalues);
            phasedState.setFromKey(PRECISION, Type.DOUBLE, precision);
            phasedState.setFromKey(MAX_DEGREE, Type.INT, maxd);
            phasedState.setFromKey(INTERNAL_WEIGHT_KEY, Type.DOUBLE_ARRAY, weight);
            phasedState.setFromKey(INTERNAL_NB_PAST_KEY, Type.INT, 2);
            phasedState.setFromKey(INTERNAL_STEP_KEY, Type.LONG, newstep);
            phasedState.setFromKey(INTERNAL_LAST_TIME_KEY, Type.LONG, newstep);
            if (callback != null) {
                callback.on(true);
            }
            return;
        } else {
            // 2 phased states need to be created
            //TODO Insert in past.
        }
        if (callback != null) {
            callback.on(false);
        }
    }

    private static double[] updateBuffer(NodeState state, double t, int maxdeg, String key) {
        double[] ts = (double[]) state.getFromKey(key);
        if (ts == null) {
            ts = new double[1];
            ts[0] = t;
            state.setFromKey(key, Type.DOUBLE_ARRAY, ts);
            return ts;
        } else if (ts.length < maxdeg * 4) {
            double[] nts = new double[ts.length + 1];
            System.arraycopy(ts, 0, nts, 0, ts.length);
            nts[ts.length] = t;
            state.setFromKey(key, Type.DOUBLE_ARRAY, nts);
            return nts;
        } else {
            double[] nts = new double[ts.length];
            System.arraycopy(ts, 1, nts, 0, ts.length - 1);
            nts[ts.length - 1] = t;
            state.setFromKey(key, Type.DOUBLE_ARRAY, nts);
            return nts;
        }
    }

    @Override
    public void extrapolate(Callback<Double> callback) {
        long time = time();
        NodeState state = unphasedState();
        long timeOrigin = state.time();
        double[] weight = (double[]) state.getFromKey(INTERNAL_WEIGHT_KEY);
        if (weight == null) {
            if (callback != null) {
                callback.on(0.0);
            }
            return;
        }
        Long inferSTEP = (Long) state.getFromKey(INTERNAL_STEP_KEY);
        if (inferSTEP == null || inferSTEP == 0) {
            if (callback != null) {
                callback.on(weight[0]);
            }
            return;
        }
        double t = (time - timeOrigin);
        Long lastTime = (Long) state.getFromKey(INTERNAL_LAST_TIME_KEY);
        if (t > lastTime) {
            t = (double) lastTime;
        }
        t = t / inferSTEP;
        if (callback != null) {
            callback.on(PolynomialFit.extrapolate(t, weight));
        }
    }

    private double maxErr(double precision, int degree) {
        //double tol = precision;
    /*    if (_prioritization == Prioritization.HIGHDEGREES) {
            tol = precision / Math.pow(2, _maxDegree - degree);
        } else if (_prioritization == Prioritization.LOWDEGREES) {*/
        //double tol = precision / Math.pow(2, degree + 0.5);
       /* } else if (_prioritization == Prioritization.SAMEPRIORITY) {
            tol = precision * degree * 2 / (2 * _maxDegree);
        }*/
        return precision / Math.pow(2, degree + 1);
    }

    private double tempError(double[] computedWeights, double[] times, double[] values) {
        double maxErr = 0;
        double temp;
        for (int i = 0; i < times.length; i++) {
            temp = Math.abs(values[i] - PolynomialFit.extrapolate(times[i], computedWeights));
            if (temp > maxErr) {
                maxErr = temp;
            }
        }
        return maxErr;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"world\":");
        builder.append(world());
        builder.append(",\"time\":");
        builder.append(time());
        builder.append(",\"id\":");
        builder.append(id());
        final NodeState state = unphasedState();
        long timeOrigin = state.time();
        long step=state.getFromKeyWithDefault(INTERNAL_STEP_KEY,1l);

        if (state != null) {
            double[] weight = (double[]) state.getFromKey(INTERNAL_WEIGHT_KEY);
            if (weight != null) {
                builder.append("\"polynomial\":\"");
                for (int i = 0; i < weight.length; i++) {
                    if (i != 0) {
                        builder.append("+(");
                    }
                    builder.append(weight[i]);
                    if (i == 1) {
                        if(timeOrigin==0) {
                            if(step==1) {
                                builder.append("*t");
                            }
                            else{
                                builder.append("*t/"+step);
                            }
                        }
                        else {
                            if(step==1) {
                                builder.append("*(t-"+timeOrigin+")");
                            }
                            else{
                                builder.append("*(t-"+timeOrigin+")/"+step);
                            }
                        }
                    } else if (i > 1) {
                        if(timeOrigin==0) {
                            if(step==1) {
                                builder.append("*t^");
                                builder.append(i);
                            }
                            else{
                                builder.append("*(t/"+step+")^");
                                builder.append(i);
                            }
                        }
                        else {
                            if(step==1) {
                                builder.append("*(t-"+timeOrigin+")^");
                                builder.append(i);
                            }
                            else{
                                builder.append("*((t-"+timeOrigin+")/"+step+")^");
                                builder.append(i);
                            }
                        }
                    }
                    if (i != 0) {
                        builder.append(")");
                    }
                }
                builder.append("\"");
            }
            builder.append("}");
        }
        return builder.toString();
    }
}
