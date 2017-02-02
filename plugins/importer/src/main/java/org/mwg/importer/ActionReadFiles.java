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
package org.mwg.importer;

import org.mwg.Constants;
import org.mwg.internal.task.TaskHelper;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

class ActionReadFiles implements Action {

    private final String _pathOrTemplate;

    ActionReadFiles(String _pathOrTemplate) {
        this._pathOrTemplate = _pathOrTemplate;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult next = ctx.wrap(null);
        final String path = ctx.template(_pathOrTemplate);
        if (path == null) {
            throw new RuntimeException("Variable " + _pathOrTemplate + " does not exist in the context");
        }
        File file = null;
        try {
            file = new File(URLDecoder.decode(path, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            //should never append
        }
        if (!file.exists()) {
            URL url = this.getClass().getClassLoader().getResource(path);
            if (url == null) {
                throw new RuntimeException("File " + path + " does not exist and it is not present in resources directory.");
            }
            file = new File(url.getPath());
        }
        if (file.isDirectory()) {
            final File[] listFiles = file.listFiles((dir, name) -> !name.contains(".DS_Store"));
            if (listFiles != null) {
                for (int i = 0; i < listFiles.length; i++) {
                    next.add(listFiles[i].getAbsolutePath());
                }
            }
        } else {
            next.add(file.getAbsolutePath());
        }
        ctx.continueWith(next);
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append("readFiles");
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_pathOrTemplate, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
