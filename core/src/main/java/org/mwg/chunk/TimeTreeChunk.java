package org.mwg.chunk;

public interface TimeTreeChunk extends Chunk {

    void insert(long key);

    void unsafe_insert(long key);

    long previousOrEqual(long key);

    void clearAt(long max);

    void range(long startKey, long endKey, long maxElements, TreeWalker walker);

    long magic();

    long size();

    long previous(long key);

    long next(long key);

}
