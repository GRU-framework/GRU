package org.gruth.script


import org.codehaus.groovy.GroovyExceptionInterface
import org.gruth.utils.TaggedObject

import java.util.concurrent.*
/**
 *
 * @author bamade
 */
// Date: 11/03/15

class Production implements  Iterable<Object>{
    static final Object POISON_PILL =  new Object()
    ExecutionContext testContext
    ArrayList<Object> content = new ArrayList<>()
    Throwable lastThrown ;
    volatile SynchronousQueue synchronousQueue ;

    Production(ExecutionContext testContext) {
        this.testContext = testContext
    }

     synchronized void  set(Object obj) {
        content.clear()
        accumulate(obj)
    }

    def get() {
      return content.get(0)
    }

    Throwable getLastThrown() {
        return lastThrown
    }

    void setLastThrown(Throwable lastThrown) {
        this.lastThrown = lastThrown
    }

    def getAt(int index) {
        return content.get(index)
    }

    synchronized def accumulate(Object obj) {
        //TODO: should test for memory!
        if(synchronousQueue != null) {
            if(obj == null) {
                // you cannot put a null element in a synch queue
                synchronousQueue.put(TaggedObject.NULL_Object)
            } else {
                synchronousQueue.put(obj)
            }
        } else {
            content.add(obj)
        }
    }

    def stop() {
       if(synchronousQueue != null)  {
           synchronousQueue.put(POISON_PILL)
       }
    }

    def int size() {
        return content.size()
    }

    @Override
    Iterator<Object> iterator() {
        return content.iterator()
    }

    //todo: with Callable and return another Production
    class WithRun implements Callable , TopContext{

        Closure closure;

        WithRun(Closure closure) {
            this.closure = closure
            //closure.setDelegate(testContext)
        }

        @Override
        public Object call() {
            Object withObject;
            //create another PRoducttion and accumulate results
            while(POISON_PILL!= (withObject = synchronousQueue.take())) {
                Thread currentThread = Thread.currentThread()
                currentThread.setName("WithEach handling: " + withObject)
                try {
                    _with(withObject, closure)
                } catch (Throwable th) {
                    //todo  report script errors differently! (and , may be, bail out!)
                    //PackCst.CURLOG.severe(" withEach caught :", th)
                    //testContext._reportException(th, RawDiagnostic.TOOL_OR_CODE_ERROR)
                    PackCst.CURLOG.severe(" withEach [" + withObject + "]:  execution problem", th)
                    //TODO : break only if script error ?
                    if(th instanceof GroovyExceptionInterface) {
                        break;
                    }
                }
            }
        }
    }

    static ExecutorService executorService = Boolean.getBoolean("gruth.multithreaded") ?
            Executors.newCachedThreadPool():
            Executors.newSingleThreadExecutor() ;

     def _withEach (Closure closure) {
        synchronousQueue = new SynchronousQueue()
        Callable callable = new WithRun(closure)
         Future future = executorService.submit(callable)
         testContext.xx_xcuteTests(this)
         //should return the Callable
         if(future.done) {
             try {
                 return future.get()
             }catch (Throwable th) {
                 //TODO : see the diagnostic
                 //testContext._reportException(th, RawDiagnostic.TOOL_OR_CODE_ERROR)
                 PackCst.CURLOG.severe(" withEach " + testContext._report.key + ": result extraction problem", th)
                 return this
             }
         }
    }


}
