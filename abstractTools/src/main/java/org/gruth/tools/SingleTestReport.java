package org.gruth.tools;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/** This class is part of an abstract layer
 * that enables any java code to publish GRU-like reports
 * (though these may not be exactly gru reports).
 * <P>
* A <TT>SingleTestReport</TT> object gather all data
 * pertaining to a given test.
 * So, for example, if a given test is about
 * invoking a constructor or method there may be
 * various <TT>SimpleAssertion</TT> objects that are
 * evaluated, collected and published as part of an
 * instance of <TT>SingleTestReport</TT> object (please
 * beware of the plural to distinguish thos two kinds of objects).
 * <BR>
 * Note that <TT>SingleTestReport</TT> instances should
 * be considered as objects that report test results:
 * these tests are not necessarily "terminal"
 * and may be about anything (for instance for reporting
 * scalability informations).
 * <P>
 * See the GRU tutorial for examples of uses
 * in Junit codes ("terminal" tests) or Java <TT>assert</TT> statements ("on the fly" tests).
 *
 * <P>
 * Programmers that use such objects should take care
 * because there should be a way to make them unique
 * (for instance in the context of a database).
 * It's up to the developers to decide which combination of fields
 * is unique; they can use:
 * <UL>
 *     <LI> testName (this field <B>should</B> be a minimum key: is is used in the management of a report cache)</LI>
 *     <LI> class Name</LI>
 *     <LI> method (or constructor) identification</LI>
 *     <LI> the current object</LI>
 *     <LI> the set of invocation arguments</LI>
 * </UL>
 * </P>
 *
 * @author bamade
 */
// Date: 21/04/15

public class SingleTestReport implements Serializable {
    /**
     * Implementations publish <TT>SingleTestReports</TT>.
     * There are various ways to register such handlers:
     * <UL>
     *     <LI> as a Service : the implementing classes should be registered
     *     in the <TT>org.gruth.tools.SingleTestReport$Reporter</TT> file
     *     in <TT>META-INF/services</TT> </LI>
     *     <LI>  if no service is registered the canonical name
     *     of the class should be cited by System property "gruth.singleTestReporter" </LI>
     *     <LI> otherwise the reporter by default will be
     *     <TT>org.gruth.tools.SimplePublisher</TT> </LI>
     * </UL>
     */
    public static interface Reporter {
        void publish(SingleTestReport report) ;
    }
    ////////////  REPORTING
    /**
     * the list of <TT>Reporters</TT> initialized by the deployment
     * static code.
     */
    static ArrayList<Reporter> reporters = new ArrayList<>() ;

    static {
        ServiceLoader<Reporter> serviceLoader = ServiceLoader.load(Reporter.class) ;
        for(Reporter reporter : serviceLoader) {
            reporters.add(reporter) ;
        }
        if(reporters.size() == 0) {
            String reporterName = System.getProperty("gruth.singleTestReporter") ;
            if(reporterName != null) {
                try {
                    Reporter reporter = (Reporter) Class.forName(reporterName).newInstance();
                    reporters.add(reporter) ;
                } catch(Exception exc) {
                    PackCommons.CURLOG.warning(" can't set up reporter " + exc);
                }
            }
        }
        // if no reporter use simpleReporter
        if(reporters.size() ==0 ) {
            reporters.add(new SimplePublisher()) ;
        }
    }

    /**
     * a cache of unpublished reports. will be emptied when the <TT>flush()</TT> method is invoked.
     * This flush is guaranteed to happen when the JVM stops.
     * The size of the cache is 20 (but could be changed by System property "gruth.lastReportsSize")
     * when the number of reports in cache overflows the eldest entry is published and removed.
     */
    protected static final Map<String,  SingleTestReport> lastReports ;

    /**
     *  local implementation of <TT>LinkeHashMap</TT>
     */
    private static class ReportMap extends LinkedHashMap<String, SingleTestReport> {

        final int sizeMax ;
        public ReportMap(int sizeMax) {
            this.sizeMax = sizeMax ;

        }
        @Override
        public boolean  removeEldestEntry(Map.Entry<String, SingleTestReport> entry) {
            SingleTestReport report = entry.getValue() ;
            if(report.published == false) {
                report.internalPublish(false);
            }
            return size() > sizeMax ;
        }

        @Override
        public SingleTestReport put(String key, SingleTestReport value) {
            SingleTestReport res = (SingleTestReport) super.put(key, value) ;
            if(res != null) {
                PackCommons.CURLOG.warning(" key duplication in reports " + key);
                res.internalPublish(false);
            }
            return res ;
        }
    }

    static {
        int sizeMap = 20 ;
        String lastReportsSizeString = System.getProperty("gruth.lastReportsSize") ;
        if(lastReportsSizeString != null) {
            try {
                sizeMap = Integer.parseInt(lastReportsSizeString) ;
            } catch (Exception exc) {
                PackCommons.CURLOG.warning(" gruth.lastReportsSize " + exc);
            }
        }
        ReportMap reportMap = new ReportMap(sizeMap) ;
        lastReports = reportMap ;
    }
    /**
     * monitor for handling  <TT>LastReports</TT> (reports cache)
     */
    static Object reportMonitor = new Object() ;

    /**
     * adds a report to the current unpublished reports cache.
     * @param report
     */
    public static void addToReports(SingleTestReport report) {
        synchronized (reportMonitor) {
            lastReports.put(report.getTestName(), report) ;
        }
    }

    /**
     * makes sure that all reports have been published (and empties the reports cache)
     */
    public static void flush() {
        synchronized (reportMonitor) {
            for(SingleTestReport report : lastReports.values()){
                report.internalPublish(false);
            }
            lastReports.clear();
        }
    }


    static {
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        flush() ;
                    }
                }
        ));
    }


    /**
     * the name of the test: mandatory
     */
    private String testName ;
    /**
     * name of invocation code : "&lt;init&gt;"  is for the name
     * of a constructor. the name may be null (the instance
     * is used for other purposes than test a method or constructor invocation
     */
    private String methodName;
    /**
     * current class (may be null)
     */
    private Class clazz ;
    /**
     * optional current object. if this instance is Serialized
     * will be replaced by a String if the object is not
     * serializable.
     */
    private transient Object currentObject ;
    /**
     * optional result of an invocation. If not serializable
     * will be replace by a String if the current instance
     * is Serialized
     */
    private transient Object result ;
    /**
     * optional additional data.
     */
    private  transient Object data ;
    /**
     * can be empty but not null.
     * any object in this array which is not serializable
     * will be replaced by a String upon serialization.
     */
    private transient Object[] invocationArguments =
            new Object[0];
    /**
     * can be empty but not null.
     *
     */
    private ArrayList<SimpleAssertion> assertionList = new ArrayList<>();
    /**
     * really transient:
     * not part of the instance state.
     */
     transient boolean published = false ;

    /**
     * a complete constructor (prefer the factories)
     * @param currentObject
     * @param testName
     * @param methodName
     * @param clazz
     * @param invocationArguments
     */
    public SingleTestReport(Object currentObject, String testName, String methodName, Class clazz, Object... invocationArguments) {
        setCurrentObject(currentObject);
        setTestName(testName);
        setMethodName(methodName);
        setClazz(clazz);
        setInvocationArguments(invocationArguments);
        addToReports(this);
    }

    /**
     * factory method for method invocation test report
     * @param testName
     * @param methodName
     * @param clazz
     * @param invocationArguments
     * @return
     */
    public static SingleTestReport _methReport(Object currentObject,  String testName,
                                               String methodName, Class clazz, Object... invocationArguments) {
        return new SingleTestReport(currentObject, testName,methodName,clazz,invocationArguments) ;
    }

    /**
     * factory method for test report for constructor invocation
     * @param testName
     * @param clazz
     * @param invocationArguments
     * @return
     */
    public static SingleTestReport _ctorReport(String testName,
                                                Class clazz, Object... invocationArguments) {
        return new SingleTestReport(null, testName,"<init>", clazz,invocationArguments) ;
    }

    /**
     * factory method for just any kind of reporting
     * @param testName mandatory value
     * @param clazz optional
     * @return
     */
    public static SingleTestReport _reportFor(String testName,Class clazz) {
        return new SingleTestReport(null, testName,"",clazz,new Object[0]) ;
    }

    /**
     * Report request.
     * can be a
     * factory method for just any kind of reporting (class information is null).
     * This method has a different behaviour from other factories: if
     * a report has been created with the same key (<TT>testName</TT>
     * and if this report is still in the reports cache then this report is returned.
     * This is useful when using <TT>SingleTestReports</TT> in Java <TT>assert</TT>
     * statements (you do not have to keep a reference on reports outside the assert
     * statements).
     * @param testName mandatory
     * @return
     */
    public static SingleTestReport _reportFor(String testName) {
        SingleTestReport res = lastReports.get(testName) ;
        if(res == null) {
            return _reportFor(testName, null);
        }
        return res ;
    }

    ////////// SET/GET


    /**
     * get id of test
     * @return name of test (guaranteed to be not null)
     */
    public String getTestName() {
        return testName;
    }

    /**
     * sets the id of the test
     * @param testName
     * @throws NullPointerException if testName is null
     */
    public void setTestName(String testName) {
        this.testName = Objects.requireNonNull(testName, "name of Test unit should be specified") ;
    }

    /**
     * gets a method name ("&lt;init&gt;" if constructor)
     * @return the optional name
     */
    public Optional<String> getMethodName() {
        return Optional.ofNullable(methodName);
    }

    /**
     * sets the name of invoked code (method, constructor).
     * @param methodName (&lt;init&gt; if constructor). can be null
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * the current class for which the report is issued
     * @return an optional value of the class
     */
    public Optional<Class> getClazz() {
        return Optional.ofNullable(clazz) ;
    }

    /**
     * sets the class
     * @param clazz (can be null)
     */
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * the current object on which the tests operates
     * @return an optional object
     */
    public Optional<Object>  getCurrentObject() {
        return Optional.ofNullable(currentObject) ;
    }

    /**
     * sets the current object
     * @param currentObject (can be null)
     */
    public void setCurrentObject(Object currentObject) {
        this.currentObject = currentObject;
    }

    /**
     * the result of the code invocation
     * @return an optional value
     */
    public Optional<Object> getResult() {
        return Optional.ofNullable(result);
    }

    /**
     * sets the result of the code invocation
     * @param result (can be null)
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * addtional data can be added to the report (examples: time, duration,...)
     * @param obj
     * @return
     */
    public SingleTestReport setData(Object obj) {
        this.data = obj ;
        return this;
    }

    /**
     * gets additional data
     * @return optional data
     */
    public Optional<Object> getData() {
        return Optional.ofNullable(data) ;
    }

    /**
     * the arguments of the code invocation
     * @return can be empty but not null
     */
    public Object[] getInvocationArguments() {
        return invocationArguments;
    }

    /**
     * sets the arguments of the invocation
     * @param invocationArguments can be empty but not null
     * @throws NullPointerException if null argument
     */
    public void setInvocationArguments(Object[] invocationArguments) {
        this.invocationArguments = Objects.requireNonNull( invocationArguments,
        "invocation arguments cannot be null");
    }

    ///////////// UTILITIES
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SingleTestReport{");
        sb.append("testName='").append(testName).append('\'');
        if(methodName != null) {
            sb.append(", methodName='").append(methodName).append('\'');
        }
        if(clazz != null) {
            sb.append(", clazz=").append(clazz);
        }
        if(currentObject != null) {
            sb.append(", currentObject=").append(currentObject);
        }
        if(result != null) {
            sb.append(", result=").append(result);
        }
        if(data != null) {
            sb.append(", data=").append(data);
        }

        sb.append(", invocationArguments=").append(invocationArguments == null ? "[]" : Arrays.asList(invocationArguments).toString());
        sb.append(", assertionList=").append(assertionList);
        sb.append('}');
        return sb.toString();
    }

/// builder methods

    /**
     * factory method to add a {@link org.gruth.tools.SimpleAssertion} to the current report.
     * @param test if true will generate a SUCCESS diagnostic otherwise a FAILED diagnostic
     * @param message core MessageFormat to be handled by formatting process
     * @param args aguments of the formatting process
     * @return the current instance
     */
    public SingleTestReport okIf(boolean test, String message, Object... args) {
        if(test) {
           assertionList.add(new SimpleAssertion(Diag.SUCCESS, message, args))  ;
        } else {
            assertionList.add(new SimpleAssertion(Diag.FAILED, message, args))  ;
        }
        return this ;
    }

    //warnIf

    /**
     * factory method to add a {@link org.gruth.tools.SimpleAssertion} to the current report.
     * @param test if true will generate a WARNING diagnostic
     * @param message core MessageFormat to be handled by formatting process
     * @param args arguments of the formatting process
     * @return the current instance
     */
    public SingleTestReport warnIf(boolean test, String message, Object... args) {
        if(test) {
            assertionList.add(new SimpleAssertion(Diag.WARNING, message, args));
        }
        return this ;
    }

    /**
     * factory method to add a {@link org.gruth.tools.SimpleAssertion} to the current report.
     * @param test if true will generate a FATAL diagnostic
     * @param message core MessageFormat to be handled by formatting process
     * @param args aguments of the formatting process
     * @return the current instance
     */
    public SingleTestReport fatalIf(boolean test, String message, Object... args) {
        if(test) {
            assertionList.add(new SimpleAssertion(Diag.FATAL, message, args));
        }
        return this ;
    }
    //caught (ok, pas ok)

    /**
     * factory method to add a {@link org.gruth.tools.SimpleAssertion} to the current report.
     * @param exception the caught Throwable
     * @param relatedDiagnostic diagnostic (to be chosen by developer since
     *                          could be SUCCESS, WARNING, FAILED, FATAL,...)
     * @return the current instance
     */
    public SingleTestReport caught(Throwable exception, Diag relatedDiagnostic) {
        assertionList.add(new SimpleAssertion(relatedDiagnostic, exception,""));
        return this ;
    }
    // report

    /**
     * factory method to add any {@link org.gruth.tools.SimpleAssertion} to the current report.
     *
     * @param simpleAssertion any simple assertion
     * @return the current instance
     */
    public SingleTestReport report(SimpleAssertion simpleAssertion) {
        assertionList.add(simpleAssertion) ;
        return this;
    }
    // data
    /**
     * the current report is submitted to the reporters.
     */
    public SingleTestReport publish() {
        internalPublish(true);
        return this;
    }

    /**
     * private method that publishes and optionally removes a report from the reports cache.
     * @param remove
     */
    private void internalPublish(boolean remove) {
        synchronized (reportMonitor) {
            for (Reporter reporter: reporters) {
                reporter.publish(this);
            }
            if(remove) lastReports.remove(this.testName) ;
            published = true ;
        }

    }


    /**
     * to be used to force the current instance to return a boolean
     * to an invoking java <TT>assert</TT> statement.
     * It operates this way:
     * <UL>
     *     <LI> first it publishes the current report </LI>
     *     <LI> gets the name of the <TT>Diag</TT> enum
     *     constant in System property "gruth.failLevel"
     *     (if not set then the constant is FAILED). </LI>
     *     <LI> invokes the <TT>overallDiagnostic</TT> method
     *     of the current instance </LI>
     *     <LI> compares the result with the failLevel: if
     *     equals or less then return false. </LI>
     * </UL>
     * @return
     */
    public boolean ack() {
        if(!published) {
            publish() ;
        }
        boolean res = hasFailed() ;
        return res ;
    }

    /**
     * much like <TT>ack</TT> except that publication is only performed
     * if the result is false.
     * This method is to be used with <TT>assert</TT> statements: if the
     * result is false then the assert will fail and all pending reports will be published.
     */
    public boolean yield() {
        boolean res = hasFailed() ;
        if(! res) {
            flush() ;
        }
        return res ;
    }

    /**
     * implementation of fail evaluation as described in <TT>ack()</TT> documentation.
     * @return
     */
    private boolean hasFailed() {
        Diag limitDiag = Diag.FAILED ;
        String limitName = System.getProperty("gruth.failLevel") ;
        if(limitName != null) {
            try {
                limitDiag = Diag.valueOf(limitName) ;
            } catch (Exception exc){
                PackCommons.CURLOG.warning("can't set up failLevel with constant " + limitName);
            }
        }
        boolean res = true ;
        for(SimpleAssertion assertion: assertionList) {
            if(limitDiag.compareTo(assertion.getDiag()) >= 0){
                return false ;
            }
        }
        return res ;
    }

    /**
     * scans all the assertions in this report and returns
     * the "worst" one (since <TT>Diag</TT> constants are ordered)
     * @return worst diagnostics
     */
    public Diag overallDiagnostic() {
        Diag result = Diag.SUCCESS ;
        for(SimpleAssertion assertion: assertionList) {
           if(result.compareTo(assertion.getDiag())> 0)  {
               result = assertion.getDiag() ;
           }
        }
        return result ;
    }

    /**
     * this method is useful to use with java <TT>assert</TT>
     * statements: it forces a boolean return value for the current
     * instance and this value will be used by the assert.
     * side effect: it forces publishing of the current instance.
     * @param bool
     * @return
     */
    public boolean ack(boolean bool) {
        if(!published) {
            publish() ;
        }
        return bool ;
    }

    /**
     * much like <TT>ack(boolean)</TT> except that publication
     * of pending reports will be performed only if the result is false.
     * @param bool
     * @return
     */
    public boolean yield(boolean bool) {
        if(! bool) {
            flush();
        }
        return bool ;
    }

    /**
     * Serialization utility
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        /*
        public transient Object currentObject ;
        public transient Object result ;
        public  transient Object data ;
        public transient Object[] invocationArguments;
        */
        if(this.currentObject != null) {
            if(this.currentObject instanceof Serializable) {
                out.writeObject(this.currentObject);
            } else {
                out.writeObject(String.valueOf(currentObject));
            }
        } else {
            out.writeObject(null);
        }

        if(this.result != null) {
            if(this.result instanceof Serializable) {
                out.writeObject(this.result);
            } else {
                out.writeObject(String.valueOf(result));
            }
        } else {
            out.writeObject(null);
        }

        if(this.data != null) {
            if(this.data instanceof Serializable) {
                out.writeObject(this.data);
            } else {
                out.writeObject(String.valueOf(data));
            }
        } else {
            out.writeObject(null);
        }

        if(this.invocationArguments != null) {
            Object[] serArgs = new Object[this.invocationArguments.length] ;
            for(int ix=0; ix < this.invocationArguments.length; ix++) {
                Object curObj = this.invocationArguments[ix] ;
                if(curObj instanceof  Serializable) {
                    serArgs[ix] = curObj ;
                } else {
                    serArgs[ix] = String.valueOf(curObj) ;
                }
            }
            out.writeObject(serArgs);
        }  else {
            out.writeObject(null);
        }

    }

    /**
     * Serialization utility
     * @param input
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
          /*
        public transient Object currentObject ;
        public transient Object result ;
        public  transient Object data ;
        public transient Object[] invocationArguments;
        */
        this.currentObject = input.readObject() ;
        this.result = input.readObject() ;
        this.data = input.readObject() ;
        this.invocationArguments = (Object[]) input.readObject() ;
    }

}
