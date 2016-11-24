package _testjava.lang
//import static org.gruth.utils.DefUtils.*

_using(Double) {
     positives  {
            NEUTRAL 12.12D
            SMALL 0.02D
            ZERO 0D
            VERY_SMALL 0.000000000037D
            BIG 1273747576.46D
            VERY_BIG 12345678973747576777879000.45D
            // with prime values
            // with imprecise double values
    }

     negatives  {
            NEUTRAL (-12.12D)
            SMALL (-0.02D)
            ZERO (-0D)
            VERY_SMALL (-0.000000000037D)
            BIG (-1273747576.46D)
            VERY_BIG (-12345678973747576777879000.45D)
            // with prime values
            // with imprecise double values
    }

     special  {
            _MAX_VALUE Double.MAX_VALUE
            _MIN_NORMAL Double.MIN_NORMAL
            _MIN_VALUE Double.MIN_VALUE
            _NAN Double.NaN
            _POSITIVE_INFINITY Double.POSITIVE_INFINITY
            _NEGATIVE_INFINITY Double.NEGATIVE_INFINITY
            //TODO check for values coming from floats ...

    }

     angles  {
            NEUTRAL_POS 25.80D
            POSITIVE33 33.33D
            POSITIVE90 90.0D
            POSITIVE180 180.0D
            POSITIVE360 360.0D
            POSITIVE720 720.0D
            NEUTRAL_NEG (-25.80D)
            NEGATIVE33 (-33.33D)
            NEGATIVE90 (-90.0D)
            NEGATIVE180 (-180.0D)
            NEGATIVE360 (-360.0D)
            NEGATIVE720 (-720.0D)
    }
    /**
     * Renard's series R10  http://en.wikipedia.org/wiki/Preferred_number
     */
     renardR10  {
            S0 1.00D
            S1 1.25D
            S2 1.60D
            S3 2.00D
            S4 2.50D
            S5 3.15D
            S6 4.00D
            S7 5.00D
            S8 6.30D
            S9 8.00D
    }
}