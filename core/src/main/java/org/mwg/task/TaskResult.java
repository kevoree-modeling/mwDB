package org.mwg.task;

public interface TaskResult<A> {

    TaskResultIterator iterator();

    A get(int index);

    void set(int index, A input);

    void allocate(int index);

    void add(A input);

    void clear();

    TaskResult<A> clone();

    void free();

    int size();

    Object[] asArray();

}
