package dotc.android.happybuy.http.result;

import java.util.Arrays;

/**
 * Created by huangli on 16/4/20.
 */
public class PojoShareResultInfo {
    public String product_id;
    public String order_id;
    public String product_name;
    public String product_item;
    public String participate_count;
    public String award_code;
    public String award_time;
    public String uid;
    public String create_time;
    public String ip;
    public String message;
    public String[] images;
    public String status;

    @Override
    public String toString() {
        return "PojoShareResultInfo{" +
                "product_id='" + product_id + '\'' +
                ", order_id='" + order_id + '\'' +
                ", product_name='" + product_name + '\'' +
                ", product_item='" + product_item + '\'' +
                ", participate_count='" + participate_count + '\'' +
                ", award_code='" + award_code + '\'' +
                ", award_time='" + award_time + '\'' +
                ", uid='" + uid + '\'' +
                ", create_time='" + create_time + '\'' +
                ", ip='" + ip + '\'' +
                ", message='" + message + '\'' +
                ", images=" + Arrays.toString(images) +
                ", status='" + status + '\'' +
                '}';
    }
}
