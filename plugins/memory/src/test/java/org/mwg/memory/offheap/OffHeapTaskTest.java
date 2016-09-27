package org.mwg.memory.offheap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.Task;

import static org.mwg.task.Actions.newTask;

/**
 * Created by gnain on 23/09/16.
 */
public class OffHeapTaskTest {

    //@Test
    public void setPropertyAfterJumpTest() {

        Graph g2 = new GraphBuilder().withPlugin(new OffHeapMemoryPlugin()).build();

        g2.connect(connectionResult -> {
            Task t = newTask()
                    .setTime("" + Constants.BEGINNING_OF_TIME)
                    .newNode()
//                    .jump("" + System.currentTimeMillis())
                    .setProperty("toto", Type.STRING, "hey");
            t.execute(g2, result -> {
                result.free();
                System.out.println("Executed");
                g2.save(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        g2.disconnect(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {

                            }
                        });
                    }
                });
            });

        });


    }

    //@After
    public void tearDown() throws Exception {
        if (OffHeapConstants.DEBUG_MODE) {
            Assert.assertEquals(OffHeapConstants.SEGMENTS.size(),0);
        }
    }


}
