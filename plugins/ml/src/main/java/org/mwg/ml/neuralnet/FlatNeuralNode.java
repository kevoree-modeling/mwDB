package org.mwg.ml.neuralnet;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.ml.common.matrix.MatrixOps;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.plugin.NodeState;
import org.mwg.struct.Matrix;

public class FlatNeuralNode extends BaseNode {

    //Inputs are organized in one row

    public static String NAME = "FlatNeuralNode";
    private static final long NB_INPUTS = 1;
    private static final long NB_OUTPUTS = 2;
    private static final long NB_LAYERS = 3;
    private static final long NB_PER_LAYER = 4;
    private static final long LEARNING_RATE = 5;

    private static final long MATRICES_OFFSET = 10; // matrix offset


    private static Matrix layerWeights(NodeState state, int layer) {
        return (Matrix) state.getOrCreate(MATRICES_OFFSET + layer * 2, Type.MATRIX);
    }

    private static Matrix layerBias(NodeState state, int layer) {
        return (Matrix) state.getOrCreate(MATRICES_OFFSET + layer * 2 + 1, Type.MATRIX);
    }


    public FlatNeuralNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    //4,2,2,3
    public FlatNeuralNode configure(int inputs, int outputs, int hiddenlayers, int nodesPerLayer, double learningRate) {
        NodeState state = phasedState();
        state.set(NB_INPUTS, Type.INT, inputs);
        state.set(NB_OUTPUTS, Type.INT, outputs);
        state.set(NB_LAYERS, Type.INT, hiddenlayers);
        state.set(NB_PER_LAYER, Type.INT, nodesPerLayer);
        state.set(LEARNING_RATE, Type.DOUBLE, learningRate);


        int previousDim = inputs;
        int nextDim = nodesPerLayer;


        for (int i = 0; i < hiddenlayers; i++) {

            Matrix weights = layerWeights(state, i);
            Matrix biases = layerBias(state, i);
            weights.init(previousDim, nextDim);
            biases.init(1, nextDim);
            weights.fillWithRandom(-1.0, 1.0, System.currentTimeMillis());
            biases.fillWithRandom(-1.0, 1.0, System.currentTimeMillis());

            //initialize randomly here

            previousDim = nodesPerLayer;
        }
        //create output weight and bias after hidden layers index
        nextDim = outputs;
        Matrix weights = layerWeights(state, hiddenlayers);
        Matrix biases = layerBias(state, hiddenlayers);
        weights.init(previousDim, nextDim);
        biases.init(1, nextDim);
        weights.fillWithRandom(-1.0, 1.0, System.currentTimeMillis());
        biases.fillWithRandom(-1.0, 1.0, System.currentTimeMillis());

        return this;
    }


    public double[] predict(final double[] inputVec) {
        NodeState state = phasedState();

        int nbInput = (int) state.get(NB_INPUTS);

        if (inputVec.length != nbInput) {
            throw new RuntimeException("Please reconfigure the neuralnet before changing input or output dimensions");
        }

        int nbHiddenLayers = (int) state.get(NB_LAYERS);

        Matrix integration;
        Matrix activation;


        Matrix input = VolatileMatrix.empty(1, nbInput);
        Matrix weights;
        Matrix biases;

        //set initial input vector as a matrix
        for (int i = 0; i < nbInput; i++) {
            input.set(0, i, inputVec[i]);
        }

        //Start the feedforward round

        for (int layer = 0; layer < nbHiddenLayers + 1; layer++) {
            weights = layerWeights(state, layer);
            biases = layerBias(state, layer);
            integration = MatrixOps.add(MatrixOps.multiply(input, weights), biases);
            activation = activate(integration, layer == nbHiddenLayers);
            // Input for the next round
            input = activation;
        }
        return input.data();
    }

    public void learn(final double[] inputVec, final double[] outputVec) {
        NodeState state = phasedState();

        int nbInput = (int) state.get(NB_INPUTS);
        int nbOutput = (int) state.get(NB_OUTPUTS);

        if (inputVec.length != nbInput || outputVec.length != nbOutput) {
            throw new RuntimeException("Please reconfigure the neuralnet before changing input or output dimensions");
        }

        int nbHiddenLayers = (int) state.get(NB_LAYERS);
        double learningRate = (double) state.get(LEARNING_RATE);

        Matrix[] forwards = new Matrix[nbHiddenLayers + 1];
        Matrix[] integrations = new Matrix[nbHiddenLayers + 1];
        Matrix[] activations = new Matrix[nbHiddenLayers + 1];
        Matrix[] derivations = new Matrix[nbHiddenLayers + 1];


        Matrix input = VolatileMatrix.empty(1, nbInput);
        Matrix[] weights = new Matrix[nbHiddenLayers + 1];
        Matrix[] biases = new Matrix[nbHiddenLayers + 1];

        //set initial input vector as a matrix
        for (int i = 0; i < nbInput; i++) {
            input.set(0, i, inputVec[i]);
        }


        //Start the feedforward round

        for (int layer = 0; layer < nbHiddenLayers + 1; layer++) {
            forwards[layer] = input;
            weights[layer] = layerWeights(state, layer);
            biases[layer] = layerBias(state, layer);
            integrations[layer] = MatrixOps.add(MatrixOps.multiply(input, weights[layer]), biases[layer]);
            activations[layer] = activate(integrations[layer], layer == nbHiddenLayers);
            derivations[layer] = derivate(integrations[layer], activations[layer], layer == nbHiddenLayers);
            // Input for the next round
            input = activations[layer];
        }

        // Calculate error.
        double[] calculated = input.data();
        double[] derivativeErr = new double[calculated.length];
        for (int i = 0; i < calculated.length; i++) {
            derivativeErr[i] = -(outputVec[i] - calculated[i]);
        }


        Matrix[] newWeights = new Matrix[nbHiddenLayers + 1];
        Matrix[] newBiases = new Matrix[nbHiddenLayers + 1];
        double[] previousErr;

        //Back-propagate

        for (int layer = nbHiddenLayers; layer >= 0; layer--) {
            newWeights[layer] = VolatileMatrix.empty(weights[layer].rows(), weights[layer].columns());
            newBiases[layer] = VolatileMatrix.empty(biases[layer].rows(), biases[layer].columns());
            for (int i = 0; i < derivativeErr.length; i++) {
                derivativeErr[i] = derivativeErr[i] * derivations[layer].get(0, i);
            }
            if (layer > 0) {
                previousErr = new double[derivations[layer - 1].columns()];
            } else {
                previousErr = null;
            }


            for (int column = 0; column < newWeights[layer].columns(); column++) {
                for (int row = 0; row < newWeights[layer].rows(); row++) {
                    newWeights[layer].set(row, column, weights[layer].get(row, column) - learningRate * derivativeErr[column] * forwards[layer].get(0, row));
                    if (layer > 0) {
                        previousErr[row] += derivativeErr[column] * weights[layer].get(row, column);
                    }
                }
                //update bias of column
                newBiases[layer].set(0, column, biases[layer].get(0, column) - learningRate * derivativeErr[column]);
            }
            derivativeErr = previousErr;
            MatrixOps.copyMatrix(newWeights[layer], weights[layer]);
            MatrixOps.copyMatrix(newBiases[layer], biases[layer]);
        }
    }


    private Matrix activate(Matrix integration, boolean linearActivation) {
        if (linearActivation) {
            return integration; // for output returns a linear activation
        } else {
            Matrix result = VolatileMatrix.empty(integration.rows(), integration.columns());
            for (int i = 0; i < integration.rows(); i++) {
                for (int j = 0; j < integration.columns(); j++) {
                    result.set(i, j, 1 / (1 + Math.exp(-integration.get(i, j)))); //else a sigmoid
                }
            }
            return result;
        }
    }

    private Matrix derivate(Matrix integration, Matrix activation, boolean linearActivation) {
        Matrix result = VolatileMatrix.empty(1, activation.columns());
        if (linearActivation) {
            for (int j = 0; j < activation.columns(); j++) {
                result.set(0, j, 1);
            }
        } else {
            for (int j = 0; j < activation.columns(); j++) {
                result.set(0, j, activation.get(0, j) * (1 - activation.get(0, j))); //else a sigmoid
            }
        }
        return result;
    }

}
