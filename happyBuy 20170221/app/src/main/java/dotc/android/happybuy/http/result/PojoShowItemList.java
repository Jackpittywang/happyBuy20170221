package dotc.android.happybuy.http.result;

import java.util.List;

/**
 * Created by huangli on 16/4/1.
 */
public class PojoShowItemList {
    public List<PojoShowItem> list;
    public String last_numb;

    @Override
    public String toString() {
        return "PojoShowItemList{" +
                "list=" + list +
                ", last_numb='" + last_numb + '\'' +
                '}';
    }
}
