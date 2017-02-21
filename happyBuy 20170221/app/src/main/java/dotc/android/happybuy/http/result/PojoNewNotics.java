package dotc.android.happybuy.http.result;

import java.util.List;

/**
 * Created by wangzhiyuan on 16/12/16.
 */
public class PojoNewNotics {

    public List<Notics> list;
    public String roll_time;

    public static class Notics {
        public String title;
        public String url;

        @Override
        public String toString() {
            return "Notics{" +
                    "title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

}
