package org.gruth.script
import org.gruth.reports.ResultHandlers
import org.gruth.reports.TztReport
import org.gruth.script.PackCst
import org.gruth.utils.*
import org.gruth.utils.logging.GruLogger
/**
 *
 * @author bamade
 */
// Date: 24/02/15

abstract class GruScript extends Script implements TopContext {

    static Binding _globalEnv = new Binding()
    //todo: bad practice suppose there is only one script running in the same JVM
    // change!
    static Binding _thisScript ;
    static {
        _globalEnv.stopAll = false
        String separator = System.getProperty("path.separator")
        String scriptsName = System.getProperty("gruth.metaScripts");
        if (scriptsName != null) {
            for (String scriptName : scriptsName.split(separator)) {
                Loads.loadEval(GruScript, _globalEnv, scriptName)
            }
        }
    }


    /**
     *  the calling environment
     *  (not used for the moment)
     */
    Binding _env


    def _kv = TaggedObject.&tag

    def _kvList = TaggedsList.&fromList

    def _kvMap = TaggedsList.&fromMap

    def _kvObjs = TaggedsList.metaClass.&invokeConstructor

    def _await = SimpleTestDataProvider.&factory

    def _combine = SimpleTestDataProvider.&combination

    def _testData = TestDataList.&factory

    def _OK = {}

    def _NULL = TaggedObject.NULL_Object
    def _EMPTY = TestDataProvider.EMPTY_PROVIDER;

    def _load (String resource) {
        Loads.loadEval(this.getClass(), _thisScript, resource)
    }

    def _loadValues (Class clazz) {
        Loads.loadEval(this.getClass(), _thisScript, clazz)
    }

    GruScript() {
        super()
        _env = getBinding()
        initThis()
    }

    GruScript(Binding binding) {
        super(binding)
        //TODO : fix!
        _env = binding
        initThis()
    }

    private void initThis() {
        _thisScript = new Binding()
        _thisScript.stopScript = false
        _thisScript.skipTests = false
        //println " ENV " + _env
        //TODO: get the _scriptName name
        def scriptName = System.getProperty("script.name")
        //println _scriptName
        _defaultBundle(scriptName)
    }


    def methodMissing(String name, args) {
        PackCst.CURLOG.error("$name [$args] : method missing\n"
                + "consider using\n"
                + "_withClass class_Or_ClassName {/*closure*/}\n"
                + "_withObjects object {/*closure*/}\n", GruLogger.SYNTAX);

    }

    /**
     * Helps resultReporters to determin a default bundle name for reports
     * (example: a base name for files containing the report)
     * @param name
     * @return
     */
    def _defaultBundle(String name) {
        ResultHandlers.reportBundleName = name
    }

    def _vars(Map map) {
        TaggedsList res = TaggedsList.fromMap(map)
        res.each {
            String key = it.key
            this.metaClass."$key" = it
        }
        return res;
    }


    def _vars(List list) {
        TaggedsList res = TaggedsList.fromList(list)
        _vars (res)
        return res;
    }

    def _vars(TaggedsProvider tList) {
        tList.each {
            String key = it.key
            this.metaClass."$key" = it
        }
        return tList
    }

    def _var(Class clazz, String name, Object... args) {
        Closure clos = clazz.metaClass.&invokeConstructor
        if(args == null){
            args = [null] as Object[]
        }
        Object res = clos(args)
        //TODO set Variable
        this.metaClass."$name" = res
        return res

    }

    def _var(String name, Object val){
        this.metaClass."$name" = val
        return val
    }

    def _var (TaggedObject taggedObject) {
        String key = taggedObject.key
        this.metaClass."$key" = it
        return taggedObject
    }

    def _setID(String key, Object object) {
        if(object == null) {
            //TODO: log warning
            return null
        }
        object.metaClass._gruID = key
        return object
    }

    //Todo modify
    def _autoID(Closure clos) {
        return {
            Object obj = clos.call()
            _setID(delegate, String.valueOf(obj))
        }
    }

    def _getID(Object object) {
        if(object == null) return null
         def res = object.metaClass.getProperty(object, '_gruID')
       return res
    }

    def _ctorOf(Class clazz) {
        return clazz.metaClass.&invokeConstructor
    }

    def _addHook(String name, Closure closure) {
        _env.terminators.put(name, closure)
    }

    def _removeHook(String name) {
        _env.terminators.remove(name)
    }

    def _executeHook(String name) {
        Closure clos = _env.terminators.remove(name)
        clos()
    }

    def _issueReport(Map map) {
        TztReport report = map
        //todo: check if essential fields are set!
        ResultHandlers.handleReport(ResultHandlers.reportBundleName, report)
        PackCst.CURLOG.info( "$report.testName $report.data" )
    }
}
