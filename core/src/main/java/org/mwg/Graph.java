package org.mwg;

import org.mwg.chunk.ChunkSpace;
import org.mwg.plugin.*;
import org.mwg.struct.Buffer;
import org.mwg.task.TaskActionFactory;
import org.mwg.task.TaskHookFactory;

/**
 * Graph is the main structure of mwDB.
 * Use the {@link GraphBuilder} to get an instance.
 */
public interface Graph {

    /**
     * Creates a new (generic) {@link Node Node} in the Graph and returns the new Node.
     *
     * @param world initial world of the node
     * @param time  initial time of the node
     * @return newly created node
     */
    Node newNode(long world, long time);

    /**
     * Creates a new {@link Node Node} using the {@link NodeFactory} previously declared with the {@link GraphBuilder#withPlugin(Plugin)} method and returns the new Node.
     *
     * @param world    initial world of the node
     * @param time     initial time of the node
     * @param nodeType name of the {@link NodeFactory} to be used, as previously declared with the {@link GraphBuilder#withPlugin(Plugin)} method.
     * @return newly created node
     */
    Node newTypedNode(long world, long time, String nodeType);

    /**
     * Create a copy of this node that can be freed independently
     *
     * @param origin node to be cloned
     * @return cloned node (aka pointer)
     */
    Node cloneNode(Node origin);

    /**
     * Asynchronous lookup of a particular node.<br>
     * Based on the tuple &lt;World, Time, Node_ID&gt; this method seeks a {@link Node} in the Graph and returns it to the callback.
     *
     * @param world    The world identifier in which the Node must be searched.
     * @param time     The time at which the Node must be resolved.
     * @param id       The unique identifier of the {@link Node} ({@link Node#id()}) researched.
     * @param callback The task to be called when the {@link Node} is retrieved.
     * @param <A>      the type of the parameter returned in the callback (should extend {@link Node}).
     */
    <A extends Node> void lookup(long world, long time, long id, Callback<A> callback);

    /**
     * Asynchronous lookup of a nodes.<br>
     * Based on the tuple &lt;World, Time, Node_ID&gt; this method seeks a {@link Node} in the Graph and returns it to the callback.
     *
     * @param world    The world identifier in which the Node must be searched.
     * @param time     The time at which the Node must be resolved.
     * @param ids      The unique identifier of {@link Node} array ({@link Node#id()}) researched.
     * @param callback The task to be called when the {@link Node} is retrieved.
     */
    void lookupAll(long world, long time, long[] ids, Callback<Node[]> callback);

    /**
     * Asynchronous lookup of a nodes.<br>
     * Based on the tuple &lt;World, Time, Node_ID&gt; this method seeks a {@link Node} in the Graph and returns it to the callback.
     *
     * @param world    The world identifier in which the Node must be searched.
     * @param from     The time at which the range extract should start.
     * @param to       The time at which the range extract should end.
     * @param id       The unique identifier of the {@link Node} ({@link Node#id()}) researched.
     * @param callback The task to be called when the {@link Node} is retrieved.
     */
    void lookupTimes(long world, long from, long to, long id, Callback<Node[]> callback);

    /**
     * Asynchronous lookup of a nodes.<br>
     * Based on the tuple &lt;World, Time, Node_ID&gt; this method seeks a {@link Node} in the Graph and returns it to the callback.
     *
     * @param world    The world identifier in which the Node must be searched.
     * @param from     The time at which the range extract should start.
     * @param to       The time at which the range extract should end.
     * @param ids      The unique identifier of {@link Node} array ({@link Node#id()}) researched.
     * @param callback The task to be called when the {@link Node} is retrieved.
     */
    void lookupAllTimes(long world, long from, long to, long[] ids, Callback<Node[]> callback);

    /**
     * Creates a spin-off world from the world given as parameter.<br>
     * The forked world can then be altered independently of its parent.<br>
     * Every modification in the parent world will nevertheless be inherited.<br>
     * In case of concurrent change, changes in the forked world overrides changes from the parent.
     *
     * @param world origin world id
     * @return newly created child world id (to be used later in lookup method for instance)
     */
    long fork(long world);

    /**
     * Triggers a save task for the current graph.<br>
     * This method synchronizes the physical storage with the current in-memory graph.
     *
     * @param callback called when the save is finished. The parameter specifies whether or not the task succeeded.
     */
    void save(Callback<Boolean> callback);

    /**
     * Connects the current graph to its storage (mandatory before any other method call)
     *
     * @param callback Called when the connection is done. The parameter specifies whether or not the connection succeeded.
     */
    void connect(Callback<Boolean> callback);

    /**
     * Disconnects the current graph fromVar its storage (a save will be trigger safely before the exit)
     *
     * @param callback Called when the disconnection is completed. The parameter specifies whether or not the disconnection succeeded.
     */
    void disconnect(Callback<Boolean> callback);

    /**
     * Adds the {@code nodeToIndex} to the global index identified by {@code indexName}.<br>
     * If the index does not exist, it is created on the fly.<br>
     * The node is referenced by its {@code keyAttributes} in the index, and can be retrieved with {@link #find(long, long, String, String, Callback)} using the same attributes.
     *
     * @param indexName         A string uniquely identifying the index in the {@link Graph}.
     * @param nodeToIndex       The node to add in the index.
     * @param flatKeyAttributes The set of attributes used as keys to index the node, given as a flat string separated by ','. The order does not matter.
     * @param callback          Called when the indexing is done. The parameter specifies whether or not the indexing has succeeded.
     */
    void index(String indexName, Node nodeToIndex, String flatKeyAttributes, Callback<Boolean> callback);

    /**
     * Adds the {@code nodeToIndex} to the global index identified by {@code indexName}.<br>
     * If the index does not exist, it is created on the fly.<br>
     * The node is referenced by its {@code keyAttributes} in the index, and can be retrieved with {@link #find(long, long, String, String, Callback)} using the same attributes.
     *
     * @param world             The world in which to index
     * @param time              The time at which to index
     * @param indexName         A string uniquely identifying the index in the {@link Graph}.
     * @param nodeToIndex       The node to add in the index.
     * @param flatKeyAttributes The set of attributes used as keys to index the node, given as a flat string separated by ','. The order does not matter.
     * @param callback          Called when the indexing is done. The parameter specifies whether or not the indexing has succeeded.
     */
    void indexAt(long world, long time, String indexName, Node nodeToIndex, String flatKeyAttributes, Callback<Boolean> callback);

    /**
     * Removes the {@code nodeToUnindex} from the global index identified by {@code indexName}.<br>
     *
     * @param indexName         A string uniquely identifying the index in the {@link Graph}.
     * @param nodeToUnindex     The node to remove from the index.
     * @param flatKeyAttributes The set of attributes used as keys to index the node, given as a flat string separated by ','. The order does not matter.
     * @param callback          Called when the unindexing is done. The parameter specifies whether or not the unindexing has succeeded.
     */
    void unindex(String indexName, Node nodeToUnindex, String flatKeyAttributes, Callback<Boolean> callback);

    /**
     * Removes the {@code nodeToUnindex} from the global index identified by {@code indexName}.<br>
     *
     * @param world             The world in which to unindex
     * @param time              The time at which to unindex
     * @param indexName         A string uniquely identifying the index in the {@link Graph}.
     * @param nodeToUnindex     The node to remove from the index.
     * @param flatKeyAttributes The set of attributes used as keys to index the node, given as a flat string separated by ','. The order does not matter.
     * @param callback          Called when the unindexing is done. The parameter specifies whether or not the unindexing has succeeded.
     */
    void unindexAt(long world, long time, String indexName, Node nodeToUnindex, String flatKeyAttributes, Callback<Boolean> callback);

    /**
     * Retrieve the list of indexes.
     *
     * @param world    The world id in which the search must be performed.
     * @param time     The timepoint at which the search must be performed.
     * @param callback Called when the retrieval is complete. Returns the retrieved indexes names, empty array otherwise.
     */
    void indexes(long world, long time, Callback<String[]> callback);

    /**
     * Retrieves from an index nodes that satisfy the query.<br>
     * The query must be defined using at least a sub-set of the attributes used for the indexing.<br>
     * The form of the query is a list of &lt;key, value&gt; tuples (i.e.: "&lt;attName&gt;=&lt;val&gt;, &lt;attName2&gt;=&lt;val2&gt;,...").<br>
     * e.g: "name=john,age=30"
     *
     * @param world     The world id in which the search must be performed.
     * @param time      The timepoint at which the search must be performed.
     * @param indexName The name of the index in which to search.
     * @param query     The query nodes must satisfy.
     * @param callback  Called when the search is finished. The requested nodes are given in parameter, empty array otherwise.
     */
    void find(long world, long time, String indexName, String query, Callback<Node[]> callback);

    /**
     * Retrieves nodes from a global index that satisfy the query object passed as parameter.<br>
     *
     * @param query    The query to satisfy
     * @param callback Called when the search is finished. The requested nodes are given in parameter, empty array otherwise.
     */
    void findByQuery(Query query, Callback<Node[]> callback);

    /**
     * Retrieves all nodes registered in a particular index.
     *
     * @param world     The world from which the index must be retrieved.
     * @param time      The timepoint at which the index must be retrieved.
     * @param indexName The unique identifier of the index.
     * @param callback  Called when the retrieval is complete. Returns all nodes in the index in an array, an empty array otherwise.
     */
    void findAll(long world, long time, String indexName, Callback<Node[]> callback);

    /**
     * Retrieve the back-end node behind a named index.
     *
     * @param world     The world from which the index must be retrieved.
     * @param time      The timepoint at which the index must be retrieved.
     * @param indexName The unique identifier of the index.
     * @param callback  Called when the retrieval is complete. Returns the retrieved index node, null otherwise.
     */
    void getIndexNode(long world, long time, String indexName, Callback<Node> callback);

    /**
     * Utility method to create a waiter based on a counter
     *
     * @param expectedEventsCount number of events expected before running a task.
     * @return The waiter object.
     */
    DeferCounter newCounter(int expectedEventsCount);

    /**
     * Utility method to create a sync waiter based on a counter
     *
     * @param expectedEventsCount number of events expected before running a task.
     * @return The waiter object.
     */
    DeferCounterSync newSyncCounter(int expectedEventsCount);

    /**
     * Retrieves the current resolver
     *
     * @return current resolver
     */
    Resolver resolver();

    /**
     * Retrieves the current scheduler
     *
     * @return current scheduler
     */
    Scheduler scheduler();

    /**
     * Retrieves the current space
     *
     * @return current space
     */
    ChunkSpace space();

    /**
     * Retrieves the current storage
     *
     * @return current storage
     */
    Storage storage();

    /**
     * Creates a new buffer for serialization and loading methods
     *
     * @return newly created buffer
     */
    Buffer newBuffer();

    /**
     * Creates a new query that can be executed on the graph.
     *
     * @return newly created query
     */
    Query newQuery();

    /**
     * Free the array of nodes (sequentially call the free method on all nodes)
     *
     * @param nodes the array of nodes to free
     */
    void freeNodes(Node[] nodes);

    /**
     * Retrieve a task action factory, resolved by its name
     *
     * @param name of the task action
     * @return the resolved task action or null if not configured
     */
    TaskActionFactory taskAction(String name);

    /**
     * Retrieve a external attribute factory, resolved by its name
     *
     * @param name of the task action
     * @return the resolved external attribute factory or null if not configured
     */
    ExternalAttributeFactory externalAttribute(String name);

    /**
     * Retrieve the default task hook factory
     *
     * @return the current default task hook factory
     */
    TaskHookFactory taskHookFactory();

}
