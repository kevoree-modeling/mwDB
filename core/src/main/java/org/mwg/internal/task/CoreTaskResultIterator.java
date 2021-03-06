/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.internal.task;

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
