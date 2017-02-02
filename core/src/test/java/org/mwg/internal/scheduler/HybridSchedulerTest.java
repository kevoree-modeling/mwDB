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
package org.mwg.internal.scheduler;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.task.Tasks.newTask;

/**
 * @ignore ts
 */
public class HybridSchedulerTest {

  //  @Test
    public void test() {
        Graph g = new GraphBuilder().withScheduler(new HybridScheduler()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                newTask().loopPar("0","99", newTask().loop("0","99", newTask().then(print("{{result}}")))).execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        System.out.println();

                    }
                });
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
