package org.mwg.structure;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.structure.action.NTreeInsertTo;
import org.mwg.structure.action.NTreeNearestN;
import org.mwg.structure.action.NTreeNearestNWithinRadius;
import org.mwg.structure.action.NTreeNearestWithinRadius;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.tree.KDTree;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import static org.mwg.task.Actions.*;

public class GeoIndexTaskTest {

    /**
     * {@native ts
     * return inS.indexOf(inS2) !== -1;
     * }
     */
    private static boolean stringContains(String inS, String inS2) {
        return inS.contains(inS2);
    }

    @Test
    public void test() {
        Graph g = new GraphBuilder().withMemorySize(100000).withPlugin(new StructurePlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                Task createGeoIndex = newTypedNode(KDTree.NAME)
                        .setProperty(KDTree.DISTANCE, Type.INT, Distances.GEODISTANCE + "")
                        .setProperty(KDTree.FROM, Type.STRING, "lat,long")
                        .asGlobalVar("geoIndex");

                Task createTenPoints = loop("0", "9", newNode().setProperty("lat", Type.DOUBLE, "49.{{i}}").setProperty("long", Type.DOUBLE, "6.{{i}}").addToGlobalVar("points"));

                newTask()
                        .subTask(createGeoIndex)
                        .subTask(createTenPoints)
                        .fromVar("points")
                        .action(NTreeInsertTo.NAME, "geoIndex")
/*
                        .fromVar("geoIndex")
                        .whileDo((new TaskFunctionConditional() {
                            @Override
                            public boolean eval(TaskContext context) {
                                return context.result().size() > 0;
                            }
                        }), print("{{result}}").subTasks(new Task[]{traverse("left"), traverse("right")}))
*/
                        .fromVar("geoIndex")
                        //.print("{{result}}")
                        .action(NTreeNearestN.NAME, "49.6116,6.1319,10") //lat,long,nb
                        //.print("{{result}}")
                        .then(new Action() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":7,\"lat\":49.5,\"long\":6.5}"));
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":6,\"lat\":49.4,\"long\":6.4}"));
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":8,\"lat\":49.6,\"long\":6.6}"));
                                context.continueTask();
                            }
                        })
                        .fromVar("geoIndex")
                        .action(NTreeNearestWithinRadius.NAME, "49.6116,6.1319,100000") //lat,long,meters
                        //.print("{{result}}")
                        .then(new Action() {
                            @Override
                            public void eval(TaskContext context) {

                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":7,\"lat\":49.5,\"long\":6.5}"));
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":6,\"lat\":49.4,\"long\":6.4}"));

                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":8,\"lat\":49.6,\"long\":6.6}"));
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":5,\"lat\":49.3,\"long\":6.3}"));

                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":9,\"lat\":49.7,\"long\":6.7}"));
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":4,\"lat\":49.2,\"long\":6.2}"));

                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":10,\"lat\":49.8,\"long\":6.8}"));
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":3,\"lat\":49.1,\"long\":6.1}"));

                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":11,\"lat\":49.9,\"long\":6.9}"));
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":2,\"lat\":49.0,\"long\":6.0}") || stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":2,\"lat\":49,\"long\":6}"));

                                context.continueTask();
                            }
                        })
                        .fromVar("geoIndex")
                        .action(NTreeNearestNWithinRadius.NAME, "49.6116,6.1319,2,100000") //lat,long,nb,meters
                        //.print("{{result}}")
                        .then(new Action() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":7,\"lat\":49.5,\"long\":6.5}"));
                                Assert.assertTrue(stringContains(context.toString(), "{\"world\":0,\"time\":0,\"id\":6,\"lat\":49.4,\"long\":6.4}"));
                                context.continueTask();
                            }
                        })
                        .execute(g, null);
            }
        });

    }

}
