package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by LiShen
 * on 2016/12/8.
 */

public class PojoUpgradeNotify implements Serializable {
    public String notifyDate = "20000101";
    public int notifyTimes;

    @Override
    public String toString() {
        return "PojoUpgradeNotify{" +
                "notifyDate='" + notifyDate + '\'' +
                ", notifyTimes=" + notifyTimes +
                '}';
    }
}
