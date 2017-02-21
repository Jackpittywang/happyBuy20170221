package dotc.android.happybuy.http.result;

import java.io.Serializable;

import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoUserInfo implements Serializable{
    public int coin;
    public String avatar;
    public String level;
    public String nick;
    public String geo;
    public String create_time;
    public String update_time;
    public boolean is_finish_newbieguide;
    public String type;
    public String bind_type;
    public int coupon_count;

    @Override
    public String toString() {
        return "PojoUserInfo{" +
                "coin=" + coin +
                ", avatar='" + avatar + '\'' +
                ", level='" + level + '\'' +
                ", nick='" + nick + '\'' +
                ", geo='" + geo + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", coupon_count='" + coupon_count + '\'' +
                ", type='" + type + '\'' +
                ", bind_type='" + bind_type + '\'' +
                '}';
    }
}
