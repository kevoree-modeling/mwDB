package org.mwg.utility;

import org.mwg.Constants;
import org.mwg.struct.Buffer;

public class DefaultBufferIterator implements org.mwg.struct.BufferIterator {

    private final Buffer _origin;
    private final long _originSize;
    private long _cursor = -1;

    public DefaultBufferIterator(Buffer p_origin) {
        _origin = p_origin;
        _originSize = p_origin.length();
    }

    @Override
    public final boolean hasNext() {
        return _originSize > 0 && (_cursor + 1) < _originSize;
    }

    @Override
    public final synchronized Buffer next() {
        long previousCursor = _cursor;
        while ((_cursor + 1) < _originSize) {
            _cursor++;
            byte current = _origin.read(_cursor);
            if (current == Constants.BUFFER_SEP) {
                return new BufferView(_origin, previousCursor + 1, _cursor - 1);
            }
        }
        if (previousCursor < _originSize) {
            return new BufferView(_origin, previousCursor + 1, _cursor);
        }
        return null;
    }
}
