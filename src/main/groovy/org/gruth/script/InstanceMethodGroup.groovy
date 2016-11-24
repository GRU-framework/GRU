package org.gruth.script
import org.codehaus.groovy.runtime.MethodClosure
import org.gruth.utils.TaggedObject
import org.gruth.utils.TestDataProvider

/**
 *
 * @author bamade
 */
// Date: 25/02/15

class InstanceMethodGroup implements TopContext{
    TaggedObject taggedObject
    MethodClosure methodClosure
    Binding _thisG = new Binding()

    InstanceMethodGroup(TaggedObject taggedObject, MethodClosure methodClosure) {
        this.taggedObject = taggedObject
        this.methodClosure = methodClosure
    }

    def _group (Closure closure){
        closure.setDelegate(this)
        closure()
    }
    def _state(String testName) {
        return new InstanceStateContext(testName, taggedObject)
    }

    def _code(String testName, Closure code) {
        return new InstanceCodeContext(testName, taggedObject, code)
    }

    // this method to avoid ambiguity problems when invoked with null arg
    InstanceMethodContext _test(String testName, Object arg) {
        boolean skipContext = (taggedObject.object == null)
        if(arg == null) {
            arg = [null] as Object[]
        }
        return new InstanceMethodContext(skipContext, testName, taggedObject, methodClosure, arg)
    }

    InstanceMethodContext _test(String testName, Object... args) {
        boolean skipContext = (taggedObject.object == null)
        return new InstanceMethodContext(skipContext, testName, taggedObject, methodClosure, args)
    }


    InstanceMethodContext _test(String testName,TestDataProvider dataProvider) {
        boolean skipContext = (taggedObject.object == null)
        return new InstanceMethodContext(skipContext, testName, taggedObject, methodClosure, dataProvider)
    }

    def get_currentObject() {
        return taggedObject.object
    }

    def get_key() {
        return taggedObject.key
    }
}
