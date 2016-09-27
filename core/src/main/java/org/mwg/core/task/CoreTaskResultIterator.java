package org.mwg.core.task;

import org.mwg.task.TaskResultIterator;
import org.mwg.utility.Tuple;

import java.util.concurrent.atomic.AtomicInteger;

class CoreTaskResultIterator<A> implements TaskResultIterator<A> {

    private final Object[] _backend;
    private final int _size;
    private final AtomicInteger _current;

    CoreTaskResultIterator(Object[] p_backend) {
        _current = new AtomicInteger(0);
        if (p_backend != null) {
            this._backend = p_backend;
        } else {
            _backend = new Object[0];
        }
        _size = _backend.length;
    }

    @Override
    public A next() {
        final int cursor = _current.getAndIncrement();
        if (cursor < _size) {
            return (A) _backend[cursor];
        } else {
            return null;
        }
    }

    @Override
    public Tuple<Integer, A> nextWithIndex() {
        final int cursor = _current.getAndIncrement();
        if (cursor < _size) {
            if (_backend[cursor] != null) {
                return new Tuple<Integer, A>(cursor, (A) _backend[cursor]);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
