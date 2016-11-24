package org.gruth.demos.junit;

import org.gruth.demos.Euros;
import org.gruth.demos.NegativeValueException;
import org.gruth.tools.Diag;
import org.gruth.tools.SingleTestReport;
import org.gruth.tools.SimplePublisher;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.EnumMap;

import static org.gruth.tools.SingleTestReport.*;

/**
 * @author bamade
 */
// Date: 21/04/15

public class TztEurosDirect {
    @BeforeClass
    public static void before() {
    }

    @AfterClass
    public static void after() {
        EnumMap<Diag, Integer> results = SimplePublisher.getResults() ;
        String res = String.valueOf(results) ;
        int fails = 0 ;
        fails += results.get(Diag.FAILED) ;
        fails += results.get(Diag.FATAL) ;
        Assert.assertEquals(res, 0, fails);

    }

    @Test
    public void testCtor() {
        String[] values = {
                "3,45",
          "3.44",
                "0.0",
                "345.567",
                "-345",
        } ;
        for(String val : values){
            SingleTestReport ctorAssertions = _ctorReport("ctor Euros" + val, Euros.class, val) ;
            try {
               Euros amount = new Euros(val) ;
               ctorAssertions.okIf(true, "ctor with {0}", val) ;
                double[] multipliers = {1, 3.45, 1000.998} ;
                for(double dbl : multipliers){
                    Euros res = amount.multiply(dbl) ;
                       _methReport(amount, "scaleMultiply " + dbl, "multiply", Euros.class,dbl)
                               .okIf(res.getDecimals()==2, "Euros decimals should be 2") ;
                               //.publish();
                }
            }catch(Exception exc) {
                if(exc instanceof NegativeValueException){
                    ctorAssertions.caught(exc, Diag.SUCCESS) ;
                } else {
                    ctorAssertions.caught(exc, Diag.FAILED) ;
                }
            }
        }

    }

    @Test
    public void testMultiply1() {

        String[] values = {
                "3.44",
                "0.0",
                "345.567",
        } ;
        for(String val : values){
            Euros gen = new Euros(val) ;
            Euros res = gen.multiply(new BigDecimal("1.000")) ;
            _reportFor( "neutral multiply for " + val,  Euros.class)
                    .okIf(gen.getDecimals()==2, "decimals should be 2" )
                    .okIf(gen.equals(res), "{0} equals {1}" , gen, res)
                    .okIf(gen.compareTo(res) == 0, "{0} compareTo {1} should yield 0 ",gen, res);
        }
    }
}
