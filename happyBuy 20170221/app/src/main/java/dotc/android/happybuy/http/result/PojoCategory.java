package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by wangjun on 16/3/30.
 */
public class PojoCategory implements Serializable {

    public String id;
    public String icon;
    public String name;
//    public String desc;


    @Override
    public String toString() {
        return "PojoCategory{" +
                "id='" + id + '\'' +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
