package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;

import javax.script.*;

public class ActionScript implements Action {

    private String _script;


    ActionScript(String script) {
        this._script = script;
    }

    /**
     * @native ts
     * var print = console.log;
     * eval(this._script);
     */
    @Override
    public void eval(TaskContext context) {
        ScriptContext scriptCtx = new SimpleScriptContext();
        scriptCtx.setAttribute("context",context,ScriptContext.ENGINE_SCOPE);
        try {
            TaskHelper.SCRIPT_ENGINE.eval(this._script,scriptCtx);
        } catch (ScriptException e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "script(\"" + _script + "\")";
    }
}
