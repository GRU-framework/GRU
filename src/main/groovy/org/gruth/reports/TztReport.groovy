package org.gruth.reports
import org.gruth.utils.TaggedObject
import org.gruth.utils.WithStringKey
import org.hibernate.annotations.Immutable

import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import java.util.logging.Level
//import org.hibernate.annotations.Immutable

//import javax.persistence.Embeddable
//import javax.persistence.EnumType
//import javax.persistence.Enumerated
/**
 *  Reports about the execution of a test.
 *  this structure is to be kept , stored and may be modified by other software
 *  (modification: should not be modified, see AnnotatedReport for modifications)
 * @author bamade
 *
 */
@Embeddable
@Immutable
class TztReport implements Cloneable, WithStringKey, Serializable, Comparable<TztReport> {
    public static long serialVersionUID = 1L;
    public static int scriptErrors = 0
    /**
     * name of test : see _rootName in TztRun
     */
    String testName;
    // persistence: this is an enum:
    @Enumerated(EnumType.STRING)
    //todo: was NEUTRAL check if correct in other codes
    RawDiagnostic rawDiagnostic = RawDiagnostic.SUCCESS;
    String methodName;
    String argsString ;
    String className;

    // persistence: should be keep as a String
    // TODO: stringify
    ArrayList<String> messages = [];
    //List<String> messages = [];
    /**
     * this Object may not be serialised: TaggedObject takes care of that
     */
    TaggedObject supportingObject;


    /**
     * Object produced by test ... if not serializable
     * will be dumped at Serialization. (or replaced by a String)
     */
    // TODO: URGENT write serialization methods
    transient Object data;
    /**
     * the result object does not have an "equals" method that can be used after serialization/deserialisation
     */
    boolean noEqualsForResult = false;

    /**
     * used for reporting anything
     */
    Serializable additionalData;

    /*
    List<Throwable> caught = [];
    List<AssertionReport> listAssertions = []
    */
    ArrayList<Throwable> caught = [];
    ArrayList<AssertionReport> listAssertions = []

    boolean exceptionFired = false
    // Advice advice = Advice.NOT_EVALUATED_YET; // TODO: decide whether o not to initialize the value here

    /**
     * used to declare this run not to be included in final report
     */
    transient boolean dontReport = false

    //TODO: deam with that
    //transient Binding _this = GruScript.running?._this

    TztReport(String className, String methodName) {
        this.className = className
        this.methodName = methodName
    }

    TztReport(String className) {
        this.className = className
    }

    TztReport(String testName,String className, TaggedObject supportingObject, String methodName) {
        this.testName = testName
        this.supportingObject = supportingObject
        this.methodName = methodName
        this.className = className
    }

    TztReport(String testName, TaggedObject supportingObject) {
        this.testName = testName
        this.className = supportingObject.object.getClass().getCanonicalName()
        this.supportingObject = supportingObject
        this.methodName = '_state_'
    }

    TztReport() {

    }

    // for test purposes only
    TztReport(String testName, RawDiagnostic rawDiagnostic, Object data, List<Throwable> caught) {
        this.testName = testName
        this.rawDiagnostic = rawDiagnostic
        if (caught != null) this.caught = caught
        this.data = data
        this.className = ""
        this.methodName = ""
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject()
        if (!(this.data instanceof Serializable)) {
            out.writeObject('-not serializable-:' + this.data.toString())
        } else {
            out.writeObject(this.data)
        }
    }

    private void readObject(java.io.ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject()
        this.data = input.readObject()
    }


    TztReport clone() {
        TztReport res = (TztReport) super.clone()
        if (caught != null) res.caught = caught.clone()
        if (messages != null) res.messages = messages.clone()
        if (listAssertions != null) res.listAssertions = listAssertions.clone()
        return res;
    }

    public String toString() {
        return """class: ${this.className} method: ${this.methodName} message [${this.messages}]
        rawDiagnostic: ${this.rawDiagnostic} caughts: ${this.caught}""".toString()
    }

    public String getTestName() {
        return testName ;
    }

    public String getSupportingObjectString() {
        Object supporting;
        if (this.supportingObject instanceof TaggedObject) {
            supporting = ((TaggedObject) this.supportingObject).key
        } else {
            supporting = this.supportingObject
        }
        if (supporting == null) {
            supporting = className;
        }
        return String.valueOf(supporting)
    }

    String getKey() {
        return this.testName
    }

    public String getUniqueKey() {
        return getKey() + "\u21E8" + getSupportingObjectString()
    }
    ////// ASSERTIONS HANDLING

    boolean registerAssertWithDiagnostic( boolean booleanExpr, RawDiagnostic diag, String message, Object... messargs) {
        return registerAssertion(booleanExpr, true, diag, message, messargs)
    }
    /**
     * Adds an <TT>AssertionReport</TT> to the current  object:
     * the result is <TT>FAILED</TT> if the evaluation of the string is true
     * (the report also contains the evaluated string).
     * [NOT WORKING PROPERLY -YET-]
     * @param stringBooleanExpr a Gstring representing a valid Groovy expression
     * @return
     */
    boolean failIf(String stringBooleanExpr) {
        return registerAssertion(stringBooleanExpr, true, RawDiagnostic.FAILED)
    }
    /**
     * Adds an <TT>AssertionReport</TT> to the current  object:
     * the result is <TT>FAILED</TT> if the evaluation of the expression is true
     * (the report also contains the message)
     * @param message to be displayed in the report
     * @param booleanExpr before evaluating exceptionfired should be tested
     * @return
     */
    boolean failIf( boolean booleanExpr, String message, Object... messargs) {
        return registerAssertion( booleanExpr, true, RawDiagnostic.FAILED, message, messargs)
    }

    /**
     * same specs as failIf but faisl if false
     * @param stringBooleanExpr
     * @return
     */
    boolean failIfNot(String stringBooleanExpr) {
        return registerAssertion(stringBooleanExpr, false, RawDiagnostic.FAILED)
    }

    /**
     * same specs as failIf but fails if false
     */
    boolean failIfNot( boolean booleanExpr, String message, Object... messargs) {
        return registerAssertion( booleanExpr, false, RawDiagnostic.FAILED, message, messargs)
    }

    /**
     * Adds an <TT>AssertionReport</TT> to the current object:
     * the result is <TT>WARNINGS</TT> if the evaluation of the string is true
     * (the report also contains the evaluated string).
     * [NOT WORKING PROPERLY -YET-]
     * @param stringBooleanExpr a Gstring representing a valid Groovy expression
     * @return
     */
    boolean warnIf(String stringBooleanExpr) {
        return registerAssertion(stringBooleanExpr, true, RawDiagnostic.WARNINGS)
    }

    /**
     * Adds an <TT>AssertionReport</TT> to the current  object:
     * the result is <TT>WARNINGS</TT> if the evaluation of the expression is true
     * (the report also contains the message)
     * @param message to be displayed in the report
     * @param booleanExpr
     * @return
     */
    boolean  warnIf( boolean booleanExpr, String message, Object... messargs) {
        return registerAssertion( booleanExpr, true, RawDiagnostic.WARNINGS, message, messargs)
    }

    /**
     * same specs as warnIf but warns if false
     */
    boolean warnIfNot(String stringBooleanExpr) {
        return registerAssertion(stringBooleanExpr, false, RawDiagnostic.WARNINGS)
    }

    /**
     * same specs as warnIf but warns if false
     */
    boolean warnIfNot( boolean booleanExpr, String message, Object... messargs) {
        return registerAssertion( booleanExpr, false, RawDiagnostic.WARNINGS, message, messargs)
    }

    public boolean message(String message, Object... args) {
        this.messages << AssertionReport.translateMessage(message,args)
        return  true
    }

    /**
     *  used to add some data to the report (does not change the result of the test)
     *
     * @param data any serializable data
     * @return the argument
     */
    Serializable reportData(Serializable data) {
        this.additionalData = data
        return data
    }

    def neutral(String stringExpression) {
        try {
            //TODO; does not work!
            def res = groovy.util.Eval.me(stringExpression)
            registerAssertion( true, true, RawDiagnostic.NEUTRAL, "message :{0} -> {1}", stringExpression, res)
            return res
        } catch (Exception exc) {
            reportException(exc, RawDiagnostic.TOOL_OR_CODE_ERROR)
            return exc
        }
    }

    def neutral(String message, Closure closure) {
        try {
            def res = closure.call()
            registerAssertion( true, true, RawDiagnostic.NEUTRAL, "message :{0} -> {1}", message, res)
            return res
        } catch (Exception exc) {
            reportException(exc, RawDiagnostic.TOOL_OR_CODE_ERROR)
            return exc
        }
    }

    def doNotReport() {
        this.dontReport = true
    }

    def resultHasNoEquals() {
        this.noEqualsForResult = true
    }

    boolean okIfCaught(Class<? extends Throwable> throwClass) {
        // use the in operator to test if one exception is in Class
        boolean found = false
        this.caught.each {
            if (it in throwClass) {
                listAssertions << new AssertionReport(true,  RawDiagnostic.SUCCESS, "thrown {0}", it.toString())
                found = true
            }
        }
        if (!found) {
            listAssertions << new AssertionReport(true,RawDiagnostic.FAILED, "not thrown {0}", throwClass)
        }
        return found
    }

    boolean isCaught(Class<? extends  Throwable> throwClass) {
        boolean res = false ;
        this.caught.each {
            if (it in throwClass) {
                res = true
            }
        }
        return res
    }

    protected boolean registerAssertion(String stringBooleanExpression, boolean expected, RawDiagnostic result) {
        try {
            throw new UnsupportedOperationException("Groovy bug on dynamic operations?")
            //TODO; does not work! when invoking -> missing property
            // create a Stream and have it evaluated by the current script
            boolean res = groovy.util.Eval.me(stringBooleanExpression)
            //boolean res = _this.currentScript.evaluate(stringBooleanExpression)
            return registerAssertion(res, expected, result, stringBooleanExpression)
        } catch (Exception exc) {
            reportException(exc, RawDiagnostic.TOOL_OR_CODE_ERROR)
            return false
        }

    }

    protected boolean registerAssertion( boolean actual, boolean expected, RawDiagnostic result, String message, Object... messargs) {
        boolean res
        AssertionReport assertion
        if (actual == expected) {
            assertion = new AssertionReport( result, message, messargs)
            res = false
        } else {
            //TODO
            //assertion = new AssertionReport(message, RawDiagnostic.NEUTRAL)
            assertion = new AssertionReport( RawDiagnostic.SUCCESS, message, messargs)
            res = true
        }
        listAssertions << assertion
        return res
    }

    void reportException(Throwable th, RawDiagnostic result) {
        this.caught << th
        this.rawDiagnostic = result
        this.exceptionFired = true
        // TODO: problem with init of Cst!!!!
        PackCst.CURLOG.log(Level.FINE, "", th)
    }

    void gatherResult() {
        // now goes through the exceptions
        // if < Ok_MARK takes the worst
        // if > OK_MARK takes the worst ?
        // if forced takes the value
        RawDiagnostic res = this.rawDiagnostic
        boolean forced = false
        this.listAssertions.each {
            RawDiagnostic nextResult = it.result
            if(res == RawDiagnostic.NOT_EVALUATED) {
                res = nextResult
            }
            if (it.force) {
                res = nextResult
                forced = true;
            }
            if ((!forced) && (nextResult < res)) {
                if (nextResult != RawDiagnostic.NEUTRAL) {
                    res = nextResult
                }
            }

        }
        this.rawDiagnostic = res;
    }

    /*
    def _fill(TaggedsMap tMap) {
        if ((!exceptionFired) && (rawDiagnostic >= RawDiagnostic.WARNINGS)) {
            String name = this.testName
            /* TODO: CHANGE TO
            *
             *
            if (name == null) {
                tMap.put(this.data)
            } else {
                tMap.put(name, data)
            }
            //tMap.put(this.data)
        }
    }
*/

    @Override
    int compareTo(TztReport other) {
        return (this.getUniqueKey()).compareTo(other.getUniqueKey());
    }
}
