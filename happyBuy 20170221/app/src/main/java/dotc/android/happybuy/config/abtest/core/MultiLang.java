package dotc.android.happybuy.config.abtest.core;

import java.lang.reflect.Field;

import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.proguard.NoProguard;

/**
 * Created by wangjun on 16/12/7.
 */

public class MultiLang implements NoProguard {
    
    public String en;
    public String th;

    public String getText(){
        String lang = Languages.getInstance().getLanguage();
        try{
            Field[] fields = getClass().getDeclaredFields();
            for(Field field:fields){
                if(field.getName().equals(lang)){
                    return (String) field.get(this);
                }
            }
        } catch (Exception e){}
        return en;
    }

    @Override
    public String toString() {
        return "MultiLang{" +
                "en='" + en + '\'' +
                ", th='" + th + '\'' +
                '}';
    }
}
