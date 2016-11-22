package org.mwg.core.task;

import org.junit.Assert;
import org.mwg.*;
import org.mwg.core.scheduler.NoopScheduler;

public abstract class AbstractActionTest {

    protected Graph graph;
    protected long startMemory;

    protected void initGraph() {
        graph = new GraphBuilder().withScheduler(new NoopScheduler()).build();
        final AbstractActionTest selfPointer = this;
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                //create graph nodes
                Node n0 = selfPointer.graph.newNode(0, 0);
                n0.set("name", Type.STRING, "n0");
                n0.set("value", Type.INT, 8);

                Node n1 = selfPointer.graph.newNode(0, 0);
                n1.set("name", Type.STRING, "n1");
                n1.set("value", Type.INT, 3);

                Node root = selfPointer.graph.newNode(0, 0);
                root.set("name", Type.STRING, "root");
                root.addToRelation("children", n0);
                root.addToRelation("children", n1);

                //create some index
                selfPointer.graph.index(0, 0, "roots", new Callback<NodeIndex>() {
                    @Override
                    public void on(NodeIndex rootsIndex) {
                        rootsIndex.addToIndex(root, "name");
                    }
                });
                selfPointer.graph.index(0, 0, "nodes", new Callback<NodeIndex>() {
                    @Override
                    public void on(NodeIndex nodesIndex) {
                        nodesIndex.addToIndex(n0, "name");
                        nodesIndex.addToIndex(n1, "name");
                        nodesIndex.addToIndex(root, "name");
                    }
                });
            }
        });
    }

    protected void initComplexGraph(Callback<Node> callback) {
        graph = new GraphBuilder().withScheduler(new NoopScheduler()).build();
        final AbstractActionTest selfPointer = this;
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node n1 = selfPointer.graph.newNode(0, 0);
                n1.set("name", Type.STRING, "n1");

                graph.save(null);
                long initcache = selfPointer.graph.space().available();

                Node n2 = selfPointer.graph.newNode(0, 0);
                n2.set("name", Type.STRING, "n2");

                Node n3 = selfPointer.graph.newNode(0, 0);
                n3.set("name", Type.STRING, "n3");

                n1.addToRelation("child", n2);
                n1.addToRelation("child", n3);

                Node n4 = selfPointer.graph.newNode(0, 0);
                n4.set("name", Type.STRING, "n4");
                n2.addToRelation("child", n4);


                Node n5 = selfPointer.graph.newNode(0, 0);
                n5.set("name",Type.STRING, "n5");
                n3.addToRelation("child", n5);

                Node n6 = selfPointer.graph.newNode(0, 0);
                n6.set("name",Type.STRING, "n6");
                n3.addToRelation("child", n6);


                Node n7 = selfPointer.graph.newNode(0, 0);
                n7.set("name",Type.STRING, "n7");
                n6.addToRelation("child", n7);

                Node n8 = selfPointer.graph.newNode(0, 0);
                n8.set("name", Type.STRING, "n8");
                n6.addToRelation("child", n8);

                n2.free();
                n3.free();
                n4.free();
                n5.free();
                n6.free();
                n7.free();
                n8.free();
                selfPointer.graph.save(null);
                Assert.assertTrue(selfPointer.graph.space().available() == initcache);

                callback.on(n1);

            }
        });

    }

    protected void removeGraph() {
        graph.disconnect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Assert.assertEquals(true, result);
            }
        });
    }

    protected void startMemoryLeakTest() {
        graph.save(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                startMemory = graph.space().available();
            }
        });
    }

    protected void endMemoryLeakTest() {
        graph.save(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Assert.assertEquals(startMemory, graph.space().available());
            }
        });
    }

}
