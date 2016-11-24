package org.gruth.utils

/**
 *
 * @author bamade
 */
// Date: 02/03/15

class TaggedsList<V> implements  TaggedsProvider<V>{
    List<TaggedObject<V>> list = new ArrayList<>()

    static {
        TaggedsList.getMetaClass().each = { Closure c -> list.each(c) };
    }

    TaggedsList(Object... objects) {
        this.add(objects)
    }

    /**
     * to avoid overloading of varargs constructor
     * and make a difference if Map or List is a real argument!
     * @param map
     * @return
     */
   static TaggedsList fromMap(Map map) {
       TaggedsList res = new TaggedsList()
       res.addMap(map)
       return res
    }

    /**
     * to avoid overloading of varargs constructor
     * and make a difference if Map or List is a real argument!
     * @param list
     * @return
     */
    static TaggedsList fromList(List list) {
        TaggedsList res = new TaggedsList()
        res.addList(list)
        return res
    }

    def add(Object... objects) {
        if(objects == null) { //bizarre but apparently needed
            TaggedObject tagged = TaggedObject.tag(null)
            list.add(tagged)
            return
        }

        for(Object obj: objects) { // each has problems with null values
            TaggedObject tagged = TaggedObject.tag(obj)
            list.add(tagged)
        }
    }

    def addList(List aList) {
        for(Object obj: aList) { // each has problems with null values
            TaggedObject tagged = TaggedObject.tag(obj)
            this.list.add(tagged)
        }
    }

    def addMap (Map map) {
        for(Map.Entry entry: map.entrySet()) {
            this.addKv(entry.key, entry.value)
        }
    }

    def addKv(String key, Object obj) {
       list.add(new TaggedObject(key, obj))
    }

    @Override
    int size() {
        return list.size()
    }

    @Override
    Iterator<TaggedObject<V>> iterator() {
        return list.iterator()
    }


    public String toString() {
        return list.toString()
    }

}
