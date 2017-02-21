package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 */
public class PojoHotword implements Serializable{

    public String id;
    public String name;

    @Override
    public String toString() {
        return "PojoHotword{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
