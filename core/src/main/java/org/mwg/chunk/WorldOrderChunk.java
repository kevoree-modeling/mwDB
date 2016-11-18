package org.mwg.chunk;

import org.mwg.struct.LongLongMap;

public interface WorldOrderChunk extends Chunk, LongLongMap {

    long magic();

    void lock();

    void unlock();

    long extra();

    void setExtra(long extraValue);

}
