package org.mwg.importer.util;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.task.Task;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

import java.util.Iterator;
import java.util.List;

public class JsonObjectResult implements TaskResult<JsonMemberResult> {

    private final JsonObject _content;

    public JsonObjectResult(JsonObject init) {
        _content = init;
    }

    @Override
    public TaskResultIterator iterator() {
        return new TaskResultIterator() {
            private Iterator<JsonObject.Member> subIt = _content.iterator();
            private int i=0;

            @Override
            public synchronized Object next() {
                i++;
                if (subIt.hasNext()) {
                    JsonObject.Member res = subIt.next();
                    if (res != null) {
                        return new JsonMemberResult(res);
                    }
                }
                return null;
            }

            @Override
            public synchronized Tuple nextWithIndex() {
                final int cursor = i;
                i++;
                if (subIt.hasNext()) {
                    JsonObject.Member res = subIt.next();
                    if (res != null) {
                        return new Tuple(cursor,new JsonMemberResult(res));
                    }
                }
                return null;
            }
        };
    }

    @Override
    public JsonMemberResult get(int index) {
        Iterator<JsonObject.Member> subIt = _content.iterator();
        JsonObject.Member result = null;
        for (int i = 0; i < index; i++) {
            if (subIt.hasNext()) {
                result = subIt.next();
            }
        }
        if (result == null) {
            return null;
        }
        return new JsonMemberResult(result);
    }

    @Override
    public void set(int index, JsonMemberResult input) {
    }

    @Override
    public void allocate(int index) {
    }

    @Override
    public void add(JsonMemberResult input) {
    }

    @Override
    public void clear() {
    }

    @Override
    public TaskResult<JsonMemberResult> clone() {
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
        return new Object[]{_content};
    }

    @Override
    public String toString() {
        return _content.toString();
    }
}
