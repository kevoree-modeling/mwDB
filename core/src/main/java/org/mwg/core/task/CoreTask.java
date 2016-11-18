package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounterSync;
import org.mwg.Graph;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

import java.util.Map;

public class CoreTask implements org.mwg.task.Task {

    public static Task task() {
        return new CoreTask();
    }

    private AbstractAction _first = null;
    private AbstractAction _last = null;
    private TaskHookFactory _hookFactory = null;

    @Override
    public Task then(Action nextAction) {
        if (_first == null) {
            _first = (AbstractAction) nextAction;
            _last = _first;
        } else {
            _last.setNext((AbstractAction) nextAction);
            _last = (AbstractAction) nextAction;
        }
        return this;
    }

    @Override
    public Task thenDo(ActionFunction nextActionFunction) {
        return then(new ActionWrapper(nextActionFunction));
    }

    @Override
    public Task doWhile(Task task, TaskFunctionConditional cond) {
        return then(new ActionDoWhile(task, cond));
    }

    @Override
    public Task loop(String from, String to, Task subTask) {
        return then(new ActionLoop(from, to, subTask));
    }

    @Override
    public Task loopPar(String from, String to, Task subTask) {
        return then(new ActionLoopPar(from, to, subTask));
    }

    @Override
    public Task forEach(Task subTask) {
        return then(new ActionForEach(subTask));
    }

    @Override
    public Task forEachPar(Task subTask) {
        return then(new ActionForEachPar(subTask));
    }

    @Override
    public Task flatMap(Task subTask) {
        return then(new ActionFlatmap(subTask));
    }

    @Override
    public Task flatMapPar(Task subTask) {
        return then(new ActionFlatmap(subTask));
    }

    @Override
    public Task ifThen(TaskFunctionConditional cond, Task then) {
        return then(new ActionIfThen(cond, then));
    }

    @Override
    public Task ifThenElse(TaskFunctionConditional cond, Task thenSub, Task elseSub) {
        return then(new ActionIfThenElse(cond, thenSub, elseSub));
    }

    @Override
    public Task whileDo(TaskFunctionConditional cond, Task then) {
        return then(new ActionWhileDo(cond, then));
    }

    @Override
    public Task map(Task... subTasks) {
        then(new ActionSubTasks(subTasks));
        return this;
    }

    @Override
    public Task isolate(Task subTask) {
        then(new ActionIsolate(subTask));
        return this;
    }


    @Override
    public Task mapPar(Task... subTasks) {
        then(new ActionSubTasksPar(subTasks));
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
                        new ActionPlugin("get", getName);//default action
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
                    new ActionPlugin(actionName, extracted);
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
                new ActionPlugin("get", getName);//default action
            }
        }
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

    public static void fillDefault(Map<String, TaskActionFactory> registry) {
        registry.put("get", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("get action need one parameter");
                }
                return new ActionGet(params[0]);
            }
        });
        registry.put("math", new TaskActionFactory() { //DefaultTask
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("math action need one parameter");
                }
                return new ActionMath(params[0]);
            }
        });
        registry.put("traverse", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("traverse action need one parameter");
                }
                return new ActionTraverse(params[0]);
            }
        });
        registry.put("traverseOrKeep", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("traverseOrKeep action need one parameter");
                }
                return new ActionTraverseOrKeep(params[0]);
            }
        });
        registry.put("fromIndexAll", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("fromIndexAll action need one parameter");
                }
                return new ActionFromIndexAll(params[0]);
            }
        });
        registry.put("fromIndex", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("fromIndex action need two parameter");
                }
                return new ActionFromIndex(params[0], params[1]);
            }
        });
        registry.put("with", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("with action need two parameter");
                }
                return new ActionWith(params[0], params[1]);
            }
        });
        registry.put("without", new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if (params.length != 2) {
                    throw new RuntimeException("without action need two parameter");
                }
                return new ActionWithout(params[0], params[1]);
            }
        });
    }

}
