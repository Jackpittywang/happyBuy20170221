package dotc.android.happybuy.config.abtest.bean;

import java.util.List;

import dotc.android.happybuy.config.abtest.core.IConfigBean;
import dotc.android.happybuy.proguard.NoProguard;

/**
 * Created by wangjun on 16/11/30.
 */

public class AbtestConfigBean implements IConfigBean, NoProguard {

    public String version;
    public String segment_id;
    public int coins;
    public UiInfo ui;
    public TopUpTips topup;
    public UrlInfo url;
    public UpgradeInfo update;
    public NotifyInfo notify;
    public RateAppInfo rate_app;
    public ShareInfo share;
    public HomeActive home_active;
    public List<YouLikeItem> guess_you_like;
    public PrizeShareInfo prize_share_info;
    public FirstBuyGuide first_buy_guide;

    public String getSegmentId() {
        return segment_id;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        return "AbtestConfigBean{" +
                "version='" + version + '\'' +
                "coins='" + coins + '\'' +
                ", segment_id='" + segment_id + '\'' +
                ", ui=" + ui +
                ", TopUpTips=" + topup +
                ", url=" + url +
                ", update=" + update +
                ", notify=" + notify +
                ", rate_app=" + rate_app +
                ", share=" + share +
                ", home_active=" + home_active +
                '}';
    }
}
