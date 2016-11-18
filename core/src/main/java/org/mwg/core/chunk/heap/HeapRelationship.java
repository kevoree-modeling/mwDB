package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.struct.Relationship;

class HeapRelationship implements Relationship {

    private long[] _back;
    private volatile int _size;
    private final HeapStateChunk parent;
    private boolean aligned = true;

    HeapRelationship(final HeapStateChunk p_listener, final HeapRelationship origin) {
        parent = p_listener;
        if (origin != null) {
            aligned = false;
            _back = origin._back;
            _size = origin._size;
        } else {
            _back = null;
            _size = 0;
        }
    }

    final void allocate(int _capacity) {
        long[] new_back = new long[_capacity];
        if (_back != null) {
            System.arraycopy(_back, 0, new_back, 0, _back.length);
        }
        _back = new_back;
        aligned = true;
    }

    @Override
    public final int size() {
        return _size;
    }

    @Override
    public final long get(int index) {
        long result;
        synchronized (parent) {
            result = _back[index];
        }
        return result;
    }

    @Override
    public void set(int index, long value) {
        synchronized (parent) {
            _back[index] = value;
        }
    }

    final long unsafe_get(int index) {
        return _back[index];
    }

    @Override
    public final Relationship add(long newValue) {
        synchronized (parent) {
            if (_back == null) {
                aligned = true;
                _back = new long[Constants.MAP_INITIAL_CAPACITY];
                _back[0] = newValue;
                _size = 1;
            } else if (_size == _back.length) {
                long[] ex_back = new long[_back.length * 2];
                System.arraycopy(_back, 0, ex_back, 0, _size);
                _back = ex_back;
                _back[_size] = newValue;
                aligned = true;
                _size++;
            } else {
                if (!aligned) {
                    long[] temp_back = new long[_back.length];
                    System.arraycopy(_back, 0, temp_back, 0, _back.length);
                    _back = temp_back;
                    aligned = true;
                }
                _back[_size] = newValue;
                _size++;
            }
            parent.declareDirty();
        }
        return this;
    }

    @Override
    public Relationship insert(final int targetIndex, final long newValue) {
        synchronized (parent) {
            if (_back == null) {
                if (targetIndex != 0) {
                    throw new RuntimeException("Bad API usage ! index out of bounds: " + targetIndex);
                }
                _back = new long[Constants.MAP_INITIAL_CAPACITY];
                _back[0] = newValue;
                _size = 1;
                aligned = true;
            } else if (_size == _back.length) {
                if (targetIndex > _size) {
                    throw new RuntimeException("Bad API usage ! index out of bounds: " + targetIndex);
                }
                final long[] ex_back = new long[_back.length * 2];
                if (_size == targetIndex) {
                    System.arraycopy(_back, 0, ex_back, 0, _size);
                    _back = ex_back;
                    _back[_size] = newValue;
                    _size++;
                } else {
                    System.arraycopy(_back, 0, ex_back, 0, targetIndex);
                    _back[targetIndex] = newValue;
                    System.arraycopy(_back, targetIndex, ex_back, targetIndex + 1, (_size - targetIndex));
                    _back = ex_back;
                    _size++;
                }
                aligned = true;
            } else {
                if (targetIndex > _size) {
                    throw new RuntimeException("Bad API usage ! index out of bounds: " + targetIndex);
                }
                if (!aligned) {
                    long[] temp_back = new long[_back.length];
                    System.arraycopy(_back, 0, temp_back, 0, _back.length);
                    _back = temp_back;
                    aligned = true;
                }
                final int afterIndexSize = _size - targetIndex;
                long[] temp = new long[afterIndexSize];
                System.arraycopy(_back, targetIndex, temp, 0, afterIndexSize);
                _back[targetIndex] = newValue;
                System.arraycopy(temp, 0, _back, targetIndex + 1, afterIndexSize);
                _size++;
            }
            parent.declareDirty();
        }
        return this;
    }

    @Override
    public Relationship remove(long oldValue) {
        synchronized (parent) {
            int indexToRemove = -1;
            for (int i = 0; i < _size; i++) {
                if (_back[i] == oldValue) {
                    indexToRemove = i;
                    break;
                }
            }
            if (indexToRemove != -1) {
                if (!aligned) {
                    long[] temp_back = new long[_back.length];
                    System.arraycopy(_back, 0, temp_back, 0, _back.length);
                    _back = temp_back;
                    aligned = true;
                }
                System.arraycopy(_back, indexToRemove + 1, _back, indexToRemove, _size - indexToRemove - 1);
                _size--;
            }
        }
        return this;
    }

    @Override
    public Relationship delete(int toRemoveIndex) {
        synchronized (parent) {
            if (toRemoveIndex != -1) {
                if (!aligned) {
                    long[] temp_back = new long[_back.length];
                    System.arraycopy(_back, 0, temp_back, 0, _back.length);
                    _back = temp_back;
                    aligned = true;
                }
                System.arraycopy(_back, toRemoveIndex + 1, _back, toRemoveIndex, _size - toRemoveIndex - 1);
                _size--;
            }
        }
        return this;
    }

    @Override
    public Relationship clear() {
        synchronized (parent) {
            _size = 0;
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        for (int i = 0; i < _size; i++) {
            if (i != 0) {
                buffer.append(",");
            }
            buffer.append(_back[i]);
        }
        buffer.append("]");
        return buffer.toString();
    }
}
