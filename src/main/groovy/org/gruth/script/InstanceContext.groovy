package org.gruth.script

import org.codehaus.groovy.runtime.MethodClosure
import org.gruth.utils.TaggedObject

/**
 *
 * @author bamade
 */
// Date: 24/02/15

class InstanceContext {
    TaggedObject currentObject
    Binding _thisObj = new Binding()

    InstanceContext(Object currentObject) {
        this.currentObject = TaggedObject.tag(currentObject)
        PackCst.CURLOG.fine("creating instance context $currentObject")
    }

    def _group(Closure closure) {
        closure.setDelegate(this)
        closure()
    }

    def _state(String testName) {
        return new InstanceStateContext(testName, currentObject)
    }

    def _code(String testName, Closure code) {
        return new InstanceCodeContext(testName, currentObject, code)
    }

    InstanceMethodGroup _method(MethodClosure methodClosure) {
        return new InstanceMethodGroup(currentObject, methodClosure)
    }

    InstanceMethodGroup _method(String methodName) {
        MethodClosure methClos
        if (currentObject.object != null) {
            methClos = currentObject.object.&"$methodName"
        } else {
            methClos = TaggedObject.NULL_Object.&"NO_METHOD_FOR_NULL_OBJECT"
        }
        return new InstanceMethodGroup(currentObject, methClos)
    }

    InstanceMethodGroup _method(MethodClosure methodClosure, Closure closure) {
        InstanceMethodGroup res = new InstanceMethodGroup(currentObject, methodClosure)
        res._group(closure)
        return res
    }

    InstanceMethodGroup _method(String methodName, Closure closure) {
        MethodClosure methClos
        if (currentObject.object != null) {
            methClos = currentObject.object.&"$methodName"
        } else {
            methClos = TaggedObject.NULL_Object.&"NO_METHOD_FOR_NULL_OBJECT"
        }
        return _method(methClos, closure)
    }

    def get_currentObject() {
        return currentObject.object
    }

    def get_key() {
        return currentObject.key
    }

}