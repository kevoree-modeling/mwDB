package org.mwg.chunk;

import org.mwg.Callback;
import org.mwg.Graph;

public interface ChunkSpace {

    /**
     * Creates a chunk, out of the space, not marked, used as a factory
     * @param type the type of chunk {@link ChunkType}
     * @param world the world id of the chunk
     * @param time the time of the chunk
     * @param id the id of the chunk
     * @return the created chunk
     */
    Chunk createAndMark(byte type, long world, long time, long id);

    /**
     * Gets and marks a chunk for the given values/keys
     * @param type the type of chunk {@link ChunkType}
     * @param world the world id of the chunk
     * @param time the time of the chunk
     * @param id the id of the chunk
     * @return the retrieved chunk
     */
    Chunk getAndMark(byte type, long world, long time, long id);

    /**
     * Gets and marks a chunk for the given values/keys, if not present in cache, loads it from configured storage
     * @param type the type of chunk {@link ChunkType}
     * @param world the world id of the chunk
     * @param time the time of the chunk
     * @param id the id of the chunk
     * @param callback method to be called once the chunk is ready for use (retrieved or loaded).
     */
    void getOrLoadAndMark(byte type, long world, long time, long id, Callback<Chunk> callback);

    void getOrLoadAndMarkAll(long[] keys, Callback<Chunk[]> callback);

    /**
     * Retrieves the chunk for the given index
     * @param index index of the chunk
     * @return the chunk
     */
    Chunk get(long index);

    /**
     * Unmarks the chunk at the given index
     * @param index the index of the chunk to unmark
     */
    void unmark(final long index);

    /**
     * Marks the chunk at the given index
     * @param index the index of the chunk to mark
     * @return the number of marks at this index
     */
    long mark(final long index);

    /**
     * Frees the chunk given in parameter from the memory
     * @param chunk the chunk to be freed
     */
    void free(final Chunk chunk);

    /**
     * Notifies that the chunk at this intex has been updated
     * @param index the index of the chunk updated
     */
    void notifyUpdate(long index);

    /**
     * Get current working graph
     * @return current graph
     */
    Graph graph();

    /**
     * Saves the ChunkSpace
     * @param callback the method to be called when save is completed. Parameter indicates status of the save.
     */
    void save(Callback<Boolean> callback);

    void clear();

    void freeAll();

    long available();

}
