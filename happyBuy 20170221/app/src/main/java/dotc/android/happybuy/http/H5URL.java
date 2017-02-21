package dotc.android.happybuy.http;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/5/4.
 */
public class H5URL {

    private static String mCountryCode;
//    private static String mLang;

    static {
        mCountryCode = AppUtil.getMetaData(GlobalContext.get(),"country");
//        HBLog.d("--------------"+mCountryCode);
    }

    public static String get(String url){
        if(!url.contains(HttpProtocol.Header.COUNTRY)){
            url = appendParam(url,HttpProtocol.Header.COUNTRY,mCountryCode);
        } else if(url.contains("{country}")){
            url = url.replace("{country}",mCountryCode);
        }

        if(!url.contains(HttpProtocol.Header.LANGUAGE)){
            url = appendParam(url,HttpProtocol.Header.LANGUAGE, Languages.getInstance().getLanguage());
        } else if(url.contains("{lang}")){
            url = url.replace("{lang}",Languages.getInstance().getLanguage());
        }
        return url;
    }

    private static String appendParam(String url,String key,String value){
        if(url.contains("?")){
            if(url.endsWith("?")){
                return url+key+"="+value;
            } else {
                return url+"&"+key+"="+value;
            }
        } else {
            return url+"?"+key+"="+value;
        }
    }

}
