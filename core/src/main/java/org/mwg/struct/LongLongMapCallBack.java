package org.mwg.struct;

@FunctionalInterface
public interface LongLongMapCallBack {
    void on(long key, long value);
}
