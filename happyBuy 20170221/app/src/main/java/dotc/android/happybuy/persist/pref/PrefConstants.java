package dotc.android.happybuy.persist.pref;

/**
 * Created by wangjun on 16/3/30.
 */
public class PrefConstants {

    @Deprecated
    public static final class Network {
        //instead of Token.Token
        @Deprecated
        public static final String TOKEN = "token";

        //instead of UserInfo.UID
        @Deprecated
        public static final String uid = "userId";
    }

    public static final class Login {

    }

    public static final class ANONYMOUS {
        public static final String ANONYMOUS_UID = "anonymous_uid";
        public static final String ANONYMOUS_TOKEN = "anonymous_token";
    }

    public static final class ISFIRSTOPEN {
        public static final String IS_FIRST_OPEN = "is_first_open";

    }

    public static final class FINISHFIRSTRECHARGE {
        public static final String HAVE_FINISHED_FIRST_RECHARGE = "have_finished_first_recharge";
        public static final String RECHARGE_INTERVAL_POSITION = "recharge_interval_position";
    }

    public static final class LONGTIMENOOPEN {
        public static final String INTERVAL_POSITION = "interval_position";
    }

    public static final class CommentTime {
        public static final String COMMENT_TIME = "comment_time";
        public static final String COMMENT_LIMIT_TIME = "comment_limit_time";
        public static final String PAY_FINISHED_COMMENT_TIME = "pay_finished_comment_time";
        public static final String PAY_FINISHED_LIMIT_COMMENT_TIME = "pay_finished_limit_comment_time";

    }

    public static final class Token {
        public static final String TOKEN = "token";
        public static final String CREATE_TIME = "token_create_time";//创建时间
        public static final String EXPIRE_TIME = "token_expire_time";//过期时间
    }

    public static final class UserInfo {
        public static final String USER_NAME = "user_name";
        public static final String USER_ICON_URL = "user_icon_url";
        public static final String LEVEL = "level";
        public static final String TYPE = "type";
        public static final String COIN = "coin";
        public static final String GEO = "geo";
        public static final String UID = "userId";
        public static final String COUPON_COUNT = "coupon_count";
        public static final String BIND_TYPE= "bind_type";

    }

    public static final class Guide{
        public static final String IS_FINISH_NEWBIEGUIDE = "is_finish_newbieguide";
        public static final String IGNORE_NEWBIEGUIDE = "ignore_newbieguide";
    }

    public static final class AbtestConfig {
        public static final String LAST_UPDATE_TIME = "abtest_config_last_update_time";
    }

    public static final class Config {
        public static final String LAST_UPDATE_TIME = "config_last_update_time";
        public static final String LAST_PAYMENT_UPDATE_TIME = "config_payment_last_update_time";//触发配置拉取时间
        public static final String LAST_PAYMENT_SUCCESS_TIME = "config_payment_last_success_time";//触发拉取成功时间
        public static final String H5_CONFIG = "config_h5";
        public static final String ICOINS_CONFIG = "config_icons";
        public static final String CATEHOTY_CONFIG = "config_category";
        public static final String RECHARGE_CONFIG = "config_recharge";
        public static final String AB_URL_PREFIX = "ab_url_prefix";
    }

    public static final class Language {
        public static final String APP = "language_app";

    }

    public static final class AppUpgrade {
        public static final String NOTIFY_UPGRADE_INFO = "notify_upgrade_info";
    }

    public static final class SharePrize{
        public static final String TIP_DIALOG_NO_LONGER_PROMPT="tip_dialog_no_longer_prompt";
    }

    public static final class NotifyAvailableCoupon{
        public static final String NOTIFIED_COUPON_IDS="notified_coupon_ids";
        public static final String LAST_NOTIFIED_REQ_CODE="last_notified_req_code";
    }
}
