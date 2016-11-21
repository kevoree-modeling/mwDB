package org.mwg.plugin;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.struct.Buffer;

/**
 * Storage defines the interface any storage solution must comply with to be plugged to mwDB.
 */
public interface Storage {

    /**
     * Used to retrieve objects from the storage.<br>
     * The {@code keys} buffer is a sequential list of keys, assembled in a flat buffer and separator by BUFFER_SEP<br>
     *
     * @param keys     The buffer of keys as specified above.
     * @param callback Is called when all objects are collected.
     *                 The size of the array in parameter is always 1/3 of the keys array size.
     *                 Objects in the result array are in the same order as the keys.
     */
    void get(Buffer keys, Callback<Buffer> callback);

    /**
     * Used to push objects to the storage.<br>
     *
     * @param stream   The objects to store organized as a list of elements, assembled in a flat buffer.
     * @param callback Called when the operation is complete. The parameter will be true if the operation succeeded, false otherwise.
     */
    void put(Buffer stream, Callback<Boolean> callback);

    /**
     * Called to remove objects from the storage.
     * The {@code keys} array is a sequential list of &lt;world, timepoint, id&gt; tuples organized as follows:<br>
     * Say you wanna remove objects &lt;1, 2, 3&gt; and &lt;1, 5, 6&gt;, the array will be: [1,2,3,1,5,6]
     *
     * @param keys     The array of keys as specified above.
     * @param callback Is called when all keys are deleted.
     */
    void remove(Buffer keys, Callback<Boolean> callback);

    /**
     * Connects the storage
     *
     * @param graph    Graph this storage is attached to
     * @param callback Called when the connection process is complete. The parameter will be true if the operation succeeded, false otherwise.
     */
    void connect(Graph graph, Callback<Boolean> callback);

    /**
     * Lock a reserved number (to be used as a prefix).
     *
     * @param callback Called when the connection process is complete. The parameter will be new lock, null in case of error.
     */
    void lock(Callback<Buffer> callback);

    /**
     * Unlock a previously reserved lock
     *
     * @param previousLock the previously reserved lock number
     * @param callback     Called when the connection process is complete. The parameter will be true if the operation succeeded, false otherwise.
     */
    void unlock(Buffer previousLock, Callback<Boolean> callback);

    /**
     * Disconnects the storage
     *
     * @param callback Called when the disconnection process is complete. The parameter will be true if the operation succeeded, false otherwise.
     */
    void disconnect(Callback<Boolean> callback);

}