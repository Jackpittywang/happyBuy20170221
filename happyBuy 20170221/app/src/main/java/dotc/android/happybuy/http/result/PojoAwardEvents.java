package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoAwardEvents implements Serializable{
    public List<PojoAwardEvent> list;
    public long server_time;

    @Override
    public String toString() {
        return "PojoAwardEvents{" +
                "list=" + list +
                ", server_time=" + server_time +
                '}';
    }
}

