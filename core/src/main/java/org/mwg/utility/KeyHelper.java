package org.mwg.utility;

import org.mwg.Constants;

public class KeyHelper {

    public static void keyToBuffer(org.mwg.struct.Buffer buffer, byte chunkType, long world, long time, long id) {
        Base64.encodeIntToBuffer((int) chunkType, buffer);
        buffer.write(Constants.KEY_SEP);
        Base64.encodeLongToBuffer(world, buffer);
        buffer.write(Constants.KEY_SEP);
        Base64.encodeLongToBuffer(time, buffer);
        buffer.write(Constants.KEY_SEP);
        Base64.encodeLongToBuffer(id, buffer);
    }

}
