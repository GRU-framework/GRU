package org.gruth.reports

import org.gruth.utils.TaggedObject

import java.util.logging.Level

/**
 *
 * @author bamade
 *
 */
class SimpleResultFormatter implements ResultFormatter {

    String pre(AnnotatedReport report) {
        return ""
    }

    static final boolean TRACE = false;

    String body(AnnotatedReport aReport) {
        TztReport report = aReport.getReport()
        if (TRACE) {
            report.caught.each {
                PackCst.CURLOG.log(Level.WARNING, "", it)
            }
        }
        Object supporting;
        if(report.supportingObject instanceof TaggedObject) {
            supporting = ((TaggedObject)report.supportingObject).key
        } else {
            supporting = report.supportingObject
        }
        // suppressed advice: ${report.advice ?: ''}
        String res = """\
${aReport.modification? "$aReport.modification\n": ''}\
${aReport.advice? "$aReport.advice\n": ''}\
\ttest: {
\t testName: $report.testName
\t rawDiagnostic: $report.rawDiagnostic
${report.methodName ? "\t method: $report.methodName $report.argsString" : ''}
\t className: $report.className
${supporting ? "\t supportingObject: $supporting \n" : ''}\
${report.data ? "\t data: $report.data \n" : ''} \
${report.additionalData ? "\t additionalData: $report.additionalData \n" : ''} \
${report.messages.empty?'' : "\t messages: {\n\t   $report.messages\n \t }\n"} \
${report.listAssertions.empty?'' : "\t assertions: {\n\t   $report.listAssertions\n \t }\n"} \
${report.caught.empty?'' : "\t caughts: {\n\t   $report.caught\n\t }\n"} \
\t}
    """
        if(aReport.neededPreviousReport != null) {
            res = res + '\n################## PREVIOUS REPORT ' +
                    body(aReport.neededPreviousReport) + '\n################# END PREVIOUS REPORT'
        }
        return res
    }

    String post(AnnotatedReport report) {
        return ""
    }
}


