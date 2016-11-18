package org.mwg.utility;

public class Tuple<A, B> {

    private A _left;
    private B _right;

    public Tuple(A p_left, B p_right) {
        this._left = p_left;
        this._right = p_right;
    }

    public A left() {
        return _left;
    }

    public B right() {
        return _right;
    }

}
