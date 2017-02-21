package dotc.android.happybuy.http.result;

import java.util.List;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoNotics {

    public List<Notics> notices;

    public static class Notics {
        public String id;

        public String click_url;
        public long award_time;
        public String award_uid;
        public String nick;
        public String product_item;
        public String product_id;
        public String name;
//        public String type;


        @Override
        public String toString() {
            return "Notics{" +
                    "id='" + id + '\'' +
                    ", click_url='" + click_url + '\'' +
                    ", award_time='" + award_time + '\'' +
                    ", award_uid='" + award_uid + '\'' +
                    ", nick='" + nick + '\'' +
                    ", product_item='" + product_item + '\'' +
                    ", product_id='" + product_id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PojoNotics{" +
                "notices=" + notices +
                '}';
    }
}
