package dotc.android.happybuy.analytics;

/**
 * Created by zhanqiang.mei on 2016/4/18.
 */
public class AnalyticsEvents {

    public static class Login {
        public static final String Login_FB = "Login_FB";
        public static final String Login_FB_success = "Login_FB_success";
        public static final String Login_FB_fail = "Login_FB_fail";
        public static final String Login_G = "Login_G";
        public static final String Login_G_success = "Login_G_success";
        public static final String Login_G_fail = "Login_G_fail";
    }
    //充值
    public static class Recharge {
        public static final String Click_Recharge = "Click_Recharge";
//        public static final String Recharge_Success = "Recharge_Success";
        public static final String Click_Recharge_Directions = "Click_Recharge_Directions";

        //每种支付渠道的点击 和 成功 失败
        public static final String Pay_sms = "Pay_sms";
        public static final String Pay_sms_success = "Pay_sms_success";
        public static final String Pay_sms_fail = "Pay_sms_fail";
        public static final String Pay_google = "Pay_google";
        public static final String Pay_google_success = "Pay_google_success";
        public static final String Pay_google_fail = "Pay_google_fail";

        public static final String Pay_12call = "Pay_12call";
        public static final String Pay_12call_success = "Pay_12call_success";
        public static final String Pay_12call_fail = "Pay_12call_fail";
        public static final String Pay_happy = "Pay_happy";
        public static final String Pay_happy_success = "Pay_happy_success";
        public static final String Pay_happy_fail = "Pay_happy_fail";
        public static final String Pay_true = "Pay_true";
        public static final String Pay_true_success = "Pay_true_success";
        public static final String Pay_true_fail = "Pay_true_fail";
        public static final String Pay_bank = "Pay_bank";
        public static final String Pay_bank_success = "Pay_bank_success";
        public static final String Pay_bank_fail = "Pay_bank_fail";
        public static final String Pay_line = "Pay_line";
        public static final String Pay_line_success = "Pay_line_success";
        public static final String Pay_line_fail = "Pay_line_fail";
        public static final String Pay_unknown = "Pay_unknown";
    }

    public static class UserCenter {
        //个人中心
        public static final String Click_PersonalCenter = "Click_PersonalCenter";
        //设置
        public static final String Click_Settings = "Click_Settings";
        public static final String Click_ServiceAgreement = "Click_ServiceAgreement";
        public static final String Click_About = "Click_About";
        public static final String Click_LogOut = "Click_LogOut";
        //头像
        public static final String Click_Portrait = "Click_Portrait";
        public static final String Portrait_Success = "Portrait_Success";
        public static final String NickName_Success = "NickName_Success";
        public static final String Click_Address = "Click_Address";
        public static final String Add_Address_Edit = "Add_Address_Edit";
        public static final String Delete_Address = "Delete_Address";

        //全部参与
        public static final String Click_AllOrder = "Click_AllOrder";
        public static final String Choose_AllOrder = "Choose_AllOrder";
        public static final String Choose_InProcess = "Choose_InProcess";
        public static final String Choose_Finished = "Choose_Finished";
        public static final String Click_Product = "Click_Product";
        //中奖纪录
        public static final String Click_WinRecord = "Click_WinRecord";
        public static final String Enter_Confirm = "Enter_Confirm";
        public static final String Select_DeliverAddress = "Select_DeliverAddress";
        public static final String Add_Address_Prize = "Add_Address_Prize";
        public static final String Card_Select_Coin = "Card_Select_Coin";
        public static final String Card_Select_Phone = "Card_Select_Phone";
//        public static final String Card_Select_Password = "Card_Select_Password";
        public static final String Click_ToShow = "Click_ToShow";
        public static final String Show_Success = "Show_Success";
        public static final String Click_ShowRecord = "Click_ShowRecord";

        public static final String ClickGuess = "ClickGuess";
        public static final String ClickBuyIt = "ClickBuyIt";

    }
    //搜索
    public static class ProductSearch{
        public static final String Click_Bar_Search = "Click_Bar_Search";
        public static final String Click_Search_Butten = "Click_Search_Butten";
        public static final String Click_Search_HotWord = "Click_Search_HotWord";
    }
    public static class DrawerCategory{
        public static final String Click_Drawer = "Click_Drawer";
        public static final String Click_Drawer_Item = "Click_Drawer_Item";
    }
    public static class HomeFragment{
        public static final String Click_Home_Button = "Click_Home_Button";
        public static final String Click_Banner_Main = "Click_Banner_Main";
        public static final String Click_Banner_Small = "Click_Banner_Small";
    }
    public static class Coupons{
        public static final String Click_Coupon = "Click_Coupon";
        public static final String Click_Coupon_Available = "Click_Coupon_Available";
        public static final String Click_Coupon_Distributed = "Click_Coupon_Distributed";
        public static final String Click_Coupon_Used = "Click_Coupon_Used";
        public static final String Click_Buy_Coupon = "Click_Buy_Coupon";
        public static final String Select_Buy_Coupon = "Select_Buy_Coupon";
    }

    public static class MainActivity{
        //充值
        public static final String Click_Recharge = "Click_Recharge";
        public static final String Recharge_Success = "Recharge_Success";
        //最新揭晓
        public static final String Click_Winner = "Click_Winner";
        public static final String Click_Product = "Click_Product";
        //晒单
        public static final String Click_Show = "Click_Show";
        public static final String Click_User_Details="Click_User_Details";
        public static final String Click_show_details="Click_show_details";

        //帮助
        public static final String Click_Help = "Click_Help";
        public static final String Click_Heip_Guarantee = "Click_Heip_Guarantee";
        public static final String Click_Help_Deliver = "Click_Help_Deliver";
        public static final String Click_Help_New = "Click_Help_New";
        public static final String Click_Help_Email = "Click_Help_Email";
    }
    public static class MainTab{
        public static final String Home_Page_Show = "Home_Page_Show";
        public static final String Category_Page_Show = "Category_Page_Show";
        public static final String Show_Page_Show = "Show_Page_Show";
        public static final String Me_Page_Show = "Me_Page_Show";
    }
    public static class PrizeNotifation{
        //中奖通知
        public static final String Click_Award_Notification = "Click_Award_Notification";
    }
    public static class ProductDetail{
        //商品详情
        public static final String Show_Latest_Details = "Show_Latest_Details";
        public static final String Show_Details = "Show_Details";
        public static final String Click_Product_Details = "Click_Product_Details";
        public static final String Click_Show = "Click_Show";
        public static final String Click_Past = "Click_Past";
        public static final String Click_Product = "Click_Product";
        public static final String Click_Lottery_Details = "Click_Lottery_Details";
        public static final String Click_Buy_Details_Has_Open = "Click_Buy_Details_Has_Open";
        public static final String Click_BuyNow = "Click_BuyNow";
        public static final String Click_Buy_Details_From_History = "Click_Buy_Details_From_History";
        public static final String Click_User_Details = "Click_User_Details";
    }
    public static class Tab{
        //全部商品
        public static final String Click_Tab_All = "Click_Tab_All";
        public static final String Click_Product_All = "Click_Product_All";
        public static final String Click_BuyNow_All = "Click_BuyNow_All";
        //十元区
        public static final String Click_Tab_Ten = "Click_Tab_Ten";
        public static final String Click_Product_Ten = "Click_Product_Ten";
        public static final String Click_BuyNow_Ten = "Click_BuyNow_Ten";
    }
    public static class Other{
        //进入应用
        public static final String Enter_ActivityMain = "Enter_ActivityMain";
        public static final String Enter_ActivityMain_Noti = "Enter_ActivityMain_Noti";
    }

    public static class Awarding{
        //倒计时
        public static final String CountDown = "CountDown";
        public static final String CountDown_Noti = "CountDown_Noti";
    }

    public static class Drawer{
        //抽屉
        public static final String Drawer = "Drawer";
        public static final String Click_Drawer = "Click_Drawer";
    }

    public static class LoginGuide{
        //登录引导
        public static final String Show_Newuser_Dialog = "Show_Newuser_Dialog";
        public static final String Click_Newuser_Yes = "Click_Newuser_Yes";
        public static final String Click_Newuser_No = "Click_Newuser_No";
        public static final String ClickWindowPromotion = "ClickWindowPromotion";
        public static final String ClickWindowClose = "ClickWindowClose";
        public static final String ClickWindowOK = "ClickWindowOK";
    }

    public static class ProductGuide{
        //商品购买引导
        public static final String Start_Guide = "Start_Guide";
        public static final String Finish_Guide = "Finish_Guide";
        public static final String Click_Home_Guide = "Click_Home_Guide";
        public static final String Click_Home_Gray = "Click_Home_Gray";

        public static final String Click_Detail_Guide = "Click_Detail_Guide";
        public static final String Click_Detail_Gray = "Click_Detail_Gray";

        public static final String Click_Select_Guide = "Click_Select_Guide";
        public static final String Click_Select_Gray = "Click_Select_Gray";

        public static final String Click_Pay_Guide = "Click_Pay_Guide";
        public static final String Click_Pay_Gray = "Click_Pay_Gray";
        public static final String Finish_Guide_Error = "Finish_Guide_Error";

        public static final String Click_Close_Guide = "Click_Close_Guide";

    }

    public static class IndexCountDown{
        //首页倒计时
        public static final String Click_Home_Count = "Click_Home_Count";
        public static final String Click_Count_More = "Click_Count_More";
    }

    public static class ProductDetails{
        //商品详情
        public static final String Click_Lottery_Details = "Click_Lottery_Details";
        public static final String Click_Buy_Details = "Click_Buy_Details";
    }
    public static class  LotteryAnimation{
        //开奖动画
        public static final String Close_Count_Dialog = "Close_Count_Dialog";
    }

    public static class  TabLayout{
        //tab
        public static final String Click_Tab_Home = "Click_Tab_Home";
        public static final String Click_Tab_Classification = "Click_Tab_Classification";
        public static final String Click_Tab_Show = "Click_Tab_Show";
        public static final String Click_Tab_Me = "Click_Tab_Me";
    }

    public static class  ClickClassification {
        //分类tab
        public static final String Click_Classification = "Click_Classification";
    }

    public static class PhoneLogin {
        public static final String Click_PhoneNumber = "Click_PhoneNumber";
        public static final String Click_GetCode = "Click_GetCode";
        public static final String Click_Code = "Click_Code";
        public static final String Login_mobile = "Login_mobile";
        public static final String Login_mobile_success = "Login_mobile_success";
        public static final String Login_mobile_fail = "Login_mobile_fail";
    }
    public static class VisitorLogin {
        public static final String Login_visitor = "Login_visitor";
        public static final String Login_visitor_success = "Login_visitor_success";
        public static final String Login_visitor_fail = "Login_visitor_fail";
        public static final String Click_bound = "Click_bound";
        public static final String Bound_success = "Bound_success";
        public static final String Bound_fail = "Bound_fail";
    }

    public static class InviteWinCoins{
        public static final String ClickS_Facebook = "ClickS_Facebook";
        public static final String ClickS_LINE = "ClickS_LINE";
        public static final String ClickS_Messenger = "ClickS_Messenger";
        public static final String ClickS_SMS = "ClickS_SMS";
        public static final String ClickS_Email = "ClickS_Email";
        public static final String ClickS_PasteCode = "ClickS_PasteCode";
        public static final String ClickSettingsShare = "ClickSettingsShare";
        public static final String ClickSettingsCode = "ClickSettingsCode";
        public static final String ClickPromotionYes = "ClickPromotionYes";
    }

    public static class WinDialog{
        public static final String Show_Win_Dialog = "Show_Win_Dialog";
        public static final String Click_Win_Yes = "Click_Win_Yes";
        public static final String Click_Win_Share = "Click_Win_Share";
    }
    public static class CommentDialog{
        public static final String Show_Dialog = "Show_Dialog";
        public static final String Click_Dialog_Yes = "Click_Dialog_Yes";
        public static final String Click_Dialog_No = "Click_Dialog_No";
    }

    public static class Push {
        public static final String Awarding_Receive_Data = "Awarding_Receive_Data";
        public static final String Awarding_Receive_Noti = "Awarding_Receive_Noti";

        public static final String Awarded_Receive_Data = "Awarded_Receive_Data";
        public static final String Awarded_Receive_Noti = "Awarded_Receive_Noti";

        public static final String Coupon_Receive_Data = "Coupon_Receive_Data";
        public static final String Coupon_Receive_Noti = "Coupon_Receive_Noti";
    }

    public static class Notification{
        public static final String Click_CloudNoti = "Click_CloudNoti";
        public static final String Send_NativeNoti = "Send_NativeNoti";
        public static final String Click_NativeNoti = "Click_NativeNoti";
    }

    public static class AppInfo{
        public static final String Line_Install = "Line_Install";
    }

}
