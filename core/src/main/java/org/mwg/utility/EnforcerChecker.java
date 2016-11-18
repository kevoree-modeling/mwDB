package org.mwg.utility;

public interface EnforcerChecker {

    void check(byte inputType, Object input) throws RuntimeException;

}
