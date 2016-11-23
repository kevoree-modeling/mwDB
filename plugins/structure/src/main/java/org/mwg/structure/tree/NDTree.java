package org.mwg.structure.tree;

import org.mwg.*;
import org.mwg.base.BaseNode;
import org.mwg.core.task.Actions;
import org.mwg.plugin.Job;
import org.mwg.plugin.NodeState;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.struct.Relation;
import org.mwg.structure.NTree;
import org.mwg.structure.action.TraverseById;
import org.mwg.structure.distance.Distance;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.distance.EuclideanDistance;
import org.mwg.structure.distance.GeoDistance;
import org.mwg.structure.util.NearestNeighborArrayList;
import org.mwg.structure.util.NearestNeighborList;
import org.mwg.task.*;

import static org.mwg.core.task.Actions.*;

public class NDTree extends BaseNode implements NTree {
    public static final String NAME = "NDTree";

    public NDTree(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    //To store only on root node
    private static long _STAT = 0;

    //Gaussian related stats on each subspace node
    private static long _TOTAL = 1;
    private static long _SUM = 2;
    private static long _SUMSQ = 3;
//    private static long _MIN = 4;  //not needed because the node has boundmin and boundmax
//    private static long _MAX = 5;   //not needed because the node has boundmin and boundmax


    // Subspace related stuff
    private static long _BOUNDMIN = 6;
    private static long _BOUNDMAX = 7;
    private static long _VALUES = 8;
    private static long _KEYS = 9;

    private static String _VALUES_STR = "8";
    private static String _KEYS_STR = "9";

    //to store only on the root node
    private static long _PRECISION = 10;
    private static long _NUMNODES = 11;
    private static long _DIM = 12;
    private static long _DISTANCE = 13;
    private static long _DISTANCETHRESHOLD = 14;
    private static long _FROM = 15;

    public static final String DISTANCE_THRESHOLD = "threshold";       //Distance threshold to define when 2 keys are not considered the same anymopre
    public static final double DISTANCE_THRESHOLD_DEF = 1e-10;
    public static final int DISTANCE_TYPE_DEF = 0;

    public static boolean STAT_DEF = false;


    //The beginning of relations navigation
    static long _RELCONST = 16; //Should be always a power of 2


    public void setBounds(double[] min, double[] max, double[] precisions) {

        NodeState state = unphasedState();

        if (state.get(_BOUNDMIN) != null) {
            throw new RuntimeException("Bounds can't be changed!, you need to re-index");
        }

        if (min.length != max.length) {
            throw new RuntimeException("Min and max bounds should be the same array length");
        }
        if (precisions.length != max.length) {
            throw new RuntimeException("Max and precision should be the same array length");
        }

        for (int i = 0; i < min.length; i++) {
            if (min[i] >= max[i]) {
                throw new RuntimeException("Min should be always exclusively smaller than max");
            }
            if (precisions[i] > (max[i] - min[i])) {
                throw new RuntimeException("Precision should always be smaller than max-min");
            }
        }

        state.set(_DIM, Type.INT, min.length);
        state.set(_BOUNDMIN, Type.DOUBLE_ARRAY, min);
        state.set(_BOUNDMAX, Type.DOUBLE_ARRAY, max);
        state.set(_PRECISION, Type.DOUBLE_ARRAY, precisions);

    }

    private static void updateCount(boolean updateCount, Node root) {
        if (!updateCount) {
            return;
        }
        if (root.getAt(_NUMNODES) != null) {
            int count = (int) root.getAt(_NUMNODES);
            count++;
            root.setAt(_NUMNODES, Type.INT, count);
        } else {
            root.setAt(_NUMNODES, Type.INT, 1);
        }
    }

    //region insert and nearest tasks
    private static Task nearestTask = initNearestTask();
    private static Task nearestRadiusTask = initRadusTask();
    //Insert key/value task
    private static Task insert = task().whileDo(new ConditionalFunction() {
        @Override
        public boolean eval(TaskContext context) {

            Node root = (Node) context.variable("root").get(0);

            Node current = context.resultAsNodes().get(0);
            NodeState state = current.graph().resolver().resolveState(current);
            boolean updateStat = (boolean) context.variable("updatestat").get(0);

            //Get state variables here

            double[] boundMax = (double[]) state.get(_BOUNDMAX);
            double[] boundMin = (double[]) state.get(_BOUNDMIN);
            double[] centerKey = new double[boundMax.length];
            for (int i = 0; i < centerKey.length; i++) {
                centerKey[i] = (boundMax[i] + boundMin[i]) / 2;
            }

            //Get variables from context
            //toDo optimize the variables here
            double[] keyToInsert = (double[]) context.variable("key").get(0);

            double[] precision = (double[]) context.variable("precision").get(0);
            int dim = keyToInsert.length;

            //Update the gaussian of the current node
            if (updateStat) {
                updateGaussian(state, keyToInsert);
            }

            //Check if we can go deeper or not:
            boolean continueNavigation = false;
            for (int i = 0; i < dim; i++) {
                if (boundMax[i] - boundMin[i] > precision[i]) {
                    continueNavigation = true;
                    break;
                }
            }

            if (continueNavigation) {
                //Set the long to traverse
                long traverseId = getRelationId(centerKey, keyToInsert);
                // System.out.println(traverseId-_RELCONST);
                //Check if there is a node already in this subspace, otherwise create it
                if (state.get(traverseId) == null) {
                    double[] newBoundMin = new double[dim];
                    double[] newBoundMax = new double[dim];

                    for (int i = 0; i < centerKey.length; i++) {
                        //Update the bounds in a way that they have always minimum precision[i] of width
                        if (keyToInsert[i] <= centerKey[i]) {
                            newBoundMin[i] = boundMin[i];
                            newBoundMax[i] = Math.max(centerKey[i] - boundMin[i], precision[i]) + boundMin[i];

                        } else {
                            newBoundMin[i] = boundMax[i] - Math.max(boundMax[i] - centerKey[i], precision[i]);
                            newBoundMax[i] = boundMax[i];
                        }
                    }
                    //Create the new subspace node
                    NDTree newChild = (NDTree) current.graph().newTypedNode(current.world(), current.time(), NAME);
                    NodeState newState = newChild.graph().resolver().resolveState(newChild);
                    newState.set(_BOUNDMIN, Type.DOUBLE_ARRAY, newBoundMin);
                    newState.set(_BOUNDMAX, Type.DOUBLE_ARRAY, newBoundMax);
                    Relation relChild = (Relation) state.getOrCreate(traverseId, Type.RELATION);
                    relChild.add(newChild.id());
                    newChild.free();
                }
                //toDo how to optimize not to lookup
                //In all cases we can traverse here
                context.setVariable("next", traverseId);
            } else {
                Node valueToInsert = (Node) context.variable("value").get(0);
                Relation rel = (Relation) state.getOrCreate(_VALUES, Type.RELATION);
                rel.add(valueToInsert.id());
                double[] keys = (double[]) state.get(_KEYS);
                if (keys != null) {
                    double[] newkeys = new double[keys.length + dim];
                    System.arraycopy(keys, 0, newkeys, 0, keys.length);
                    System.arraycopy(keyToInsert, 0, newkeys, keys.length, dim);
                    state.set(_KEYS, Type.DOUBLE_ARRAY, newkeys);
                } else {
                    state.set(_KEYS, Type.DOUBLE_ARRAY, keyToInsert);
                }
                updateCount(true, root);
                //In case we want an reverse relationship we should set it here
            }

            return continueNavigation;
        }

        //todo check how to traverse on long
    }, task().then(Actions.pluginAction(TraverseById.NAME, "{{next}}")));


    private static Task initNearestTask() {
        Task reccursiveDown = task();
        reccursiveDown.then(defineAsVar("parent")).thenDo(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                NDTree current = (NDTree) context.result().get(0);
                NodeState state = current.graph().resolver().resolveState(current);
                NearestNeighborList nnl = (NearestNeighborList) context.variable("nnl").get(0);

                Relation values = (Relation) state.get(_VALUES);

                //Leave node
                if (values != null) {
                    int dim = (int) context.variable("dim").get(0);
                    double[] k = new double[dim];
                    double[] keys = (double[]) state.get(_KEYS);

                    double[] target = (double[]) context.variable("key").get(0);
                    Distance distance = (Distance) context.variable("distance").get(0);

                    for (int i = 0; i < values.size(); i++) {
                        for (int j = 0; j < dim; j++) {
                            k[j] = keys[i * dim + j];
                        }
                        nnl.insert(values.get(i), distance.measure(k, target));
                    }
                    context.continueWith(null);

                } else {
                    final double[] boundMax = (double[]) state.get(_BOUNDMAX);
                    final double[] boundMin = (double[]) state.get(_BOUNDMIN);
                    final double[] target = (double[]) context.variable("key").get(0);
                    final Distance distance = (Distance) context.variable("distance").get(0);
                    final double worst = nnl.getWorstDistance();


                    if (!nnl.isCapacityReached() || getclosestDistance(target, boundMin, boundMax, distance) <= worst) {
                        final double[] precision = (double[]) context.variable("precision").get(0);
                        final int dim = boundMin.length;
                        final double[] childMin = new double[dim];
                        final double[] childMax = new double[dim];

                        final NearestNeighborArrayList temp = new NearestNeighborArrayList();

                        state.each(new NodeStateCallback() {
                            @Override
                            public void on(long attributeKey, byte elemType, Object elem) {
                                if (attributeKey >= _RELCONST) {
                                    boolean[] binaries = binaryFromLong(attributeKey, dim);
                                    for (int i = 0; i < dim; i++) {
                                        if (!binaries[i]) {
                                            childMin[i] = boundMin[i];
                                            childMax[i] = Math.max((boundMax[i] - boundMin[i]) / 2, precision[i]) + boundMin[i];

                                        } else {
                                            childMin[i] = boundMax[i] - Math.max((boundMax[i] - boundMin[i]) / 2, precision[i]);
                                            childMax[i] = boundMax[i];
                                        }
                                    }
                                    temp.insert(attributeKey, getclosestDistance(target, childMin, childMax, distance));
                                }
                            }
                        });

                        temp.sort();
                        long[] relations = temp.getNodes();
                        //double[] distances =temp.getDistances();
                        context.continueWith(context.wrap(relations));
                    } else {
                        context.continueWith(null);
                    }
                }
            }
        }).forEach(task().then(defineAsVar("relid")).then(readVar("parent")).then(pluginAction(TraverseById.NAME, "{{relid}}")).map(reccursiveDown));


        return reccursiveDown;
    }

    private static Task initRadusTask() {
        Task reccursiveDown = task();
        reccursiveDown.then(defineAsVar("parent")).thenDo(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                NDTree current = (NDTree) context.result().get(0);
                NodeState state = current.graph().resolver().resolveState(current);
                NearestNeighborArrayList nnl = (NearestNeighborArrayList) context.variable("nnl").get(0);

                Relation values = (Relation) state.get(_VALUES);

                //Leave node
                if (values != null) {
                    int dim = (int) context.variable("dim").get(0);
                    double[] k = new double[dim];
                    double[] keys = (double[]) state.get(_KEYS);

                    double[] target = (double[]) context.variable("key").get(0);
                    Distance distance = (Distance) context.variable("distance").get(0);

                    for (int i = 0; i < values.size(); i++) {
                        for (int j = 0; j < dim; j++) {
                            k[j] = keys[i * dim + j];
                        }
                        nnl.insert(values.get(i), distance.measure(k, target));
                    }
                    context.continueWith(null);

                } else {
                    final double[] boundMax = (double[]) state.get(_BOUNDMAX);
                    final double[] boundMin = (double[]) state.get(_BOUNDMIN);
                    final double[] target = (double[]) context.variable("key").get(0);
                    final Distance distance = (Distance) context.variable("distance").get(0);
                    final double radius = (double) context.variable("radius").get(0);


                    if (getclosestDistance(target, boundMin, boundMax, distance) <= radius) {
                        final double[] precision = (double[]) context.variable("precision").get(0);
                        final int dim = boundMin.length;
                        final double[] childMin = new double[dim];
                        final double[] childMax = new double[dim];

                        final NearestNeighborArrayList temp = new NearestNeighborArrayList();

                        state.each(new NodeStateCallback() {
                            @Override
                            public void on(long attributeKey, byte elemType, Object elem) {
                                if (attributeKey >= _RELCONST) {
                                    boolean[] binaries = binaryFromLong(attributeKey, dim);
                                    for (int i = 0; i < dim; i++) {
                                        if (!binaries[i]) {
                                            childMin[i] = boundMin[i];
                                            childMax[i] = Math.max((boundMax[i] - boundMin[i]) / 2, precision[i]) + boundMin[i];

                                        } else {
                                            childMin[i] = boundMax[i] - Math.max((boundMax[i] - boundMin[i]) / 2, precision[i]);
                                            childMax[i] = boundMax[i];
                                        }
                                    }
                                    temp.insert(attributeKey, getclosestDistance(target, childMin, childMax, distance));
                                }
                            }
                        });

                        temp.sort();
                        long[] relations = temp.getNodes();
                        //double[] distances =temp.getDistances();
                        context.continueWith(context.wrap(relations));
                    } else {
                        context.continueWith(null);
                    }
                }
            }
        }).forEach(task().then(defineAsVar("relid")).then(readVar("parent")).then(pluginAction(TraverseById.NAME, "{{relid}}")).map(reccursiveDown));


        return reccursiveDown;
    }


    //endregion


    //region Gaussian and stat related code for each node subspace
    public void setUpdateStat(boolean value) {
        NodeState state = unphasedState();
        state.set(_STAT, Type.BOOL, value);
    }

    private static void updateGaussian(NodeState state, double[] key) {
        int total = 0;
        Integer x = (Integer) state.get(_TOTAL);
        if (x != null) {
            total = x;
        }
        if (total == 0) {
            state.set(_TOTAL, Type.INT, 1);
            state.set(_SUM, Type.DOUBLE_ARRAY, key);
        } else {
            int features = key.length;
            double[] sum;
//            double[] min;
//            double[] max;
            double[] sumsquares;

            //Upgrade dirac to gaussian
            if (total == 1) {
                //Create getMin, getMax, sumsquares
                sum = (double[]) state.get(_SUM);
//                min = new double[features];
//                max = new double[features];
//                System.arraycopy(sum, 0, min, 0, features);
//                System.arraycopy(sum, 0, max, 0, features);
                sumsquares = new double[features * (features + 1) / 2];
                int count = 0;
                for (int i = 0; i < features; i++) {
                    for (int j = i; j < features; j++) {
                        sumsquares[count] = sum[i] * sum[j];
                        count++;
                    }
                }
            }
            //Otherwise, get previously stored values
            else {
                sum = (double[]) state.get(_SUM);
//                min = (double[]) state.get(_MIN);
//                max = (double[]) state.get(_MAX);
                sumsquares = (double[]) state.get(_SUMSQ);
            }

            //Update the values
            for (int i = 0; i < features; i++) {
//                if (key[i] < min[i]) {
//                    min[i] = key[i];
//                }
//
//                if (key[i] > max[i]) {
//                    max[i] = key[i];
//                }
                sum[i] += key[i];
            }

            int count = 0;
            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    sumsquares[count] += key[i] * key[j];
                    count++;
                }
            }
            total++;
            //Store everything
            state.set(_TOTAL, Type.INT, total);
            state.set(_SUM, Type.DOUBLE_ARRAY, sum);
//            state.set(_MIN, Type.DOUBLE_ARRAY, min);
//            state.set(_MAX, Type.DOUBLE_ARRAY, max);
            state.set(_SUMSQ, Type.DOUBLE_ARRAY, sumsquares);
        }
    }

    public int getTotal() {
        Integer x = (Integer) super.getAt(_TOTAL);
        if (x == null) {
            return 0;
        } else {
            return x;
        }
    }

    public double[] getAvg() {
        int total = getTotal();
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            return (double[]) super.getAt(_SUM);
        } else {
            double[] avg = (double[]) super.getAt(_SUM);
            for (int i = 0; i < avg.length; i++) {
                avg[i] = avg[i] / total;
            }
            return avg;
        }

    }

    public double[] getCovarianceArray(double[] avg, double[] err) {
        if (avg == null) {
            double[] errClone = new double[err.length];
            System.arraycopy(err, 0, errClone, 0, err.length);
            return errClone;
        }
        if (err == null) {
            err = new double[avg.length];
        }
        int features = avg.length;

        int total = getTotal();
        if (total == 0) {
            return null;
        }
        if (total > 1) {
            double[] covariances = new double[features];
            double[] sumsquares = (double[]) super.getAt(_SUMSQ);

            double correction = total;
            correction = correction / (total - 1);

            int count = 0;
            for (int i = 0; i < features; i++) {
                covariances[i] = (sumsquares[count] / total - avg[i] * avg[i]) * correction;
                if (covariances[i] < err[i]) {
                    covariances[i] = err[i];
                }
                count += features - i;
            }
            return covariances;
        } else {
            double[] errClone = new double[err.length];
            System.arraycopy(err, 0, errClone, 0, err.length);
            return errClone;
        }
    }

    //endregion


    /**
     * @native ts
     * var result = Long.UZERO;
     * for(var i = 0; i < centerKey.length; i++) {
     * if(i!=0){
     * result = result.shiftLeft(1);
     * }
     * if (keyToInsert[i] > centerKey[i]) {
     * result = result.add(Long.ONE);
     * }
     * }
     * return result.add(Long.fromNumber(org.mwg.structure.tree.NDTree._RELCONST, true)).toNumber();
     */
    static long getRelationId(double[] centerKey, double[] keyToInsert) {
        long result = 0;
        for (int i = 0; i < centerKey.length; i++) {
            if (i != 0) {
                result = result << 1;
            }
            if (keyToInsert[i] > centerKey[i]) {
                result += 1;
            }
        }
        return result + _RELCONST;
    }

    static boolean[] binaryFromLong(final long value, final int dim) {
        long tempvalue = value - _RELCONST;
        long shiftvalue = tempvalue >> 1;
        boolean[] res = new boolean[dim];
        for (int i = 0; i < dim; i++) {
            res[dim - i - 1] = ((tempvalue - (shiftvalue << 1)) == 1);
            tempvalue = shiftvalue;
            shiftvalue = tempvalue >> 1;
        }
        return res;
    }


    protected Distance getDistance(NodeState state) {
        int d = state.getWithDefault(_DISTANCE, DISTANCE_TYPE_DEF);
        Distance distance;
        if (d == Distances.EUCLIDEAN) {
            distance = EuclideanDistance.instance();
        } else if (d == Distances.GEODISTANCE) {
            distance = GeoDistance.instance();
        } else {
            throw new RuntimeException("Unknown distance code metric");
        }
        return distance;
    }


    @Override
    public void setDistance(int distanceType) {
        setAt(_DISTANCE, Type.INT, distanceType);
    }

    @Override
    public void setFrom(String extractor) {
        setAt(_FROM, Type.STRING, extractor);
    }


    @Override
    public void nearestN(final double[] key, final int n, final Callback<Node[]> callback) {
        NodeState state = unphasedState();
        int dim = state.getWithDefault(_DIM, key.length);

        checkKey(state, key, dim);

        final NearestNeighborList nnl = new NearestNeighborList(n);
        Distance distance = getDistance(state);


        TaskContext tc = nearestTask.prepare(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {

                long[] res = nnl.getNodes();
                if (res.length != 0) {
                    Task lookupall = task().then(setWorld(String.valueOf(world()))).then(setTime(String.valueOf(time()))).then(readVar("res")).then(lookupAll("{{result}}"));
                    TaskContext tc = lookupall.prepare(graph(), null, new Callback<TaskResult>() {
                        @Override
                        public void on(TaskResult result) {

                            final Node[] finalres = new Node[result.size()];
                            for (int i = 0; i < result.size(); i++) {
                                finalres[i] = (Node) result.get(i);
                            }
                            callback.on(finalres);
                        }
                    });

                    TaskResult tr = tc.wrap(res);
                    tc.addToGlobalVariable("res", tr);
                    lookupall.executeUsing(tc);
                } else {
                    callback.on(new Node[0]);
                }
            }
        });

        TaskResult res = tc.newResult();
        res.add(key);

        // (this, distance, key, hr, max_dist_sqd, 0, dim, err, nnl);

        tc.setGlobalVariable("key", res);
        tc.setGlobalVariable("distance", distance);
        tc.setGlobalVariable("dim", dim);
        final double[] precisions = (double[]) state.get(_PRECISION);
        TaskResult resPres = tc.newResult();
        resPres.add(precisions);
        tc.setGlobalVariable("precision", resPres);
        tc.setGlobalVariable("nnl", nnl);

        nearestTask.executeUsing(tc);
    }

    @Override
    public void nearestWithinRadius(double[] key, double radius, Callback<Node[]> callback) {
        NodeState state = unphasedState();
        int dim = state.getWithDefault(_DIM, key.length);
        checkKey(state, key, dim);

        final NearestNeighborArrayList nnl = new NearestNeighborArrayList();
        Distance distance = getDistance(state);


        TaskContext tc = nearestRadiusTask.prepare(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {

                long[] res = nnl.getNodes();
                if (res.length != 0) {
                    Task lookupall = task().then(setWorld(String.valueOf(world()))).then(setTime(String.valueOf(time()))).then(readVar("res")).then(lookupAll("{{result}}"));
                    TaskContext tc = lookupall.prepare(graph(), null, new Callback<TaskResult>() {
                        @Override
                        public void on(TaskResult result) {

                            final Node[] finalres = new Node[result.size()];
                            for (int i = 0; i < result.size(); i++) {
                                finalres[i] = (Node) result.get(i);
                            }
                            callback.on(finalres);
                        }
                    });

                    TaskResult tr = tc.wrap(res);
                    tc.addToGlobalVariable("res", tr);
                    lookupall.executeUsing(tc);
                } else {
                    callback.on(new Node[0]);
                }
            }
        });

        TaskResult res = tc.newResult();
        res.add(key);

        // (this, distance, key, hr, max_dist_sqd, 0, dim, err, nnl);

        tc.setGlobalVariable("key", res);
        tc.setGlobalVariable("distance", distance);
        tc.setGlobalVariable("dim", dim);
        tc.setGlobalVariable("radius", radius);
        final double[] precisions = (double[]) state.get(_PRECISION);
        TaskResult resPres = tc.newResult();
        resPres.add(precisions);
        tc.setGlobalVariable("precision", resPres);
        tc.setGlobalVariable("nnl", nnl);

        nearestRadiusTask.executeUsing(tc);
    }

    @Override
    public void nearestNWithinRadius(double[] key, int nbElem, double radius, Callback<Node[]> callback) {
        NodeState state = unphasedState();
        int dim = state.getWithDefault(_DIM, key.length);
        checkKey(state, key, dim);

        final NearestNeighborList nnl = new NearestNeighborList(nbElem);
        Distance distance = getDistance(state);


        TaskContext tc = nearestTask.prepare(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {

                long[] res = nnl.getAllNodesWithin(radius);
                if (res.length != 0) {
                    Task lookupall = task().then(setWorld(String.valueOf(world()))).then(setTime(String.valueOf(time()))).then(readVar("res")).then(lookupAll("{{result}}"));
                    TaskContext tc = lookupall.prepare(graph(), null, new Callback<TaskResult>() {
                        @Override
                        public void on(TaskResult result) {

                            final Node[] finalres = new Node[result.size()];
                            for (int i = 0; i < result.size(); i++) {
                                finalres[i] = (Node) result.get(i);
                            }
                            callback.on(finalres);
                        }
                    });

                    TaskResult tr = tc.wrap(res);
                    tc.addToGlobalVariable("res", tr);
                    lookupall.executeUsing(tc);
                } else {
                    callback.on(new Node[0]);
                }
            }
        });

        TaskResult res = tc.newResult();
        res.add(key);

        // (this, distance, key, hr, max_dist_sqd, 0, dim, err, nnl);

        tc.setGlobalVariable("key", res);
        tc.setGlobalVariable("distance", distance);
        tc.setGlobalVariable("dim", dim);
//        tc.defineVariable("lev", 0);
        final double[] precisions = (double[]) state.get(_PRECISION);
        TaskResult resPres = tc.newResult();
        resPres.add(precisions);
        tc.setGlobalVariable("precision", resPres);
        tc.setGlobalVariable("nnl", nnl);

        nearestTask.executeUsing(tc);
    }


    private boolean checkKey(NodeState state, double[] key, int dim) {
        if (key.length != dim) {
            throw new RuntimeException("Key size should always be the same");
        }

        double[] min = (double[]) state.get(_BOUNDMIN);
        double[] max = (double[]) state.get(_BOUNDMAX);

        if (min == null || max == null) {
            throw new RuntimeException("Please set min and max boundary before inserting in the tree");
        }

        for (int i = 0; i < dim; i++) {
            if (key[i] < min[i] || key[i] > max[i]) {
                throw new RuntimeException("Key should be between min and max boundaries");
            }
        }

        return true;
    }


    @Override
    public void insertWith(double[] key, Node value, Callback<Boolean> callback) {
        NodeState state = unphasedState();

        if (value == null || key == null) {
            return;
        }

        if (state.get(_DIM) == null) {
            state.set(_DIM, Type.INT, key.length);
        }

        final int dim = state.getWithDefault(_DIM, key.length);
        checkKey(state, key, dim);


        TaskContext tc = insert.prepare(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                result.free();
                if (callback != null) {
                    callback.on(true);
                }
            }
        });

        TaskResult res = tc.newResult();
        res.add(key);

        //Set global variables
        tc.setGlobalVariable("key", res);
        tc.setGlobalVariable("value", value);


        final double[] precisions = (double[]) state.get(_PRECISION);

        final boolean updateStat = (boolean) state.getWithDefault(_STAT, STAT_DEF);
        tc.setGlobalVariable("updatestat", updateStat);

        tc.setGlobalVariable("root", this);
        TaskResult resPres = tc.newResult();
        resPres.add(precisions);
        tc.setGlobalVariable("precision", resPres);

        //Set local variables
        insert.executeUsing(tc);
    }


    protected void extractFeatures(final Node current, final Callback<double[]> callback) {
        String query = (String) super.getAt(_FROM);
        if (query != null) {
            //TODO CACHE TO AVOID PARSING EVERY TIME
            String[] split = query.split(",");
            Task[] tasks = new Task[split.length];
            for (int i = 0; i < split.length; i++) {
                Task t = task().then(setWorld("" + world()));
                t.then(setTime(time() + ""));
                t.parse(split[i].trim());
                tasks[i] = t;
            }
            //END TODO IN CACHE
            final double[] result = new double[tasks.length];
            final DeferCounter waiter = graph().newCounter(tasks.length);
            for (int i = 0; i < split.length; i++) {
                //prepare initial result
                final TaskResult initial = emptyResult();
                initial.add(current);
                //prepare initial context
                final Callback<Integer> capsule = new Callback<Integer>() {
                    @Override
                    public void on(final Integer i) {
                        tasks[i].executeWith(graph(), initial, new Callback<TaskResult>() {
                            @Override
                            public void on(TaskResult currentResult) {
                                if (currentResult == null) {
                                    result[i] = Constants.NULL_LONG;
                                } else {
                                    result[i] = Double.parseDouble(currentResult.get(0).toString());
                                    currentResult.free();
                                }
                                waiter.count();
                            }
                        });
                    }
                };
                capsule.on(i);
            }
            waiter.then(new Job() {
                @Override
                public void run() {
                    callback.on(result);
                }
            });
        } else {
            callback.on(null);
        }
    }

    @Override
    public void insert(Node value, Callback<Boolean> callback) {
        extractFeatures(value, new Callback<double[]>() {
            @Override
            public void on(final double[] result) {
                insertWith(result, value, callback);
            }
        });
    }


    @Override
    public int size() {
        if (getAt(_NUMNODES) != null) {
            return (int) getAt(_NUMNODES);
        } else {
            return 1;
        }
    }


    private static double getclosestDistance(double[] target, double[] boundMin, double[] boundMax, Distance distance) {
        double[] closest = new double[target.length];
        for (int i = 0; i < target.length; i++) {
            if (target[i] >= boundMax[i]) {
                closest[i] = boundMax[i];
            } else if (target[i] <= boundMin[i]) {
                closest[i] = boundMin[i];
            } else {
                closest[i] = target[i];
            }
        }
        return distance.measure(closest, target);
    }


}