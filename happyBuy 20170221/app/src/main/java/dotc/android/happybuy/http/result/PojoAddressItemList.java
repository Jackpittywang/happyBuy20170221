package dotc.android.happybuy.http.result;

import java.util.List;

/**
 * Created by huangli on 16/4/5.
 */
public class PojoAddressItemList {
    public List<PojoAddressItem> list;
    public String max_add_count;
    public boolean allow_add;

    @Override
    public String toString() {
        return "PojoAddressItemList{" +
                "list=" + list +
                ", max_add_count='" + max_add_count + '\'' +
                ", allow_add=" + allow_add +
                '}';
    }
}
