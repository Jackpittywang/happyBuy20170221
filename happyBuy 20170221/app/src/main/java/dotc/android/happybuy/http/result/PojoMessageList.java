package dotc.android.happybuy.http.result;

import java.util.List;

/**
 * Created by 陈尤岁 on 2016/12/15.
 */

public class PojoMessageList extends PojoBaseList{

    public List<PojoMessageItem> list;

    public String last_numb;

    @Override
    public int length() {
        return list==null?0:list.size();
    }

    @Override
    public String lastNumb() {
        return last_numb;
    }
}
