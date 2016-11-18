package org.mwg.struct;

@FunctionalInterface
public interface StringLongMapCallBack {
    void on(String key, long value);
}
