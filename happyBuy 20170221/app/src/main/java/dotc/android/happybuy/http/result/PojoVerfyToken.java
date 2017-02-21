package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by Avazu on 2016/4/1.
 */
public class PojoVerfyToken implements Serializable {

    public String new_token;
    public long expire_time;

    @Override
    public String toString() {
        return "PojoVerfyToken{" +
                "new_token='" + new_token + '\'' +
                ", expire_time=" + expire_time +
                '}';
    }
}
