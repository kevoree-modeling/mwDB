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

class ActionLocalIndexOrUnindex extends AbstractAction {
    private final String _indexedRelation;
    private final String _flatKeyAttributes;
    private final boolean _isIndexation;
    private final String _varNodeToAdd;

    ActionLocalIndexOrUnindex(String indexedRelation, String flatKeyAttributes, String varNodeToAdd, boolean _isIndexation) {
        super();
        this._indexedRelation = indexedRelation;
        this._flatKeyAttributes = flatKeyAttributes;
        this._isIndexation = _isIndexation;
        this._varNodeToAdd = varNodeToAdd;
    }

    @Override
    public void eval(TaskContext context) {
        final TaskResult previousResult = context.result();
        final String templatedIndexName = context.template(_indexedRelation);
        final String templatedKeyAttributes = context.template(_flatKeyAttributes);

        final TaskResult toAdd = context.variable(_varNodeToAdd);
        if(toAdd.size() == 0 ) {
            throw new RuntimeException("Error while adding a new node in a local index: '" + _varNodeToAdd + "' does not contain any element.");
        }

        final DeferCounter counter = new CoreDeferCounter(previousResult.size() * toAdd.size());

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

        for (int srcNodeIdx = 0; srcNodeIdx < previousResult.size(); srcNodeIdx++) {
            final Object srcNode = previousResult.get(srcNodeIdx);

            for(int targetNodeIdx = 0; targetNodeIdx < toAdd.size(); targetNodeIdx++) {
                final Object targetNode = toAdd.get(targetNodeIdx);
                if(targetNode instanceof BaseNode && srcNode instanceof BaseNode) {
                    if(_isIndexation) {
                        ((BaseNode) srcNode).index(templatedIndexName, (BaseNode) targetNode, templatedKeyAttributes, end);
                    } else {
                        ((BaseNode) srcNode).unindex(templatedIndexName, (BaseNode) targetNode, templatedKeyAttributes, end);
                    }
                } else {
                    counter.count();
                }
            }
        }
        counter.then(new Job() {
            @Override
            public void run() {
                context.continueTask();
            }
        });

    }
}
