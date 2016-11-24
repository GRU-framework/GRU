package org.gruth.script

import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.MethodClosure
import org.gruth.utils.SimpleTestDataProvider
import org.gruth.utils.TaggedObject
import org.gruth.utils.TestDataProvider

/**
 *
 * @author bamade
 */
// Date: 10/03/15

class ClassMethodContext extends TestContext{
    Class currentClass
    String _testName
    String _methodName

    ClassMethodContext(String testName, Class clazz, MethodClosure methodClosure, Object[] args) {
        super(testName, clazz.getCanonicalName(),methodClosure.method, new TaggedObject(clazz.getSimpleName(), clazz))
        this.currentClass = clazz
        this._testName = testName
        xx_private_setClos methodClosure
        argsProvider = new SimpleTestDataProvider(args)
        _methodName = methodClosure.getMethod()
    }

    ClassMethodContext(String testName, Class clazz, MethodClosure methodClosure, TestDataProvider provider) {
        super(testName, clazz.getCanonicalName(),methodClosure.method, new TaggedObject(clazz.getSimpleName(), clazz))
        this.currentClass = clazz
        this._testName = testName
        xx_private_setClos methodClosure
        argsProvider = provider
        _methodName = methodClosure.getMethod()
    }
    public Class get_currentClass() {
        return this.currentClass
    }

    protected xx_private_exec(Object[] args) {
        //xxxx_code.setDelegate(this)
        //xxxx_code(args)
        InvokerHelper.invokeStaticMethod(currentClass, _methodName, args)
    }

}
