package org.mwg.struct;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.Query;

public interface RelationIndexed {

    long size();

    long[] all();

    RelationIndexed add(Node node, String... attributeNames);

    RelationIndexed remove(Node node, String... attributeNames);

    RelationIndexed clear();

    void find(String query, Callback<Node[]> callback);

    void findUsing(Callback<Node[]> callback, String... params);

    void findByQuery(Query query, Callback<Node[]> callback);

}
