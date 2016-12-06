package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.base.BaseNode;
import org.mwg.core.utility.CoreDeferCounter;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionAddToGlobalIndex implements Action {

    private final String _name;
    private final String[] _attributes;

    ActionAddToGlobalIndex(final String name, final String... attributes) {
        this._name = name;
        this._attributes = attributes;
    }

    @Override
    public void eval(final TaskContext context) {


        final TaskResult previousResult = context.result();
        final String templatedIndexName = context.template(_name);
        final String templatedKeyAttributes;//= context.template(_flatKeyAttributes);
        final DeferCounter counter = new CoreDeferCounter(previousResult.size());
        /*final Callback<Boolean> end = new Callback<Boolean>() {
            @Override
            public void on(Boolean succeed) {
                if (succeed) {
                    counter.count();
                } else {
                    throw new RuntimeException("Error during indexation of node with id " + ((Node) previousResult.get(0)).id());
                }
            }
        };*/
        for (int i = 0; i < previousResult.size(); i++) {
            final Object loop = previousResult.get(i);
            if (loop instanceof BaseNode) {
                BaseNode loopBaseNode = (BaseNode) loop;
                context.graph().index(loopBaseNode.world(), Constants.BEGINNING_OF_TIME, templatedIndexName, indexNode -> {
                    indexNode.addToIndex(loopBaseNode,_attributes);
                    counter.count();
                });


                /*
                if (_isIndexation) {
                    context.graph().index(templatedIndexName, (Node) loop, templatedKeyAttributes, end);
                } else {
                    context.graph().unindex(templatedIndexName, (Node) loop, templatedKeyAttributes, end);
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
