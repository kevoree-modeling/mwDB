package org.mwg;

/**
 * Defines a set of rules for filtering nodes from the graph.
 */
public interface Query {

    /**
     * Fills this query with elements from a String
     * @param flatQuery the stringified query
     * @return the {@link Query}, for a fluent API
     */
    Query parse(String flatQuery);

    /**
     * Adds a filtering element based on the value of an attribute
     * @param attributeName the name of the attribute
     * @param value the value of the attribute for which nodes have to be collected
     * @return the {@link Query}, for a fluent API
     */
    Query add(String attributeName, String value);

    /**
     * Sets the world in which to execute the Query.
     * @param initialWorld the world id
     * @return the {@link Query}, for a fluent API
     */
    Query setWorld(long initialWorld);

    /**
     * Retrieves the world id in which the query is executed.
     * @return the world id
     */
    long world();

    /**
     * Sets the time at which to execute this query.
     * @param initialTime the time
     * @return the {@link Query}, for a fluent API
     */
    Query setTime(long initialTime);

    /**
     * Retrieves the time in which the query is executed.
     * @return the time
     */
    long time();

    /**
     * Sets by name the index in which the query is executed
     * @param indexName the name of the index
     * @return the {@link Query}, for a fluent API
     */
    Query setIndexName(String indexName);

    /**
     * Retrieves the index name in which the query is executed.
     * @return the index name
     */
    String indexName();

    /**
     * Returns the hash code of this query
     * @return the hash code
     */
    long hash();

    /**
     * Returns the attributes used in this query
     * @return the array of attributes used in this query
     */
    long[] attributes();

    /**
     * Returns the values of attributes used in this query to filter nodes
     * @return the values of attributes used in this query to filter nodes
     */
    Object[] values();

}



