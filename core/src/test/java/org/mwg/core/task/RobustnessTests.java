package org.mwg.core.task;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.TaskContext;
import org.mwg.task.ConditionalFunction;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.newTask;


public class RobustnessTests {
    private Graph _graph;

    @Before
    public void initGraph() {
        _graph = new GraphBuilder().build();
        _graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node root = _graph.newNode(0, 0);
                root.set("name", Type.STRING, "root");

                Node n1 = _graph.newNode(0, 0);
                n1.set("name", Type.STRING, "n1");

                Node n2 = _graph.newNode(0, 0);
                n2.set("name", Type.STRING, "n2");

                Node n3 = _graph.newNode(0, 0);
                n3.set("name", Type.STRING, "n3");

                root.addToRelation("child", n1);
                root.addToRelation("child", n2);
                root.addToRelation("child", n3);

                _graph.index(0, 0, "rootIndex", rootIndex -> {
                    rootIndex.addToIndex(root, "name");
                });
            }
        });

    }

    @After
    public void deleteGrap() {

        _graph.index(0, 0, "rootIndex", rootIndex -> {
            rootIndex.findUsing(result -> {
                for (Node r : result) {
                    final Node rr = r;
                    r.relation("child", new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            for (Node n : result) {
                                n.free();
                            }
                            rr.free();
                        }
                    });
                }
            }, "name", "root");
        });
    }

    @Test
    public void robustnessAsVar() {
        boolean exceptionCaught = false;
        try {
            newTask().then(inject(1)).then(defineAsGlobalVar(null)).execute(_graph, null);
        } catch (RuntimeException npe) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessFromVar() {
        boolean exceptionCaught = false;
        try {
            newTask().then(readVar(null)).execute(_graph, null);
        } catch (RuntimeException npe) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessFrom() {
        boolean exceptionCaught = false;
        try {
            newTask().then(inject(null)).execute(_graph, null);
        } catch (RuntimeException npe) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessFromIndex() {
        //indexName null
        boolean exceptionCaught = false;
        try {
            newTask().then(readGlobalIndex(null, "name=root")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //query null
        exceptionCaught = false;
        try {
            newTask().then(readGlobalIndex("rootIndex", null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessFromIndexAll() {
        //indexName null
        boolean exceptionCaught = false;
        try {
            newTask().then(readGlobalIndexAll(null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessSelectWith() {
        boolean exceptionCaught = false;
        try {
            newTask().then(selectWith("child", null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessSelectWithout() {
        boolean exceptionCaught = false;
        try {
            newTask().then(selectWithout("child", null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessSelect() {
        boolean exceptionCaught = false;
        try {
            newTask().then(select(null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    /*
    @Test
    public void robustnessTraverseIndex() {
        boolean exceptionCaught = false;
        try {
            task().then(traverseIndex(null, "name", "root")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessTraverseIndexAll() {
        boolean exceptionCaught = false;
        try {
            task().then(traverseIndexAll(null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }
*/
    @Test
    public void robustnessMap() {
        boolean exceptionCaught = false;
        try {
            new CoreTask().map(null).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessForeach() {
        boolean exceptionCaught = false;
        try {
            newTask().forEach(null).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessForeachPar() {
        boolean exceptionCaught = false;
        try {
            newTask().forEachPar(null).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    /*
    @Test
    public void robustnessForeachThen() {
        boolean exceptionCaught = false;
        try {
            new CoreTask().foreachThen(null).execute(_graph);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }*/

    @Test
    public void robustnessWait() {
        boolean exceptionCaught = false;
        try {
            newTask().map(null).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessIfThen() {
        //condition null
        boolean exceptionCaught = false;
        try {
            new CoreTask().ifThen(null, new CoreTask()).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //subTask null
        exceptionCaught = false;
        try {
            new CoreTask().ifThen(new ConditionalFunction() {
                @Override
                public boolean eval(TaskContext context) {
                    return true;
                }
            }, null).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    /*
    @Test
    public void robustnessThen() {
        boolean exceptionCaught = false;
        try {
            new CoreTask().then(null).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }*/

    @Test
    public void robustnessParse() {
        boolean exceptionCaught = false;
        try {
            new CoreTask().parse(null).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessAction() {
        //condition null
        boolean exceptionCaught = false;
        try {
            newTask().then(pluginAction(null, "")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //subTask null
        exceptionCaught = false;
        try {
            newTask().then(pluginAction("", null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessNodeSet() {
        //propertyName
        boolean exceptionCaught = false;
        try {
            newTask().then(set(null, Type.STRING, "")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //propertyValue
        exceptionCaught = false;
        try {
            newTask().then(set("", Type.STRING, null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessNodeSetProperty() {
        //propertyName
        boolean exceptionCaught = false;
        try {
            newTask().then(set(null, Type.STRING, "")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //propertyValue
        exceptionCaught = false;
        try {
            newTask().then(set("", Type.STRING, null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessNodeRemoveProperty() {
        boolean exceptionCaught = false;
        try {
            newTask().then(remove(null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessNodeAdd() {
        //relationName
        boolean exceptionCaught = false;
        try {
            newTask().then(inject(_graph.newNode(0, 0))).then(defineAsGlobalVar("x")).then(addVarToRelation(null, "x")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //relatedNode
        exceptionCaught = false;
        try {
            newTask().then(addVarToRelation("", null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    /*
    @Test
    public void robustnessNodeRemove() {
        //relationName
        boolean exceptionCaught = false;
        try {
            task().then(inject(_graph.newNode(0, 0))).then(defineAsGlobalVar("x")).then(removeFromRelationship(null, "x")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //relatedNode
        exceptionCaught = false;
        try {
            task().then(removeFromRelationship("", null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }*/


}
