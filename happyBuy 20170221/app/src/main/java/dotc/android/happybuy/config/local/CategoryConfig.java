package dotc.android.happybuy.config.local;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.http.result.PojoCategory;
import dotc.android.happybuy.http.result.PojoCategoryList;
import dotc.android.happybuy.http.result.PojoH5;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by wangjun on 16/5/10.
 */
public class CategoryConfig {

    static final String TAG = CategoryConfig.class.getSimpleName();
    public List<PojoCategory> categories;


    public static CategoryConfig newLocalConfig(Context context){

        String cache = PrefUtils.getString(PrefConstants.Config.CATEHOTY_CONFIG, "");
        if(!TextUtils.isEmpty(cache)) {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray Jarray = parser.parse(cache).getAsJsonArray();

            List<PojoCategory> categoryList = new ArrayList<>();
            for(JsonElement obj : Jarray ){
                PojoCategory category = gson.fromJson(obj , PojoCategory.class);
                categoryList.add(category);
            }
            CategoryConfig config = new CategoryConfig();
            config.categories = categoryList;
            return config;
        }
        return newDefaultConfig(context);
    }

    private static CategoryConfig newDefaultConfig(Context context){
//        HBLog.d(TAG+" newDefaultConfig");
        CategoryConfig config = new CategoryConfig();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(context.getAssets().open("config/categorys.json"), "UTF-8");
            Gson gson = new Gson();
            PojoCategoryList categoryList = gson.fromJson(reader, PojoCategoryList.class);
            config.categories = categoryList.categories;
        } catch (Exception e) {
            HBLog.d(TAG+" newDefaultConfig error:"+ e.getMessage());
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

    public static void saveToLocal(List<PojoCategory> pojoCategories){
        Gson gson = new Gson();
        String jsonString = gson.toJson(pojoCategories);
        HBLog.d(TAG+" saveToLocal "+jsonString);
        if(!TextUtils.isEmpty(jsonString)){
            PrefUtils.putString(PrefConstants.Config.CATEHOTY_CONFIG, jsonString);
        }
    }



}
