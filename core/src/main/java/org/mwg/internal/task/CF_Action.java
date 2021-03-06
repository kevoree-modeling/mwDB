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

import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import java.util.Map;

public abstract class CF_Action implements Action {

    abstract public Task[] children();

    abstract public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS);

    @Override
    public abstract void eval(TaskContext ctx);

    @Override
    public void serialize(StringBuilder builder) {
        throw new RuntimeException("serialization error !!!");
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        cf_serialize(res, null);
        return res.toString();
    }

}
