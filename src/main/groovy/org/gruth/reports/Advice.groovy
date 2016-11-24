package org.gruth.reports


/**
 * A simple value to store the result of a <em>human being</em>'s analysis
 * of the {@link RawDiagnostic} (result) of a {@link TztReport} (report).
 *
 *
 *
 * @author Alain BECKER <alain.becker@gmail.com>
 *     @author Bernard AMADE
 *
 */
public enum Advice {
    //TODO: review the order , harmonize with FindBugs
    /**
     * ERRORS
     */
    RESULT_INDEPENDENT_FORCE_ERROR("always an error (whatever the result)"),
    FORCE_ERROR("always an error when same result"),
    //RESULT_INDEPENDENT_NEGATIVE ???
    ACKNOWLEDGED_NEGATIVE("known to be wrong"),

    /**
     * WARNINGS
     */
     KNOWN_TO_BE_UNSUCCESSFUL("feature? bug unlikely to be corrected...."),
    TO_BE_CORRECTED_LATER("known bug but will be handled later") ,
    BEING_INVESTIGATED("unsure of conclusion"),
    /**
     * Force success
     */
    RESULT_INDEPENDENT_FORCE_SUCCESS("always a success (whatever the result)"),
    FORCE_SUCCESS("always a success with same result"),
    /**
     * OK
     */
    RESULT_INDEPENDENT_POSITIVE("acknowledged but results may differ freely"),
    ACKNOWLEDGED_POSITIVE("known to be right");


    private String details ;

    Advice(String details) {
        this.details = details
    }
}


