package org.mwg.struct;

public interface Matrix {

    Matrix init(int rows, int columns);

    Matrix fill(double value);

    Matrix fillWith(double[] values);

    Matrix fillWithRandom(double min, double max, long seed);

    int rows();

    int columns();

    double get(int rowIndex, int columnIndex);

    Matrix set(int rowIndex, int columnIndex, double value);

    Matrix add(int rowIndex, int columnIndex, double value);

    double[] data();

    int leadingDimension();

    double unsafeGet(int index);

    Matrix unsafeSet(int index, double value);




}
