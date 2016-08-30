package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.GenChunk;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;

final class OffHeapGenChunk implements GenChunk {

    private final OffHeapChunkSpace space;
    private final long index;
    private final long prefix;
    private final long addr;

    private static final int SEED = 0;
    private static final int LOCK = 1;
    private static final int DIRTY = 2;
    private static final int CHUNK_SIZE = 3;

    OffHeapGenChunk(final OffHeapChunkSpace p_space, final long p_id, final long p_index) {
        index = p_index;
        space = p_space;
        //moves the prefix 53-size(short) times to the left;
        prefix = p_id << (Constants.LONG_SIZE - Constants.PREFIX_SIZE);
        long temp_addr = space.addrByIndex(index);
        if (temp_addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            temp_addr = OffHeapLongArray.allocate(CHUNK_SIZE);
            space.setAddrByIndex(index, temp_addr);
            OffHeapLongArray.set(temp_addr, SEED, -1);
            OffHeapLongArray.set(temp_addr, LOCK, 0);
            OffHeapLongArray.set(temp_addr, DIRTY, 0);
        }
        addr = temp_addr;
    }

    public static void free(final long addr) {
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            OffHeapLongArray.free(addr);
        }
    }

    @Override
    public final void save(final Buffer buffer) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            Base64.encodeLongToBuffer(OffHeapLongArray.get(addr, SEED), buffer);
            OffHeapLongArray.set(addr, DIRTY, 0);
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                System.out.println("CAS error !!!");
            }
        }
    }

    @Override
    public final void load(final Buffer buffer) {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            if (buffer == null || buffer.length() == 0) {
                return;
            }
            long loaded = Base64.decodeToLongWithBounds(buffer, 0, buffer.length());
            long previousSeed = OffHeapLongArray.get(addr, SEED);
            OffHeapLongArray.set(addr, SEED, loaded);
            if (previousSeed != -1 && previousSeed != loaded) {
                if (space != null && OffHeapLongArray.get(addr, DIRTY) != 1) {
                    OffHeapLongArray.set(addr, DIRTY, 1);
                    space.notifyUpdate(index);
                }
            }
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                System.out.println("CAS error !!!");
            }
        }
    }

    @Override
    public final long newKey() {
        while (!OffHeapLongArray.compareAndSwap(addr, LOCK, 0, 1)) ;
        try {
            long seed = OffHeapLongArray.get(addr, SEED);
            if (seed == Constants.KEY_PREFIX_MASK) {
                throw new IndexOutOfBoundsException("Object Index could not be created because it exceeded the capacity of the current prefix. Ask for a new prefix.");
            }
            if (seed == -1) {
                seed = 0;
            }
            seed++;
            OffHeapLongArray.set(addr, SEED, seed);
            long objectKey = prefix + seed;
            if (space != null) {
                space.notifyUpdate(index);
            }
            if (objectKey >= Constants.END_OF_TIME) {
                throw new IndexOutOfBoundsException("Object Index exceeds the maximum JavaScript number capacity. (2^" + Constants.LONG_SIZE + ")");
            }
            return objectKey;
        } finally {
            if (!OffHeapLongArray.compareAndSwap(addr, LOCK, 1, 0)) {
                System.out.println("CAS error !!!");
            }
        }
    }

    @Override
    public final long index() {
        return index;
    }

    @Override
    public final long world() {
        return space.worldByIndex(index);
    }

    @Override
    public final long time() {
        return space.timeByIndex(index);
    }

    @Override
    public final long id() {
        return space.idByIndex(index);
    }

    @Override
    public final byte chunkType() {
        return ChunkType.GEN_CHUNK;
    }

}