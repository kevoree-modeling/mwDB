package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.set;
import static org.mwg.core.task.Actions.task;

public class MicroWorldTest {

    public static void main(String[] args) {

        Graph g = new GraphBuilder()
                .withMemorySize(10000)
                .withPlugin(new org.mwg.utility.VerbosePlugin())
                .build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean isConnected) {

                task().loopPar("1", "2",
                        task()
                                .then(createNode())
                                .then(set("name", Type.STRING, "room_{{i}}"))
                                .then(addToGlobalIndex("rooms", "name"))
                                .then(defineAsVar("parentRoom"))
                                .loop("1", "3",
                                        task()
                                                .then(createNode())
                                                .then(set("sensor", Type.STRING, "sensor_{{i}}"))
                                        //.then(addTo("sensors", "parentRoom"))
                                )
                ).execute(g, null);


                /*
                loop("0", "3",
                        newNode()
                        .setAttribute("name", Type.STRING, "node_{{i}}")
                        .print("{{result}}")
                )
                .execute(g,null);
*/

            }
        });


    }

}
