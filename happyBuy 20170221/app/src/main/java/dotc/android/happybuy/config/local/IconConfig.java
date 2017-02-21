package dotc.android.happybuy.config.local;

import android.text.TextUtils;

import com.google.gson.Gson;

import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.result.PojoH5;
import dotc.android.happybuy.http.result.PojoIcons;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by wangzhiyuan on 16/10/28.
 */
public class IconConfig {
    private static String TAG = IconConfig.class.getSimpleName();
    public String topUp;
    public String helpCenter;
    private IconConfig(){}

    public static IconConfig newLocalConfig(){
        String cache = PrefUtils.getString(PrefConstants.Config.ICOINS_CONFIG,"");
        if(!TextUtils.isEmpty(cache)){
            IconConfig config = new IconConfig();
            Gson gson = new Gson();
            PojoIcons pojoIcons = gson.fromJson(cache,PojoIcons.class);
            config.topUp = pojoIcons.top_up;
            config.helpCenter = pojoIcons.help_center;
            return config;
        }
        return newDefaultConfig();

    }

    private static IconConfig newDefaultConfig(){
        IconConfig config = new IconConfig();
        config.topUp ="";
        config.helpCenter ="";
        return config;
    }

    public static void saveToLocal(PojoIcons pojoIcons){
        Gson gson = new Gson();
        String jsonString = gson.toJson(pojoIcons);
        HBLog.d(TAG+" saveToLocal "+jsonString);
        if(!TextUtils.isEmpty(jsonString)){
            PrefUtils.putString(PrefConstants.Config.ICOINS_CONFIG, jsonString);
        }
    }

}
