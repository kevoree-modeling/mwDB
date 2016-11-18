package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.core.CoreConstants;
import org.mwg.base.AbstractAction;
import org.mwg.task.Action;
import org.mwg.task.TaskActionFactory;
import org.mwg.task.TaskContext;

class ActionPlugin extends AbstractAction {

    private final String _actionName;
    private final String _flatParams;
    //private boolean initilized = false;

    private Action subAction = null;

    ActionPlugin(final String actionName, final String flatParams) {
        super();
        this._actionName = actionName;
        this._flatParams = flatParams;
    }

    @Override
    public void eval(final TaskContext context) {
        final String templatedName = context.template(_actionName);
        final String templatedParams = context.template(_flatParams);
        //if (!initilized) {
        final TaskActionFactory actionFactory = context.graph().taskAction(templatedName);
        if (actionFactory == null) {
            throw new RuntimeException("Unknown task action: " + templatedName);
        }
        int paramsCapacity = CoreConstants.MAP_INITIAL_CAPACITY;
        String[] params = new String[paramsCapacity];
        int paramsIndex = 0;
        int cursor = 0;
        int flatSize = templatedParams.length();
        int previous = 0;
        while (cursor < flatSize) {
            char current = templatedParams.charAt(cursor);
            if (current == Constants.QUERY_SEP) {
                String param = templatedParams.substring(previous, cursor);
                if (param.length() > 0) {
                    if (paramsIndex >= paramsCapacity) {
                        int newParamsCapacity = paramsCapacity * 2;
                        String[] newParams = new String[newParamsCapacity];
                        System.arraycopy(params, 0, newParams, 0, paramsCapacity);
                        params = newParams;
                        paramsCapacity = newParamsCapacity;
                    }
                    params[paramsIndex] = param;
                    paramsIndex++;
                }
                previous = cursor + 1;
            }
            cursor++;
        }
        //add last param
        String param = templatedParams.substring(previous, cursor);
        if (param.length() > 0) {
            if (paramsIndex >= paramsCapacity) {
                int newParamsCapacity = paramsCapacity * 2;
                String[] newParams = new String[newParamsCapacity];
                System.arraycopy(params, 0, newParams, 0, paramsCapacity);
                params = newParams;
                paramsCapacity = newParamsCapacity;
            }
            params[paramsIndex] = param;
            paramsIndex++;
        }
        //schrink
        if (paramsIndex < params.length) {
            String[] shrinked = new String[paramsIndex];
            System.arraycopy(params, 0, shrinked, 0, paramsIndex);
            params = shrinked;
        }
        //add the action to the action
        subAction = actionFactory.create(params);
        //initilized = true;
        // }
        if (subAction != null) {
            subAction.eval(context);
        } else {
            context.continueTask();
        }
    }

    @Override
    public String toString() {
        return _actionName + "(" + _flatParams + ")";
    }

}
