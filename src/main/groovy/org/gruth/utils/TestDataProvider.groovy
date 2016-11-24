package org.gruth.utils
/**
 *
 * @author bamade
 */
// Date: 09/03/15

interface TestDataProvider extends Iterable<TestData>{
    static class NoProvider implements TestDataProvider {
        @Override
        Iterator<TestData> iterator() {
            return Collections.emptyIterator()
        }
    }

    static class EmptyArgsIterator implements Iterator<TestData> {
        TestData singleton = SimpleTestData.EMPTY_ARGS
        boolean invoked ;

        @Override
        boolean hasNext() {
            if(! invoked) {
                invoked = true
                return true
            }
            return false
        }

        @Override
        TestData next() {
            return singleton
        }
    }

    static class EmptyArgsProvider implements TestDataProvider{

        @Override
        Iterator<TestData> iterator() {
            return new EmptyArgsIterator()
        }
    }

    public static TestDataProvider EMPTY_PROVIDER  = new NoProvider()
    public static TestDataProvider EMPTY_ARGS_PROVIDER  = new EmptyArgsProvider()
}