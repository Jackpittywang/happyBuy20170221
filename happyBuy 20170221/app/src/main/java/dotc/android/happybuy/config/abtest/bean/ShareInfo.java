package dotc.android.happybuy.config.abtest.bean;

import dotc.android.happybuy.config.abtest.core.MultiLang;

/**
 * Created by wangjun on 16/12/7.
 */

public class ShareInfo {

    public Facebook facebook;
    public Line line;
    public Other other;

    public class Facebook {
        public String link;
        public MultiLang title;
        public MultiLang content;
        public String icon;

        @Override
        public String toString() {
            return "Facebook{" +
                    "link='" + link + '\'' +
                    ", title=" + title +
                    ", content=" + content +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

    public class Line {
        public String link;
        public MultiLang title;
        public MultiLang content;
        public String icon;

        @Override
        public String toString() {
            return "Line{" +
                    "link='" + link + '\'' +
                    ", title=" + title +
                    ", content=" + content +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

    public class Other {
        public String link;
        public MultiLang title;
        public MultiLang content;
        public String icon;

        @Override
        public String toString() {
            return "Other{" +
                    "link='" + link + '\'' +
                    ", title=" + title +
                    ", content=" + content +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ShareInfo{" +
                "facebook=" + facebook +
                ", line=" + line +
                ", other=" + other +
                '}';
    }
}
