package org.mwg;

import org.junit.Test;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.plugin.Job;

import java.io.File;
import java.io.IOException;

public class StorageTest {

    @Test
    public void test() throws IOException {

        try {
            test(new GraphBuilder().withStorage(new CassandraStorage("myspace")).withScheduler(new NoopScheduler()).withMemorySize(2000000).build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    final int valuesToInsert = 1000000;
    final long timeOrigin = 1000;

    private void test(final Graph graph) throws IOException {
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                final long before = System.currentTimeMillis();

                final Node node = graph.newNode(0, 0);
                final DeferCounter counter = graph.newCounter(valuesToInsert);
                for (long i = 0; i < valuesToInsert; i++) {

                    if (i % 10000 == 0) {
                         System.out.println("<insert til " + i + " in " + (System.currentTimeMillis() - before) / 1000 + "s");
                        graph.save(null);
                    }

                    final double value = i * 0.3;
                    final long time = timeOrigin + i;
                    graph.lookup(0, time, node.id(), new Callback<Node>() {
                        @Override
                        public void on(Node timedNode) {
                            timedNode.set("value", Type.DOUBLE, value);
                            counter.count();
                            timedNode.free();//free the node, for cache management
                        }
                    });
                }
                node.free();

                counter.then(new Job() {
                    @Override
                    public void run() {

                        long beforeRead = System.currentTimeMillis();

                        //System.out.println("<end insert phase>" + " " + (System.currentTimeMillis() - before) / 1000 + "s");
                        //System.out.println(name + " result: " + (valuesToInsert / ((System.currentTimeMillis() - before) / 1000) / 1000) + "kv/s");

                        graph.disconnect(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {
                                //System.out.println("Graph disconnected");
                            }
                        });
                    }
                });

            }
        });
        File data = new File("data");
        if (data.exists()) {
            delete(data);
        }
    }

    private static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length == 0) {
                file.delete();
            } else {
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }
                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                }
            }

        } else {
            //if file, then delete it
            file.delete();
        }
    }

}
