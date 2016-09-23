package org.mwg.plugin;

/**
 * Node state, used to access all stored state element of a particular node
 */
public interface NodeState {

    /**
     * Returns the id of the world this state is attached to.
     * @return the world id
     */
    long world();

    /**
     * Returns the time this state is attached to.
     *
     * @return current resolved time
     */
    long time();

    /**
     * Set the named state element
     *
     * @param index    unique key of element
     * @param elemType type of the element (based on Type definition)
     * @param elem     element to be set
     */
    void set(long index, byte elemType, Object elem);

    /**
     * Set the named state element
     *
     * @param key      unique key of element
     * @param elemType type of the element (based on Type definition)
     * @param elem     element to be set
     */
    void setFromKey(String key, byte elemType, Object elem);

    /**
     * Get the named state element
     *
     * @param index unique key of element
     * @return      stored element
     */
    Object get(long index);

    /**
     * Get the named state element
     *
     * @param key   unique key of element
     * @return      stored element
     */
    Object getFromKey(String key);

    /**
     * Get the named state element
     *
     * @param key           unique key of element
     * @param defaultValue  default value in case of null on the previous state
     * @param <A>           The type of the value
     * @return typed stored element
     */
    <A> A getFromKeyWithDefault(String key, A defaultValue);

    /**
     * Get from the state element long
     *
     * @param key           unique long key of element
     * @param defaultValue  default value in case of null on the previous state
     * @param <A>           The type of the value
     * @return stored element
     */
    <A> A getWithDefault(long key, A defaultValue);

    /**
     * Atomically get or create an element according to the elemType parameter.
     * This method is particularly handy for map manipulation that have to be initialize by the node state before any usage.
     *
     * @param index         unique key of element
     * @param elemType      type of the element (according to Type definition)
     * @return              new or previously stored element
     */
    Object getOrCreate(long index, byte elemType);

    /**
     * Atomically get or create an element according to the elemType parameter.
     * This method is particularly handy for map manipulation that have to be initialize by the node state before any usage.
     *
     * @param key       unique key of element
     * @param elemType  type of the element (according to Type definition)
     * @return          new or previously stored element
     */
    Object getOrCreateFromKey(String key, byte elemType);

    /**
     * Get the type of the stored element, -1 if not found
     *
     * @param index     unique key of element
     * @return          type currently stored, encoded as a int according the Type defintion
     */
    byte getType(long index);

    /**
     * Get the type of the stored element, -1 if not found
     *
     * @param key   unique key of element
     * @return      type currently stored, encoded as a int according the Type defintion
     */
    byte getTypeFromKey(String key);


    /**
     * Iterate over NodeState elements
     *
     * @param callBack the method to be called for each state.
     */
    void each(NodeStateCallback callBack);


}
