package dotc.android.happybuy.modules.show.func;

import android.app.IntentService;
import android.content.Intent;

import java.io.File;

/**
 * Created by LiShen
 * on 2016/12/17.
 */

public class ClearPhotoCacheFolderService extends IntentService {
    public static final String EXTRA_FOLDER_PATH = "extra_folder_path";

    public ClearPhotoCacheFolderService() {
        super("ClearPhotoCacheFolderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String path = intent.getStringExtra(EXTRA_FOLDER_PATH);
            File folder = new File(path);
            if (folder.exists() && folder.isDirectory()) {
                File[] cacheList = folder.listFiles();
                if (cacheList != null && cacheList.length > 0) {
                    for (File cache : cacheList) {
                        //noinspection ResultOfMethodCallIgnored
                        cache.delete();
                    }
                }
            }
        } catch (Exception ignore) {
        }
    }
}
