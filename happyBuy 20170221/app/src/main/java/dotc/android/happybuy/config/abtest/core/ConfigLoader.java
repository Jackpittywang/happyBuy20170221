package dotc.android.happybuy.config.abtest.core;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.update.UpdateSdk;
import com.google.gson.Gson;
import com.stat.analytics.AnalyticsSdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.util.AppUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wangjun on 16/11/29.
 */

public class ConfigLoader<T extends IConfigBean> {

    private final static String TAG = ConfigLoader.class.getSimpleName();
    public static final String CHARSET = "UTF-8";

    private Context mContext;
    private final String mFileName;
    private final String mModuleId;
    private Class<T> mCls;

    public ConfigLoader(Context context, Class<T> clz, String fileName,String moduleid){
        this.mContext = context.getApplicationContext();
        mCls = clz;
        mFileName = fileName;
        mModuleId = moduleid;
    }

    public T loadConfig(){
        T bean = loadFromFile(mFileName);
        if(bean == null){
            bean = loadFromAssetFile(mFileName);
        }
        if(bean == null){
            Log.e("Loader","hb config can't load from file...");
            bean = loadFromDefault();
        }
        return bean;
    }

    private T loadFromDefault(){
        try {
            return mCls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private T loadFromAssetFile(String fileName) {
        HBLog.d(TAG,"loadFromAssetFile");
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(mContext.getAssets().open(fileName), CHARSET);
            return parse(reader, mCls);
        } catch (Exception e) {
            HBLog.e(TAG + " loadFromAssetFile error:"+e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private synchronized T loadFromFile(String fileName) {
        HBLog.d(TAG,"loadFromFile");
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(mContext.openFileInput(fileName), CHARSET);
            return parse(reader, mCls);
        } catch (Exception e) {
            HBLog.e(TAG + " parse error:"+e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public boolean updateConfig(T configBean) {
//        T configBean = parse(json, mCls);
        HBLog.d(TAG ,"updateConfig "+configBean);
        if (configBean != null && configBean.isValid()) {
            return saveConfigToFile(toJson(configBean));
        }
        return false;
    }

    private String toJson(T configBean){
        Gson gson = new Gson();
        return gson.toJson(configBean);
    }

    private T parse(Reader reader, Class<T> clz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(reader, clz);
        } catch (Exception e) {
            e.printStackTrace();
            HBLog.e(TAG, "parse error:"+e);
        }
        return null;
    }

    private T parse(String jsonString, Class<T> clz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, clz);
        } catch (Exception e) {
            HBLog.e(TAG, "parse error:"+e);
        }
        return null;
    }

    private void migrateToDataFolder(File targetFile,File newTempFile) {
        HBLog.d(TAG,"migrateToDataFolder ");
        File backupTargetFile = null;
        if(targetFile.exists()){
            backupTargetFile = mContext.getFileStreamPath("backup_"+System.currentTimeMillis());
            targetFile.renameTo(backupTargetFile);
        }

        boolean opt = false;

        if(!newTempFile.renameTo(targetFile)){
            opt = copyToFile(newTempFile, targetFile);
        } else {
            opt = true;
        }
        if (opt){
            if(backupTargetFile!=null){
                backupTargetFile.delete();
            }
        } else {
            if(backupTargetFile!=null){
                backupTargetFile.renameTo(targetFile);
            }
        }
    }

    private synchronized boolean saveConfigToFile(String json){
        HBLog.d(TAG,"saveConfigToFile ");
        String tempFileName = "temp_"+System.currentTimeMillis();
        File tempFile = null;
        try {
            tempFile = mContext.getFileStreamPath(tempFileName);
            File targetFile = mContext.getFileStreamPath(mFileName);
            writeToFile(tempFile,json);
            migrateToDataFolder(targetFile,tempFile);
            return true;
        } catch (IOException e){
            e.printStackTrace();
            HBLog.w(TAG,"saveConfigToFile error:"+e);
        } finally {
            if(tempFile!=null){
                tempFile.delete();
            }
        }

        return false;
    }

    private void writeToFile(File file,String data) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data.getBytes(CHARSET));
        } finally {
            if(out!=null){
                out.close();
            }
        }
    }

    private boolean copyToFile(File inputFile, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            InputStream inputStream = new FileInputStream(inputFile);
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                if(inputStream!=null){
                    inputStream.close();
                }
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
