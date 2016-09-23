package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.core.task.math.CoreMathExpressionEngine;
import org.mwg.core.task.math.MathExpressionEngine;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

import java.lang.reflect.InvocationTargetException;
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
    private AbstractTaskAction _current;
    TaskResult _result;
    private long _world;
    private long _time;
    private final TaskHook _hook;

    CoreTaskContext(final TaskContext parentContext, final TaskResult initial, final Graph p_graph, final TaskHook p_hook, final Callback<TaskResult> p_callback) {
        this._hook = p_hook;
        if (parentContext != null) {
            this._time = parentContext.time();
            this._world = parentContext.world();
        } else {
            this._world = 0;
            this._time = 0;
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
    public final void setWorld(long p_world) {
        this._world = p_world;
    }

    @Override
    public final long time() {
        return this._time;
    }

    @Override
    public final void setTime(long p_time) {
        this._time = p_time;
    }

    @Override
    public final TaskResult variable(final String name) {
        TaskResult resolved = this._globalVariables.get(name);
        if (resolved == null) {
            resolved = internal_deep_resolve(name);
        }
        return resolved;
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
            return new CoreTaskResult(input, false);
        //}
    }

    @Override
    public TaskResult wrapClone(Object input) {
        return new CoreTaskResult(input, true);
    }

    @Override
    public TaskResult newResult() {
        return new CoreTaskResult(null, false);
    }

    @Override
    public void declareVariable(final String name) {
        if (this._localVariables == null) {
            this._localVariables = new HashMap<String, TaskResult>();
        }
        this._localVariables.put(name, new CoreTaskResult(null, false));
    }

    private TaskResult lazyWrap(Object input) {
        if (input instanceof CoreTaskResult) {
            return (TaskResult) input;
        } else {
            return wrap(input);
        }
    }

    @Override
    public void defineVariable(final String name, Object initialResult) {
        if (this._localVariables == null) {
            this._localVariables = new HashMap<String, TaskResult>();
        }
        this._localVariables.put(name, lazyWrap(initialResult).clone());
    }

    @Override
    public void defineVariableForSubTask(String name, Object initialResult) {
        if (this._nextVariables == null) {
            this._nextVariables = new HashMap<String, TaskResult>();
        }
        this._nextVariables.put(name, lazyWrap(initialResult).clone());
    }

    @Override
    public final void setGlobalVariable(final String name, final Object value) {
        final TaskResult previous = this._globalVariables.put(name, lazyWrap(value).clone());
        if (previous != null) {
            previous.free();
        }
    }

    @Override
    public final void setVariable(final String name, final Object value) {
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
    public final void addToGlobalVariable(final String name, final Object value) {
        TaskResult previous = this._globalVariables.get(name);
        if (previous == null) {
            previous = new CoreTaskResult(null, false);
            this._globalVariables.put(name, previous);
        }
        if (value != null) {
            if (value instanceof CoreTaskResult) {
                TaskResult casted = (TaskResult) value;
                for (int i = 0; i < casted.size(); i++) {
                    final Object loop = casted.get(i);
                    if (loop instanceof AbstractNode) {
                        final Node castedNode = (Node) loop;
                        previous.add(castedNode.graph().cloneNode(castedNode));
                    } else {
                        previous.add(loop);
                    }
                }
            } else if (value instanceof AbstractNode) {
                final Node castedNode = (Node) value;
                previous.add(castedNode.graph().cloneNode(castedNode));
            } else {
                previous.add(value);
            }
        }
    }

    @Override
    public final void addToVariable(final String name, final Object value) {
        Map<String, TaskResult> target = internal_deep_resolve_map(name);
        if (target == null) {
            if (this._localVariables == null) {
                this._localVariables = new HashMap<String, TaskResult>();
            }
            target = this._localVariables;
        }
        TaskResult previous = target.get(name);
        if (previous == null) {
            previous = new CoreTaskResult(null, false);
            target.put(name, previous);
        }
        if (value != null) {
            if (value instanceof CoreTaskResult) {
                TaskResult casted = (TaskResult) value;
                for (int i = 0; i < casted.size(); i++) {
                    final Object loop = casted.get(i);
                    if (loop instanceof AbstractNode) {
                        final Node castedNode = (Node) loop;
                        previous.add(castedNode.graph().cloneNode(castedNode));
                    } else {
                        previous.add(loop);
                    }
                }
            } else if (value instanceof AbstractNode) {
                final Node castedNode = (Node) value;
                previous.add(castedNode.graph().cloneNode(castedNode));
            } else {
                previous.add(value);
            }
        }
    }

    Map<String, TaskResult> globalVariables() {
        return this._globalVariables;
    }

    Map<String, TaskResult> nextVariables() {
        return this._globalVariables;
    }

    Map<String, TaskResult> variables() {
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
        //next step now...
        if (this._hook != null) {
            this._hook.afterAction(_current, this);
        }
        final AbstractTaskAction nextAction = _current.next();
        _current = nextAction;
        if (nextAction == null) {
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
            if (this._hook != null) {
                if (this._parent == null) {
                    this._hook.end(this);
                } else {
                    this._hook.afterTask(this);
                }
            }
            if (this._callback != null) {
                this._callback.on(_result);
            } else {
                if (this._result != null) {
                    this._result.free();
                }
            }
        } else {
            if (this._hook != null) {
                this._hook.beforeAction(nextAction, this);
            }
            nextAction.eval(this);
        }
    }

    final void execute(AbstractTaskAction initialTaskAction) {
        this._current = initialTaskAction;
        if (this._hook != null) {
            if (_parent == null) {
                _hook.start(this);
            } else {
                _hook.beforeTask(_parent, this);
            }
            this._hook.beforeAction(_current, this);
        }
        this._current.eval(this);
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
                    String[] methodCalls = extractMethodElements(contextKey);
                    //check if it is an array access
                    int indexArray = -1;
                    if (methodCalls[0].charAt(methodCalls[0].length() - 1) == ']') {
                        int indexStart = -1;
                        for (int i = methodCalls[0].length() - 3; i >= 0; i--) {
                            if (methodCalls[0].charAt(i) == '[') {
                                indexStart = i + 1;
                                break;
                            }
                        }
                        if (indexStart != -1) {
                            indexArray = TaskHelper.parseInt(methodCalls[0].substring(indexStart, methodCalls[0].length() - 1));
                            contextKey = methodCalls[0].substring(0, indexStart - 1);
                            if (indexArray < 0) {
                                throw new RuntimeException("Array index out of range: " + indexArray);
                            }
                        }
                    }
                    TaskResult foundVar = variable(contextKey);
                    if (foundVar == null && methodCalls[0].equals("result")) {
                        foundVar = result();
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

                            if(methodCalls.length > 1) {
                                int sizeParans = (methodCalls.length > 2)? (methodCalls.length - 2) : 0;
                                Object[] parameters = new Object[sizeParans];
                                Class[] parametersType = new Class[sizeParans];
                                for(int i=0;i<parameters.length;i++) {
                                    TaskResult var = variable(methodCalls[i + 2]);
                                    Object p;
                                    if(var == null && methodCalls[i + 2].equals("result")) {
                                        p = result().get(0);
                                    } else if(var == null) {
                                        p = methodCalls[i + 2];
                                    } else {
                                        p = var.get(0);
                                    }
                                    parameters[i] = p;
                                    parametersType[i] = p.getClass();
                                }
                                try {
                                    toShow = toShow.getClass().getMethod(methodCalls[1],parametersType).invoke(toShow, parameters);
                                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                                    throw new RuntimeException(ex);
                                }
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

    /**
     * Extract all the methods elements (object, method name and parameters) of a given method call.
     * Result:
     *  return[0] = object name
     *  return[1] = method name //if present
     *  return[2..n] = parameters //if present
     *
     * @param methodCall method call
     * @return methods elements
     */
    private String[] extractMethodElements(String methodCall) {
        String[] result = null;

        int startIndex = -1;
        int cursor = 0;
        while(cursor < methodCall.length()) {
            if(methodCall.charAt(cursor) == '.') {
                result = new String[1];
                result[0] = methodCall.substring(0,cursor);
                startIndex = -1;
            } else if(methodCall.charAt(cursor) == '(' || methodCall.charAt(cursor) == ',' ||
                    (methodCall.charAt(cursor) == ')' && startIndex != -1)){
                String[] tmp = new String[result.length + 1];
                System.arraycopy(result,0,tmp,0,result.length);
                result = tmp;
                result[result.length - 1] = methodCall.substring(startIndex,cursor);
                startIndex = -1;
            } else if(startIndex == -1) {
                startIndex = cursor;
            }

            cursor++;
        }

        if(result == null) {
            return new String[]{methodCall};
        }
        return result;
    }

    @Override
    public TaskHook hook() {
        return this._hook;
    }

    @Override
    public String toString() {
        return "{result:" + _result.toString() + "}";
    }
}
