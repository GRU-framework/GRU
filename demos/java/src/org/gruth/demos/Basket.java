package org.gruth.demos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author bamade
 */
// Date: 17/03/15

public class Basket {
    static int number = 0 ;

    private List<Product> contentList = new ArrayList<>() ;
    private List<Product> nonModifiableView = Collections.unmodifiableList(contentList) ;
    private Client client ;
    private int idNum = number++ ;

    //// CONSTRUCTORS
    public Basket() {
    }

    public Basket(Client client) {
        this.client = client;
    }

    ///// ACCESSORS/MUTATORS


    public Optional<Client> getClient() {
        return Optional.ofNullable(client);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Product> getContentList() {
        return nonModifiableView ;
    }

    ////////// methods
    public synchronized void add(Product produit) throws Product.StockException {
        produit.removeFromStock(1);
        // NOT EXECUTED IF EXCEPTION
        contentList.add(produit);
    }

    public synchronized  void remove(Product produit) {
        contentList.remove(produit) ;
    }

    public Euros getTotal() {
        Euros res = Euros.ZERO ;
        for(Product produit : contentList) {
            res = res.plus(produit.getPrice()) ;
        }
        // on applique la reduction si le client est present
        if(client != null) {
            res = res.multiply(client.getReduction()) ;
        }
        return res ;
    }

    String getId() {
        return "Basket_"+idNum ;
    }

}
