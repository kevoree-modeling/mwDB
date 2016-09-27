package org.mwg.ml.neuralnet;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.LongLongMap;
import org.mwg.struct.Relationship;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by assaad on 20/09/16.
 */
public class NeuralNode extends AbstractNode {
    public static String NAME = "NeuralNode";

    public static String INPUTS = "inputs"; //Input relationships
    public static String INPUTS_MAP = "inputs_map"; //order of the relationships

    public static String OUTPUTS = "outputs"; //output relationships
    public static String OUTPUTS_MAP = "outputs_map"; //order of the relationships

    private static String WEIGHTS = "weights"; //weights of the network
    private static String NODE_TYPE = "node_type";

    public NeuralNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }


    public NeuralNode configure(int inputs, int outputs, int hiddenlayers, int nodesPerLayer) {

        ArrayList<NeuralNode> internalNodes = new ArrayList<NeuralNode>();//inputs + outputs + hiddenlayers * nodesPerLayer + 1
        internalNodes.add(this);
        int layer = 0;

        ArrayList<NeuralNode> previousLayer = new ArrayList<NeuralNode>();

        //create input layers:
        for (int i = 0; i < inputs; i++) {
            NeuralNode inputNode = createNewNode(NeuralNodeType.INPUT);
            this.forwardConnect(inputNode);
            internalNodes.add(inputNode);
            previousLayer.add(inputNode);
        }

        ArrayList<NeuralNode> nextLayer = new ArrayList<NeuralNode>();
        layer++;


        //Create hidden layers
        for (int i = 0; i < hiddenlayers; i++) {
            for (int j = 0; j < nodesPerLayer; j++) {
                NeuralNode hidden = createNewNode(NeuralNodeType.HIDDEN);
                nextLayer.add(hidden);
                internalNodes.add(hidden);

                for (int k = 0; k < previousLayer.size(); k++) {
                    previousLayer.get(k).forwardConnect(hidden);
                }
            }

            previousLayer = nextLayer;
            nextLayer = new ArrayList<NeuralNode>();
            layer++;
        }

        //Create output layers
        for (int i = 0; i < outputs; i++) {
            NeuralNode output = createNewNode(NeuralNodeType.OUTPUT);
            for (int k = 0; k < previousLayer.size(); k++) {
                previousLayer.get(k).forwardConnect(output);
            }
            output.forwardConnect(this);
            internalNodes.add(output);
        }

        for (int i = 0; i < internalNodes.size(); i++) {
            internalNodes.get(i).initWeightsRadomly(0.1);
            internalNodes.get(i).free();
        }
        return this;
    }

    private static Random random=new Random();

    private void initWeightsRadomly(double maxValue){
        NodeState state =unphasedState();
        int type = state.getFromKeyWithDefault(NODE_TYPE, NeuralNodeType.HIDDEN);
        if(type== NeuralNodeType.HIDDEN|| type==NeuralNodeType.OUTPUT){
            Relationship input= (Relationship) state.getFromKey(INPUTS);
            double[] weights = new double[input.size()+1];
            for(int i=0;i<weights.length;i++){
                weights[i]= random.nextDouble()*maxValue;
            }
            state.setFromKey(WEIGHTS,Type.DOUBLE_ARRAY,weights);
        }
    }

    private void forwardConnect(NeuralNode inputNode) {
        Relationship outputs = getOrCreateRel(OUTPUTS);
        outputs.add(inputNode.id());
        int pos = outputs.size() - 1;
        LongLongMap map = (LongLongMap) getOrCreate(OUTPUTS_MAP, Type.LONG_TO_LONG_MAP);
        map.put(inputNode.id(), pos);


        Relationship inputs = inputNode.getOrCreateRel(INPUTS);
        inputs.add(this.id());
        int posint = inputs.size() - 1;
        LongLongMap mapin = (LongLongMap) inputNode.getOrCreate(INPUTS_MAP, Type.LONG_TO_LONG_MAP);
        mapin.put(id(),posint);
    }

    private NeuralNode createNewNode(int neuralNodeType) {
        NeuralNode temp = (NeuralNode) graph().newTypedNode(world(), time(), NAME);
        temp.setProperty(NODE_TYPE, Type.INT, neuralNodeType);
        return temp;
    }


    // todo to be replaced in more generic way after
    private static double integrationFct(double[] buffer, double[] weights) {
        double value = 0;

        //todo to test with matrix mult later
        for (int i = 0; i < weights.length; i++) {
            value += weights[i] * buffer[i];
        }
        return value;
    }

    private static double activationFunction(double value, int type) {
        if (type == NeuralNodeType.HIDDEN) {
            return 1 / (1 + Math.exp(-value)); //Sigmoid by default, todo to be changed later to a generic activation
        } else {
            return value;
        }
    }

    private static double derivateActivationFunction(double fctVal, double value, int type) {
        if (type == NeuralNodeType.HIDDEN) {
            return fctVal * (1 - fctVal);
        } else {
            return 1;
        }
        // return fctVal * (1 - fctVal);

    }


    private static double calculateErr(double calculated, double target) {
        return (target - calculated) * (target - calculated) / 2;
    }

    private static double calculateDerivativeErr(double calculated, double target) {
        return -(target - calculated);
    }


    private void propagate(long senderId, NodeState state, long msgId, double msg, boolean forwardPropagation, boolean learn, double learningRate, Callback<double[]> callback) {
        Relationship outputRel = (Relationship) state.getFromKey(OUTPUTS);
        for (int i = 0; i < outputRel.size(); i++) {
            send(outputRel.get(i), msgId, msg, forwardPropagation, learn, learningRate, callback);
        }
    }


    //todo add time sensitivity
    public void receive(long senderId, long msgId, double msg, boolean forwardPropagation, boolean learn, double learningRate, Callback<double[]> callback) {
        NodeState state = unphasedState();
        int type = state.getFromKeyWithDefault(NODE_TYPE, NeuralNodeType.HIDDEN);

        if (forwardPropagation) {
            //forward propagation code here
            if (type == NeuralNodeType.INPUT) {
                //If it is an input node and in forward propagation setting, send to all connected output nodes
                propagate(id(), state, msgId, msg, forwardPropagation, learn, learningRate, callback);

            } else if (type == NeuralNodeType.HIDDEN || type == NeuralNodeType.OUTPUT) {
                LongLongMap inputmap = (LongLongMap) state.getFromKey(INPUTS_MAP);
                int pos = (int) inputmap.get(senderId);
                Buffer forwardBuffer = BufferManager.getBuffer(id(), (int) inputmap.size(), BufferManager.INPUT, true, false);
                double[] values = forwardBuffer.insert(msgId, pos, msg);
                if (values != null) {
                    double[] weights = (double[]) state.getFromKey(WEIGHTS);
                    double integration = integrationFct(values, weights);
                    double activation = activationFunction(integration, type);
                    Buffer integrationBuffer = BufferManager.getBuffer(id(), 1, BufferManager.INTEGRATION, false, false);
                    Buffer activationBuffer = BufferManager.getBuffer(id(), 1, BufferManager.ACTIVATION, false, false);
                    integrationBuffer.insert(msgId, 0, integration);
                    activationBuffer.insert(msgId, 0, activation);
                    propagate(id(), state, msgId, activation, forwardPropagation, learn, learningRate, callback);
                }
            } else if (type == NeuralNodeType.ROOT) {
                LongLongMap inputMap = (LongLongMap) state.getFromKey(INPUTS_MAP);
                int pos = (int) inputMap.get(senderId);
                Buffer forwardBuffer = BufferManager.getBuffer(id(), (int) inputMap.size(), BufferManager.INPUT, false, true);
                double[] values = forwardBuffer.insert(msgId, pos, msg);
                if (learn) {
                    Buffer outputBuffer = BufferManager.getBuffer(id(), (int) inputMap.size(), BufferManager.OUTPUT, false, false);
                    double[] realoutputs = outputBuffer.getArray(msgId, false);
                    double err = calculateErr(msg, realoutputs[pos]);
                    Buffer integrationBuffer = BufferManager.getBuffer(id(), realoutputs.length, BufferManager.INTEGRATION, false, true);

                    double[] errors = integrationBuffer.insert(msgId, pos, err);
                    if (errors != null) {
                        outputBuffer.removeFromBuffer(msgId);
//                        double total=0;
//                        for(int i=0;i<errors.length;i++){
//                            total+=errors[i];
//                        }
//                        System.out.println("total error: "+total);
                    }

                    //Initiate backpropagation:
                    send(senderId, msgId, calculateDerivativeErr(msg, realoutputs[pos]), false, learn, learningRate, callback);
                } else {
                    if (values != null) {
                        if (callback != null) {
                            callback.on(values);
                        }
                    }
                }


            }
        } else {
            //backward propagation code here
            if (type == NeuralNodeType.INPUT || type == NeuralNodeType.ROOT) {

                LongLongMap outputMap = (LongLongMap) state.getFromKey(OUTPUTS_MAP);
                int pos = (int) outputMap.get(senderId);
                Buffer backwardBuffer = BufferManager.getBuffer(id(), (int) outputMap.size(), BufferManager.OUTPUT, false, true);
                double[] values = backwardBuffer.insert(msgId, pos, msg);
                if (values != null) {
                    if (type == NeuralNodeType.INPUT) {
                        Relationship inputRel = (Relationship) state.getFromKey(INPUTS);
                        for (int i = 0; i < inputRel.size(); i++) {
                            send(inputRel.get(i), msgId, msg, forwardPropagation, learn, learningRate, callback);
                        }
                    }
                    if (type == NeuralNodeType.ROOT) {
                        if (callback != null) {
                            callback.on(new double[]{(double) msgId}); //done learning for this
                        }
                    }

                }


            } else if (type == NeuralNodeType.OUTPUT || type == NeuralNodeType.HIDDEN) {

                LongLongMap outputMap = (LongLongMap) state.getFromKey(OUTPUTS_MAP);
                LongLongMap inputMap = (LongLongMap) state.getFromKey(INPUTS_MAP);
                int pos = (int) outputMap.get(senderId);
                Buffer backwardBuffer = BufferManager.getBuffer(id(), (int) outputMap.size(), BufferManager.OUTPUT, false, true);
                double[] values = backwardBuffer.insert(msgId, pos, msg);
                if (values != null) {
                    double[] weights = (double[]) state.getFromKey(WEIGHTS);
                    Buffer integrationBuffer = BufferManager.getBuffer(id(), 1, BufferManager.INTEGRATION, false, false);
                    Buffer activationBuffer = BufferManager.getBuffer(id(), 1, BufferManager.ACTIVATION, false, false);
                    Buffer forwardBuffer = BufferManager.getBuffer(id(),  (int) inputMap.size(), BufferManager.INPUT, true, false);

                    double[] integration = integrationBuffer.getArray(msgId, true);
                    double[] activation = activationBuffer.getArray(msgId, true);
                    double[] forward = forwardBuffer.getArray(msgId, true);

                    double delta = integrationFct(values, weights);
                    delta = delta * derivateActivationFunction(activation[0], integration[0], type);
                    double[] newWeight = new double[weights.length];

                    for (int i = 0; i < weights.length - 1; i++) {
                        newWeight[i] = weights[i] - learningRate * delta * forward[i];
                        //  System.out.println("output "+id+": "+newWeight[i]);
                    }
                    newWeight[weights.length - 1] = weights[weights.length - 1] - learningRate * delta;//update bias
                    //  System.out.println("output "+id+": "+newWeight[newWeight.length - 1]);

                    Relationship inputs = (Relationship) state.getFromKey(INPUTS);

                    for (int i = 0; i < inputs.size(); i++) {
                        send(inputs.get(i), msgId, delta * weights[i], forwardPropagation, learn, learningRate, callback);
                    }
                    state.setFromKey(WEIGHTS, Type.DOUBLE_ARRAY, newWeight);
                }

            }
        }
    }


    public void send(final long outputid, final long msgId, final double msg, final boolean forwardPropagation, final boolean learn, final double learningRate, final Callback<double[]> callback) {
        this.graph().lookup(world(), time(), outputid, new Callback<Node>() {
            @Override
            public void on(Node result) {
                NeuralNode res = (NeuralNode) result;
                res.receive(id(), msgId, msg, forwardPropagation, learn, learningRate, callback);
                res.free();
            }
        });


    }


}
