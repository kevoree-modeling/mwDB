package org.mwg.ml.neuralnet;

import java.util.HashMap;

/**
 * Created by assaad on 26/09/16.
 */
public class BufferManager {
    private static int CAPACITY = 10;

    public static int INPUT = 0;
    public static int OUTPUT = 1;
    public static int INTEGRATION = 2;
    public static int ACTIVATION = 3;


    private static HashMap<Long, Buffer> inputBuffers = new HashMap<>();
    private static HashMap<Long, Buffer> outputBuffers = new HashMap<>();
    private static HashMap<Long, Buffer> integrationBuffers = new HashMap<>();
    private static HashMap<Long, Buffer> activationBuffers = new HashMap<>();

    public static Buffer getBuffer(Long id, int dimension, int bufferType, boolean lastWeightIsOne, boolean clearAfterFull) {
        HashMap<Long, Buffer> map = null;
        if (bufferType == INPUT) {
            map = inputBuffers;
        } else if (bufferType == OUTPUT) {
            map = outputBuffers;
        } else if (bufferType == INTEGRATION) {
            map = integrationBuffers;
        } else if (bufferType == ACTIVATION) {
            map = activationBuffers;
        } else {
            throw new RuntimeException("Buffer not recognizable");
        }


        Buffer temp = map.get(id);
        if (temp != null) {
            return temp;
        } else {
            if (lastWeightIsOne) {
                temp = new Buffer(CAPACITY, dimension + 1, lastWeightIsOne, clearAfterFull);
            } else {
                temp = new Buffer(CAPACITY, dimension, lastWeightIsOne, clearAfterFull);
            }
            map.put(id, temp);
            return temp;
        }
    }


}
