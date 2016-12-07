package org.mwg.task;

import org.mwg.GraphBuilder;

public class Actions {

    private static GraphBuilder.InternalBuilder _internalBuilder = null;


    /**
     * {@native ts
     * if (org.mwg.task.Actions._internalBuilder == null) {
     * org.mwg.task.Actions._internalBuilder = new org.mwg.core.Builder();
     * }
     * return org.mwg.task.Actions._internalBuilder.newTask();
     * }
     *
     * To be called once all options have been set, to actually create a task instance.
     * @return The new task
     */
    public static Task newTask() {
        if (_internalBuilder == null) {
            try {
                _internalBuilder = (GraphBuilder.InternalBuilder) Actions.class.getClassLoader().loadClass("org.mwg.core.Builder").newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return _internalBuilder.newTask();
    }

    public static Task setWorld(String variableName) {
        return newTask().setWorld(variableName);
    }

    public static Task setTime(String variableName) {
        return newTask().setTime(variableName);
    }

    public static Task then(Action action) {
        return newTask().then(action);
    }

    public static Task inject(Object input) {
        return newTask().inject(input);
    }

    public static Task fromVar(String variableName) {
        return newTask().fromVar(variableName);
    }

    public static Task fromVarAt(String variableName, int index) {
        return newTask().fromVarAt(variableName, index);
    }

    public static Task fromIndexAll(String indexName) {
        return newTask().fromIndexAll(indexName);
    }

    public static Task fromIndex(String indexName, String query) {
        return newTask().fromIndex(indexName, query);
    }

    public static Task indexNode(String indexName, String flatKeyAttributes) {
        return newTask().indexNode(indexName, flatKeyAttributes);
    }

    public static Task unindexNode(String indexName, String flatKeyAttributes) {
        return newTask().unindexNode(indexName, flatKeyAttributes);
    }

    public static Task localIndex(String indexedRelation, String flatKeyAttributes, String varNodeToAdd) {
        return newTask().localIndex(indexedRelation, flatKeyAttributes, varNodeToAdd);
    }

    public static Task localUnindex(String indexedRelation, String flatKeyAttributes, String varNodeToAdd) {
        return newTask().localUnindex(indexedRelation, flatKeyAttributes, varNodeToAdd);
    }

    public static Task indexesNames() {
        return newTask().indexesNames();
    }

    public static Task parse(String flatTask) {
        return newTask().parse(flatTask);
    }

    public static Task asGlobalVar(String variableName) {
        return newTask().asGlobalVar(variableName);
    }

    public static Task addToGlobalVar(String variableName) {
        return newTask().addToGlobalVar(variableName);
    }

    public static Task asVar(String variableName) {
        return newTask().asVar(variableName);
    }

    public static Task defineVar(String variableName) {
        return newTask().defineVar(variableName);
    }

    public static Task addToVar(String variableName) {
        return newTask().addToVar(variableName);
    }

    public static Task map(TaskFunctionMap mapFunction) {
        return newTask().map(mapFunction);
    }

    public static Task selectWith(String name, String pattern) {
        return newTask().selectWith(name, pattern);
    }

    public static Task selectWithout(String name, String pattern) {
        return newTask().selectWithout(name, pattern);
    }

    public static Task select(TaskFunctionSelect filterFunction) {
        return newTask().select(filterFunction);
    }

    public static Task selectObject(TaskFunctionSelectObject filterFunction) {
        return newTask().selectObject(filterFunction);
    }

    public static Task traverse(String relationName) {
        return newTask().traverse(relationName);
    }


    public static Task get(String name) {
        return newTask().get(name);
    }

    public static Task traverseIndex(String indexName, String... queryParams) {
        Task t = newTask().traverseIndex(indexName, queryParams);
        return t;
    }

    public static Task traverseOrKeep(String relationName) {
        return newTask().traverseOrKeep(relationName);
    }

    public static Task traverseIndexAll(String indexName) {
        return newTask().traverseIndexAll(indexName);
    }

    public static Task loop(String from, String to, Task subTask) {
        return newTask().loop(from, to, subTask);
    }

    public static Task loopPar(String from, String to, Task subTask) {
        return newTask().loopPar(from, to, subTask);
    }

    public static Task print(String name) {
        return newTask().print(name);
    }

    public static Task println(String name) {
        return newTask().println(name);
    }

    public static Task setProperty(String propertyName, byte propertyType, String variableNameToSet) {
        return newTask().setProperty(propertyName, propertyType, variableNameToSet);
    }

    public static Task selectWhere(Task subTask) {
        return newTask().selectWhere(subTask);
    }

    public static Task foreach(Task subTask) {
        return newTask().foreach(subTask);
    }

    public static Task foreachPar(Task subTask) {
        return newTask().foreachPar(subTask);
    }

    public static Task flatmap(Task subTask) {
        return newTask().flatmap(subTask);
    }

    public static Task flatmapPar(Task subTask) {
        return newTask().flatmapPar(subTask);
    }

    public static Task math(String expression) {
        return newTask().math(expression);
    }

    public static Task action(String name, String params) {
        return newTask().action(name, params);
    }

    public static Task remove(String relationName, String variableNameToRemove) {
        return newTask().remove(relationName, variableNameToRemove);
    }

    public static Task add(String relationName, String variableNameToAdd) {
        return newTask().add(relationName, variableNameToAdd);
    }

    public static Task properties() {
        return newTask().properties();
    }

    public static Task propertiesWithTypes(byte filter) {
        return newTask().propertiesWithTypes(filter);
    }

    public static Task jump(String time) {
        return newTask().jump(time);
    }

    public static Task removeProperty(String propertyName) {
        return newTask().removeProperty(propertyName);
    }

    public static Task newNode() {
        return newTask().newNode();
    }

    public static Task newTypedNode(String nodeType) {
        return newTask().newTypedNode(nodeType);
    }

    public static Task save() {
        return newTask().save();
    }

    public static Task ifThen(TaskFunctionConditional cond, Task then) {
        return newTask().ifThen(cond, then);
    }

    public static Task ifThenElse(TaskFunctionConditional cond, Task thenSub, Task elseSub) {
        return newTask().ifThenElse(cond, thenSub, elseSub);
    }

    public static Task whileDo(TaskFunctionConditional cond, Task then) {
        return newTask().whileDo(cond, then);
    }

    public static Task doWhile(Task then, TaskFunctionConditional cond) {
        return newTask().doWhile(then, cond);
    }

    public static Task split(String splitPattern) {
        return newTask().split(splitPattern);
    }

    public static Task lookup(String nodeId) {
        return newTask().lookup(nodeId);
    }

    public static Task hook(TaskHookFactory fact) {
        return newTask().hook(fact);
    }

    public static Task clear() {
        return newTask().clear();
    }

    public static Task subTask(final Task subTask) {
        return newTask().subTask(subTask);
    }

    public static Task isolate(Task subTask) {
        return newTask().isolate(subTask);
    }

    public static Task subTasks(final Task[] subTasks) {
        return newTask().subTasks(subTasks);
    }

    public static Task subTasksPar(final Task[] subTasks) {
        return newTask().subTasksPar(subTasks);
    }

    public static TaskFunctionConditional cond(String mathExpression) {
        return newTask().mathConditional(mathExpression);
    }


    public static Task timepoints(String from, String to) {
        return newTask().timepoints(from,to);
    }

}
