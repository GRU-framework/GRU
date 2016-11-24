package org.gruth.reports


import java.util.logging.Level

/**
 *  The handling of reports.
 *  A list of reports will be handled by <TT>ResultReporter</TT> objects.
 *  This can be explicitly fired by the gru script code or implicitly
 *  at the end of execution of the script (through a <TT>shutdownhook</TT>)
 * @author bamade
 *
 */
public class ResultHandlers {
    /**
     * the list of code that are able to deliver results
     * (<TT>ResultReporter</TT>s).
     * Those codes can be specified:
     * <UL>
     *     <LI/> using the standard <TT>ServiceLoader</TT> services definition
     *   <LI/> by specifying a class name in system property "gruth.resultReporter"
     *   <LI/> by default an instance of <TT>ObjectFileResultReporter</TT>is used
     * </UL>
     */
    static List<ResultReporter> listReporters = []
    //@TODO: important change to treemap but check the test names!

    /**
     * List of <TT>TztReport</TT> instance that will be handled by the
     *  <TT>ResultReporter</TT>s
     */
    static ArrayList<TztReport> listReports = []

    static String reportBundleName = 'defaultReports'

    static {
        try {
            ServiceLoader<ResultReporter> loader = ServiceLoader.load(ResultReporter)
            for (ResultReporter reporter : loader) {
                listReporters << reporter
            }
        } catch (Throwable th) {
            PackCst.CURLOG.warning("Loading service Resultreporter :" + th)

        }
        if (listReporters.size() == 0) {
            //listReporters << new SimpleResultReporter()
            String reporterNames = System.getProperty('gruth.resultReporter')
            if (reporterNames != null ) {
                String[] resReporterClassNames = reporterNames.split(":")
                for (String resReporterClassName : resReporterClassNames) {
                    try {
                        ResultReporter reporter =
                                (ResultReporter) Class.forName(resReporterClassName).newInstance()
                        listReporters << reporter
                    } catch (Exception exc) {
                        PackCst.CURLOG.log(Level.SEVERE,
                                'wrong name for ResultReporter class', exc)
                    }
                }
            }

        }
        //again trying
        if (listReporters.size() == 0) {
            //listReporters << new ObjectFileResultReporter()
            listReporters << new SimpleResultReporter()
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            //@TODO: be sure of concurrency issues!
            public void run() {
                if (listReports.size() > 0) {
                    listReporters.each {
                        it.groupReportsUnderName(reportBundleName)
                        it.begin()
                        for (TztReport report : listReports) {
                            if (!report.dontReport) {
                                it.report(new AnnotatedReport(report))
                            }
                        }
                        it.end()
                    }
                    listReports.clear()
                }
                listReporters.each {
                    it.close()
                }
            }
        })
    }

    /**
     * Explicitly called to report a list of <TTT>TztReport</TT>
     * under a common bundelName
     * @param bundleName
     * @param list
     */
    static synchronized def handleReports(String bundleName, List<TztReport> list) {
        listReports.addAll(list)
        if (bundleName == null) bundleName = reportBundleName
        deliver(bundleName)
    }

    static synchronized def handleReport(String bundleName, TztReport report) {
        listReports << report
        if (bundleName == null) bundleName = reportBundleName
        deliver(bundleName)
    }

    /**
     * explicitly ask each <TT>ResultReporter</TT>
     * to get reports and bundle them under a common name.
     * In fact it is instances of <TT>AnnotatedReport</TT>
     * that are created and fed to the <TT>ResultReporter>/TT>
     * @param bundleName
     */
    static def deliver(String bundleName) {
        //int maximum = reportBufferSize;
        if (listReports.size() > 0) {
            listReporters.each {
                it.groupReportsUnderName(bundleName)
                it.begin()
                for (TztReport report : listReports) {
                    if (!report.dontReport) {
                        it.report(new AnnotatedReport(report))
                    }
                }
                it.end()
            }
            listReports.clear()
        }

    }
}
