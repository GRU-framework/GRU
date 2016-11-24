package org.gruth.utils

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Used to trace execution of gruth.
 * Many errors when writing a test or a node tree are just
 * fairly hard to find: use the tracer to understand what is wrong in
 * the interpretation of your code.
 * <P>
 *  Gru internal code should use <TT>Tracer.trace</TT> at critical points.
 *  <P/>
 *  Gru users should set a property (or environment variable)
 *  with name 'gruth.traces' the value is a comma separated list
 *  of <TT>Tracer</TT> values.
 *  <P/>
 *  The system property "gruth.traceHandler" is used to specify the canonical
 *  name of a class to be used as a <TT>Tracer.Handler</TT> that may replace the standard
 *  Handler
 *
 * @author bamade
 */

public enum Tracer {

    /**
    * when building a Node
     */
    NODE_BUILD,
    /**
    *  node evaluation
    */
    NODE_EVAL,
    /**
    *  modification of a Node
     */
    NODE_MODIF,
    /**
    * "calls" block operations
     */
    NODE_CALLS,
    /**
    * syntax of DSLs
     */
    SYNTAX,
    /**
    * test building
     */
    TEST_BUILD,
    /**
    * evaluation of test
     */
    TEST_EVAL,
    /**
     * set up operations
     */
    PRE,
    /**
    * tear down operations
     */
    POST,
    /**
    * expectations evaluation
     */
    XPECT;

    /**
     * each enum member has a corresponding bit in a bit pattern.
     * this bit is build according to the ordinal of the value.
     *
     */
    int bitPosition;

    /**
     * according to the value of 'gruth.traces' this mask is use
     * to pick up the corresponding calls to <TT>trace</TT>
     */
    static final int traceMask ;
    /**
     * A Handler manages the trace reports
     */
    static interface Handler {
        /**
         *
         * @param code the code of the trace (negative number means no code)
         * @param tracer the enum value used to  specify the context of the trace
         * @param trace a message
         * @return an int that may be interpreted by the invoking code as a request to stop operating (if the value is negative)
         */
      int show(int code, Tracer tracer, String trace)  ;
    }

    /**
     * default Handler that logs messages to JUL loggers
     */
    static class LogHandler implements Handler {
         Logger logger =  Logger.getLogger("org.lsst.gruth")
        @Override
        int show(int code, Tracer tracer, String trace) {
            Level level = logger.getLevel()
            if(level != null && level.compareTo(Level.INFO) <=0 ) {
                logger.log(level, tracer + ": " + trace)
            }
            return 0 ;
        }
    }

    /**
     * the trace handler
     */
    static Handler traceHandler = new LogHandler();

    /**
     * the current traceHandler
     * @return
     */
    static Handler getTraceHandler() {
        return traceHandler
    }

    /**
     * modifies the default traceHandler
     * @param traceHandler
     */
    static void setTraceHandler(Handler traceHandler) {
        Tracer.traceHandler = traceHandler
    }

    static {
        int mask =0 ;
        String traceSpecs = System.getenv('gruth.traces')
        if(traceSpecs == null) {
            traceSpecs = System.getProperty('gruth.traces')
        }
        if(traceSpecs!= null) {
            String[] specs = traceSpecs.split(',')
            for(String spec: specs) {
                Tracer tracer = Tracer.valueOf(spec)
                //todo OR equals
                mask |=  tracer.bitPosition
            }
        }
        traceMask = mask ;
        String traceHandlerClassName  = System.getenv('gruth.traceHandler')
        if(traceHandlerClassName == null) {
            traceHandlerClassName = System.getProperty('gruth.traceHandler')
        }
        if( traceHandlerClassName != null ){
            try {
                Handler handler = Class.forName(traceHandlerClassName).newInstance()
                setTraceHandler(handler)
            } catch (Exception exc) {
                System.err.println(" handler " + traceHandlerClassName + " : " + exc)
            }
        }
    }

    /**
     * constructor used to set up bit position that enables the specification of active
     * trace domains.
     */
    Tracer()  {
       bitPosition = 1 << this.ordinal()
    }


    static int trace (Tracer tracer, Object... args) {
        return trace(-1, tracer, args)
    }

    static int trace (Tracer tracer, int code ,Object... args) {
        if( traceMask & tracer.bitPosition) {
            StringBuilder builder = new StringBuilder()
           for(Object obj : args) {
               if(obj == null ){
                   builder.append(" %no trace (null value)% ")
               }
               try {
                    builder.append(" $obj")
               } catch (Exception exc) {
                   builder.append(" %no trace (null value in toString() method)% ")
               }
           }
            //TODO: use code number to get additional message

            return traceHandler.show(code, tracer,builder.toString())
        }

    }

}