/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.TaskResult;
import org.mwg.task.Tasks;

import static org.mwg.internal.task.CoreActions.timepoints;

public class ActionTimepointsTest {

    @Test
    public void testTP() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Tasks.newTask()
                        .travelInTime("0")
                        .travelInWorld("0")
                        .createNode()
                        .setAsVar("node")
                        .setAttribute("value", Type.INT,"1")
                        .readVar("node")
                        .travelInTime("1")
                        .setAttribute("value", Type.INT,"2")
                        .readVar("node")
                        .travelInTime("3")
                        .setAttribute("value", Type.INT,"3")
                        .readVar("node")
                        .then(timepoints(Constants.BEGINNING_OF_TIME + "", Constants.END_OF_TIME + ""))
                        .execute(graph, new Callback<TaskResult>() {
                            @Override
                            public void on(TaskResult result) {
                                Assert.assertEquals(3,result.size());
                                Assert.assertArrayEquals(new Object[]{3L,1L,0L},result.asArray());
                            }
                        });
            }

        });
    }
}
