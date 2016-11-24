package _utils

import org.gruth.utils.TaggedObject

/**
 *
 * @author bamade
 */
// Date: 02/03/15

NamedObject obj = []

TaggedObject tagged = TaggedObject.tag(obj)
println tagged

obj = null
tagged = TaggedObject.tag(obj)
println tagged

String[] array = ["hello", "world"]
tagged = TaggedObject.tag(array)
println tagged

int[] intArray = [1,3,4]
tagged = TaggedObject.tag(intArray)
println tagged

def aList = [1, 3.14, "hello"]
tagged = TaggedObject.tag(aList)
println tagged
