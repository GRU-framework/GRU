package org.gruth.demos;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author bamade
 */
// Date: 17/03/15

public class Client {
    private final String name ;
    private BigDecimal reduction ;

    public Client(String name, BigDecimal reduction) {
        this.name = Objects.requireNonNull(name) ;
        this.setReduction(reduction);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getReduction() {
        return reduction;
    }

    public final void setReduction(BigDecimal reduction) {
        this.reduction = Objects.requireNonNull(reduction);
    }
}
