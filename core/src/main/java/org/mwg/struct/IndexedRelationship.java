package org.mwg.struct;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.Query;

public interface IndexedRelationship {

    long size();

    long[] all();

    IndexedRelationship add(Node node, String... attributeNames);

    IndexedRelationship remove(Node node, String... attributeNames);

    IndexedRelationship clear();

    void find(String query, Callback<Node[]> callback);

    void findUsing(Callback<Node[]> callback, String... params);

    void findByQuery(Query query, Callback<Node[]> callback);

}
