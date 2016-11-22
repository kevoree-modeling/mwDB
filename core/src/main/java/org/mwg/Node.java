package org.mwg;

/**
 * Node is the base element contained in the {@link Graph}.<br>
 * They belong to a world and time, have attributes (e.g. primitives, relationships, and indexes).
 */
public interface Node {

    /**
     * The world this node belongs to.
     *
     * @return World identifier
     */
    long world();

    /**
     * Provides the timepoint of the node.
     *
     * @return Timestamp value
     */
    long time();

    /**
     * Provides the identifier for this node in the graph.<br>
     * This identifier is constant over timePoints and worlds.
     *
     * @return the node id.
     */
    long id();

    /**
     * Returns the value of an attribute of the node.
     *
     * @param name The name of the attribute to be read.
     * @return The value of the required attribute in this node for the current timepoint and world.
     * The type of the returned object (i.e.: of the attribute) is given by {@link #type(String)}
     * (typed by one of the Type)
     */
    Object get(String name);

    /**
     * Returns the value of an attribute of the node.
     *
     * @param index index of attribute.
     * @return The value of the required attribute in this node for the current timepoint and world.
     * The type of the returned object (i.e.: of the attribute) is given by {@link #type(String)}
     * (typed by one of the Type)
     */
    Object getByIndex(long index);

    /**
     * Allows to know the type of an attribute. The returned value is one of {@link Type}.
     *
     * @param name The name of the attribute for which the type is asked.
     * @return The type of the attribute inform of an int belonging to {@link Type}.
     */
    byte type(String name);

    /**
     * Allows to know the type name of the current node (case of typed node).
     *
     * @return The type name of the current node.
     */
    String nodeTypeName();

    /**
     * Sets the value of an attribute of this node, for its current world and time.<br>
     * This method hasField to be used for primitive types.
     *
     * @param name  The name of the attribute. Must be unique per node.
     * @param value The value of the attribute. Must be consistent with the propertyType.
     */
    void set(String name, Object value);

    /**
     * Sets the value of an attribute of this node, for its current world and time.<br>
     * This method hasField to be used for primitive types.
     *
     * @param name  Must be unique per node.
     * @param type  Must be one of {@link Type} int value.
     * @param value Must be consistent with the propertyType.
     */
    void setAttribute(String name, byte type, Object value);

    /**
     * Sets the value of an attribute of this node, for its current world and time.<br>
     * This method hasField to be used for primitive types.
     *
     * @param name  Must be unique per node.
     * @param type  Must be one of {@link Type} int value.
     * @param value Must be consistent with the propertyType.
     */
    void forceAttribute(String name, byte type, Object value);

    /**
     * Sets the value of an attribute of this node, for its current world and time.<br>
     * This method hasField to be used for primitive types.
     *
     * @param index Must be unique per node.
     * @param type  Must be one of {@link Type} int value.
     * @param value Must be consistent with the propertyType.
     */
    void setAttributeByIndex(long index, byte type, Object value);

    /**
     * Gets or creates atomically a complex attribute (such as Maps).<br>
     * It returns a mutable Map.
     *
     * @param name The name of the object to create. Must be unique per node.
     * @param type The type of the attribute. Must be one of {@link Type} int value.
     * @return A Map instance that can be altered at the current world and time.
     */
    Object getOrCreate(String name, byte type);

    /**
     * @param name                  The name of the object to create. Must be unique per node.
     * @param externalAttributeType The type of the external attribute type.
     * @return A Map instance that can be altered at the current world and time.
     */
    Object getOrCreateExternal(String name, String externalAttributeType);

    /**
     * Removes an attribute from the node.
     *
     * @param name The name of the attribute to remove.
     */
    void removeAttribute(String name);

    /**
     * Retrieves asynchronously the nodes contained in a traverseIndex.
     *
     * @param relationName The name of the traverseIndex to retrieve.
     * @param callback     Callback to be called when the nodes of the relationship have been connected.
     */
    void rel(String relationName, Callback<Node[]> callback);

    /**
     * Retrieves asynchronously the nodes contained in a traverseIndex.
     *
     * @param relationIndex The name of the traverseIndex to retrieve.
     * @param callback      Callback to be called when the nodes of the relationship have been connected.
     */
    void relByIndex(long relationIndex, Callback<Node[]> callback);

    /**
     * Adds a node to a relation.<br>
     * If the relationship doesn't exist, it is created on the fly.<br>
     *
     * @param relationName The name of the relation in which the node is added.
     * @param relatedNode  The node to insert in the relation.
     */
    void add(String relationName, Node relatedNode);

    /**
     * Removes a node from a relation.
     *
     * @param relationName The name of the relation.
     * @param relatedNode  The node to remove.
     */
    void remove(String relationName, Node relatedNode);

    /**
     * Creates or compliments an index of nodes.<br>
     * Indexes are special relationships for quick access to referred nodes based on some of their attributes values.<br>
     * Index names must be unique within a given node.
     *
     * @param indexName         The name of the index (should be unique per relation).
     * @param nodeToIndex       The new node to index.
     * @param flatKeyAttributes The flat list of attribute names (of the node to index, seperated by a ',') to be used as keys for indexing (order does not matter)
     * @param callback          Called when the index has been created/updated. The boolean value specifies the success of the operation.
     */
    //void index(String indexName, Node nodeToIndex, String flatKeyAttributes, Callback<Boolean> callback);

    /**
     * Removes an element from an index of nodes.<br>
     * Indexes are special relationships for quick access to referred nodes based on some of their attributes values.<br>
     * Index names must be unique within the indexed relation names.
     *
     * @param indexName         The name of the index (should be unique per relation).
     * @param nodeToIndex       The node to de-index.
     * @param flatKeyAttributes The list of attribute names (flat string seperated by a ',') to be used as keys for de-indexing (order does not matter)
     * @param callback          Called when the node has been de-index. The boolean value specifies the success of the operation.
     */
    // void unindex(String indexName, Node nodeToIndex, String flatKeyAttributes, Callback<Boolean> callback);

    /**
     * Retrieves nodes from an index that satisfies a query at the current node world and the current node time<br>
     * The query is composed by &lt;key, value&gt; tuples, separated by commas.
     *
     * @param indexName The name of the index (should be unique per relation)
     * @param query     The query on the searched node's attribute (e.g.: "firstName=john,lastName=doe,age=30")
     * @param callback  Called when the task is fully processed. The parameter is the requested nodes, empty array otherwise.
     */
    //void find(String indexName, String query, Callback<Node[]> callback);

    /**
     * Retrieves nodes from a local index that satisfies the query object passed as parameter.<br>
     *
     * @param query    The query on the searched node's attribute (e.g.: "firstname=john,lastname=doe,age=30"
     * @param callback Called when the task is fully processed. The parameter is the requested nodes, empty array otherwise.
     */
    //void findByQuery(Query query, Callback<Node[]> callback);

    /**
     * Retrieves all nodes in a particular index at the current node world and the current node time
     *
     * @param indexName The name of the index
     * @param callback  Called whe the collection is complete. Gives the list of contained nodes in parameter.
     */
    //void findAll(String indexName, Callback<Node[]> callback);

    /**
     * Compute the time dephasing of this node, i.e. the difference between last modification and current node timepoint.
     *
     * @return The amount of time between the current time of the node and the last recorded state chunk time.
     */
    long timeDephasing();

    /**
     * Compute the time dephasing of this node, i.e. the difference between last modification and current node timepoint.
     *
     * @return The amount of time between the current time of the node and the last recorded state chunk time.
     */
    long lastModification();

    /**
     * Forces the creation of a new timePoint of a node for its time.<br>
     * Clones the previous state to the exact time of this node.<br>
     * This cancels the dephasing between the current timepoint of the node and the last record timepoint.
     */
    void rephase();

    /**
     * Retrieves all timePoints from the timeLine of this node when alterations occurred.<br>
     * This method also jumps over the world hierarchy to collect all available timepoints.<br>
     * To unbound the search, please use {@link Constants#BEGINNING_OF_TIME} and {@link Constants#END_OF_TIME} as bounds.
     *
     * @param beginningOfSearch (inclusive) earliest bound for the search.
     * @param endOfSearch       (inclusive) latest bound for the search.
     * @param callback          Called when the search is finished. Provides an array containing all the timepoints required.
     */
    void timepoints(long beginningOfSearch, long endOfSearch, Callback<long[]> callback);

    /**
     * Informs mwDB memory manager that this node object can be freed from the memory.<br>
     * <b>Warning: this MUST be the last method called on this node.</b><br>
     * To work with the node afterwards, a new lookup is mandatory.
     */
    void free();

    /**
     * Return the graph that have created this node.
     *
     * @return the graph this node belongs to
     */
    Graph graph();

    /**
     * Jump over the time for this object. This method is equivalent to a call to lookup with the same ID than the current Node.
     *
     * @param targetTime target time selectWhere this node hasField to be resolved.
     * @param callback   Called whe the jump is complete. Gives the new timed node in parameter.
     * @param <A>        Generic parameter that define the type of the result, should be a sub-type of Node
     */
    <A extends Node> void jump(long targetTime, Callback<A> callback);

    /*
    long initialTime();

    long lastTime();

    long previousTime();

    long nextTime();
*/
}
