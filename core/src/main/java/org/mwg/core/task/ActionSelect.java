package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionSelect;
import org.mwg.task.TaskResult;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

class ActionSelect implements Action {

    private final String _script;
    private final TaskFunctionSelect _filter;

    ActionSelect(String script, TaskFunctionSelect filter) {
        this._script = script;
        this._filter = filter;
    }


    @Override
    public void eval(TaskContext context) {
        final TaskResult previous = context.result();
        final TaskResult next = context.newResult();
        final int previousSize = previous.size();

        for (int i = 0; i < previousSize; i++) {
            final Object obj = previous.get(i);
            if (obj instanceof BaseNode) {
                final Node casted = (Node) obj;

                if (_filter != null && _filter.select(casted, context)) {
                    next.add(casted);
                } else if (_script != null && callScript(casted,context)) {
                    next.add(casted);
                } else {
                    casted.free();
                }
            } else {
                next.add(obj);
            }
        }

        //optimization to avoid the need to clone selected nodes
        previous.clear();
        context.continueWith(next);
    }


    /**
     * @native ts
     * var print = console.log;
     * return eval(this._script);
     */
    private boolean callScript(Node node, TaskContext context) {
        ScriptContext scriptCtx = new SimpleScriptContext();
        scriptCtx.setAttribute("node", node, ScriptContext.ENGINE_SCOPE);
        scriptCtx.setAttribute("context", context, ScriptContext.ENGINE_SCOPE);
        try {
            return (boolean) TaskHelper.SCRIPT_ENGINE.eval(_script, scriptCtx);
        } catch (ScriptException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        if(_filter != null) {
            return "select()";
        } else {
            return "selectScript(\"" + _script + "\");";
        }
    }
}
