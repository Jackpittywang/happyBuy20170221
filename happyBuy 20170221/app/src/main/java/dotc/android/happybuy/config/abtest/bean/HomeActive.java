package dotc.android.happybuy.config.abtest.bean;

import java.util.List;

import dotc.android.happybuy.config.abtest.core.MultiLang;

/**
 * Created by wangjun on 16/12/13.
 */

public class HomeActive {

    public List<PortalButton> portal_button;
    public List<PortalBanner> portal_banner;
    public int banner_width;
    public int banner_height;

    public class PortalButton {
        public String pic_url;
        public MultiLang name;
        public String click_url;

    }

    public class PortalBanner {
        public String pic_url;
        public String click_url;

    }

    @Override
    public String toString() {
        return "HomeActive{" +
                "portal_button=" + portal_button +
                ", portal_banner=" + portal_banner +
                ", banner_width=" + banner_width +
                ", banner_height=" + banner_height +
                '}';
    }
}
