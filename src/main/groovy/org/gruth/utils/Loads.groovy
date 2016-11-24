package org.gruth.utils

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

/**
 *
 * @author bamade
 */
// Date: 16/03/15

class Loads {
    static ImportCustomizer importCustomizer = new ImportCustomizer()
    static {
        importCustomizer.addStarImports('org.gruth.utils.*')
        importCustomizer.addStarImports('org.gruth.script.*')
        importCustomizer.addStaticImport('org.gruth.utils.DefUtils', '_using')
    }

    static def loadEval(Class clazz, Binding env, String scriptName) {
        for (String fileType : ['.groovy', '.gru']) {
            if (scriptName.endsWith(fileType)) {
                scriptName = scriptName.substring(0, scriptName.lastIndexOf(fileType))
                scriptName = scriptName.replaceAll('\\.', '/')
                scriptName = scriptName + fileType
                break;
            } else {
                scriptName = scriptName.replaceAll('\\.', '/')
            }
        }

        if (!scriptName.startsWith('/')) {
            scriptName = '/' + scriptName
        }
        InputStream is = clazz.getResourceAsStream(scriptName)
        if (is != null) {
            Reader isReader = new InputStreamReader(is)
            CompilerConfiguration conf = new CompilerConfiguration()
            conf.addCompilationCustomizers(importCustomizer)
            GroovyShell gsh = new GroovyShell(env, conf)
            try {
                return gsh.evaluate(isReader)
            } catch (Throwable th) {
                PackCst.CURLOG.severe("could not evaluate $scriptName", th)
            }

        } else {
            PackCst.CURLOG.severe("not found resource $scriptName")
        }
    }

    static def loadEval(Class clazz, Binding env, Class classToPopulate) {
       String resourceName =
               String.format('_test%ss.groovy', classToPopulate.getCanonicalName())
        return loadEval(clazz, env, resourceName)

    }
}
