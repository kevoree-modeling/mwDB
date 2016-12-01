package org.mwg.importer;

import org.mwg.base.BasePlugin;
import org.mwg.task.Action;
import org.mwg.task.TaskActionFactory;

public class ImporterPlugin extends BasePlugin {

    public ImporterPlugin() {
        declareTaskAction(ImporterActions.READLINES, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ImporterActions.READLINES + " action need one parameter");
                }
                return new ActionReadLines(params[0]);
            }
        });
        declareTaskAction(ImporterActions.READFILES, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ImporterActions.READFILES + " action need one parameter");
                }
                return new ActionReadFiles(params[0]);
            }
        });

        declareTaskAction(ImporterActions.READJSON, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ImporterActions.READFILES + " action need one parameter");
                }
                return new ActionReadJson(params[0]);
            }
        });

        /*
        declareTaskAction(ImporterActions.JSONMATCH, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException(ImporterActions.JSONMATCH + " action need one parameter");
                }
                return new ActionJsonMatch(params[0]);
            }
        });*/

    }
}
