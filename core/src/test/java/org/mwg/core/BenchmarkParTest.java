package org.mwg.core;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.core.scheduler.HybridScheduler;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.task;

/**
 * @ignore ts
 */
@SuppressWarnings("Duplicates")
public class BenchmarkParTest {

    public static void main(String[] args) {
        Graph g = new GraphBuilder()
                .withMemorySize(1000000)
                .withScheduler(new HybridScheduler())
                //.withScheduler(new TrampolineScheduler())
                //.withScheduler(new ExecutorScheduler())
                .build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                final long previous = System.currentTimeMillis();
                final long previousCache = g.space().available();
                task().loopPar("0", "9999",
                        task()
                                .then(createNode())
                                .then(set("name", Type.STRING, "node_{{i}}"))
                                .then(print("{{result}}"))
                                .then(addToGlobalIndex("nodes", "name"))
                                .loop("0", "999",
                                        task().then(jump("{{i}}")).then(set("val", Type.INT, "{{i}}")).then(clearResult()))
                                .ifThen(cond("i % 100 == 0"), task().then(save()))
                                .then(clearResult())
                ).then(save()).then(readGlobalIndexAll("nodes")).execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        System.out.println("indexSize=" + result.size());
                        result.free();
                        long after = System.currentTimeMillis();
                        long afterCache = g.space().available();
                        System.out.println(after - previous + "ms");
                        System.out.println(previousCache + "-" + afterCache);
                        g.disconnect(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {
                            }
                        });
                    }
                });
            }
        });
    }


}
