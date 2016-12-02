package org.mwg;

import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;

class ChunkKey {

    byte type;

    long world;

    long time;

    long id;

    static ChunkKey build(Buffer buffer) {
        ChunkKey tuple = new ChunkKey();
        long cursor = 0;
        long length = buffer.length();
        long previous = 0;
        int index = 0;
        while (cursor < length) {
            byte current = buffer.read(cursor);
            if (current == Constants.KEY_SEP) {
                switch (index) {
                    case 0:
                        tuple.type = (byte) Base64.decodeToIntWithBounds(buffer, previous, cursor);
                        break;
                    case 1:
                        tuple.world = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                        break;
                    case 2:
                        tuple.time = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                        break;
                    case 3:
                        tuple.id = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                        break;
                }
                index++;
                previous = cursor + 1;
            }
            cursor++;
        }
        //collect last
        switch (index) {
            case 0:
                tuple.type = (byte) Base64.decodeToIntWithBounds(buffer, previous, cursor);
                break;
            case 1:
                tuple.world = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                break;
            case 2:
                tuple.time = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                break;
            case 3:
                tuple.id = Base64.decodeToLongWithBounds(buffer, previous, cursor);
                break;
        }
        return tuple;
    }

}
