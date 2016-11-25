package org.mwg.ml.neuralnet.bio;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.ml.common.matrix.MatrixOps;
import org.mwg.struct.LongLongMap;
import org.mwg.struct.Matrix;

import java.util.Random;

public class BioOutputNeuralNode extends BaseNode {

    public static String NAME = "BioOutputNeuralNode";

    public BioOutputNeuralNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    @Override
    public void init() {
        set(BioNeuralNetwork.BIAS, Type.DOUBLE, new Random().nextDouble() * 2 - 1);
    }

    //if buffer not full and total threshold < return absolute zero
    @SuppressWarnings("Duplicates")
    double learn(long sender, double value, int spikeLimit, double threshold) {
        //TODO atomic
        final Matrix spikeSum = (Matrix) get(BioNeuralNetwork.BUFFER_SPIKE_SUM);
        final Matrix spikeNb = (Matrix) get(BioNeuralNetwork.BUFFER_SPIKE_NB);
        final Matrix weights = (Matrix) get(BioNeuralNetwork.WEIGHTS);
        final LongLongMap reverse = (LongLongMap) get(BioNeuralNetwork.RELATION_INPUTS);
        final int senderIndex = (int) reverse.get(sender);
        //update neural content
        spikeSum.add(0, senderIndex, value);
        spikeNb.add(0, senderIndex, 1);
        //integrate all values
        double signal = MatrixOps.multiply(spikeSum, weights).get(0, 0);
        double bias = (double) get(BioNeuralNetwork.BIAS);
        double integrated_signal = signal + bias;
        //test if one spike limit is reached
        int maxSpikeNb = 0;
        for (int i = 0; i < spikeNb.columns(); i++) {
            int loopSpikeNb = (int) spikeNb.get(0, i);
            if (loopSpikeNb >= spikeLimit) {
                spikeSum.fill(0d);
                spikeNb.fill(0d);
                return integrated_signal;
            }
            if (loopSpikeNb > maxSpikeNb) {
                maxSpikeNb = loopSpikeNb;
            }
        }
        //activate capacitor effect
        /*
        if ((maxSpikeNb >= (spikeLimit / 2)) && (sigmoid > threshold)) {
            spikeSum.fill(0d);
            spikeNb.fill(0d);
            return sigmoid;
        }*/
        return 0d;
    }

}
