package org.gruth.utils.logging;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * @author bamade
 */
// Date: 23/02/15


/**
 * Just a wrapper class around java.util.Logger to add specific logging methods.
 * The instances are not cached (so better have only one per package)
 * <BR/>
 * Most methods allow "multi-dimensional" logging (log the same data to various
 * loggers).
 * <BR/>
 * Many of those are intended to replace log4j invocations by just changing the
 * <TT>import</TT> directives. The mapping from log4J Levels is operated this
 * way
 * <PRE>
 * finest -> finest
 * trace -> finer
 * debug-> fine
 * info -> info
 * warn -> warning
 * error -> severe
 * fatal -> severe
 * </PRE>
 * <BR/>
 * Logging methods that return a boolean can be used through <TT>assert</TT>
 * calls (they always return true).
 * <p>
 * Do not use this <TT>Logger</TT> for admin purpose (setting a level, a Filter,
 * a Formatter or a Handler): use the corresponding
 * <TT>java.util.logging.Logger</TT> instead.
 *
 * @author bamade
 */
public class GruLogger {
    public static final GruLogger SYNTAX = GruLogger.getLogger("org.gruth.SYNTAX", "org.gruth.messages") ;

    /**
     * the real JUL logger for which we delegate log4J lookalike methods
     * and all our logs.
     */
    private java.util.logging.Logger julDelegate;

    /**
     * scheduler for delayed logs
     */
    protected ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread res = new Thread(r, "LoggerDelayedExecutor");
                    res.setDaemon(true);
                    return res;
                }
            });

    /**
     * private constructor: we need a JUL delegate to deliver our own Logger.
     * see factory methods to obtain a Logger.
     *
     * @param delegate
     */
    private GruLogger(java.util.logging.Logger delegate) {
        this.julDelegate = delegate;
    }

    /**
     * factory method to obtain a <TT>Logger</TT> proxy.
     *
     * @param name usually a package name (top of hierarchy is "" empty
     *             String)
     * @return
     */
    public static GruLogger getLogger(String name) {
        java.util.logging.Logger realLogger = java.util.logging.Logger.getLogger(name);
        return new GruLogger(realLogger);
    }

    /**
     * factory method to obtain a <TT>Logger</TT> proxy.
     *
     * @param name               usually a package name (top of hierarchy is "" empty
     *                           String)
     * @param resourceBundleName
     * @return
     */
    public static GruLogger getLogger(String name, String resourceBundleName) {
        java.util.logging.Logger realLogger = java.util.logging.Logger.getLogger(name, resourceBundleName);
        return new GruLogger(realLogger);
    }

    /**
     * @return the name of the Logger
     */
    public String getName() {
        return julDelegate.getName();
    }

    /**
     * @return the level of the associated JUL logger
     */
    public Level getLevel() {
        return julDelegate.getLevel();
    }

    public void setLevel(Level level) {
        julDelegate.setLevel(level);
    }


    /**
     * creates a simple log record. This factory manipulates the caller
     * information (className, method Name) by getting rid of any
     * information coming from a package that contains "logging" (or
     * "log4j").
     * <BR/>
     * note that the LogRecord is automatically marked with a sequenceNumber
     * that can be used by <TT>Handlers</TT>
     * that want to detect duplicate publications.
     *
     * @param level   JUL Level
     * @param message (avoid null values)
     * @return a simple LogRecord
     */
    public LogRecord createLogRecord(Level level, String message) {
        LogRecord res = new LogRecord(level, message);
        //now modifies the method and class calls
        //Throwable throwable = new Throwable();
        //StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            String className = stackTraceElement.getClassName();
            if (!className.startsWith("org.lsst.ccs.utilities.logging")) {
                if (!className.startsWith("org.apache.log4j") && !className.contains(".logging.")) {
                    String methodName = stackTraceElement.getMethodName();
                    if (!"getStackTrace".equals(methodName)) {
                        res.setSourceClassName(className);
                        res.setSourceMethodName(methodName);
                        break;
                    }
                }
            }
        }

        return res;
    }

    /* LOG4J compatible calls
     finest -> finest
     trace -> finer
     debug-> fine
     info -> info
     warn -> warning
     error -> severe
     fatal -> severe
     isDebugEnabled
     */

    /**
     * utility method to log a message with a Level
     *
     * @param level    JUL level
     * @param message  (avoid null values)
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    protected boolean logMessage(Level level, Object message, GruLogger... concerns) {
        String msg = String.valueOf(message);
        LogRecord record = createLogRecord(level, msg);
        this.log(record, concerns);
        return true;
    }

    /**
     * logs a message at FINEST level
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean finest(Object message, GruLogger... concerns) {
        return logMessage(Level.FINEST, message, concerns);
    }

    /**
     * logs a message at FINER level
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean trace(Object message, GruLogger... concerns) {
        return logMessage(Level.FINER, message, concerns);
    }

    /**
     * logs a message at FINER level
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean finer(Object message, GruLogger... concerns) {
        return logMessage(Level.FINER, message, concerns);
    }

    /**
     * logs a message at FINE Level
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean debug(Object message, GruLogger... concerns) {
        return logMessage(Level.FINE, message, concerns);
    }

    /**
     * logs a message at FINE Level
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean fine(Object message, GruLogger... concerns) {
        return logMessage(Level.FINE, message, concerns);
    }

    /**
     * logs a message at INFO Level
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean info(Object message, GruLogger... concerns) {
        return logMessage(Level.INFO, message, concerns);
    }

    /**
     * logs a message at WARNING Level
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean warn(Object message, GruLogger... concerns) {
        return logMessage(Level.WARNING, message, concerns);
    }

    /**
     * logs a message at WARNING Level
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean warning(Object message, GruLogger... concerns) {
        return logMessage(Level.WARNING, message, concerns);
    }

    /**
     * logs a message at SEVERE Level (use preferably the error method that
     * uses a <TT>Throwable</TT> parameter)
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     */
    public void error(Object message, GruLogger... concerns) {
        logMessage(Level.SEVERE, message, concerns);
    }

    /**
     * logs a message at SEVERE Level (use preferably the fatal method that
     * uses a <TT>Throwable</TT> parameter)
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     */
    public void fatal(Object message, GruLogger... concerns) {
        logMessage(Level.SEVERE, message, concerns);
    }

    /**
     * logs a message at SEVERE Level (use preferably the severe method that
     * uses a <TT>Throwable</TT> parameter)
     *
     * @param message
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     */
    public void severe(Object message, GruLogger... concerns) {
        logMessage(Level.SEVERE, message, concerns);
    }

    /**
     * tells if the FINE level is activated for the corresponding JUL
     * Logger. (to be used if a subsequent logging call is costly)
     *
     * @return
     */
    public boolean isDebugEnabled() {
        return julDelegate.isLoggable(Level.FINE);
    }

    /**
     * tells if the INFO level is activated for the corresponding JUL
     * Logger. (to be used if a subsequent logging call is costly)
     *
     * @return
     */
    public boolean isInfoEnabled() {
        return julDelegate.isLoggable(Level.INFO);
    }

    /**
     * utility method to forward a <TT>Throwable</TT> and a message to the
     * loggers.
     *
     * @param level     JUL level
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     */
    protected void logSimpleThrowable(Level level, Object message, Throwable throwable, GruLogger... concerns) {
        String msg = String.valueOf(message);
        LogRecord record = createLogRecord(level, msg);
        record.setThrown(throwable);
        this.log(record, concerns);
    }

    /**
     * logs a message at SEVERE Level
     *
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     */
    public void fatal(Object message, Throwable throwable, GruLogger... concerns) {
        logSimpleThrowable(Level.SEVERE, message, throwable, concerns);
    }

    /**
     * logs a message at SEVERE Level
     *
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     */
    public void severe(Object message, Throwable throwable, GruLogger... concerns) {
        logSimpleThrowable(Level.SEVERE, message, throwable, concerns);
    }

    /**
     * logs a message at SEVERE Level
     *
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     */
    public void error(Object message, Throwable throwable, GruLogger... concerns) {
        logSimpleThrowable(Level.SEVERE, message, throwable, concerns);
    }

    /**
     * logs a message at WARNING Level
     *
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     */
    public void warn(Object message, Throwable throwable, GruLogger... concerns) {
        logSimpleThrowable(Level.WARNING, message, throwable, concerns);
    }

    /**
     * logs a message at WARNING Level
     *
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     */
    public void warning(Object message, Throwable throwable, GruLogger... concerns) {
        logSimpleThrowable(Level.WARNING, message, throwable, concerns);
    }


    /**
     * logs a message at INFO Level
     *
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     */
    public void info(Object message, Throwable throwable, GruLogger... concerns) {
        logSimpleThrowable(Level.INFO, message, throwable, concerns);
    }

    /**
     * logs a message at FINE Level
     *
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     */
    public void debug(Object message, Throwable throwable, GruLogger... concerns) {
        logSimpleThrowable(Level.FINE, message, throwable, concerns);
    }


    /**
     * tells if the corresponding JUL level is activated for the
     * corresponding JUL Logger. (to be used if a subsequent logging call is
     * costly)
     *
     * @return
     */
    public boolean isLoggable(Level level) {
        return julDelegate.isLoggable(level);
    }

    /**
     * A utility to plug into Handlers that want to detect duplicate logs.
     * it keeps a list of the 32 last logs and return false to the <TT>isLoggable</TT> method
     * if a <TT>LogRecord</TT> with the same sequence number has been processed.
     *
     * @author bamade
     */
// Date: 29/05/13

    public static class IsLoggableDelegate {
        private static final int MAX_ENTRIES = 32;
        private LinkedHashMap<Long, String> mapRecords =
                // the size of the Map is the nearest prime number more than MAX_ENTRIES
                new LinkedHashMap<Long, String>(37) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry eldest) {
                        return size() > MAX_ENTRIES;
                    }
                };

        // if the logRecord is already in the map the put does not return null
        public boolean isLoggable(LogRecord logRecord) {
            String logger = logRecord.getLoggerName();
            long seqNumber = logRecord.getSequenceNumber();
            return null == mapRecords.put(seqNumber, logger);
        }
    }

    static Map<Handler, IsLoggableDelegate> mapHandlers = new HashMap<>();

    /**
     * utility method to send a <TT>LogRecord</TT> to the corresponding JUL
     * logger and to a list of other loggers.
     *
     * @param record   (should be created with the <TT>createLogRecord</TT>
     *                 factory :otherwise stack information will be wrong)
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     */
    public void log(LogRecord record, GruLogger... concerns) {
        record.setLoggerName(julDelegate.getName());
        //duplicates the Log of Logger!!!
        /////////////////////////////
        if (!julDelegate.isLoggable(record.getLevel())) {
            return;
        }
        Filter theFilter = julDelegate.getFilter();
        if (theFilter != null && !theFilter.isLoggable(record)) {
            return;
        }

        // Post the LogRecord to all our Handlers, and then to
        // our parents' handlers, all the way up the tree.

        Logger logger = julDelegate;
        while (logger != null) {
            for (Handler handler : logger.getHandlers()) {
                IsLoggableDelegate delegate = mapHandlers.get(handler);
                if (delegate == null) {
                    delegate = new IsLoggableDelegate();
                    mapHandlers.put(handler, delegate);
                }
                if (delegate.isLoggable(record)) {
                    handler.publish(record);
                }
            }

            if (!logger.getUseParentHandlers()) {
                break;
            }

            logger = logger.getParent();
        }

        /////////////////////////
        for (GruLogger concern : concerns) {
            concern.log(record);
        }
    }

    /**
     * gets the first Handler in the hierarchy which is assignable to the class
     * @param handlerClass
     * @return
     */
    public Optional<Handler> getFirstHandler(Class handlerClass) {
        Handler res = null ;
        Logger logger = julDelegate;
        while (logger != null) {
            for (Handler handler : logger.getHandlers()) {
                Class currentClass = handler.getClass() ;
                if(handlerClass.isAssignableFrom(currentClass)) {
                    res = handler;
                    break ;
                }
            }
            logger= logger.getParent();
        }
        return Optional.ofNullable(res) ;
    }

    /**
     * sets the level of this Logger and of the requested Handler
     * @param level
     * @param handlerClass
     */
    public void coordLevelSettings(Level level, Class handlerClass) {
        this.setLevel(level);
        Optional<Handler> optHandler = this.getFirstHandler(handlerClass) ;
        if(optHandler.isPresent()) {
            optHandler.get().setLevel(level) ;
        }
    }

    /**
     * same method as <TT>log(LogRecord, String... concerns</TT>
     * but executed in a different Thread.
     * <BR/>
     * this might be interesting when the Thread that logs should be
     * different from the Thread that handles the LogRecord
     *
     * @param delay    in millis
     * @param record
     * @param concerns
     */
    public void decoupledLog(long delay, final LogRecord record, final GruLogger... concerns) {
        executor.schedule(new Runnable() {
            public void run() {
                log(record, concerns);
            }
        }, delay, TimeUnit.MILLISECONDS);

    }

    /**
     * does the same as the equivalent standard JUL method but forwards also
     * to other Loggers.
     *
     * @param level    JUL level
     * @param message
     * @param argument
     * @param concerns a list of additional gruth Loggers  (such as
     *                 "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean log(Level level, String message, Object argument, GruLogger... concerns) {
        LogRecord record = createLogRecord(level, message);
        record.setParameters(new Object[]{argument});
        this.log(record, concerns);
        return true;
    }

    /**
     * same method as <TT>public boolean log(Level level, String message,
     * Object argument, String... concerns)</TT>
     * but executed in a different Thread.
     * <BR/>
     * this might be interesting when the Thread that logs should be
     * different from the Thread that handles the LogRecord
     *
     * @param delay    in Milliseconds
     * @param level
     * @param message
     * @param argument
     * @param concerns
     * @return
     */
    public boolean decoupledLog(long delay, Level level, String message, Object argument, GruLogger... concerns) {
        LogRecord record = createLogRecord(level, message);
        record.setParameters(new Object[]{argument});
        this.decoupledLog(delay, record, concerns);
        return true;
    }

    /**
     * does the same as the equivalent standard JUL method but forwards also
     * to other Loggers.
     *
     * @param level     JUL level
     * @param message
     * @param arguments
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     * @return true
     */
    public boolean log(Level level, String message, Object[] arguments, GruLogger... concerns) {
        LogRecord record = createLogRecord(level, message);
        record.setParameters(arguments);
        this.log(record, concerns);
        return true;
    }

    /**
     * does the same as the corresponding <TT>log</TT> method but executed
     * in a different Thread.
     * <BR/>
     * this might be interesting when the Thread that logs should be
     * different from the Thread that handles the LogRecord
     *
     * @param delay     in millis
     * @param level
     * @param message
     * @param arguments
     * @param concerns
     * @return
     */
    public boolean decoupledLog(long delay, Level level, String message, Object[] arguments, GruLogger... concerns) {
        LogRecord record = createLogRecord(level, message);
        record.setParameters(arguments);
        this.decoupledLog(delay, record, concerns);
        return true;
    }

    /**
     * does the same as the equivalent standard JUL method but forwards also
     * to other Loggers.
     *
     * @param level     JUL level
     * @param message
     * @param throwable
     * @param concerns  a list of additional gruth Loggers  (such as
     *                  "INIT", "CONFIG" ,...)
     * @return true
     */
    public void log(Level level, String message, Throwable throwable, GruLogger... concerns) {
        LogRecord record = createLogRecord(level, message);
        record.setThrown(throwable);
        this.log(record, concerns);
    }

    /**
     * does the same as the corresponding <TT>log</TT> method but executed
     * in a different Thread.
     * <BR/>
     * this might be interesting when the Thread that logs should be
     * different from the Thread that handles the LogRecord
     *
     * @param delay     in millis
     * @param level
     * @param message
     * @param throwable
     * @param concerns
     */
    public void decoupledLog(long delay, Level level, String message, Throwable throwable, GruLogger... concerns) {
        LogRecord record = createLogRecord(level, message);
        record.setThrown(throwable);
        this.decoupledLog(delay, record, concerns);
    }


}
