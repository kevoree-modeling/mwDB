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
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.Tasks;

class ActionDeclareVar implements Action {

    private final String _name;
    private final boolean _isGlobal;

    ActionDeclareVar(final boolean isGlobal, final String p_name) {
        if (p_name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = p_name;
        this._isGlobal = isGlobal;
    }

    @Override
    public void eval(final TaskContext ctx) {
        if(_isGlobal){
            ctx.setGlobalVariable(ctx.template(_name), Tasks.emptyResult());
        } else {
            ctx.declareVariable(ctx.template(_name));
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        if(_isGlobal){
            builder.append(CoreActionNames.DECLARE_GLOBAL_VAR);
        } else {
            builder.append(CoreActionNames.DECLARE_VAR);
        }
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
