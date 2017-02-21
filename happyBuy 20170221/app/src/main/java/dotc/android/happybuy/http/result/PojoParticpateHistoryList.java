package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangjun on 16/3/29.
 */
public class PojoParticpateHistoryList implements Serializable {

    public long server_time;
    public List<PojoParticpateHistory> list;
    public String last_numb;
    
}
