package org.gruth.utils

/**
 *
 * @author bamade
 */
// Date: 05/03/15

trait TestData {
    abstract String[] getArgNames() ;
    abstract Object[] getArgs()  ;
    Closure getDefaultXpectations() {
        return null ;
    }

}
