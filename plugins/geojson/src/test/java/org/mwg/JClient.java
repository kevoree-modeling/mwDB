package org.mwg;

import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;
import org.mwg.structure.StructurePlugin;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.mwg.task.Actions.*;

/**
 * Created by gnain on 14/09/16.
 */
public class JClient {


    public static void main(String[] args) {


        final Graph g = new GraphBuilder().withMemorySize(200000).withPlugin(new StructurePlugin()).withPlugin(new MLPlugin()).withStorage(new WSClient("ws://localhost:8050")).build();
        g.connect(connectionResult -> {




            Task showAvailabilitiesTask = org.mwg.task.Actions
                    // .hook(hookFactory)
                    .setTime("{{processTime}}")
                    .lookup("{{nodeId}}")
                    .asVar("node")
                    .println("{{result}}")
                    .traverse("available_bikes")
                    .println("{{result}}")
                    .asVar("available_bikes")
                    .fromVar("node")
                    .traverse("available_bike_stands")
                    .println("{{result}}")
                    .asVar("available_bike_stands")
                    .fromVar("node")
                    .traverse("station_profile")
                    .println("{{result}}")
                    .asVar("station_profile")
                    .then(context->{
                        context.variable("node").get(0);
                        context.variable("available_bike_stands").get(0);
                        context.variable("available_bikes").get(0);
                context.continueTask();
            });

                TaskContext context = showAvailabilitiesTask.prepareWith(g, null, result-> {
                    result.free();
                });
                context.setVariable("nodeId", 4588);
                context.setVariable("processTime", 1474370640000L);
                showAvailabilitiesTask.executeUsing(context);

        });
    }

    private void test1(Graph g) {
        //updateContract(g);
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("09/09/2016 17:06:00", formatter);
        System.out.printf("%s%n", date);
        System.out.println(date.toInstant(ZoneOffset.of("+1")).toEpochMilli());



        Task readProfile = newTask()
                .setTime("" + System.currentTimeMillis())
                .fromIndex("cities", "name=Luxembourg")
                .traverseIndex("stations", "name", "BRICHERHAFF")
                //.print("{{result}}")
                .asVar("station")
                .inject(date.toInstant(ZoneOffset.of("+1")).toEpochMilli())
                .asVar("initialTime")
                .asVar("currentTime")
                .fromVar("station")
                .doWhile(
                        jump("{{currentTime}}")
                                .asVar("station")
                                //.print("{{result}}")
                                .traverse(JCDecauxHistoryLoad.STATION_PROFILE)
                                .asVar(JCDecauxHistoryLoad.STATION_PROFILE_NODE)
                                .fromVar("station")
                                //.jump("{{=currentTime-(7*24*3600*1000)}}")
                                //.asVar("station")
                                .traverse(JCDecauxHistoryLoad.AVAILABLE_BIKES)
                                .asVar(JCDecauxHistoryLoad.AVAILABLE_BIKES_VALUE)
                                .fromVar("station")
                                .traverse(JCDecauxHistoryLoad.AVAILABLE_STANDS)
                                .asVar(JCDecauxHistoryLoad.AVAILABLE_STANDS_VALUE)
                                .fromVar("station")
                                .then(context -> {
                                    GaussianSlotNode stationProfileNode = (GaussianSlotNode) context.variable(JCDecauxHistoryLoad.STATION_PROFILE_NODE).get(0);
                                    LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(stationProfileNode.time()), ZoneId.systemDefault());

                                    Node availableBikesValue = (Node)context.variable(JCDecauxHistoryLoad.AVAILABLE_BIKES_VALUE).get(0);
                                    Node availableStandsValue = (Node)context.variable(JCDecauxHistoryLoad.AVAILABLE_STANDS_VALUE).get(0);

                                    stationProfileNode.predict(result -> {
                                        System.out.println("" + ldt.getHour() + "h: \t" + result[0] + " bikes(" + availableBikesValue.get("value") + ")  and " + result[1] + " stands(" + availableStandsValue.get("value") + ") available");
                                        context.continueTask();
                                    });


                                }), context -> {
                            long time = (long)context.variable("currentTime").get(0);
                            time += 3600 * 1000;
                            if (time < (((long)context.variable("initialTime").get(0)) + (25 * 3600 * 1000))) {
                                context.setTime(time);
                                context.setVariable("currentTime", time);
                                return true;
                            } else {
                                return false;
                            }
                        });
        readProfile.execute(g, null);
    }

}
