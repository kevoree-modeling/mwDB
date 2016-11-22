package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.task.TaskResult;
import org.mwg.utility.VerbosePlugin;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.task;

public class SmallWorldTest {

    public static void main(String[] args) {

        Graph g = new GraphBuilder()
                .withMemorySize(100000)
                .withPlugin(new VerbosePlugin())
                .build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean isConnected) {
                task()
                        .then(setTime("0"))
                        .then(setWorld("0"))
                        .then(createNode()).then(set("name", Type.STRING, "room0")).then(addToGlobalIndex("rooms", "name")).then(setAsVar("room0"))
                        .then(createNode()).then(set("name", Type.STRING, "room01")).then(addToGlobalIndex("rooms", "name")).then(setAsVar("room01"))
                        .then(createNode()).then(set("name", Type.STRING, "room001")).then(addToGlobalIndex("rooms", "name")).then(setAsVar("room001"))
                        .then(createNode()).then(set("name", Type.STRING, "room0001")).then(addToGlobalIndex("rooms", "name")).then(setAsVar("room0001"))
                        .then(readVar("room0")).then(addVarToRelation("rooms", "room01"))
                        .then(readVar("room01")).then(addVarToRelation("rooms", "room001"))
                        .then(readVar("room001")).then(addVarToRelation("rooms", "room0001"))
                        .loop("0", "9", //loop automatically inject an it variable
                                task()
                                        .then(createNode())
                                        .then(set("id", Type.STRING, "sensor_{{it}}"))
                                        .then(addToGlobalIndex("sensors", "id"))
                                        .then(defineAsVar("sensor"))
                                        .ifThenElse(cond("i % 4 == 0"), task().then(readVar("room0")).then(addVarToRelation("sensors", "sensor")),
                                                task().ifThenElse(cond("i % 4 == 1"), task().then(readVar("room01")).then(addVarToRelation("sensors", "sensor")),
                                                        task().ifThenElse(cond("i % 4 == 2"), task().then(readVar("room001")).then(addVarToRelation("sensors", "sensor")),
                                                                task().ifThen(cond("i % 4 == 3"), task().then(readVar("room0001")).then(addVarToRelation("sensors", "sensor"))))))
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
