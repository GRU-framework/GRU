package org.gruth.utils
/**
 *
 * @author bamade
 */
// Date: 11/03/15

class RangeProvider<T> implements TestDataProvider {

    Range<T> range;
    Closure transform ;

    RangeProvider(Range<T> range, Closure transform) {
        this.range = range
        this.transform = transform
    }

    RangeProvider(Range<T> range) {
        this(range, null)
    }

    class RangeIterator implements Iterator<TestData> {
        Iterator iterator = range.iterator()

        @Override
        boolean hasNext() {
            return iterator.hasNext()
        }

        @Override
        TestData next() {
            T element = iterator.next()
            if(transform != null) {
                element = transform(element)
            }
            TaggedObject tagged = TaggedObject.tag(element)
            TaggedObject[] args = [tagged]
            return  new SimpleTestData(args)
        }
    }

    @Override
    Iterator<TestData> iterator() {
        return new RangeIterator()
    }
}
