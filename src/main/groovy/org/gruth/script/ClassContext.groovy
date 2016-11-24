package org.gruth.script

import org.codehaus.groovy.runtime.MethodClosure
import org.gruth.utils.logging.GruLogger

/**
 *
 * @author bamade
 */
// Date: 24/02/15

class ClassContext {
    Class clazz
    ClassContext(Class clazz) {
        //println("CLASS CONTEXT")
        this.clazz = clazz

    }

    def _group(Closure closure){
      PackCst.CURLOG.fine('creating ClassContext', GruLogger.SYNTAX)
        closure.setDelegate(this)
        closure()
    }



    def _classState (String testName) {
        return new ClassStateContext(testName, clazz)
    }

    def _classCode (String testName, Closure code) {
        return new ClassCodeContext(testName, clazz, code)
    }

    ClassMethodGroup _classMethod (MethodClosure methodClosure) {
        return new ClassMethodGroup(clazz, methodClosure)
    }

    ClassMethodGroup _classMethod (String methodName) {
        MethodClosure methClos = clazz.&"$methodName"
        return new ClassMethodGroup(clazz, methClos)
    }

    ClassMethodGroup _classMethod (MethodClosure methodClosure, Closure closure) {
        ClassMethodGroup res =  new ClassMethodGroup(clazz, methodClosure)
        res._group(closure)
        return res
    }

    ClassMethodGroup _classMethod (String methodName, Closure closure) {
        MethodClosure methClos = clazz.&"$methodName"
        return _classMethod(methClos, closure)
    }

    CtorGroup _ctor() {
        return new CtorGroup(clazz)
    }

    CtorGroup _ctor( Closure closure) {
       CtorGroup res = new CtorGroup(clazz)
        res._group(closure)
        return res
    }

}