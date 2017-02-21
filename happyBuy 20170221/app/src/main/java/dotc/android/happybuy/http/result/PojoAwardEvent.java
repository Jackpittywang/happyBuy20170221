package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoAwardEvent implements Serializable{

    public String id;//此接口表示product_item_id
    public String award_uid;
    public long award_time;

    public String product_id;
    public String product_item;//此接口表示期数
    public String default_image;
    public String click_url;
    public String name;
    public String nick;
    public int status;

    @Override
    public String toString() {
        return "PojoAwardEvent{" +
                "id='" + id + '\'' +
                ", award_uid='" + award_uid + '\'' +
                ", award_time=" + award_time +
                ", product_id='" + product_id + '\'' +
                ", product_item='" + product_item + '\'' +
                ", default_image='" + default_image + '\'' +
                ", click_url='" + click_url + '\'' +
                ", name='" + name + '\'' +
                ", nick='" + nick + '\'' +
                ", status=" + status +
                '}';
    }
}
