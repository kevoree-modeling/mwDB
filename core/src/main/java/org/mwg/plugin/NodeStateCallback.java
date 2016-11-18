package org.mwg.plugin;

@FunctionalInterface
public interface NodeStateCallback {

    void on(long attributeKey, byte elemType, Object elem);

}
