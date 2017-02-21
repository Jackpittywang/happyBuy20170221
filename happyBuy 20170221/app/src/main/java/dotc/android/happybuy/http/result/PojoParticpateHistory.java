package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by wangjun on 16/3/29.
 */
public class PojoParticpateHistory implements Serializable {

    public String product_id;
    public String product_item_id;
    public String default_image;
    public String product_name;
    public int product_item_status;
    public String period;
    public int min_units;
    public int max_units;
    public int total_units;
    public int remain_units;
    public int default_units;
    public int buy_item_count;

    public String is_shareover;

    public String award_code;
    public String award_time;
    public String award_uid;
    public int award_buy_count;
    public String award_user_avatar;
    public String award_nickname;

    @Override
    public String toString() {
        return "PojoParticpateHistory{" +
                "is_shareover='" + is_shareover + '\'' +
                ", product_id='" + product_id + '\'' +
                ", product_item_id='" + product_item_id + '\'' +
                ", default_image='" + default_image + '\'' +
                ", product_name='" + product_name + '\'' +
                ", product_item_status=" + product_item_status +
                ", period='" + period + '\'' +
                ", min_units=" + min_units +
                ", max_units=" + max_units +
                ", total_units=" + total_units +
                ", remain_units=" + remain_units +
                ", default_units=" + default_units +
                ", buy_item_count=" + buy_item_count +
                ", award_code='" + award_code + '\'' +
                ", award_time='" + award_time + '\'' +
                ", award_uid='" + award_uid + '\'' +
                ", award_nickname='" + award_nickname + '\'' +
                '}';
    }

}
