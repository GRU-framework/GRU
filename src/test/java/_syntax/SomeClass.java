package _syntax;

/**
 * @author bamade
 */
// Date: 24/02/15

public class SomeClass {
    static int idGen;
    String id = String.valueOf(idGen++) ;

    public String getId() {
        return id;
    }

    String value ;

    public SomeClass(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if(value == null) {
            throw new IllegalArgumentException("should be nonnull") ;
        }
        this.value = value ;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SomeClass{");
        sb.append("id='").append(id).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
