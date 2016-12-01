package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.*;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.newTask;

public class ActionIfThenTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        final boolean[] result = {false, false};

        Task modifyResult0 = newTask().thenDo(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                result[0] = true;
            }
        });

        Task modifyResult1 = newTask().thenDo(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                result[0] = true;
            }
        });

        newTask().ifThen(new ConditionalFunction() {
            @Override
            public boolean eval(TaskContext context) {
                return true;
            }
        }, modifyResult0).execute(graph, null);

        newTask().ifThen(new ConditionalFunction() {
            @Override
            public boolean eval(TaskContext context) {
                return false;
            }
        }, modifyResult0).execute(graph, null);

        Assert.assertEquals(true, result[0]);
        Assert.assertEquals(false, result[1]);
        removeGraph();
    }

    @Test
    public void testChainAfterIfThen() {
        initGraph();
        Task addVarInContext = newTask().then(inject(5)).then(defineAsGlobalVar("variable")).thenDo(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                context.continueTask();
                //empty action
            }
        });

        newTask().ifThen(context -> true, addVarInContext).then(readVar("variable"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext context) {
                        Integer val = (Integer) context.result().get(0);
                        Assert.assertEquals(5, (int) val);
                    }
                }).execute(graph, null);
        removeGraph();
    }

    @Test
    public void accessContextVariableInThenTask() {
        initGraph();
        Task accessVar = newTask().thenDo(new ActionFunction() {
            @Override
            public void eval(TaskContext context) {
                Integer variable = (Integer) context.variable("variable").get(0);
                Assert.assertEquals(5, (int) variable);
                context.continueTask();
            }
        });

        newTask().then(inject(5)).then(defineAsGlobalVar("variable")).ifThen(new ConditionalFunction() {
            @Override
            public boolean eval(TaskContext context) {
                return true;
            }
        }, accessVar).execute(graph, null);
        removeGraph();
    }
}
