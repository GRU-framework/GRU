package org.gruth.script
import org.gruth.reports.RawDiagnostic
import org.gruth.reports.ResultHandlers
import org.gruth.reports.TztReport
import org.gruth.utils.TaggedObject
import org.gruth.utils.TestData
import org.gruth.utils.TestDataList
import org.gruth.utils.TestDataProvider

import java.lang.reflect.Field

import static org.gruth.script.GruScript.get_globalEnv
import static org.gruth.script.GruScript.get_thisScript
/**
 *
 * @author bamade
 */
// Date: 02/03/15

abstract class TestContext implements ExecutionContext, TopContext{
    Closure pre;
    Closure post;
    Closure xpectations ;

    protected Closure xxxx_code ;
    TestDataProvider argsProvider = TestDataList.EMPTY_ARGS_PROVIDER;
    def _thisT = new Binding()

    //to be cleaned at each run
    TztReport _report;
    Object _result ;
    Object[] _args ;
    boolean exceptionFired;
    Closure defaultExpectations ;
    Throwable executionCaught ;
    boolean rethrow ;

    // private fields
    private String testName ;
    private String  className ;
    private String methodName ;
    private TaggedObject taggedObject ;
    private boolean doAccumulate ;
    private boolean skipContext =false

    TestContext(String testName, String className, String methodName, TaggedObject taggedObject, boolean skipContext) {
        this.testName = testName
        this.className = className
        this.methodName = methodName
        this.taggedObject = taggedObject
        this.skipContext = skipContext
    }

    TestContext(String testName, String className, String methodName, TaggedObject taggedObject) {
       this(testName, className, methodName, taggedObject, false)
    }

    private def clean() {
        _report = new TztReport(testName,className, taggedObject, methodName)
        this.exceptionFired = false
        this._result = null
        _args = null
        this.defaultExpectations = null
        this.executionCaught = null
        this.rethrow = false
    }



    def _pre(Closure closure) {
        pre = closure
        closure.setDelegate(this)
        return this
    }

    def _post(Closure closure) {
        post = closure
        closure.setDelegate(this)
        return this
    }

    protected xx_private_setClos(Closure codeClosure) {
        xxxx_code = codeClosure
        codeClosure.setDelegate(this)
    }

    protected xx_private_exec(Object[] args) {
       //xxxx_code.setDelegate(this)
       xxxx_code()
    }

    Production _xpect(Closure xpectations) {
        this.xpectations = xpectations
        Production production = new Production(this)
        return xx_xcuteTests(production)
    }

    Production _xpect() {
        return _xpect(null)
    }

    Production _xpectAndYield(Closure xpectations) {
        this.xpectations = xpectations
        Production production = new Production(this)
        return production
    }

    Production _xpectAndYield() {
        return _xpectAndYield (null)
    }

    Production _withEach (Closure closure) {
        return _xpectAndYield()._withEach(closure)
    }

    def or(cl) {
        if(cl instanceof Closure) {
            return _withEach (cl as Closure)
        }
        //todo: syntax error
    }

    Production xx_xcuteTests(Production production) {
        try {
            if (_thisScript.stopScript || _globalEnv.stopAll) {
                return production
            }
            for (TestData taggedArgs : argsProvider) {
                clean()
                _args = taggedArgs.args
                _report.argsString = Arrays.toString(taggedArgs.argNames)
                boolean getGoing = true
                this.defaultExpectations = taggedArgs.defaultXpectations
                boolean runDefaultTest = (defaultExpectations != null)
                if (_thisScript.skipTests || skipContext) {
                    this._report.rawDiagnostic = RawDiagnostic.SKIPPED
                    ResultHandlers.handleReport(null, _report)
                    // todo: do not put in production?
                    dealWithResult(production, null)
                    continue
                }
                if (pre) {
                    try {
                        pre()
                    } catch (Throwable th) {
                        _reportException(th, RawDiagnostic.TOOL_OR_CODE_ERROR)
                    }
                }
                if (getGoing) {
                    if (xxxx_code != null) {
                        try {
                            _result = xx_private_exec(_args)
                        } catch (GroovyRuntimeException grex) {
                            //println 'GROOVY EXC'
                            _reportException(grex, RawDiagnostic.NOT_YET_IMPLEMENTED)
                        } catch (Throwable th) {
                            this.executionCaught = th
                            _reportException(th, RawDiagnostic.FAILED)
                        }
                    }
                    if (xpectations != null) {
                        try {
                            xpectations.setDelegate(this)
                            xpectations()
                        } catch (Throwable th) {
                            _reportException(th, RawDiagnostic.TOOL_OR_CODE_ERROR)

                        }
                    }
                    if (runDefaultTest) {
                        try {
                            this.defaultExpectations.setDelegate(this)
                            this.defaultExpectations()
                        } catch (Throwable th) {
                            _reportException(th, RawDiagnostic.TOOL_OR_CODE_ERROR)

                        }
                    }
                    if (post) {
                        try {
                            post()
                        } catch (Throwable th) {
                            _reportException(th, RawDiagnostic.TOOL_OR_CODE_ERROR)

                        }
                    }
                }
                this._report.gatherResult()
                ResultHandlers.handleReport(null, _report)
                dealWithResult(production, _result)
            }
        } finally {
            production.stop()
        }
        return  production
    }

    private void dealWithResult(Production production, Object res) {
        if(this.executionCaught != null) {
            production.lastThrown = this.executionCaught;
            if(this.rethrow) {
                throw this.executionCaught
            }
        }
        if(doAccumulate) {
            production.accumulate(res)
        } else {
            production.set(res)
        }

    }


    /////////// METHODS

    def _accumulate(boolean keepResults) {
        doAccumulate = keepResults
    }

    def _message(String mess, Object... args) {
        this._report.message(mess, args)
    }
    /**
     *  used to add some data to the _report (does not change the result of the test)
     *
     * @param data any serializable data
     * @return the argument
     */
    Serializable _reportData(Serializable data) {
        return this._report.reportData(data)
    }

    def _neutral(String stringExpression) {
        this.exceptionFired = this._report.neutral(stringExpression)
    }

    def _neutral(String message, Closure closure) {
        this.exceptionFired = this._report.neutral(message, closure)
    }

    def _doNotReport() {
        this._report.doNotReport()
    }

    def _okIfCaught(Class<? extends Throwable> throwClass) {
        this._report.okIfCaught(throwClass)
    }

    def _okIfCaughtAndRethrown (Class<? extends Throwable> throwClass) {
        boolean res = _okIfCaught(throwClass)
        if(res) {
            this.rethrow = true ;
        }
        return res
    }

    boolean _isCaught(Class<? extends Throwable> throwClass) {
        return this._report.isCaught(throwClass)
    }

    boolean _rethrownIfCaught(Class<? extends Throwable> throwClass) {
        boolean res = _isCaught(throwClass)
        if(res) {
            this.rethrow = true
        }
        return res
    }

    //TODO: change that not called
    void _reportException(Throwable th, RawDiagnostic result) {
        this._report.reportException(th, result)
        this.exceptionFired = true
    }

    def _okIf(boolean expression, String message, Object... args) {
        _failIfNot(expression, message, args)
    }

    def _okIf(String stringBooleanExpression) {
        _failIfNot(stringBooleanExpression)
    }

    def _ok(Object object) {
        if(object == null) {
           _failIf(_result != null ," result should be null")
        } else {
            _failIfNot( object.equals(_result), "result should not be {0}", _result)
        }
    }

    /**
     * Adds an <TT>AssertionReport</TT> to the current <TT>TztReport</TT> object:
     * the result is <TT>FAILED</TT> if the evaluation of the string is true
     * (the _report also contains the evaluated string).
     * [NOT WORKING PROPERLY -YET-]
     * @param stringBooleanExpr a Gstring representing a valid Groovy expression
     * @return
     */
    def _failIf(String stringBooleanExpr) {
        this.exceptionFired = this._report.failIf(stringBooleanExpr)
    }
    /**
     * Adds an <TT>AssertionReport</TT> to the current <TT>TztReport</TT> object:
     * the result is <TT>FAILED</TT> if the evaluation of the expression is true
     * (the _report also contains the message)
     * @param message to be displayed in the _report
     * @param booleanExpr before evaluating exceptionfired should be tested
     * @return
     */
    def _failIf(boolean booleanExpr, String message, Object... args) {
        this.exceptionFired = this._report.failIf( booleanExpr, message, args)
    }

    def _fail(String message, Object... args) {
        this._failIf(true, message, args)
    }

    /**
     * registers a test failure assertions but all other tests in the current script will be skipped
     * (except if <TT>_stopSkipping(true)</TT> is invoked later in the code.
     * Note that all other assertions in the _xpect block will still be evaluated.
     *
     * @param message
     * @param booleanExpr
     * @return
     */
    def _scriptLevelSkipIf( boolean booleanExpr, String message, Object... args) {
        if(booleanExpr) {
            _thisScript.skipTests = true
        }
        this.exceptionFired = this._report.registerAssertWithDiagnostic( booleanExpr,RawDiagnostic.SCRIPT_LEVEL_FAIL, message, args)
    }

    /**
     * test skipping capability is modified (mostly used with argument true
     * to stop skipping test)
     * @param stop
     * @return
     */
    def _stopSkipping(boolean stop) {
        _thisScript.skipTests = !stop
    }

    /**
     * registers a test failure and all other test in the current script will not generate a report.
     * @param message
     * @param booleanExpr
     * @return
     */
    def _scriptLevelFailIf(boolean booleanExpr, String message, Object... args) {
        if(booleanExpr) {
            _thisScript.stopScript = true
        }
        this.exceptionFired = this._report.registerAssertWithDiagnostic( booleanExpr,RawDiagnostic.SCRIPT_LEVEL_FAIL, message, args)
    }

    /***
     * registers a <TT>FATAL</TT> test failure and all other tests in all scripts  in the same JVM will not  be run or generate a report.
     * @param message
     * @param booleanExpr
     * @return
     */
    def _fatalFailIf( boolean booleanExpr, String message, Object... args) {
        if(booleanExpr) {
            _globalEnv.stopAll = true
        }
        this._report.registerAssertWithDiagnostic(booleanExpr,RawDiagnostic.FATAL, message, args)
        this.exceptionFired = true

    }

    /**
     * same specs as failIf but faisl if false
     * @param stringBooleanExpr
     * @return
     */
    def _failIfNot(String stringBooleanExpr) {
        this.exceptionFired = this._report.failIfNot(stringBooleanExpr)
    }

    /**
     * same specs as failIf but fails if false
     */
    def _failIfNot( boolean booleanExpr, String message, Object... args) {
        this.exceptionFired = this._report.failIfNot(booleanExpr, message, args)
    }

    /**
     * Adds an <TT>AssertionReport</TT> to the current <TT>TztReport</TT> object:
     * the result is <TT>WARNINGS</TT> if the evaluation of the string is true
     * (the _report also contains the evaluated string).
     * [NOT WORKING PROPERLY -YET-]
     * @param stringBooleanExpr a Gstring representing a valid Groovy expression
     * @return
     */
    def _warnIf(String stringBooleanExpr) {
        this.exceptionFired = this._report.warnIf(stringBooleanExpr)
    }

    /**
     * Adds an <TT>AssertionReport</TT> to the current <TT>TztReport</TT> object:
     * the result is <TT>WARNINGS</TT> if the evaluation of the expression is true
     * (the _report also contains the message)
     * @param message to be displayed in the _report
     * @param booleanExpr
     * @return
     */
    def _warnIf( boolean booleanExpr, String message, Object... args) {
        this.exceptionFired = this._report.warnIf(booleanExpr, message, args)
    }

    /**
     * same specs as warnIf but warns if false
     */
    def _warnIfNot(String stringBooleanExpr) {
        this.exceptionFired = this._report.warnIfNot(stringBooleanExpr)
    }

    /**
     * same specs as warnIf but warns if false
     */
    def _warnIfNot( boolean booleanExpr, String message, Object... argds) {
        this.exceptionFired = this._report.warnIfNot(booleanExpr, message, args)
    }

    boolean  _isSet(String fieldName){
        boolean res = false
        Object curObj = taggedObject.object
        Class curClass;
        if(curObj instanceof Class) {
            curClass = curObj
        } else {
            curClass = curObj.class
        }

        while (curClass != null) {
            try {
                Field field = curClass.getDeclaredField(fieldName)
                field.setAccessible(true)
                Object value = field.get(curObj)
                res = value != null
                curClass = null
            } catch (Exception exc) {
                curClass = curClass.getSuperclass()
            }
        }
        return res
    }

    Object _tagResult(){
        _result.metaClass._gruTag = '#'+_report.argsString //+'('+ _result.toString()+')'
        return _result
    }
    Object _tagResult(String tag){
        _result.metaClass._gruTag = tag
        return _result
    }

    String get_argsString(){
        return _report.argsString
    }

    Object get_currentObject() {
        return taggedObject.object
    }

    Object get_key() {
        return taggedObject.key
    }

    boolean get_exceptionFired() {
        return exceptionFired
    }

}