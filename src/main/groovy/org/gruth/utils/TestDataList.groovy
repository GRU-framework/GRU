package org.gruth.utils
/**
 *
 * @author bamade
 */
// Date: 09/03/15

class TestDataList implements TestDataProvider{

    List<TestDataProvider> internalList ;

    TestDataList() {
        internalList = Collections.emptyList()
    }
    TestDataList(List<TestDataProvider> listProviders) {
        this.internalList = listProviders
    }

    TestDataList(TestDataProvider aProvider) {
        List<TestDataProvider> list = new ArrayList<>()
        list.add(aProvider)
        this.internalList = list
    }

    static TestDataList fromArgs(Object[] args) {
       List<TestDataProvider> list = new ArrayList<>()
        SimpleTestDataProvider provider = new SimpleTestDataProvider(args)
        list.add(provider)
        return new TestDataList(list)
    }

    static TestDataList factory (TestDataProvider... objects) {
        List<TestDataProvider> list = new ArrayList<>()
        for(Object obj :objects) {
                list.add(obj)
        }
        return new TestDataList(list)
    }

    class DelegateIterator implements Iterator<TestData> {
        int listIndex = 0
        Iterator<TestData> currentIterator

        DelegateIterator() {
            if(internalList.size() > 0) {
                TestDataProvider currentProvider = internalList.get(0)
                currentIterator = currentProvider.iterator()
            }
        }

        @Override
        boolean hasNext() {
            if(listIndex >= internalList.size()) return false
            if(!currentIterator.hasNext()) {
                listIndex++
                if(listIndex >= internalList.size()) return false
                TestDataProvider currentProvider = internalList.get(listIndex)
                currentIterator = currentProvider.iterator()
            }
            return currentIterator.hasNext()
        }

        @Override
        TestData next() {
            def res =currentIterator.next()
            return res
        }
    }

    @Override
    Iterator<TestData> iterator() {
        return new DelegateIterator()
    }
}
