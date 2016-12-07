package org.mwg.core.task.math;

import org.junit.Test;
import org.mwg.*;
import org.mwg.task.Actions;
import org.mwg.task.TaskResult;

public class CoreTaskTest {



    @Test
    public void testParse() {
        Graph graph = new GraphBuilder().build();
        graph.connect(succeed -> {
            Node root1 = graph.newNode(0,0);
            root1.setProperty("name", Type.STRING,"root1");

            Node root2 = graph.newNode(0,0);
            root2.setProperty("name", Type.STRING,"root2");

            Node root3 = graph.newNode(0,0);
            root3.setProperty("name", Type.STRING,"root3");

            Node child1R1 = graph.newNode(0,0);
            child1R1.set("name","Patrick Jean");
            root1.add("children",child1R1);

            graph.index("roots",root1,"name",null);
            graph.index("roots",root2,"name",null);
            graph.index("roots",root3,"name",null);


            System.out.println(child1R1);
//            Actions.newTask()
//                    .setTime("0")
//                    .setWorld("0")
//                    .fromIndex("roots","name=root1")
//                    .traverse("children")
//                    .select(((node, context) -> node.get("name").equals("Patrick Jean")))
//                    .setProperty("name",Type.STRING,"newName")
//                    .execute(graph, new Callback<TaskResult>() {
//                        @Override
//                        public void on(TaskResult result) {
//                            System.out.println(child1R1);
//                        }
//                    });

            final String action2  = "fromIndex(roots, name=root1)" +
                    ".traverse(children)" +
                    ".selectScript('node.get(\'name\') == \'Patrick Jean\'')" +
                    ".setProperty(name,2,newName)" +
                    ".save()";

            Actions.newTask()
                    .setTime("0")
                    .setWorld("0")
                    .parse(action2)
                    .execute(graph, new Callback<TaskResult>() {
                        @Override
                        public void on(TaskResult result) {
                            System.out.println(child1R1);
                        }
                    });

        });

    }
}
