package org.mwg.importer.util;

import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

public class JsonSingleResult implements TaskResult {

    public JsonSingleResult(Object content) {
        this._content = content;
    }

    private Object _content;

    @Override
    public TaskResultIterator iterator() {
        return new TaskResultIterator() {

            private int i = 0;

            @Override
            public synchronized Object next() {
                i++;
                if (i == 1) {
                    return _content;
                } else {
                    return null;
                }
            }

            @Override
            public synchronized Tuple nextWithIndex() {
                i++;
                if (i == 1) {
                    return new Tuple(0, _content);
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public Object get(int index) {
        if (index == 0) {
            return _content;
        }
        return null;
    }

    @Override
    public void set(int index, Object input) {

    }

    @Override
    public void allocate(int index) {

    }

    @Override
    public void add(Object input) {

    }

    @Override
    public void clear() {

    }

    @Override
    public TaskResult clone() {
        return this;
    }

    @Override
    public void free() {

    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Object[] asArray() {
        return new Object[]{_content};
    }

    @Override
    public String toString() {
        return _content.toString();
    }
}
