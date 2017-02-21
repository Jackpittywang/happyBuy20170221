package dotc.android.happybuy.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/10/11.
 */
public class Languages {
    private static String TAG = Languages.class.getSimpleName();
    public final static String LANGUAGE_DAFAULT = "default";
    private static Languages sInstance;

    public static Languages getInstance() {
        return getInstance(GlobalContext.get());
    }

    public static Languages getInstance(Context context) {
        if(sInstance==null){
            sInstance = new Languages(context);
        }
        return sInstance;
    }

    private Context mContext;
    private String mServerAvailableLanguageCode;
    private List<OnLangChangeListener> mLangChangeListeners;

    private Languages(Context context) {
        this.mContext = context;
        mLangChangeListeners = new ArrayList<>();
        mServerAvailableLanguageCode = getServerAvailableLanguageCode();
    }

    public void initWhenLauncher(){
        String userChoiceLanguage = getUserChoiceLanguage();
        HBLog.d(TAG+" initWhenLauncher userChoiceLanguage:"+userChoiceLanguage);
        if (!LANGUAGE_DAFAULT.equals(userChoiceLanguage)){
            String languageCode = getLanguageCode(userChoiceLanguage);
            String appLanguageCode = getAppLanguageCode();

            if(!languageCode.equals(appLanguageCode)){
                if(updateAppLocale(languageCode)){
//                    notifyLanguageChanged(userChoiceLanguage);
                    mServerAvailableLanguageCode = languageCode;
                } else {
                    setUserChoiceLanguage(LANGUAGE_DAFAULT);
                    mServerAvailableLanguageCode = getServerAvailableLanguageCode();
                }
            }
        } else {
            mServerAvailableLanguageCode = getServerAvailableLanguageCode();
        }
    }

    /*
    * 获取服务器交互层的语言参数
    * */
    public String getLanguage(){
        return mServerAvailableLanguageCode;
    }

    public void userChoiceChanged(String language){
        setUserChoiceLanguage(language);
        String languageCode = getLanguageCode(language);
        dispatchLangChangeListener(languageCode, mServerAvailableLanguageCode);
        mServerAvailableLanguageCode = languageCode;
    }

    public boolean updateLanguageWithLanguage(String language){
        return updateAppLocale(getLanguageCode(language));
    }

    private String getAppLanguageCode(){
        Resources resources = mContext.getResources();
        Configuration config = resources.getConfiguration();
        String language = config.locale.getLanguage();
        /*if("vi".equals(language)){
            return "vn";
        }*/
        return language;
    }

    public boolean updateAppLocale(String languageCode){
        HBLog.d(TAG+" updateAppLocale language:"+languageCode);
        /*if(languageCode.equals("vn")){
            languageCode = "vi";
        }*/
        try {
            Resources resources = mContext.getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();
            config.locale = new Locale(languageCode, "");
            resources.updateConfiguration(config,dm);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            HBLog.w(TAG+" updateAppLocale failed:"+e.getMessage());
        }
        return false;
    }

    private Locale getLocale(String langString){
        if (langString.equals("ไทย")){
            return new Locale("th", "");
        }else if(langString.equals("Tiếng Việt")){
            return new Locale("vn", "");
        }else if(langString.equals("Indonesia")){
            return new Locale("id", "");
        }  else{
            return Locale.ENGLISH;
        }
    }

    private String getLanguageCode(String language){
        if (language.equals("ไทย")){
            return "th";
        } else if(language.equals("Tiếng Việt")){
            return "vi";
        }else if(language.equals("Indonesia")){
            return "id";
        }else {
            return Locale.ENGLISH.getLanguage();
        }
    }

    public String getUserChoiceLanguage(){
        return PrefUtils.getString(mContext, PrefConstants.Language.APP,LANGUAGE_DAFAULT);
    }

    public void setUserChoiceLanguage(String language){
        PrefUtils.putString(mContext, PrefConstants.Language.APP, language);
    }

    /*
    *
    **/
    public String getServerAvailableLanguageCode(){
        String languageCode = getAppLanguageCode();
        String[] scopes = getLanguages();
        if(isLangInScope(languageCode,scopes)){
            return languageCode;
        }
        String country = AppUtil.getMetaData(mContext,"country");

        return AppUtil.getStringValue(mContext,country+"_default_lang","en");
    }

    public String getSymbol() {
        String country = AppUtil.getMetaData(mContext, "country");
        return AppUtil.getSymbolValue(mContext, country + "_symbol", "vn");
    }

    /*
    * 获取国家对应的语言集
    **/
    public String[] getLanguages(){
        String country = AppUtil.getMetaData(mContext,"country");
        return AppUtil.getArrayValue(mContext,country+"_language");
    }

    private boolean isLangInScope(String code,String[] scopes){
        for(String language:scopes){
            String languageCode = getLanguageCode(language);
            if(languageCode.equals(code)){
                return true;
            }
        }
        return false;
    }

    public void addLangChangeListener(OnLangChangeListener listener){
        mLangChangeListeners.add(listener);
    }

    public void removeLangChangeListener(OnLangChangeListener listener){
        mLangChangeListeners.remove(listener);
    }

    private void dispatchLangChangeListener(String newLang,String oldLang){
        for(OnLangChangeListener listener:mLangChangeListeners){
            listener.onLangChanged(newLang,oldLang);
        }
    }

    interface OnLangChangeListener{
        void onLangChanged(String newLang,String oldLang);
    }

}
