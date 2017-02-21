package dotc.android.happybuy.modules.show.func;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoPhotoFolder;
import dotc.android.happybuy.modules.show.SharePrizeActivity;

/**
 * Created by LiShen
 * on 2016/12/13.
 */

public enum PhotoChooser {
    Singleton;

    private static final String KEY_ALL_PHOTOS = "all_photos";

    private List<String> sharePhotosChoose = new ArrayList<>();
    private List<PojoPhotoFolder> photoFolderList = new ArrayList<>();

    private int sharePhotoMaxNum = 4;
    private boolean ready = false;

    /**
     * Search for all the photos info on the phone, to generate a list of folders
     */
    public void searchPhotos() {
        photoFolderList = new ArrayList<>();

        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = GlobalContext.get().getContentResolver();

        Cursor cursor = contentResolver.query(imageUri, null, MediaStore.Images.Media.MIME_TYPE
                        + " in(?, ?)", new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (cursor == null) {
            setReady(true);
            return;
        }
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

        ArrayMap<String, PojoPhotoFolder> folderMap = new ArrayMap<>();
        PojoPhotoFolder allFolder = new PojoPhotoFolder();
        allFolder.folderName = GlobalContext.get().getString(R.string.all_photos);
        folderMap.put(KEY_ALL_PHOTOS, allFolder);

        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(columnIndex);
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();
                if (folderMap.containsKey(dirPath)) {
                    folderMap.get(dirPath).photos.add(path);
                    folderMap.get(KEY_ALL_PHOTOS).photos.add(path);
                } else {
                    PojoPhotoFolder photoFolder = new PojoPhotoFolder();
                    photoFolder.photos.add(path);
                    photoFolder.folderPath = dirPath;
                    photoFolder.folderName = new File(dirPath).getName();
                    photoFolder.iconPhoto = path;
                    folderMap.put(dirPath, photoFolder);
                    folderMap.get(KEY_ALL_PHOTOS).photos.add(path);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();


        photoFolderList.add(folderMap.get(KEY_ALL_PHOTOS));
        for (String key : folderMap.keySet()) {
            if (!key.equals(KEY_ALL_PHOTOS)) {
                folderMap.get(key).photoNum = folderMap.get(key).photos.size();
                photoFolderList.add(folderMap.get(key));
            }
        }

        if (photoFolderList.get(0).photos != null &&
                photoFolderList.get(0).photos.size() > 0) {
            photoFolderList.get(0).iconPhoto = photoFolderList.get(0).photos.get(0);
            photoFolderList.get(0).photoNum = photoFolderList.get(0).photos.size();
        }

        setReady(true);
    }

    public void clear() {
        photoFolderList = new ArrayList<>();
        sharePhotosChoose = new ArrayList<>();
        setReady(false);
    }

    public List<PojoPhotoFolder> getPhotoFolderList() {
        return photoFolderList;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setSharePhotoMaxNum(int sharePhotoMaxNum) {
        this.sharePhotoMaxNum = sharePhotoMaxNum;
    }

    public int getSharePhotoMaxNum() {
        return sharePhotoMaxNum;
    }

    public List<String> getSharePhotosChoose() {
        if (sharePhotosChoose == null) {
            sharePhotosChoose = new ArrayList<>();
        }
        return sharePhotosChoose;
    }

    public void addSharePhoto(String path) {
        if (sharePhotosChoose.size() < sharePhotoMaxNum)
            sharePhotosChoose.add(path);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeSharePhoto(int position) {
        if (sharePhotosChoose != null && sharePhotosChoose.size() > 0) {
            sharePhotosChoose.remove(position);
        }
    }
}
