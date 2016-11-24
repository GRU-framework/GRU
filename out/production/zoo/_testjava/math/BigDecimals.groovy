package _testjava.math

//import static org.gruth.utils.DefUtils.*

_using(BigDecimal) {
     positives  {
         scale2 {
             NEUTRAL 12.12
             SMALL 0.02
             ZERO 0.00
             PRIME2 47.47
             BIG 1273747576.46
             VERY_BIG 12345678973747576777879000.45
         }

         scale3 {
             NEUTRAL 12.122
             SMALL 0.020
             ZERO 0.000
             PRIME3 47.477
             BIG 1273747576.467
             VERY_BIG 12345678973747576777879000.457
         }

         other {
             CURRENCY_RATIO 1.134567
             VERY_SMALL 0.000000000037
             BIG 1273747576.467699567
             VERY_BIG 12345678973747576777879000.457699457
         }
            // with prime values
    }

     negatives  {
            NEUTRAL (-12.12)
            SMALL (-0.02)
            ZERO (-0)
            VERY_SMALL (-0.000000000037)
            BIG (-1273747576.46)
            VERY_BIG (-12345678973747576777879000.45)
            // with prime values
            // with imprecise double values
    }

}