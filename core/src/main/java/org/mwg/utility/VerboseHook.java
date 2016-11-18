package org.mwg.utility;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;

import java.util.HashMap;
import java.util.Map;

class VerboseHook implements TaskHook {

    private Map<TaskContext, Integer> ctxIdents = new HashMap<TaskContext, Integer>();

    @Override
    public synchronized void start(TaskContext initialContext) {
        ctxIdents.put(initialContext, 0);
        System.out.println("StartTask:" + initialContext);
    }

    @Override
    public synchronized void beforeAction(Action action, TaskContext context) {
        Integer currentPrefix = ctxIdents.get(context);
        for (int i = 0; i < currentPrefix; i++) {
            System.out.print("\t");
        }
        String taskName = action.toString();
        System.out.println(context.template(taskName));
        /*
        for (int i = 0; i < context.ident(); i++) {
            System.out.print("\t");
            System.out.println(context.result().toString());
        }*/
    }

    @Override
    public synchronized void afterAction(Action action, TaskContext context) {
        //NOOP
    }

    @Override
    public synchronized void beforeTask(TaskContext parentContext, TaskContext context) {
        Integer currentPrefix = ctxIdents.get(parentContext);
        ctxIdents.put(context, currentPrefix + 1);
    }

    @Override
    public synchronized void afterTask(TaskContext context) {
        ctxIdents.remove(context);
    }

    @Override
    public synchronized void end(TaskContext finalContext) {
        System.out.println("EndTask:" + finalContext.toString());
    }
}
