package org.mwg.core.task;

import org.junit.Test;

import static org.mwg.core.task.Actions.*;

public class ActionScriptTest extends AbstractActionTest {

    @Test
    public void testLookup() {
        initGraph();

        task()
                .then(readGlobalIndexAll("nodes"))
                .then(script("print('toto');context.continueTask();"))
                .then(script("print('tata');context.continueTask();"))
                .execute(graph, null);

        removeGraph();
    }
}
