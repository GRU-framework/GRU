package org.gruth.reports

/**
 *
 * @author bamade
 *
 */
interface ResultFormatter {
    String pre(AnnotatedReport report);
    String body(AnnotatedReport report)
    String post(AnnotatedReport report);
}

