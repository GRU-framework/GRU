package org.gruth.reports


/**
 * A simple value that summarizes the result of an execution.
 * It is "raw" because  the analysis of test results may produce more sophisticated
 * reports (for instance a FAILED test may be annotated with BEYOND_LIMIT to indicate
 * that this is not an error : it is a feature!)
 *
 * @author bamade
 *
 */
public enum RawDiagnostic {
    // TODO: the exact order of NOT_EVALUATED should be re-evaluated
    //todo: see the findbugs  corresponding enum

    /**
     * dummy diagnostic for comparisons
     */
    GROUND_MARK,

    /**
     *  failure request: all scripts are stopped
     */
            FATAL,
    /**
     *  failure request: all tests in this script will be skipped
     */
            SCRIPT_LEVEL_FAIL,
    /**
     * the tests specification may be erroneous (or the testing tool itself failed)
     */
            TOOL_OR_CODE_ERROR,
    /**
     * the test failed
     */
            FAILED,
    /**
     * the test failed because some needed data could not be evaluated (usually because
     * a previous test failed and did not produced this data). Usually the data is null
     * without being tagged with a NULL* name.
     */
            MISSING_DATA,
    /**
     * not used: this is a programmatic mark, values superior to this one are considered
     * to mark a test that did not fail.
     */
            OK_MARK, // NOT USED! just to define order  from here tests may be ok! -- Below = ERROR

    /**
     * the resquested class or method is not yet implemented
     */
            NOT_YET_IMPLEMENTED,
    /**
     * the test was not evaluated . Example : the developer wrote a test but for the moment asked not to evaluate the result
     */
            NOT_EVALUATED,
    /**
     * the test was not run! because other tests failed
     */
            SKIPPED,
    /**
     *  not used this is a programmatic mark: values superior to this are
     *  considered executed and not failed
     */
            OK_DONE_MARK,  // below = WARNING

    /**
     * the test succeeded but with warnings
     */
            WARNINGS,

    /**
     *  no advice on fail or succeed, just a trace.
     */
            NEUTRAL,
    /**
     *  success: expectations met
     */
            SUCCESS;

}