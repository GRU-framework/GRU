package _gen;

/**
 * @author bamade
 */
// Date: 05/07/2015

public class WithFactory {
    private WithFactory() {}
    public static  WithFactory factory(String name) {
        return new WithFactory() ;
    }
}
