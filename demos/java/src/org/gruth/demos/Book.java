package org.gruth.demos;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * @author bamade
 */
// Date: 17/03/15

public class Book extends  Product {
    //  should come from a resource!!!
    public static BigDecimal TAX_RATE = new BigDecimal("1.08") ;

    ///////// CONSTRUCTORS

    public Book( String reference, String title, String author,
                 String editor, Euros rawPrice, int stock,
                 String description, String imageName) {
        super( reference, title,
                Objects.requireNonNull(author) , editor, rawPrice, stock, description, imageName);
    }

    public Book( String reference, String title, String author,
                 String editor, Euros rawPrice, int stock) {
        this(reference,title,author,
                editor, rawPrice, stock,
                null, null) ;
    }
    public Book( String reference, String title, String author,
                 String editor, BigDecimal rawPrice, int stock) {
        this(reference,title,author,
                editor, new Euros(rawPrice), stock,
                null, null) ;
    }

    ////////// ABSTRACT METHODS

    @Override
    public String getProductType() {
        return "Book";
    }

    @Override
    public BigDecimal getTaxRate() {
        return TAX_RATE;
    }

    public static void setTAX_RATE(BigDecimal TAX_RATE) {
        Book.TAX_RATE = TAX_RATE;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Book{");
        sb.append("reference:\"").append(getReference()).append("\"") ;
        sb.append(", title:\"").append(getTitle()).append("\"") ;
        sb.append(", author:\"").append(getAuthor()).append("\"") ;
        sb.append(", price:").append(getPrice()) ;
        sb.append('}');
        return sb.toString();
    }

    ////////// ACCESSEURS SEMANTIQUES

    public String getTitle() {
        return this.getName();
    }

    public String getAuthor() {
        Optional<String> notOptional = this.getDesigner() ;
        return notOptional.get() ;
    }

    public String getEditor() {
        return this.getProducer();
    }


}
