package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

import java.util.ArrayList;

class CoreTaskResult<A> implements TaskResult<A> {

    private Object[] _backend;
    private int _capacity = 0;
    private int _size = 0;

    @Override
    public Object[] asArray() {
        Object[] flat = new Object[_size];
        if (_backend != null) {
            System.arraycopy(_backend, 0, flat, 0, _size);
        }
        return flat;
    }

    CoreTaskResult(Object toWrap, boolean protect) {
        if (toWrap instanceof Object[]) {
            Object[] castedToWrap = (Object[]) toWrap;
            _size = ((Object[]) toWrap).length;
            _capacity = _size;
            _backend = new Object[this._size];
            if (protect) {
                for (int i = 0; i < _size; i++) {
                    Object loopObj = castedToWrap[i];
                    if (loopObj instanceof BaseNode) {
                        Node loopNode = (Node) loopObj;
                        _backend[i] = loopNode.graph().cloneNode(loopNode);
                    } else {
                        _backend[i] = loopObj;
                    }
                }
            } else {
                System.arraycopy(castedToWrap, 0, _backend, 0, this._size);
            }
        } else if (toWrap instanceof long[]) {
            long[] castedOther = (long[]) toWrap;
            _backend = new Object[castedOther.length];
            for (int i = 0; i < castedOther.length; i++) {
                _backend[i] = castedOther[i];
            }
            _capacity = _backend.length;
            _size = _capacity;
        } else if (toWrap instanceof int[]) {
            int[] castedOther = (int[]) toWrap;
            _backend = new Object[castedOther.length];
            for (int i = 0; i < castedOther.length; i++) {
                _backend[i] = castedOther[i];
            }
            _capacity = _backend.length;
            _size = _capacity;
        } else if (toWrap instanceof double[]) {
            double[] castedOther = (double[]) toWrap;
            _backend = new Object[castedOther.length];
            for (int i = 0; i < castedOther.length; i++) {
                _backend[i] = castedOther[i];
            }
            _capacity = _backend.length;
            _size = _capacity;
        } else if (toWrap instanceof ArrayList) {
            ArrayList<Object> castedOtherList = (ArrayList<Object>) toWrap;
            _backend = new Object[castedOtherList.size()];
            for (int i = 0; i < castedOtherList.size(); i++) {
                _backend[i] = castedOtherList.get(i);
            }
            _capacity = _backend.length;
            _size = _capacity;
        } else if (toWrap instanceof CoreTaskResult) {
            CoreTaskResult other = (CoreTaskResult) toWrap;
            _size = other._size;
            _capacity = other._capacity;
            if (other._backend != null) {
                _backend = new Object[other._backend.length];
                if (protect) {
                    for (int i = 0; i < _size; i++) {
                        Object loopObj = other._backend[i];
                        if (loopObj instanceof BaseNode) {
                            Node loopNode = (Node) loopObj;
                            _backend[i] = loopNode.graph().cloneNode(loopNode);
                        } else {
                            _backend[i] = loopObj;
                        }
                    }
                } else {
                    System.arraycopy(other._backend, 0, _backend, 0, _size);
                }
            }
        } else {
            if (toWrap != null) {
                _backend = new Object[1];
                _capacity = 1;
                _size = 1;
                if (toWrap instanceof BaseNode) {
                    Node toWrapNode = (Node) toWrap;
                    if (protect) {
                        _backend[0] = toWrapNode.graph().cloneNode(toWrapNode);
                    } else {
                        _backend[0] = toWrapNode;
                    }
                } else {
                    _backend[0] = toWrap;
                }
            }
        }
    }

    @Override
    public TaskResultIterator iterator() {
        return new CoreTaskResultIterator(_backend);
    }

    @Override
    public A get(int index) {
        if (index < _size) {
            return (A) _backend[index];
        } else {
            return null;
        }
    }

    @Override
    public void set(int index, A input) {
        if (index >= _capacity) {
            extendTil(index);
        }
        _backend[index] = input;
        if (index >= _size) {
            _size++;
        }
    }

    @Override
    public void allocate(int index) {
        if (index >= _capacity) {
            if (_backend == null) {
                _backend = new Object[index];
                _capacity = index;
            } else {
                throw new RuntimeException("Not implemented yet!!!");
            }
        }
    }

    @Override
    public void add(A input) {
        if (_size >= _capacity) {
            extendTil(_size);
        }
        set(_size, input);
    }

    @Override
    public void clear() {
        this._backend = null;
        this._capacity = 0;
        this._size = 0;
    }

    @Override
    public TaskResult<A> clone() {
        return new CoreTaskResult<A>(this, true);
    }

    @Override
    public void free() {
        for (int i = 0; i < _capacity; i++) {
            if (_backend[i] instanceof BaseNode) {
                ((Node) _backend[i]).free();
            }
        }
    }

    @Override
    public int size() {
        return this._size;
    }

    private synchronized void extendTil(int index) {
        if (_capacity <= index) {
            int newCapacity = _capacity * 2;
            if (newCapacity <= index) {
                newCapacity = index + 1;
            }
            Object[] extendedBackend = new Object[newCapacity];
            if (_backend != null) {
                System.arraycopy(_backend, 0, extendedBackend, 0, _size);
            }
            _backend = extendedBackend;
            _capacity = newCapacity;
        }
    }

    @Override
    public String toString() {
        return toJson(true);
    }

    private String toJson(boolean withContent) {
        final StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < _size; i++) {
            if (i != 0) {
                builder.append(",");
            }
            Object loop = _backend[i];
            if (loop != null) {
                builder.append(loop.toString());
            }
        }
        builder.append("]");
        return builder.toString();
    }

}
