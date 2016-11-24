package org.gruth.script

import org.codehaus.groovy.runtime.MethodClosure
import org.gruth.utils.TestDataProvider

/**
 *
 * @author bamade
 */
// Date: 10/03/15

class ClassMethodGroup implements TopContext{
    Class currentClass
    MethodClosure methodClosure
    Binding _thisG = new Binding()

    ClassMethodGroup(Class currentClass, MethodClosure methodClosure) {
        this.currentClass = currentClass
        this.methodClosure = methodClosure
    }

    def _group (Closure closure){
        closure.setDelegate(this)
        closure()
    }

    // hack to check for problems with ambiguous null args
    ClassMethodContext _test(String testName, Object arg) {
        if(arg == null) {
            arg = [null] as Object[]
        }
        return new ClassMethodContext(testName, currentClass, methodClosure, arg)
    }

    ClassMethodContext _test(String testName, Object... args) {
        return new ClassMethodContext(testName, currentClass, methodClosure, args)
    }


    ClassMethodContext _test(String testName,TestDataProvider dataList) {
        return new ClassMethodContext(testName, currentClass, methodClosure, dataList)
    }
}
