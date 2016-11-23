package org.mwg.structure;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.distance.GeoDistance;
import org.mwg.structure.tree.KDTree;

/**
 * Created by assaad on 09/09/16.
 */
public class TestDistance {
    //@Test
    public void test1() {

        double[] x = {49.6325588, 6.1691682};
        double[] y = {49.63203414528315, 6.170411109924317};
        double[] z = {49.630116182698124, 6.1684370040893555};
        double[] w = {49.62013599926226, 6.138868331909181};


        System.out.println(GeoDistance.instance().measure(x, y));
        System.out.println(GeoDistance.instance().measure(x, z));
        System.out.println(GeoDistance.instance().measure(x, w));
    }

    @Test
    public void test2() {

        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                //.withScheduler(new NoopScheduler())
                .withMemorySize(100000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                double[] x = {49.6325588, 6.1691682};
                double[] y = {49.63203414528315, 6.170411109924317};
                double[] z = {49.630116182698124, 6.1684370040893555};
                double[] w = {49.62013599926226, 6.138868331909181};

                Node valy = graph.newNode(0, 0);
                Node valz = graph.newNode(0, 0);
                Node valw = graph.newNode(0, 0);

                valy.set("key", Type.DOUBLE_ARRAY, y);
                valz.set("key", Type.DOUBLE_ARRAY, z);
                valw.set("key", Type.DOUBLE_ARRAY, w);


                KDTree testTask = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);
                testTask.set(KDTree.DISTANCE_THRESHOLD, Type.DOUBLE, 1e-30);
                testTask.set(KDTree.DISTANCE, Type.INT, Distances.GEODISTANCE);

                testTask.insertWith(y, valy, null);
                testTask.insertWith(z, valz, null);
                testTask.insertWith(w, valw, null);

//                System.out.println(GeoDistance.instance().measure(x,y));
//                System.out.println(GeoDistance.instance().measure(x,z));
//                System.out.println(GeoDistance.instance().measure(x,w));


//                System.out.println(" ");
                testTask.nearestNWithinRadius(x, 10, 200, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        Assert.assertTrue(result.length == 1);

//                        for(int i=0;i<result.length;i++){
//                            System.out.println("At 200m: "+result[i]+ " distance: "+GeoDistance.instance().measure(x,(double[])result[i].get("key")));
//                        }
                    }
                });
//                System.out.println(" ");


                testTask.nearestNWithinRadius(x, 10, 300, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        Assert.assertTrue(result.length == 2);

//                        for(int i=0;i<result.length;i++){
//                            System.out.println("At 300m: "+result[i]+ " distance: "+GeoDistance.instance().measure(x,(double[])result[i].get("key")));
//                        }
                    }
                });
//                System.out.println(" ");


                testTask.nearestNWithinRadius(x, 10, 5000, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        Assert.assertTrue(result.length == 3);

//                        for(int i=0;i<result.length;i++){
//                            System.out.println("At 5000m: "+result[i]+ " distance: "+GeoDistance.instance().measure(x,(double[])result[i].get("key")));
//                        }
                    }
                });


            }
        });
    }


}


