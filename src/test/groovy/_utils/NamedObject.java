package _utils;

/**
 * @author bamade
 */
// Date: 02/03/15

public class NamedObject {
    static int idGen;
    String id = String.valueOf(idGen++) ;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("NamedObject{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
