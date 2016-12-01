package org.mwg.core.task;


import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.struct.RelationIndexed;
import org.mwg.task.*;

import static org.mwg.core.task.Actions.readGlobalIndexAll;
import static org.mwg.core.task.Actions.attributes;
import static org.mwg.core.task.Actions.attributesWithTypes;
import static org.mwg.core.task.Actions.newTask;

public class ActionPropertiesTest {
    private Graph graph;

    public void initGraph() {
        graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node root = graph.newNode(0, 0);
                root.set("id", Type.INT, 1);
                root.set("attribute", Type.BOOL, false);

                graph.index(0, 0, "root", rootIndex -> {
                    rootIndex.addToIndex(root, "id");
                });

                Node child1 = graph.newNode(0, 0);
                child1.set("name", Type.STRING, "child1");
                root.addToRelation("rel1", child1);

                ((RelationIndexed) root.getOrCreate("localIindex1", Type.RELATION_INDEXED)).add(child1, "name");
            }
        });
    }

    public void deleteGraph() {
        graph.disconnect(null);
    }

    @Test
    public void testNormalRelations() {
        initGraph();
        newTask()
                .then(readGlobalIndexAll("root"))
                .then(attributes())
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> result = context.result();
                        Assert.assertEquals(4, result.size());

                        Assert.assertEquals("id", result.get(0));
                        Assert.assertEquals("attribute", result.get(1));
                        Assert.assertEquals("rel1", result.get(2));
                        Assert.assertEquals("localIindex1", result.get(3));
                        context.continueTask();
                    }
                })
                .execute(graph, null);
        deleteGraph();
    }

    @Test
    public void testLocalIndex() {
        initGraph();
        newTask()
                .then(readGlobalIndexAll("root"))
                .map(
                        newTask().then(attributesWithTypes(Type.RELATION)),
                        newTask().then(attributesWithTypes(Type.RELATION_INDEXED))
                )
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> result = context.result();
                        Assert.assertEquals(2, result.size());

                        Assert.assertEquals("rel1", result.get(0));
                        Assert.assertEquals("localIindex1", result.get(1));
                    }
                })
                .execute(graph, null);
        deleteGraph();
    }
}
