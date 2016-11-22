package org.mwg.core;

import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.Query;
import org.mwg.plugin.Resolver;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;
import org.mwg.utility.HashHelper;

public class CoreQuery implements Query {

    private final Resolver _resolver;
    private final Graph _graph;
    private int capacity = 1;
    private long[] _attributes = new long[capacity];
    private String[] _values = new String[capacity];
    private int size = 0;
    private Long _hash;

    CoreQuery(Graph graph, Resolver p_resolver) {
        _graph = graph;
        _resolver = p_resolver;
        _hash = null;
    }

    @Override
    public Query parse(String flatQuery) {
        int cursor = 0;
        long currentKey = Constants.NULL_LONG;
        int lastElemStart = 0;
        while (cursor < flatQuery.length()) {
            if (flatQuery.charAt(cursor) == Constants.QUERY_KV_SEP) {
                if (lastElemStart != -1) {
                    currentKey = _resolver.stringToHash(flatQuery.substring(lastElemStart, cursor).trim(), false);
                }
                lastElemStart = cursor + 1;
            } else if (flatQuery.charAt(cursor) == Constants.QUERY_SEP) {
                if (currentKey != Constants.NULL_LONG) {
                    internal_add(currentKey, flatQuery.substring(lastElemStart, cursor).trim());
                }
                currentKey = Constants.NULL_LONG;
                lastElemStart = cursor + 1;
            }
            cursor++;
        }
        //insert the last element
        if (currentKey != Constants.NULL_LONG) {
            internal_add(currentKey, flatQuery.substring(lastElemStart, cursor).trim());
        }
        return this;
    }

    @Override
    public Query add(String attributeName, String value) {
        internal_add(_resolver.stringToHash(attributeName.trim(), false), value);
        return this;
    }

    public long hash() {
        if (_hash == null) {
            compute();
        }
        return this._hash;
    }

    @Override
    public long[] attributes() {
        return this._attributes;
    }

    @Override
    public Object[] values() {
        return this._values;
    }

    private void internal_add(long att, String val) {
        if (size == capacity) {
            //init
            int temp_capacity = capacity * 2;
            long[] temp_attributes = new long[temp_capacity];
            String[] temp_values = new String[temp_capacity];
            //copy
            System.arraycopy(_attributes, 0, temp_attributes, 0, capacity);
            System.arraycopy(_values, 0, temp_values, 0, capacity);
            //assign
            _attributes = temp_attributes;
            _values = temp_values;
            capacity = temp_capacity;

        }
        _attributes[size] = att;
        _values[size] = val;
        size++;
    }

    private void compute() {
        //SORT
        for (int i = (size - 1); i >= 0; i--) {
            for (int j = 1; j <= i; j++) {
                if (_attributes[j - 1] > _attributes[j]) {
                    long tempK = _attributes[j - 1];
                    String tempV = _values[j - 1];
                    _attributes[j - 1] = _attributes[j];
                    _values[j - 1] = _values[j];
                    _attributes[j] = tempK;
                    _values[j] = tempV;
                }
            }
        }
        Buffer buf = _graph.newBuffer();
        for (int i = 0; i < size; i++) {
            Base64.encodeLongToBuffer(_attributes[i], buf);
            Object loopValue = _values[i];
            if (loopValue != null) {
                Base64.encodeStringToBuffer(_values[i], buf);
            }
        }
        _hash = HashHelper.hashBytes(buf.data());
        buf.free();
    }

}
