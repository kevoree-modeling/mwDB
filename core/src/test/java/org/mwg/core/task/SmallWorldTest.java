package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.task.TaskResult;
import org.mwg.utility.VerbosePlugin;

import static org.mwg.core.task.Actions.*;

public class SmallWorldTest {

    public static void main(String[] args) {

        Graph g = new GraphBuilder()
                .withMemorySize(100000)
                .withPlugin(new VerbosePlugin())
                .build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean isConnected) {
                setTime("0").setWorld("0")
                        .newNode().setProperty("name", Type.STRING, "room0").indexNode("rooms", "name").asVar("room0")
                        .newNode().setProperty("name", Type.STRING, "room01").indexNode("rooms", "name").asVar("room01")
                        .newNode().setProperty("name", Type.STRING, "room001").indexNode("rooms", "name").asVar("room001")
                        .newNode().setProperty("name", Type.STRING, "room0001").indexNode("rooms", "name").asVar("room0001")
                        .fromVar("room0").add("rooms", "room01")
                        .fromVar("room01").add("rooms", "room001")
                        .fromVar("room001").add("rooms", "room0001")
                        .loop("0", "9", //loop automatically inject an it variable
                                newNode()
                                        .setProperty("id", Type.STRING, "sensor_{{it}}")
                                        .indexNode("sensors", "id")
                                        .defineVar("sensor")
                                        .ifThenElse(cond("i % 4 == 0"), fromVar("room0").add("sensors", "sensor"),
                                                ifThenElse(cond("i % 4 == 1"), fromVar("room01").add("sensors", "sensor"),
                                                        ifThenElse(cond("i % 4 == 2"), fromVar("room001").add("sensors", "sensor"),
                                                                ifThen(cond("i % 4 == 3"), fromVar("room0001").add("sensors", "sensor")))))
                        ).execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult taskResult) {
                        if (taskResult != null) {
                            taskResult.free();
                        }
                        System.out.println("MWG Server listener through :8050");
                    }
                });

            }
        });
    }

}
