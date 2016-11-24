HERE=`pwd`

cd ../apidocs

$GROOVY_HOME/bin/groovydoc -d groovyDoc -private -sourcepath ../src/main/groovy -windowtitle "GRU$1 FRAMEWORK" org.gruth org.gruth.tools org.gruth.gen org.gruth.reports org.gruth.scripts org.gruth.utils org.gruth.utils.zoo org.gruth.utils.logging org.gruth.junit

javadoc  -d javaDoc -private -sourcepath ../src/main/java:../abstractTools/src/main/java -windowtitle "GRU$1 FRAMEWORK" -doctitle "GRU$1 FRAMEWORK" org.gruth org.gruth.tools org.gruth.gen org.gruth.reports org.gruth.scripts org.gruth.utils org.gruth.utils.zoo org.gruth.utils.logging org.gruth.junit

cd $HERE

cd ..
zip -r $HERE/gruDoc$1.zip  apidocs
cd $HERE

cd ../src/site
zip -u -r $HERE/gruDoc$1.zip asciidoc