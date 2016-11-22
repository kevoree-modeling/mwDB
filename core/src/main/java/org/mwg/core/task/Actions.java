package org.mwg.core.task;

import org.mwg.Type;
import org.mwg.core.task.math.MathConditional;
import org.mwg.task.*;

public class Actions {

    //Context manipulation zone

    /**
     * Sets the task context to a particular world.
     *
     * @param variableName to be set into the task context and will be used for next sub tasks.
     * @return this task to chain actions (fluent API)
     */
    public static Action setWorld(String variableName) {
        return new ActionSetWorld(variableName);
    }

    /**
     * Sets the task context to a particular time.
     *
     * @param variableName that hasField to be set into the task context and will be used for next sub tasks
     * @return this task to chain actions (fluent API)
     */
    public static Action setTime(String variableName) {
        return new ActionSetTime(variableName);
    }

    /**
     * Jump the node , or the array of nodes, in the result to the given time
     *
     * @param time Time to jump for each nodes
     * @return this task to chain actions (fluent API)
     */
    public static Action jump(String time) {
        return new ActionJump(time);
    }

    /**
     * Inject external object to current task
     *
     * @param input object to be injected
     * @return this task to chain actions (fluent API)
     */
    public static Action inject(Object input) {
        return new ActionInject(input);
    }

    /**
     * Initialise a new scope context for a variable (copy the parent and isolate all set, such as re-definition in imperative languages)
     *
     * @param name identifier of this result
     * @return this task to chain actions (fluent API)
     */
    public static Action defineAsGlobalVar(String name) {
        return new ActionDefineAsVar(name, true);
    }

    /**
     * Initialise a new scope context for a variable (copy the parent and isolate all set, such as re-definition in imperative languages)
     *
     * @param variableName identifier of this result
     * @return this task to chain actions (fluent API)
     */
    public static Action defineAsVar(String variableName) {
        return new ActionDefineAsVar(variableName, false);
    }

    /**
     * Declare a new global variable. Every store instructions past this point using this varName will be stored globally.
     *
     * @param variableName
     * @return this task to chain actions (fluent API)
     */
    public static Action declareGlobalVar(String variableName) {
        return new ActionDeclareGlobalVar(variableName);
    }

    /**
     * Declare a new local variable.
     *
     * @param variableName
     * @return this task to chain actions (fluent API)
     */
    public static Action declareVar(String variableName) {
        return new ActionDeclareVar(variableName);
    }

    /**
     * Retrieves a stored variable. To reach a particular index, classic array notation can be used.
     * Therefore A[B] will be interpreted as: extract value stored at index B from the variable stored at name A.
     *
     * @param name interpreted as a template
     * @return this task to chain actions (fluent API)
     */
    public static Action readVar(String name) {
        return new ActionReadVar(name);
    }

    /**
     * Stores the current task result into a named variable
     *
     * @param variableName identifier of this result
     * @return this task to chain actions (fluent API)
     */
    public static Action setAsVar(String variableName) {
        return new ActionSetAsVar(variableName);
    }

    /**
     * Add the current task result to the named variable
     *
     * @param variableName identifier of this result
     * @return this task to chain actions (fluent API)
     */
    public static Action addToVar(String variableName) {
        return new ActionAddToVar(variableName);
    }

    //Attribute manipulation zone

    /**
     * Sets the value of an attribute for all node present in the current result.
     * If value is similar to previously stored one, nodes will remain unmodified.
     *
     * @param name  Must be unique per node.
     * @param type  Must be one of {@link Type} int value.
     * @param value Will be interpreted as template.
     * @return this task to chain actions (fluent API)
     */
    public static Action set(String name, byte type, String value) {
        return new ActionSet(name, type, value, false);
    }

    /**
     * Force the value of an attribute for all node present in the current result.
     * If value is similar to previously stored one, nodes will still be modified and their timeline will be affected.
     *
     * @param name  Must be unique per node.
     * @param type  Must be one of {@link Type} int value.
     * @param value Will be interpreted as template.
     * @return this task to chain actions (fluent API)
     */
    public static Action force(String name, byte type, String value) {
        return new ActionSet(name, type, value, true);
    }

    /**
     * Removes an attribute from a node or an array of nodes.
     * The node (or the array) should be init in the previous task
     *
     * @param name The name of the attribute to remove.
     * @return this task to chain actions (fluent API)
     */
    public static Action remove(String name) {
        return new ActionRemove(name);
    }

    /**
     * Get all the attributes names of nodes present in the previous result
     *
     * @return this task to chain actions (fluent API)
     */
    public static Action attributes() {
        return new ActionAttributes((byte) -1);
    }

    /**
     * Get and filter all the attributes names of nodes present in the previous result. <br>
     *
     * @param filterType type of attributes to filter
     * @return this task to chain actions (fluent API)
     */
    public static Action attributesWithTypes(byte filterType) {
        return new ActionAttributes(filterType);
    }

    /**
     * Add nodes present in the named variable from the named relationship, in all nodes present in current result.
     *
     * @param name    The name of the relation.
     * @param varFrom will be interpreted as a template.
     * @return this task to chain actions (fluent API)
     */
    public static Action addVarToRelation(String name, String varFrom, String... attributes) {
        return new ActionAddRemoveVarToRelation(true, name, varFrom, attributes);
    }

    /**
     * Remove nodes present in the named variable from the named relationship, in all nodes present in current result.
     *
     * @param name    The name of the relation.
     * @param varFrom will be interpreted as a template.
     * @return this task to chain actions (fluent API)
     */
    public static Action removeVarFromRelation(String name, String varFrom, String... attributes) {
        return new ActionAddRemoveVarToRelation(false, name, varFrom, attributes);
    }

    /**
     * Retrieve any attribute/relationship of nodes presents in current result.
     *
     * @param name of property to retrieve
     * @return this task to chain actions (fluent API)
     */
    public static Action get(String name, String... params) {
        return new ActionGet(name, params);
    }

    //Index manipulation zone

    /**
     * Retrieves all nodes from a named index
     *
     * @param indexName name of the index
     * @return this task to chain actions (fluent API)
     */
    public static Action readGlobalIndexAll(String indexName) {
        return new ActionReadGlobalIndexAll(indexName);
    }

    /**
     * Retrieves indexed nodes that matches the query
     *
     * @param indexName name of the index to use
     * @param query     query to filter nodes, such as name=FOO
     * @return this task to chain actions (fluent API)
     */
    public static Action readGlobalIndex(String indexName, String query) {
        return new ActionReadGlobalIndex(indexName, query);
    }

    public static Action addToGlobalIndex(String name, String... attributes) {
        return new ActionAddToGlobalIndex(name, attributes);
    }


    /**
     * Get all the index names
     *
     * @return this task to chain actions (fluent API)x
     */
    public static Action indexNames() {
        return new ActionIndexNames();
    }

    /**
     * Filters the previous result to keep nodes which named attribute has a specific value
     *
     * @param name    the name of the attribute used to filter
     * @param pattern the value nodes must have for this attribute
     * @return this task to chain actions (fluent API)
     */
    public static Action selectWith(String name, String pattern) {
        return new ActionWith(name, pattern);
    }

    /**
     * Filters the previous result to keep nodes which named attribute do not have a given value
     *
     * @param name    the name of the attribute used to filter
     * @param pattern the value nodes must not have for this attribute
     * @return this task to chain actions (fluent API)
     */
    public static Action selectWithout(String name, String pattern) {
        return new ActionWithout(name, pattern);
    }

    /**
     * Filters the previous result to get nodes that complies to the condition specified in {@code filterFunction}
     *
     * @param filterFunction condition that nodes have to respect
     * @return this task to chain actions (fluent API)
     */
    public static Action select(TaskFunctionSelect filterFunction) {
        return new ActionSelect(filterFunction);
    }

    /**
     * Selects an object complying to the filter function.
     *
     * @param filterFunction condition that objects have to respect
     * @return this task to chain actions (fluent API)
     */
    public static Action selectObject(TaskFunctionSelectObject filterFunction) {
        return new ActionSelectObject(filterFunction);
    }

    //Helper zone

    public static Action print(String name) {
        return new ActionPrint(name, false);
    }

    public static Action println(String name) {
        return new ActionPrint(name, false);
    }


    //Execution manipulation zone

    /**
     * Execute a executeExpression expression on all nodes given from previous step
     *
     * @param expression executeExpression expression to execute
     * @return this task to chain actions (fluent API)
     */
    public static Action executeExpression(String expression) {
        return new ActionExecuteExpression(expression);
    }

    /**
     * Build a named action, based on the task registry.
     * This allows to extend task API with your own DSL.
     *
     * @param name   designation of the task to add, should correspond to the name of the Task plugin registered.
     * @param params parameters of the newly created task
     * @return this task to chain actions (fluent API)
     */
    public static Action pluginAction(String name, String params) {
        return new ActionPlugin(name, params);
    }

    /**
     * Create a new node on the [world,time] of the context
     *
     * @return this task to chain actions (fluent API)
     */
    public static Action createNode() {
        return new ActionCreateNode(null);
    }

    /**
     * Create a new typed node on the [world,time] of the context
     *
     * @param nodeType the type name of the node
     * @return this task to chain actions (fluent API)
     */
    public static Action createTypedNode(String nodeType) {
        return new ActionCreateNode(nodeType);
    }

    public static Action save() {
        return new ActionSave();
    }

    public static Action lookup(String nodeId) {
        return new ActionLookup(nodeId);
    }

    public static Action lookupAll(String nodeIds) {
        return new ActionLookupAll(nodeIds);
    }

    public static Action clearResult() {
        return new ActionClearResult();
    }

    public static TaskFunctionConditional cond(String mathExpression) {
        return new MathConditional(mathExpression).conditional();
    }

    public static Task task() {
        return new CoreTask();
    }

    public static TaskResult emptyResult() {
        return new CoreTaskResult(null, false);
    }

}
