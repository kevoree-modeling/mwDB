package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ActionScript implements Action {

    private String _script;

    public ActionScript(String script) {
        this._script = script;
    }

    /**
     * @native ts
     * var print = console.log;
     * eval(this._script);
     */
    @Override
    public void eval(TaskContext context) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        engine.getContext().setAttribute("context", context, javax.script.ScriptContext.ENGINE_SCOPE);
        try {
            engine.eval(this._script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "script(\"" + _script + "\")";
    }
}
