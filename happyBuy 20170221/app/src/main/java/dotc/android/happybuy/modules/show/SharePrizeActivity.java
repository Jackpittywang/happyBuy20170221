package dotc.android.happybuy.modules.show;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.AbtestConfigBean;
import dotc.android.happybuy.dialog.RateActivity;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoFileUpload;
import dotc.android.happybuy.http.result.PojoPendingUploadImage;
import dotc.android.happybuy.http.result.PojoShareResult;
import dotc.android.happybuy.http.result.PojoShowImage;
import dotc.android.happybuy.modules.show.adapter.SharePhotoChooseAdapter;
import dotc.android.happybuy.modules.show.func.ClearPhotoCacheFolderService;
import dotc.android.happybuy.modules.show.func.PhotoChooser;
import dotc.android.happybuy.modules.show.widget.PrizeShareAlertDialog;
import dotc.android.happybuy.modules.show.widget.PrizeShareTipDialog;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.dialog.SharePicDialog;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.widget.NoScrollGridLayoutManager;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.BitmapUtils;
import dotc.android.happybuy.util.Md5Util;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by LiShen
 * on 16/12/14.
 */
public class SharePrizeActivity extends BaseActivity {
    private static final int FOR_GRANTED_WRITE_EXTERNAL_STORAGE = 10001;
    private static final int FOR_CHOOSE_PHOTO = 10003;
    private static final int FOR_OPEN_CAMERA = 10004;
    private static final int FOR_CHECK_PHOTO = 10005;

    private static final int MSG_START_UPLOAD = 1001;
    private static final int MSG_FINISH_UPLOAD = 1003;

    private static final int SHARE_PHOTO_MAX_WIDTH = 640;
    private static final int SHARE_PHOTO_MAX_HEIGHT = 960;
    private static final int SHARE_PHOTO_QUALITY = 75;

    private HBToolbar tbSharePrize;
    private EditText etSharePrize;
    private RecyclerView rvSharePrize;
    private ImageView ivSharePrizePic;
    private TextView tvSharePrizeName;
    private TextView tvSharePrizeDetail;
    private Button btnSharePrizeSubmit;
    private SharePicDialog spdSharePrize;
    private ProgressDialog pdSharePrize;
    private PrizeShareAlertDialog adSharePrize;

    private SharePhotoChooseAdapter sharePhotoChooseAdapter;

    private MainHandler mainHandler;
    private HandlerThread workThread;
    private WorkHandler workHandler;

    private String orderId;
    private int inputMaxLength = 2000;

    private String CACHE_FOLDER;
    private String cameraPhotoPath;

    private String content;

    private boolean submitCooldown;

    private List<PojoPendingUploadImage> pendingUploadImages;
    private HashMap<String, PojoFileUpload> uploadedPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        findView();
        initData();
        setListener();
        initView();
        requestPermission();
    }

    private void setLayout() {
        setContentView(R.layout.activity_share_prize);
    }

    private void findView() {
        tbSharePrize = (HBToolbar) findViewById(R.id.tbSharePrize);
        etSharePrize = (EditText) findViewById(R.id.etSharePrize);
        rvSharePrize = (RecyclerView) findViewById(R.id.rvSharePrize);
        ivSharePrizePic = (ImageView) findViewById(R.id.ivSharePrizePic);
        tvSharePrizeName = (TextView) findViewById(R.id.tvSharePrizeName);
        tvSharePrizeDetail = (TextView) findViewById(R.id.tvSharePrizeDetail);
        btnSharePrizeSubmit = (Button) findViewById(R.id.btnSharePrizeSubmit);
        spdSharePrize = (SharePicDialog) findViewById(R.id.spdSharePrize);
    }

    private void initData() {
        mainHandler = new MainHandler(this, Looper.getMainLooper());
        CACHE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Gogobuy/SharePrizePhotoCache/";
        orderId = getIntent().getStringExtra("orderId");

        // load config
        AbtestConfigBean config = AbConfigManager.getInstance(GlobalContext.get()).getConfig();
        if (config != null && config.prize_share_info != null) {
            if (config.prize_share_info.user_input_text_max > 0) {
                inputMaxLength = config.prize_share_info.user_input_text_max;
            }
            if (config.prize_share_info.user_photos_max > 0) {
                PhotoChooser.Singleton.setSharePhotoMaxNum(config.prize_share_info.user_photos_max);
            }
        }
    }

    private void setListener() {
        spdSharePrize.setOnSharePicDialogButtonClickListener(new SharePicDialog.
                OnSharePicDialogButtonClickListener() {
            @Override
            public void onbtnAlbumClick() {
                startActivityForResult(new Intent(SharePrizeActivity.this,
                        PhotoChooseActivity.class), FOR_CHOOSE_PHOTO);
            }

            @Override
            public void onbtnCameraClick() {
                try {
                    cameraPhotoPath = CACHE_FOLDER +
                            Md5Util.getMD5LowerCase(String.valueOf(System.currentTimeMillis()))
                            + "camera";
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraPhotoPath)));
                    startActivityForResult(intent, FOR_OPEN_CAMERA);
                } catch (Exception e) {
                    ToastUtils.showShortToast(GlobalContext.get(), R.string.open_camera_failed);
                }
            }
        });
        btnSharePrizeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput())
                    startUpload();

            }
        });
    }

    private void initView() {
        sharePhotoChooseAdapter = new SharePhotoChooseAdapter(this);
        rvSharePrize.setLayoutManager(new NoScrollGridLayoutManager(this, 4));
        rvSharePrize.setAdapter(sharePhotoChooseAdapter);
        rvSharePrize.setHasFixedSize(false);

        tbSharePrize.setTitle(R.string.share_title);
        tbSharePrize.setDisplayHomeAsUpEnabled(true);

        tvSharePrizeName.setText(getIntent().getStringExtra("name"));
        tvSharePrizeDetail.setText(getString(R.string.share_introduce_num,
                getIntent().getStringExtra("number")));
        Glide.with(this).load(getIntent().getStringExtra("imageUrl")).error(R.drawable.ic_pic_default)
                .into(ivSharePrizePic);

        pdSharePrize = new ProgressDialog(this);
        pdSharePrize.setMessage(getString(R.string.commiting));
        pdSharePrize.setIndeterminate(true);
        pdSharePrize.setCancelable(false);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        FOR_GRANTED_WRITE_EXTERNAL_STORAGE);
            } else {
                showShareTipDialog();
            }
        } else {
            showShareTipDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case FOR_GRANTED_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                } else {
                    showShareTipDialog();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FOR_CHOOSE_PHOTO:
            case FOR_CHECK_PHOTO:
                sharePhotoChooseAdapter.refresh();
                break;
            case FOR_OPEN_CAMERA:
                if (resultCode == RESULT_OK) {
                    File file = new File(cameraPhotoPath);
                    if (file.exists()) {
                        PhotoChooser.Singleton.addSharePhoto(cameraPhotoPath);
                        sharePhotoChooseAdapter.refresh();
                    }
                    cameraPhotoPath = "";
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // reset photos info
        PhotoChooser.Singleton.clear();
        // stop work thread
        if (workThread != null) {
            workThread.quit();
        }
        // clear cache
        Intent clearCacheService = new Intent(this, ClearPhotoCacheFolderService.class);
        clearCacheService.putExtra(ClearPhotoCacheFolderService.EXTRA_FOLDER_PATH, CACHE_FOLDER);
        startService(clearCacheService);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (spdSharePrize.isshowing()) {
            spdSharePrize.dismiss();
            return;
        }
        super.onBackPressed();
    }

    public void showShareDialog() {
        if (!spdSharePrize.isshowing())
            spdSharePrize.show();
    }

    public void viewSharePhoto(int position) {
        startActivityForResult(new Intent(this, ViewSharePhotosActivity.class)
                        .putExtra(ViewSharePhotosActivity.EXTRA_FIRST_LOOK_POSITION, position),
                FOR_CHECK_PHOTO);
    }

    private void showAlertDialog(String content) {
        if (adSharePrize == null) {
            adSharePrize = new PrizeShareAlertDialog(this, R.style.prize_share_dialog);
        }
        adSharePrize.show(content);
    }

    private void showShareTipDialog() {
        checkCacheFolder();

        if (!PrefUtils.getBoolean(this, PrefConstants.SharePrize.TIP_DIALOG_NO_LONGER_PROMPT, false)) {
            PrizeShareTipDialog dialog = new PrizeShareTipDialog(this, R.style.prize_share_dialog);
            dialog.show();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void checkCacheFolder() {
        File file = new File(CACHE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private boolean checkInput() {
        content = etSharePrize.getEditableText().toString();
        try {
            content = URLEncoder.encode(content, "utf-8");
            //noinspection ResultOfMethodCallIgnored
            do {
                // clear too much newlines
                content = content.replace("%0A%0A%0A", "%0A");
            } while (content.contains("%0A%0A%0A"));
        } catch (Exception ignore) {
        }
        if (TextUtils.isEmpty(content)) {
            showAlertDialog(getString(R.string.share_prize_input_not_empty));
            return false;
        }
        if (content.length() >= inputMaxLength) {
            showAlertDialog(getString(R.string.share_prize_input_too_long,
                    String.valueOf(inputMaxLength)));
            return false;
        }
        if (PhotoChooser.Singleton.getSharePhotosChoose().size() < 1) {
            showAlertDialog(getString(R.string.add_at_least_one_photo));
            return false;
        }
        return true;
    }

    private void startUpload() {
        if (submitCooldown) {
            return;
        }

        pendingUploadImages = new ArrayList<>();
        uploadedPhotos = new HashMap<>();

        checkWorkThread();
        Message msg2 = workHandler.obtainMessage(MSG_START_UPLOAD);
        workHandler.sendMessage(msg2);

        submitCooldown = true;
        pdSharePrize.show();
    }

    /**
     * handle photos and submit share info, on working thread
     */
    private void submit() {
        if (!compressPhotos()) {
            Message msg1 = mainHandler.obtainMessage(MSG_FINISH_UPLOAD);
            msg1.obj = false;
            mainHandler.sendMessage(msg1);
            return;
        }
        List<PojoFileUpload> result = uploadPhotos();
        if (result == null || result.size() !=
                PhotoChooser.Singleton.getSharePhotosChoose().size()) {
            Message msg2 = mainHandler.obtainMessage(MSG_FINISH_UPLOAD);
            msg2.obj = false;
            mainHandler.sendMessage(msg2);
            return;
        }
        List<PojoShowImage> imageList = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            pendingUploadImages.get(i).image.url = result.get(i).file_url;
            imageList.add(pendingUploadImages.get(i).image);
        }
        submitShareInfo(imageList);
    }

    /**
     * compress photos on working thread
     *
     * @return success or not
     */
    private boolean compressPhotos() {
        try {
            PojoPendingUploadImage pojoPendingUploadImage;
            for (String path : PhotoChooser.Singleton.getSharePhotosChoose()) {

                Bitmap b = BitmapUtils.compressFromFile(path,
                        SHARE_PHOTO_MAX_WIDTH, SHARE_PHOTO_MAX_HEIGHT);

                // Adjustment camera photo direction
                if (path.endsWith("camera")) {
                    ExifInterface ei = new ExifInterface(path);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                        b = BitmapUtils.rotateImage(b, 90);
                    }
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                        b = BitmapUtils.rotateImage(b, 180);
                    }
                }

                String photoSavePath = CACHE_FOLDER +
                        Md5Util.getMD5LowerCase(String.valueOf(System.currentTimeMillis())) + ".jpg";

                boolean result = BitmapUtils.saveBitmapToFile(b,
                        photoSavePath, SHARE_PHOTO_QUALITY, Bitmap.CompressFormat.JPEG);
                if (result) {
                    int[] widthHeight = BitmapUtils.getImageWidthHeightFromFile(photoSavePath);
                    pojoPendingUploadImage = new PojoPendingUploadImage();
                    pojoPendingUploadImage.file = new File(photoSavePath);
                    pojoPendingUploadImage.image.w = widthHeight[0];
                    pojoPendingUploadImage.image.h = widthHeight[1];
                    pendingUploadImages.add(pojoPendingUploadImage);
                }
                b.recycle();
            }
        } catch (Exception e) {
            return false;
        }
        return pendingUploadImages.size() == PhotoChooser.Singleton.getSharePhotosChoose().size();
    }

    /**
     * upload photos on working thread
     *
     * @return uploaded info
     */
    private List<PojoFileUpload> uploadPhotos() {

        List<PojoFileUpload> pojoFileUploads = new ArrayList<>();
        for (PojoPendingUploadImage image : pendingUploadImages) {
            PojoFileUpload pojoFileUpload;
            if (uploadedPhotos.get(Uri.fromFile(image.file).toString()) != null) {
                pojoFileUpload = uploadedPhotos.get(Uri.fromFile(image.file).toString());
            } else {
                pojoFileUpload = Network.get(GlobalContext.get()).syncUploadFile(image.file);
            }
            if (pojoFileUpload != null && !TextUtils.isEmpty(pojoFileUpload.file_url)) {
                uploadedPhotos.put(Uri.fromFile(image.file).toString(), pojoFileUpload);
                pojoFileUploads.add(pojoFileUpload);
            }
        }
        return pojoFileUploads;
    }

    /**
     * submit the share info
     */
    private void submitShareInfo(List<PojoShowImage> imageList) {
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("order_id", orderId);
        map.put("message", content);
        // JSONArray images = new JSONArray();
        JSONArray newImages = new JSONArray();
        for (PojoShowImage i : imageList) {
            //images.put(i.url);
            try {
                // image url can not be null
                if (!TextUtils.isEmpty(i.url)) {
                    JSONObject o = new JSONObject(gson.toJson(i));
                    newImages.put(o);
                }
            } catch (Exception ignore) {
            }
        }
        // map.put("images", images);

        // image list size can not be 0
        if (newImages.length() == 0) {
            Message msg = mainHandler.obtainMessage(MSG_FINISH_UPLOAD);
            msg.obj = false;
            mainHandler.sendMessage(msg);
            return;
        }

        map.put("new_images", newImages);

        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.SHARE_OVER_ADD, map,
                new Network.JsonCallBack<PojoShareResult>() {
                    @Override
                    public void onSuccess(PojoShareResult pojoShareResult) {
                        Message msg = mainHandler.obtainMessage(MSG_FINISH_UPLOAD);
                        msg.obj = true;
                        mainHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailed(int code, String message, Exception e) {
                        Message msg = mainHandler.obtainMessage(MSG_FINISH_UPLOAD);
                        msg.obj = false;
                        mainHandler.sendMessage(msg);
                    }

                    @Override
                    public Class<PojoShareResult> getObjectClass() {
                        return PojoShareResult.class;
                    }
                });
    }

    private void handleMainMessage(Message msg) {
        if (msg.what == MSG_FINISH_UPLOAD) {
            pdSharePrize.dismiss();
            submitCooldown = false;
            if ((boolean) msg.obj) {
                ToastUtils.showShortToast(SharePrizeActivity.this, R.string.share_success);
                int count = PrefUtils.getInt(PrefConstants.CommentTime.COMMENT_TIME, 0);
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Show_Success, null, null);
                PrefUtils.putInt(PrefConstants.CommentTime.COMMENT_TIME, count + 1);
                if (count < AbConfigManager.getInstance(GlobalContext.get()).getConfig().rate_app.max_alert) {
                    // if(count<PrefUtils.getInt(PrefConstants.CommentTime.COMMENT_LIMIT_TIME,3)){
                    PrefUtils.putInt(PrefConstants.CommentTime.COMMENT_TIME, count + 1);
                    Intent comment = new Intent(SharePrizeActivity.this, RateActivity.class);
                    startActivity(comment);
                }
                finish();
            } else {
                ToastUtils.showShortToast(SharePrizeActivity.this, R.string.share_fail);
            }
        }
    }

    private void handleWorkMessage(Message msg) {
        if (msg.what == MSG_START_UPLOAD) {
            submit();
        }
    }

    private void checkWorkThread() {
        if (workThread == null || !workThread.isAlive()) {
            workThread = new HandlerThread("SharePrizeWorkThread", 10);
            workThread.start();
            workHandler = new WorkHandler(this, workThread.getLooper());
        }
    }

    private static class MainHandler extends Handler {
        private final WeakReference<SharePrizeActivity> weakReference;

        private MainHandler(SharePrizeActivity activity, Looper Looper) {
            super(Looper);
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SharePrizeActivity activity = weakReference.get();
            if (activity == null || msg == null ||
                    AppUtil.isActivityDestroyed(activity)) {
                return;
            }
            activity.handleMainMessage(msg);
        }
    }

    private static class WorkHandler extends Handler {
        private final WeakReference<SharePrizeActivity> weakReference;

        private WorkHandler(SharePrizeActivity activity, Looper Looper) {
            super(Looper);
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SharePrizeActivity activity = weakReference.get();
            if (activity == null || msg == null ||
                    AppUtil.isActivityDestroyed(activity)) {
                return;
            }
            activity.handleWorkMessage(msg);
        }
    }
}
