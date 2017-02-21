package dotc.android.happybuy.config.abtest.bean;

import dotc.android.happybuy.config.abtest.core.MultiLang;

/**
 * Created by wangjun on 16/12/7.
 */

public class RateAppInfo {

    public boolean enable;
    public MultiLang title;
    public MultiLang content;
    public String click_action;
    public String link;
    public int max_alert;
    public int after_buy_times;

    @Override
    public String toString() {
        return "RateAppInfo{" +
                "enable=" + enable +
                ", title=" + title +
                ", content=" + content +
                ", click_action='" + click_action + '\'' +
                ", link='" + link + '\'' +
                ", max_alert=" + max_alert +
                ", after_buy_times=" + after_buy_times +
                '}';
    }
}
