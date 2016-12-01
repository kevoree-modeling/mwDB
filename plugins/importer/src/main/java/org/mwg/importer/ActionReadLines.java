package org.mwg.importer;

import org.mwg.importer.util.IterableLines;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionReadLines implements Action {

    private final String _pathOrTemplate;

    ActionReadLines(final String p_pathOrTemplate) {
        this._pathOrTemplate = p_pathOrTemplate;
    }

    @Override
    public void eval(final TaskContext context) {
        final String path = context.template(_pathOrTemplate);
        context.continueWith(new IterableLines(path));
    }

}
