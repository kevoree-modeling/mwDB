package org.mwg;

import org.mwg.chunk.ChunkSpace;
import org.mwg.plugin.*;
import org.mwg.struct.Buffer;
import org.mwg.struct.IndexedRelationship;
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
     * Disconnects the current graph from its storage (a save will be trigger safely before the exit)
     *
     * @param callback Called when the disconnection is completed. The parameter specifies whether or not the disconnection succeeded.
     */
    void disconnect(Callback<Boolean> callback);

    /**
     * Retrieve a named global index, at a precise world and time
     *
     * @param world
     * @param time
     * @param name
     * @param callback
     */
    void index(long world, long time, String name, Callback<NodeIndex> callback);

    /**
     * Retrieve the list of indexes.
     *
     * @param world    The world id in which the search must be performed.
     * @param time     The timepoint at which the search must be performed.
     * @param callback Called when the retrieval is complete. Returns the retrieved indexes names, empty array otherwise.
     */
    void indexNames(long world, long time, Callback<String[]> callback);

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
