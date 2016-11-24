package _junit;

import org.gruth.junit.JunitWrapper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author bamade
 */
// Date: 05/03/15

public class TestWrapper extends JunitWrapper{
    static {
        System.setProperty("org.gruth.junit.referenceClass", "_junit.TestWrapper") ;
    }
    @BeforeClass
    public static void before() {
        System.setProperty("gruth.resultReporter", "org.gruth.reports.SimpleFailsReporter:org.gruth.reports.SimpleResultReporter");
        JunitWrapper.before() ;
    }
    @Test
    public void testAll() {
       super.testAll();
    }
}
