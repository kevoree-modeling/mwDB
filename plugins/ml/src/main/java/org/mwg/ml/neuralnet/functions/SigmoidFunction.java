package org.mwg.ml.neuralnet.functions;

import org.mwg.ml.neuralnet.NeuralFunction;

public class SigmoidFunction implements NeuralFunction {
    @Override
    public double activate(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    @Override
    public double derivate(double x, double fct) {
        return fct * (1 - fct);
    }
}
