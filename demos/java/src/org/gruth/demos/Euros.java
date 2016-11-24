package org.gruth.demos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Objects;

import static java.math.RoundingMode.HALF_EVEN;


public class Euros implements Comparable<Euros>, Serializable {

    /**
     * internal value
     */
        private final BigDecimal rawValue;
        private static Currency currency = Currency.getInstance("EUR");
        public static final Euros ZERO = new Euros("0.00");

        protected static final DecimalFormat formatter = new DecimalFormat() ;

        static {
            formatter.setParseBigDecimal(true);
        }


        public Euros(BigDecimal val) throws NegativeValueException {
            Objects.requireNonNull(val) ;
            if (-1 == val.signum()) {
                throw new NegativeValueException( val);
            }
            rawValue = val ;
        }

        public Euros(String str) throws NumberFormatException, NegativeValueException {
            this(new BigDecimal(str == null ? "null" : str));
            // ici comment tester si argument null?
            // pas terrible
        }

        public static Euros local(String str) throws ParseException, NegativeValueException {
            Objects.requireNonNull(str) ;
            BigDecimal val = localParse(str) ;
            return new Euros(val) ;
        }

        protected static BigDecimal localParse(String str) throws ParseException{
            Objects.requireNonNull(str) ;
            BigDecimal val ;
            String chNom = str.trim() ;
            chNom = chNom.replace(' ', '\u00A0') ;

            synchronized(formatter) {
                val = (BigDecimal) formatter.parse(chNom) ;
            }
            return val ;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof Euros) && (((Euros) obj).asBigDecimal().equals(this.asBigDecimal()));
        }

        public int hashCode() {
            return asBigDecimal().hashCode();
        }

        @Override
        public int compareTo(Euros otherPrice) {
            Objects.requireNonNull(otherPrice) ;
            return this.asBigDecimal().compareTo(otherPrice.asBigDecimal());
        }

        @Override
        public String toString() {
            return asBigDecimal().toPlainString();

        }

        public String toLocalizedString() {
            synchronized(formatter) {
                return formatter.format(this.asBigDecimal()) ;
            }
        }

        public int getDecimals() {
            return currency.getDefaultFractionDigits();
        }

        public Euros normalize() {
            return new Euros(this.asBigDecimal()) ;
        }

        public Currency getCurrency() {
            return  currency ;
        }

        public BigDecimal asBigDecimal() {
            BigDecimal value = rawValue.setScale(this.getDecimals(), HALF_EVEN);
            return value;
        }

        public BigDecimal asRawBigDecimal() {
            return this.rawValue;
        }

        public double doubleValue() {
            return asBigDecimal().doubleValue();
        }

        public Euros plus(Euros otherPrice) {
            Objects.requireNonNull(otherPrice) ;
            return new Euros(rawValue.add(otherPrice.rawValue));
        }

        public Euros minus(Euros otherPrice) throws NumericOperationException {
            Objects.requireNonNull(otherPrice) ;
            try {
                return new Euros(rawValue.subtract(otherPrice.rawValue));
            } catch (IllegalArgumentException exc) {
                throw  new NumericOperationException(exc, "-",
                        this.rawValue,
                        otherPrice.rawValue);
            }

        }

        public Euros multiply(BigDecimal arg) throws NegativeValueException {
            Objects.requireNonNull(arg) ;
            return new Euros(this.rawValue.multiply(arg));
        }

        public Euros multiply(String strNum) throws NegativeValueException {
            return this.multiply(new BigDecimal(strNum)) ;
        }

        public Euros localMultiply(String strNum) throws NegativeValueException, ParseException {
            BigDecimal val = localParse(strNum) ;
            return this.multiply(val) ;
        }
        public Euros multiply(int nombre) throws NegativeValueException {
            return this.multiply(new BigDecimal(nombre)) ;
        }

        public Euros multiply(double val) throws IllegalArgumentException   {
            return this.multiply(new BigDecimal(val));
        }


    }

