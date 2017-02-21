package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by wangjun on 16/3/28.
 */
public class PojoOrder implements Serializable{

    public String order_id;
//    public int gp;


    @Override
    public String toString() {
        return "PojoOrder{" +
                "order_id='" + order_id + '\'' +
                '}';
    }
}
