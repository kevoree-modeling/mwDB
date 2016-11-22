package org.mwg;

/**
 * Defines the constants used in mwDB.
 */
public class Type {

    /**
     * Primitive Types
     */
    public static final byte BOOL = 1;
    public static final byte STRING = 2;
    public static final byte LONG = 3;
    public static final byte INT = 4;
    public static final byte DOUBLE = 5;

    /*
     * Primitive Arrays
     */
    public static final byte DOUBLE_ARRAY = 6;
    public static final byte LONG_ARRAY = 7;
    public static final byte INT_ARRAY = 8;
    public static final byte LONG_TO_LONG_MAP = 9;
    public static final byte LONG_TO_LONG_ARRAY_MAP = 10;
    public static final byte STRING_TO_LONG_MAP = 11;
    public static final byte RELATION = 12;
    public static final byte INDEXED_RELATION = 13;
    public static final byte MULTI_INDEXED_RELATION = 14;
    public static final byte MATRIX = 15;
    public static final byte EXTERNAL = 16;

    /**
     * Convert a type that represent a byte to a readable String representation
     *
     * @param p_type byte encoding a particular type
     * @return readable string representation of the type
     */
    public static String typeName(byte p_type) {
        switch (p_type) {
            /** Primitives */
            case Type.BOOL:
                return "boolean";
            case Type.STRING:
                return "string";
            case Type.LONG:
                return "long";
            case Type.INT:
                return "int";
            case Type.DOUBLE:
                return "double";
            /** Arrays */
            case Type.DOUBLE_ARRAY:
                return "double[]";
            case Type.LONG_ARRAY:
                return "long[]";
            case Type.INT_ARRAY:
                return "int[]";
            /** Maps */
            case Type.LONG_TO_LONG_MAP:
                return "map(long->long)";
            case Type.LONG_TO_LONG_ARRAY_MAP:
                return "map(long->long[])";
            case Type.STRING_TO_LONG_MAP:
                return "map(string->long)";
            case Type.RELATION:
                return "relation";
            case Type.INDEXED_RELATION:
                return "indexed_relation";
            case Type.MULTI_INDEXED_RELATION:
                return "multi_indexed_relation";
            case Type.MATRIX:
                return "matrix";
            case Type.EXTERNAL:
                return "external";
            default:
                return "unknown";
        }
    }

}
