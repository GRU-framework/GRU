package org.gruth.reports

/**
 *
 * @author bamade
 *
 */
class SimpleResultReporter extends ResultReporter{
    SimpleResultReporter() {
        this.formatter = new SimpleResultFormatter()
    }

    @Override
    report(AnnotatedReport report) {
        println formatter.body(report)
    }

    @Override
    def begin() {
        return  ""
    }

    @Override
    def end() {
        return ""
    }

    @Override
    def groupReportsUnderName(String name) {
        println 'bundle = ' + name
        return ''
    }

    @Override
    void close() {
        //does nothing
    }
}


