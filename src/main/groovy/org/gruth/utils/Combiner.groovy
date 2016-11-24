package org.gruth.utils

/**
 *
 * @author bamade
 */
// Date: 04/03/15

class Combiner implements Iterable<List> {
    //TaggedsList currentList;
    TaggedsProvider currentList;
    Combiner nextCombiner;

    Combiner(List list) {
        if(list.isEmpty()) {
            currentList = TaggedsList.fromList(list)
            return
        }
        Object first = list.get(0);
        if (first instanceof TaggedsProvider) {
        //if (first instanceof TaggedsList) {
            currentList = first
        } else {
            currentList = new TaggedsList(first)
        }
        List subList = list.subList(1, list.size())
        if (subList.size() != 0) {
            nextCombiner = new Combiner(subList)
        }
    }

    class CombinerIterator implements Iterator<List<TaggedObject>> {
        Iterator thisIterator;
        Iterator nextIterator;
        Object currentData;
        boolean  okNext ;

        CombinerIterator() {
            thisIterator = currentList.iterator()
            getThisNext()
        }

        @Override
        boolean hasNext() {
            return okNext
        }

        private void getThisNext() {
            if(thisIterator.hasNext()) {
                currentData = thisIterator.next();
                if (nextCombiner) {
                    nextIterator = nextCombiner.iterator()
                }
                okNext = true
            } else {
                okNext = false
            }
        }

        @Override
        List<TaggedObject> next() {
            List<TaggedObject> res = new ArrayList<>()
            Object toAdd = currentData
            if (nextIterator) {
                if (nextIterator.hasNext()) {
                    List addList = nextIterator.next()
                    res.addAll(addList)
                    if( ! nextIterator.hasNext()) {
                        getThisNext()
                    } else {
                        okNext = true
                    }
                } else {
                    getThisNext()
                }
            } else {
                getThisNext()
            }
            res.add(0, toAdd)
            return res
        }
    }

    @Override
    Iterator<List> iterator() {
        return new CombinerIterator()
    }
}
