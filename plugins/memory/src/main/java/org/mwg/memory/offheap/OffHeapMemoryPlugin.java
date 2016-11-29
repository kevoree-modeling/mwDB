package org.mwg.memory.offheap;


import org.mwg.base.BasePlugin;

public class OffHeapMemoryPlugin extends BasePlugin {

    public OffHeapMemoryPlugin() {
        declareMemoryFactory(new OffHeapMemoryFactory());
    }
}
