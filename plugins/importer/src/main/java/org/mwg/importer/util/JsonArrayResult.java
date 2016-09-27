package org.mwg.importer.util;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonArrayResult implements TaskResult {

    private final JsonArray _content;

    public JsonArrayResult(JsonArray init) {
        _content = init;
    }

    @Override
    public TaskResultIterator iterator() {
        return new TaskResultIterator() {

            private final Iterator<JsonValue> it = _content.iterator();
            private int cursor = 0;

            @Override
            public synchronized Object next() {
                cursor++;
                if (it.hasNext()) {
                    return JsonValueResultBuilder.build(it.next());
                } else {
                    return null;
                }
            }

            @Override
            public synchronized Tuple nextWithIndex() {
                final int i = cursor;
                cursor++;
                if (it.hasNext()) {
                    return new Tuple<Integer, Object>(i, JsonValueResultBuilder.build(it.next()));
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public Object get(int index) {
        return JsonValueResultBuilder.build(_content.get(index));
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
    public TaskResult<JsonValue> clone() {
        return this;
    }

    @Override
    public void free() {

    }

    @Override
    public int size() {
        return _content.size();
    }

    @Override
    public Object[] asArray() {
        Object[] flat = new Object[_content.size()];
        for (int i = 0; i < _content.size(); i++) {
            flat[i] = _content.get(i);
        }
        return flat;
    }

    @Override
    public String toString() {
        return _content.toString();
    }

}
