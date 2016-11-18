package org.mwg.task;

import org.mwg.utility.Tuple;

public interface TaskResultIterator<A> {

    A next();

    Tuple<Integer,A> nextWithIndex();

}
