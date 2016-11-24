package org.gruth.utils

/**
 *
 * @author bamade
 */
// Date: 02/03/15

interface TaggedsProvider<V> extends Iterable<TaggedObject<V>>{
    /**
     * @return   the possible number of elements INTEGER.MAX_VALUE if not known (dynamic providers)
     */
    int size()
}
