package org.gruth.script

import org.gruth.utils.TaggedObject

/**
 *
 * @author bamade
 */
// Date: 10/03/15

class ClassStateContext extends TestContext{
    Class currentClass

    public ClassStateContext(String testName, Class clazz) {
        super(testName, clazz.getCanonicalName(),null, new TaggedObject(clazz.getSimpleName(), clazz))
        this.currentClass = clazz
    }

    Class get_currentClass() {
        return this.currentClass
    }

    String get_key() {
        return this.currentClass.getSimpleName()
    }
}
