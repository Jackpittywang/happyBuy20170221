package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangjun on 16/3/30.
 */
public class PojoCategoryList implements Serializable {

    public List<PojoCategory> categories;

    @Override
    public String toString() {
        return "PojoCategoryList{" +
                "categories=" + categories +
                '}';
    }
}
