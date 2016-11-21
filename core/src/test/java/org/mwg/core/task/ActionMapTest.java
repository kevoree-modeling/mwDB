package org.mwg.core.task;

public class ActionMapTest extends AbstractActionTest {

    /*
    @Test
    public void test() {
        initGraph();
        task()
                .then(readIndexAll("nodes"))
                .map(node -> ((Node)node).get("name"))
                .then(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> names = context.resultAsStrings();
                        Assert.assertEquals(names.get(0), "n0");
                        Assert.assertEquals(names.get(1), "n1");
                        Assert.assertEquals(names.get(2), "root");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }*/

}
