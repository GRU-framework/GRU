package org.gruth.demos.junit;

import org.gruth.junit.JunitWrapper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author bamade
 */
// Date: 11/03/15

public class TztWrapper extends JunitWrapper {
    @BeforeClass
    public static void before() {
        System.setProperty("gruth.resultReporter", "org.gruth.reports.SimpleFailsReporter:org.gruth.reports.SimpleResultReporter");
        JunitWrapper.before();
    }
    @Test
    public void testAll() {
        super.testAll();
    }
}

