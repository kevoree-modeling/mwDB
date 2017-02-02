/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.internal.task;

import org.junit.Assert;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.internal.scheduler.NoopScheduler;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.inject;
import static org.mwg.internal.task.CoreActions.select;
import static org.mwg.task.Tasks.newTask;

public class ContextCleanTest {

    // @Test
    public void finalCleanTest() {


        /*
        final TaskContext[] retention = new TaskContext[1];

        final Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        retention[0] = context;
                        Node node = graph.newNode(0, 0);
                        node.set("name", "node");
                        context.setResult(node);
                    }
                }).execute(graph, null);
            }
        });

        boolean shouldCrash = false;
        try {
            System.out.println(retention[0]);
        } catch (Exception e) {
            shouldCrash = true;
        }
        Assert.assertEquals(shouldCrash, true);
        */
    }

    // @Test
    public void complexTest() {
        final TaskContext[] retention = new TaskContext[2];
        final Graph graph = new GraphBuilder().withScheduler(new NoopScheduler()).build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                final String[] flat = {""};
                Node n0 = graph.newNode(0, 0);
                Node n1 = graph.newNode(0, 0);
                newTask().then(inject(new Node[]{n0, n1}))
                        .then(select((node, context) -> true))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext ctx) {
                                retention[0] = ctx;
                            }
                        })
                        /*
                        .foreachThen(new Callback<Node>() {
                            @Override
                            public void on(Node result) {
                                flat[0] += result.toString();
                            }
                        })
                        .executeThen(graph, new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                retention[1] = context;
                            }
                        });*/;
            }
        });

        boolean shouldCrash = false;
        try {
            System.out.println(retention[0]);
            System.out.println(retention[1]);
        } catch (Exception e) {
            shouldCrash = true;
        }
        Assert.assertEquals(shouldCrash, true);
    }

}
