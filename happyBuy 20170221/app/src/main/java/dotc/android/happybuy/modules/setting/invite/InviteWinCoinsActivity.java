package dotc.android.happybuy.modules.setting.invite;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.ShareInfo;
import dotc.android.happybuy.config.abtest.bean.UrlInfo;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoSharingInfo;
import dotc.android.happybuy.modules.setting.invite.adapter.SharePlatformAdapter;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.activity.WebActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.uibase.widget.NoScrollGridView;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ShareUtils;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by LiShen
 * on 2016/12/6.
 */

public class InviteWinCoinsActivity extends BaseActivity {
    private static final String PLACEHOLDER_INVITE_CODE = "$invite_code$";

    private static final int REQUEST_MESSENGER_SHARE = 2;
    private static final int REQUEST_FACEBOOK_SHARE = 1;

    private HBToolbar tbInviteWinCoins;
    private TextView tvInviteWinCoinsShareNum;
    private TextView tvInviteWinCoinsCoinsNum;
    private TextView tvInviteWinCoinsCheckDetail;
    private TextView tvInviteWinCoinsInviteCode;
    private NoScrollGridView gvInviteWinCoins;
    private RelativeLayout rlInviteWinCoinsShareCode;
    private RelativeLayout rlInviteWinCoins;
    private TextView tvInviteWinCoinsCopy;
    private NetworkErrorLayout nelInviteWinCoins;
    private ProgressBar pbInviteWinCoins;

    private ShareUtils shareUtils;

    private String inviteCode = "";
    private int shareNum;
    private int shareCoins;
    private String winCoinsDetailUrl;

    private ShareInfo.Facebook facebookShareInfo;
    private ShareInfo.Line lineShareInfo;
    private ShareInfo.Other otherShareInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        findView();
        setListener();
        initView();

        getSharingInfo();
    }

    private void setLayout() {
        setContentView(R.layout.activity_invite_win_coins);
    }

    private void findView() {
        tbInviteWinCoins = (HBToolbar) findViewById(R.id.tbInviteWinCoins);
        tvInviteWinCoinsShareNum = (TextView) findViewById(R.id.tvInviteWinCoinsShareNum);
        tvInviteWinCoinsCoinsNum = (TextView) findViewById(R.id.tvInviteWinCoinsCoinsNum);
        tvInviteWinCoinsCheckDetail = (TextView) findViewById(R.id.tvInviteWinCoinsCheckDetail);
        tvInviteWinCoinsInviteCode = (TextView) findViewById(R.id.tvInviteWinCoinsInviteCode);
        gvInviteWinCoins = (NoScrollGridView) findViewById(R.id.gvInviteWinCoins);
        rlInviteWinCoinsShareCode = (RelativeLayout) findViewById(R.id.rlInviteWinCoinsShareCode);
        rlInviteWinCoins = (RelativeLayout) findViewById(R.id.rlInviteWinCoins);
        tvInviteWinCoinsCopy = (TextView) findViewById(R.id.tvInviteWinCoinsCopy);
        nelInviteWinCoins = (NetworkErrorLayout) findViewById(R.id.nelInviteWinCoins);
        pbInviteWinCoins = (ProgressBar) findViewById(R.id.pbInviteWinCoins);
    }

    private void setListener() {
        gvInviteWinCoins.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toShareInviteCode(i);
            }
        });
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tvInviteWinCoinsCheckDetail:
                        Intent intent = new Intent(InviteWinCoinsActivity.this, WebActivity.class);
                        intent.putExtra(WebActivity.EXTRA_TITLE, getString(R.string.invite_friends_win_coins));
                        if (winCoinsDetailUrl != null) {
                            intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(winCoinsDetailUrl));
                        }
                        startActivity(intent);
                        break;
                    case R.id.tvInviteWinCoinsCopy:
                        // copy the invite code
                        Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickS_PasteCode,
                                null, null);
                        ClipboardManager manager = (ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        manager.setPrimaryClip(ClipData.newPlainText("text",
                                tvInviteWinCoinsInviteCode.getText().toString()));
                        ToastUtils.showShortToast(InviteWinCoinsActivity.this,
                                R.string.copy_success);
                        break;
                }
            }
        };
        tvInviteWinCoinsCheckDetail.setOnClickListener(l);
        tvInviteWinCoinsCopy.setOnClickListener(l);
        nelInviteWinCoins.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharingInfo();
            }
        });
    }

    private void initView() {
        shareUtils = new ShareUtils(this);

        rlInviteWinCoins.setVisibility(View.GONE);

        tbInviteWinCoins.setTitle(getString(R.string.invite_friends_win_coins));
        tbInviteWinCoins.setDisplayHomeAsUpEnabled(true);

        tvInviteWinCoinsCheckDetail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        SharePlatformAdapter platformAdapter = new SharePlatformAdapter(this);
        gvInviteWinCoins.setAdapter(platformAdapter);
        gvInviteWinCoins.setSelector(R.color.transparent);

        // move scrollview to top
        rlInviteWinCoinsShareCode.setFocusable(true);
        rlInviteWinCoinsShareCode.setFocusableInTouchMode(true);
        rlInviteWinCoinsShareCode.requestFocus();

        refreshDisplay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (shareUtils != null && shareUtils.getCallbackManager() != null)
            shareUtils.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    /**
     * share content
     */
    private void initShareContent() {
        if (AbConfigManager.getInstance(this).getConfig() != null) {
            ShareInfo shareInfo = AbConfigManager.getInstance(this).getConfig().share;
            if (shareInfo != null) {
                facebookShareInfo = shareInfo.facebook;
                lineShareInfo = shareInfo.line;
                otherShareInfo = shareInfo.other;
            }
            UrlInfo urlInfo = AbConfigManager.getInstance(this).getConfig().url;
            if (urlInfo != null)
                winCoinsDetailUrl = urlInfo.invite_win_coins_detail;
        }
    }

    /**
     * get user friends num and coins from sharing
     */
    private void getSharingInfo() {
        pbInviteWinCoins.setVisibility(View.VISIBLE);
        nelInviteWinCoins.setVisibility(View.GONE);
        String uid = PrefUtils.getString(GlobalContext.get(), PrefConstants.UserInfo.UID, "");
        Map<String, Object> param = new HashMap<>();
        param.put("uid", uid);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.INVITE_SHARING, param,
                new Network.JsonCallBack<PojoSharingInfo>() {
                    @Override
                    public void onSuccess(PojoSharingInfo pojoSharingInfo) {
                        if (!AppUtil.isActivityDestroyed(InviteWinCoinsActivity.this)) {
                            pbInviteWinCoins.setVisibility(View.GONE);
                            if (pojoSharingInfo != null) {
                                shareNum = pojoSharingInfo.share_num;
                                shareCoins = pojoSharingInfo.gold_num;
                                inviteCode = pojoSharingInfo.code;
                                rlInviteWinCoins.setVisibility(View.VISIBLE);
                                nelInviteWinCoins.setVisibility(View.GONE);
                                refreshDisplay();
                                initShareContent();
                            } else {
                                nelInviteWinCoins.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailed(int code, String message, Exception e) {
                        if (!AppUtil.isActivityDestroyed(InviteWinCoinsActivity.this)) {
                            nelInviteWinCoins.setVisibility(View.VISIBLE);
                            pbInviteWinCoins.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public Class<PojoSharingInfo> getObjectClass() {
                        return PojoSharingInfo.class;
                    }
                });
    }

    private void refreshDisplay() {
        tvInviteWinCoinsShareNum.setText(getString(R.string.already_share_n_friends,
                String.valueOf(shareNum)));
        tvInviteWinCoinsCoinsNum.setText(getString(R.string.already_gain_n_coins,
                String.valueOf(shareCoins)));
        tvInviteWinCoinsInviteCode.setText(inviteCode);
    }

    /**
     * share invite code in each platform
     *
     * @param platform platform index
     */
    private void toShareInviteCode(int platform) {
        if (shareUtils == null) {
            shareUtils = new ShareUtils(this);
        }

        switch (platform) {
            case 0:
                // line
                lineShare();
                break;
            case 1:
                // facebook
                facebookShare();
                break;
            case 2:
                // messenger
                messengerShare();
                break;
            case 3:
                // sms
                smsShare();
                break;
            case 4:
                // mail
                mailShare();
                break;
        }
    }

    private void smsShare() {
        Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickS_SMS, null, null);
        if (otherShareInfo == null) {
            ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
            return;
        }
        if (!shareUtils.smsShare(replacePlaceHolder(otherShareInfo.title.getText() + "\n" +
                otherShareInfo.content.getText()) + "\n" + otherShareInfo.link))
            ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
    }

    private void mailShare() {
        Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickS_Email, null, null);
        if (otherShareInfo == null) {
            ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
            return;
        }
        if (!shareUtils.emailShare(replacePlaceHolder(otherShareInfo.title.getText()),
                replacePlaceHolder(otherShareInfo.content.getText()) + "\n" + otherShareInfo.link))
            ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
    }

    private void facebookShare() {
        Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickS_Facebook, null, null);
        if (facebookShareInfo == null) {
            ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
            return;
        }
        shareUtils.shareLinkInFacebook(replacePlaceHolder(facebookShareInfo.title.getText()),
                facebookShareInfo.link, facebookShareInfo.icon,
                replacePlaceHolder(facebookShareInfo.content.getText()),
                REQUEST_FACEBOOK_SHARE,
                new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {

                    }

                    @Override
                    public void onCancel() {
                        ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
                    }
                });
    }

    private void messengerShare() {
        Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickS_Messenger, null, null);
        if (facebookShareInfo == null) {
            ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
            return;
        }
        shareUtils.shareLinkInMessenger(replacePlaceHolder(facebookShareInfo.title.getText())
                , facebookShareInfo.link, facebookShareInfo.icon,
                replacePlaceHolder(facebookShareInfo.content.getText()),
                REQUEST_MESSENGER_SHARE,
                new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {

                    }

                    @Override
                    public void onCancel() {
                        ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        ToastUtils.showShortToast(GlobalContext.get(),
                                R.string.not_install_messenger);
                    }
                });
    }

    private void lineShare() {
        Analytics.sendUIEvent(AnalyticsEvents.InviteWinCoins.ClickS_LINE, null, null);
        if (lineShareInfo == null) {
            ToastUtils.showShortToast(GlobalContext.get(), R.string.share_fail);
            return;
        }
        if (!shareUtils.shareTextInLine(replacePlaceHolder(lineShareInfo.title.getText()),
                replacePlaceHolder(lineShareInfo.content.getText() + "\n" + lineShareInfo.link)))
            ToastUtils.showShortToast(GlobalContext.get(), R.string.not_install_line);
    }

    private String replacePlaceHolder(String str) {
        if (!TextUtils.isEmpty(str)) {
            return str.replace(PLACEHOLDER_INVITE_CODE, inviteCode);
        } else {
            return str;
        }
    }
}
