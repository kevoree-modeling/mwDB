package org.mwg.plugin;

import org.mwg.task.TaskActionFactory;
import org.mwg.task.TaskHookFactory;

public interface Plugin {

    Plugin declareNodeType(String name, NodeFactory factory);

    Plugin declareTaskAction(String name, TaskActionFactory factory);

    Plugin declareExternalAttribute(String name, ExternalAttributeFactory factory);

    Plugin declareMemoryFactory(MemoryFactory factory);

    Plugin declareTaskHookFactory(TaskHookFactory factory);

    Plugin declareResolverFactory(ResolverFactory factory);

    String[] nodeTypes();

    NodeFactory nodeType(String nodeTypeName);

    String[] taskActionTypes();

    TaskActionFactory taskActionType(String taskTypeName);

    String[] externalAttributes();

    ExternalAttributeFactory externalAttribute(String externalAttribute);

    TaskHookFactory hookFactory();

    MemoryFactory memoryFactory();

    ResolverFactory resolverFactory();

    void stop();

}
