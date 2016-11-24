package org.mwg.task;

import org.mwg.Callback;
import org.mwg.Graph;

public interface Task {

    /**
     * Chains actions.
     *
     * @param nextAction to chain
     * @return this task to chain
     */
    Task then(Action nextAction);

    /**
     * Chains action functions.
     *
     * @param nextActionFunction next action function to chain
     * @return this task to chain
     */
    Task thenDo(ActionFunction nextActionFunction);

    /**
     * Executes a give task until a given condition evaluates to true.
     *
     * @param task to execute
     * @param cond condition to check
     * @return this task to chain
     */
    Task doWhile(Task task, ConditionalFunction cond);

    /**
     * Executes a task in a range.
     *
     * @param from    range start
     * @param to      range end
     * @param subTask to execute
     * @return this task to chain
     */
    Task loop(String from, String to, Task subTask);

    /**
     * Parallel version of {@link #loop(String, String, Task)}l.
     * Executes a task in a range. Steps can be executed in parallel. Creates as many threads as elements in the collection.
     *
     * @param from    range start
     * @param to      range end
     * @param subTask task to execute
     * @return this task to chain
     */
    Task loopPar(String from, String to, Task subTask);

    /**
     * Iterates through a collection and calls the sub task for each element.
     *
     * @param subTask sub task to call for each element
     * @return this task to chain
     */
    Task forEach(Task subTask);

    /**
     * Parallel version of {@link #forEach(Task)}.
     * All sub tasks can be called in parallel. Creates as many threads as elements in the collection.
     *
     * @param subTask sub task to call for each element
     * @return this task to chain
     */
    Task forEachPar(Task subTask);

    /**
     * Iterates through a collection and calls the sub task for each element and then aggregates all results.
     *
     * @param subTask sub task to call for each element
     * @return this task to chain
     */
    Task flatMap(Task subTask);

    /**
     * Parallel version of {@link #flatMap(Task)}.
     * Iterates through a collection and calls the sub task for each element in parallel and then aggregates all results.
     * Creates as many threads as elements in the collection.
     *
     * @param subTask sub task to call for each element
     * @return this task to chain
     */
    Task flatMapPar(Task subTask);

    /**
     * Executes a sub task if a given condition is evaluated to true.
     *
     * @param cond condition to check
     * @param then sub task to execute if the condition is evaluated to true
     * @return this task to chain
     */
    Task ifThen(ConditionalFunction cond, Task then);

    /**
     * Executes a sub task if a given condition is evaluated to true.
     *
     * @param cond    condition to check
     * @param thenSub sub task to execute if the condition is evaluate to true
     * @param elseSub sub task to execute if the condition is evaluated to false
     * @return this task to chain
     */
    Task ifThenElse(ConditionalFunction cond, Task thenSub, Task elseSub);

    /**
     * Similar to {@link #doWhile(Task, ConditionalFunction)} but the task is at least executed once.
     *
     * @param cond condition to check
     * @param task to execute
     * @return this task to chain
     */
    Task whileDo(ConditionalFunction cond, Task task);

    /**
     * Executes and waits for a number of given sub tasks.
     * The result of these sub tasks is immediately enqueued and available in the next sub task.
     *
     * @param subTasks that needs to be executed
     * @return this task to chain
     */
    Task map(Task... subTasks);

    /**
     * Parallel version of {@link #map(Task...)}.
     * Executes and waits a number of given sub tasks.
     * The result of these sub tasks is immediately enqueued and available in the next sub task.
     *
     * @param subTasks that have to be executed
     * @return this task to chain
     */
    Task mapPar(Task... subTasks);

    /**
     * Executes a given sub task in an isolated environment.
     *
     * @param subTask to execute
     * @return this task to chain
     */
    Task isolate(Task subTask);

    /**
     * Parses a string to build the current task.
     * Syntax is as follows: actionName(param).actionName2(param2)...
     * In case actionName() is empty, the default task is get(name).
     * This results in the following: children.name should be read as get(children).get(name)
     *
     * @param flat string definition of the task
     * @return this task to chain
     */
    Task parse(String flat);

    /**
     * Creates a hook to extend the Task API.
     *
     * @param hook
     * @return this task to chain
     */
    Task addHook(TaskHook hook);

    /**
     * Executes the defined chain of tasks on a graph in an asynchronous way.
     *
     * @param graph    the graph where the execution is applied to
     * @param callback to notify when the chain of tasks has been executed
     */
    void execute(final Graph graph, final Callback<TaskResult> callback);

    /**
     * Synchronous version of {@link #execute(Graph, Callback)}.
     *
     * @param graph where the execcution is applied to
     * @return the task result
     */
    TaskResult executeSync(final Graph graph);

    /**
     * Executes the defined chain of tasks with an initial task result object.
     *
     * @param graph    where the execution is applied to
     * @param initial  initial object of the task result
     * @param callback to notify when the chain of tasks has been executed
     */
    void executeWith(final Graph graph, final Object initial, final Callback<TaskResult> callback);


    /**
     * Prepares a context in order to initialize the task context.
     *
     * @param graph    where the execution is applied to
     * @param initial  initial object for the task context
     * @param callback to notify when the preparation is executed
     * @return the prepared task context
     */
    TaskContext prepare(final Graph graph, final Object initial, final Callback<TaskResult> callback);

    /**
     * Executes a prepared task context.
     * See {@link #prepare(Graph, Object, Callback)}.
     *
     * @param preparedContext the prepared task context
     */
    void executeUsing(TaskContext preparedContext);

    /**
     * @param parentContext
     * @param initial       initial task result
     * @param affinity      defines the thread affinity, see {@link org.mwg.plugin.SchedulerAffinity}
     * @param callback      notifies when the execution is done
     */
    void executeFrom(final TaskContext parentContext, final TaskResult initial, final byte affinity, final Callback<TaskResult> callback);

    /**
     * Executes a prepared task context.
     * Similar to {@link #executeUsing(TaskContext)} but the task context can be initialized within a callback.
     *
     * @param parentContext      context of the parent task
     * @param initial            initial object for the task context
     * @param affinity           defines the thread affinity, see {@link org.mwg.plugin.SchedulerAffinity}
     * @param contextInitializer callback to initialize the context
     * @param callback           notifies when the execution is done
     */
    void executeFromUsing(final TaskContext parentContext, final TaskResult initial, final byte affinity, final Callback<TaskContext> contextInitializer, final Callback<TaskResult> callback);

}
