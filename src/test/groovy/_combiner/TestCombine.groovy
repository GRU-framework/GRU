package _combiner
import org.gruth.utils.Combiner
import org.gruth.utils.TaggedsList

/**
 *
 * @author bamade
 */
// Date: 04/03/15

def listA = ["name", TaggedsList.fromMap(first: "john", second:"elijah"), TaggedsList.fromList(["Jones","Smith"])]
def list1 = ["name", TaggedsList.fromMap(first: "john", second:"elijah")]
def list11 = [ TaggedsList.fromMap(first: "john", second:"elijah"), "name"]
def list2 = ["name", "jones", 345, null ]
def list3 = ["name"]

def listNull = [null, null]

Combiner comb1 = new Combiner(listNull)



/*
Iterator itr = comb1.iterator()
    while(itr.hasNext()) {
        println "result : " + itr.next()
    }
    */

[listA, list1, list11, list2, list3 ,listNull].each {
     println "New list $it"
    new Combiner(it).each {
        println it
    }

}

