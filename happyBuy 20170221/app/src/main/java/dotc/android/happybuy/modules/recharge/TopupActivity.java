package dotc.android.happybuy.modules.recharge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.ConfigManager;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.http.result.PojoPayConfig;
import dotc.android.happybuy.http.result.PojoPayItems;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.recharge.engine.ControllerFactory;
import dotc.android.happybuy.modules.recharge.engine.PaymentController;
import dotc.android.happybuy.modules.recharge.widget.ChannelClusterLayout;
import dotc.android.happybuy.modules.recharge.widget.FaceValueClusterLayout;
import dotc.android.happybuy.modules.schema.SchemeProcessor;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.activity.WebActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/12/16.
 */

public class TopupActivity extends BaseActivity {

    public final static String EXTRA_PAYMENT_POS = "extra_payment_pos";
    public final static String EXTRA_ACTIVITY_FROM = "extra_activity_from";
    public static final String ACTIVITY_FROM_HOME = "Home";
    public static final String ACTIVITY_FROM_PERSONAL = "PersonalCenter";
    public static final String ACTIVITY_FROM_PAY = "pay";
    public static final String ACTIVITY_FROM_ACTIVE = "active";
    private static final int REQUEST_CODE_SMS = 100;

    public String mExtraFrom;
    public int mExtraPaymentPosition;

    private List<PojoPay> mPayConfigs;

    private View mLoadingView;
    private View mContentView;
    private NetworkErrorLayout mNetworkErrorLayout;

    private View mTipsView;

    private HBToolbar mToolbar;
    private FaceValueClusterLayout mFaceValueClusterLayout;
    private ChannelClusterLayout mPaymentClusterLayout;
    private TextView mPriceTextView;
    private TextView mCoinsTextView;
    private TextView mButton;
    private TextView mIknowButton;
    private TextView mTvTopUpTips;
    private RelativeLayout mRlTopUpTips;

    private PaymentController mController;
    private PojoPay mCheckedPay;
    private PojoPayItems mCheckedPayItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readExtraFromIntent();
        setContentView(R.layout.activity_recharge);
        initActionbar();
        initUI();
        setupListeners();
        initData();
        loadTopupData();
        mController.onCreate(this);
    }

    private void readExtraFromIntent() {
        if (getIntent().hasExtra(EXTRA_ACTIVITY_FROM)) {
            mExtraFrom = getIntent().getStringExtra(EXTRA_ACTIVITY_FROM);
        } else {
            mExtraFrom = "unknown";
        }
        if (getIntent().hasExtra(EXTRA_PAYMENT_POS)) {
            mExtraPaymentPosition = getIntent().getIntExtra(EXTRA_PAYMENT_POS, 0);
        }
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_recharge);
        mToolbar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setRightItem(R.drawable.ic_instruction, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                SchemeProcessor.handle(TopupActivity.this, AbConfigManager.getInstance(GlobalContext.get()).getConfig().topup.event_url);
//                String url = ConfigManager.get(TopupActivity.this).getH5Config().rechargeInstructions;
//                Intent intent = new Intent(TopupActivity.this, WebActivity.class);
//                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(url));
//                intent.putExtra(WebActivity.EXTRA_TITLE, getString(R.string.activity_recharge_instruction));
//                startActivity(intent);
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Click_Recharge_Directions, null, null);
            }
        });
    }

    private void initUI() {
        mLoadingView = findViewById(R.id.layout_loading);
        mContentView = findViewById(R.id.layout_content);
        mNetworkErrorLayout = (NetworkErrorLayout) findViewById(R.id.layout_network_error);
        mTipsView = findViewById(R.id.layout_tips);

        mIknowButton = (TextView) findViewById(R.id.tips_iknow);
        mTvTopUpTips = (TextView) findViewById(R.id.tv_topup_tips);
        mRlTopUpTips = (RelativeLayout) findViewById(R.id.rl_topup_tips);


        mFaceValueClusterLayout = (FaceValueClusterLayout) findViewById(R.id.layout_coin_value);
        mPaymentClusterLayout = (ChannelClusterLayout) findViewById(R.id.layout_payment_channel);
        mPriceTextView = (TextView) findViewById(R.id.textview_price);
        mCoinsTextView = (TextView) findViewById(R.id.textview_coins);
        mButton = (TextView) findViewById(R.id.button_recharge);
    }

    private void setupListeners() {
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTopupData();
            }
        });
        mIknowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTipsView.setVisibility(View.GONE);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Click_Recharge, mExtraFrom, null);
                doPayPressed(v);
            }
        });

        mPaymentClusterLayout.setOnItemCheckedListener(new ChannelClusterLayout.OnItemCheckedListener() {
            @Override
            public void onItemCheckedChanged(int newPos, int oldPos) {
                mCheckedPay = mPayConfigs.get(newPos);
                mFaceValueClusterLayout.setFaceValues(mCheckedPay.items);
//                mController.
            }
        });
        mFaceValueClusterLayout.setOnItemCheckedListener(new FaceValueClusterLayout.OnItemCheckedListener() {
            @Override
            public void onItemCheckedChanged(int newPos, int oldPos) {
                if (newPos >= 0) {
                    mCheckedPayItem = mCheckedPay.items.get(newPos);
                    mPriceTextView.setText(mCheckedPayItem.price + Languages.getInstance().getSymbol());
                    mCoinsTextView.setText(getString(R.string.lable_coin_1, mCheckedPayItem.coin_num + mCheckedPayItem.give_coin));
                } else {
                    mCheckedPayItem = null;
                }

            }
        });
    }

    private void initData() {
        String type = PrefUtils.getString(PrefConstants.UserInfo.BIND_TYPE, "");
        if (HttpProtocol.UserType.ANONYMOUS.equals(type)) {
            mRlTopUpTips.setVisibility(View.VISIBLE);
        } else {
            mRlTopUpTips.setVisibility(View.GONE);
        }
        mTvTopUpTips.setText(AbConfigManager.getInstance(this).getConfig().topup.tips.getText());
        mController = ControllerFactory.creator(this);

    }

    private void loadTopupData() {
        mLoadingView.setVisibility(View.VISIBLE);
        mNetworkErrorLayout.setVisibility(View.GONE);
        mContentView.setVisibility(View.INVISIBLE);
        String url = HttpProtocol.URLS.RECHARGE_LIST;
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", HttpProtocol.AppId.APP_ID);
        params.put("bid", AppUtil.getBucketId(this));
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoPayConfig>() {
            @Override
            public void onSuccess(PojoPayConfig config) {
                HBLog.d(TAG + " fetchRemoteConfig onSuccess " + config);
                mLoadingView.setVisibility(View.GONE);
                mNetworkErrorLayout.setVisibility(View.GONE);
                mContentView.setVisibility(View.VISIBLE);
                fillUIWithData(config);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " fetchRemoteConfig onFailed " + code + " " + message + " " + e);
                mLoadingView.setVisibility(View.GONE);
                mContentView.setVisibility(View.INVISIBLE);
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public Class<PojoPayConfig> getObjectClass() {
                return PojoPayConfig.class;
            }
        });
    }

    private void fillUIWithData(PojoPayConfig config) {
        this.mPayConfigs = filterList(config.list);
        mController.setPayments(mPayConfigs, mExtraPaymentPosition);
    }

    private List<PojoPay> filterList(List<PojoPay> list) {
        List<PojoPay> newList = new ArrayList<>();
        for (PojoPay pay : list) {
            if (pay.items != null && pay.items.size() > 0) {
                newList.add(pay);
            }
        }
        return newList;
    }

    public void doPayPressed(final View view) {
        HBLog.d(TAG, "doPayPressed");
        mController.startPay(view, mCheckedPay, mCheckedPayItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mController.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.onPause();
    }

    @Override
    protected void onDestroy() {
        mController.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mController.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
