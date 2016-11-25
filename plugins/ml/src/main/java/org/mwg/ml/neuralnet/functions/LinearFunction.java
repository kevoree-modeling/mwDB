package org.mwg.ml.neuralnet.functions;

import org.mwg.ml.neuralnet.NeuralFunction;

public class LinearFunction implements NeuralFunction{
    @Override
    public double activate(double x) {
        return x;
    }

    @Override
    public double derivate(double x, double fct) {
        return 1;
    }
}
