package org.mwg.ml.analytics;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.ml.BaseMLNode;
import org.mwg.plugin.NodeState;

public class AttributeNode extends BaseMLNode {
    public final static String NAME = "AttributeNode";

    public static final String VALUE = "value"; //ToDo move value to a subnode

    public static final String VALID_VALUE = "valid_value";
    public static final String IS_VALID = "is_valid";
    public static final String EXTRAPOLATE = "extrapolate";

    //To set the business logic - tolerated min, max
    public static final String MIN_TOLERATED = "min_tol";
    public static final String MAX_TOLERATED = "max_tol";

    public static final String MIN_OBSERVED = "min_obs";
    public static final String MAX_OBSERVED = "max_obs";

    public static final String MIN_VALID = "min_val";
    public static final String MAX_VALID = "max_val";

    public static final String AVG = "avg";
    public static final String SIGMA = "sigma";
    public static final String TOTAL = "total";
    public static final String TOTAL_VALID = "total_val";

    private static final String INTERNAL_SUM_KEY = "_sum";
    private static final String INTERNAL_SUMSQUARE_KEY = "_sumSquare";
    private final static String NOT_MANAGED_ATT_ERROR = "Attribute node can only handle value attribute, please use a super node to store other data";


    public AttributeNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    //Override default Abstract node default setters and getters
    @Override
    public Node set(String propertyName, byte propertyType, Object propertyValue) {
        if (propertyName.equals(VALUE)) {
            internalSet(Double.parseDouble(propertyValue.toString()), null);
        } else if (propertyName.equals(MIN_TOLERATED)) {
            super.set(MIN_TOLERATED, Type.DOUBLE, propertyValue);
        } else if (propertyName.equals(MAX_TOLERATED)) {
            super.set(MAX_TOLERATED, Type.DOUBLE, propertyValue);
        } else if (propertyName.equals(EXTRAPOLATE)) {
            super.set(EXTRAPOLATE, Type.BOOL, propertyValue);
        } else {
            throw new RuntimeException(NOT_MANAGED_ATT_ERROR);
        }
        return this;
    }

    @Override
    public Object get(String propertyName) {
        switch (propertyName) {
            case VALUE:
                return getValue();
            case VALID_VALUE:
                return getValidValue();
            case IS_VALID:
                return super.get(propertyName);
            case AVG:
                return getAvg();
            case SIGMA:
                return getSigma();
            default:
                return super.get(propertyName);
        }
    }


    private Double getValue() {
        return 0.0; //ToDo not implemented yet should take a callback
    }

    private Double getValidValue() {
        return 0.0; //ToDo not implemented yet should take a callback
    }

    private Double getAvg() {
        NodeState state = phasedState();
        long totalVal = state.getFromKeyWithDefault(TOTAL_VALID, 0);
        if (totalVal == 0) {
            return null;
        } else {
            Double v = state.getFromKeyWithDefault(INTERNAL_SUM_KEY, 0.0);
            return v / totalVal;
        }
    }

    private Double getSigma() {
        NodeState state = phasedState();
        long totalVal = state.getFromKeyWithDefault(TOTAL_VALID, 0);
        if (totalVal < 1) {
            return null;
        } else {
            double avg = state.getFromKeyWithDefault(INTERNAL_SUM_KEY, 0);
            avg = avg / totalVal;
            double correction = totalVal;
            correction = correction / (totalVal - 1);
            double sumsq = state.getFromKeyWithDefault(INTERNAL_SUMSQUARE_KEY, 0.0);
            double cov = (sumsq / totalVal - avg * avg) * correction;
            return Math.sqrt(cov);
        }
    }


    private boolean checkValid(double v, Double min, Double max) {
        boolean res = true;
        if (min != null) {
            res = res && (v >= min);
        }
        if (max != null) {
            res = res && (v <= max);
        }
        return res;
    }


    //Return the validity state of the set
    private void internalSet(double v, Callback<Boolean> callback) {

        //Get the phase state now
        NodeState state = phasedState();
        //Set the value whatever it is
        state.setFromKey(VALUE, Type.DOUBLE, v); //ToDo set on another subnode

        //Check the total and uptade min max for the first time
        long total = state.getFromKeyWithDefault(TOTAL, 0);
        if (total == 0) {
            state.setFromKey(MIN_OBSERVED, Type.DOUBLE, v);
            state.setFromKey(MAX_OBSERVED, Type.DOUBLE, v);
        }
        state.setFromKey(TOTAL, Type.LONG, total + 1);


        //Get tolerated bound
        Double mintol = state.getFromKeyWithDefault(MIN_TOLERATED, null);
        Double maxtol = state.getFromKeyWithDefault(MAX_TOLERATED, null);


        Double minval = state.getFromKeyWithDefault(MIN_VALID, null);
        Double maxval = state.getFromKeyWithDefault(MAX_VALID, null);

        //Check validity of the current insert
        boolean valid = checkValid(v, mintol, maxtol);
        state.setFromKey(IS_VALID, Type.BOOL, valid);
        if (valid) {
            //Update min, max valid
            if (minval == null || v < minval) {
                state.setFromKey(MIN_VALID, Type.DOUBLE, v);
            }
            if (maxval == null || v > maxval) {
                state.setFromKey(MAX_VALID, Type.DOUBLE, v);
            }

            //Update statistics:
            double internalSum = state.getFromKeyWithDefault(INTERNAL_SUM_KEY, 0);
            internalSum += v;
            state.setFromKey(INTERNAL_SUM_KEY, Type.DOUBLE, internalSum);

            double internalSumSq = state.getFromKeyWithDefault(INTERNAL_SUMSQUARE_KEY, 0);
            internalSumSq += v * v;
            state.setFromKey(INTERNAL_SUMSQUARE_KEY, Type.DOUBLE, internalSumSq);

            long totalval = state.getFromKeyWithDefault(TOTAL_VALID, 0);
            totalval++;
            state.setFromKey(TOTAL_VALID, Type.LONG, totalval);
        }

        if (callback != null) {
            callback.on(valid);
        }
    }


}
