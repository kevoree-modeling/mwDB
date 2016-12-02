package org.mwg;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.chunk.StateChunk;
import org.mwg.base.BaseNode;
import org.mwg.struct.Buffer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;

public class WSServerTest {

    public static void main(String[] args) {
        final Graph graph = new GraphBuilder()
                .withMemorySize(10000)
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean connectResult) {
                WSServer graphServer = new WSServer(graph, 8050);
                graphServer.start();
                System.out.println("Connected!");


                Node root = graph.newNode(0, 0);
                root.set("name", Type.STRING, "root");

                Node n0 = graph.newNode(0, 0);
                n0.set("name", Type.STRING, "n0");

                Node n1 = graph.newNode(0, 0);
                n1.set("name", Type.STRING, "n0");

                root.addToRelation("children", n0);
                root.addToRelation("children", n1);

                graph.index(0, 0, "nodes", new Callback<NodeIndex>() {
                    @Override
                    public void on(NodeIndex indexNode) {
                        indexNode.addToIndex(root, "name");

                        System.out.println(indexNode.toString());

                        StateChunk chunk = (StateChunk) graph.space().get(((BaseNode) indexNode)._index_stateChunk);

                        Buffer buffer = graph.newBuffer();
                        chunk.save(buffer);

                        System.out.println(new String(buffer.data()));
                        System.out.println(chunk.index());

                    }
                });

            }
        });
    }

    @Test
    public void test() {


        final Graph graph = new GraphBuilder()
                .withMemorySize(10000)
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node node = graph.newNode(0, 0);
                node.set("name", Type.STRING, "hello");

                graph.index(0, 0, "nodes", new Callback<NodeIndex>() {

                    @Override
                    public void on(NodeIndex indexNode) {
                        indexNode.addToIndex(node, "name");


                    }
                });

                //   graph.index("nodes", node, "name", null);

                Assert.assertEquals("{\"world\":0,\"time\":0,\"id\":1,\"name\":\"hello\"}", node.toString());

                int port = 8050;
                try {
                    ServerSocket servSock = new ServerSocket(0);
                    port = servSock.getLocalPort();
                    servSock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                WSServer graphServer = new WSServer(graph, port);
                graphServer.start();
                final CountDownLatch latch = new CountDownLatch(1);
                final Graph graph2 = new GraphBuilder().withMemorySize(10000).withStorage(new WSClient("ws://localhost:" + port)).build();
                graph2.connect(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result1) {

                        graph2.index(0, 0, "nodes", new Callback<NodeIndex>() {
                            @Override
                            public void on(NodeIndex indexNodes) {
                                indexNodes.findAll(new Callback<Node[]>() {
                                    @Override
                                    public void on(Node[] result1) {
                                        Assert.assertEquals(result1[0].toString(), node.toString());

                                        Node newNode = graph2.newNode(0, 0);
                                        newNode.set("name", Type.STRING, "hello2");

                                        Assert.assertEquals("{\"world\":0,\"time\":0,\"id\":137438953473,\"name\":\"hello2\"}", newNode.toString());

                                        graph2.index(0, 0, "nodes", new Callback<NodeIndex>() {
                                            @Override
                                            public void on(NodeIndex graph2Nodes) {
                                                graph2Nodes.addToIndex(newNode, "name");
                                                graph2Nodes.findAll(new Callback<Node[]>() {
                                                    @Override
                                                    public void on(Node[] result) {
                                                        Assert.assertEquals(2, result.length);
                                                    }
                                                });
                                            }
                                        });


                                        graph2.save(new Callback<Boolean>() {
                                            @Override
                                            public void on(Boolean result) {
                                                //ok now try to access new node from graph

                                                graph.index(0, 0, "nodes", new Callback<NodeIndex>() {
                                                    @Override
                                                    public void on(NodeIndex grapNodeIndex) {
                                                        grapNodeIndex.findAll(new Callback<Node[]>() {
                                                            @Override
                                                            public void on(Node[] result) {
                                                                Assert.assertEquals(2, result.length);
                                                                Assert.assertEquals(result[0].toString(), "{\"world\":0,\"time\":0,\"id\":1,\"name\":\"hello\"}");
                                                                Assert.assertEquals(result[1].toString(), "{\"world\":0,\"time\":0,\"id\":137438953473,\"name\":\"hello2\"}");
                                                                latch.countDown();
                                                            }
                                                        });
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });


    }

}
