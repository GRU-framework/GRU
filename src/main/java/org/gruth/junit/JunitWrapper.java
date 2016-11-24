package org.gruth.junit;

import org.gruth.gru;
import org.gruth.reports.SimpleFailsReporter;
import org.gruth.reports.TztReport;
import org.gruth.utils.logging.GruLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;


/**
 * @author bamade
 */
// Date: 22/04/2014

public class JunitWrapper {
    static final GruLogger CURLOG = GruLogger.getLogger("org.gruth.junit") ;

    static Class referenceClass = JunitWrapper.class;
    static String resourceName ;
    @BeforeClass
    public static void before() {
        String referenceClassName = System.getProperty("org.gruth.junit.referenceClass") ;
        if( referenceClassName != null) {
            try {
                referenceClass = Class.forName(referenceClassName) ;
            } catch (ClassNotFoundException e) {
                 CURLOG.severe("could not find reference class", e);
            }
        }

        resourceName = System.getProperty("org.gruth.junit.listResource", "/gruFiles.txt") ;
        //System.setProperty("gruth.traces", "NODE_BUILD,NODE_EVAL");
        if( null == System.getProperty("gruth.resultReporter")) {
            System.setProperty("gruth.resultReporter", "org.gruth.reports.SimpleFailsReporter");
        }
        CURLOG.coordLevelSettings(Level.INFO, java.util.logging.Handler.class);

    }

    @Before
    public void beforeEachTest() {
//        SimpleFailsReporter.clear();
    }

    @Test
    public void testAll() {
        int scriptErrors = 0 ;
        ArrayList<String> scriptsMessages  = new ArrayList() ;
        try {
            InputStream is = referenceClass.getResourceAsStream(resourceName) ;
            BufferedReader bufr = new BufferedReader(new InputStreamReader(is)) ;
            String line = null;
            while (null != (line = bufr.readLine())) {
                line = line.trim();
                if (line.startsWith("#")) continue;
                if(line.length() == 0) continue ;
                List<String> argsList = new ArrayList<>() ;
                StringTokenizer stk = new StringTokenizer(line) ;
                String resourceFile = stk.nextToken() ;
                URL url = referenceClass.getResource(resourceFile);
                if (url != null) {
                    argsList.add(url.getPath()) ;
                    while (stk.hasMoreElements()) {
                        argsList.add((String)stk.nextElement()) ;
                    }
                    String[] args = argsList.toArray(new String[0]) ;
                    try {
                        gru.main(args);
                    } catch (Exception exc) {
                        CURLOG.severe(" caught script Exception " , exc);
                        scriptsMessages.add(Arrays.toString(args) + " ->" + exc.toString() + "\n") ;
                        scriptErrors++ ;
                        continue ;
                    }
                } else {
                    Assert.fail("resource " + line + " : not found");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            CURLOG.info("---> " + SimpleFailsReporter.getNbTests() + " tests!. Success: " + SimpleFailsReporter.getSuccess() + "; failed :" + SimpleFailsReporter.getFails() + "; scriptErrors :" + TztReport.scriptErrors + "\n stats: " + SimpleFailsReporter.diagStats());
            if(scriptErrors != 0 ) {
                CURLOG.severe(" SCRIPT ERRORS : \n" + scriptsMessages);
            }
            Assert.assertEquals(SimpleFailsReporter.failsString(), 0, SimpleFailsReporter.getFails() + scriptErrors);
            //Assert.assertEquals("", 0, SimpleFailsReporter.getFails() + scriptErrors);
        } catch (IOException e) {
            CURLOG.severe(resourceName + " not found giving up(or exception -> ", e);
            return;
        }

    }
}

