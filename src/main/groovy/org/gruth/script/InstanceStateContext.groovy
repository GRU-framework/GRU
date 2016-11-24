package org.gruth.script

import org.gruth.utils.TaggedObject
/**
 *
 * @author bamade
 */
// Date: 25/02/15

class InstanceStateContext extends TestContext{
    TaggedObject taggedObject ;
    InstanceStateContext(String testName, TaggedObject obj) {
        super(testName,obj.object!=null? obj.object.getClass().getSimpleName(): "null",  null, obj, obj.object == null)
        this.taggedObject = obj
    }

    def get_currentObject() {
        return this.taggedObject.object
    }

    String get_key() {
        return this.taggedObject.key
    }

}
