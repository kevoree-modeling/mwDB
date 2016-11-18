package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.plugin.NodeState;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;


class ActionProperties extends AbstractAction {
    private final byte _filter;

    ActionProperties(byte filterType) {
        super();
        this._filter = filterType;
    }

    @Override
    public void eval(TaskContext context) {
        final TaskResult previous = context.result();
        final TaskResult result = context.newResult();
        for (int i = 0; i < previous.size(); i++) {
            if (previous.get(i) instanceof BaseNode) {
                final Node n = (Node) previous.get(i);
                final NodeState nState = context.graph().resolver().resolveState(n);
                nState.each(new NodeStateCallback() {
                    @Override
                    public void on(long attributeKey, byte elemType, Object elem) {
                        if (_filter == -1 || elemType == _filter) {
                            String retrieved = context.graph().resolver().hashToString(attributeKey);
                            if (retrieved != null) {
                                result.add(retrieved);
                            } else {
                                result.add(attributeKey);
                            }
                        }
                    }
                });
                n.free();
            }
        }
        previous.clear();
        context.continueWith(result);

    }
}
