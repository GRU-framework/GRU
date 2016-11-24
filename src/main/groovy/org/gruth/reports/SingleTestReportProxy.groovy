package org.gruth.reports

import org.gruth.tools.SimpleAssertion
import org.gruth.tools.Diag
import org.gruth.tools.SingleTestReport
import org.gruth.utils.TaggedObject

/**
 *
 * @author bamade
 */
// Date: 21/04/15

class SingleTestReportProxy implements SingleTestReport.Reporter{
    @Override
    void publish(SingleTestReport report) {
        TztReport tztReport = new TztReport( report.clazz.toString(),
                report.methodName
        )
        tztReport.testName = report.testName
        if(report.currentObject != null) {
            tztReport.supportingObject = TaggedObject.tag(report.currentObject)
        }
        tztReport.data = report.data
        String[] argNames = new String[report.invocationArguments.length]
        for ( int ix = 0 ; ix < report.invocationArguments.length; ix++){
            argNames[ix] = TaggedObject.tag(report.invocationArguments[ix]).key
        }
        tztReport.argsString = Arrays.toString(argNames)
        for(SimpleAssertion assertion : report.assertionList) {
            RawDiagnostic diagnostic = RawDiagnostic.NEUTRAL;
            switch(assertion.diag) {
                case Diag.SUCCESS:
                    diagnostic = RawDiagnostic.SUCCESS;
                    break ;
                case Diag.FATAL :
                    diagnostic = RawDiagnostic.FATAL;
                    break ;
                case Diag.FAILED :
                    diagnostic = RawDiagnostic.FAILED;
                    break ;
                case Diag.WARNING:
                    diagnostic = RawDiagnostic.WARNINGS;
                    break ;
                case Diag.NEUTRAL :
                    diagnostic = RawDiagnostic.NEUTRAL;
                    break ;

            }
            //println(" Diag" + assertion.diag + " diagnostic " + diagnostic )
            if(assertion.caught != null) {
                tztReport.caught << assertion.caught
                tztReport.listAssertions << new AssertionReport(diagnostic, "caught " + assertion.caught)
            }  else {
                tztReport.listAssertions << new AssertionReport(diagnostic, assertion.messageText, assertion.args)
            }
        }

        tztReport.gatherResult()
        ResultHandlers.handleReport("", tztReport)

    }
}
