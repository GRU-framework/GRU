package org.gruth.demos;

import java.util.Arrays;
import java.util.Objects;


public class NumericOperationException extends Exception {
    private final Number[] values;
    public final String operation;

    public NumericOperationException(String operation, Number... values) {
        Objects.requireNonNull(operation);
        this.values = values;
        this.operation = operation;
    }

    public NumericOperationException(String message, String operation, Number... values) {
        super(message);
        Objects.requireNonNull(operation);
        this.values = values;
        this.operation = operation;
    }

    public NumericOperationException(String message, Throwable cause, String operation, Number... values) {
        super(message, cause);
        Objects.requireNonNull(operation);
        this.values = values;
        this.operation = operation;
    }

    public NumericOperationException(Throwable cause, String operation, Number... values) {
        super(cause);
        Objects.requireNonNull(operation);
        this.values = values;
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public Number[] getArguments() {
        return values.clone();
    }

    public String toString() {
        return super.toString()
                + " "
                + operation
                + " "
                + Arrays.toString(values);
    }
}

