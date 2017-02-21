package dotc.android.happybuy.http.result;

import java.util.List;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoProductTabList {
    public List<PojoProductTab> list;
    public long timestamp;

    @Override
    public String toString() {
        return "ADS{" +
                "list=" + list +
                ", timestamp=" + timestamp +
                '}';
    }

    public class PojoProductTab {
        public String categoryId;
        public String showAlertTime;
        public String title;

        @Override
        public String toString() {
            return "PojoProductTab{" +
                    "categoryId='" + categoryId + '\'' +
                    ", showAlertTime='" + showAlertTime + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
}
