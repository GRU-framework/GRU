package org.gruth.utils
/**
 * An Object linked to its key.
 * Looks like a Map.Entry but used internally for specific purposes in GRU
 */
class TaggedObject<V> implements WithStringKey, Serializable {
    public static final long serialVersionUID = 1237L;
    static enum StrangeObjs {
        NULL;
        public void NO_METHOD_FOR_NULL_OBJECT(Object... nulArgs) {

        }
    }
    public static final Object NULL_Object = StrangeObjs.NULL

    final String key;

    /**
     * beware if not Serializable this object will dumped on serialization
     */
    transient V object;
    //??? don't remember???
    boolean keep;

    TaggedObject(String key, V object) {
        this.key = key
        this.object = object
    }

    TaggedObject(Map.Entry<String, V> entry) {
        this.key = entry.key
        this.object = entry.value

    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject()
        if (!(this.object instanceof Serializable)) {
            out.writeObject('-not serializable-:' + this.object.toString())
        } else {
            out.writeObject(this.object)
        }
    }

    private void readObject(java.io.ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject()
        this.object = input.readObject()
    }

    boolean equals(o) {
        if (this.is(o)) return true;
        if (!(o instanceof TaggedObject)) return false;

        TaggedObject that = (TaggedObject) o;

        if (key != that.key) return false;

        return true;
    }

    int hashCode() {
        return key.hashCode();
    }

    String toString() {
        key + ': ' + object
    }

    String getKey() {
        return key
    }

    static Map<Class,Closure> taggingMap = new HashMap<>()

    public static def _autoID(Class clazz, Closure idClosure) {
        taggingMap.put(clazz, idClosure)
    }

    public static TaggedObject tag(String key,Object obj) {
       return new TaggedObject(key, obj)
    }

    public static TaggedObject tag(Object obj) {
        if(obj == null || obj == NULL_Object) {
            return new TaggedObject('_null', null)
        }
        if(''.equals(obj)){
            return new TaggedObject('Strings.nocontent.EMPTY', obj)
        }
        if (obj instanceof TaggedObject) return obj
        if (obj instanceof WithStringKey) {
            WithStringKey keyed = obj as WithStringKey
            return new TaggedObject(keyed.key, keyed)
        }
        if(obj instanceof Map.Entry) {
            return new TaggedObject(obj as Map.Entry)
        }
        /*
        if(obj.metaClass.respondsTo(obj, 'getName')){
            return new TaggedObject(obj.getName(), obj)
        }
        */
        Class clazz = obj.getClass()
        if(clazz.isArray()) {
            //String key = Arrays.toString(obj)
            String key = tagIterable(Arrays.asList(obj))
            return new TaggedObject(key, obj)
        }

        Closure idClos = taggingMap.get(clazz)
        if (idClos != null) {
            idClos.setDelegate(obj)
            try {
                Object keyObj = idClos()
                //obj.metaClass._gruID = keyObj
                String key = String.valueOf(keyObj)
                return new TaggedObject(key, obj)
            } catch (Exception exc) {
                PackCst.CURLOG.severe("id generation failed for " + obj, exc)
            }
        }



        MetaClass metaClazz = obj.metaClass
        if( null != metaClazz.hasProperty(obj, '_gruID')) {
            String key = metaClazz.getProperty(obj, '_gruID')
            return new TaggedObject(key, obj)
        }
        def methNames = [ "getName", "getKey", "getId"]
        for (String name: methNames) {
            try {
                MetaMethod method = metaClazz.getMetaMethod(name)
                if(method != null) {
                    def res = method.invoke(obj)
                    if(res instanceof  String) {
                        return  new TaggedObject(res, obj)
                    }
                }

            } catch (Exception exc) {
                // sorry
                //System.err.println(" caught " +exc)
            }
        }
        if( null != metaClazz.hasProperty(obj, '_gruTag')) {
            String key = metaClazz.getProperty(obj, '_gruTag')
            return new TaggedObject(key, obj)
        }
        if(obj instanceof Iterable){
            String key = tagIterable((Iterable) obj)
            return new TaggedObject(key, obj)
        }
        String projectedTag = String.valueOf(obj)
        return new TaggedObject(projectedTag, obj)
    }

    static String tagIterable(Iterable it) {
        List<String> list = new ArrayList<>()
        Iterator iterator = it.iterator()
        for(int ix = 0 ;ix < 11 ; ix++){
            if(iterator.hasNext()) {
                String key
                if(ix == 10) {
                    key = '...'
                } else {
                    TaggedObject tagged = tag(iterator.next())
                    key = tagged.key
                }
                list.add(key)
            } else {
                break ;
            }
        }
        return list.toString()
    }


}
