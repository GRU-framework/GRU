package org.gruth.utils

/**
 *
 * @author bamade
 */
// Date: 16/03/15

class DefUtils {
    Class clazz

    DefUtils(Class clazz) {
        this.clazz = clazz
    }

    def _defineStatic (Closure closure) {
        //todo if _values is defined skip
       ClassDefs classDefs = new ClassDefs(clazz)
        classDefs.defineIt(closure)
        LinkedTaggedsMap map = classDefs.linkedMap
        clazz.metaClass.'static'.getValuez = {map}
        //added later
        return map
    }

    static def _using (Class clazz) {
        return new DefUtils(clazz)
    }
    static def _using (Class clazz, Closure clos) {
        DefUtils res = new DefUtils(clazz)
       res._defineStatic (clos)
    }
}
