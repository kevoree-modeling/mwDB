package org.mwg.plugin;

import org.mwg.Constants;

import java.util.Map;

public class Query {

    private long _hash;
    private int capacity = 1;
    long[] attributes = new long[capacity];
    public String[] values = new String[capacity];
    public int size = 0;

    public long hash() {
        return this._hash;
    }

    public void add(long att, String val) {
        if (size == capacity) {
            //init
            int temp_capacity = capacity * 2;
            long[] temp_attributes = new long[temp_capacity];
            String[] temp_values = new String[temp_capacity];
            //copy
            System.arraycopy(attributes, 0, temp_attributes, 0, capacity);
            System.arraycopy(values, 0, temp_values, 0, capacity);
            //assign
            attributes = temp_attributes;
            values = temp_values;
            capacity = temp_capacity;

        }
        attributes[size] = att;
        values[size] = val.trim();
        size++;
    }

    /**
     * @native ts
     * this.sort();
     * this._hash = 0;
     * for (var i = 0; i < this.size; i++) {
     * this._hash = ((this._hash * 32) - this._hash) + this.attributes[i];
     * if (this.values[i] != null) {
     * for (var j = 0; j < this.values[i].length; j++) {
     * this._hash = ((this._hash * 32) - this._hash) + this.values[i].charCodeAt(j);
     * }
     * }
     * }
     */
    public void compute() {
        sort();
        _hash = 0;
        for (int i = 0; i < size; i++) {
            _hash = ((_hash << 5) - _hash) + attributes[i];
            if (values[i] != null) {
                for (int j = 0; j < values[i].length(); j++) {
                    _hash = ((_hash << 5) - _hash) + values[i].codePointAt(j);
                }
            }
        }
        //
        //_hash = _hash % Constants.END_OF_TIME;
    }

    private void sort() {
        for (int i = (size - 1); i >= 0; i--) {
            for (int j = 1; j <= i; j++) {
                if (attributes[j - 1] > attributes[j]) {
                    long tempK = attributes[j - 1];
                    String tempV = values[j - 1];
                    attributes[j - 1] = attributes[j];
                    values[j - 1] = values[j];
                    attributes[j] = tempK;
                    values[j] = tempV;
                }
            }
        }
    }

    /**
     * Parse the query and return the complex FlatQuery object, containing the decomposition of keys/values
     */
    public static Query parseQuery(String query, Resolver p_resolver) {
        int cursor = 0;
        long currentKey = Constants.NULL_LONG;
        int lastElemStart = 0;
        Query flatQuery = new Query();
        while (cursor < query.length()) {
            if (query.charAt(cursor) == Constants.QUERY_KV_SEP) {
                if (lastElemStart != -1) {
                    currentKey = p_resolver.stringToHash(query.substring(lastElemStart, cursor).trim(), false);
                }
                lastElemStart = cursor + 1;
            } else if (query.charAt(cursor) == Constants.QUERY_SEP) {
                if (currentKey != Constants.NULL_LONG) {
                    flatQuery.add(currentKey, query.substring(lastElemStart, cursor).trim());
                }
                currentKey = Constants.NULL_LONG;
                lastElemStart = cursor + 1;
            }
            cursor++;
        }
        //insert the last element
        if (currentKey != Constants.NULL_LONG) {
            flatQuery.add(currentKey, query.substring(lastElemStart, cursor).trim());
        }
        flatQuery.compute();
        return flatQuery;
    }

    /**
     * Should allow escaping of special character like = and ,
     *
     * @param keyvalue   query under the form of map
     * @param p_resolver
     * @return complex FlatQuery object, containing the decomposition of keys/values
     */
    public static Query parseQuery(Map<String, String> keyvalue, Resolver p_resolver) {
        Query flatQuery = new Query();
        for (Map.Entry<String, String> entry : keyvalue.entrySet()) {
            flatQuery.add(p_resolver.stringToHash(entry.getKey(), false), entry.getValue());
        }
        flatQuery.compute();
        return flatQuery;
    }


}
