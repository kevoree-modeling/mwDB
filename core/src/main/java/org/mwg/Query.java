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



