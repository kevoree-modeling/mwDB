package org.mwg.plugin.geojson;

import com.eclipsesource.json.JsonValue;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

public class JsonResult implements TaskResult<JsonValue> {

    private JsonValue[] _content;

    JsonResult(JsonValue[] init) {
        _content = init;
    }

    @Override
    public TaskResultIterator iterator() {
        return new TaskResultIterator() {
            private int currentIndex = 0;

            @Override
            public synchronized Object next() {
                return (currentIndex < _content.length ? _content[currentIndex++] : null);
            }

            @Override
            public synchronized Tuple nextWithIndex() {
                return (currentIndex < _content.length ? new Tuple(currentIndex, _content[currentIndex++]) : null);
            }
        };
    }

    @Override
    public JsonValue get(int index) {
        return (index < _content.length ? _content[index] : null);
    }

    @Override
    public void set(int index, JsonValue input) {
    }

    @Override
    public void allocate(int index) {
    }

    @Override
    public void add(JsonValue input) {
    }

    @Override
    public void clear() {
        _content = new JsonValue[0];
    }

    @Override
    public TaskResult<JsonValue> clone() {
        return this;
    }

    @Override
    public void free() {
        _content = null;
    }

    @Override
    public int size() {
        return _content.length;
    }

    @Override
    public Object[] asArray() {
        return _content;
    }
}
