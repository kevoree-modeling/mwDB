package org.mwg.chunk;

import org.mwg.struct.Buffer;

public interface Chunk {

    long world();

    long time();

    long id();

    byte chunkType();

    long index();

    void save(Buffer buffer);

    void load(Buffer buffer);

}
