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
// Date: 03/03/15

class InstanceMethodContext extends TestContext{
    TaggedObject taggedObject
    String _testName
    String _methodName

    InstanceMethodContext(boolean skipContext, String testName, TaggedObject taggedObject, MethodClosure methodClosure, Object[] args) {
        super(testName,taggedObject.object!=null? taggedObject.object.getClass().getSimpleName(): "null",  methodClosure.getMethod(), taggedObject, skipContext)
        this._testName = testName
        this.taggedObject = taggedObject
        xx_private_setClos methodClosure
        argsProvider = new SimpleTestDataProvider(args)
        _methodName = methodClosure.getMethod()
    }

    InstanceMethodContext(boolean skipContext, String testName, TaggedObject taggedObject, MethodClosure methodClosure, TestDataProvider provider) {
        super(testName,taggedObject.object!=null? taggedObject.object.getClass().getSimpleName(): "null",  methodClosure.getMethod(), taggedObject, skipContext)
        this._testName = testName
        this.taggedObject = taggedObject
        xx_private_setClos methodClosure
        argsProvider = provider
        _methodName = methodClosure.getMethod()
    }

    public Object get_currentObject() {
        return taggedObject.object
    }

    protected xx_private_exec(Object[] args) {
        //xxxx_code.setDelegate(this)
        //xxxx_code(args)
        InvokerHelper.invokeMethod(taggedObject.object, _methodName, args)
    }



}
