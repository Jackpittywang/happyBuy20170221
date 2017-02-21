package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huangli on 16/3/31.
 */
public class PojoShowItem implements Serializable {
    public String id;
    public String uid;
    public String product_id;
    public String order_id;
    public String product_name;
    public String product_item;
    public String product_item_id;
    public String participate_count;
    public String award_code;
    public String award_time;
    public String message;
    public String approval_count;
    public String approval_status;
    public String create_time;
    public String update_time;
    public String create_time_str;
    public String ip;
    public String status;
    public String[] images;
    public List<PojoShowImage> new_images;
    public PojoUserInfo user_info;
    public String type;

    @Override
    public String toString() {
        return "PojoShowItem{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", product_id='" + product_id + '\'' +
                ", order_id='" + order_id + '\'' +
                ", product_name='" + product_name + '\'' +
                ", product_item='" + product_item + '\'' +
                ", product_item_id='" + product_item_id + '\'' +
                ", participate_count='" + participate_count + '\'' +
                ", award_code='" + award_code + '\'' +
                ", award_time='" + award_time + '\'' +
                ", message='" + message + '\'' +
                ", approval_count='" + approval_count + '\'' +
                ", approval_status='" + approval_status + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", create_time_str='" + create_time_str + '\'' +
                ", ip='" + ip + '\'' +
                ", status='" + status + '\'' +
                ", images=" + Arrays.toString(images) +
                ", new_images=" + new_images +
                ", user_info=" + user_info +
                ", type='" + type + '\'' +
                '}';
    }
}
