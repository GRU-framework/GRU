package org.gruth.script

import org.gruth.utils.TestDataProvider

/**
 *
 * @author bamade
 */
// Date: 10/03/15

class CtorGroup implements TopContext{
    Class currentClass
    Binding _thisG = new Binding()

    CtorGroup(Class currentClass) {
        this.currentClass = currentClass
    }

    def _group (Closure closure){
        closure.setDelegate(this)
        closure()
    }

    // hack to avoid problems with ambiguous overloading when null
    CtorContext _test(String testName, Object arg) {
        if (arg == null) {
            arg = [null] as Object[]
        }
        return new CtorContext(testName, currentClass,  arg)
    }

    CtorContext _test(String testName, Object... args) {
        if (args == null) {
            args = [null] as Object[]
        }
        return new CtorContext(testName, currentClass,  args)
    }


    CtorContext _test(String testName,TestDataProvider dataProvider) {
        return new CtorContext(testName, currentClass,  dataProvider)
    }
}
