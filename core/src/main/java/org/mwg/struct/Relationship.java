package org.mwg.struct;

public interface Relationship {

    int size();

    long get(int index);

    void set(int index, long value);

    Relationship add(long newValue);

    /**
     * Insert a long (node id) into a relationship at a particular index,
     *
     * @param newValue node id to insert
     * @param index    insert to insert, note that bigger index will be shifted
     * @return this Relationship, fluent API
     */
    Relationship insert(int index,long newValue);

    Relationship remove(long oldValue);

    Relationship delete(int oldValue);

    Relationship clear();

}
