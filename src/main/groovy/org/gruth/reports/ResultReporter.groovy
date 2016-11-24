package org.gruth.reports

/**
 *
 * @author bamade
 *
 */
public abstract class  ResultReporter implements Closeable{
    ResultFormatter formatter
    def abstract report(AnnotatedReport report)
    def abstract begin()
    def abstract end()
    /**
     * report objects in the same test batch can be grouped in different places
     * (files for instance).
     * It is up to the implementation to manage this switch: now the next reports
     * are going to be grouped under this name (for instance in a file with this name).
     * It will be guaranteed that an initial name will be furnished by calling code before any call
     * to report.
     * @param name
     * @return
     */
    def abstract groupReportsUnderName(String name)
}
