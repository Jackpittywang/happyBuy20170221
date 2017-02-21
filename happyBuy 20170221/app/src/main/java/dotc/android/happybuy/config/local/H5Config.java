package dotc.android.happybuy.config.local;

import android.text.TextUtils;

import com.google.gson.Gson;

import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.result.PojoH5;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by wangjun on 16/5/5.
 */
public class H5Config {
    private static String TAG = H5Config.class.getSimpleName();
    public String lastAnnouncement;
    public String helpCenter;
    public String agreement;
    public String calculatingFormula;
    public String rechargeInstructions;
    private H5Config(){}

    public static H5Config newLocalConfig(){
        String cache = PrefUtils.getString(PrefConstants.Config.H5_CONFIG,"");
        if(!TextUtils.isEmpty(cache)){
            H5Config config = new H5Config();
            Gson gson = new Gson();
            PojoH5 pojoH5 = gson.fromJson(cache,PojoH5.class);
            config.lastAnnouncement = pojoH5.last_announcement;
            config.helpCenter = pojoH5.help_center;
            config.agreement = pojoH5.agreement;
            config.calculatingFormula = pojoH5.calculating_formula;
            config.rechargeInstructions = pojoH5.recharge_instructions;
            return config;
        }
        return newDefaultConfig();

    }

    private static H5Config newDefaultConfig(){
        H5Config config = new H5Config();
        config.lastAnnouncement = H5URL.get(HttpProtocol.H5.LAST_ANNOUNCEMENT);
        config.helpCenter = H5URL.get(HttpProtocol.H5.HELP_CENTER);
        config.agreement = H5URL.get(HttpProtocol.H5.AGREEMENT);
        config.calculatingFormula = H5URL.get(HttpProtocol.H5.CALCULATING_FORMULA);
        config.rechargeInstructions = H5URL.get(HttpProtocol.H5.RECHARGE_INSTRUCTION);
        return config;
    }

    public static void saveToLocal(PojoH5 pojoH5){
        Gson gson = new Gson();
        String jsonString = gson.toJson(pojoH5);
        HBLog.d(TAG+" saveToLocal "+jsonString);
        if(!TextUtils.isEmpty(jsonString)){
            PrefUtils.putString(PrefConstants.Config.H5_CONFIG, jsonString);
        }
    }

}
