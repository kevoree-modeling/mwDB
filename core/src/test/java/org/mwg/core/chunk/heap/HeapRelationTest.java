package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractRelationTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapRelationTest extends AbstractRelationTest {

    public HeapRelationTest() {
        super(new HeapMemoryFactory());
    }

}
