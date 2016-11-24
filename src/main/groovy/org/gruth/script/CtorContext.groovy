package org.gruth.script
import org.codehaus.groovy.runtime.InvokerHelper
import org.gruth.utils.SimpleTestDataProvider
import org.gruth.utils.TaggedObject
import org.gruth.utils.TestDataProvider

/**
 *
 * @author bamade
 */
// Date: 25/02/15

class CtorContext extends TestContext{
    static String ctorName = '<init>'
    static Closure emptyClosure = {}
    Class currentClass
    String _testName
    String _methodName = ctorName

    CtorContext(String testName, Class clazz,  Object[] args) {
        super(testName, clazz.getCanonicalName(),ctorName, new TaggedObject(clazz.getSimpleName(), clazz))
        this.currentClass = clazz
        this._testName = testName
        xx_private_setClos emptyClosure
        argsProvider = new SimpleTestDataProvider(args)
    }

    CtorContext(String testName, Class clazz,  TestDataProvider provider) {
        super(testName, clazz.getCanonicalName(),ctorName, new TaggedObject(clazz.getSimpleName(), clazz))
        this.currentClass = clazz
        this._testName = testName
        xx_private_setClos emptyClosure
        argsProvider = provider
    }
    public Class get_currentClass() {
        return this.currentClass
    }

    protected xx_private_exec(Object[] args) {
        //xxxx_code.setDelegate(this)
        //xxxx_code(args)
        InvokerHelper.invokeConstructorOf(currentClass,  args)
    }
}
