package org.mwg.struct;

public interface LongLongArrayMap extends Map {

    /**
     * Get all associated values to the current param key
     *
     * @param key key to be retrieve
     * @return array of associated values[]
     */
    long[] get(long key);

    /**
     * Add the tuple key/value to the getOrCreateMap.
     * This getOrCreateMap allows keys conflicts.
     * In other words, one key can be mapped to various values.
     *
     * @param key   to insert key
     * @param value to insert value
     */
    void put(long key, long value);

    /**
     * Remove the current K/V tuple fromVar the getOrCreateMap
     *
     * @param key   to delete key
     * @param value to delete value
     */
    void delete(long key, long value);

    /**
     * Iterate over all Key/value tuple of the cam
     *
     * @param callback closure that will be called for each K/V tuple
     */
    void each(LongLongArrayMapCallBack callback);


    boolean contains(long key, long value);

}
