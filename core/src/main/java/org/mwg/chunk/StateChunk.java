package org.mwg.chunk;

import org.mwg.plugin.NodeState;

public interface StateChunk extends Chunk, NodeState {

    void loadFrom(StateChunk origin);

}
