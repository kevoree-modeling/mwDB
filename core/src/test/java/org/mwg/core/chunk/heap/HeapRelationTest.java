package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractRelationshipTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapRelationTest extends AbstractRelationshipTest {

    public HeapRelationTest() {
        super(new HeapMemoryFactory());
    }

}
