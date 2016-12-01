package org.mwg.ml;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.Graph;
import org.mwg.base.BaseNode;
import org.mwg.plugin.Job;
import org.mwg.task.Task;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;

public abstract class BaseMLNode extends BaseNode {

    public static String FROM_SEPARATOR = ";";
    public static String FROM = "from";

    public BaseMLNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    /**
     * If {@code obj} is null, throws {@code NullPointerException} with a {@code message}
     *
     * @param obj
     * @param message
     */
    protected static void requireNotNull(Object obj, String message) {
        if (obj == null) {
            throw new RuntimeException(message);
        }
    }

    /**
     * Asserts that condition is true. If not - throws {@code IllegalArgumentException} with a specified error message
     *
     * @param condition    Condition to test
     * @param errorMessage Error message thrown with {@code IllegalArgumentException} (if thrown)
     * @throws IllegalArgumentException if condition is false
     */
    protected void illegalArgumentIfFalse(boolean condition, String errorMessage) {
        assert errorMessage != null;
        if (!condition) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    protected void extractFeatures(final Callback<double[]> callback) {
        String query = (String) super.get(FROM);
        if (query != null) {
            //TODO CACHE TO AVOID PARSING EVERY TIME
            String[] split = query.split(FROM_SEPARATOR);
            Task[] tasks = new Task[split.length];
            for (int i = 0; i < split.length; i++) {
                Task t = newTask().then(setWorld("" + world()));
                t.then(setTime(time() + ""));
                t.parse(split[i].trim());
                tasks[i] = t;
            }
            //END TODO IN CACHE
            final double[] result = new double[tasks.length];
            final DeferCounter waiter = graph().newCounter(tasks.length);
            for (int i = 0; i < split.length; i++) {
                final int taskIndex = i;
                final TaskResult initial = emptyResult();
                initial.add(this);
                tasks[i].executeWith(graph(), initial, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult currentResult) {
                        if (currentResult == null) {
                            result[taskIndex] = Constants.NULL_LONG;
                        } else {
                            result[taskIndex] = Double.parseDouble(currentResult.get(0).toString());
                            currentResult.free();
                        }
                        waiter.count();
                    }
                });
            }
            waiter.then(new Job() {
                @Override
                public void run() {
                    callback.on(result);
                }
            });
        } else {
            callback.on(null);
        }
    }

}
