package dotc.android.happybuy.modules.schema;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.config.ConfigManager;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.active.H5ActiveActivity;
import dotc.android.happybuy.modules.boutique.BoutiqueActivity;
import dotc.android.happybuy.modules.coupon.RedPacketActivity;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.prize.AwardProductActivity;
import dotc.android.happybuy.modules.recharge.TopupActivity;
import dotc.android.happybuy.modules.setting.SettingActivity;
import dotc.android.happybuy.modules.setting.invite.EnterInviteCodeActivity;
import dotc.android.happybuy.modules.setting.invite.InviteWinCoinsActivity;
import dotc.android.happybuy.modules.show.ShowListActivity;
import dotc.android.happybuy.modules.splash.SplashActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.activity.WebActivity;
import dotc.android.happybuy.util.AesUtil;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.Md5Util;
import dotc.android.happybuy.util.RandomStringUtils;

/**
 * Created by wangjun on 16/12/15.
 */

public class SchemeProcessor {

    private final static String TAG = SchemeProcessor.class.getSimpleName();
    private final static String WEB_TYPE_SYSTEM = "system";

    public static boolean handle(Context context,String clickUrl){
        HBLog.d(TAG, "handle " + clickUrl);
        if(TextUtils.isEmpty(clickUrl)){
            return false;
        }
        String scheme = "onebuy://";
        if(clickUrl.startsWith(scheme)){
            String command = clickUrl.replace(scheme, "");
            return dispatchEvent(context,command);
        }
        if(Patterns.WEB_URL.matcher(clickUrl).matches()){
            return openAppBrowser(context,clickUrl);
        }
        if(clickUrl.startsWith("http://")||clickUrl.startsWith("https://")){
            return openAppBrowser(context,clickUrl);
        }
        return false;
    }

    public static boolean handle(Context context,String scheme,String dataString){
        HBLog.d(TAG, "handle scheme:" + scheme+" dataString:"+dataString);
        if(TextUtils.isEmpty(dataString)){
            context.startActivity(new Intent(context, SplashActivity.class));
            return true;
        } else {
            String command = dataString.replace(scheme + "://", "");
            return dispatchEvent(context,command);
        }
    }

    private static boolean openAppBrowser(Context context,String url){
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(url));
        if(url.contains("help_center")){
            intent.putExtra(WebActivity.EXTRA_TYPE_KEY,WebActivity.TYPE_FEEDBACK);
        }
        context.startActivity(intent);
        return true;
    }

    private static boolean openSystemBrowser(Context context,String url){
        try{
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e){}
        return false;
    }

    private static boolean dispatchEvent(Context context,String command){
        if (command.startsWith("detail?")) {
            String content = command.substring("detail?".length());
            return handleDetailEvent(context,content);
        } else if (command.startsWith("web?")) {
            String content = command.substring("web?".length());
            return handleWebEvent(context,content);
        } else if (command.startsWith("h5?")) {
            String content = command.substring("h5?".length());
            return handleH5ActiveEvent(context,content);
        } else if (command.startsWith("tag?")) {
            String content = command.substring("tag?".length());
            return handleGoodsWithTagEvent(context,content);
        } else if (command.startsWith("prize?")) {
            String content = command.substring("prize?".length());
            return handlePrizeEvent(context,content);
        } else if (command.startsWith("recharge")) {
            return handleRechargeEvent(context,null);
        } else if (command.startsWith("setting")) {
            return handleSettingEvent(context,null);
        } else if (command.startsWith("coupon")) {
            String content = command.substring("coupon".length());
            return handleCouponEvent(context,content);
        } else if (command.startsWith("latestTimeline")) {
            return handleLatestTimelineEvent(context,null);
        } else if (command.startsWith("inviteFriends")) {
            return handleInviteFriendsEvent(context,null);
        } else if (command.startsWith("inputInviteCode")) {
            return handleInputInviteCodeEvent(context,null);
        } else if (command.startsWith("timeline?")) {
            String content = command.substring("timeline?".length());
            return handleTimelinEvent(context,content);
        } else if (command.startsWith("goto?")) {
            String content = command.substring("goto?".length());
            return handleGotoEvent(context,content);
        }
        return false;
    }

    private static boolean handleDetailEvent(Context context,String content){
        HBLog.d(TAG, "handleDetailEvent " + content);
        Map<String,String> paramsMap = readArgsFromScheme(content);
        Intent intent = new Intent(context, GoodsDetailActivity.class);
        intent.putExtra(GoodsDetailActivity.EXTRA_ACTIVITY_FROM,GoodsDetailActivity.ACTIVITY_FROM_ACTIVE);
        intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,paramsMap.get("product_id"));
        intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ITEM_ID,paramsMap.get("product_item_id"));
        context.startActivity(intent);
        return true;
    }

    private static boolean handleWebEvent(Context context, String content){
        HBLog.d(TAG, "handleWebEvent " + content);
        Map<String,String> paramsMap = readArgsFromScheme(content);
        String type = paramsMap.get("type");
        if(WEB_TYPE_SYSTEM.equals(type)){
            String url = urlDecode(paramsMap.get("url"));
            return openSystemBrowser(context,url);
        } else {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra(WebActivity.EXTRA_TITLE, paramsMap.get("title"));
            intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(urlDecode(paramsMap.get("url"))));
            context.startActivity(intent);
        }
        return true;
    }

    private static boolean handleH5ActiveEvent(Context context, String content){
        HBLog.d(TAG, "handleH5ActiveEvent " + content);
        Map<String,String> paramsMap = readArgsFromScheme(content);
        String url = H5URL.get(urlDecode(paramsMap.get("url")));
        Intent intent = new Intent(context, H5ActiveActivity.class);
        intent.putExtra(H5ActiveActivity.EXTRA_TITLE, paramsMap.get("title"));
        intent.putExtra(H5ActiveActivity.EXTRA_URL, appendPrivateData(url));
        context.startActivity(intent);
        return true;
    }

    private static String appendPrivateData(String url){
        StringBuilder sb = new StringBuilder(url);
        if(url.contains("?")){
            sb.append("&");
        } else {
            sb.append("?");
        }
        sb.append("data=").append(encryPrivateData(getPrivateData()));
//        sb.append(HttpProtocol.Header.TOKEN).append("=");
//        sb.append(PrefUtils.getString(PrefConstants.Token.TOKEN, ""));
//        sb.append("&").append(HttpProtocol.Header.UID).append("=");
//        sb.append(PrefUtils.getString(PrefConstants.UserInfo.UID, ""));
//        sb.append("&").append(HttpProtocol.Header.APP_ID).append("=");
//        sb.append(HttpProtocol.AppId.APP_ID);
//        sb.append("&").append(HttpProtocol.Header.PRD_ID).append("=");
//        sb.append(HttpProtocol.ProducteId.PRD_ID);
//        sb.append("&").append(HttpProtocol.Header.COUNTRY).append("=");
//        sb.append(AppUtil.getMetaData(GlobalContext.get(),"country"));
//        sb.append("&").append(HttpProtocol.Header.LANGUAGE).append("=");
//        sb.append(Languages.getInstance().getLanguage());
//        sb.append("&").append(HttpProtocol.Header.PACKAGE_NAME).append("=");
//        sb.append(GlobalContext.get().getPackageName());
//        sb.append("&").append(HttpProtocol.Header.PACKAGE_VER).append("=");
//        sb.append(AppUtil.getVersionCode(GlobalContext.get()));
        return sb.toString();
    }

    private static String encryPrivateData(String data){
        String randomKey = RandomStringUtils.getRandomString(8);
        String realKey = Md5Util.md5Hex(randomKey + HttpProtocol.Secure.AES_KEY);
        byte[] binaryKey = AesUtil.hex2byte(realKey);
        String encryptContent = AesUtil.encrypt(data, binaryKey);
        return randomKey + encryptContent;
    }

    private static String getPrivateData(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HttpProtocol.Header.TOKEN, PrefUtils.getString(PrefConstants.Token.TOKEN, ""));
            jsonObject.put(HttpProtocol.Header.UID, PrefUtils.getString(PrefConstants.UserInfo.UID, ""));
            jsonObject.put(HttpProtocol.Header.LANGUAGE, Languages.getInstance().getLanguage());
            jsonObject.put(HttpProtocol.Header.COUNTRY, AppUtil.getMetaData(GlobalContext.get(),"country"));
            jsonObject.put(HttpProtocol.Header.DEVICE_ID, AppUtil.getDeviceId(GlobalContext.get()));
            jsonObject.put(HttpProtocol.Header.PRD_ID,HttpProtocol.ProducteId.PRD_ID);
            jsonObject.put(HttpProtocol.Header.APP_ID,HttpProtocol.AppId.APP_ID);
            jsonObject.put(HttpProtocol.Header.PACKAGE_NAME,GlobalContext.get().getPackageName());
            jsonObject.put(HttpProtocol.Header.PACKAGE_VER,AppUtil.getVersionCode(GlobalContext.get()));
        } catch (Exception e){}

        return jsonObject.toString();
    }

    private static boolean handleGoodsWithTagEvent(Context context,String content){
        HBLog.d(TAG, "handleGoodsWithTagEvent " + content);
        Map<String,String> paramsMap = readArgsFromScheme(content);
        Intent intent = new Intent(context, BoutiqueActivity.class);
        intent.putExtra(BoutiqueActivity.EXTRA_CATEGORY_ID, paramsMap.get("id"));
        intent.putExtra(BoutiqueActivity.EXTRA_CATEGORY_NAME, paramsMap.get("name"));
        context.startActivity(intent);
        return true;
    }

    private static boolean handlePrizeEvent(Context context,String content){
        HBLog.d(TAG, "handlePrizeEvent " + content);
        Map<String,String> paramsMap = readArgsFromScheme(content);
        Intent intent = new Intent(context, AwardProductActivity.class);
        intent.putExtra(AwardProductActivity.EXTRA_PRODUCT_ITEM_ID, paramsMap.get("product_item_id"));
        context.startActivity(intent);
        return true;
    }

    private static boolean handleRechargeEvent(Context context,String content){
        HBLog.d(TAG, "handleRechargeEvent ");
        Intent intent = new Intent(context, TopupActivity.class);
        intent.putExtra(TopupActivity.EXTRA_ACTIVITY_FROM,TopupActivity.ACTIVITY_FROM_ACTIVE);
        context.startActivity(intent);
        return true;
    }

    private static boolean handleSettingEvent(Context context,String content){
        HBLog.d(TAG, "handleSettingEvent " + content);
        context.startActivity(new Intent(context, SettingActivity.class));
        return true;
    }

    private static boolean handleCouponEvent(Context context,String content){
        HBLog.d(TAG, "handleCouponEvent " + content);
        if(content.startsWith("?")){
            Map<String,String> paramsMap = readArgsFromScheme(content.substring(1));
            Intent intent = new Intent(context,RedPacketActivity.class);
            intent.putExtra(RedPacketActivity.EXTRA_POSITION,Integer.parseInt(paramsMap.get("position")));
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context,RedPacketActivity.class);
            context.startActivity(intent);
        }
        return true;
    }

    private static boolean handleLatestTimelineEvent(Context context,String content){
        HBLog.d(TAG, "handleDetailEvent " + content);
        String url = ConfigManager.get(context).getH5Config().lastAnnouncement;
        Intent intent = new Intent(context,WebActivity.class);
        intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(url));
        intent.putExtra(WebActivity.EXTRA_TITLE,context.getString(R.string.last_award));
        context.startActivity(intent);
        return true;
    }

    private static boolean handleInviteFriendsEvent(Context context,String content){
        HBLog.d(TAG, "handleDetailEvent " + content);
        Intent intent = new Intent(context,InviteWinCoinsActivity.class);
        context.startActivity(intent);
        return true;
    }

    private static boolean handleInputInviteCodeEvent(Context context,String content){
        HBLog.d(TAG, "handleDetailEvent " + content);
        Intent intent = new Intent(context,EnterInviteCodeActivity.class);
        context.startActivity(intent);
        return true;
    }


    private static boolean handleTimelinEvent(Context context,String content){
        HBLog.d(TAG, "handleTimelinEvent " + content);
        Map<String,String> paramsMap = readArgsFromScheme(content);
        Intent intent = new Intent(context, ShowListActivity.class);
        intent.putExtra("from_uid",paramsMap.get("uid"));
        intent.putExtra("product_id",paramsMap.get("product_id"));
        context.startActivity(intent);
        return true;
    }

    //----
    private static boolean handleGotoEvent(Context context,String content){
        HBLog.d(TAG, "handleGotoEvent " + content);
        Map<String,String> paramsMap = readArgsFromScheme(content);
        HBLog.d(TAG + " onCreate param map " + paramsMap);
        String classPath = paramsMap.get("class");
        String extraString = paramsMap.get("extra");
        Intent intent = new Intent();
        intent.setClassName(context, classPath);
        Map<String,Object> extraMap = parseExtra(extraString);
        for(String key:extraMap.keySet()){
            Object value = extraMap.get(key);
            if(value instanceof Integer){
                intent.putExtra(key,(int) value);
            } else if(value instanceof Boolean){
                intent.putExtra(key,(Boolean) value);
            } else if(value instanceof String){
                intent.putExtra(key,(String) value);
            }
        }
        context.startActivity(intent);
        return true;
    }

    private static Map<String,String> readArgsFromScheme(String content){
        Map<String,String> paramsMap = new HashMap<>();
        String[] arr = content.split("&");
        for(String string:arr){
            int position = string.indexOf("=");
            paramsMap.put(string.substring(0,position),string.substring(position+1));
//            String[] keyValue = string.split("=");
//            paramsMap.put(keyValue[0],keyValue[1]);
        }
        return paramsMap;
    }

    private static Map<String,Object> parseExtra(String jsonString){
        Map<String,Object> map = new HashMap<>();
        try {
            if(!TextUtils.isEmpty(jsonString)){
                HBLog.d(TAG+" parseExtra srcString:"+jsonString);
                String fixedString = URLDecoder.decode(jsonString,"UTF-8");
                HBLog.d(TAG+" parseExtra fixedString:"+fixedString);
                JSONObject jsonObject = new JSONObject(fixedString);
                Iterator<String> it = jsonObject.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    Object value = jsonObject.get(key);
                    map.put(key,value);
//                    if(value instanceof String){
//                        map.put(key, URLDecoder.decode((String)value,"UTF-8"));
//                    } else {
//                        map.put(key,value);
//                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            HBLog.w(TAG+" parseExtra error:"+e.getMessage());
        }
        return map;
    }

    private static String urlDecode(String url){
        try {
            return URLDecoder.decode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
