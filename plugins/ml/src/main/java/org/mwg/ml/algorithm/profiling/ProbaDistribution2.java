package org.mwg.ml.algorithm.profiling;

import org.mwg.ml.ProgressReporter;
import org.mwg.ml.common.NDimentionalArray;
import org.mwg.ml.common.matrix.operation.MultivariateNormalDistribution;


/**
 * @ignore ts
 */
public class ProbaDistribution2 {

    public MultivariateNormalDistribution[] distributions;
    public int total[];
    public int global;

    public ProbaDistribution2(int total[], MultivariateNormalDistribution[] distributions, int global) {
        this.total = total;
        this.distributions = distributions;
        this.global = global;
    }

    public NDimentionalArray calculate(double[] min, double[] max, double[] resolution, double[] err, ProgressReporter reporter) {
        if (reporter != null) {
            reporter.updateInformation("Number of distributions: " + distributions.length + " , values: " + global);
        }
        NDimentionalArray result = new NDimentionalArray(min, max, resolution);
        int percent;
        double weight;

        double[] sqrerr = new double[err.length];
        for (int i = 0; i < err.length; i++) {
            sqrerr[i] = Math.sqrt(err[i]);
        }

        for (int i = 0; i < distributions.length; i++) {
            weight = total[i] * 1.0 / global;
            calculateOneDist(distributions[i], weight, min, max, sqrerr, result);
            if (reporter != null) {
                percent = (i + 1) * 100 / distributions.length;
                reporter.updateProgress(percent);
                if (reporter.isCancelled()) {
                    return null;
                }
            }
        }
        result.normalize();
        return result;
    }

    private void calculateOneDist(MultivariateNormalDistribution distribution, double weight, double[] min, double[] max, double[] sqrerr, NDimentionalArray result) {
        double[] seed = new double[min.length];
        System.arraycopy(min, 0, seed, 0, min.length);
        reccursiveCalc(distribution, weight, min, max, sqrerr, seed, result);
    }

    private void reccursiveCalc(MultivariateNormalDistribution distribution, double weight, double[] min, double[] max, double[] steps, double[] seed, NDimentionalArray result) {
        int level;
        do {
            result.add(seed, distribution.density(seed, false) * weight);
            level=0;
            while (level < min.length && seed[level] >= max[level]) {
                level++;
            }
            if (level != min.length) {
                seed[level] += steps[level];
                System.arraycopy(min, 0, seed, 0, level);
            }
        }
        while (level!= min.length);
    }


}
