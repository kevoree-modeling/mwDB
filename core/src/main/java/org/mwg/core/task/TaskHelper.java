package org.mwg.core.task;


import org.mwg.Node;
import org.mwg.base.BaseNode;

class TaskHelper {

    /**
     * Return an array with all nodes contains in the input.
     * If the strict mode is enable, the input should contain only Node element. Otherwise, the not Node element will
     * be ignore.
     *
     * @param toFLat input to flat
     * @param strict is the function throw an exception when a no node element is found
     * @return a node array (one dimension)
     */
    public static Node[] flatNodes(Object toFLat, boolean strict) {
        if(toFLat instanceof BaseNode) {
            return new Node[]{(Node) toFLat};
        }

        if(toFLat instanceof Object[]) {
            Object[] resAsArray = (Object[]) toFLat;
            Node[] nodes = new Node[0];
            for(int i=0;i<resAsArray.length;i++) {
                if(resAsArray[i] instanceof BaseNode) {
                    Node tmp[] = new Node[nodes.length + 1];
                    System.arraycopy(nodes,0,tmp,0,nodes.length);
                    tmp[nodes.length] = (BaseNode) resAsArray[i];
                    nodes = tmp;
                } else if(resAsArray[i] instanceof Object[]) {
                    Node[] innerNodes = flatNodes(resAsArray[i],strict);
                    Node[] tmp = new Node[nodes.length + innerNodes.length];
                    System.arraycopy(nodes,0,tmp,0,nodes.length);
                    System.arraycopy(innerNodes,0,tmp,nodes.length,innerNodes.length);
                    nodes = tmp;
                } else if(strict){
                    throw new RuntimeException("[ActionIndexOrUnindexNode] The array in result contains an element with wrong type. " +
                            "Expected type: BaseNode. Actual type: " + resAsArray[i]);
                }
            }
            return nodes;
        } else if(strict) {
            throw new RuntimeException("[ActionIndexOrUnindexNode] Wrong type of result. Expected type is BaseNode or an array of BaseNode." +
                    "Actual type is " + toFLat);
        }
        return new Node[0];
    }

    /**
     * @native ts
     * return parseInt(s);
     */
    public static int parseInt(String s) {
        return Integer.parseInt(s);
    }
}
