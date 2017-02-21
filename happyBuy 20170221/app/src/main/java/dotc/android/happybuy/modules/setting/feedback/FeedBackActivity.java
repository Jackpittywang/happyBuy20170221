package dotc.android.happybuy.modules.setting.feedback;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.FormatVerifyUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by huangli on 16/5/13.
 */
public class FeedBackActivity extends BaseActivity{

    private static final String TAG = "FeedBackActivity";

    private static final int REQUEST_CODE_READ_PHONE_STATE = 100;

    private HBToolbar idToolbar;
    private RelativeLayout layoutupload;

    public static final int FROM_HELP_CENTER = 0;
    public static final int FROM_HELP_EXCEPTION = 1;

    public static final String FROM_KEY = "from";

    public int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        findviews();
        setListeners();
        type = getIntent().getIntExtra(FROM_KEY,FROM_HELP_CENTER);
        requestPermissionIfNeeded();
    }

    private void findviews(){
        idToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        layoutupload = (RelativeLayout) findViewById(R.id.layout_uplpad);
        idToolbar.setTitle(R.string.activity_feed_back);
    }

    private String getDeviceId(){
        String deviceId = AppUtil.getDeviceId(this);
        return deviceId;
    }

    private void uploadFeedBack(String content,String email){
        Map<String, Object> map = new HashMap<>();
        map.put("email",email);
        map.put("content", content);
        map.put("category",type);
        map.put("device_id",getDeviceId());
        HBLog.i(TAG+" uploadFeedBack "+" content "+content+" category "+type+" device_id "+getDeviceId());
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.FeedBack, map, new Network.JsonCallBack<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                ToastUtils.showShortToast(FeedBackActivity.this,R.string.cs_feedback_upload_scuess);
                finish();
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                Toast.makeText(GlobalContext.get(), message, Toast.LENGTH_LONG).show();
            }

            @Override
            public Class<JSONObject> getObjectClass() {
                return JSONObject.class;
            }
        });
    }

    private void setListeners(){
        layoutupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = getEditContent().getText().toString();
                String email = getEditEmail().getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShortToast(FeedBackActivity.this, R.string.cs_feedback_format_content_tips);
                    return;
                }
                if (!FormatVerifyUtil.isEmail(email)) {
                    ToastUtils.showShortToast(FeedBackActivity.this, R.string.cs_feedback_format_email_tips);
                    return;
                }
                uploadFeedBack(content, email);
            }
        });
    }

    private void requestPermissionIfNeeded(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READ_PHONE_STATE);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        HBLog.d(TAG + " onRequestPermissionsResult requestCode:" + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
//                    finish();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private EditText getEditContent(){
        return (EditText) findViewById(R.id.edit_content);
    }

    private EditText getEditEmail(){
        return (EditText) findViewById(R.id.edit_email);
    }
}
