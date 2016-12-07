package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounterSync;
import org.mwg.Graph;
import org.mwg.core.task.math.MathConditional;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

import java.util.Map;

public class CoreTask implements org.mwg.task.Task {

    private AbstractTaskAction _first = null;
    private AbstractTaskAction _last = null;
    private TaskHookFactory _hookFactory = null;

    private void addAction(AbstractTaskAction nextAction) {
        if (_first == null) {
            _first = nextAction;
            _last = _first;
        } else {
            _last.setNext(nextAction);
            _last = nextAction;
        }
    }

    @Override
    public final org.mwg.task.Task setWorld(String template) {
        addAction(new ActionWorld(template));
        return this;
    }

    @Override
    public final org.mwg.task.Task setTime(String template) {
        addAction(new ActionTime(template));
        return this;
    }

    @Override
    public final org.mwg.task.Task fromIndex(String indexName, String query) {
        if (indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        if (query == null) {
            throw new RuntimeException("query should not be null");
        }
        addAction(new ActionFromIndex(indexName, query));
        return this;
    }

    @Override
    public final org.mwg.task.Task fromIndexAll(String indexName) {
        if (indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        addAction(new ActionFromIndexAll(indexName));
        return this;
    }

    @Override
    public Task indexNode(String indexName, String flatKeyAttributes) {
        addAction(new ActionIndexOrUnindexNode(indexName, flatKeyAttributes, true));
        return this;
    }

    @Override
    public Task indexNodeAt(String world, String time, String indexName, String flatKeyAttributes) {
        addAction(new ActionIndexOrUnindexNodeAt(world, time, indexName, flatKeyAttributes, true));
        return this;
    }

    @Override
    public Task localIndex(String indexedRelation, String flatKeyAttributes, String varNodeToAdd) {
        addAction(new ActionLocalIndexOrUnindex(indexedRelation, flatKeyAttributes, varNodeToAdd, true));
        return this;
    }

    @Override
    public Task unindexNodeAt(String world, String time, String indexName, String flatKeyAttributes) {
        addAction(new ActionIndexOrUnindexNodeAt(world, time, indexName, flatKeyAttributes, true));
        return this;
    }

    @Override
    public Task unindexNode(String indexName, String flatKeyAttributes) {
        addAction(new ActionIndexOrUnindexNode(indexName, flatKeyAttributes, false));
        return this;
    }

    @Override
    public Task localUnindex(String indexedRelation, String flatKeyAttributes, String varNodeToAdd) {
        addAction(new ActionLocalIndexOrUnindex(indexedRelation, flatKeyAttributes, varNodeToAdd, false));
        return this;
    }

    @Override
    public Task indexesNames() {
        addAction(new ActionIndexesNames());
        return this;
    }

    @Override
    public final org.mwg.task.Task selectWith(String name, String pattern) {
        if (pattern == null) {
            throw new RuntimeException("pattern should not be null");
        }
        addAction(new ActionWith(name, pattern));
        return this;
    }

    @Override
    public final org.mwg.task.Task selectWithout(String name, String pattern) {
        if (pattern == null) {
            throw new RuntimeException("pattern should not be null");
        }
        addAction(new ActionWithout(name, pattern));
        return this;
    }

    @Override
    public final org.mwg.task.Task asGlobalVar(String variableName) {
        if (variableName == null) {
            throw new RuntimeException("variableName should not be null");
        }
        addAction(new ActionAsVar(variableName, true));
        return this;
    }

    @Override
    public final org.mwg.task.Task asVar(String variableName) {
        if (variableName == null) {
            throw new RuntimeException("variableName should not be null");
        }
        addAction(new ActionAsVar(variableName, false));
        return this;
    }

    @Override
    public final org.mwg.task.Task defineVar(String variableName) {
        if (variableName == null) {
            throw new RuntimeException("variableName should not be null");
        }
        addAction(new ActionDefineVar(variableName));
        return this;
    }

    @Override
    public Task addToGlobalVar(String variableName) {
        if (variableName == null) {
            throw new RuntimeException("variableName should not be null");
        }
        addAction(new ActionAddToVar(variableName, true));
        return this;
    }

    @Override
    public Task addToVar(String variableName) {
        if (variableName == null) {
            throw new RuntimeException("variableName should not be null");
        }
        addAction(new ActionAddToVar(variableName, false));
        return this;
    }

    @Override
    public final org.mwg.task.Task fromVar(String variableName) {
        if (variableName == null) {
            throw new RuntimeException("variableName should not be null");
        }
        addAction(new ActionFromVar(variableName, -1));
        return this;
    }

    @Override
    public final org.mwg.task.Task fromVarAt(String variableName, int index) {
        if (variableName == null) {
            throw new RuntimeException("variableName should not be null");
        }
        addAction(new ActionFromVar(variableName, index));
        return this;
    }

    @Override
    public final org.mwg.task.Task select(TaskFunctionSelect filter) {
        if (filter == null) {
            throw new RuntimeException("filter should not be null");
        }
        addAction(new ActionSelect(null,filter));
        return this;
    }

    @Override
    public Task selectScript(String script) {
        if (script == null) {
            throw new RuntimeException("filter should not be null");
        }
        addAction(new ActionSelect(script,null));
        return this;
    }

    @Override
    public final Task selectObject(TaskFunctionSelectObject filterFunction) {
        if (filterFunction == null) {
            throw new RuntimeException("filterFunction should not be null");
        }
        addAction(new ActionSelectObject(filterFunction));
        return this;
    }

    @Override
    public final org.mwg.task.Task selectWhere(org.mwg.task.Task subTask) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public final Task get(String name) {
        addAction(new ActionGet(name));
        return this;
    }

    @Override
    public final org.mwg.task.Task traverse(String relationName) {
        addAction(new ActionTraverse(relationName));
        return this;
    }

    @Override
    public Task traverseTimeRange(String from, String to) {
        addAction(new ActionTraverseTimeRange(from,to));
        return this;
    }

    @Override
    public final Task traverseOrKeep(String relationName) {
        addAction(new ActionTraverseOrKeep(relationName));
        return this;
    }

    @Override
    public final org.mwg.task.Task traverseIndex(String indexName, String... queryParams) {
        if (indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        if (queryParams.length % 2 != 0) {
            throw new RuntimeException("The number of arguments in the queryParams MUST be even, because it should be a sequence of \"key\",\"value\". Current size: " + queryParams.length);
        }
        addAction(new ActionTraverseIndex(indexName, queryParams));
        return this;
    }

    @Override
    public final org.mwg.task.Task traverseIndexAll(String indexName) {
        if (indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        addAction(new ActionTraverseIndexAll(indexName));
        return this;
    }

    @Override
    public final org.mwg.task.Task map(TaskFunctionMap mapFunction) {
        if (mapFunction == null) {
            throw new RuntimeException("mapFunction should not be null");
        }
        addAction(new ActionMap(mapFunction));
        return this;
    }

    @Override
    public final org.mwg.task.Task group(TaskFunctionGroup groupFunction) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public final org.mwg.task.Task groupWhere(Task groupSubTask) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public final org.mwg.task.Task inject(Object inputValue) {
        if (inputValue == null) {
            throw new RuntimeException("inputValue should not be null");

        }
        addAction(new ActionInject(inputValue));
        return this;
    }

    @Override
    public final org.mwg.task.Task subTask(Task subTask) {
        if (subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionSubTask(subTask));
        return this;
    }

    @Override
    public Task isolate(Task subTask) {
        if (subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionIsolate(subTask));
        return this;
    }

    @Override
    public Task subTasks(Task[] subTasks) {
        if (subTasks == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionSubTasks(subTasks));
        return this;
    }

    @Override
    public Task subTasksPar(Task[] subTasks) {
        if (subTasks == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionSubTasksPar(subTasks));
        return this;
    }

    @Override
    public final org.mwg.task.Task ifThen(TaskFunctionConditional cond, Task then) {
        if (cond == null) {
            throw new RuntimeException("condition should not be null");
        }
        if (then == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionIfThen(cond, then));
        return this;
    }

    @Override
    public Task ifThenElse(TaskFunctionConditional cond, Task thenSub, Task elseSub) {
        if (cond == null) {
            throw new RuntimeException("condition should not be null");
        }
        if (thenSub == null) {
            throw new RuntimeException("thenSub should not be null");
        }
        if (elseSub == null) {
            throw new RuntimeException("elseSub should not be null");
        }
        addAction(new ActionIfThenElse(cond, thenSub, elseSub));
        return this;
    }

    @Override
    public final org.mwg.task.Task whileDo(TaskFunctionConditional cond, org.mwg.task.Task then) {
        addAction(new ActionWhileDo(cond, then));
        return this;
        //throw new RuntimeException("Not implemented yet");
    }

    @Override
    public final org.mwg.task.Task doWhile(Task then, TaskFunctionConditional cond) {
        addAction(new ActionDoWhile(then, cond));
        return this;
    }

    @Override
    public final org.mwg.task.Task then(Action p_action) {
        if (p_action == null) {
            throw new RuntimeException("action should not be null");
        }
        addAction(new ActionWrapper(p_action));
        return this;
    }

    @Override
    public final org.mwg.task.Task foreach(Task subTask) {
        if (subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionForeach(subTask));
        return this;
    }

    @Override
    public Task flatmap(Task subTask) {
        if (subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionFlatmap(subTask));
        return this;
    }

    @Override
    public final org.mwg.task.Task foreachPar(Task subTask) {
        if (subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionForeachPar(subTask));
        return this;
    }

    @Override
    public Task flatmapPar(Task subTask) {
        if (subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        addAction(new ActionFlatmapPar(subTask));
        return this;
    }

    @Override
    public Task save() {
        addAction(new ActionSave());
        return this;
    }

    @Override
    public Task clear() {
        addAction(new ActionClear());
        return this;
    }

    @Override
    public Task lookup(String nodeId) {
        addAction(new ActionLookup(nodeId));
        return this;
    }

    @Override
    public Task lookupAll(String nodeId) {
        addAction(new ActionLookupAll(nodeId));
        return this;
    }

    @Override
    public void execute(final Graph graph, final Callback<TaskResult> callback) {
        executeWith(graph, null, callback);
    }

    @Override
    public TaskResult executeSync(final Graph graph) {
        DeferCounterSync waiter = graph.newSyncCounter(1);
        executeWith(graph, null, waiter.wrap());
        return (TaskResult) waiter.waitResult();
    }

    @Override
    public void executeWith(final Graph graph, final Object initial, final Callback<TaskResult> callback) {
        if (_first != null) {
            final TaskResult initalRes;
            if (initial instanceof CoreTaskResult) {
                initalRes = ((TaskResult) initial).clone();
            } else {
                initalRes = new CoreTaskResult(initial, true);
            }
            TaskHook hook = null;
            if (_hookFactory != null) {
                hook = _hookFactory.newHook();
            } else if (graph.taskHookFactory() != null) {
                hook = graph.taskHookFactory().newHook();
            }
            final CoreTaskContext context = new CoreTaskContext(null, initalRes, graph, hook, callback);
            graph.scheduler().dispatch(SchedulerAffinity.SAME_THREAD, new Job() {
                @Override
                public void run() {
                    context.execute(_first);
                }
            });
        } else {
            if (callback != null) {
                callback.on(emptyResult());
            }
        }
    }

    @Override
    public TaskContext prepareWith(Graph graph, Object initial, Callback<TaskResult> callback) {
        final TaskResult initalRes;
        if (initial instanceof CoreTaskResult) {
            initalRes = ((TaskResult) initial).clone();
        } else {
            initalRes = new CoreTaskResult(initial, true);
        }
        TaskHook hook = null;
        if (_hookFactory != null) {
            hook = _hookFactory.newHook();
        } else if (graph.taskHookFactory() != null) {
            hook = graph.taskHookFactory().newHook();
        }
        return new CoreTaskContext(null, initalRes, graph, hook, callback);
    }

    @Override
    public void executeUsing(final TaskContext preparedContext) {
        if (_first != null) {
            preparedContext.graph().scheduler().dispatch(SchedulerAffinity.SAME_THREAD, new Job() {
                @Override
                public void run() {
                    ((CoreTaskContext) preparedContext).execute(_first);
                }
            });
        } else {
            CoreTaskContext casted = (CoreTaskContext) preparedContext;
            if (casted._callback != null) {
                casted._callback.on(emptyResult());
            }
        }
    }

    @Override
    public void executeFrom(final TaskContext parentContext, final TaskResult initial, byte affinity, final Callback<TaskResult> callback) {
        if (_first != null) {
            final CoreTaskContext context = new CoreTaskContext(parentContext, initial.clone(), parentContext.graph(), parentContext.hook(), callback);
            parentContext.graph().scheduler().dispatch(affinity, new Job() {
                @Override
                public void run() {
                    context.execute(_first);
                }
            });
        } else {
            if (callback != null) {
                callback.on(emptyResult());
            }
        }
    }

    @Override
    public void executeFromUsing(TaskContext parentContext, TaskResult initial, byte affinity, Callback<TaskContext> contextInitializer, Callback<TaskResult> callback) {
        if (_first != null) {
            final CoreTaskContext context = new CoreTaskContext(parentContext, initial.clone(), parentContext.graph(), parentContext.hook(), callback);
            if (contextInitializer != null) {
                contextInitializer.on(context);
            }
            parentContext.graph().scheduler().dispatch(affinity, new Job() {
                @Override
                public void run() {
                    context.execute(_first);
                }
            });
        } else {
            if (callback != null) {
                callback.on(emptyResult());
            }
        }
    }

    @Override
    public Task action(String name, String flatParams) {
        if (name == null) {
            throw new RuntimeException("name should not be null");
        }
        if (flatParams == null) {
            throw new RuntimeException("flatParams should not be null");
        }
        addAction(new ActionPlugin(name, flatParams));
        return this;
    }

    @Override
    public Task parse(final String flat) {
        if (flat == null) {
            throw new RuntimeException("flat should not be null");
        }
        int cursor = 0;
        int flatSize = flat.length();
        int previous = 0;
        String actionName = null;
        boolean isClosed = false;
        boolean isEscaped = false;
        while (cursor < flatSize) {
            char current = flat.charAt(cursor);
            switch (current) {
                case '\'':
                    isEscaped = true;
                    while (cursor < flatSize) {
                        if (flat.charAt(cursor) == '\'') {
                            break;
                        }
                        cursor++;
                    }
                    break;
                case Constants.TASK_SEP:
                    if (!isClosed) {
                        String getName = flat.substring(previous, cursor);
                        action("get", getName);//default action
                    }
                    actionName = null;
                    isEscaped = false;
                    previous = cursor + 1;
                    break;
                case Constants.TASK_PARAM_OPEN:
                    actionName = flat.substring(previous, cursor);
                    previous = cursor + 1;
                    break;
                case Constants.TASK_PARAM_CLOSE:
                    //ADD LAST PARAM
                    String extracted;
                    if (isEscaped) {
                        extracted = flat.substring(previous + 1, cursor - 1);
                    } else {
                        extracted = flat.substring(previous, cursor);
                    }
                    action(actionName, extracted);
                    actionName = null;
                    previous = cursor + 1;
                    isClosed = true;
                    //ADD TASK
                    break;
            }
            cursor++;
        }
        if (!isClosed) {
            String getName = flat.substring(previous, cursor);
            if (getName.length() > 0) {
                action("get", getName);//default action
            }
        }
        return this;
    }

    @Override
    public Task newNode() {
        addAction(new ActionNewNode(null));
        return this;
    }

    @Override
    public Task newTypedNode(String typeNode) {
        addAction(new ActionNewNode(typeNode));
        return this;
    }

    @Override
    public Task setProperty(String propertyName, byte propertyType, String variableNameToSet) {
        if (propertyName == null) {
            throw new RuntimeException("propertyName should not be null");
        }
        if (variableNameToSet == null) {
            throw new RuntimeException("variableNameToSet should not be null");
        }
        addAction(new ActionSetProperty(propertyName, propertyType, variableNameToSet, false));
        return this;
    }

    @Override
    public Task forceProperty(String propertyName, byte propertyType, String variableNameToSet) {
        if (propertyName == null) {
            throw new RuntimeException("propertyName should not be null");
        }
        if (variableNameToSet == null) {
            throw new RuntimeException("variableNameToSet should not be null");
        }
        addAction(new ActionSetProperty(propertyName, propertyType, variableNameToSet, true));
        return this;
    }

    @Override
    public Task removeProperty(String propertyName) {
        if (propertyName == null) {
            throw new RuntimeException("propertyName should not be null");
        }
        addAction(new ActionRemoveProperty(propertyName));
        return this;
    }

    @Override
    public Task add(String relationName, String variableNameToAdd) {
        if (relationName == null) {
            throw new RuntimeException("relationName should not be null");
        }
        if (variableNameToAdd == null) {
            throw new RuntimeException("variableNameToAdd should not be null");
        }
        addAction(new ActionAdd(relationName, variableNameToAdd));
        return this;
    }

    @Override
    public Task addTo(String relationName, String variableNameTarget) {
        if (relationName == null) {
            throw new RuntimeException("relationName should not be null");
        }
        if (variableNameTarget == null) {
            throw new RuntimeException("variableNameTarget should not be null");
        }
        addAction(new ActionAddTo(relationName, variableNameTarget));
        return this;
    }

    @Override
    public Task propertiesWithTypes(byte filter) {
        addAction(new ActionProperties(filter));
        return this;
    }

    @Override
    public Task properties() {
        addAction(new ActionProperties((byte) -1));
        return this;
    }

    @Override
    public Task remove(String relationName, String variableNameToRemove) {
        if (relationName == null) {
            throw new RuntimeException("relationName should not be null");
        }
        if (variableNameToRemove == null) {
            throw new RuntimeException("variableNameToRemove should not be null");
        }
        addAction(new ActionRemove(relationName, variableNameToRemove));
        return this;
    }

    @Override
    public Task jump(String time) {
        if (time == null) {
            throw new RuntimeException("time should not be null");
        }
        addAction(new ActionJump(time));
        return this;
    }

    @Override
    public Task math(String expression) {
        addAction(new ActionMath(expression));
        return this;
    }

    @Override
    public Task split(String splitPattern) {
        addAction(new ActionSplit(splitPattern));
        return this;
    }

    @Override
    public Task loop(String from, String to, Task subTask) {
        addAction(new ActionLoop(from, to, subTask));
        return this;
    }

    @Override
    public Task loopPar(String from, String to, Task subTask) {
        addAction(new ActionLoopPar(from, to, subTask));
        return this;
    }

    @Override
    public Task print(String name) {
        addAction(new ActionPrint(name,false));
        return this;
    }

    @Override
    public Task println(String name) {
        addAction(new ActionPrint(name,true));
        return this;
    }

    @Override
    public Task hook(final TaskHookFactory p_hookFactory) {
        this._hookFactory = p_hookFactory;
        return this;
    }

    @Override
    public TaskResult emptyResult() {
        return new CoreTaskResult(null, false);
    }

    @Override
    public TaskFunctionConditional mathConditional(String mathExpression) {
        return new MathConditional(mathExpression).conditional();
    }

    public static void fillDefault(Map<String, TaskActionFactory> registry) {
        registry.put("get", new TaskActionFactory() { //DefaultTask
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("get action need one parameter");
                }
                return new ActionGet(params[0]);
            }
        });
        registry.put("math", new TaskActionFactory() { //DefaultTask
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("math action need one parameter");
                }
                return new ActionMath(params[0]);
            }
        });
        registry.put("traverse", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("traverse action need one parameter");
                }
                return new ActionTraverse(params[0]);
            }
        });
        registry.put("traverseOrKeep", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("traverseOrKeep action need one parameter");
                }
                return new ActionTraverseOrKeep(params[0]);
            }
        });
        registry.put("fromIndexAll", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("fromIndexAll action need one parameter");
                }
                return new ActionFromIndexAll(params[0]);
            }
        });
        registry.put("fromIndex", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("fromIndex action need two parameter");
                }
                return new ActionFromIndex(params[0], params[1]);
            }
        });
        registry.put("with", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("with action need two parameter");
                }
                return new ActionWith(params[0], params[1]);
            }
        });
        registry.put("without", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("without action need two parameter");
                }
                return new ActionWithout(params[0], params[1]);
            }
        });

        registry.put("without", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("without action need two parameter");
                }
                return new ActionWithout(params[0], params[1]);
            }
        });

        registry.put("println", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("println action need one parameter");
                }
                return new ActionPrint(params[0],true);
            }
        });

        registry.put("setProperty", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 3) {
                    throw new RuntimeException("setProperty action need three parameter");
                }
                int i = TaskHelper.parseInt(params[1]);
                return new ActionSetProperty(params[0],(byte) i ,params[2],false);
            }
        });

        registry.put("lookup", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("lookup action one three parameter");
                }
                return new ActionLookup(params[0]);
            }
        });

        registry.put("jump", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("jump action need three parameter");
                }
                return new ActionJump(params[0]);
            }
        });

        registry.put("save", new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 0) {
                    throw new RuntimeException("save action does not need parameter");
                }
                return new ActionSave();
            }
        });


    }

}
