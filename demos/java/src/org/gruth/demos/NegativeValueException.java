package org.gruth.demos;

/**
 * @author bamade
 */
// Date: 09/03/15


public class NegativeValueException extends IllegalArgumentException {
    private final Number valeur;

    public NegativeValueException(Number valeur) {
        this.valeur = valeur;
    }

    public NegativeValueException(String message, Number valeur) {
        super(message);
        this.valeur = valeur;
    }

    public NegativeValueException(String message, Throwable cause, Number valeur) {
        super(message, cause);
        this.valeur = valeur;
    }

    public NegativeValueException(Throwable cause, Number valeur) {
        super(cause);
        this.valeur = valeur;
    }

    @Override
    public String toString() {
        return super.toString() + " " + valeur;
    }

}

