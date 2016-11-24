package org.gruth.gen

import java.lang.reflect.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This class helps build a ".gruModel" file: a test template for unit tests for a given class.
 * The generated code should contain
 * <UL>
 *     <LI> a proposition of package (the same as the current class? might be a problem for "sealed" codes)
 *     <LI> comment: a proposition of "include" code (_load or property)
 *     <LI> comment : a reminder about hooks
 * </UL>
 * Then we should test if the class has constructors or not. (if no constructor just a template for static code
 * should be generated). If the class has more than one constructor. Then numbering schmed is activated
 * and the script will define a method that will use this number as argument.
 * <BR>
 *  this method will return a Closure which define:
 *  <UL>
 *      <LI> a _state block
 *      <LI> a _method group with an  entry for each method (note that overloaded method get a different
 *      entry). for each method a data structure is defined which describe the argument types
 *      this data structure will be used to generate a TestDataList that will be written before the
 *      overall Closure definition.
 *      <BR>
 *       methods will be explored up the hierarchy, up to a given pacakge (special case for default methods?)
 *      </UL>
 * @author bamade
 */
// Date: 25/03/15

//TODO: rewrite generation (clumsy code)
//TODO: beware factories/constructor that throw Exceptions not well generated

class UnitTestsGen1 {
    class CodeAndArgs implements Cloneable{
        String codeName;
        boolean isVarArgs ;
        boolean isFactory ;
        String testName ;
        int index; // used in case of overloading
        int num =0 ;
        int catchIndex = -1; // used to generate different case for exception
        Class<? extends Throwable> exceptionType;
        ArrayList<String> argTypesAndName = new ArrayList<String>();
        String resultTypeName

        CodeAndArgs(String methodName) {
            this.codeName = methodName;
        }

        public CodeAndArgs clone() {
           CodeAndArgs res = super.clone()
           res.exceptionType = null
           return res
        }
    }
    ArrayList<Class> superClasses = new ArrayList<>()
    TreeMap<String, List<CodeAndArgs>> mapInstanceMethodDefinition = new TreeMap<>()
    TreeMap<String, List<CodeAndArgs>> mapStaticMethodDefinition = new TreeMap<>()
    ArrayList<CodeAndArgs> methodsDefinitions = new ArrayList<>()
    ArrayList<CodeAndArgs> ctorDefinitions = new ArrayList<>()
    BufferedWriter bufw ;

    UnitTestsGen1(String classCanonicalName ){
        this(classCanonicalName, 'java')
    }

    UnitTestsGen1(String classCanonicalName, String topPackage){
        Class clazz = Class.forName(classCanonicalName)
        Class superClazz = clazz;
        superClasses.add(clazz)
        while(null != (superClazz = superClazz.getSuperclass())) {
            if(! superClazz.getPackage().getName().startsWith(topPackage)) {
                superClasses.add(superClazz)
            } else {
                break
            }
        }
        Constructor[] constructors = clazz.getDeclaredConstructors()
        if( constructors.length == 0 ){
           generateStaticCode(clazz)
        } else {
            // todo: use all methods (not only the public ones!)
            Method[] methods = clazz.getMethods()
            for(Method method: methods) {
                Class declaringClass = method.getDeclaringClass()
                if(superClasses.contains(declaringClass)) {
                    String name = method.getName()
                    CodeAndArgs codeAndArgs = new CodeAndArgs(name)
                    Parameter[] parameters = method.getParameters()
                    for(Parameter parameter : parameters) {
                        //todo
                        codeAndArgs.argTypesAndName .add(parmString(parameter))
                    }
                    codeAndArgs.isVarArgs = method.isVarArgs()
                    codeAndArgs.resultTypeName = method.getReturnType().getCanonicalName()
                    // see if static
                    if(Modifier.isStatic(method.getModifiers())) {
                        Class clazzResult = method.getReturnType()
                        if(clazz.equals(clazzResult)) {
                            codeAndArgs.isFactory = true
                        }
                        updateMap(mapStaticMethodDefinition, codeAndArgs, name, null)
                    } else {
                        updateMap(mapInstanceMethodDefinition, codeAndArgs, name, null)
                    }
                    //codeAndArgs.testName = transformName(name)
                    //methodsDefinitions.add(codeAndArgs)
                    Class<? extends Throwable>[] excs = method.getExceptionTypes()
                    int excCount = 0
                    for(Class excClass : excs) {
                        CodeAndArgs excCodeAndArgs = codeAndArgs.clone()
                        excCodeAndArgs.catchIndex = excCount ++ ;
                        excCodeAndArgs.exceptionType = excClass ;
                        //excCodeAndArgs.testName = codeAndArgs.testName+'_'+(excClass.getSimpleName()).toUpperCase()
                        String excName = excClass.getSimpleName().replace('Exception','')
                        if(Modifier.isStatic(method.getModifiers())) {
                            updateMap(mapStaticMethodDefinition, excCodeAndArgs, name, excName)
                        } else {
                            updateMap(mapInstanceMethodDefinition, excCodeAndArgs, name, excName)
                        }
                        //methodsDefinitions.add(excCodeAndArgs)
                    }

                    //todo : change name for methods that are overloaded?
                }
            }

            int numCtor = 0 ;
            for (Constructor constructor: constructors) {
                CodeAndArgs codeAndArgs = new CodeAndArgs('<init>')
                Parameter[] parameters = constructor.getParameters()
                for(Parameter parameter : parameters) {
                    codeAndArgs.argTypesAndName .add(parmString(parameter))
                }
                codeAndArgs.testName = "CTOR$numCtor"
                codeAndArgs.isVarArgs = constructor.isVarArgs()
                codeAndArgs.resultTypeName = classCanonicalName
                codeAndArgs.num = numCtor
                numCtor++ ;
                ctorDefinitions.add(codeAndArgs)
                Class<? extends Throwable>[] excs =   constructor.getExceptionTypes()
                int excCount = 0
                for(Class excClass : excs) {
                    CodeAndArgs excCodeAndArgs = codeAndArgs.clone()
                    excCodeAndArgs.catchIndex = excCount ++ ;
                    excCodeAndArgs.exceptionType = excClass ;
                    String excName = excClass.getSimpleName().replace('Exception','')
                    excCodeAndArgs.testName = codeAndArgs.testName+'_'+excName
                    ctorDefinitions.add(excCodeAndArgs)
                }
            }
            initFile(clazz)
            genHeader(clazz)
            genVars()
            genMethodsClosure(constructors.length > 0)
            genCtor(clazz)
            endFile()

        }
    }

    def generateStaticCode(clazz) {

    }

    def initFile(Class clazz) {
        //get simpleName
        String simpleName = clazz.getSimpleName()
        // generates "UTest_$simpleName"
        String fileName = "uTest_$simpleName" + '.gruModel'
        // initializes a writer for the File
        bufw = Files.newBufferedWriter(Paths.get(fileName))

    }

    def endFile() {
        bufw.flush()
        bufw.close()
    }

    def genHeader(Class clazz) {
        //declares same package as class
        String packName = clazz.getPackage().getName()
        showString("package $packName \n")
        showString ('// IMPORTS\n')
        showString ('// LOADING GROOVY RESOURCES\n')
        showString('// _loadEval someClass ')
        showString ('// OTHER INITIALIZATIONS  (system properties, logging, hooks, application context, ...) \n')

    }

    def genVars() {

    }

    def genArgs(String indent, ArrayList<CodeAndArgs> listArgs) {
        for(CodeAndArgs codeAndArgs : listArgs) {
            String argsShow = String.valueOf(codeAndArgs.argTypesAndName)
            String dataName = codeAndArgs.testName+'_DATA'
            String resultName = codeAndArgs.resultTypeName
            if(codeAndArgs.exceptionType == null) {
                showString("$indent TestDataList $dataName = [")
                if (codeAndArgs.argTypesAndName.size() > 0) {
                    argsShow = argsShow.substring(1, argsShow.length() - 1)
                    showString("\t// _await(closure, $argsShow) //, ")
                } else {
                    showString("\t// _await(closure) //, ")
                }
                showString("$indent ]")
            } else {
                if (codeAndArgs.argTypesAndName.size() > 0) {
                    argsShow = argsShow.substring(1, argsShow.length() - 1)
                    showString("$indent def $dataName = _EMPTY // should be: = _combine($argsShow) ")

                } else {
                    //nothing?

                }

            }
        }

    }

    def updateMap(TreeMap<String, List<CodeAndArgs>> treeMap, CodeAndArgs codeAndArgs, String name, String
            additionalName) {
        List<CodeAndArgs> list = treeMap.get(name)
        String upperName = transformName(name)
        if(list == null) {
            if(additionalName == null) {
                codeAndArgs.testName = upperName
            } else {
                codeAndArgs.testName = upperName+'_'+additionalName
            }
            ArrayList<CodeAndArgs> listArgs = new ArrayList<>()
            listArgs.add(codeAndArgs)
            treeMap.put(name, listArgs)
        } else {
            int number = list.size()
            if(additionalName == null) {
                codeAndArgs.testName = upperName + number
            } else {
                codeAndArgs.testName = upperName + number + '_' + additionalName
            }
            list.add(codeAndArgs)
        }
    }

    def genMethodsClosure(boolean useNumbering) {
        String numberString = ''
        if(useNumbering) {
            numberString = '_$number'
        }

        showString('/////////////METHODS TEST DEFINITION')
        showString('// METHODS DEFS\n')
        if(useNumbering) {
            showString('Closure methodCodes( int number) {\n')
        } else {
            showString('Closure methodCodes() {\n')
        }

        showString('// METHODS ARGS\n')
        for(Map.Entry<String, ArrayList<CodeAndArgs>> entry : mapInstanceMethodDefinition) {
            genArgs('  ' ,entry.value)
        }
        showString('\treturn {\n')
        showString('\t\t_state "STATE$number" _xpect{\n\t\t}\n')
        genMethTests(false, mapInstanceMethodDefinition , numberString)

        showString("\t}\n}\n")

    }

    def genMethTests(boolean isStatic, Map<String,List<CodeAndArgs>> map, String numberString) {
        for(Map.Entry<String, ArrayList<CodeAndArgs>> entry : map) {
            if(isStatic) {
                showString('\t_classMethod (\'' + entry.key + '\') {\n')

            } else {
                showString('\t\t_method (\'' + entry.key + '\') {\n')
            }
            for (CodeAndArgs codeAndArgs : entry.value) {
                String resultTypeName = codeAndArgs.resultTypeName
                if (codeAndArgs.exceptionType == null) {
                    if (codeAndArgs.argTypesAndName.size() == 0) {
                        if(isStatic) {
                            if(codeAndArgs.isFactory) {
                                // todo : change number when overloading of factory (now -1 is arbitrary)
                                showString('\t\t_test (\'' + codeAndArgs.testName + "') _post {/*_tagResult?*/} _withEach methodCodes (-1)\n")

                            } else {
                                showString('\t\t_test (\'' + codeAndArgs.testName + numberString + '\') _xpect {}' + " // -> $resultTypeName\n")
                            }
                        } else {
                            showString('\t\t\t_test ("' + codeAndArgs.testName + numberString + '[$_key]") _xpect {}' + " // -> $resultTypeName\n")
                        }

                    } else {
                        if(isStatic) {
                            if(codeAndArgs.isFactory) {
                                // todo : change number when overloading of factory (now -1 is arbitrary)
                                        showString('\t\t_test (\'' + codeAndArgs.testName + numberString + '\' , ' + codeAndArgs.testName +
                                        "_DATA') _post {/*_tagResult?*/} _withEach methodCodes (-1)\n")
                            } else {
                                showString('\t\t_test (\'' + codeAndArgs.testName + numberString + '\' , ' + codeAndArgs.testName +
                                        '_DATA ) _xpect {}' + " // -> $resultTypeName\n")
                            }
                        } else { // NON static methods
                            showString('\t\t\t_test ("' + codeAndArgs.testName + numberString + '[$_key]" , ' + codeAndArgs.testName + '_DATA ) _xpect {}' + " // -> $resultTypeName\n")
                        }
                    }
                } else {
                    if (codeAndArgs.argTypesAndName.size() == 0) {
                        if(isStatic) {
                            showString('\t\t_test (\'' + codeAndArgs.testName + numberString + '\')   _xpect {\n\t\t_okIfCaught(' +
                                    codeAndArgs.exceptionType.getCanonicalName() + ')\n\t\t}\n')

                        } else {
                            showString('\t\t\t_test ("' + codeAndArgs.testName + numberString + '[$_key]")   _xpect {\n\t\t_okIfCaught(' +
                                    codeAndArgs.exceptionType.getCanonicalName() + ')\n\t\t\t}\n')
                        }
                    } else {
                        if(isStatic) {
                            showString('\t\t_test (\'' + codeAndArgs.testName + numberString + '\' , ' + codeAndArgs
                                    .testName + '_DATA ) _xpect {\n\t\t\t_okIfCaught(' +
                                    codeAndArgs.exceptionType.getCanonicalName() + ')\n\t\t}\n')
                        } else {
                            showString('\t\t\t_test ("' + codeAndArgs.testName + numberString + '[$_key]" , ' + codeAndArgs
                                    .testName + '_DATA ) _xpect {\n\t\t\t\t_okIfCaught(' +
                                    codeAndArgs.exceptionType.getCanonicalName() + ')\n\t\t\t}\n')
                        }
                    }

                }
            }
            if(isStatic) {
                showString('\t}\n')
            } else {
                showString('\t\t}\n')
            }
        }
    }

    def genCtor(Class clazz) {
        String className = clazz.getCanonicalName()
        showString('///////////// CTOR TEST DEFINITION')
        showString('// CTOR ARGS\n')
        genArgs('',ctorDefinitions)
        if(mapStaticMethodDefinition.size() > 0) {
            showString('// STATIC METHODS ARGS\n')
            for(Map.Entry<String, ArrayList<CodeAndArgs>> entry : mapStaticMethodDefinition) {
                genArgs('',entry.value)
            }
        }
        showString('// CLASS DEFS\n')
        showString ("_withClass($className) {\n\t_ctor() {\n")
        for(CodeAndArgs ctorCodeAndArgs: ctorDefinitions) {
            int num = ctorCodeAndArgs.num
            if(ctorCodeAndArgs.catchIndex == -1) {
                if(ctorCodeAndArgs.argTypesAndName.size() == 0) {
                    showString('\t\t_test (\'' + ctorCodeAndArgs.testName + "') _post {/*_tagResult?*/} _withEach methodCodes ($num)\n")

                } else {
                    showString('\t\t_test (\'' + ctorCodeAndArgs.testName + '\' , '+ ctorCodeAndArgs.testName + "_DATA ) _post {/*_tagResult?*/} _withEach methodCodes ($num)\n")

                }

            } else {
                if(ctorCodeAndArgs.argTypesAndName.size() == 0) {
                    showString('\t\t_test (\'' + ctorCodeAndArgs.testName  + '\')   _xpect {\n\t\t_okIfCaught(' +
                            ctorCodeAndArgs.exceptionType.getCanonicalName() + ')\n\t\t}\n')
                } else {
                    showString('\t\t_test (\'' + ctorCodeAndArgs.testName  + '\' , ' + ctorCodeAndArgs
                            .testName + '_DATA ) _xpect {\n\t\t\t_okIfCaught(' +
                            ctorCodeAndArgs.exceptionType.getCanonicalName() + ')\n\t\t}\n')
                }
            }
        }
        showString('\t}\n' )
        if(mapStaticMethodDefinition.size() > 0) {
            genMethTests(true, mapStaticMethodDefinition ,'')
        }
        showString('}\n')
    }

    def showString( String arg) {
        String line = arg.replaceAll('\t','    ')
        //println arg
        bufw.writeLine(line)
    }

    String transformName(String name) {
        StringBuilder stb = new StringBuilder('')
        for(char car : name.toCharArray()) {
            if(Character.isUpperCase(car)) {
                stb.append('_').append(car)
            }else {
                stb.append(Character.toUpperCase(car))
            }
        }
        return stb.toString()
    }

    String parmString(Parameter parm) {
        Type type = parm.getParameterizedType()
        String res = type.getTypeName()
        if(parm.isVarArgs()) {
            res = res.substring(0,res.lastIndexOf('['))
            res += '...'
        }
        res += ' '
        res += parm.getName()
        return res;
    }


    public static void main(String[] args) {
        for(String str : args) {
           new UnitTestsGen1(str)
        }
    }
}
