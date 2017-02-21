package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by zhanqiang.mei on 2016/4/1.
 */
public class PojoLogin implements Serializable{

    public long expire_time;
    public String uid;
    public String token;

    @Override
    public String toString() {
        return "PojoLogin{" +
                "expire_time=" + expire_time +
                ", uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
