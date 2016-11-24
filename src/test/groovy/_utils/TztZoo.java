package _utils;

import org.gruth.utils.LinkedTaggedsMap;
import org.gruth.utils.zoo.ZooUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author bamade
 */
// Date: 27/04/15

public class TztZoo {
    public static void main(String[] args) {
        LinkedTaggedsMap mapBig = ZooUtils.getZoo(BigDecimal.class) ;
        System.out.println(" BigDecimal = " +mapBig);

        LinkedTaggedsMap mapAny = ZooUtils.getZoo(BigInteger.class) ;
        System.out.println(" ANY = " + mapAny);

        Iterable res1 = ZooUtils.getValuesFor(BigDecimal.class);
        print(res1);

        res1 = ZooUtils.getValuesFor(BigDecimal.class,"") ;
        System.out.println("empty path");
        print(res1);
        res1 = ZooUtils.getValuesFor(BigDecimal.class,"positives") ;
        System.out.println("POSITIVES");
        print(res1);
        res1 = ZooUtils.getValuesFor(BigDecimal.class,"negatives") ;
        System.out.println("Negatives");
        print(res1);
        res1 = ZooUtils.getValuesFor(BigDecimal.class,"positives.scale2") ;
        System.out.println("POSITIVES.scale2");
        print(res1);
        res1 = ZooUtils.getValuesFor(BigDecimal.class,"positives.scale2.NEUTRAL") ;
        System.out.println("POSITIVES.scale2.NEUTRAL");
        print(res1);

    }
    public static void print(Iterable it) {
        System.out.print('[');
        for(Object obj : it) {
            System.out.print(obj + " ,");
        }
        System.out.println(']');
    }
}
