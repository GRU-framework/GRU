package _syntax;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author bamade
 */
// Date: 10/03/15

public class ClassWithStatic {
    private static long timeStamp = System.currentTimeMillis() ;

    private static String staticMessage ;

    String name ;

    int[] values ;
    BigDecimal[] bigValues ;

    public ClassWithStatic(String name, int... values) {
        this.name = name;
        this.values = values;
    }

    public ClassWithStatic(int number, BigDecimal... bigValues) {
        this.name = String.valueOf(number);
        this.bigValues = bigValues;
    }

    public ClassWithStatic() {
    }

    public static long getTimeStamp() {
        return timeStamp;
    }

    public static String getStaticMessage() {
        return staticMessage;
    }

    public String getName() {
        return name;
    }

    public int[] getValues() {
        return values;
    }

    public BigDecimal[] getBigValues() {
        return bigValues;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ClassWithStatic{");
        sb.append("name='").append(name).append('\'');
        sb.append(", values=");
        if (values == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < values.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(values[i]);
            sb.append(']');
        }
        sb.append(", bigValues=").append(bigValues == null ? "null" : Arrays.asList(bigValues).toString());
        sb.append('}');
        return sb.toString();
    }

    public static void setStaticMessage(String staticMessage) {
        ClassWithStatic.staticMessage = staticMessage;
    }
}
