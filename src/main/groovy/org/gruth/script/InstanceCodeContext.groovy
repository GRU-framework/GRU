package org.gruth.script

import org.gruth.utils.TaggedObject
/**
 *
 * @author bamade
 */
// Date: 02/03/15

class InstanceCodeContext extends TestContext{
    TaggedObject taggedObject ;
    InstanceCodeContext(String testName, TaggedObject obj, Closure code) {
        super(testName,obj.object!=null? obj.object.getClass().getSimpleName(): "null",null,  obj, obj.object == null)
        this.taggedObject = obj
        this.xx_private_setClos (code)
    }

    def get_currentObject() {
        return this.taggedObject.object
    }

    String get_key() {
        return this.taggedObject.key
    }

}
