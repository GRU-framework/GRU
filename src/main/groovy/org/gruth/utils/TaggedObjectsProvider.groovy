package org.gruth.utils

/**
 *
 * @author bamade
 */
// Date: 27/03/15

class TaggedObjectsProvider implements TaggedsProvider{
    TestDataProvider argsProvider
    Closure generationClosure
    Closure postBuildClosure

    TaggedObjectsProvider(TestDataProvider argsProvider, Closure generationClosure, Closure postBuildClosure) {
        this.argsProvider = argsProvider
        this.generationClosure = generationClosure
        this.postBuildClosure = postBuildClosure
    }

    TaggedObjectsProvider(TestDataProvider argsProvider, Closure generationClosure) {
        this(argsProvider, generationClosure, null)
    }

    @Override
    int size() {
        return argsProvider.size()
    }

    class GeneratedObjectIterator implements  Iterator<TaggedObject> {
        Iterator<TestData> itArgs = argsProvider.iterator()

        @Override
        boolean hasNext() {
            return itArgs.hasNext()
        }

        @Override
        TaggedObject next() {
            TestData testData = itArgs.next()
            Object[] arguments = testData.args
            Object res = generationClosure.call(arguments)
            if(postBuildClosure != null) {
                postBuildClosure.setDelegate(res)
                //postBuildClosure.resolveStrategy = Closure.OWNER_ONLY
                postBuildClosure.call()
            }
            return TaggedObject.tag(res)
        }
    }

    @Override
    Iterator<TaggedObject> iterator() {
        return new GeneratedObjectIterator()
    }
}
