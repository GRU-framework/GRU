package org.gruth.utils
/**
 *
 * @author bamade
 */
// Date: 03/03/15

class SimpleTestData implements  TestData{
    public static final SimpleTestData EMPTY_ARGS = new SimpleTestData(new TaggedObject[0])

    TaggedObject[] taggedObjects;
    Closure defaultExpectations = null

    SimpleTestData(TaggedObject[] taggedObjects) {
        this.taggedObjects = taggedObjects
    }

    SimpleTestData(Closure defaultExpectations,TaggedObject[] taggedObjects) {
        this.taggedObjects = taggedObjects
        this.defaultExpectations = defaultExpectations
    }
    String[] getArgNames() {
        String[] res = new String[taggedObjects.length]
        for(int ix = 0; ix < taggedObjects.length; ix++) {
            res[ix] = taggedObjects[ix].key
        }
       return res
    }

    Object[] getArgs() {
        Object[] res = new Object[taggedObjects.length]
        for(int ix = 0; ix < taggedObjects.length; ix++) {
            res[ix] = taggedObjects[ix].object
        }
        return res
    }

    Closure getDefaultXpectations() {
     return this.defaultExpectations
    }

    public String toString() {
        return Arrays.toString(taggedObjects)
    }

}
