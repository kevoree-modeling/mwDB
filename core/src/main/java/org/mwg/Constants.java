package org.mwg;

/**
 * Static constants used
 */
public class Constants {

    public static final int KEY_SIZE = 4;

    // Limit long lengths to 53 bits because of JS limitation
    public static final int LONG_SIZE = 53;

    public static final int PREFIX_SIZE = 16;

    public static final long BEGINNING_OF_TIME = -0x001FFFFFFFFFFFFEl;

    public static final long END_OF_TIME = 0x001FFFFFFFFFFFFEl;

    public static final long NULL_LONG = 0x001FFFFFFFFFFFFFl;

    // Limit limit local index to LONG limit - prefix size
    public static final long KEY_PREFIX_MASK = 0x0000001FFFFFFFFFl;

    public static final String CACHE_MISS_ERROR = "Cache miss error";

    public static final char QUERY_SEP = ',';

    public static final char QUERY_KV_SEP = '=';

    public static final char TASK_SEP = '.';

    public static final char TASK_PARAM_OPEN = '(';

    public static final char TASK_PARAM_CLOSE = ')';

    /**
     * {@native ts
     * public static CHUNK_SEP : number = "|".charCodeAt(0);
     * }
     */
    public static final byte CHUNK_SEP = '|';

    /**
     * {@native ts
     * public static CHUNK_SUB_SEP : number = ",".charCodeAt(0);
     * }
     */
    public static final byte CHUNK_SUB_SEP = ',';

    /**
     * {@native ts
     * public static CHUNK_SUB_SUB_SEP : number = ":".charCodeAt(0);
     * }
     */
    public static final byte CHUNK_SUB_SUB_SEP = ':';

    /**
     * {@native ts
     * public static CHUNK_SUB_SUB_SUB_SEP : number = "%".charCodeAt(0);
     * }
     */
    public static final byte CHUNK_SUB_SUB_SUB_SEP = '%';

    /**
     * {@native ts
     * return param != undefined && param != null;
     * }
     */
    /**
     * Checks if a parameter is defined (!= null)
     *
     * @param param The parameter to test
     * @return true if not null, false otherwise.
     */
    public static boolean isDefined(Object param) {
        return param != null;
    }

    /**
     * {@native ts
     * return src === other
     * }
     */
    /**
     * Tests if an object is equal to another. This is an indirection method to offer an alternative implementation for JS
     *
     * @param src   the first object
     * @param other the second object
     * @return true if objects are equal, false otherwise.
     */
    public static boolean equals(String src, String other) {
        return src.equals(other);
    }

    public static boolean longArrayEquals(long[] src, long[] other) {
        if (src.length != other.length) {
            return false;
        }
        for (int i = 0; i < src.length; i++) {
            if (src[i] != other[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@native ts
     * public static BUFFER_SEP : number = "#".charCodeAt(0);
     * }
     */
    public static final byte BUFFER_SEP = '#';

    /**
     * Chunk Save/Load special chars
     */
    /**
     * {@native ts
     * public static KEY_SEP : number = ";".charCodeAt(0);
     * }
     */
    public static final byte KEY_SEP = ';';

    public static final int MAP_INITIAL_CAPACITY = 8;

    /**
     * @native ts
     * public static BOOL_TRUE : number = "1".charCodeAt(0);
     */
    public static byte BOOL_TRUE = (byte) '1';
    /**
     * @native ts
     * public static BOOL_FALSE : number = "0".charCodeAt(0);
     */
    public static byte BOOL_FALSE = (byte) '0';

}

