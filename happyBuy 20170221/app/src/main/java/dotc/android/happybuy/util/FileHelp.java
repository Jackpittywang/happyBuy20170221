package dotc.android.happybuy.util;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

import dotc.android.happybuy.GlobalContext;

/**
 * Created by wangjun on 16/4/1.
 */
public class FileHelp {

    private static final String SYSTEM_CAMERA_PATH = Environment.getExternalStorageDirectory()
            + "/DCIM/Camera";


    public static String getSystemCameraPath(){
        return SYSTEM_CAMERA_PATH;
    }

    public static File newCameraImageFile(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return new File(SYSTEM_CAMERA_PATH, UUID.randomUUID().toString()+".jpg");
        }
        return getRandomImageCache();
    }

    public static File getRandomImageCache() {
        return new File(getExCacheDir(), UUID.randomUUID().toString());
    }

    public static File getExCacheDir() {
        return GlobalContext.get().getExternalCacheDir();
    }
}
