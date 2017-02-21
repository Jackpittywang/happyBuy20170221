package dotc.android.happybuy.config.abtest.bean;

import dotc.android.happybuy.config.abtest.core.MultiLang;

/**
 * Created by wangjun on 16/12/7.
 */

public class NotifyInfo {

    public LongNoopenApp long_noopen_app;
    public LongTopup long_topup;

    public class LongNoopenApp {
        public boolean enable;
        public String interval;
        public MultiLang title;
        public MultiLang content;
        public String click_action;

        @Override
        public String toString() {
            return "LongNoopenApp{" +
                    "enable=" + enable +
                    ", interval='" + interval + '\'' +
                    ", title=" + title +
                    ", content=" + content +
                    ", click_action='" + click_action + '\'' +
                    '}';
        }
    }

    public class LongTopup {
        public boolean enable;
        public String interval;
        public MultiLang title;
        public MultiLang content;
        public String click_action;
        public String link;

        @Override
        public String toString() {
            return "LongTopup{" +
                    "enable=" + enable +
                    ", interval='" + interval + '\'' +
                    ", title=" + title +
                    ", content=" + content +
                    ", click_action='" + click_action + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NotifyInfo{" +
                "long_noopen_app=" + long_noopen_app +
                ", long_topup=" + long_topup +
                '}';
    }
}
