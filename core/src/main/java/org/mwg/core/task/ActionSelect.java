package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionSelect;
import org.mwg.task.TaskResult;

import javax.script.*;

class ActionSelect extends AbstractTaskAction {

    private final TaskFunctionSelect _filter;
    private final String _script;

    /**
     * @ignore ts
     */
    static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

    ActionSelect(String script, TaskFunctionSelect filter) {
        super();
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
            if (obj instanceof AbstractNode) {
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
            return (boolean) SCRIPT_ENGINE.eval(_script, scriptCtx);
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
