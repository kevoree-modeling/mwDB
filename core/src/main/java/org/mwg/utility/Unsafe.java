package org.mwg.utility;

import java.lang.reflect.Field;

/**
 * @ignore ts
 */
public class Unsafe {

    private static sun.misc.Unsafe unsafe_instance = null;

    @SuppressWarnings("restriction")
    public static sun.misc.Unsafe getUnsafe() {
        if (unsafe_instance == null) {
            try {
                Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                unsafe_instance = (sun.misc.Unsafe) theUnsafe.get(null);
            } catch (Exception e) {
                throw new RuntimeException("ERROR: unsafe operations are not available");
            }
        }
        return unsafe_instance;
    }

}
