package org.gruth.utils


/**
 *
 * @author bamade
 */
// Date: 16/03/15

class ClassDefs {
    Class clazz ;
    int level = 0 ;
    LinkedTaggedsMap linkedMap = new LinkedTaggedsMap()
    String path ;

    ClassDefs(Class clazz) {
        this.clazz = clazz
        path = clazz.getSimpleName()
        level = 0 ;
    }

    ClassDefs(Class clazz, int level, String path) {
        this.clazz = clazz
        this.level = level ;
        this.path = path
    }

    def defineIt(Closure clos ) {
        clos.setDelegate(this)
        clos.setResolveStrategy(Closure.DELEGATE_ONLY)
        clos()
    }

    public def methodMissing(String varName, args) {
        def toRegister
        String pathName = getPath()+ "."+varName
        if(args == null || args.length == 0) {
            toRegister = new TaggedObject(path+varName, null)
        } else {
            def obj = args[0]
            if(obj instanceof Closure) {
                ClassDefs classDefs = new ClassDefs(clazz, level+1, pathName)
                classDefs.defineIt (obj as Closure)
                toRegister = classDefs.getLinkedMap()
            } else {
               toRegister = new TaggedObject<>(pathName, obj)
            }
            //println( "Method name $varName : $obj")
            //toUpperCase
            if(level == 0 ) {
                char zechar = varName.charAt(0)
                char uppeChar = Character.toUpperCase(zechar)
                def upVarName = String.valueOf(uppeChar) + varName.substring(1)
                clazz.metaClass.'static'."get$upVarName" = { toRegister }
            }
        }
        getLinkedMap().put(varName, toRegister)
    }

    LinkedTaggedsMap getLinkedMap() {
        return this.linkedMap
    }

    String getPath() {
        String res = this.path
        return res
    }
}
