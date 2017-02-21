package dotc.android.happybuy.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huangli on 16/4/11.
 * 泰国测试手机 66617532988
 */
public class FormatVerifyUtil {
    /**
     * 验证手机格式 (规则针对泰国)
     *
     * 幻灵石:
     * 泰国手机号长度最多11个，如果是11位时，需要校验前两位是66
     * 最短10位，10位时需要校验第一位是0。
     * 幻灵石:
     * 当10位且第一位 是0时，需要将0替换成66(泰国国家码)然后进行充值。
     */
    public static boolean isMobileNO(Context context,String mobiles) {
        String name;
        if(getMetaData(context,"country").equals("vn")){
             name="0"+mobiles;
            return FormatVerifyUtil.isVnMobileNO(name);
        }else{

            return FormatVerifyUtil.isThMobileNO(mobiles);
        }

    }

    public static String checkMobileNumber(Context context,String mobiles){
        String name;
        if(getMetaData(context,"country").equals("vn")){
             name="0"+mobiles;
            return name;
        }else{
            return mobiles;
        }
    }
    public static boolean isVnMobileNO(String mobiles) {
        if (mobiles.equals("") || mobiles.length() > 11 || mobiles.length() < 10){
            return false;
        }else {
            return mobiles.charAt(0) == '0';
        }
    }
    public static boolean isCnMobileNO(String mobiles) {
        if (mobiles.equals("") || mobiles.length() > 13 || mobiles.length() < 13){
            return false;
        }else {
            return !(mobiles.charAt(0) != '8' || mobiles.charAt(1) != '6');
        }
    }

    public static boolean isThMobileNO(String mobiles) {
        if (mobiles.equals("") || mobiles.length() > 11 || mobiles.length() < 10){
            return false;
        }else {
            if (mobiles.length() == 10 && mobiles.charAt(0) != '0'){
                return false;
            }
            if (mobiles.length() == 11 && (mobiles.charAt(0) != '6' || mobiles.charAt(1) != '6')){
                return false;
            }
            return true;
        }
    }

    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断邮箱是否合法
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)){
            return false;
        }
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isEamilAccountFormatOK(Context context,EditText accountEdit,String tips){
        String account = accountEdit.getText().toString();
        if(isEmail(account)){
            return true;
        }else{
            ToastUtils.showShortToast(context, tips);
            return false;
        }
    }

    public static boolean isMobileAccountFormatOK(Context context,EditText accountEdit,String tips){
        String account = accountEdit.getText().toString();
        if(isMobileNO(context,account)){
            return true;
        }else{
            ToastUtils.showShortToast(context, tips);
            return false;
        }
    }

    public static boolean isEditNull(Context context,EditText edit,String tips){
        String str =edit.getText().toString();
        if(str == null || str.equals("")){
            ToastUtils.showShortToast(context, tips);
            return false;
        }
        return true;
    }

    public static boolean isAccountFormatOK(Context context,EditText accountEdit,String tips){
        String account = accountEdit.getText().toString();
        if(isEmail(account) || isMobileNO(context,account)){
            return true;
        }else{
            ToastUtils.showShortToast(context, tips);
            return false;
        }
    }
}
