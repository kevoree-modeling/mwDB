package org.mwg.core.task;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionConditional;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.CoreTask.task;
import static org.mwg.task.Actions.selectWhere;


public class RobustnessTests {
    private Graph _graph;

    @Before
    public void initGraph() {
        _graph = new GraphBuilder().build();
        _graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node root = _graph.newNode(0, 0);
                root.set("name", "root");

                Node n1 = _graph.newNode(0, 0);
                n1.set("name", "n1");

                Node n2 = _graph.newNode(0, 0);
                n2.set("name", "n2");

                Node n3 = _graph.newNode(0, 0);
                n3.set("name", "n3");

                root.add("child", n1);
                root.add("child", n2);
                root.add("child", n3);

                _graph.index("rootIndex", root, "name", new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                    }
                });
            }
        });

    }

    @After
    public void deleteGrap() {
        _graph.find(0, 0, "rootIndex", "name=root", new Callback<Node[]>() {
            @Override
            public void on(Node[] result) {
                for (Node r : result) {
                    final Node rr = r;
                    r.rel("child", new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            for (Node n : result) {
                                n.free();
                            }
                            rr.free();
                        }
                    });
                }
            }
        });
    }

    @Test
    public void robustnessAsVar() {
        boolean exceptionCaught = false;
        try {
            task().then(inject(1)).then(asGlobalVar(null)).execute(_graph, null);
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
            task().then(fromVar(null)).execute(_graph, null);
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
            task().then(inject(null)).execute(_graph, null);
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
            task().then(fromIndex(null, "name=root")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //query null
        exceptionCaught = false;
        try {
            task().then(fromIndex("rootIndex", null)).execute(_graph, null);
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
            task().then(fromIndexAll(null)).execute(_graph, null);
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
            task().then(selectWith("child", null)).execute(_graph, null);
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
            task().then(selectWithout("child", null)).execute(_graph, null);
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
            task().then(select(null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

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
            task().forEach(null).execute(_graph, null);
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
            task().forEachPar(null).execute(_graph, null);
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
            task().map(null).execute(_graph, null);
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
            new CoreTask().ifThen(new TaskFunctionConditional() {
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
    }

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
            task().then(pluginAction(null, "")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //subTask null
        exceptionCaught = false;
        try {
            task().then(pluginAction("", null)).execute(_graph, null);
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
            task().then(setProperty(null, Type.STRING, "")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //propertyValue
        exceptionCaught = false;
        try {
            task().then(setProperty("", Type.STRING, null)).execute(_graph, null);
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
            task().then(setProperty(null, Type.STRING, "")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //propertyValue
        exceptionCaught = false;
        try {
            task().then(setProperty("", Type.STRING, null)).execute(_graph, null);
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
            task().then(removeProperty(null)).execute(_graph, null);
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
            task().then(inject(_graph.newNode(0, 0))).then(asGlobalVar("x")).then(add(null, "x")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //relatedNode
        exceptionCaught = false;
        try {
            task().then(add("", null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }

    @Test
    public void robustnessNodeRemove() {
        //relationName
        boolean exceptionCaught = false;
        try {
            task().then(inject(_graph.newNode(0, 0))).then(asGlobalVar("x")).then(remove(null, "x")).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);

        //relatedNode
        exceptionCaught = false;
        try {
            task().then(remove("", null)).execute(_graph, null);
        } catch (RuntimeException e) {
            exceptionCaught = true;
        } catch (Exception e) {
            Assert.fail("Unexpected exception thrown");
        }
        Assert.assertEquals(true, exceptionCaught);
    }


}
