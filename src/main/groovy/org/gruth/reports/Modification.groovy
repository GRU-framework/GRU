package org.gruth.reports

/**
 * a when an advice is issued what are the modification by user and automatic comparisons?
 * @author bamade
 */
// Date: 04/09/12

public enum Modification {
    //TODO: review the order

    /**
     * automatic comparison: regression spotted
     */
    COMPARISON_REGRESSION(Importance.ERROR, "!X", "regression spotted"),
    /**
     * automatic comparison: copied an USER_REGRESSION advice
     */
            //REGRESSION_COPY(Importance.ERROR,">x", "user opted for regression (copied by automatic comparison)"),
            /**
             * user noted a regression
             */
            //REGRESSION_BY_USER(Importance.ERROR," x", "user opted for a regression"),
            /**
             * user forced a negative result: copied by automatic comparison
             */
            //MODIFIED_NEGATIVELY_COPY(Importance.ERROR, ">-", "user forced a negative result"),
            /**
             * user forced a negative result
             */
            //MODIFIED_NEGATIVELY_BY_USER(Importance.ERROR, " -", "user forced a negative result"),
            MODIFIED_NEGATIVELY_BY_USER(Importance.ERROR, " -", "user forced a negative result"),
    ACKNOWLEDGED_NEGATIVE(Importance.ERROR, " x", "negative result acknowledged"),
    /**
     * automatic comparison : incoherent advices and result : does not know which advice to issue
     */
            CONFUSING_COMPARISON(Importance.WARNING, "!!", " (automatic) comparison cannot conclude"),
    /**
     * automatic comparison : copied an USER_ON_HOLD advice
     */
            //ON_HOLD_COPY(Importance.WARNING,">?", "user unsure of result (copied by automatic comparison)"),
            /**
             *  User still waiting for a final advice
             */
            //ON_HOLD_BY_USER(Importance.WARNING, " ?", "user unsure of result"),
            ON_HOLD(Importance.WARNING, " ?", "unsure of result"),
    /**
     * user forced a positive result: copied by automatic comparison
     */
            //MODIFIED_POSITIVELY_COPY(Importance.OK, ">+", "user forced a positive result (copied by automatic comparison)"),
            /**
             * user forced a positive result
             */
            MODIFIED_POSITIVELY_BY_USER(Importance.OK, " +", "user forced a positive result"),
    /**
     * automatic comparison: copied an USER_ACKNOWLEDGED from previous result
     */
            //ACKNOWLEDGED_COPY (Importance.OK, ">a", "result acknowledged by user and copied by automatic comparison"),
            /**
             * user acknowledged result
             */
            ACKNOWLEDGED_POSITIVE(Importance.OK, " a", "positive result acknowledged");

    public static enum Importance {
        ERROR, WARNING, OK;
    }

    String simplifiedNotation;
    String details;
    Importance importance;

    Modification(Importance importance, String simplifiedNotation, String details) {
        this.simplifiedNotation = simplifiedNotation
        this.details = details
        this.importance = importance
    }

    String getSimplifiedNotation() {
        return simplifiedNotation
    }

    String getDetails() {
        return details
    }

    Importance getImportance() {
        return importance
    }
}

