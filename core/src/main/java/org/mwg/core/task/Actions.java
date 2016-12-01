package org.mwg.core.task;

import org.mwg.Type;
import org.mwg.core.task.math.MathConditional;
import org.mwg.task.*;

public class Actions {

    //Context manipulation zone

    /**
     * Sets the task context to a particular world.
     *
     * @param world the world to which the task context will be set to
     * @return the action to chain
     */
    public static Action setWorld(String world) {
        return new ActionSetWorld(world);
    }

    /**
     * Sets the task context to a particular time.
     *
     * @param time the time to which the task context will be set to
     * @return the action to chain
     */
    public static Action setTime(String time) {
        return new ActionSetTime(time);
    }

    /**
     * Switches the time of the task context, i.e. travels the task context in time.
     *
     * @param time time to which the task context is switched to for all nodes
     * @return the action to chain
     */
    public static Action travelInTime(String time) {
        return new ActionTravelInTime(time);
    }

    /**
     * Injects an external object to the current task context.
     *
     * @param input object to be injected into the task context
     * @return the action to chain
     */
    public static Action inject(Object input) {
        return new ActionInject(input);
    }

    /**
     * Stores the task result as a global variable in the task context and starts a new scope (for sub tasks).
     *
     * @param name of the global variable
     * @return the action to chain
     */
    public static Action defineAsGlobalVar(String name) {
        return new ActionDefineAsVar(name, true);
    }

    /**
     * Stores the task result as a local variable in the task context and starts a new scope (for sub tasks).
     *
     * @param name name of the local variable
     * @return the action to chain
     */
    public static Action defineAsVar(String name) {
        return new ActionDefineAsVar(name, false);
    }

    /**
     * Declares a new global variable.
     *
     * @param name of the global variable
     * @return the action to chain
     */
    public static Action declareGlobalVar(String name) {
        return new ActionDeclareGlobalVar(name);
    }

    /**
     * Declare a new local variable.
     *
     * @param name of the local variable
     * @return the action to chain
     */
    public static Action declareVar(String name) {
        return new ActionDeclareVar(name);
    }

    /**
     * Retrieves a stored variable. To reach a particular index, a default array notation can be used.
     * Therefore, A[B] will be interpreted as: extract value stored at index B from the variable A.
     *
     * @param name interpreted as a template
     * @return the action to chain
     */
    public static Action readVar(String name) {
        return new ActionReadVar(name);
    }

    /**
     * Stores the current task result into a named variable without starting a new scope.
     *
     * @param name of the variable
     * @return the action to chain
     */
    public static Action setAsVar(String name) {
        return new ActionSetAsVar(name);
    }

    /**
     * Adds the current task result to the named variable.
     *
     * @param name of the variable
     * @return the action to chain
     */
    public static Action addToVar(String name) {
        return new ActionAddToVar(name);
    }

    //Attribute manipulation zone

    /**
     * Sets the value of an attribute for all nodes present in the current result.
     * If value is similar to the previously stored one, nodes will remain unmodified.
     *
     * @param name  must be unique per node
     * @param type  must be one of {@link Type} int values
     * @param value will be interpreted as a template
     * @return the action to chain
     */
    public static Action set(String name, byte type, String value) {
        return new ActionSet(name, type, value, false);
    }

    /**
     * Forces the value of an attribute for all nodes present in the current result.
     * If value is similar to the previously stored one, nodes will still be modified and their timeline will be affected.
     *
     * @param name  must be unique per node
     * @param type  must be one of {@link Type} int values
     * @param value will be interpreted as template
     * @return the action to chain
     */
    public static Action force(String name, byte type, String value) {
        return new ActionSet(name, type, value, true);
    }

    /**
     * Removes an attribute from a node or an array of nodes.
     * The node (or the array) must be initialised in the previous task
     *
     * @param name the name of the attribute to remove
     * @return the action to chain
     */
    public static Action remove(String name) {
        return new ActionRemove(name);
    }

    /**
     * Retrieves all attribute names of nodes present in the previous task result.
     *
     * @return the action to chain
     */
    public static Action attributes() {
        return new ActionAttributes((byte) -1);
    }

    /**
     * Gets and filters all attribute names of nodes present in the previous result.
     *
     * @param filterType type of attributes to filter
     * @return the action to chain
     */
    public static Action attributesWithTypes(byte filterType) {
        return new ActionAttributes(filterType);
    }

    /**
     * Adds nodes present in the named variable to the named relation in all nodes present in the current task result.
     *
     * @param relName name of the relation
     * @param varName interpreted as a template
     * @return the action to chain
     */
    public static Action addVarToRelation(String relName, String varName, String... attributes) {
        return new ActionAddRemoveVarToRelation(true, relName, varName, attributes);
    }

    /**
     * Removes nodes present in the named variable from the named relation in all nodes present in the current result.
     *
     * @param relName name of the relation.
     * @param varFrom will be interpreted as a template.
     * @return the action to chain
     */
    public static Action removeVarFromRelation(String relName, String varFrom, String... attributes) {
        return new ActionAddRemoveVarToRelation(false, relName, varFrom, attributes);
    }

    /**
     * Retrieves any nodes contained in a relations of the nodes present in the current result.
     *
     * @param name of property to retrieve
     * @return the action to chain
     */
    public static Action traverse(String name, String... params) {
        return new ActionGet(name, params);
    }

    /**
     * Retrieves any attribute(s) contained in the nodes present in the current result.
     *
     * @param name of property to retrieve
     * @return the action to chain
     */
    public static Action attribute(String name, String... params) {
        return new ActionGet(name, params);
    }

    //Index manipulation zone

    /**
     * Retrieves all nodes from a named index.
     *
     * @param indexName name of the index
     * @return the action to chain
     */
    public static Action readGlobalIndexAll(String indexName) {
        return new ActionReadGlobalIndexAll(indexName);
    }

    /**
     * Retrieves indexed nodes matching the query.
     *
     * @param indexName name of the index to use
     * @param query     query to filter nodes, such as name=FOO
     * @return the action to chain
     */
    public static Action readGlobalIndex(String indexName, String query) {
        return new ActionReadGlobalIndex(indexName, query);
    }

    /**
     * Adds properties to the named global index.
     *
     * @param name       of the index
     * @param attributes
     * @return the action to chain
     */
    public static Action addToGlobalIndex(String name, String... attributes) {
        return new ActionAddToGlobalIndex(name, attributes);
    }


    /**
     * Retrieves all index names.
     *
     * @return the action to chain
     */
    public static Action indexNames() {
        return new ActionIndexNames();
    }

    /**
     * Filters the previous result to keep nodes, which named attribute has a specific value.
     *
     * @param name    the name of the attribute used to filter
     * @param pattern the value nodes must have for this attribute
     * @return the action to chain
     */
    public static Action selectWith(String name, String pattern) {
        return new ActionWith(name, pattern);
    }

    /**
     * Filters the previous result to keep nodes, which named attribute does not have a given value.
     *
     * @param name    the name of the attribute used to filter
     * @param pattern the value nodes must not have for this attribute
     * @return the action to chain
     */
    public static Action selectWithout(String name, String pattern) {
        return new ActionWithout(name, pattern);
    }

    /**
     * Filters the previous result to get nodes that comply to the condition specified in {@code filterFunction}
     *
     * @param filterFunction condition that nodes have to respect
     * @return the action to chain
     */
    public static Action select(TaskFunctionSelect filterFunction) {
        return new ActionSelect(null, filterFunction);
    }

    /**
     * Selects an object complying to the filter function.
     *
     * @param filterFunction condition that objects have to respect
     * @return the action to chain
     */
    public static Action selectObject(TaskFunctionSelectObject filterFunction) {
        return new ActionSelectObject(filterFunction);
    }

    //Helper zone

    /**
     * Prints the action in a human readable format (without line breaks).
     *
     * @param name of the action
     * @return the action to chain
     */
    public static Action print(String name) {
        return new ActionPrint(name, false);
    }

    /**
     * Prints the action in a human readable format (with line breaks).
     *
     * @param name of the action
     * @return the action to chain
     */
    public static Action println(String name) {
        return new ActionPrint(name, true);
    }


    //Execution manipulation zone

    /**
     * Executes an expression on all nodes given from the previous step
     *
     * @param expression the to execute
     * @return the action to chain
     */
    public static Action executeExpression(String expression) {
        return new ActionExecuteExpression(expression);
    }

    /**
     * Registers a named action to the action registry.
     * This hook allows to extend the Task API with additional actions.
     *
     * @param name   name of the action to add, should correspond to the name of the registered Task plugin.
     * @param params parameters of the action
     * @return the action to chain
     */
    public static Action pluginAction(String name, String params) {
        return new ActionPlugin(name, params);
    }

    /**
     * Creates a new node in the [world,time] of the current context.
     *
     * @return the action to chain
     */
    public static Action createNode() {
        return new ActionCreateNode(null);
    }

    /**
     * Creates a new typed node in the [world,time] of the current context.
     *
     * @param type the type name of the node
     * @return the action to chain
     */
    public static Action createTypedNode(String type) {
        return new ActionCreateNode(type);
    }

    /**
     * Saves the graph to a storage.
     *
     * @return the action to chain
     */
    public static Action save() {
        return new ActionSave();
    }

    /**
     * Execute a JS script
     * The task context is inject in the variable 'context'
     * <p>
     * Should only be used to serialize {@link Task#thenDo(ActionFunction)}.
     *
     * @return the action to chain
     */
    public static Action script(String script) {
        return new ActionScript(script);
    }

    /**
     * Use a JS script to filter nodes
     * The task context is inject in the variable 'context'. The current node is inject in the variable 'node'.
     * <p>
     * Should only be used to serialize {@link #select(TaskFunctionSelect)}
     *
     * @return the action to chain
     */
    public static Action selectScript(String script) {
        return new ActionSelect(script, null);
    }

    /**
     * Retrieves in the current [world,time] a named node.
     *
     * @param nodeId id of the node to lookup
     * @return the action to chain
     */
    public static Action lookup(String nodeId) {
        return new ActionLookup(nodeId);
    }

    /**
     * Retrieves in the current [world,time] the named nodes.
     *
     * @param nodeIds ids of the nodes to lookup
     * @return the action to chain
     */
    public static Action lookupAll(String nodeIds) {
        return new ActionLookupAll(nodeIds);
    }

    /**
     * Resets the result of the task context.
     *
     * @return the action to chain
     */
    public static Action clearResult() {
        return new ActionClearResult();
    }


    /**
     * Evaluates a conditional math expression.
     *
     * @param mathExpression the math expression
     * @return the conditional function
     */
    public static ConditionalFunction cond(String mathExpression) {
        return new MathConditional(mathExpression).conditional();
    }

    /**
     * Creates a new task.
     *
     * @return the created task
     */
    public static Task newTask() {
        return new CoreTask();
    }

    /**
     * Creates a new, empty task result.
     *
     * @return an empty task result
     */
    public static TaskResult emptyResult() {
        return new CoreTaskResult(null, false);
    }

    public static Task then(Action action) {
        return newTask().then(action);
    }

    public static Task thenDo(ActionFunction actionFunction) {
        return newTask().thenDo(actionFunction);
    }

    public static Task doWhile(Task task, ConditionalFunction cond) {
        return newTask().doWhile(task, cond);
    }

    public static Task loop(String from, String to, Task subTask) {
        return newTask().loop(from, to, subTask);
    }

    public static Task loopPar(String from, String to, Task subTask) {
        return newTask().loopPar(from, to, subTask);
    }

    public static Task forEach(Task subTask) {
        return newTask().forEach(subTask);
    }

    public static Task forEachPar(Task subTask) {
        return newTask().forEachPar(subTask);
    }

    public static Task flatMap(Task subTask) {
        return newTask().flatMap(subTask);
    }

    public static Task flatMapPar(Task subTask) {
        return newTask().flatMapPar(subTask);
    }

    public static Task ifThen(ConditionalFunction cond, Task then) {
        return newTask().ifThen(cond, then);
    }

    public static Task ifThenElse(ConditionalFunction cond, Task thenSub, Task elseSub) {
        return newTask().ifThenElse(cond, thenSub, elseSub);
    }

    public static Task whileDo(ConditionalFunction cond, Task task) {
        return newTask().whileDo(cond, task);
    }

    public static Task map(Task... subTasks) {
        return newTask().map(subTasks);
    }

    public static Task mapPar(Task... subTasks) {
        return newTask().mapPar(subTasks);
    }

    public static Task isolate(Task subTask) {
        return newTask().isolate(subTask);
    }

    public static Task parse(String flat) {
        return newTask().parse(flat);
    }

}
