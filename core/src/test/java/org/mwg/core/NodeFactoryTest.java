package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.base.BaseNode;
import org.mwg.base.BasePlugin;
import org.mwg.plugin.NodeFactory;

public class NodeFactoryTest {

    private static final String NAME = "HelloWorldNode";

    interface ExNode extends org.mwg.Node {
        String sayHello();
    }

    class ExNodeImpl extends BaseNode implements ExNode {

        public ExNodeImpl(long p_world, long p_time, long p_id, Graph p_graph) {
            super(p_world, p_time, p_id, p_graph);
        }

        @Override
        public Object get(String name) {
            if (name.equals("hello")) {
                return "world";
            }
            return super.get(name);
        }

        @Override
        public String sayHello() {
            return "HelloWorld";
        }
    }

    @Test
    public void heapTest() {
        test(new GraphBuilder().withScheduler(new NoopScheduler()).withPlugin(new BasePlugin().declareNodeType(NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new ExNodeImpl(world, time, id, graph);
            }
        })).build());
    }

    /**
     * @ignore ts
     */
    @Test
    public void offHeapTest() {
        /*
        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        test(new GraphBuilder().withScheduler(new NoopScheduler()).withOffHeapMemory().withMemorySize(10000).saveEvery(20).withPlugin(new BasePlugin().declareNodeType(NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new ExNodeImpl(world, time, id, graph);
            }
        })).build());

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
        */
    }

    private void test(final Graph graph) {

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node specializedNode = graph.newTypedNode(0, 0, NAME);

                Assert.assertEquals(specializedNode.nodeTypeName(), "HelloWorldNode");

                String hw = (String) specializedNode.get("hello");
                Assert.assertTrue(hw.equals("world"));

                Node parent = graph.newNode(0, 0);
                parent.addToRelation("children", specializedNode);
                parent.rel("children", new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        Assert.assertEquals("HelloWorld", ((ExNode) result[0]).sayHello());
                    }
                });

                specializedNode.free();
                graph.disconnect(null);
            }
        });
    }

}
