package _testjava.lang
//import static org.gruth.utils.DefUtils.*

_using(Integer) {
    positives {
        NEUTRAL 122
        ZERO 0
        ONE 1
        BIG 12737475
        PRIME1 47
        PRIME2 104723
        FAIRLY_BIG 107273747576
    }

    negatives {
        NEUTRAL(-122)
        ONE(-1)
        BIG(-12737475)
        PRIME1(-47)
        PRIME2(-104723)
        FAIRLY_BIG(-107273747576)
    }

    special {
        _MAX_VALUE Integer.MAX_VALUE
        _MIN_VALUE Integer.MIN_VALUE
        _BYTE_MAX Byte.MAX_VALUE
        _BYTE_MIN Byte.MIN_VALUE
        _SHORT_MAX Short.MAX_VALUE
        _SHORT_MIN Short.MIN_VALUE
    }

    //TODO values for big-endian little-endian tests

    sizes {
        ZERO 0
        ONE 1
        NEUTRAL1 66
        PRIME2 104723
        K1 1024
        K1_PLUS 1025
        K1_MINUS 1023
        K2 2048
        K2_PLUS 2049
        K2_MINUS 2047
        K5(1024 * 5)
        K5_PLUS((1024 * 5) + 1)
        K5_MINUS((1024 * 5) - 1)
        K8(1024 * 8)
        K8_PLUS((1024 * 8) + 1)
        K8_MINUS((1024 * 8) - 1)
    }

    // one of "Renard series" (preferred numbers ISO 3)

    renardR5B100 {
        S0 10
        S1 16
        S2 25
        S3 40
        S4 63
        S5 100
    }


    seryP10 {
        S0 10
        S1 100
        S3 1000
        S4 10000
        S5 100000
        S6 1000000
        S7 10000000
    }
    // TODO  add other series (exponential...) to measure scalability
}