package org.mwg.ml.algorithm.profiling;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.core.task.Actions;
import org.mwg.ml.BaseMLNode;
import org.mwg.ml.ProfilingNode;
import org.mwg.ml.common.ActionTraverseOrKeep;
import org.mwg.ml.common.NDimentionalArray;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution;
import org.mwg.plugin.NodeState;
import org.mwg.struct.Matrix;
import org.mwg.struct.Relation;
import org.mwg.task.*;
import org.mwg.utility.Enforcer;

import static org.mwg.core.task.Actions.*;

public class GaussianMixtureNode extends BaseMLNode implements ProfilingNode {

    //Getters and setters
    public final static String NAME = "GaussianMixtureNode";

    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String AVG = "avg";
    public static final String COV = "cov";

    //Mixture model params
    public static final String LEVEL = "_level";  //Current level of the gaussian node, top level is the highest number, bottom leaves have level 0.
    public static final int LEVEL_DEF = 0;
    public static final String WIDTH = "_width";  // Nuber of children after compressing, note that Factor x wodth is the max per level tolerated before compressing
    public static final int WIDTH_DEF = 10;
    public static final String COMPRESSION_FACTOR = "_compression";  // Factor of subnodes allowed before starting compression. For ex: 2 => 2x Width before compressing to width
    public static final double COMPRESSION_FACTOR_DEF = 2;
    public static final String COMPRESSION_ITER = "_compressioniter"; //Number of time to iterate K-means before finding the best compression
    public static final int COMPRESSION_ITER_DEF = 10;
    public static final String THRESHOLD = "_threshold";  //Factor of distance before check inside fail
    public static final double THRESHOLD_DEF = 3;

    public static final String PRECISION = "_precision"; //Default covariance matrix for a dirac function

    //Gaussian keys
    public static final String INTERNAL_SUBGAUSSIAN = "_subGaussian";
    private static final String INTERNAL_SUM_KEY = "_sum";
    private static final String INTERNAL_SUMSQUARE_KEY = "_sumSquare";
    private static final String INTERNAL_TOTAL_KEY = "_total";
    private static final String INTERNAL_MIN_KEY = "_min";
    private static final String INTERNAL_MAX_KEY = "_max";

    public GaussianMixtureNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    private static final Enforcer enforcer = new Enforcer()
            .asIntWithin(LEVEL, 0, 1000)
            .asIntWithin(WIDTH, 1, 1000)
            .asPositiveDouble(COMPRESSION_FACTOR)
            .asPositiveDouble(THRESHOLD)
            .asDoubleArray(PRECISION);

    @Override
    public Node set(String propertyName, byte propertyType, Object propertyValue) {
        enforcer.check(propertyName, propertyType, propertyValue);
        return super.set(propertyName, propertyType, propertyValue);
    }

    @Override
    public byte type(String attributeName) {
        if (attributeName.equals(AVG)) {
            return Type.DOUBLE_ARRAY;
        } else if (attributeName.equals(MIN)) {
            return Type.DOUBLE_ARRAY;
        } else if (attributeName.equals(MAX)) {
            return Type.DOUBLE_ARRAY;
        } else if (attributeName.equals(COV)) {
            return Type.DOUBLE_ARRAY;
        } else if (attributeName.equals(PRECISION)) {
            return Type.DOUBLE_ARRAY;
        } else {
            return super.type(attributeName);
        }
    }

    @Override
    public Object get(String attributeName) {
        if (attributeName.equals(AVG)) {
            return getAvg();
        } else if (attributeName.equals(MIN)) {
            return getMin();
        } else if (attributeName.equals(MAX)) {
            return getMax();
        } else if (attributeName.equals(MAX)) {
            return getMax();
        } else if (attributeName.equals(COV)) {
            final NodeState resolved = this._resolver.resolveState(this);
            double[] initialPrecision = (double[]) resolved.getFromKey(PRECISION);
            int nbfeature = this.getNumberOfFeatures();
            if (initialPrecision == null) {
                initialPrecision = new double[nbfeature];
                for (int i = 0; i < nbfeature; i++) {
                    initialPrecision[i] = 1;
                }
            }
            return getCovariance(getAvg(), initialPrecision);
        } else {
            return super.get(attributeName);
        }
    }


    @Override
    public void learn(final Callback<Boolean> callback) {
        extractFeatures(new Callback<double[]>() {
            @Override
            public void on(double[] values) {
                //ToDO temporal hack to avoid features extractions - to remove later
                learnVector(values, callback);
            }
        });
    }


    //ToDO temporal hack to avoid features extractions - to remove later
    public void learnVector(final double[] values, final Callback<Boolean> callback) {
        final NodeState resolved = this._resolver.alignState(this);
        final int width = resolved.getFromKeyWithDefault(WIDTH, WIDTH_DEF);
        final double compressionFactor = resolved.getFromKeyWithDefault(COMPRESSION_FACTOR, COMPRESSION_FACTOR_DEF);
        final int compressionIter = resolved.getFromKeyWithDefault(COMPRESSION_ITER, COMPRESSION_ITER_DEF);
        double[] initialPrecision = (double[]) resolved.getFromKey(PRECISION);
        if (initialPrecision == null) {
            initialPrecision = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                initialPrecision[i] = 1;
            }
        }
        final double[] precisions = initialPrecision;
        final double threshold = resolved.getFromKeyWithDefault(THRESHOLD, THRESHOLD_DEF);

        final GaussianMixtureNode self = this;

        Task traverse = task();
        traverse.then(defineAsGlobalVar("parent")).then(Actions.get(INTERNAL_SUBGAUSSIAN)).then(new Action() {
            @Override
            public void eval(TaskContext context) {
                TaskResult<Node> result = context.resultAsNodes();
                GaussianMixtureNode parent = (GaussianMixtureNode) context.variable("parent").get(0);
                GaussianMixtureNode resultChild = self.filter(result, values, precisions, threshold, parent.getLevel() - 1.0);
                if (resultChild != null) {
                    parent.internallearn(values, width, compressionFactor, compressionIter, precisions, threshold, false);
                    context.continueWith(context.wrapClone(resultChild));
                } else {
                    parent.internallearn(values, width, compressionFactor, compressionIter, precisions, threshold, true);
                    context.continueWith(null);
                }

            }
        }).ifThen(new ConditionalFunction() {
            @Override
            public boolean eval(TaskContext context) {
                return (context.result() != null);
            }
        }, traverse);

        Task mainTask = task().then(setTime(time() + "")).then(setWorld(world() + "")).then(inject(this)).map(traverse);
        mainTask.execute(graph(), new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                if (result != null) {
                    result.free();
                }
                if (callback != null) {
                    callback.on(true);
                }
            }
        });
    }

    private boolean checkInside(final double[] min, final double[] max, final double[] precisions, double threshold, double level) {
        threshold = threshold + level * 0.707;

        double[] avg = getAvg();
        boolean result = true;
        double[] cov = getCovarianceArray(avg, precisions);


        for (int i = 0; i < min.length; i++) {
            cov[i] = Math.sqrt(cov[i]);
            if (((avg[i] + cov[i]) < (min[i] - threshold * precisions[i])) || ((avg[i] - cov[i]) > (max[i] + threshold * precisions[i]))) {
                result = false;
                break;
            }
        }
        return result;
    }

    //ToDo need to be replaced by gaussian distances !!
    private GaussianMixtureNode filter(final TaskResult<Node> result, final double[] features, final double[] precisions, double threshold, double level) {
        threshold = threshold + level * 0.707;


        if (result == null || result.size() == 0) {
            return null;
        }
        double[] distances = new double[result.size()];
        double min = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < result.size(); i++) {
            GaussianMixtureNode temp = ((GaussianMixtureNode) result.get(i));
            double[] avg = temp.getAvg();
            distances[i] = distance(features, avg, temp.getCovarianceArray(avg, precisions));
            if (distances[i] < min) {
                min = distances[i];
                index = i;
            }
        }
        if (min < threshold) {
            return ((GaussianMixtureNode) result.get(index));
        } else {
            return null;
        }
    }

    @Override
    public void predict(Callback<double[]> callback) {

    }


    public int getLevel() {
        return this._resolver.resolveState(this).getFromKeyWithDefault(LEVEL, LEVEL_DEF);
    }

    public int getWidth() {
        return this._resolver.resolveState(this).getFromKeyWithDefault(WIDTH, WIDTH_DEF);
    }

    public double getCompressionFactor() {
        return this._resolver.resolveState(this).getFromKeyWithDefault(COMPRESSION_FACTOR, COMPRESSION_FACTOR_DEF);
    }

    public int getCompressionIter() {
        return this._resolver.resolveState(this).getFromKeyWithDefault(COMPRESSION_ITER, COMPRESSION_ITER_DEF);
    }


    private void updateLevel(final int newLevel) {
        super.set(LEVEL, Type.INT, newLevel);
        if (newLevel == 0) {
            super.set(INTERNAL_SUBGAUSSIAN, Type.LONG_ARRAY, new long[0]);
        } else {
            super.relation(INTERNAL_SUBGAUSSIAN, new Callback<Node[]>() {
                @Override
                public void on(Node[] result) {
                    for (int i = 0; i < result.length; i++) {
                        GaussianMixtureNode g = (GaussianMixtureNode) result[i];
                        g.updateLevel(newLevel - 1);
                        result[i].free();
                    }
                }
            });
        }
    }

    private GaussianMixtureNode createLevel(double[] values, final int level, final int width, final double compressionFactor, final int compressionIter, final double[] precisions, final double threshold) {
        GaussianMixtureNode g = (GaussianMixtureNode) graph().newTypedNode(this.world(), this.time(), NAME);
        g.set(LEVEL, Type.INT, level);
        g.internallearn(values, width, compressionFactor, compressionIter, precisions, threshold, false); //dirac
        super.addToRelation(INTERNAL_SUBGAUSSIAN, g);
        return g;
    }

    private void checkAndCompress(final int width, final double compressionFactor, final int compressionIter, final double[] precisions, final double threshold) {

        final Node selfPointer = this;

        Relation subgaussians = (Relation) super.get(INTERNAL_SUBGAUSSIAN);
        if (subgaussians != null && subgaussians.size() != 0 && subgaussians.size() >= compressionFactor * width) {
            super.relation(INTERNAL_SUBGAUSSIAN, new Callback<Node[]>() {
                @Override
                //result.length hold the original subgaussian number, and width is after compression
                public void on(Node[] result) {
                    GaussianMixtureNode[] subgauss = new GaussianMixtureNode[result.length];
                    double[][] data = new double[result.length][];
                    for (int i = 0; i < result.length; i++) {
                        subgauss[i] = (GaussianMixtureNode) result[i];
                        data[i] = subgauss[i].getAvg();
                    }

                    //Cluster the different gaussians
                    KMeans clusteringEngine = new KMeans();
                    int[][] clusters = clusteringEngine.getClusterIds(data, width, compressionIter, precisions);

                    //Select the ones which will remain as head by the maximum weight
                    GaussianMixtureNode[] mainClusters = new GaussianMixtureNode[width];
                    for (int i = 0; i < width; i++) {
                        if (clusters[i] != null && clusters[i].length > 0) {
                            int max = 0;
                            int maxpos = 0;
                            for (int j = 0; j < clusters[i].length; j++) {
                                int x = subgauss[clusters[i][j]].getTotal();
                                if (x > max) {
                                    max = x;
                                    maxpos = clusters[i][j];
                                }
                            }
                            mainClusters[i] = subgauss[maxpos];
                        }
                    }


                    //move the nodes
                    for (int i = 0; i < width; i++) {
                        //if the main cluster node contains only 1 sample, it needs to clone itself in itself
                        if (clusters[i].length > 1 && mainClusters[i].getTotal() == 1 && mainClusters[i].getLevel() > 0) {
                            mainClusters[i].createLevel(mainClusters[i].getAvg(), mainClusters[i].getLevel() - 1, width, compressionFactor, compressionIter, precisions, threshold).free();
                        }

                        if (clusters[i] != null && clusters[i].length > 0) {
                            for (int j = 0; j < clusters[i].length; j++) {
                                GaussianMixtureNode g = subgauss[clusters[i][j]];
                                if (g != mainClusters[i]) {
                                    mainClusters[i].move(g);
                                    selfPointer.removeFromRelation(INTERNAL_SUBGAUSSIAN, g);
                                    g.free();
                                }
                            }
                            mainClusters[i].checkAndCompress(width, compressionFactor, compressionIter, precisions, threshold);
                        }
                    }

                    for (int i = 0; i < result.length; i++) {
                        result[i].free();
                    }
                }
            });
        }
    }


    private void move(GaussianMixtureNode subgaus) {
        //manage total
        int total = getTotal();
        int level = getLevel();


        double[] sum = getSum();
        double[] min = getMin();
        double[] max = getMax();
        double[] sumsquares = getSumSquares();


        //Start the merging phase

        total = total + subgaus.getTotal();

        double[] sum2 = subgaus.getSum();
        double[] min2 = subgaus.getMin();
        double[] max2 = subgaus.getMax();
        double[] sumsquares2 = subgaus.getSumSquares();

        for (int i = 0; i < sum.length; i++) {
            sum[i] = sum[i] + sum2[i];
            if (min2[i] < min[i]) {
                min[i] = min2[i];
            }
            if (max2[i] > max[i]) {
                max[i] = max2[i];
            }
        }

        for (int i = 0; i < sumsquares.length; i++) {
            sumsquares[i] = sumsquares[i] + sumsquares2[i];
        }

        //Store everything
        set(INTERNAL_TOTAL_KEY, Type.INT, total);
        set(INTERNAL_SUM_KEY, Type.DOUBLE_ARRAY, sum);
        set(INTERNAL_MIN_KEY, Type.DOUBLE_ARRAY, min);
        set(INTERNAL_MAX_KEY, Type.DOUBLE_ARRAY, max);
        set(INTERNAL_SUMSQUARE_KEY, Type.DOUBLE_ARRAY, sumsquares);

        //Add the subGaussian to the relationship
        if (level > 0) {
            Relation subrelations = (Relation) subgaus.get(INTERNAL_SUBGAUSSIAN);
            if (subrelations == null || subrelations.size() == 0) {
                subgaus.updateLevel(level - 1);
                super.addToRelation(INTERNAL_SUBGAUSSIAN, subgaus);
            } else {
                Relation oldrel = (Relation) this.getOrCreate(INTERNAL_SUBGAUSSIAN, Type.RELATION);
                for (int i = 0; i < subrelations.size(); i++) {
                    oldrel.add(subrelations.get(i));
                }
            }
        }
    }


    public void query(int level, double[] min, double[] max, final Callback<ProbaDistribution> callback) {
        final int nbfeature = this.getNumberOfFeatures();
        if (nbfeature == 0) {
            callback.on(null);
            return;
        }
        final NodeState resolved = this._resolver.resolveState(this);
        double[] initialPrecision = (double[]) resolved.getFromKey(PRECISION);
        if (initialPrecision == null) {
            initialPrecision = new double[nbfeature];
            for (int i = 0; i < nbfeature; i++) {
                initialPrecision[i] = 1;
            }
        }
        if (min == null) {
            min = getMin();
        }
        if (max == null) {
            max = getMax();
        }

        for (int i = 0; i < nbfeature; i++) {
            if ((max[i] - min[i]) < initialPrecision[i]) {
                min[i] = min[i] - initialPrecision[i];
                max[i] = min[i] + 2 * initialPrecision[i];
            }
        }

        final double[] finalMin = min;
        final double[] finalMax = max;
        final double[] err = initialPrecision;
        final double threshold = resolved.getFromKeyWithDefault(THRESHOLD, THRESHOLD_DEF);

        //At this point we have min and max at least with 2xerr of difference

        Task deepTraverseTask = task().then(setTime(time() + "")).then(setWorld(world() + ""));
        final int parentLevel = this.getLevel();

        deepTraverseTask.then(inject(new Node[]{this}));
        for (int i = 0; i < this.getLevel() - level; i++) {
            deepTraverseTask.then(new ActionTraverseOrKeep(INTERNAL_SUBGAUSSIAN));
            final int finalI = i;
            deepTraverseTask.then(select(new TaskFunctionSelect() {
                @Override
                public boolean select(Node node, TaskContext context) {
                    return ((GaussianMixtureNode) node).checkInside(finalMin, finalMax, err, threshold, parentLevel - finalI);
                }
            }));
        }

        deepTraverseTask.execute(graph(), new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {

                Matrix covBackup = VolatileMatrix.empty(nbfeature, nbfeature);
                for (int i = 0; i < nbfeature; i++) {
                    covBackup.set(i, i, err[i]);
                }
                MultivariateNormalDistribution mvnBackup = new MultivariateNormalDistribution(null, covBackup, false);

                int[] totals = new int[result.size()];
                int globalTotal = 0;

                MultivariateNormalDistribution[] distributions = new MultivariateNormalDistribution[result.size()];
                for (int i = 0; i < result.size(); i++) {
                    GaussianMixtureNode temp = ((GaussianMixtureNode) result.get(i));
                    totals[i] = temp.getTotal();
                    globalTotal += totals[i];
                    double[] avg = temp.getAvg();
                    if (totals[i] > 2) {
                        distributions[i] = new MultivariateNormalDistribution(avg, temp.getCovariance(avg, err), false);
                        distributions[i].setMin(temp.getMin());
                        distributions[i].setMax(temp.getMax());
                    } else {
                        distributions[i] = mvnBackup.clone(avg); //this can be optimized later by inverting covBackup only once
                    }
                }

                result.free();
                callback.on(new ProbaDistribution(totals, distributions, globalTotal));
            }
        });
    }

    public void generateDistributions(int level, final Callback<ProbaDistribution> callback) {
        final int nbfeature = this.getNumberOfFeatures();
        if (nbfeature == 0) {
            callback.on(null);
            return;
        }
        final NodeState resolved = this._resolver.resolveState(this);
        double[] initialPrecision = (double[]) resolved.getFromKey(PRECISION);
        if (initialPrecision == null) {
            initialPrecision = new double[nbfeature];
            for (int i = 0; i < nbfeature; i++) {
                initialPrecision[i] = 1;
            }
        }
        final double[] err = initialPrecision;
        Task deepTraverseTask = task().then(setTime(time() + "")).then(setWorld(world() + ""));
        deepTraverseTask.then(inject(new Node[]{this}));
        for (int i = 0; i < this.getLevel() - level; i++) {
            deepTraverseTask.then(new ActionTraverseOrKeep(INTERNAL_SUBGAUSSIAN));
        }

        deepTraverseTask.then(new Action() {
            @Override
            public void eval(TaskContext context) {
                TaskResult<Node> leaves = context.resultAsNodes();   // to check
                Matrix covBackup = VolatileMatrix.empty(nbfeature, nbfeature);
                for (int i = 0; i < nbfeature; i++) {
                    covBackup.set(i, i, err[i]);
                }
                MultivariateNormalDistribution mvnBackup = new MultivariateNormalDistribution(null, covBackup, false);

                int[] totals = new int[leaves.size()];
                int globalTotal = 0;

                MultivariateNormalDistribution[] distributions = new MultivariateNormalDistribution[leaves.size()];
                for (int i = 0; i < leaves.size(); i++) {
                    GaussianMixtureNode temp = ((GaussianMixtureNode) leaves.get(i));
                    totals[i] = temp.getTotal();
                    globalTotal += totals[i];
                    double[] avg = temp.getAvg();
                    if (totals[i] > 2) {
                        distributions[i] = new MultivariateNormalDistribution(avg, temp.getCovariance(avg, err), false);
                        distributions[i].setMin(temp.getMin());
                        distributions[i].setMax(temp.getMax());
                    } else {
                        distributions[i] = mvnBackup.clone(avg); //this can be optimized later by inverting covBackup only once
                    }
                }
                context.continueTask();
                callback.on(new ProbaDistribution(totals, distributions, globalTotal));
            }
        });

        deepTraverseTask.execute(graph(), null);
    }

    @Override
    public String toString() {
        return NAME;
     /*  double[] avg = getAvg();
        StringBuilder sb = new StringBuilder("[L-" + getLevel() + "]: ");
        if (avg != null) {
            for (int i = 0; i < avg.length; i++) {
                sb.append(avg[i]).append(" ");
            }
            sb.append(", total: ").append(getTotal());
        }
        return sb.toString();*/
    }


    private void internallearn(final double[] values, final int width, final double compressionFactor, final int compressionIter, final double[] precisions, double threshold, final boolean createNode) {
        int features = values.length;

        boolean reccursive = false;
        //manage total
        int total = getTotal();
        int level = getLevel();

        //Create dirac
        if (total == 0) {
            double[] sum = new double[features];
            System.arraycopy(values, 0, sum, 0, features);
            total = 1;

            //set total, weight, sum, return
            set(INTERNAL_TOTAL_KEY, Type.INT, total);
            set(INTERNAL_SUM_KEY, Type.DOUBLE_ARRAY, sum);
        } else {
            double[] sum;
            double[] min;
            double[] max;
            double[] sumsquares;

            //Upgrade dirac to gaussian
            if (total == 1) {
                //Create getMin, getMax, sumsquares
                sum = (double[]) super.get(INTERNAL_SUM_KEY);
                min = new double[features];
                max = new double[features];
                System.arraycopy(sum, 0, min, 0, features);
                System.arraycopy(sum, 0, max, 0, features);

                sumsquares = new double[features * (features + 1) / 2];
                int count = 0;
                for (int i = 0; i < features; i++) {
                    for (int j = i; j < features; j++) {
                        sumsquares[count] = sum[i] * sum[j];
                        count++;
                    }
                }
                //Self clone to create a sublevel
                if (createNode && level > 0) {
                    GaussianMixtureNode newLev = createLevel(sum, level - 1, width, compressionFactor, compressionIter, precisions, threshold);
                    double d = distance(values, sum, precisions);
                    if (d < threshold) {
                        reccursive = true;
                        newLev.internallearn(values, width, compressionFactor, compressionIter, precisions, threshold, createNode);
                    }
                    newLev.free();
                }
            }
            //Otherwise, get previously stored values
            else {
                sum = (double[]) super.get(INTERNAL_SUM_KEY);
                min = (double[]) super.get(INTERNAL_MIN_KEY);
                max = (double[]) super.get(INTERNAL_MAX_KEY);
                sumsquares = (double[]) super.get(INTERNAL_SUMSQUARE_KEY);
            }

            //Update the values
            for (int i = 0; i < features; i++) {
                if (values[i] < min[i]) {
                    min[i] = values[i];
                }

                if (values[i] > max[i]) {
                    max[i] = values[i];
                }
                sum[i] += values[i];
            }

            int count = 0;
            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    sumsquares[count] += values[i] * values[j];
                    count++;
                }
            }
            total++;
            if (createNode && level > 0 && !reccursive) {
                GaussianMixtureNode newLev = createLevel(values, level - 1, width, compressionFactor, compressionIter, precisions, threshold);
                newLev.free();
                checkAndCompress(width, compressionFactor, compressionIter, precisions, threshold);
            }
            //Store everything
            set(INTERNAL_TOTAL_KEY, Type.INT, total);
            set(INTERNAL_SUM_KEY, Type.DOUBLE_ARRAY, sum);
            set(INTERNAL_MIN_KEY, Type.DOUBLE_ARRAY, min);
            set(INTERNAL_MAX_KEY, Type.DOUBLE_ARRAY, max);
            set(INTERNAL_SUMSQUARE_KEY, Type.DOUBLE_ARRAY, sumsquares);
        }
    }

    public int getNumberOfFeatures() {
        int total = getTotal();
        if (total == 0) {
            return 0;
        } else {
            double[] sum = (double[]) super.get(INTERNAL_SUM_KEY);
            return sum.length;
        }
    }

    public double[] getSum() {
        int total = getTotal();
        if (total == 0) {
            return null;
        } else {
            return (double[]) super.get(INTERNAL_SUM_KEY);
        }
    }

    public double[] getSumSquares() {
        int total = getTotal();
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            double[] sum = (double[]) super.get(INTERNAL_SUM_KEY);

            int features = sum.length;
            double[] sumsquares = new double[features * (features + 1) / 2];
            int count = 0;
            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    sumsquares[count] = sum[i] * sum[j];
                    count++;
                }
            }
            return sumsquares;
        } else {
            return (double[]) super.get(INTERNAL_SUMSQUARE_KEY);
        }
    }

    public double getProbability(double[] featArray, double[] err, boolean normalizeOnAvg) {
        double[] sum = (double[]) super.get(INTERNAL_SUM_KEY);
        double[] sumsquares = (double[]) super.get(INTERNAL_SUMSQUARE_KEY);
        MultivariateNormalDistribution mnd = MultivariateNormalDistribution.getDistribution(sum, sumsquares, getTotal(), false);
        if (mnd == null) {
            //todo handle dirac to be replaced later
            return 0;
        } else {
            return mnd.density(featArray, normalizeOnAvg);
        }
    }

    public double[] getProbabilityArray(double[][] featArray, double[] err, boolean normalizeOnAvg) {
        double[] res = new double[featArray.length];

        double[] sum = (double[]) super.get(INTERNAL_SUM_KEY);
        double[] sumsquares = (double[]) super.get(INTERNAL_SUMSQUARE_KEY);
        MultivariateNormalDistribution mnd = MultivariateNormalDistribution.getDistribution(sum, sumsquares, getTotal(), false);

        if (mnd == null) {
            //todo handle dirac to be replaced later
            return res;
        } else {
            for (int i = 0; i < res.length; i++) {
                res[i] = mnd.density(featArray[i], normalizeOnAvg);
            }
            return res;
        }

    }

    public int getTotal() {
        Integer x = (Integer) super.get(INTERNAL_TOTAL_KEY);
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
            return (double[]) super.get(INTERNAL_SUM_KEY);
        } else {
            double[] avg = (double[]) super.get(INTERNAL_SUM_KEY);
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
            double[] sumsquares = (double[]) super.get(INTERNAL_SUMSQUARE_KEY);

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


    public Matrix getCovariance(double[] avg, double[] err) {
        int features = avg.length;

        int total = getTotal();
        if (total == 0) {
            return null;
        }
        if (err == null) {
            err = new double[avg.length];
        }
        if (total > 1) {
            double[] covariances = new double[features * features];
            double[] sumsquares = (double[]) super.get(INTERNAL_SUMSQUARE_KEY);

            double correction = total;
            correction = correction / (total - 1);

            int count = 0;
            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    covariances[i * features + j] = (sumsquares[count] / total - avg[i] * avg[j]) * correction;
                    covariances[j * features + i] = covariances[i * features + j];
                    count++;
                    if (covariances[i * features + i] < err[i]) {
                        covariances[i * features + i] = err[i];
                    }
                }
            }
            return VolatileMatrix.wrap(covariances, features, features);
        } else {
            return null;
        }
    }

    public double[] getMin() {
        int total = getTotal();
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            double[] min = (double[]) super.get(INTERNAL_SUM_KEY);
            return min;
        } else {
            double[] min = (double[]) super.get(INTERNAL_MIN_KEY);
            return min;
        }
    }

    public double[] getMax() {
        int total = getTotal();
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            double[] max = (double[]) super.get(INTERNAL_SUM_KEY);
            return max;
        } else {
            double[] max = (double[]) super.get(INTERNAL_MAX_KEY);
            return max;
        }
    }

    public long[] getSubGraph() {
        Relation res = (Relation) super.get(INTERNAL_SUBGAUSSIAN);
        if (res == null) {
            return null;
        }
        long[] reslong = new long[res.size()];
        for (int i = 0; i < res.size(); i++) {
            reslong[i] = res.get(i);
        }
        return reslong;
    }


    public static double distance(double[] features, double[] avg, double[] precisions) {
        double max = 0;
        double temp;
        for (int i = 0; i < features.length; i++) {
            temp = (features[i] - avg[i]) * (features[i] - avg[i]) / precisions[i];
            if (temp > max) {
                max = temp;
            }
        }
        return Math.sqrt(max);
    }


    /**
     * @ignore ts
     */
    public void predictValue(double[] temp, int[] pos, int level, Callback<double[]> callback) {
        if (callback != null) {
//            double[] values = new double[temp.length];
//            System.arraycopy(temp, 0, values, 0, temp.length);

            final NodeState resolved = this._resolver.resolveState(this);
            double[] initialPrecision = (double[]) resolved.getFromKey(PRECISION);
            if (initialPrecision == null) {
                initialPrecision = new double[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    initialPrecision[i] = 1;
                }
            }
            final double[] err = initialPrecision;
            double[] min = getMin();
            double[] max = getMax();


            double[] minsearch = new double[temp.length];
            double[] maxsearch = new double[temp.length];

            for (int i = 0; i < temp.length; i++) {
                minsearch[i] = temp[i] - Math.sqrt(err[i]);
                maxsearch[i] = temp[i] + Math.sqrt(err[i]);
            }

            for (int i = 0; i < pos.length; i++) {
                minsearch[pos[i]] = min[pos[i]];
                maxsearch[pos[i]] = max[pos[i]];
            }

            query(level, minsearch, maxsearch, new Callback<ProbaDistribution>() {
                @Override
                public void on(ProbaDistribution probabilities) {
                    ProbaDistribution2 newCalc = new ProbaDistribution2(probabilities.total, probabilities.distributions, probabilities.global);
                    double[] best = new double[temp.length];
                    System.arraycopy(temp, 0, best, 0, temp.length);
                    if (probabilities.distributions.length == 0) {
                        double[] avg = getAvg();
                        for (int i = 0; i < pos.length; i++) {
                            best[pos[i]] = avg[i];
                        }
                    } else {
                        NDimentionalArray temp = newCalc.calculate(minsearch, maxsearch, err, err, null);
                        best[pos[0]] = temp.getBestPrediction(pos[0]);
                    }
                    callback.on(best);

                }
            });


        }
    }
}
