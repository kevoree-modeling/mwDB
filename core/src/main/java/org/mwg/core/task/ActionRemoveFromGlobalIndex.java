package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.base.BaseNode;
import org.mwg.core.utility.CoreDeferCounter;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionRemoveFromGlobalIndex implements Action {

    private final String _name;
    private final String[] _attributes;

    ActionRemoveFromGlobalIndex(final String name, final String... attributes) {
        this._name = name;
        this._attributes = attributes;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final String templatedIndexName = context.template(_name);
        final DeferCounter counter = new CoreDeferCounter(previousResult.size());

        for (int i = 0; i < previousResult.size(); i++) {
            final Object loop = previousResult.get(i);
            if (loop instanceof BaseNode) {
                BaseNode loopBaseNode = (BaseNode) loop;
                context.graph().index(loopBaseNode.world(), Constants.BEGINNING_OF_TIME, templatedIndexName, indexNode -> {
                    indexNode.removeFromIndex(loopBaseNode,_attributes);
                    counter.count();
                });
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

    /*
    @Override
    public String toString() {
        if (_isIndexation) {
            return "indexNode('" + _indexName + "','" + _flatKeyAttributes + "')";
        } else {
            return "unindexNode('" + _indexName + "','" + _flatKeyAttributes + "')";
        }
    }
    */
}
