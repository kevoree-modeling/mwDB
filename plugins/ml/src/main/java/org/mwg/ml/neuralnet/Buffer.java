package org.mwg.ml.neuralnet;

import java.util.HashMap;

/**
 * Created by assaad on 26/09/16.
 */
public class Buffer {
    private double[][] _values;
    private int[] _counts;
    private HashMap<Long, Integer> _index;
    private int _dim;
    private int _capacityFull = 0;
    private int _capacity;
    private boolean _lastWeightIsOne;
    private boolean _clearAfterFull;


    public Buffer(int capacity, int dimension, boolean lastWeightIsOne, boolean clearAfterFull) {
        this._index = new HashMap<>(); // capacity
        this._counts = new int[capacity];
        this._values = new double[capacity][_dim];
        this._dim = dimension;
        this._capacity = capacity;
        this._lastWeightIsOne=lastWeightIsOne;
        this._clearAfterFull=clearAfterFull;
    }


    private double[] insertInside(int indexpos, int position, long msgId, double value) {
        double[] v = _values[indexpos];
        v[position] = value;
        _counts[indexpos]++;
        if (_counts[indexpos] == _dim) {
            if(_clearAfterFull) {
                clearBuffer(indexpos, msgId);
            }
            return v;
        } else {
            return null;
        }
    }

    public void removeFromBuffer(long msgId){
        Integer indexpos = _index.get(msgId);
        if (indexpos != null) {
            clearBuffer(indexpos,msgId);
        }
    }

    private void clearBuffer(int id, long msgId){
        _values[id]=new double[_dim];
        _counts[id]=0;
        _index.remove(msgId);
        _capacityFull--;
    }


    public double[] getArray(Long msgId, boolean clear){
        Integer indexpos = _index.get(msgId);
        if (indexpos != null) {
            double[] v=_values[indexpos];
            if (clear) {
                clearBuffer(indexpos, msgId);
                return v;
            }
            else{
                return v;
            }
        }
        else{
            return null;
        }
    }


    public void insertArray(Long msgId, double[] values){
        Integer indexpos = _index.get(msgId);
        if (indexpos != null) {
            System.arraycopy(values,0,_values[indexpos],0,values.length);
            _counts[indexpos]=values.length;

        } else {
            if (_capacityFull == _capacity) {
                throw new RuntimeException("Buffer is full");
            } else {
                int pos = -1;
                for (int i = 0; i < _capacity; i++) {
                    if (_counts[i] == 0) {
                        pos = i;
                        break;
                    }
                }
                _index.put(msgId, pos);
                System.arraycopy(values,0,_values[pos],0,values.length);
                _counts[pos]=values.length;
                _capacityFull++;
            }
        }
    }


    //return null if the buffer is not full and the double[] otherwise
    public double[] insert(Long msgId, int position, double value) {
        Integer indexpos = _index.get(msgId);
        if (indexpos != null) {
            return insertInside(indexpos, position, msgId, value);
        } else {
            if (_capacityFull == _capacity) {
                throw new RuntimeException("Buffer is full");
            } else {
                int pos = -1;
                for (int i = 0; i < _capacity; i++) {
                    if (_counts[i] == 0) {
                        pos = i;
                        break;
                    }
                }
                _index.put(msgId, pos);
                if (_lastWeightIsOne) {
                    _counts[pos] = 1;
                    _values[pos][_dim - 1] = 1;
                }
                return insertInside(pos, position, msgId, value);
            }
        }
    }

}
