package dotc.android.happybuy.config.local;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.result.PojoBillingConfig;
import dotc.android.happybuy.http.result.PojoBluePayConfig;
import dotc.android.happybuy.http.result.PojoCategory;
import dotc.android.happybuy.http.result.PojoCategoryList;
import dotc.android.happybuy.http.result.PojoConfig;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.http.result.PojoPayConfig;
import dotc.android.happybuy.http.result.PojoPayItems;
import dotc.android.happybuy.http.result.PojoRechargeConfig;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by wangjun on 16/6/7.
 */
public class RechargeConfig {

    static final String TAG = RechargeConfig.class.getSimpleName();
    public List<PojoPayItems> billingConfig;
    public List<PojoPayItems> smsConfig;
    public List<PojoPayItems> bankConfig;
    public List<PojoPayItems> aisConfig;
    public List<PojoPayItems> dtacConfig;
    public List<PojoPayItems> truemoneyConfig;
    public List<PojoPayItems> linepayConfig;

    //越南
    public List<PojoPayItems> viettelConfig;
    public List<PojoPayItems> vinaphoneConfig;
    public List<PojoPayItems> mobifoneConfig;
    public List<PojoPayItems> vtcConfig;
    public List<PojoPayItems> hopeConfig;
    public List<PojoPayItems> vietnamSmsConfig;
    public List<PojoPayItems> googlePayConfig;
    //印尼
    public List<PojoPayItems> mogplayConfig;
    public List<PojoPayItems> atmConfig;
    public List<PojoPayItems> otcConfig;
    public List<PojoPayItems> indonesiaSmsConfig;



    public List<PojoPay> pojoPays;


    public static RechargeConfig newLocalConfig(Context context){
        String cache = PrefUtils.getString(PrefConstants.Config.RECHARGE_CONFIG, "");
        if(!TextUtils.isEmpty(cache)) {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray Jarray = parser.parse(cache).getAsJsonArray();

            List<PojoPay> pojoPays = new ArrayList<>();
            for(JsonElement obj : Jarray ){
                PojoPay pojoPay = gson.fromJson(obj , PojoPay.class);
                if(pojoPay.items!=null){
                        pojoPays.add(pojoPay);
                }
            }
            RechargeConfig config = new RechargeConfig();
            config.pojoPays= pojoPays;
            config.smsConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_SMS);
            config.billingConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_BILLING);
            config.bankConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_BANK);
            config.truemoneyConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_TRUEMONEY);
            config.dtacConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_HAPPY);
            config.linepayConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_LINE);
            config.aisConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_12CALL);

            config.viettelConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_VIETTEL);
            config.vinaphoneConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_VINAPHONE);
            config.mobifoneConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_MOBIFONE);
            config.vtcConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_VTC);
            config.hopeConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_HOPE);
            config.vietnamSmsConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_VIETNAM_SMS);
            config.googlePayConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.GOOGLEPAY);


            config.mogplayConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_MOGPLAY);
            config.atmConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_OFFLINE_ATM);
            config.otcConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_OFFLINE_OTC);
            config.indonesiaSmsConfig=transfer2PojoPay(pojoPays, HttpProtocol.Payment.BLUEPAY_INDONESIA_SMS);

            return config;
        }
        return newDefaultConfig(context);
    }

    private static RechargeConfig newDefaultConfig(Context context){
//        HBLog.d(TAG+" newDefaultConfig");
        RechargeConfig config = new RechargeConfig();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(context.getAssets().open("config/recharge.json"), "UTF-8");
            Gson gson = new Gson();
            PojoPayConfig pojoPayConfig = gson.fromJson(reader, PojoPayConfig.class);
            config.pojoPays=pojoPayConfig.list;
            config.smsConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_SMS);
            config.billingConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_BILLING);
            config.bankConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_BANK);
            config.truemoneyConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_TRUEMONEY);
            config.dtacConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_HAPPY);
            config.linepayConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_LINE);
            config.aisConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_12CALL);

            config.viettelConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_VIETTEL);
            config.vinaphoneConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_VINAPHONE);
            config.mobifoneConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_MOBIFONE);
            config.vtcConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_VTC);
            config.hopeConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_HOPE);
            config.vietnamSmsConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_VIETNAM_SMS);
            config.googlePayConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.GOOGLEPAY);

            config.mogplayConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_MOGPLAY);
            config.atmConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_OFFLINE_ATM);
            config.otcConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_OFFLINE_OTC);
            config.indonesiaSmsConfig=transfer2PojoPay(config.pojoPays, HttpProtocol.Payment.BLUEPAY_INDONESIA_SMS);



            /*PojoRechargeConfig categoryList = gson.fromJson(reader, PojoRechargeConfig.class);
            config.billingConfig = categoryList.billing;
            config.smsConfig = categoryList.bluepay_sms;
            config.bankConfig = categoryList.bluepay_bank;
            config.truemoneyConfig = categoryList.bluepay_truemoney;
            config.aisConfig = categoryList.bluepay_12call;
            config.dtacConfig = categoryList.bluepay_happy;
            config.linepayConfig = categoryList.bluepay_linepay;*/
        } catch (Exception e) {
            HBLog.d(TAG + " newDefaultConfig error:" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return config;
    }

    public static void saveToLocal(List<PojoPay> pojoPay){
        Gson gson = new Gson();
        String jsonString = gson.toJson(pojoPay);
        HBLog.d(TAG+" saveToLocal "+jsonString);
        if(!TextUtils.isEmpty(jsonString)){
            PrefUtils.putString(PrefConstants.Config.RECHARGE_CONFIG, jsonString);
        }
    }

    private static List<PojoPayItems> transfer2PojoPay(List<PojoPay> pojoPays,int type){
        List<PojoPayItems> list = new ArrayList<>();
        for(PojoPay pojoPay:pojoPays){
            if(pojoPay.id.equals(String.valueOf(type))){
                list=pojoPay.items;
            }
        }
        return list;
    }


}
