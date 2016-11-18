package org.mwg.task;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Type;

public interface Task {

    /**
     * Chain next action
     *
     * @param nextAction next action to chain
     * @return this task to chain actions (fluent API)
     */
    Task then(Action nextAction);

    /**
     * Chain next action
     *
     * @param nextActionFunction next action function to chain
     * @return this task to chain actions (fluent API)
     */
    Task thenDo(ActionFunction nextActionFunction);

    Task doWhile(Task task, TaskFunctionConditional cond);

    Task loop(String from, String to, Task subTask);

    Task loopPar(String from, String to, Task subTask);

    /**
     * Iterate through a collection and calls the sub task for each elements
     *
     * @param subTask sub task to call for each elements
     * @return this task to chain actions (fluent API)
     */
    Task forEach(Task subTask);

    /**
     * Same as {@link #forEach(Task)} method, but all the subtask are called in parallel
     * There is thus as thread as element in the collection
     *
     * @param subTask sub task to call for each elements
     * @return this task to chain actions (fluent API)
     */
    Task forEachPar(Task subTask);

    /**
     * Iterate through a collection and calls the sub task for each elements, aggregate all results
     *
     * @param subTask sub task to call for each elements
     * @return this task to chain actions (fluent API)
     */
    Task flatMap(Task subTask);

    /**
     * Iterate through a collection and calls the sub task for each elements, aggregate all results
     *
     * @param subTask sub task to call for each elements
     * @return this task to chain actions (fluent API)
     */
    Task flatMapPar(Task subTask);

    /**
     * Execute a sub task if the condition is satisfied
     *
     * @param cond condition to check
     * @param then sub task to execute if the condition is satisfied
     * @return this task to chain actions (fluent API)
     */
    Task ifThen(TaskFunctionConditional cond, Task then);

    /**
     * Execute a sub task if the condition is satisfied
     *
     * @param cond    condition to check
     * @param thenSub sub task to execute if the condition is satisfied
     * @param elseSub sub task to execute if the condition is not satisfied
     * @return this task to chain actions (fluent API)
     */
    Task ifThenElse(TaskFunctionConditional cond, Task thenSub, Task elseSub);

    Task whileDo(TaskFunctionConditional cond, Task then);

    /**
     * Execute and wait various sub tasks, result of this sub task is immediately enqueue and available for next
     *
     * @param subTasks that have to be executed
     * @return this task to chain actions (fluent API)
     */
    Task map(Task... subTasks);

    /**
     * Execute subTask in an isolated environment
     *
     * @param subTask
     * @return
     */
    Task isolate(Task subTask);

    /**
     * Execute and wait various sub tasks, result of this sub task is immediately enqueue and available for next
     *
     * @param subTasks that have to be executed
     * @return this task to chain actions (fluent API)
     */
    Task mapPar(Task... subTasks);

    /**
     * Parse a string to build the current task. Syntax is as follow: actionName(param).actionName2(param2)...
     * In case actionName() are empty, default task is get(name).
     * Therefore the following: children.name should be read as get(children).get(name)
     *
     * @param flat string definition of the task
     * @return this task to chain actions (fluent API)
     */
    Task parse(String flat);

    Task hook(TaskHookFactory hookFactory);

    void execute(final Graph graph, final Callback<TaskResult> callback);

    TaskResult executeSync(final Graph graph);

    void executeWith(final Graph graph, final Object initial, final Callback<TaskResult> callback);

    TaskContext prepareWith(final Graph graph, final Object initial, final Callback<TaskResult> callback);

    void executeUsing(TaskContext preparedContext);

    void executeFrom(final TaskContext parentContext, final TaskResult initial, final byte affinity, final Callback<TaskResult> callback);

    void executeFromUsing(final TaskContext parentContext, final TaskResult initial, final byte affinity, final Callback<TaskContext> contextInitializer, final Callback<TaskResult> callback);

    TaskResult emptyResult();

}
