package org.gruth.tools;

/**
 * @author bamade
 */
// Date: 10/05/15


/**
 * This enum states diagnostics linked to an assertion.
 * <P/> note that this is different
 * from the more complex diagnostic that go
 * with the full fledged use of GRU (<TT>RawDiagnostic</TT> for instance).
 * Here the diagnostic is not guessed by an automatic tool
 * but should be explicitly stated in programmer code invocations.
 */
public  enum Diag {
    /**
     * means that the result of the test has failed
     * and that codes should stop (for instance
     * if a critical hardware failed).
     * Typical use is with an <TT>assert</TT> statement
     * that then should fire an <TT>AssertionError</TT>
     */
    FATAL,
    /**
     * the test failed (but may be other tests could
     * be run: this is a configuration decision)
     */
    FAILED,
    /**
     * something fishy is happening but won't crash
     * (for the moment)
     */
    WARNING,
    /**
     * for undecided programmers who do not know
     * if there is a problem or not!
     */
    NEUTRAL,
    /**
     * everything went as expected.
     */
    SUCCESS;
}

