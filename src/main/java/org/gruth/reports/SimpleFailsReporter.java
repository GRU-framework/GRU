package org.gruth.reports;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author bamade
 */
// Date: 25/09/13

public class SimpleFailsReporter extends ResultReporter {
    static int fails = 0;
    static int success = 0 ;
    static int nbTests = 0 ;
    static boolean printGoodReports = Boolean.getBoolean("gruth.simplereporter.printall") ;

    static ArrayBlockingQueue<TztReport> failedList = new ArrayBlockingQueue<TztReport>(5);
    static EnumMap<RawDiagnostic,Integer> enumDiags = new EnumMap<RawDiagnostic, Integer>(RawDiagnostic.class);
    static {
        for(RawDiagnostic diag : RawDiagnostic.values()) {
            enumDiags.put(diag,0) ;
        }
    }


    public static int getFails() {
        return fails;
    }
    public static int getSuccess() {
        return success;
    }
    public static int getNbTests() {
        return nbTests ;
    }

    public static String failsString() {
        StringBuilder builder = new StringBuilder() ;
        if(fails > failedList.size()) {
            builder.append("... " + (fails-failedList.size()) + "reports skipped\n");
        }
        for(TztReport report : failedList) {
            builder.append("testName: ").append(report.getTestName()).append("\n")  ;
            builder.append("    result:").append(report.getRawDiagnostic()).append("\n");
            List<AssertionReport> listAssertions = report.getListAssertions();
            if(listAssertions.size() >0 ) {
                builder.append("    assertions").append(listAssertions).append("\n") ;
            }
            List<Throwable> thList = report.getCaught() ;
            if(thList.size() > 0 ){
                builder.append("    caught").append(thList).append("\n") ;
            }

        }
        return builder.toString() ;
    }

    public static String diagStats() {
        StringBuilder builder = new StringBuilder() ;
        for(Map.Entry<RawDiagnostic, Integer> mapEntry: enumDiags.entrySet()) {
            int number = mapEntry.getValue();
            if (number > 0) {
                builder.append(mapEntry.getKey()).append(':').append(number).append("; ") ;
            }
        }
        return builder.toString();
    }

    public static void clear() {
        fails= 0 ;
        failedList.clear();
    }

    String bundleName;

    @Override
    public Object report(AnnotatedReport report) {
        TztReport testReport = report.getReport();
        RawDiagnostic diagnostic = testReport.getRawDiagnostic();
        nbTests++ ;
        int numDiags = enumDiags.get(diagnostic);
        enumDiags.put(diagnostic, numDiags+1);
        if (diagnostic.compareTo(RawDiagnostic.OK_MARK) < 0) {
            fails++;
            if(failedList.remainingCapacity() == 0) {
                failedList.remove() ;
            }
            failedList.add(report.getReport()) ;
            System.err.println(String.format("failed: %s [%s] -> %s\n%s", testReport.getTestName(), bundleName, diagnostic, testReport.getListAssertions()));
        } else {
            success++ ;
            if(printGoodReports) {
                List listCaught = report.getReport().getCaught();
                String caughtString = "";
                if (listCaught != null && listCaught.size() > 0) {
                    caughtString = String.valueOf(listCaught);
                }
                System.out.println(String.format("ok: %s [%s] -> %s %s", testReport.getTestName(), bundleName, diagnostic, caughtString));
            }
        }
        List<String> messages = report.getReport().getMessages() ;
        if(printGoodReports) {
            if (messages != null) {
                for (String message : messages) {
                    System.out.println("# test message : " + message);
                }
            }
        }
        return "";
    }

    @Override
    public Object begin() {
        return "";
    }

    @Override
    public Object end() {
        return "";
    }

    @Override
    public Object groupReportsUnderName(String name) {
        bundleName = name;
        return "";
    }

    @Override
    public void close() throws IOException {
    }
}

