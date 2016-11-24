package org.gruth
import org.codehaus.groovy.control.CompilerConfiguration
import org.gruth.reports.TztReport
import org.gruth.utils.Loads
import org.gruth.utils.logging.GruLogger
/**
 *
 * @author bamade
 */
// Date: 23/02/15

GruLogger logger = GruLogger.getLogger('org.gruth', 'org.gruth.messages')
if (!args) {
    logger.error('usage : gru -i | -e scriptString | fileOrResourceName [arguments]')
    return
}
Reader isReader = null
String arg1 = args[0]
String[] argz = new String[0]
String scriptName

switch (arg1) {
    case '-i': case '-I':
        isReader = new InputStreamReader(System.in)
        scriptName = 'in'
        if (args.length > 1) {
            argz = Arrays.copyOfRange(args, 1, args.length)
        }
        break
    case '-e': case '-E':
        if (args.length < 2) {
            logger.error('usage :  -e scriptString  [arguments]')
            return
        }
        isReader = new StringReader(args[1])
        scriptName = 'eval'
        if (args.length > 2) {
            argz = Arrays.copyOfRange(args, 2, args.length)
        }
        break
    default:
        String str = arg1
        if (args.length > 1) {
            argz = Arrays.copyOfRange(args, 1, args.length)
        }
//ClassLoader cloader = ClassLoader.getSystemClassLoader()
//  check if not ".gru" already
        if (str.endsWith('.gru')) {
            str = str.subSequence(0, str.lastIndexOf('.'))
        }
        // transforms it to an URL ending with .gru
        str = '/' + str.replace('.' as char, '/' as char)
        int lastIndex = str.lastIndexOf('/')
        scriptName = str.substring(lastIndex + 1)
        str = str + '.gru'
        //println str
        InputStream is = this.class.getResourceAsStream(str)
        //is = org.lsst.gruth.grutils.TaggedsMap.class.getResourceAsStream(str)

        //is = cloader.getResourceAsStream(str)
        if (is == null) { // then reads a file
            try {
                File file = new File(arg1)
                if (!file.exists()) {
                    file = new File(arg1 + ".gru")
                }
                is = new FileInputStream(file)

            } catch (IOException exc) {
                //was documented as possible!!!! (implicit log)
                //log.severe ( "$str or $it not found in class path" )
                logger.error("$str not found in class path or file path : " + exc)
                return

            }

        }
        isReader = new InputStreamReader(is)
}

if (isReader != null) {
    def conf = new CompilerConfiguration()
    conf.addCompilationCustomizers(Loads.importCustomizer)
    Binding env = new Binding()
    //println "ENV gru :$env"
    LinkedHashMap terminators = new LinkedHashMap<String, Closure>()
    //env.setVariable("terminators", terminators)
    env.setProperty("terminators", terminators)
    //println " ENV1 " + env.getVariables()
    conf.setScriptBaseClass("org.gruth.script.GruScript")
    def shell = new GroovyShell(env,conf)
    //env = shell.getContext()
    //println " ENV2 " + env.getVariables()
    /*
    InputStream endIS = new ByteArrayInputStream('\n_runAll() ; \n'.getBytes());
    InputStream allIs = new SequenceInputStream(is, endIS)
    // new version deprecated evaluate inputStream
    Reader reader = new InputStreamReader(allIs)
    */
    boolean debug = Boolean.getBoolean('gruth.dumpSource')
    if (debug) {
        BufferedReader bufr = new BufferedReader(isReader)
        String line
        while (null != (line = bufr.readLine())) {
            println line
        }
    }
    System.setProperty("script.name", scriptName)
    shell.setVariable('_scriptName', scriptName)
    shell.setProperty('_scriptName', scriptName)
    try {
        /* doe snot work
        Script script = shell.parse(isReader)
        script.setBinding(env)
        script.run()
        */
        Object res = shell.run(isReader, scriptName, argz)
        //Object res = shell.run(isReader, "groovy.lang.GroovyShell", argz)

    } catch(Throwable th) {
        TztReport.scriptErrors++
        logger.severe("script error :", th)
    } finally {
        //shell.
       for(Closure clos : terminators) {
           try {
               clos()
           } catch (Throwable th) {
               logger.severe("shutdown hook error :", th)
           }
       }
    }
    //shell.evaluate('_runAll()')
}
