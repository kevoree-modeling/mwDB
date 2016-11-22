package org.mwg.struct;

import org.mwg.Callback;
import org.mwg.Node;

public interface IndexedRelationship {

    long size();

    long[] all();

    IndexedRelationship add(Node node, String... attributeNames);

    IndexedRelationship remove(Node node, String... attributeNames);

    IndexedRelationship find(String query, Callback<Node[]> callback);

    IndexedRelationship clear();
    
}
