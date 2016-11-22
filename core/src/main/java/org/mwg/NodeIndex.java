package org.mwg;

public interface NodeIndex extends Node {

    long size();

    long[] all();


    NodeIndex add(Node node, String... attributeNames);

    NodeIndex remove(Node node, String... attributeNames);

    NodeIndex clear();

    void findAll(Callback<Node[]> callback);

    void find(String query, Callback<Node[]> callback);

    void findUsing(Callback<Node[]> callback, String... params);

    void findByQuery(Query query, Callback<Node[]> callback);

}
