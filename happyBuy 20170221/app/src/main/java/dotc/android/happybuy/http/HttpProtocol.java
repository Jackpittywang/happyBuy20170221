package dotc.android.happybuy.http;

import build.Environment;

/**
 * Created by wangjun on 16/3/28.
 */
public class HttpProtocol {

    protected static boolean MOCK_ENABLE = false;
    protected static boolean ENCRY_ENABLE = true;
//        private static Env PROD_ENV = Environment.ENV;//生产环境
	protected static boolean FILE_ESCAPE = false;//

    private static class Domain{
        private static String PHP = Environment.PHP_URL;//php 服务器
        private static String PHP_H5 = Environment.PHP_H5_URL;//php h5 服务器
        private static String USERCENTERPHP = Environment.PHP_USR_CENTER_URL;//php 用户中心服务器

        //c++ 服务器
        private static String C = Environment.C_CACHE_URL;
        //c++ 注册服务器
        private static String C2 = Environment.C_REG_URL;//14101
        //c++ 配置服务器
        private static String C3 = Environment.ABTEST_URL;//14101
    }

    public static class H5{
        public static final String LAST_ANNOUNCEMENT = Domain.PHP_H5+"h5/index/latest_announcement";
        public static final String HELP_CENTER = Domain.PHP_H5+"en/help_center.php";
        public static final String AGREEMENT = Domain.PHP_H5+"agreement.php";
        public static final String CALCULATING_FORMULA = Domain.PHP_H5+"calculating_formula.php";
        public static final String RECHARGE_INSTRUCTION = Domain.PHP_H5+"en/recharge_instructions.php";
    }

    public static class URLS{
        public static final String ABTEST = Domain.C3+"/p/config";
//        public static final String ABTEST = Domain.C3+"sw/config/ab/";
        public static final String MAIN_HEADER = Domain.PHP+"product/mainHeader";
//        public static final String MAIN_NOTICS = Domain.C+"product/notices";
        public static final String MAIN_NOTICS = Domain.PHP+"notice/getList";
        public static final String MAIN_EVENT = Domain.C+"product/events";
        public static final String EVENT_STATUS = Domain.C+"product/status";
        public static final String RECHARGE_LIST = Domain.PHP+"payment/getPayItems";
        public static final String GOODS_DETAIL = Domain.C+ "product/detail";
        public static final String PRODUCT_PARTICIPATE_LIST = Domain.PHP+"order/getProductOrderList";
        public static final String PRIZE_INFO = Domain.PHP+"productItem/awardOperation";
        public static final String ADDRESS_CONFIRM = Domain.PHP+"award/confirmAddress";
        public static final String AWARD_EXCHANGE_COIN = Domain.PHP+"award/exchangeCoin";
        public static final String AWARD_RECHARGE_CARD = Domain.PHP+"award/cardRecharge";
        public static final String CATEGORY = Domain.PHP+"product/category";
        public static final String SEARCH = Domain.PHP+"product/search";
        public static final String HOT_WORD = Domain.PHP+"product/searchHotword";
        public static final String CONFIG = Domain.PHP+"index/config";

        public static final String USER_INFO = Domain.PHP+"user/getuserinfo";
        public static final String NICKNAME = Domain.PHP+"user/saveNickname";
        public static final String PORTRAIT = Domain.PHP+"user/saveAvatar";
        public static final String FILE_UPLOAD = Domain.PHP+"fileupload/upload";
        public static final String GCM_TOKEN = Domain.PHP+"user/update_gcmtoken";
        public static final String INVITE_SHARING = Domain.PHP+"user/getInviteSharing";
        public static final String USE_INVITE_CODE = Domain.PHP+"user/useInviteCode";

        public static final String AWARD_RESULT = Domain.PHP+"productItem/award";
        public static final String INIT_ORDER = Domain.PHP+"payment/initOrder/?pay=";
        public static final String RESULT_ORDER = Domain.PHP+"payment/backendPayResult/?pay=";

        public static final String ORDER_CREATE = Domain.PHP+"payment/initOrder/?pay=";
        public static final String ORDER_RESULT = Domain.PHP+"payment/backendPayResult/?pay=";
        public static final String ORDER_CREATE2 = Domain.PHP+"payment/initOrder/?pay=BluePay";
        public static final String ORDER_RESULT2 = Domain.PHP+"payment/backendPayResult/?pay=BluePay";
        public static final String BUY = Domain.PHP+"order/addorder";
        public static final String PARTICPATE_HISTORY = Domain.PHP+"product/getparticpatehistory";
        public final static String PRODUCT_LIST = Domain.C+"product/list";
        public final static String LOGIN_URL= Domain.C2+"user/login";
        public final static String REGISTER_URL= Domain.C2+"user/register";
        public final static String TOKEN_VERIFY_URL= Domain.C2+"user/verify_token";
        public final static String BIND_URL= Domain.C2+"user/bind";


        public static final String PREPAY = Domain.PHP+"payment/prepay";
        public static final String FINISHNEWBIEGUIDE = Domain.PHP+"user/setUserFinishNewbieGuide";

        public static final String VERIFICATION = Domain.USERCENTERPHP+"user/sendSMS";
        public static final String AWAITINGACCEPT = Domain.PHP+"award/getAwaitingAccept";

        public static final String MESSAGECENTER = Domain.PHP+"notice/messageCenter";

        //HL
        public static final String SHOW_ITEMS =  Domain.PHP+"showitems";
        public static final String SHOW_ITEM =  Domain.PHP+"showitem";
        public static final String ADDRESS_ITEMS = Domain.PHP+"address/getlist";
        public static final String ADDRESS_ADD = Domain.PHP+"address/add";
        public static final String ADDRESS_DELETE = Domain.PHP+"address/del";
        public static final String ADDRESS_MODIFY = Domain.PHP+"address/modify";
        public static final String SHARE_OVER_ADD = Domain.PHP+"shareover/add";
        public static final String SHARE_OVER_LIST = Domain.PHP+"shareover/getListNew";
        public static final String SHARE_PRODUCT_LIST = Domain.PHP+"shareover/getListProduct";
        public static final String SHARE_USER_LIST = Domain.PHP+"shareover/getListUser";
        public static final String SHARE_ZAN =  Domain.PHP+"shareover/approval";
        public static final String FeedBack = Domain.PHP+"index/feedback";
        public static final String COUPONS = Domain.PHP+"coupon/mycoupon";
    }
    //http 返回码
    public static class CODE{
        public final static int NET_ERROR = -0x01;
        public final static int OK = 0x00;
        public final static int REOK = -2001;
        public final static int FAIL = 0x02;
        public final static int TOKEN_ERROR = 0x03;
        public final static int VERIFICATION_ERROR = 101201;
        public final static int CODE_REPEAT_BIND = 101401;//重复绑定 同一个uid重复绑定
        public final static int CODE_ALREADY_BIND  = 101402;//第三方账号被其他设备(uid)绑定
        public final static int CODE_PWD_ERROR = 101102; //验证码（密码）错误
        public final static int VERIFICATION_COUNT_LIMIT = 100507;//验证码次数限制
    }
    //http 请求头
    public static class Header{
        public static final String CIPHER_SPEC = "x-cipher-spec";
        public static final String PACKAGE_NAME = "package-name";
        public static final String PACKAGE_VER = "package-ver";
        public static final String TOKEN = "token";
        public static final String UID = "uid";
        public static final String COUNTRY = "country";
        public static final String LANGUAGE = "lang";
        public static final String DEVICE_ID = "device_id";
        public static final String APP_ID = "app_id";
        public static final String PRD_ID = "prd_id";

    }

    public static class AppId{
        public static final String APP_ID = Environment.APP_ID;
    }

    public static class ProducteId{
        public static final String PRD_ID = Environment.PRD_ID;
    }
    //http 加密信息
    public static class Secure{
        public static final String AES_KEY = "8vA6au9Z";
//        public static final String AES_KEY = "DFJK#$i@(@rphTuIfp";
//        public static final String AES_KEY = "DFJK#$i@(@";
    }

    //购买错误码
    public static class BUY_CODE{
        public final static int MONEY_NOT_ENOUGH = -1001;//余额不足
        public final static int SOLDOUT = -1002;//
        public final static int NOPRODUCT = -1003;//
        public final static int TIMES_NOT_ENOUGH = -1004;//份数不足
    }

    //商品状态
    public static class PRODUCT_STATE{
        public final static int ONSALE = 0x01;//售卖中
        public final static int SOLDOUT = 0x02;//售卖完
        public final static int AWARDING = 0x03;//开奖中
        public final static int AWARD = 0x04;//已开奖
        public final static int SHARD = 0x05;//已晒单
    }

    //订单状态
    public static class ORDER_STATE{
//        public final static int INIT = 0x00;//待领奖
        public final static int INIT = 0x01;//待发货
        public final static int SHIPPED = 0x02;//已发货
        public final static int RECEIVED = 0x03;//已收货
        public final static int COMPLETE = 0x04; //订单已完成
        public final static int EXCEPTION = 0x05; //发货过程出现异常
        public final static int FAIL= 0x06; //收货失败
        public final static int DEL = -1;// 删除
    }

    //用户参与状态，
    public static class USER_PARTICIPATE_STATUS{
        public final static int ONSALE = 0x01;//进行中
        public final static int AWARDED = 0x02;//已开奖
        public final static int WIN = 0x03;//中奖记录
        public final static int ALL = -0x03;//全部参与
    }

    //商品tag
    public static class TAG{
        public final static int ALL = 100;
        public final static int TEN = 101;
        public final static int HUNDRED = 103;
    }
    public static class UserType{
        public final static String GENERAL = "0";
        public final static String ANONYMOUS ="1";
        public final static String FACEBOOK = "2";
        public final static String GOOGLE = "3";
        public final static String TWITTER = "4";
        public final static String PHONE = "10";
    }

    public static class Payment{
        public final static int BLUEPAY_BILLING = 1;
        public final static int BLUEPAY_SMS = 31;
        public final static int BLUEPAY_TRUEMONEY = 32;
        public final static int BLUEPAY_12CALL = 33;
        public final static int BLUEPAY_HAPPY = 34;
        public final static int BLUEPAY_BANK = 35;
        public final static int BLUEPAY_LINE = 36;
        //越南
        public final static int BLUEPAY_VIETTEL = 41;
        public final static int BLUEPAY_VINAPHONE= 42;
        public final static int BLUEPAY_MOBIFONE = 43;
        public final static int BLUEPAY_VTC = 44;
        public final static int BLUEPAY_HOPE = 45;
        public final static int BLUEPAY_VIETNAM_SMS = 46;
        //印尼
        public final static int BLUEPAY_MOGPLAY = 51;
        public final static int BLUEPAY_OFFLINE_ATM = 52;
        public final static int BLUEPAY_OFFLINE_OTC = 53;
        public final static int BLUEPAY_INDONESIA_SMS = 54;

        //越南googlepay
        public final static int GOOGLEPAY = 55;
    }
}
