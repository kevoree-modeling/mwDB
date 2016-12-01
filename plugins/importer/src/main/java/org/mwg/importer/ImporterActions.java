package org.mwg.importer;

import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.pluginAction;

public class ImporterActions {

    public static final String READFILES = "readFiles";

    public static final String READLINES = "readLines";

    public static final String READJSON = "readJson";

    public static final String JSONMATCH = "jsonMatch";

    public static Action split(String path) {
        return new ActionSplit(path);
    }

    public static Action readLines(String path) {
        return new ActionReadLines(path);
    }

    public static Action readFiles(String pathOrVar) {
        return new ActionReadFiles(pathOrVar);
    }

    public static Action readJson(String pathOrVar) {
        return pluginAction(READJSON, pathOrVar);
    }

    public static Action jsonMatch(String filter, Task then) {
        return new Action() {
            @Override
            public void eval(TaskContext context) {
                new ActionJsonMatch(filter, then).eval(context);
            }
        };
    }

}
