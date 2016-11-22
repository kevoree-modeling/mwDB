package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.core.utility.CoreDeferCounter;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.Job;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionIndexOrUnindexNodeAt extends AbstractAction {
    private final String _indexName;
    private final String _flatKeyAttributes;
    private final boolean _isIndexation;

    private final String _world;
    private final String _time;

    ActionIndexOrUnindexNodeAt(final String world, final String time, final String indexName, final String flatKeyAttributes, final boolean isIndexation) {
        super();
        this._indexName = indexName;
        this._flatKeyAttributes = flatKeyAttributes;
        this._isIndexation = isIndexation;
        this._world = world;
        this._time = time;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();

        final long templatedWorld = Long.parseLong(context.template(_world));
        final long templatedTime = Long.parseLong(context.template(_time));

        final String templatedIndexName = context.template(_indexName);
        final String templatedKeyAttributes = context.template(_flatKeyAttributes);
        final DeferCounter counter = new CoreDeferCounter(previousResult.size());
        final Callback<Boolean> end = new Callback<Boolean>() {
            @Override
            public void on(Boolean succeed) {
                if (succeed) {
                    counter.count();
                } else {
                    throw new RuntimeException("Error during indexation of node with id " + ((Node) previousResult.get(0)).id());
                }
            }
        };
        for (int i = 0; i < previousResult.size(); i++) {
            final Object loop = previousResult.get(i);
            if (loop instanceof BaseNode) {
                /*
                if (_isIndexation) {
                    context.graph().indexAt(templatedWorld, templatedTime, templatedIndexName, (Node) loop, templatedKeyAttributes, end);
                } else {
                    context.graph().unindexAt(templatedWorld, templatedTime, templatedIndexName, (Node) loop, templatedKeyAttributes, end);
                }
                */
            } else {
                counter.count();
            }
        }
        counter.then(new Job() {
            @Override
            public void run() {
                context.continueTask();
            }
        });
    }

    @Override
    public String toString() {
        if (_isIndexation) {
            return "indexNodeAt('" + _world + "','" + _time + "','" + "'" + _indexName + "','" + _flatKeyAttributes + "')";
        } else {
            return "unindexNodeAt('" + _world + "','" + _time + "','" + "'" + _indexName + "','" + _flatKeyAttributes + "')";
        }
    }
}
