package org.gruth.tools;

import java.util.EnumMap;

/**
 * A simple {@link org.gruth.tools.SingleTestReport.Reporter}
 * that publishes reports on the standard output (for
 * reports that are ok) or standard Error (for  reports that
 * are less or equals to WARNING).
 * <BR>
 *  The System property <TT>gruth.simpleReports.printAll</TT>
 *  should be set to "true" if reports on the standard output
 *  are needed (otherwise only reports on stderr will be printed).
 *  <P>
 *      This <TT>Reporter</TT> keeps track of the number
 *      of each overall diagnostic and so the
 *      <TT>getResults</TT> static method could be used to
 *      trace statistics.
 *  </P>
 *
 *  This reporter is the default Reporter unless
 *  other classes are specified as explained in
 *  the deployment documentation for
 *  {@link org.gruth.tools.SingleTestReport}
 *
 * @author bamade
 */
// Date: 26/04/15

public class SimplePublisher  implements SingleTestReport.Reporter{
    /**
     * map of results
     */
    private static EnumMap<Diag, Integer> results = new EnumMap<Diag, Integer>(Diag.class) ;

    /**
     * have results been read?
     */
    private static boolean resultsPublished ;

    static { // results initialisation to zero
        for(Diag diag : Diag.values()) {
            results.put(diag, 0) ;
        }

        // this hook is used to get some results if an assertion failed and so fired an Error
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (! resultsPublished) {
                            System.err.println(" publication results : " + getResults());
                        }
                    }
                }
        ));
    }

    /**
     *  get reports statistics.
     *  Invocation of this method flushes the publishing
     *  of <TT>SingleTestReport</TT>
     * @return a Map of the number of overall diagnostics ofpublished <TT>SingleTestReport</TT>
     */
    public static EnumMap<Diag, Integer> getResults() {
        SingleTestReport.flush();
        resultsPublished = true ;
        return results;
    }


    /**
     * Prints a report.
     * gets its overallDiagnostic; update report statistics
     * then:
     * <UL>
     *     <LI> if overall diagnostic is WARNING or less (FAILED, FATAL) prints it to standard error
     *     <LI>
     *         else if system boolean property <TT>gruth.simpleReports.printAll</TT> is set prints the report to
     *         standard output.
     *     </LI>
     * </UL>
     * @param report
     */
    @Override
    public synchronized void publish(SingleTestReport report) {
        Diag diagnostic = report.overallDiagnostic() ;
        int number = results.get(diagnostic) ;
        number++ ;
        results.put(diagnostic, number) ;
        if(Diag.WARNING.compareTo(diagnostic) >= 0) {
            System.err.println(report);
        } else {
            if(Boolean.getBoolean("gruth.simpleReports.printAll")) {
                System.out.println(report);
            }
        }
    }
}
