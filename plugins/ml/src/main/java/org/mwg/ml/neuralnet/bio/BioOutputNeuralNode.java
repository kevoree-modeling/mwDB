/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.ml.neuralnet.bio;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.ml.common.matrix.MatrixOps;
import org.mwg.struct.DMatrix;
import org.mwg.struct.LongLongMap;

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
        final DMatrix spikeSum = (DMatrix) get(BioNeuralNetwork.BUFFER_SPIKE_SUM);
        final DMatrix spikeNb = (DMatrix) get(BioNeuralNetwork.BUFFER_SPIKE_NB);
        final DMatrix weights = (DMatrix) get(BioNeuralNetwork.WEIGHTS);
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
