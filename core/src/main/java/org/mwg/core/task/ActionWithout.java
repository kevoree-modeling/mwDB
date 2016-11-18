package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.base.AbstractAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.regex.Pattern;

class ActionWithout extends AbstractAction {
    
    private final String _patternTemplate;
    private final String _name;

    ActionWithout(final String name, final String stringPattern) {
        super();
        if (name == null) {
            throw new RuntimeException("name should not be null");
        }
        if (stringPattern == null) {
            throw new RuntimeException("pattern should not be null");
        }
        this._patternTemplate = stringPattern;
        this._name = name;
    }

    @Override
    public String toString() {
        return "without(\'" + _name + "\'" + Constants.QUERY_SEP + "\'" + _patternTemplate + "\')";
    }

    @Override
    public void eval(final TaskContext context) {
        final Pattern pattern = Pattern.compile(context.template(_patternTemplate));
        final TaskResult previous = context.result();
        final TaskResult next = context.newResult();
        final int previousSize = previous.size();
        for (int i = 0; i < previousSize; i++) {
            final Object obj = previous.get(i);
            if (obj instanceof BaseNode) {
                final Node casted = (Node) obj;
                Object currentName = casted.get(_name);
                if (currentName == null || !pattern.matcher(currentName.toString()).matches()) {
                    next.add(casted.graph().cloneNode(casted));
                }
            } else {
                next.add(obj);
            }
        }
        context.continueWith(next);
    }

}
