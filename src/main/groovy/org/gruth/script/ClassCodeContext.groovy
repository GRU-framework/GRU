package org.gruth.script

import org.gruth.utils.TaggedObject

/**
 *
 * @author bamade
 */
// Date: 10/03/15

class ClassCodeContext extends TestContext{
    Class currentClass
    ClassCodeContext(String testName, Class clazz, Closure code) {
        super(testName, clazz.getCanonicalName(),null, new TaggedObject(clazz.getSimpleName(), clazz))
        currentClass = clazz
        this.xx_private_setClos (code)
    }
    Class get_currentClass() {
        return this.currentClass
    }

    String get_key() {
        return this.currentClass.getSimpleName()
    }
}
