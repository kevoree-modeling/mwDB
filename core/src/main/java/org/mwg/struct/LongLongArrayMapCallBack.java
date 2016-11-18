package org.mwg.struct;

@FunctionalInterface
public interface LongLongArrayMapCallBack {
    void on(long key, long value);
}
