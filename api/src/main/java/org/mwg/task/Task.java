package org.mwg.task;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Type;

public interface Task {

    /**
     * Sets the task context to a particular world.
     *
     * @param template to be set into the task context and will be used for next sub tasks.
     * @return this task to chain actions (fluent API)
     */
    Task setWorld(String template);

    /**
     * Sets the task context to a particular time.
     *
     * @param template that hasField to be set into the task context and will be used for next sub tasks
     * @return this task to chain actions (fluent API)
     */
    Task setTime(String template);

    /**
     * Initialise a new scope context for a variable (copy the parent and isolate all set, such as re-definition in imperative languages)
     *
     * @param variableName identifier of this result
     * @return this task to chain actions (fluent API)
     */
    Task defineVar(String variableName);

    /**
     * Stores the current task result into a named variable with a global scope
     *
     * @param variableName identifier of this result
     * @return this task to chain actions (fluent API)
     */
    Task asGlobalVar(String variableName);

    /**
     * Add the current task result to the global named variable
     *
     * @param variableName identifier of this result
     * @return this task to chain actions (fluent API)
     */
    Task addToGlobalVar(String variableName);

    /**
     * Stores the current task result into a named variable with a local scope
     *
     * @param variableName identifier of this result
     * @return this task to chain actions (fluent API)
     */
    Task asVar(String variableName);

    /**
     * Add the current task result to the local named variable
     *
     * @param variableName identifier of this result
     * @return this task to chain actions (fluent API)
     */
    Task addToVar(String variableName);

    /**
     * Retrieves a stored variable and stack it for next sub tasks.
     *
     * @param variableName identifying a variable
     * @return this task to chain actions (fluent API)
     */
    Task fromVar(String variableName);

    /**
     * Retrieves a stored array and stack the element at the specified index for next sub tasks.
     *
     * @param variableName identifying a previous array
     * @param index        the index element in the array
     * @return this task to chain actions (fluent API)
     */
    Task fromVarAt(String variableName, int index);

    /**
     * Method to initialise a task with any object
     *
     * @param inputValue object used as source of a task
     * @return this task to chain actions (fluent API)
     */
    Task inject(Object inputValue);

    /**
     * Retrieves indexed nodes that matches the query
     *
     * @param indexName name of the index to use
     * @param query     query to filter nodes, such as name=FOO
     * @return this task to chain actions (fluent API)
     */
    Task fromIndex(String indexName, String query);

    /**
     * Retrieves all nodes from a named index
     *
     * @param indexName name of the index
     * @return this task to chain actions (fluent API)
     */
    Task fromIndexAll(String indexName);

    /**
     * Index the node (or the array of nodes) present in the result
     *
     * @param indexName         index name
     * @param flatKeyAttributes node attributes used to index
     * @return this task to chain actions (fluent API)
     */
    Task indexNode(String indexName, String flatKeyAttributes);

    /**
     * Index the node (or the array of nodes) present in the result
     *
     * @param indexName         index name
     * @param flatKeyAttributes node attributes used to index
     * @return this task to chain actions (fluent API)
     */
    Task indexNodeAt(String world, String time, String indexName, String flatKeyAttributes);

    /**
     * DRAFT
     * Create or compliments an index of nodes. <br>
     * These indexes are special relationships for quick access to referred nodes based on some of their attributes values.<br>
     * Index names must be unique within a given node.
     */
    Task localIndex(String indexedRelation, String flatKeyAttributes, String varNodeToAdd);

    /**
     * Unindex the node (or the array of nodes) present in the result
     *
     * @param indexName         index name
     * @param flatKeyAttributes node attributes used to index
     * @return this task to chain actions (fluent API)
     */
    Task unindexNodeAt(String world, String time, String indexName, String flatKeyAttributes);

    /**
     * Unindex the node (or the array of nodes) present in the result
     *
     * @param indexName         index name
     * @param flatKeyAttributes node attributes used to index
     * @return this task to chain actions (fluent API)
     */
    Task unindexNode(String indexName, String flatKeyAttributes);

    /**
     * DRAFT
     * Create or compliments an index of nodes. <br>
     * These indexes are special relationships for quick access to referred nodes based on some of their attributes values.<br>
     * Index names must be unique within a given node.
     */
    Task localUnindex(String indexedRelation, String flatKeyAttributes, String varNodeToAdd);

    /**
     * Get all the index names
     *
     * @return this task to chain actions (fluent API)x
     */
    Task indexesNames();

    /**
     * Filters the previous result to keep nodes which named attribute has a specific value
     *
     * @param name    the name of the attribute used to filter
     * @param pattern the value nodes must have for this attribute
     * @return this task to chain actions (fluent API)
     */
    Task selectWith(String name, String pattern);

    /**
     * Filters the previous result to keep nodes which named attribute do not have a given value
     *
     * @param name    the name of the attribute used to filter
     * @param pattern the value nodes must not have for this attribute
     * @return this task to chain actions (fluent API)
     */
    Task selectWithout(String name, String pattern);

    /**
     * Filters the previous result to get nodes that complies to the condition specified in {@code filterFunction}
     * If you want to access/modify the context, please use {@link #selectWhere(Task)}
     *
     * @param filterFunction condition that nodes have to respect
     * @return this task to chain actions (fluent API)
     */
    Task select(TaskFunctionSelect filterFunction);

    Task selectScript(String script);

    /**
     * Selects an object complying to the filter function.
     *
     * @param filterFunction condition that objects have to respect
     * @return this task to chain actions (fluent API)
     */
    Task selectObject(TaskFunctionSelectObject filterFunction);

    /**
     * Filter the previous result to get nodes that respect the specified condition in {@code subTask}
     * Similar to {@link #select(TaskFunctionSelect)}, but allow access/modification of the context
     *
     * @param subTask sub task called to filter the elemen
     * @return this task to chain actions (fluent API)
     */
    Task selectWhere(Task subTask);

    /**
     * Traverse the specified relation
     *
     * @param relationName relation to traverse
     * @return this task to chain actions (fluent API)
     */
    Task traverse(String relationName);

    /**
     * Traverse in times all nodes in current context
     *
     * @return this task to chain actions (fluent API)
     */
    Task traverseTimeRange(String from, String to);

    /**
     * Retrieve any property given a precise name.
     * If the property is a relationship, it is traversed an related nodes are retrieved.
     *
     * @param name of property to retrieve
     * @return this task to chain actions (fluent API)
     */
    Task get(String name);

    /**
     * Traverse the specified relation if not empty, otherwise keep leaf nodes
     *
     * @param relationName relation to traverse if not empty
     * @return this task to chain actions (fluent API)
     */
    Task traverseOrKeep(String relationName);

    /**
     * Traverse a relation indexed by {@code indexName} and retrieve specific node thanks to the {@code query}
     *
     * @param indexName index name of indexed relation
     * @param queryArgs arguments of the query. Must be an even number, in form of: "&lt;att1&gt;","&lt;value1&gt;","&lt;att2&gt;","&lt;value2&gt;"
     * @return this task to chain actions (fluent API)
     */
    Task traverseIndex(String indexName, String... queryArgs);

    /**
     * Traverse a relation indexed by {@code indexName}
     *
     * @param indexName index name of indexed relation
     * @return this task to chain actions (fluent API)
     */
    Task traverseIndexAll(String indexName);

    Task map(TaskFunctionMap mapFunction);

    Task group(TaskFunctionGroup groupFunction);

    Task groupWhere(Task groupSubTask);

    /**
     * Iterate through a collection and calls the sub task for each elements
     *
     * @param subTask sub task to call for each elements
     * @return this task to chain actions (fluent API)
     */
    Task foreach(Task subTask);

    /**
     * Iterate through a collection and calls the sub task for each elements, aggregate all results
     *
     * @param subTask sub task to call for each elements
     * @return this task to chain actions (fluent API)
     */
    Task flatmap(Task subTask);

    /**
     * Same as {@link #foreach(Task)} method, but all the subtask are called in parallel
     * There is thus as thread as element in the collection
     *
     * @param subTask sub task to call for each elements
     * @return this task to chain actions (fluent API)
     */
    Task foreachPar(Task subTask);

    /**
     * Iterate through a collection and calls the sub task for each elements, aggregate all results
     *
     * @param subTask sub task to call for each elements
     * @return this task to chain actions (fluent API)
     */
    Task flatmapPar(Task subTask);

    /**
     * Execute and wait a sub task, result of this sub task is immediately enqueue and available for next
     *
     * @param subTask that have to be executed
     * @return this task to chain actions (fluent API)
     */
    Task subTask(Task subTask);

    Task isolate(Task subTask);

    /**
     * Execute and wait various sub tasks, result of this sub task is immediately enqueue and available for next
     *
     * @param subTasks that have to be executed
     * @return this task to chain actions (fluent API)
     */
    Task subTasks(Task[] subTasks);

    /**
     * Execute and wait various sub tasks, result of this sub task is immediately enqueue and available for next
     *
     * @param subTasks that have to be executed
     * @return this task to chain actions (fluent API)
     */
    Task subTasksPar(Task[] subTasks);

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

    Task doWhile(Task then, TaskFunctionConditional conditional);

    Task then(Action action);

    Task save();

    Task clear();

    /**
     * Create a new node on the [world,time] of the context
     *
     * @return this task to chain actions (fluent API)
     */
    Task newNode();

    /**
     * Create a new typed node on the [world,time] of the context
     *
     * @param typeNode the type name of the node
     * @return this task to chain actions (fluent API)
     */
    Task newTypedNode(String typeNode);

    /**
     * Sets the value of an attribute of a node or an array of nodes with a variable value
     * The node (or the array) should be init in the previous task
     *
     * @param propertyName      The name of the attribute. Must be unique per node.
     * @param propertyType      The type of the attribute. Must be one of {@link Type} int value.
     * @param variableNameToSet The name of the property to set, should be stored previously as a variable in task context.
     * @return this task to chain actions (fluent API)
     */
    Task setProperty(String propertyName, byte propertyType, String variableNameToSet);

    /**
     * Force the value of an attribute of a node or an array of nodes with a variable value
     * The node (or the array) should be init in the previous task
     *
     * @param propertyName      The name of the attribute. Must be unique per node.
     * @param propertyType      The type of the attribute. Must be one of {@link Type} int value.
     * @param variableNameToSet The name of the property to set, should be stored previously as a variable in task context.
     * @return this task to chain actions (fluent API)
     */
    Task forceProperty(String propertyName, byte propertyType, String variableNameToSet);

    /**
     * Removes an attribute from a node or an array of nodes.
     * The node (or the array) should be init in the previous task
     *
     * @param propertyName The name of the attribute to remove.
     * @return this task to chain actions (fluent API)
     */
    Task removeProperty(String propertyName);

    /**
     * Adds a node to a relation of a node or of an array of nodes.
     *
     * @param relationName  The name of the relation.
     * @param variableToAdd The name of the property to add, should be stored previously as a variable in task context.
     * @return this task to chain actions (fluent API)
     */
    Task add(String relationName, String variableToAdd);

    /**
     * Adds a node to a relation of a node or of an array of nodes.
     *
     * @param relationName   The name of the relation.
     * @param variableTarget The name of the property to add, should be stored previously as a variable in task context.
     * @return this task to chain actions (fluent API)
     */
    Task addTo(String relationName, String variableTarget);

    /**
     * Get all the properties names of nodes present in the previous result
     *
     * @return this task to chain actions (fluent API)
     */
    Task properties();

    /**
     * Get and filter all the properties names of nodes present in the previous result. <br>
     *
     * @param filterType type of properties to filter
     * @return this task to chain actions (fluent API)
     */
    Task propertiesWithTypes(byte filterType);

    /**
     * Removes a node from a relation of a node or of an array of nodes.
     *
     * @param relationName         The name of the relation.
     * @param variableNameToRemove The name of the property to add, should be stored previously as a variable in task context.
     * @return this task to chain actions (fluent API)
     */
    Task remove(String relationName, String variableNameToRemove);

    /**
     * Jump the node , or the array of nodes, in the result to the given time
     *
     * @param time Time to jump for each nodes
     * @return this task to chain actions (fluent API)
     */
    Task jump(String time);

    /**
     * Parse a string to build the current task. Syntax is as follow: actionName(param).actionName2(param2)...
     * In case actionName() are empty, default task is get(name).
     * Therefore the following: children.name should be read as get(children).get(name)
     *
     * @param flat string definition of the task
     * @return this task to chain actions (fluent API)
     */
    Task parse(String flat);

    /**
     * Build a named action, based on the task registry.
     * This allows to extend task API with your own DSL.
     *
     * @param name   designation of the task to add, should correspond to the name of the Task plugin registered.
     * @param params parameters of the newly created task
     * @return this task to chain actions (fluent API)
     */
    Task action(String name, String params);

    Task split(String splitPattern);

    Task lookup(String nodeId);

    Task lookupAll(String nodeId);

    /**
     * Execute a math expression on all nodes given from previous step
     *
     * @param expression math expression to execute
     * @return this task to chain actions (fluent API)
     */
    Task math(String expression);

    Task loop(String from, String to, Task subTask);

    Task loopPar(String from, String to, Task subTask);

    Task print(String name);

    Task println(String name);

    Task hook(TaskHookFactory hookFactory);

    void execute(final Graph graph, final Callback<TaskResult> callback);

    TaskResult executeSync(final Graph graph);

    void executeWith(final Graph graph, final Object initial, final Callback<TaskResult> callback);

    TaskContext prepareWith(final Graph graph, final Object initial, final Callback<TaskResult> callback);

    void executeUsing(TaskContext preparedContext);

    void executeFrom(final TaskContext parentContext, final TaskResult initial, final byte affinity, final Callback<TaskResult> callback);

    void executeFromUsing(final TaskContext parentContext, final TaskResult initial, final byte affinity, final Callback<TaskContext> contextInitializer, final Callback<TaskResult> callback);

    TaskResult emptyResult();

    TaskFunctionConditional mathConditional(String mathExpression);

    Task timepoints(String from, String to);
}
