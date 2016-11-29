package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.Relation;
import org.mwg.utility.Base64;

@SuppressWarnings("Duplicates")
class OffHeapRelation implements Relation {

    private static int CAPACITY = 0;
    private static int SIZE = 1;
    private static int SHIFT = 2;

    private final long index;
    private final OffHeapStateChunk chunk;

    OffHeapRelation(final OffHeapStateChunk p_chunk, final long p_index) {
        chunk = p_chunk;
        index = p_index;
    }

    @Override
    public long[] all() {
        long[] ids;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                ids = new long[0];
            } else {
                final long relSize = OffHeapLongArray.get(addr, SIZE);
                ids = new long[(int) relSize];
                for (int i = 0; i < relSize; i++) {
                    ids[i] = OffHeapLongArray.get(addr, i + SHIFT);
                }
            }
        } finally {
            chunk.unlock();
        }
        return ids;
    }

    public final void allocate(int newCapacity) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                //initial allocation
                final long newly = OffHeapLongArray.allocate(newCapacity + SHIFT);
                OffHeapLongArray.set(newly, SIZE, 0);
                OffHeapLongArray.set(newly, CAPACITY, newCapacity);
                chunk.setAddrByIndex(index, newly);
            } else {
                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                if (capacity < newCapacity) {
                    //extends
                    long exAddr = OffHeapLongArray.reallocate(addr, newCapacity + SHIFT);
                    chunk.setAddrByIndex(index, exAddr);
                    OffHeapLongArray.set(exAddr, CAPACITY, newCapacity);
                }
            }
        } finally {
            chunk.unlock();
        }
    }

    @Override
    public final int size() {
        chunk.lock();
        long size = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                size = OffHeapLongArray.get(addr, SIZE);
            }
        } finally {
            chunk.unlock();
        }
        return (int) size;
    }

    @Override
    public final long get(final int elemIndex) {
        long result = -1;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                if (elemIndex < size) {
                    result = OffHeapLongArray.get(addr, elemIndex + SHIFT);
                } else {
                    return -1;
                }
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public void set(int index, long value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                if (index < size) {
                    OffHeapLongArray.set(addr, index + SHIFT, value);
                }
            }
        } finally {
            chunk.unlock();
        }
    }

    @Override
    public final Relation add(final long newValue) {
        chunk.lock();
        try {
            internal_add(newValue);
            chunk.declareDirty();
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public Relation addNode(Node node) {
        return add(node.id());
    }

    @Override
    public Relation insert(int insertIndex, long newValue) {
        chunk.lock();
        try {
            long size = 0;
            long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                size = OffHeapLongArray.get(addr, SIZE);
            }
            if (size == 0) {
                if (insertIndex != 0) {
                    throw new RuntimeException("Bad API usage ! index out of bounds: " + index);
                }
                long capacity = Constants.MAP_INITIAL_CAPACITY;
                addr = OffHeapLongArray.allocate(SHIFT + capacity);
                OffHeapLongArray.set(addr, CAPACITY, capacity);
                chunk.setAddrByIndex(index, addr);
                OffHeapLongArray.set(addr, SHIFT, newValue);
                OffHeapLongArray.set(addr, SIZE, 1);
            } else {
                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                if (capacity == size) {
                    //need to reallocate first
                    final long newCapacity = capacity * 2;
                    addr = OffHeapLongArray.reallocate(addr, newCapacity + SHIFT);
                    chunk.setAddrByIndex(index, addr);
                    OffHeapLongArray.set(addr, CAPACITY, newCapacity);
                }
                OffHeapLongArray.insert(addr, SHIFT + insertIndex, newValue, size + SHIFT);
                OffHeapLongArray.set(addr, SIZE, size + 1);
            }
            chunk.declareDirty();
        } finally {
            chunk.unlock();
        }
        return this;
    }

    final void internal_add(final long newValue) {
        long addr = chunk.addrByIndex(index);
        long size;
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            long capacity = Constants.MAP_INITIAL_CAPACITY;
            addr = OffHeapLongArray.allocate(SHIFT + capacity);
            OffHeapLongArray.set(addr, CAPACITY, capacity);
            chunk.setAddrByIndex(index, addr);
            size = 0;
        } else {
            size = OffHeapLongArray.get(addr, SIZE);
            final long capacity = OffHeapLongArray.get(addr, CAPACITY);
            if (size == capacity) {
                final long newCapacity = capacity * 2;
                addr = OffHeapLongArray.reallocate(addr, newCapacity + SHIFT);
                chunk.setAddrByIndex(index, addr);
                OffHeapLongArray.set(addr, CAPACITY, newCapacity);
            }
        }
        OffHeapLongArray.set(addr, size + SHIFT, newValue);
        OffHeapLongArray.set(addr, SIZE, size + 1);
    }

    @Override
    public final Relation remove(final long oldValue) {
        boolean leftShift = false;
        chunk.lock();
        try {
            long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                for (int i = 0; i < size; i++) {
                    long current = OffHeapLongArray.get(addr, SHIFT + i);
                    if (leftShift) {
                        OffHeapLongArray.set(addr, SHIFT + i - 1, current);
                    } else {
                        if (current == oldValue) {
                            leftShift = true;
                        }
                    }
                }
                if (leftShift) {
                    OffHeapLongArray.set(addr, SIZE, size - 1);
                    chunk.declareDirty();
                }
            }

        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public Relation delete(int indexToDelete) {
        chunk.lock();
        try {
            long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                OffHeapLongArray.delete(addr, indexToDelete, size);
                OffHeapLongArray.set(addr, SIZE, size - 1);
                chunk.declareDirty();
            }

        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public final Relation clear() {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.set(addr, SIZE, 0);
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }


    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        //chunk.lock();
        // try {
        final long addr = chunk.addrByIndex(index);
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            buffer.append("[");
            final long size = OffHeapLongArray.get(addr, SIZE);
            for (int i = 0; i < size; i++) {
                if (i != 0) {
                    buffer.append(",");
                }
                buffer.append(OffHeapLongArray.get(addr, i + SHIFT));
            }
            buffer.append("]");
        }
        //} finally {
        // chunk.unlock();
        //}
        return buffer.toString();
    }

    static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return;
        }
        final long size = OffHeapLongArray.get(addr, SIZE);
        Base64.encodeLongToBuffer(size, buffer);
        for (long i = 0; i < size; i++) {
            buffer.write(Constants.CHUNK_SUB_SUB_SEP);
            Base64.encodeLongToBuffer(OffHeapLongArray.get(addr, i + SHIFT), buffer);
        }
    }

    static long clone(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return OffHeapConstants.OFFHEAP_NULL_PTR;
        }
        final long capacity = OffHeapLongArray.get(addr, CAPACITY);
        return OffHeapLongArray.cloneArray(addr, capacity + SHIFT);
    }

    static void free(final long addr) {
        OffHeapLongArray.free(addr);
    }

}
