package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

public class ActionLocalIndexOrUnindexTest {

    @Test
    public void testLocalIndex() {
        Graph graph = new GraphBuilder().build();

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean succeed) {
                Actions.newTask()
                        .newNode()
                        .setProperty("name", Type.STRING, "child1")
                        .addToVar("child")
                        .newNode()
                        .setProperty("name", Type.STRING, "child2")
                        .addToVar("child")
                        .newNode()
                        .setProperty("name", Type.STRING, "child3")
                        .addToVar("child")
                        .newNode()
                        .setProperty("name", Type.STRING, "root")
                        .indexNode("rootIdx", "name")
                        .localIndex("idxRelation","name","child")
                        .fromIndexAll("rootIdx")
                        .traverseIndexAll("idxRelation")
                        .then(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                TaskResult result = context.result();
                                Assert.assertEquals(3,result.size());

                                Assert.assertEquals("child1",((BaseNode)result.get(0)).get("name"));
                                Assert.assertEquals("child2",((BaseNode)result.get(1)).get("name"));
                                Assert.assertEquals("child3",((BaseNode)result.get(2)).get("name"));
                            }
                        })
                        .fromIndexAll("rootIdx")
                        .localUnindex("idxRelation","name","child")
                        .fromIndexAll("rootIdx")
                        .traverseIndexAll("idxRelation")
                        .then(new ActionFunction() {
                            @Override
                            public void eval(TaskContext context) {
                                TaskResult result = context.result();
                                Assert.assertEquals(0,result.size());
                            }
                        })
                        .execute(graph,null);


            }
        });
    }
}
