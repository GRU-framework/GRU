package _combiner

import org.gruth.utils.RangeProvider

/**
 *
 * @author bamade
 */
// Date: 11/03/15

RangeProvider provider = [0.00..2.00, {x -> x/100}]
provider.each {
    println it
}
 
