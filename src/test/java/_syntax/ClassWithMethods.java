package _syntax;

import java.math.BigDecimal;

/**
 * @author bamade
 */
// Date: 05/03/15

public class ClassWithMethods {
    String stringField;
    int intField;
    double doubleField ;
    BigDecimal bigDecField ;

    public ClassWithMethods(String stringField, int intField, double doubleField, BigDecimal bigDecField) {
        this.stringField = stringField;
        this.intField = intField;
        this.doubleField = doubleField;
        this.bigDecField = bigDecField;
    }

    public ClassWithMethods(String stringField, int intField,  BigDecimal bigDecField) {
        this.stringField = stringField;
        this.intField = intField;
        this.bigDecField = bigDecField;
    }
    public ClassWithMethods(String stringField, int intField) throws NullPointerException {
        this.stringField = stringField;
        this.intField = intField;
    }

    public void setAll(String stringField, int intField, double doubleField, BigDecimal bigDecField) {
        this.stringField = stringField;
        this.intField = intField;
        this.doubleField = doubleField;
        this.bigDecField = bigDecField;
    }

    public static ClassWithMethods factory (String stringField, int intField, double doubleField, BigDecimal bigDecField) {
        return new ClassWithMethods(stringField,intField,doubleField,bigDecField) ;
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField)  throws NullPointerException {
        this.stringField = stringField;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public BigDecimal getBigDecField() {
        return bigDecField;
    }

    public void setBigDecField(BigDecimal bigDecField) {
        this.bigDecField = bigDecField;
    }

    public int countObjects(String arg1, Object... other) {
        return other.length ;
    }

    public int countInts(String arg1, int... other) {
        return other.length ;
    }
    public int simpleCountObjects( Object... other) {
        return other.length ;
    }
    public int simpleCountInts( int... other) {
        return other.length ;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ClassWithMethods{");
        sb.append("stringField='").append(stringField).append('\'');
        sb.append(", intField=").append(intField);
        sb.append(", doubleField=").append(doubleField);
        sb.append(", bigDecField=").append(bigDecField);
        sb.append('}');
        return sb.toString();
    }
}
