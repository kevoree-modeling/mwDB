package org.mwg.base;

import org.mwg.plugin.*;
import org.mwg.task.TaskActionFactory;
import org.mwg.task.TaskHookFactory;

import java.util.HashMap;
import java.util.Map;

public class BasePlugin implements Plugin {

    private final Map<String, NodeFactory> _nodeTypes = new HashMap<String, NodeFactory>();

    private final Map<String, TaskActionFactory> _taskActions = new HashMap<String, TaskActionFactory>();

    private final Map<String, ExternalAttributeFactory> _externalAttributes = new HashMap<String, ExternalAttributeFactory>();

    private MemoryFactory _memoryFactory;

    private ResolverFactory _resolverFactory;

    private TaskHookFactory _hookFactory;

    @Override
    public Plugin declareNodeType(String name, NodeFactory factory) {
        _nodeTypes.put(name, factory);
        return this;
    }

    @Override
    public Plugin declareTaskAction(String name, TaskActionFactory factory) {
        _taskActions.put(name, factory);
        return this;
    }

    @Override
    public Plugin declareExternalAttribute(String name, ExternalAttributeFactory factory) {
        _externalAttributes.put(name, factory);
        return this;
    }

    @Override
    public Plugin declareMemoryFactory(MemoryFactory factory) {
        _memoryFactory = factory;
        return this;
    }

    @Override
    public Plugin declareResolverFactory(ResolverFactory factory) {
        _resolverFactory = factory;
        return this;
    }

    @Override
    public TaskHookFactory hookFactory() {
        return _hookFactory;
    }

    @Override
    public Plugin declareTaskHookFactory(TaskHookFactory factory) {
        _hookFactory = factory;
        return this;
    }

    @Override
    public final String[] nodeTypes() {
        return _nodeTypes.keySet().toArray(new String[_nodeTypes.size()]);
    }

    @Override
    public final NodeFactory nodeType(String nodeTypeName) {
        return _nodeTypes.get(nodeTypeName);
    }

    @Override
    public String[] taskActionTypes() {
        return _taskActions.keySet().toArray(new String[_taskActions.size()]);
    }

    @Override
    public TaskActionFactory taskActionType(String taskTypeName) {
        return _taskActions.get(taskTypeName);
    }

    @Override
    public String[] externalAttributes() {
        return _externalAttributes.keySet().toArray(new String[_externalAttributes.size()]);
    }

    @Override
    public ExternalAttributeFactory externalAttribute(String externalAttribute) {
        return _externalAttributes.get(externalAttribute);
    }

    @Override
    public MemoryFactory memoryFactory() {
        return _memoryFactory;
    }

    @Override
    public ResolverFactory resolverFactory() {
        return _resolverFactory;
    }

    @Override
    public void stop() {

    }

}
