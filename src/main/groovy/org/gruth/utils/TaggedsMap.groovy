package org.gruth.utils

/**
 * This class is used to store test objects (descriptions, results).
 * <P>
 *  Due to its specific properties it should never been tested for anything else.
 *  Every Object stored is converted in a <TT>TaggedObject (used internally by GRU)
 * <P>
 *  Implementation is naïve and should be retested
 */
class TaggedsMap extends TreeMap {

    /* BUG in polymorphic use of toString! *
    static {
        TaggedsMap.metaClass.toString = {
            toMyString()
        }
    }
    /**/

    TaggedsMap() { super() }

    TaggedsMap(Comparator comparator) { super(comparator)}

    TaggedsMap(Map m) {
        super()
        putAll( m)
    }

    TaggedsMap(SortedMap m) {
        super()
        putAll (m)
    }

    TaggedsMap(WithStringKey obj){
        super()
        put obj
    }

    TaggedsMap(Object obj){
        super()
        if (obj instanceof WithStringKey){
            put( obj as WithStringKey)
        } else {

            //put(obj.toString(), obj)
         put( TaggedObject.tag(obj))
        }
    }

    def put(key, value) {
        if (value instanceof TaggedObject) {
             TaggedObject named = value as TaggedObject
            super.put(named.key, value)
            // TODO:log discrepancies
            // check if coherent! super.put(key, value)
        } else {
            super.put(key, new TaggedObject(key, value))
        }
    }

    def put(Object object){
        if (object instanceof TaggedObject) {
           TaggedObject named = object as TaggedObject
            super.put(named.key, object)
        } else if (object instanceof WithStringKey) {
            WithStringKey keyed = object as WithStringKey
            super.put(keyed.key, new TaggedObject(keyed.key, keyed))
        } else {
            TaggedObject tagged = TaggedObject.tag(object);
            super.put(tagged.key, tagged)
        }
    }

    TaggedsMap plus(Map map ){
        putAll( map)
        return this
    }

    TaggedsMap plus(Object obj){
        put(obj)
        return this
    }

    TaggedsMap leftShift(Map map) {
        putAll( map)
        return this
    }
    TaggedsMap leftShift(List list) {
        list.each {
           /* TaggedsMap is considered special.
             But a list containing simple Maps must be considered 'as is!"
             looks like a contradiction with leftShift(Map ) but it isn't
            */
            if(it instanceof TaggedsMap) {
                TaggedsMap other = it as TaggedsMap
                putAll(other)
            } else  {
                put(it)
            }
        }
        return this
    }

    TaggedsMap leftShift (Object obj) {
        put(obj)
        return this
    }

/*.
   TaggedsMap plus(TaggedsMap tmapA){
       tmapA.values().each {
           WithStringKey obj = it
           this.put(obj)
       }
       return this

    }
    */

    Object getValue(key) {
        TaggedObject mapped = (TaggedObject) get(key)
        return mapped.object
    }
/**
 * to be verified
 * @param map
 * @return
 */
    void putAll(Map map) {
        super.putAll(map)
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder("[ ");
        values().each {
            build.append(it).append(' ,')
        }
        build.setCharAt(build.length() -1, ']' as char)
        return build.toString()
    }

    public String toMyString() {
        return this.toString()
    }

}
