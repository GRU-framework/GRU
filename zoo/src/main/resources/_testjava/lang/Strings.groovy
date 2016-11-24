package _testjava.lang
//import static org.gruth.utils.DefUtils.*

_using(String) {

/**
 *  series of String values for test.
 *  As in all prepared test values the package start with underscore and name of class ends in "_s".
 * @author bamade
 *
 */
    floatingNumeric {
        positives {
            NEUTRAL_3 ('12.129')
            NEUTRAL_2 ('12.12')
            NEUTRAL_0 ('12.')
            NEUTRAL_0_NO_DOT ('12')
            VERY_SMALL ('0.0000003')
            DOT_FIRST ('.01')
            ZERO ('0.')
            ZERO_NO_DOT ('0')
        }


        negatives {
            NEUTRAL_3 ('-12.129')
            NEUTRAL_2 ('-12.12')
            NEUTRAL_0 ('-12')
            VERY_SMALL ('-0.0000003')
            ZERO ('-0.')
            ZERO_NO_DOT ('-0')

        }

        //TODO java thousand separator!
        special {
            EXPONENT ('10.29E66')
            SMALL_EXPONENT ('0.007E-133')
            MAX_DOUBLE(String.valueOf(Double.MAX_VALUE))
            MIN_NORMAL(String.valueOf(Double.MIN_NORMAL))
            MIN_VALUE(String.valueOf(Double.MIN_VALUE))
        }

        failed {
            COMMA ('1069,0')
            SPACES ('10 000.69')
            ANYTHING ('ANY')
        }
    }

    plain {

        //TODO parameterize by evaluating a system property
        NEUTRAL ('hello')
        SUPPER ('HELLO')
        CAMEL ('HelloWorld')
        UNDERSCORES ('hello_world')
        SHORT_1 ('h')
        SHORT_UNDERSCORE ('_')
        LONG ('paradimethylaminobenzeneazobenzeneparasulfonatedesodium')
        //DAMN_LONG 256 characters long
        DAMN_LONG ("JAPRxmJajsBKbyTebnzHyICpcPVdPWDBagBjQeZedufWCLhGGNqMMvOfxSCgOrUigoOFsOzouIRYdgKZVhswJLpAwObgemb" +
                "yMmTriAPmjwwnSkcNrVUgZlLDmLBGvrnnMdXMNeCBMuooZDlyNyZdwDapnEqDbGHBqYMdsIHmBWYfcrnUsAYGqGkqyyJUHXEYuAwxmtufvtOmMvoDwxfXqt" +
                "uypNhpBZCIWHWFMybuJhsMNlPXIPKdwxJbXngevFRz")


    }

    nocontent {
        EMPTY ('')
        _NULL null as String
    }

    //TODO parameterize by evaluating  system properties
    strange {
        SPACES ('hello world\ttab')
        SPACES_AROUND (' to be trimed ')
        NEWLINE ('before\nafter')
        COMMENT_LINE ('//comment')
        C_COMMENT ('/*comment*/')
        SH_COMMENT ('#comment')
        QUOTE_1 ('h\'o\'p')
        QUOTE_2 ('h"o"p')
        C_ADDRESS ('&thing')
        NO_ID_1 ('++thing')
        NO_ID_2 ('th+ing')
        NO_ID_3 ('th%?-*ing')
        AROBASE ('@thing')
        BACKSLASH ('he\\lo')
        BACKSLASH_END ('hello\\')

    }
    //TODO parameterize by evaluating  system properties
    paths {
        RELATIVE ('dir/file')
        RELATIVE_SLASH ('dir/dir/')
        ABSOLUTE ('/top/file')
        ABSOLUTE_SLASH ('/top/dir/')
        UP ('../there')
        UP_IN_THE_MIDDLE ('/dir/../there')
    }

    //TODO complete
    i18n {
        ISO_8859_15 ('')

    }

    //TODO regexp (in a different file)
}


