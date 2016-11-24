package org.gruth.demos;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * @author bamade
 */
// Date: 17/03/15

public abstract class Product {

    /**
     * MemberClass
     */
    public static class StockException extends Exception {
        private final Product missingProduct;
        private final int stock;
        private final int requiredQuantity;
        private final long timeStamp = System.currentTimeMillis();

        public StockException(Product missingProduct, int stock, int requiredQuantity) {
            this.missingProduct = missingProduct;
            this.stock = stock;
            this.requiredQuantity = requiredQuantity;
        }

        public Product getMissingProduct() {
            return missingProduct;
        }

        public int getStock() {
            return stock;
        }

        public int getRequiredQuantity() {
            return requiredQuantity;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(super.toString());
            sb.append("{missingProduct=").append(missingProduct);
            sb.append(", stock=").append(stock);
            sb.append(", requiredQuantity=").append(requiredQuantity);
            sb.append('}');
            return sb.toString();
        }
    }
    /////////////////////////////////////////////////////////////////////////////
    /**
     */
    private final String reference;

    /**
     */
    private final String name;
    /**
     */
    private final String designer;
    /**
     */
    private final String producer;

    private Euros rawPrice;

    private int stock;

    private String description;
    private String imageName;

    // todo: champ Properties
    ///////////////////////// CONSTRUCTEUR GENERAL


    protected Product(String reference, String name, String designer, String producer, Euros rawPrice, int stock, String description, String imageName) {
        this.reference = Objects.requireNonNull(reference);
        this.name = Objects.requireNonNull(name);
        this.designer = designer;
        this.producer = Objects.requireNonNull(producer);
        this.rawPrice = Objects.requireNonNull(rawPrice);
        if (stock < 0) throw new NegativeValueException(stock);
        this.stock = stock;
        this.description = description;
        this.imageName = imageName;
    }

    ///////// ACCESSEURS CONCRETS

    public String getReference() {
        return reference;
    }


    public String getName() {
        return name;
    }

    public Optional<String> getDesigner() {
        return Optional.ofNullable(this.designer);
    }

    public String getProducer() {
        return producer;
    }

    public Euros getRawPrice() {
        return rawPrice;
    }

    public int getStock() {
        return stock;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    public Optional<String> getImageName() {
        return Optional.ofNullable(this.imageName);
    }

    ///////////// ACCESSORS

    public abstract String getProductType();

    public abstract BigDecimal getTaxRate();

    ///////////// ATTRIBUTES

    public Euros getPrice() {
        return this.rawPrice.multiply(this.getTaxRate());
    }

    ///////////// MUTATORS


    public void setRawPrice(Euros rawPrice) {
        this.rawPrice = Objects.requireNonNull(rawPrice);
    }
    public void setRawPrice(BigDecimal rawPrice) {
        BigDecimal bigDecimal = Objects.requireNonNull(rawPrice);
        setRawPrice(new Euros(bigDecimal));
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    /////////// STANDARD METHODS

    /**
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof Product) && reference.equals(((Product) other).reference);
    }

    @Override
    public int hashCode() {
        return reference.hashCode();
    }

    public abstract String toString();

    //////////// METHODS :  MODIFER/SERVICES

    public synchronized void addToStock(int nb) {
        if (nb < 0) throw new NegativeValueException(nb);
        stock += nb;
    }

    public synchronized void removeFromStock(int nb) throws StockException {
        if (nb < 0) throw new NegativeValueException(nb);
        if (nb > stock) {
            throw new StockException(this, stock, nb);
        }
        stock -= nb;
    }
}

