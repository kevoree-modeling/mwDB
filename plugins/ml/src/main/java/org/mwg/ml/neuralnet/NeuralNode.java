package org.mwg.ml.neuralnet;

import org.mwg.Graph;
import org.mwg.plugin.AbstractNode;

/**
 * Created by assaad on 20/09/16.
 */
public class NeuralNode extends AbstractNode {
    public static String NAME="NeuralNode";

    public static String INPUTS="inputs";
    public static String OUTPUTS="outputs";

    private static String WEIGHTS="weights";
    private static String INPUT_BUFFER="input_buffer";


    public NeuralNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }


    public void receive(long inputId ){

    }




}
