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

import org.mwg.Constants;
import org.mwg.base.BaseTaskResult;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionFlat implements Action {

    ActionFlat() {
    }

    @Override
    public void eval(final TaskContext ctx) {
        TaskResult result = ctx.result();
        if (result == null) {
            ctx.continueTask();
        } else {
            final TaskResult next = ctx.newResult();
            for (int i = 0; i < result.size(); i++) {
                final Object loop = result.get(i);
                if(loop instanceof BaseTaskResult){
                    BaseTaskResult casted = (BaseTaskResult) loop;
                    for (int j = 0; j < casted.size(); j++) {
                        final Object resultLoop = casted.get(j);
                        if (resultLoop != null) {
                            next.add(resultLoop);
                        }
                    }
                } else {
                    if (loop != null) {
                        next.add(loop);
                    }
                }


            }
            ctx.continueWith(next);
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.FLAT);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
