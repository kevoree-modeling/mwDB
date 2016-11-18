package org.mwg.struct;

public interface LongLongMap extends Map {

    /**
     * Retrieve the value inserted with the param key
     *
     * @param key key that have to be retrieve
     * @return associated value, Constants.NULL.LONG in case of not found.
     */
    long get(long key);

    /**
     * Add the tuple key/value to the getOrCreateMap.
     * In case the value is equals to Constants.NULL_LONG, the value will be atomically replaced by the current size of the getOrCreateMap
     *
     * @param key to insert key
     * @param value to insert value
     */
    void put(long key, long  value);

    /**
     * Remove the key passed as parameter fromVar the getOrCreateMap
     *
     * @param key key that have to be removed
     */
    void remove(long key);

    /**
     * Iterate over all Key/value tuple of the cam
     *
     * @param callback closure that will be called for each K/V tuple
     */
    void each(LongLongMapCallBack callback);

}
