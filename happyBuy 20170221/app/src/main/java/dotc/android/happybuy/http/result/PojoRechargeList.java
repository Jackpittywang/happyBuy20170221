package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoRechargeList implements Serializable{
    public List<PojoBilling> list;
    public String default_product_id;

    @Override
    public String toString() {
        return "PojoRechargeList{" +
                "list=" + list +
                ", default_product_id='" + default_product_id + '\'' +
                '}';
    }
}
