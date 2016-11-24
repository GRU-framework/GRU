package org.gruth.reports


import javax.persistence.* ;

/**
 * used to store TztReport with notes about modification and handling of the corresponding report
 * @author bamade
 */
// Date: 30/08/12

@Entity
class AnnotatedReport implements Serializable, Cloneable, Comparable<AnnotatedReport> {
    public static long serialVersionUID = 1L ;
    public static enum Origin {
        /**
         * modification operated by automatic comparison tool
         */
        AUTO,
        /**
         * automatic comparison tool copied previous advice from use
         */
                COPY,
        /**
         * user modified the annotation
         */
                USER;
    }
    //TODO : modify the convention
    @Id
    @GeneratedValue
    private long id;
    long timeStamp = System.currentTimeMillis()

    /**
     * the original report as created by the test run.
     * Persistence: serialized (@Lob) or
     */
    @Embedded
    TztReport report

    /**
     * a duplicate of information in TztReport.
     * (but useful for queries)
     */
    //String testName ;

    /**
     * advice addded by user (or by automatic comprison tool).
     * Persistence: advice is an enum
     */
    @Enumerated(EnumType.STRING)
    Advice advice
    /**
     * nature of modification of the annotation.
     * Persistence: modification is an enum
     */
    @Enumerated(EnumType.STRING)
    Modification modification
    /**
     * who modified.
     * Persistence: Origin is an enum
     */
    @Enumerated(EnumType.STRING)
    Origin modificationOrigin;

    /**
     * user added a comment on this test
     */
    String userComment
    /**
     * this report has been through automatic comparison
     */
    boolean beenThroughAutomaticComparison

    /**
     * in case of regression last correct version
     * (-1 means no prvious correct version kept)
     */
    int neededPreviousVersion = -1
    /**
     * in case of regression last correct annotated report.
     * may be needed in other cases for just the latest previous version (confused state for example)
     */
    //@MapsId
    @ManyToOne (fetch=FetchType.EAGER, optional = true) //optional : use join table ?
    //@Transient
    @JoinColumn(name="neededPreviousReport_id")
    AnnotatedReport neededPreviousReport

    /**
     * "normal" constructor.
     * @param report
     */
    AnnotatedReport(TztReport report) {
        this.report = report
        //this.testName = report.testName
    }

    private AnnotatedReport() {
    }
/**
 * use this constructor for test purposes only
 * @param modification
 * @param advice
 * @param report
 */
    AnnotatedReport(Modification modification, Advice advice,
                    TztReport report) {
        this(modification, advice, "", -1, null, report)
    }

    /**
     * use this constructor for test purposes only
     * @param modification
     * @param advice
     * @param userComment
     * @param neededPreviousVersion
     * @param neededPreviousReport
     * @param report
     */
    AnnotatedReport(Modification modification, Advice advice, String userComment,
                    int neededPreviousVersion, AnnotatedReport neededPreviousReport,
                    TztReport report) {
        this.modification = modification
        this.report = report
        //this.testName = report.testName
        this.advice = advice
        this.userComment = userComment
        this.neededPreviousVersion = neededPreviousVersion
        this.neededPreviousReport = neededPreviousReport
    }

    Modification getModification() {
        return modification
    }

    void setModification(Modification modification) {
        this.modification = modification
    }

    Origin getModificationOrigin() {
        return modificationOrigin
    }

    void setModificationOrigin(Origin modificationOrigin) {
        this.modificationOrigin = modificationOrigin
    }

    TztReport getReport() {
        return report
    }


    Advice getAdvice() {
        return advice
    }

    void setAdvice(Advice advice) {
        this.advice = advice
    }

    String getUserComment() {
        return userComment
    }

    void setUserComment(String userComment) {
        this.userComment = userComment
    }

    @Override
    int compareTo(AnnotatedReport other) {
        return this.report(compareTo(other.report))
    }

    public AnnotatedReport clone() {
        AnnotatedReport res = super.clone();
        res.report = report.clone()
        if(this.neededPreviousReport != null) {
            res.neededPreviousReport = neededPreviousReport.clone()
        }
        return res
    }

    /**
     * a user may modify an uncommented Report or a previously commented report (modified automatically)
     * By adding/modifying and advice he/she changes the modification status that is considered
     * no longer automatic
     * @param advice
     */
    public void userModification(Advice advice) {
        /**/
        if (beenThroughAutomaticComparison) {
            beenThroughAutomaticComparison = false;
            //TODO: think!
            /*
            switch (advice) {

            } */

        }
        if (advice != null) {
            switch (advice) {
                case Advice.ACKNOWLEDGED_NEGATIVE:
                    modification = Modification.ACKNOWLEDGED_NEGATIVE;
                    break;
                case Advice.ACKNOWLEDGED_POSITIVE:
                    modification = Modification.ACKNOWLEDGED_POSITIVE;
                    break;
                case Advice.FORCE_SUCCESS: case Advice.RESULT_INDEPENDENT_FORCE_SUCCESS:
                    modification = Modification.MODIFIED_POSITIVELY_BY_USER;
                    break;
                case Advice.BEING_INVESTIGATED: case Advice.TO_BE_CORRECTED_LATER:
                case Advice.KNOWN_TO_BE_UNSUCCESSFUL:
                    modification = Modification.ON_HOLD;
                    break;
                case Advice.FORCE_ERROR: case Advice.RESULT_INDEPENDENT_FORCE_ERROR:
                    modification = Modification.MODIFIED_NEGATIVELY_BY_USER;
                    break;
                default:
                    System.err.println(advice + " incomplete code in method user modification: please warn maintenance")

            }
        }
        /*} */

        setAdvice(advice);
        modificationOrigin = Origin.USER
    }

    /**
     * this method is used by automatic comparison tool.
     * It is extremely complex and prone to errors
     * so behaviour is prone to changes in earlier version of the gru tool.
     * @param previous
     * @param currentVersion
     * @param previousVersion
     */
    public void automaticModification(AnnotatedReport previous, int currentVersion, int previousVersion) {
        if (previous == null) return;
        if (!this.report.getUniqueKey().equals(previous.report.getUniqueKey())) {
            //log?
            return;
        }
        //TODO modify origin
        boolean diagEquals = this.report.rawDiagnostic == previous.report.rawDiagnostic;
        def res1 = this.report.data
        def res2 = previous.report.data
        //todo : take care of noEqualsForResult?
        boolean resEquals = (res1 != null) ? res1.equals(res2) : (res2 == null);
        def caughts1 = this.report.caught;
        def caughts2 = previous.report.caught;
        boolean caughtEquals = (caughts1 != null) ? caughts1.equals(caughts2) : (caughts2 == null);
        boolean allResEquals = report.noEqualsForResult? caughtEquals: resEquals && caughtEquals;
        if (allResEquals) {
            if (diagEquals) {
                copy(previous, currentVersion, previousVersion);
            } else { // results are equals but not rawresult!!!!
                //user must correct this! (change in specification?)
                confused(previous, currentVersion, previousVersion);
            }

        } else { // now the hard part
            // there is a difference between results
            if (diagEquals) {
                diagEqualsResultsDiffer(previous, currentVersion, previousVersion);
            } else {
                //diagDiffersResultsDiffer(previous, currentVersion, previousVersion)
                //       if diag differ -> ???
                RawDiagnostic currentDiag = this.report.rawDiagnostic ;
                RawDiagnostic previousDiag = previous.report.rawDiagnostic ;

                if(currentDiag < RawDiagnostic.OK_MARK) { //ERRORS
                    if(currentDiag > previousDiag) {
                        progress(previous, currentVersion, previousVersion) ;
                        // positive evolution
                    } else {
                        // si OK_DONE -> regression
                        // si  OK  -> regression
                        regression(previous, currentVersion, previousVersion) ;
                    }
                } else if (currentDiag < RawDiagnostic.OK_DONE_MARK) { // OUT OF TESTING CONTEXT
                    // previous was NOT_EVALUATED OR NOT YET IMPELEMNTED no comparions possible
                    if(previousDiag == RawDiagnostic.NOT_YET_IMPLEMENTED || previousDiag ==
                            RawDiagnostic.NOT_EVALUATED) {
                        // do nothing?
                        modification= Modification.ON_HOLD ;
                    }
                    else if(currentDiag > previousDiag) {
                        // if previous incorrect ->  have a look new pertinent advice
                        lostResults(previous, currentVersion, previousVersion) ;
                    } else {
                        // if previous correct ->  have a look new pertinent advice
                        lostResults(previous, currentVersion, previousVersion) ;
                    }
                } else { //OK
                    if(currentDiag > previousDiag) {
                        //ok! positive evolution
                        progress(previous, currentVersion, previousVersion) ;
                    } else {
                        // small regression
                        smallRegression(previous, currentVersion, previousVersion) ;
                    }
                }

            }

        }
        //TODO verify: been through automatic, modification values and tests

    }

    /**
     * the comparison looks incoherent: user advice is requested
     * @param previous
     * @param currentVersion
     * @param previousVersion
     */
    private void confused(AnnotatedReport previous, int currentVersion, int previousVersion) {
        //user must correct this! (change in specification?)
        modification = Modification.CONFUSING_COMPARISON;
        if (previous.modification == Modification.CONFUSING_COMPARISON) {
            this.neededPreviousReport = previous.neededPreviousReport;
            this.neededPreviousVersion = previous.neededPreviousVersion;
        } else {
            this.neededPreviousReport = previous;
            this.neededPreviousVersion = previousVersion;
        }
    }

    /**
     * two versions of the test get same diagnostic with differing results
     * @param previous
     * @param currentVersion
     * @param previousVersion
     */
    private void diagEqualsResultsDiffer(AnnotatedReport previous, int currentVersion, int previousVersion) {

        // if marked as indifferent to result
        switch (previous.advice) {
            case Advice.RESULT_INDEPENDENT_FORCE_SUCCESS:
                modification = Modification.MODIFIED_POSITIVELY_BY_USER;
                this.advice = previous.advice;
                break;
            case Advice.RESULT_INDEPENDENT_FORCE_ERROR:
                modification = Modification.MODIFIED_NEGATIVELY_BY_USER;
                this.advice = previous.advice;
                break;
            case Advice.RESULT_INDEPENDENT_POSITIVE:
                //TODO: is this correct ?
                modification = Modification.ACKNOWLEDGED_POSITIVE;
                this.advice = previous.advice;
                break;
            case Advice.BEING_INVESTIGATED: //TODO : relevant ?
            case Advice.KNOWN_TO_BE_UNSUCCESSFUL:
                modification = Modification.ON_HOLD;
                this.advice = previous.advice;
                break;
        //FORCE ERROR -> confused
        //ACK_NEGATIVE -> confused
        //KNOWN_UNSUCCS -> confused
        //COORECT_LATER -> confused
        // FORCE_SUCCESS -> confused
        // ACK_POSITIVE -> confused
            default:
                /* TODO: think about this are we still confused?
                */
                confused(previous, currentVersion, previousVersion);
        }

    }

    /**
     * automatic comparison detected a Major regression
     * @param previous
     * @param currentVersion
     * @param previousVersion
     */
    private void regression(AnnotatedReport previous, int currentVersion, int previousVersion) {
        modification = Modification.COMPARISON_REGRESSION
        neededPreviousVersion = previousVersion
        neededPreviousReport = previous

        //look if copy or auto
    }
    /**
     * automatic comparison detected a minor regression
     * a "minor regression" happens when diagnostic skips from
     * SUCCESS to NEUTRAL or WARNINGS or from NEUTRAL to WARNINGS
     * @param previous
     * @param currentVersion
     * @param previousVersion
     */
    private void smallRegression(AnnotatedReport previous, int currentVersion, int previousVersion) {
        modification = Modification.COMPARISON_REGRESSION
        neededPreviousVersion = previousVersion
        neededPreviousReport = previous

        //look if copy or auto
    }

    /**
     * marks a progress ... in fact does nothing for the moment
     * @param previous
     * @param currentVersion
     * @param previousVersion
     */
    private void progress(AnnotatedReport previous, int currentVersion, int previousVersion) {
        // no modification it's up to the user to acknowldge again ...

        //look if copy or auto
    }

    /**
     *  happens when suddenly a test becomes NOT-YET-IMPLEMENTED or NOT-EVALUATED
     *  (but was not before!)
     * @param previous
     * @param currentVersion
     * @param previousVersion
     */
    private void lostResults(AnnotatedReport previous, int currentVersion, int previousVersion) {
        // no modification it's up to the user to acknowldge again ...
        neededPreviousVersion = previousVersion
        neededPreviousReport = previous

        //look if copy or auto
    }


    //TODO : addd version information
    /**
     *  automatic comparison copies a previous user's advice.
     * @param previous
     * @param currentVersion
     * @param previousVersion
     */
    private void copy(AnnotatedReport previous, int currentVersion, int previousVersion) {
        if (previous.beenThroughAutomaticComparison) {
            modification = previous.modification;

        } else {
            switch (previous.advice) {
                case Advice.ACKNOWLEDGED_POSITIVE:
                case Advice.RESULT_INDEPENDENT_POSITIVE:
                    modification = Modification.ACKNOWLEDGED_POSITIVE;
                    break;
                case Advice.FORCE_SUCCESS: case Advice.RESULT_INDEPENDENT_FORCE_SUCCESS:
                    modification = Modification.MODIFIED_POSITIVELY_BY_USER;
                    break;
                case Advice.BEING_INVESTIGATED: case Advice.TO_BE_CORRECTED_LATER:
                case Advice.KNOWN_TO_BE_UNSUCCESSFUL:
                    modification = Modification.ON_HOLD;
                    break;
                case Advice.FORCE_ERROR: case Advice.RESULT_INDEPENDENT_FORCE_ERROR:
                    modification = Modification.MODIFIED_NEGATIVELY_BY_USER;
                    break;
            }
        }
        this.advice = previous.advice;
        this.modificationOrigin = Origin.COPY;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AnnotatedReport)) return false;

        AnnotatedReport that = (AnnotatedReport) o;

        if (advice != that.advice) return false;
        if (modification != that.modification) return false;
        if (!report.getUniqueKey().equals(that.report.getUniqueKey())) return false;
        if (userComment != null ? !userComment.equals(that.userComment) : that.userComment != null) return false;

        return true;
    }
}

/*
TODO: verify what happens to modification to advice to other members of annotated result after auto+ user
*/
