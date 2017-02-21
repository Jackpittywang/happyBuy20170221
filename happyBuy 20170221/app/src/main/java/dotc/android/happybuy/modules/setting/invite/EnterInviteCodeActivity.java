package dotc.android.happybuy.modules.setting.invite;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by LiShen
 * on 2016/12/6.
 */
@SuppressWarnings("deprecation")
public class EnterInviteCodeActivity extends BaseActivity {
    private HBToolbar tbEnterInviteCode;
    private EditText etEnterInviteCode;
    private Button btnEnterInviteCode;
    private ImageView ivEnterInviteCodeClear;
    private ProgressBar pbEnterInviteCode;

    private static final int ERROR_ALREADY_USED = 100805;

    private boolean submitCooldown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        findView();
        setListener();
        initView();
    }

    private void setLayout() {
        setContentView(R.layout.activity_enter_invite_code);
    }

    private void findView() {
        tbEnterInviteCode = (HBToolbar) findViewById(R.id.tbEnterInviteCode);
        etEnterInviteCode = (EditText) findViewById(R.id.etEnterInviteCode);
        btnEnterInviteCode = (Button) findViewById(R.id.btnEnterInviteCode);
        ivEnterInviteCodeClear = (ImageView) findViewById(R.id.ivEnterInviteCodeClear);
        pbEnterInviteCode = (ProgressBar) findViewById(R.id.pbEnterInviteCode);
    }

    private void setListener() {
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ivEnterInviteCodeClear:
                        etEnterInviteCode.setText(null);
                        break;
                    case R.id.btnEnterInviteCode:
                        submitInviteCode();
                        break;
                }
            }
        };
        btnEnterInviteCode.setOnClickListener(l);
        ivEnterInviteCodeClear.setOnClickListener(l);
        etEnterInviteCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (TextUtils.isEmpty(str)) {
                    btnEnterInviteCode.setEnabled(false);
                    ivEnterInviteCodeClear.setVisibility(View.GONE);
                    // invite code editText turns into grey bottom line
                    Drawable greyLine = getResources().
                            getDrawable(R.drawable.shape_grey_enter_invite_code_bottom_line);
                    greyLine.setBounds(0, 0, greyLine.getMinimumWidth(), greyLine.getMinimumHeight());
                    etEnterInviteCode.setCompoundDrawables(null, null, null, greyLine);
                } else {
                    btnEnterInviteCode.setEnabled(true);
                    ivEnterInviteCodeClear.setVisibility(View.VISIBLE);
                    // invite code editText turns into red bottom line
                    Drawable redLine = getResources().
                            getDrawable(R.drawable.shape_red_enter_invite_code_bottom_line);
                    redLine.setBounds(0, 0, redLine.getMinimumWidth(), redLine.getMinimumHeight());
                    etEnterInviteCode.setCompoundDrawables(null, null, null, redLine);
                }
            }
        });
    }

    private void initView() {
        tbEnterInviteCode.setTitle(getString(R.string.enter_your_promotion_code));
        tbEnterInviteCode.setDisplayHomeAsUpEnabled(true);
        btnEnterInviteCode.setEnabled(false);
        ivEnterInviteCodeClear.setVisibility(View.GONE);
    }

    /**
     * submit the invite code to server
     */
    private void submitInviteCode() {
        String invite_code = etEnterInviteCode.getEditableText().toString().trim();
        if (submitCooldown || TextUtils.isEmpty(invite_code)) {
            return;
        }
        submitCooldown = true;
        pbEnterInviteCode.setVisibility(View.VISIBLE);

        Map<String, Object> param = new HashMap<>();
        param.put("uid", PrefUtils.getString(GlobalContext.get(), PrefConstants.UserInfo.UID, ""));
        param.put("device_id", AppUtil.getDeviceId(this));
        param.put("invite_code", invite_code);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.USE_INVITE_CODE, param,
                new Network.JsonCallBack<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        if (!AppUtil.isActivityDestroyed(EnterInviteCodeActivity.this)) {
                            submitCooldown = false;
                            pbEnterInviteCode.setVisibility(View.GONE);
                            ToastUtils.showShortToast(EnterInviteCodeActivity.this,
                                    R.string.invite_code_enter_success);
                        }
                    }

                    @Override
                    public void onFailed(int code, String message, Exception e) {
                        if (!AppUtil.isActivityDestroyed(EnterInviteCodeActivity.this)) {
                            submitCooldown = false;
                            pbEnterInviteCode.setVisibility(View.GONE);
                            if (code == ERROR_ALREADY_USED) {
                                ToastUtils.showShortToast(EnterInviteCodeActivity.this,
                                        R.string.invite_code_enter_already);
                            } else {
                                ToastUtils.showShortToast(EnterInviteCodeActivity.this,
                                        R.string.invite_code_enter_wrong);
                            }
                        }
                    }

                    @Override
                    public Class<JSONObject> getObjectClass() {
                        return JSONObject.class;
                    }
                });
        Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickPromotionYes, null, null);
    }
}
