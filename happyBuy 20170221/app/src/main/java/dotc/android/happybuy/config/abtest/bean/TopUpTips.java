package dotc.android.happybuy.config.abtest.bean;

import dotc.android.happybuy.config.abtest.core.MultiLang;

/**
 * Created by wangzhiyuan  on 17/01/10.
 */

public class TopUpTips {

    public MultiLang tips;
    public String event_url;

    @Override
    public String toString() {
        return "ShareInfo{" +
                "tips=" + tips +
                ", event_url=" + event_url +
                '}';
    }
}
