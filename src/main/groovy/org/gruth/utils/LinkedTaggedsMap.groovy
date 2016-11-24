package org.gruth.utils
/**
 *
 * @author bamade
 */
// Date: 16/03/15

class LinkedTaggedsMap<T> extends LinkedHashMap<String, Object> implements TaggedsProvider {
    class DeepIterator implements Iterator<TaggedObject<T>> {
        Iterator<TaggedObject<T>> currentIterator = values().iterator()
        Iterator<TaggedObject<T>> delegateIterator;

        @Override
        boolean hasNext() {
            if (delegateIterator != null) {
                if (delegateIterator.hasNext()) return true;
                delegateIterator = null
            }
            return currentIterator.hasNext()
        }

        @Override
        TaggedObject<T> next() {
            if (delegateIterator != null) {
                return delegateIterator.next()
            }
            def obj = currentIterator.next()
            //todo : we assume it's not empty
            if (obj instanceof LinkedTaggedsMap) {
                delegateIterator = (obj as LinkedTaggedsMap).iterator()
                if (delegateIterator.hasNext()) {
                    return delegateIterator.next()
                } else {
                    return new TaggedObject<T>("EMPTY", null)
                }
            }
            return obj as TaggedObject
        }
    }

    class ValuesIterator implements Iterator<T> {
        DeepIterator deepIterator = new DeepIterator()

        @Override
        boolean hasNext() {
            return deepIterator.hasNext()
        }

        @Override
        T next() {
            return deepIterator.next().object
        }
    }

    class ValuesProvider implements Iterable<T> {

        @Override
        Iterator<T> iterator() {
            return new ValuesIterator()
        }
    }

    @Override
    Iterator<TaggedObject<T>> iterator() {
        return new DeepIterator()
    }

    Iterable<T> getValuesIterable() {
        return new ValuesProvider()
    }

    Iterable<T> getValuesIterable(String[] path) {
        if(path.length >0) {
           String key = path[0]  ;
            if(key.length() ==0) {
                return getValuesIterable()
            }
            def quest = this.get(key) ;
            if(quest instanceof LinkedTaggedsMap) {
                LinkedTaggedsMap<T> goal = (LinkedTaggedsMap<T>) quest ;
                int length = path.length -1 ;
                String[] newPath = new String[length] ;
                if(length >0) {
                    System.arraycopy(path, 1, newPath, 0, length)
                }
                return goal.getValuesIterable(newPath)
            } else {
                if(quest == null) {
                    return []
                }
                return [quest.object]
            }
        } else {
            return getValuesIterable()
        }
    }
}
