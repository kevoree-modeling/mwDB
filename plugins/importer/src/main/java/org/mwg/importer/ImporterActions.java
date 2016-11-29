package org.mwg.importer;

import org.mwg.importer.action.JsonMatch;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.pluginAction;

public class ImporterActions {

    public static final String READFILES = "readFiles";

    public static final String READLINES = "readLines";

    public static final String READJSON = "readJson";

    public static final String JSONMATCH = "jsonMatch";

    public static Action readLines(String path) {
        return pluginAction(READLINES, path);
    }

    public static Action readFiles(String pathOrVar) {
        return pluginAction(READFILES, pathOrVar);
    }

    public static Action readJson(String pathOrVar) {
        return pluginAction(READJSON, pathOrVar);
    }

    public static Action jsonMatch(String filter, Task then) {
        return new Action() {
            @Override
            public void eval(TaskContext context) {
                new JsonMatch(filter, then).eval(context);
            }
        };
    }

}
