package org.gruth.reports

import java.text.MessageFormat

/**
 *
 * @author bamade
 */
// Date: 02/03/15

/**
 *  Generated by assertion methods. Gru assertions do not fail at once when evaluated:
 *  <TT>AssertionReport</TT> keeps the result of an assertion evaluation: the test
 *  overall result is the "worst" contained in all AssertionReports
 *  (see RawDiagnostic order to evaluate Result order).
 *  <P>
 *   There is an exception to this rule: if the <TT>force</TT> boolean is used
 *   the result of the test is forced to the first "forced" assertion
 *   (this happens for example when an exception is fired: the test result may be temporarily failed
 *   but if the exception is expected the assertion forces to SUCCESS).
 *
 * @author bamade
 *
 */
class AssertionReport implements Serializable{
    static ResourceBundle i18NBundle = null
    static {
        String bundleName = System.getProperty("gruth.i18n_bundleName")
        if(bundleName != null) {
            try {
                i18NBundle = ResourceBundle.getBundle(bundleName)
            } catch (Throwable exc) {
                PackCst.CURLOG.warning(bundleName + " not found " + exc)
            }
        }

    }
    String message ;
    RawDiagnostic result ;
    boolean force = false


    AssertionReport( RawDiagnostic result, String message , Object... messArgs) {
        this(false, result, message, messArgs)
    }
    AssertionReport(boolean force , RawDiagnostic result, String message, Object... messargs) {
        this.force = force
        this.message = translateMessage(message , messargs)
        this.result = result
    }

    String toString() {
        "assert: ${message} -> ${result}"
    }

    static String translateMessage(String message, Object... messargs) {
        String res = message
        if( i18NBundle != null) {
            res = i18NBundle.getString(res)
        }
        if(messargs.length != 0) {
            res = MessageFormat.format(res, messargs)
        }
        return res
    }
}
