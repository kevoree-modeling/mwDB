package org.mwg.chunk;

public interface Stack {

    boolean enqueue(long index);

    long dequeueTail();

    boolean dequeue(long index);

    void free();

    long size();

}