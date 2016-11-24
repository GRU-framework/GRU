package org.gruth.script

import org.gruth.utils.TaggedsProvider

/**
 *
 * @author bamade
 */
// Date: 24/02/15

trait TopContext {

    def _this = new Binding()

     ClassContext _withClass( Class clazz) {
        PackCst.CURLOG.fine("_withClass : invoked with $clazz" )
         return  new ClassContext(clazz)
    }

    ClassContext _withClass (Class clazz, Closure closure) {
        def res = new ClassContext(clazz)
        res._group closure
        return res
    }


    InstanceContext _with (Object obj, Closure closure) {
        if(obj == null) {
            PackCst.CURLOG.error("_withObjects : null Object argument", )
            return // NullInstanceContext
        }
        def res = new InstanceContext(obj)
        res._group closure
        return res
    }

    InstanceContext _with(Object obj) {
        PackCst.CURLOG.fine(" single _with")
       return new InstanceContext(obj)
    }

    Production _withEach (TaggedsProvider provider, Closure code) {
        MultipleInstanceContext multipleInstanceContext =  new MultipleInstanceContext(provider, code)
        return multipleInstanceContext.production
    }

}
