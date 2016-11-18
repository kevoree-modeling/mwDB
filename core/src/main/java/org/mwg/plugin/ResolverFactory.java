package org.mwg.plugin;

import org.mwg.chunk.ChunkSpace;

public interface ResolverFactory {

    Resolver newResolver(Storage storage, ChunkSpace space);

}
