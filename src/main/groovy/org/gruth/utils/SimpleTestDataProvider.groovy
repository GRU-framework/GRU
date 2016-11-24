package org.gruth.utils
/**
 *
 * @author bamade
 */
// Date: 03/03/15

class SimpleTestDataProvider implements TestDataProvider{
    static TaggedObject[] taggedObjectsModel = new TaggedObject[0]

    Object[] rawargs ;
    Combiner combiner
    Closure defaultExpectation = null;

    SimpleTestDataProvider(Object[] rawArgs) {
        this.rawargs = rawArgs
        if(rawargs.size() == 0) {
            return
        }
        ArrayList<TaggedObject> list = new ArrayList<>() ;
        for(Object obj : rawArgs) {
            if(obj instanceof  TaggedsProvider) {
                list.add(obj)
            } /*else
            if(obj instanceof TaggedsList) {
                list.add(obj)
            } */
            else {
                list.add(TaggedObject.tag(obj))
            }
        }
        combiner = new Combiner (list)
    }

    SimpleTestDataProvider(Closure defaultExpectation, Object[] rawArgs) {
        this(rawArgs)
        this.defaultExpectation = defaultExpectation
    }

    static TestDataProvider factory(Closure expectations, Object... args){
        if(args == null) {
            args = [null] as Object[]
        }
       return new SimpleTestDataProvider(expectations, args)
    }
    static TestDataProvider combination( Object... args){
        if(args == null) {
            args = [null] as Object[]
        }
        return new SimpleTestDataProvider( args)
    }

    class CombinerIterator implements Iterator<TestData> {
        Iterator combinerIterator = combiner.iterator()

        @Override
        boolean hasNext() {
            combinerIterator.hasNext()
        }

        @Override
        TestData next() {
            List<TaggedObject> res = combinerIterator.next()
            return new SimpleTestData(defaultExpectation,res.toArray(taggedObjectsModel))
        }
    }

    class EmptyIterator implements Iterator<TestData> {
        boolean invoked ;
        @Override
        boolean hasNext() {
           return !invoked
        }
        @Override
        TestData next() {
            invoked = true
            return SimpleTestData.EMPTY_ARGS
        }
    }

    @Override
    Iterator<TestData> iterator() {
        if(rawargs.length == 0) {
            return new EmptyIterator()
        }
        return new CombinerIterator()
    }
}
