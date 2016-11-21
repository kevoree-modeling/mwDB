package org.mwg.struct;

import org.mwg.Node;

public interface IndexedRelationship {

    int size();

    long get(int index);

    void set(int index, long value);

    IndexedRelationship add(Node node, String attributeNames);

    IndexedRelationship remove(Node node, String attributeNames);

    IndexedRelationship clear();

}
