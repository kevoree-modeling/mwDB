package org.mwg.struct;

/**
 * Buffer defines the interface to exchange byte[] (payload), between Storage and the various KChunk
 */
public interface Buffer {

    /**
     * Append a byte to the buffer
     *
     * @param b byte to append
     */
    void write(byte b);

    /**
     * Append an array of bytes to the buffer
     *
     * @param bytes byte array to append
     */
    void writeAll(byte[] bytes);

    /**
     * Read the buffer at a precise position
     *
     * @param position index in the buffer
     * @return read byte
     */
    byte read(long position);

    /**
     * Extract data as byte[]
     *
     * @return content as native byte[]
     */
    byte[] data();

    /**
     * Size of the buffer
     *
     * @return length of the buffer
     */
    long length();

    /**
     * Free the buffer from memory, this method should be the last called
     */
    void free();

    /**
     * Create a new iterator for this buffer
     *
     * @return the newly created iterator
     */
    BufferIterator iterator();

    void removeLast();

    byte[] slice(long initPos, long endPos);

}
