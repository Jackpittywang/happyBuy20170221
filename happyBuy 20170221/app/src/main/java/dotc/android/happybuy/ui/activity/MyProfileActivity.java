package dotc.android.happybuy.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoFileUpload;
import dotc.android.happybuy.http.result.PojoNone;
import dotc.android.happybuy.http.result.PojoUserInfo;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.address.AddressCenterActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.FileHelp;
import dotc.android.happybuy.util.FileUtils;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 * 个人信息
 */
public class MyProfileActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_USERINFO = "extra_userinfo";
    public static final String OUTPUT_EXTRA_USERINFO = "output_extra_userinfo";
    private HBToolbar mToolbar;
    private View mPortraitLayout;
    private View mNicknameLayout;
    private View mAddressManagerLayout;
    private ImageView mPortraitImageView;
    private TextView mNicknameTextView;

    private final int REQUEST_CODE_OPEN_CAMERA = 0x01;
    private final int REQUEST_CODE_OPEN_CROP = 0x02;

    private File mCameraTempFile;
    private Bitmap mTempBitmap;
//    private final int REQUEST_CODE_LOCATION = 0x03;

    public static final int PORTRAIT_IMAGE_DEFAULT_WIDTH = 200;
    public static final int PORTRAIT_IMAGE_DEFAULT_HEIGHT = 200;

    private AsyncTask mPortraitUploadAsyncTask;
    private AsyncTask mNicknameAsyncTask;

    private PojoUserInfo mExtraUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        readExtraFromIntent();
        initActionbar();
        initUI();
        initData();
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_profile);
        mToolbar.setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void readExtraFromIntent() {
        mExtraUserInfo = (PojoUserInfo) getIntent().getSerializableExtra(EXTRA_USERINFO);
    }

    private void initUI() {
        mPortraitLayout = findViewById(R.id.layout_portrait);
        mNicknameLayout = findViewById(R.id.layout_nickname);
        mAddressManagerLayout = findViewById(R.id.layout_address);
        mPortraitImageView = (ImageView) findViewById(R.id.imageview_portrait);
        mNicknameTextView = (TextView) findViewById(R.id.textview_nickname);

        mPortraitLayout.setOnClickListener(this);
        mNicknameLayout.setOnClickListener(this);
        mAddressManagerLayout.setOnClickListener(this);
    }

    private void initData() {
        mNicknameTextView.setText(String.valueOf(mExtraUserInfo.nick));
        Glide.with(GlobalContext.get()).load(mExtraUserInfo.avatar).placeholder(R.drawable.pic_circle_portrait_placeholder).crossFade().into(mPortraitImageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPortraitUploadAsyncTask != null && mPortraitUploadAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPortraitUploadAsyncTask.cancel(true);
        }
        if (mNicknameAsyncTask != null && mNicknameAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mNicknameAsyncTask.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_portrait:
                showChoosePictureDialog();
                break;
            case R.id.layout_nickname:
                showEditNameDialog();
                break;
            case R.id.layout_address:
                Intent intent = new Intent(MyProfileActivity.this, AddressCenterActivity.class);
                startActivity(intent);
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Address, null, null);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent data = new Intent();
            data.putExtra(OUTPUT_EXTRA_USERINFO, mExtraUserInfo);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        HBLog.d(TAG + " onActivityResult resultCode:" + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_OPEN_CAMERA) {
            doOpenImageCropApp(data == null ? null : data.getData());
        } else if (requestCode == REQUEST_CODE_OPEN_CROP) {
            Bitmap bitmap = loadBitmapByIntent(data);
            if (bitmap != null) {
                mTempBitmap = bitmap;
                mPortraitImageView.setImageBitmap(bitmap);
                doSavePortraitTask(mTempBitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doSavePortraitTask(final Bitmap bitmap) {
        mPortraitUploadAsyncTask = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String[] params) {
                File file = saveBitmapToMomory(bitmap);
                PojoFileUpload fileUpload = Network.get(GlobalContext.get()).syncUploadFile(file);
//                PojoFileUpload fileUpload = doHttpSavePortrait(bitmap);
                if (fileUpload != null) {
                    return doHttpSavePortrait(fileUpload);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    ToastUtils.showLongToast(GlobalContext.get(), R.string.portrait_commit_success);
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Portrait_Success, null, null);
                } else {
                    ToastUtils.showLongToast(GlobalContext.get(), R.string.portrait_commit_fail);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean doHttpSavePortrait(PojoFileUpload fileUpload) {
        String url = HttpProtocol.URLS.PORTRAIT;
        Map<String, Object> params = new HashMap<>();
        params.put("avatar_url", fileUpload.file_url);
        try {
            PojoNone none = Network.get(GlobalContext.get()).syncPost(url, params, PojoNone.class);
            mExtraUserInfo.avatar = fileUpload.file_url;
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    private void doSaveNickname(final String newNickname) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", newNickname);
        String url = HttpProtocol.URLS.NICKNAME;
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoNone>() {
            @Override
            public void onSuccess(PojoNone pojoAds) {
                HBLog.d(TAG + " test onSuccess " + pojoAds);
                mExtraUserInfo.nick = newNickname;
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.NickName_Success, null, null);
//                ToastUtils.showLongToast(GlobalContext.get(),R.string.order_pay_fail);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " test onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
            }

            @Override
            public Class<PojoNone> getObjectClass() {
                return PojoNone.class;
            }
        });
    }

    private void showEditNameDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_edit_name, null);
        final EditText editText = (EditText) view.findViewById(R.id.edittext);
        String text = mExtraUserInfo == null ? "" : mExtraUserInfo.nick;
        editText.setText(text);
        editText.setSelection(text.length());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.modify_nickname);
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showLongToast(MyProfileActivity.this, R.string.nickname_empty_hint);
                } else if (name.length() > 20) {
                    ToastUtils.showLongToast(MyProfileActivity.this, R.string.nickname_long_hint);
                } else {
                    dialog.dismiss();
                    mNicknameTextView.setText(name);
                    doSaveNickname(name);
                }
            }
        });
        builder.create().show();
    }

    private void showChoosePictureDialog() {
        ChoosePictureDialog dialog = new ChoosePictureDialog();
//        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), ChoosePictureDialog.class.getName());
    }

    private void doOpenCameraApp() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        mCameraTempFile = FileHelp.newCameraImageFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraTempFile));
        try {
            startActivityForResult(intent, REQUEST_CODE_OPEN_CAMERA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_available_app,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void doOpenGalleryApp() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", PORTRAIT_IMAGE_DEFAULT_WIDTH);
        intent.putExtra("outputY", PORTRAIT_IMAGE_DEFAULT_HEIGHT);
        intent.putExtra("return-data", true);
        try {
            startActivityForResult(intent, REQUEST_CODE_OPEN_CROP);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_available_app, Toast.LENGTH_SHORT).show();
        }

    }

    private void doOpenImageCropApp(Uri uri) {
        if (uri == null) {
            MediaScannerConnection.scanFile(this, new String[]{FileHelp.getSystemCameraPath()}, null, null);
            File photoImage = mCameraTempFile;// new File(Directories.getPhotoPath());
            if (photoImage != null && photoImage.exists()) {
                uri = Uri.fromFile(photoImage);
            } else {
                Toast.makeText(this, R.string.no_available_app, Toast.LENGTH_LONG).show();
                return;
            }
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", PORTRAIT_IMAGE_DEFAULT_WIDTH);
        intent.putExtra("outputY", PORTRAIT_IMAGE_DEFAULT_HEIGHT);
        intent.putExtra("return-data", true);
        try {
            startActivityForResult(intent, REQUEST_CODE_OPEN_CROP);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_available_app, Toast.LENGTH_LONG).show();
        }
    }


    private Bitmap loadBitmapByIntent(Intent data) {
        Bitmap bitmap = data.getParcelableExtra("data");
        if (bitmap == null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = getImagePath(uri);
                if (!TextUtils.isEmpty(filePath)) {
                    bitmap = loadBitmapWithLimit(filePath, PORTRAIT_IMAGE_DEFAULT_WIDTH, PORTRAIT_IMAGE_DEFAULT_HEIGHT);
                } else {
                    bitmap = loadBitmapWithLimit(uri, PORTRAIT_IMAGE_DEFAULT_WIDTH, PORTRAIT_IMAGE_DEFAULT_HEIGHT);
                }
            } else {

            }
        }
        return bitmap;
    }

    private Bitmap loadBitmapWithLimit(String filePath, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = calcBitmapSampleSize(opts, width, height);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts);
        return bitmap;
    }

    private Bitmap loadBitmapWithLimit(Uri uri, int width, int height) {
        Bitmap bitmap = null;
        InputStream is = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try {
            is = getContentResolver().openInputStream(uri);
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeStream(is, null, opts);
            try {
                if (is != null)
                    is.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = calcBitmapSampleSize(opts, width, height);
            is = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    private int calcBitmapSampleSize(BitmapFactory.Options options, int expectedWidth, int expectedHeight) {
        int scale = 1;
        while (options.outWidth / scale / 2 >= expectedWidth && options.outHeight / scale / 2 >= expectedHeight)
            scale *= 2;
        return scale;
    }

    private String getImagePath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private File saveBitmapToMomory(Bitmap bitmap) {
        File file = null;
        FileOutputStream fileOS = null;
        try {
            file = FileUtils.newMemoryFile(FileUtils.EX_IMAGE);
            fileOS = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileOS != null) {
                try {
                    fileOS.close();
                } catch (IOException e) {
                }
            }
        }
        return file;
    }


    public static class ChoosePictureDialog extends DialogFragment
            implements DialogInterface.OnClickListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle(R.string.choose_pic_type);
            builder.setItems(R.array.choose_picture_from, this);
            return builder.create();
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            MyProfileActivity activity = (MyProfileActivity) getActivity();
            if (i == 0) {
                activity.doOpenCameraApp();
            } else if (i == 1) {
                activity.doOpenGalleryApp();
            }
        }
    }

}
