package org.mwg.struct;

public interface Relation {

    int size();

    long get(int index);

    void set(int index, long value);

    Relation add(long newValue);

    /**
     * Insert a long (node id) into a relationship at a particular index,
     *
     * @param newValue node id to insert
     * @param index    insert to insert, note that bigger index will be shifted
     * @return this Relation, fluent API
     */
    Relation insert(int index, long newValue);

    Relation remove(long oldValue);

    Relation delete(int oldValue);

    Relation clear();

}
