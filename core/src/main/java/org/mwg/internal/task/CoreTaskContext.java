package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.base.BaseTaskResult;
import org.mwg.internal.task.math.CoreMathExpressionEngine;
import org.mwg.internal.task.math.MathExpressionEngine;
import org.mwg.base.BaseNode;
import org.mwg.task.*;
import org.mwg.utility.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class CoreTaskContext implements TaskContext {

    private final Map<String, TaskResult> _globalVariables;
    private final TaskContext _parent;
    private final Graph _graph;
    final Callback<TaskResult> _callback;

    private Map<String, TaskResult> _localVariables = null;
    private Map<String, TaskResult> _nextVariables = null;
    TaskResult _result;
    private long _world;
    private long _time;
    private final CoreTask _origin;
    private int cursor = 0;
    final TaskHook[] _hooks;
    private StringBuilder _output = null;

    CoreTaskContext(final CoreTask origin, final TaskHook[] p_hooks, final TaskContext parentContext, final TaskResult initial, final Graph p_graph, final Callback<TaskResult> p_callback) {
        this._origin = origin;
        this._hooks = p_hooks;
        if (parentContext != null) {
            this._time = parentContext.time();
            this._world = parentContext.world();
        } else {
            this._world = 0;
            this._time = Constants.BEGINNING_OF_TIME;
        }
        this._graph = p_graph;
        this._parent = parentContext;
        final CoreTaskContext castedParentContext = (CoreTaskContext) parentContext;
        if (parentContext == null) {
            this._globalVariables = new ConcurrentHashMap<String, TaskResult>();
        } else {
            this._globalVariables = castedParentContext.globalVariables();
        }
        this._result = initial;
        this._callback = p_callback;
    }

    @Override
    public final Graph graph() {
        return _graph;
    }

    @Override
    public final long world() {
        return this._world;
    }

    @Override
    public final TaskContext setWorld(long p_world) {
        this._world = p_world;
        return this;
    }

    @Override
    public final long time() {
        return this._time;
    }

    @Override
    public final TaskContext setTime(long p_time) {
        this._time = p_time;
        return this;
    }

    @Override
    public Tuple<String, TaskResult>[] variables() {
        Map<String, TaskResult> collected = new HashMap<String, TaskResult>();
        String[] globalKeys = _globalVariables.keySet().toArray(new String[_globalVariables.size()]);
        for (int i = 0; i < globalKeys.length; i++) {
            collected.put(globalKeys[i], _globalVariables.get(globalKeys[i]));
        }
        //recursive_collect(this, collected);
        //flatResult
        String[] collectedKeys = collected.keySet().toArray(new String[collected.size()]);
        Tuple<String, TaskResult>[] result = new Tuple[collectedKeys.length];
        for (int i = 0; i < collectedKeys.length; i++) {
            result[i] = new Tuple<String, TaskResult>(collectedKeys[i], collected.get(collectedKeys[i]));
        }
        return result;
    }

    private void recursive_collect(TaskContext ctx, Map<String, TaskResult> collector) {
        Map<String, TaskResult> localVariables = ((CoreTaskContext) ctx).localVariables();
        if(localVariables != null){
            String[] localKeys = localVariables.keySet().toArray(new String[localVariables.size()]);
            for (int i = 0; i < localKeys.length; i++) {
                if (!collector.containsKey(localKeys[i])) {
                    collector.put(localKeys[i], localVariables.get(localKeys[i]));
                }
            }
        }
        if (((CoreTaskContext) ctx)._parent != null) {
            recursive_collect(((CoreTaskContext) ctx)._parent, collector);
        }
    }

    @Override
    public final TaskResult variable(final String name) {
        TaskResult resolved = this._globalVariables.get(name);
        if (resolved == null) {
            resolved = internal_deep_resolve(name);
        }
        return resolved;
    }

    @Override
    public boolean isGlobal(String name) {
        return _globalVariables.containsKey(name);
    }

    private TaskResult internal_deep_resolve(final String name) {
        TaskResult resolved = null;
        if (this._localVariables != null) {
            resolved = this._localVariables.get(name);
        }
        if (resolved == null && this._parent != null) {
            final CoreTaskContext castedParent = (CoreTaskContext) _parent;
            if (castedParent._nextVariables != null) {
                resolved = castedParent._nextVariables.get(name);
                if (resolved != null) {
                    return resolved;
                }
            }
            return castedParent.internal_deep_resolve(name);
        } else {
            return resolved;
        }
    }

    @Override
    public TaskResult wrap(Object input) {
        //if(input instanceof TaskResult){
        //    return (TaskResult) input;
        //} else {
        return new BaseTaskResult(input, false);
        //}
    }

    @Override
    public TaskResult wrapClone(Object input) {
        return new BaseTaskResult(input, true);
    }

    @Override
    public TaskResult newResult() {
        return new BaseTaskResult(null, false);
    }

    @Override
    public TaskContext declareVariable(final String name) {
        if (this._localVariables == null) {
            this._localVariables = new HashMap<String, TaskResult>();
        }
        this._localVariables.put(name, new BaseTaskResult(null, false));
        return this;
    }

    private TaskResult lazyWrap(Object input) {
        if (input instanceof BaseTaskResult) {
            return (TaskResult) input;
        } else {
            return wrap(input);
        }
    }

    @Override
    public TaskContext defineVariable(final String name, Object initialResult) {
        if (this._localVariables == null) {
            this._localVariables = new HashMap<String, TaskResult>();
        }
        this._localVariables.put(name, lazyWrap(initialResult).clone());
        return this;
    }

    @Override
    public TaskContext defineVariableForSubTask(String name, Object initialResult) {
        if (this._nextVariables == null) {
            this._nextVariables = new HashMap<String, TaskResult>();
        }
        this._nextVariables.put(name, lazyWrap(initialResult).clone());
        return this;
    }

    @Override
    public final TaskContext setGlobalVariable(final String name, final Object value) {
        final TaskResult previous = this._globalVariables.put(name, lazyWrap(value).clone());
        if (previous != null) {
            previous.free();
        }
        return this;
    }

    @Override
    public final TaskContext setVariable(final String name, final Object value) {
        Map<String, TaskResult> target = internal_deep_resolve_map(name);
        if (target == null) {
            if (this._localVariables == null) {
                this._localVariables = new HashMap<String, TaskResult>();
            }
            target = this._localVariables;
        }
        final TaskResult previous = target.put(name, lazyWrap(value).clone());
        if (previous != null) {
            previous.free();
        }
        return this;
    }

    private Map<String, TaskResult> internal_deep_resolve_map(final String name) {
        if (this._localVariables != null) {
            TaskResult resolved = this._localVariables.get(name);
            if (resolved != null) {
                return this._localVariables;
            }
        }
        if (this._parent != null) {
            final CoreTaskContext castedParent = (CoreTaskContext) _parent;
            if (castedParent._nextVariables != null) {
                TaskResult resolved = castedParent._nextVariables.get(name);
                if (resolved != null) {
                    return this._localVariables;
                }
            }
            return ((CoreTaskContext) _parent).internal_deep_resolve_map(name);
        } else {
            return null;
        }
    }

    @Override
    public final TaskContext addToGlobalVariable(final String name, final Object value) {
        TaskResult previous = this._globalVariables.get(name);
        if (previous == null) {
            previous = new BaseTaskResult(null, false);
            this._globalVariables.put(name, previous);
        }
        if (value != null) {
            if (value instanceof BaseTaskResult) {
                TaskResult casted = (TaskResult) value;
                for (int i = 0; i < casted.size(); i++) {
                    final Object loop = casted.get(i);
                    if (loop instanceof BaseNode) {
                        final Node castedNode = (Node) loop;
                        previous.add(castedNode.graph().cloneNode(castedNode));
                    } else {
                        previous.add(loop);
                    }
                }
            } else if (value instanceof BaseNode) {
                final Node castedNode = (Node) value;
                previous.add(castedNode.graph().cloneNode(castedNode));
            } else {
                previous.add(value);
            }
        }
        return this;
    }

    @Override
    public final TaskContext addToVariable(final String name, final Object value) {
        Map<String, TaskResult> target = internal_deep_resolve_map(name);
        if (target == null) {
            if (this._localVariables == null) {
                this._localVariables = new HashMap<String, TaskResult>();
            }
            target = this._localVariables;
        }
        TaskResult previous = target.get(name);
        if (previous == null) {
            previous = new BaseTaskResult(null, false);
            target.put(name, previous);
        }
        if (value != null) {
            if (value instanceof BaseTaskResult) {
                TaskResult casted = (TaskResult) value;
                for (int i = 0; i < casted.size(); i++) {
                    final Object loop = casted.get(i);
                    if (loop instanceof BaseNode) {
                        final Node castedNode = (Node) loop;
                        previous.add(castedNode.graph().cloneNode(castedNode));
                    } else {
                        previous.add(loop);
                    }
                }
            } else if (value instanceof BaseNode) {
                final Node castedNode = (Node) value;
                previous.add(castedNode.graph().cloneNode(castedNode));
            } else {
                previous.add(value);
            }
        }
        return this;
    }

    Map<String, TaskResult> globalVariables() {
        return this._globalVariables;
    }

    Map<String, TaskResult> localVariables() {
        return this._localVariables;
    }

    @Override
    public final TaskResult result() {
        return this._result;
    }

    @Override
    public TaskResult<Node> resultAsNodes() {
        return (TaskResult<Node>) _result;
    }

    @Override
    public TaskResult<String> resultAsStrings() {
        return (TaskResult<String>) _result;
    }

    @Override
    public final void continueWith(TaskResult nextResult) {
        final TaskResult previousResult = this._result;
        if (previousResult != null && previousResult != nextResult) {
            previousResult.free();
        }
        _result = nextResult;
        continueTask();
    }

    @Override
    public final void continueTask() {
        final TaskHook[] globalHooks = this._graph.taskHooks();
        final Action currentAction = _origin.actions[cursor];
        //next step now...
        if (_hooks != null) {
            for (int i = 0; i < _hooks.length; i++) {
                _hooks[i].afterAction(currentAction, this);
            }
        }
        if (globalHooks != null) {
            for (int i = 0; i < globalHooks.length; i++) {
                globalHooks[i].afterAction(currentAction, this);
            }
        }
        cursor++;
        final Action nextAction;
        if (cursor == _origin.insertCursor) {
            nextAction = null;
        } else {
            nextAction = _origin.actions[cursor];
        }
        if (nextAction == null) {
            endTask(null, null);
        } else {
            if (_hooks != null) {
                for (int i = 0; i < _hooks.length; i++) {
                    _hooks[i].beforeAction(nextAction, this);
                }
            }
            if (globalHooks != null) {
                for (int i = 0; i < globalHooks.length; i++) {
                    globalHooks[i].beforeAction(nextAction, this);
                }
            }
            final int previousCursot = cursor;
            try {
                nextAction.eval(this);
            } catch (Exception e) {
                if (cursor == previousCursot) {
                    endTask(null, e);
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void endTask(final TaskResult preFinalResult, final Exception e) {
        if (preFinalResult != null) {
            if (_result != null) {
                _result.free();
            }
            _result = preFinalResult;
        }

        final TaskHook[] globalHooks = this._graph.taskHooks();
        /* Clean */
        if (this._localVariables != null) {
            Set<String> localValues = this._localVariables.keySet();
            String[] flatLocalValues = localValues.toArray(new String[localValues.size()]);
            for (int i = 0; i < flatLocalValues.length; i++) {
                this._localVariables.get(flatLocalValues[i]).free();
            }
        }
        if (this._nextVariables != null) {
            Set<String> nextValues = this._nextVariables.keySet();
            String[] flatNextValues = nextValues.toArray(new String[nextValues.size()]);
            for (int i = 0; i < flatNextValues.length; i++) {
                this._nextVariables.get(flatNextValues[i]).free();
            }
        }
        if (this._parent == null) {
            Set<String> globalValues = this._globalVariables.keySet();
            String[] globalFlatValues = globalValues.toArray(new String[globalValues.size()]);
            for (int i = 0; i < globalFlatValues.length; i++) {
                this._globalVariables.get(globalFlatValues[i]).free();
            }
        }
        /* End Clean */
        if (_hooks != null) {
            for (int i = 0; i < _hooks.length; i++) {
                if (this._parent == null) {
                    _hooks[i].end(this);
                } else {
                    _hooks[i].afterTask(this);
                }
            }
        }
        if (globalHooks != null) {
            for (int i = 0; i < globalHooks.length; i++) {
                if (this._parent == null) {
                    globalHooks[i].end(this);
                } else {
                    globalHooks[i].afterTask(this);
                }
            }
        }
        if (this._callback != null) {
            if (e != null) {
                if (_result == null) {
                    _result = new BaseTaskResult(null, false);
                }
                _result.setException(e);
            }
            if (_output != null) {
                if (_result == null) {
                    _result = new BaseTaskResult(null, false);
                }
                _result.setOutput(_output.toString());
            }
            this._callback.on(_result);
        } else {
            if (e != null) {
                e.printStackTrace();
            }
            if (_output != null) {
                System.out.print(_output.toString());
            }
            if (this._result != null) {
                this._result.free();
            }
        }
    }

    @SuppressWarnings("Duplicates")
    final void execute() {
        final Action current = _origin.actions[cursor];
        if (_hooks != null) {
            for (int i = 0; i < _hooks.length; i++) {
                if (_parent == null) {
                    _hooks[i].start(this);
                } else {
                    _hooks[i].beforeTask(_parent, this);
                }
                _hooks[i].beforeAction(current, this);
            }
        }
        final TaskHook[] globalHooks = this._graph.taskHooks();
        if (globalHooks != null) {
            for (int i = 0; i < globalHooks.length; i++) {
                if (_parent == null) {
                    globalHooks[i].start(this);
                } else {
                    globalHooks[i].beforeTask(_parent, this);
                }
                globalHooks[i].beforeAction(current, this);
            }
        }
        try {
            current.eval(this);
        } catch (Exception e) {
            if (cursor == 0) {
                endTask(null, e);
            } else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public final String template(String input) {
        if (input == null) {
            return null;
        }
        int cursor = 0;
        StringBuilder buffer = null;
        int previousPos = -1;
        while (cursor < input.length()) {
            char currentChar = input.charAt(cursor);
            char previousChar = '0';
            char nextChar = '0';
            if (cursor > 0) {
                previousChar = input.charAt(cursor - 1);
            }
            if (cursor + 1 < input.length()) {
                nextChar = input.charAt(cursor + 1);
            }
            if (currentChar == '{' && previousChar == '{') {
                previousPos = cursor + 1;
            } else if (previousPos != -1 && currentChar == '}' && previousChar == '}') {
                if (buffer == null) {
                    buffer = new StringBuilder();
                    buffer.append(input.substring(0, previousPos - 2));
                }
                String contextKey = input.substring(previousPos, cursor - 1).trim();
                if (contextKey.length() > 0 && contextKey.charAt(0) == '=') { //Math expression
                    final MathExpressionEngine mathEngine = CoreMathExpressionEngine.parse(contextKey.substring(1));
                    double value = mathEngine.eval(null, this, new HashMap<String, Double>());
                    //supress ".0" if it exists
                    String valueStr = value + "";
                    for (int i = valueStr.length() - 1; i >= 0; i--) {
                        if (valueStr.charAt(i) == '.') {
                            valueStr = valueStr.substring(0, i);
                            break;
                        } else if (valueStr.charAt(i) != '0') {
                            break;
                        }
                    }
                    buffer.append(valueStr);
                } else {//variable name or array access
                    //check if it is an array access
                    int indexArray = -1;
                    if (contextKey.charAt(contextKey.length() - 1) == ']') {
                        int indexStart = -1;
                        for (int i = contextKey.length() - 3; i >= 0; i--) {
                            if (contextKey.charAt(i) == '[') {
                                indexStart = i + 1;
                                break;
                            }
                        }
                        if (indexStart != -1) {
                            String idx = contextKey.substring(indexStart, contextKey.length() - 1);
                            TaskResult vari = variable(idx);
                            if(vari != null) {
                                if(vari.get(0) instanceof Integer) {
                                    indexArray = (int) vari.get(0);
                                } else {
                                    throw new RuntimeException("Array index is set by a non integer variable " + idx + "=" + vari);
                                }
                            } else {
                                try {
                                    indexArray = TaskHelper.parseInt(idx);
                                } catch (NumberFormatException e) {
                                    throw new RuntimeException("Array index is set by a non integer literal: " + idx);
                                }
                            }
                            contextKey = contextKey.substring(0, indexStart - 1);
                            if (indexArray < 0) {
                                throw new RuntimeException("Array index out of range: " + indexArray);
                            }
                        }
                    }
                    TaskResult foundVar = variable(contextKey);

                    if (foundVar == null) {
                        switch (contextKey) {
                            case "result": {
                                foundVar = result();
                            }
                            break;
                            case "time": {
                                foundVar = wrap(_time);
                            }
                            break;
                            case "world": {
                                foundVar = wrap(_world);
                            }
                            break;
                        }
                    }

                    if (foundVar != null) {
                        if (foundVar.size() == 1 || indexArray != -1) {
                            //show element of array
                            Object toShow = null;
                            if (indexArray == -1) {
                                toShow = foundVar.get(0);
                            } else {
                                toShow = foundVar.get(indexArray);
                            }
                            buffer.append(toShow);
                        } else {
                            //show all
                            TaskResultIterator it = foundVar.iterator();
                            buffer.append("[");
                            boolean isFirst = true;
                            Object next = it.next();
                            while (next != null) {
                                if (isFirst) {
                                    isFirst = false;
                                } else {
                                    buffer.append(",");
                                }
                                buffer.append(next);
                                next = it.next();
                            }
                            buffer.append("]");
                        }
                    }
                }
                previousPos = -1;
            } else {
                if (previousPos == -1 && buffer != null) {
                    //check if we are not opening a {{
                    if (currentChar == '{' && nextChar == '{') {
                        //noop
                    } else {
                        buffer.append(input.charAt(cursor));
                    }
                }
            }
            cursor++;
        }
        if (buffer == null) {
            return input;
        } else {
            return buffer.toString();
        }
    }

    @Override
    public final String[] templates(String[] inputs) {
        if (inputs == null) {
            return null;
        }
        String[] result = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            result[i] = template(inputs[i]);
        }
        return result;
    }

    @Override
    public final synchronized void append(String additionalOutput) {
        if (_output == null) {
            _output = new StringBuilder();
        }
        _output.append(additionalOutput);
    }

    @Override
    public final String toString() {
        return "{result:" + _result.toString() + "}";
    }
}
