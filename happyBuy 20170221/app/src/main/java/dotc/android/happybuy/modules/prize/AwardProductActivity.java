package dotc.android.happybuy.modules.prize;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoParticpateHistory;
import dotc.android.happybuy.http.result.PojoPrizeInfo;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.address.AddressSelectActivity;
import dotc.android.happybuy.modules.setting.feedback.FeedBackActivity;
import dotc.android.happybuy.modules.show.SharePrizeActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.DateUtil;
import dotc.android.happybuy.util.FormatVerifyUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 * 商品确认
 *
 */
public class AwardProductActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_PRODUCT_ITEM_ID = "extra_product_item_id";
    private HBToolbar mToolbar;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View mLoadingView;
    private View mContentView;


    private ImageView mProductImageView;
    private TextView mProductNameTextView;
    private TextView mProductTimesTextView;
    private TextView mProductStateTextView;

    private TextView mAwardDateTextView;

    private View mRealProductLayout;
    private View mVirtualProductLayout;
    //real

    private View mAddressSelectLayout;
    private View mAddressSelectedLayout;
    private TextView mAddressTextView;
    private TextView mAddressDateTextView;
    private TextView mOrderShippedTime;
    private TextView mOrderReceivedTime;

    //virtual
    private View mVirtualUseSelectLayout;

    private View mWantExchangeCoinLayout;
    private View mWantExchangePhoneLayout;
    private ImageView mWantExchangeCoinCheckbox;
    private ImageView mSelectPhoneNumber;
    private ImageView mSelectWay;
    private ImageView mShareToWinner;
    private ImageView mTransactionSuccess;
    private ImageView mUsePhoneDistributed;


    private ImageView mWantExchangePhoneCheckbox;
    private ImageView mRealSelectAddress;
    private ImageView mReadyToDeliver;
    private ImageView mHasReceived;

    private View mOrderStatusInit,mOrderStatusShipped;
    private Button mSelectExchangeModeButton;

    private TextView mHasReceivedTextView;
    private TextView mOrderStatusTextView;
    private TextView mOrderStatusWeb;
    private TextView mOrderStatusOrderId;

    private View mShareSelectLayout;
    private View mShareSelectedLayout;
    private View mShareLookDetaiLayout;
    private TextView mSharedTimeTextView;
    //    private TextView mAddressTextView;
    private PojoPrizeInfo mPrizeInfo;
//    private PojoParticpateHistory mExtraParticpateHistory;
    private String mExtraProductItemId;

    private static Set<String> mChoisePhoneCache = new HashSet<>();
    private final int EXCHANGE_COIN = 0x01;//1 换金币，2 冲话费
    private final int EXCHANGE_PHONE = 0x02;

    private final int EXCHANGE_RESULT_OK = HttpProtocol.ORDER_STATE.SHIPPED;
    private final int EXCHANGE_RESULT_SUCCESS = HttpProtocol.ORDER_STATE.RECEIVED;
    private final int EXCHANGE_RESULT_ERROR = 0x02;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award_product);
        readExtraFromIntent();
        initActionbar();
        initUI();
        Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Enter_Confirm, null, null);
//        doLoadInfo();
    }

    private void readExtraFromIntent() {
        mExtraProductItemId = getIntent().getStringExtra(EXTRA_PRODUCT_ITEM_ID);
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_goods_confirm);
        mToolbar.setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initUI() {
        mHasReceivedTextView= (TextView) findViewById(R.id.tv_has_reseived);

        mHasReceived= (ImageView) findViewById(R.id.iv_have_received);
        mReadyToDeliver = (ImageView) findViewById(R.id.iv_ready_to_deliver);
        mRealSelectAddress = (ImageView) findViewById(R.id.iv_select_address);
        mShareToWinner = (ImageView) findViewById(R.id.iv_share_to_winner);


        mUsePhoneDistributed= (ImageView) findViewById(R.id.iv_use_phone_distributed);
        mTransactionSuccess = (ImageView)findViewById(R.id.iv_transaction_succeed);
        mSelectPhoneNumber = (ImageView) findViewById(R.id.iv_select_phone_number);
        mSelectWay = (ImageView) findViewById(R.id.iv_select_way);

        mNetworkErrorLayout = (NetworkErrorLayout) findViewById(R.id.layout_network_error);
        mLoadingView = findViewById(R.id.layout_loading);
        mContentView = findViewById(R.id.layout_content);

        mProductImageView = (ImageView) findViewById(R.id.imageview_product);
        mProductNameTextView = (TextView) findViewById(R.id.textview_product_name);
        mProductTimesTextView = (TextView) findViewById(R.id.textview_times);
        mProductStateTextView = (TextView) findViewById(R.id.textview_state);

        mAwardDateTextView = (TextView) findViewById(R.id.textview_award_date);
        mRealProductLayout = findViewById(R.id.layout_real_product);
        mVirtualProductLayout = findViewById(R.id.layout_virtual_product);
        initRealProductUI();
        initVirtualProductUI();

        mOrderStatusShipped=  findViewById(R.id.layout_order_shipped);
        mOrderStatusInit=  findViewById(R.id.layout_order_init);
        mOrderStatusWeb= (TextView) findViewById(R.id.tv_web);
        mOrderStatusOrderId= (TextView) findViewById(R.id.tv_order_id);
//        mOrderStatusTextView = (TextView) findViewById(R.id.textview_order_status);

        mShareSelectLayout = findViewById(R.id.layout_share_select);
        mShareSelectedLayout = findViewById(R.id.layout_share_selected);
        mShareLookDetaiLayout = findViewById(R.id.layout_lookdetail);

        mSharedTimeTextView = (TextView) findViewById(R.id.tv_shared_time);


        mAddressSelectLayout.setOnClickListener(this);
        mShareSelectLayout.setOnClickListener(this);
        mShareLookDetaiLayout.setOnClickListener(this);
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLoadInfo();
            }
        });
        mShareSelectLayout.setClickable(false);
    }
    private void showRedDot(ImageView imageview) {
        if (imageview == mShareToWinner) {
            mShareSelectLayout.setClickable(true);
        }
        mSelectPhoneNumber.setBackgroundResource(R.drawable.grey_circle_selector);
        mRealSelectAddress.setBackgroundResource(R.drawable.grey_circle_selector);
        imageview.setBackgroundResource(R.drawable.circle_dot);

    }

    private void initRealProductUI() {
        mAddressSelectLayout = findViewById(R.id.layout_address_select);
        mAddressSelectedLayout = findViewById(R.id.layout_address_selected);
        mAddressTextView = (TextView) findViewById(R.id.textview_address);
        mAddressDateTextView = (TextView) findViewById(R.id.textview_address_date);

        mOrderShippedTime = (TextView) findViewById(R.id.order_shipped_time);
        mOrderReceivedTime = (TextView) findViewById(R.id.order_received_time);



    }

    private void initVirtualProductUI() {
        mVirtualUseSelectLayout = findViewById(R.id.layout_virtual_use_select);

        mWantExchangeCoinLayout = findViewById(R.id.layout_user_mode_coin);
        mWantExchangePhoneLayout = findViewById(R.id.layout_user_mode_phone);
        mWantExchangeCoinCheckbox = (ImageView) findViewById(R.id.imageview_user_mode_coin);
        mWantExchangePhoneCheckbox = (ImageView) findViewById(R.id.imageview_user_mode_phone);

        mWantExchangeCoinLayout.setOnClickListener(mChoseUserModeListener);
        mWantExchangePhoneLayout.setOnClickListener(mChoseUserModeListener);

        mSelectExchangeModeButton = (Button) findViewById(R.id.button_select_use_mode);
        mSelectExchangeModeButton.setOnClickListener(mChoseUserModeButtonListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doLoadInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_address_select: {
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Select_DeliverAddress, null, null);
                Intent intent = new Intent(this, AddressSelectActivity.class);
                intent.putExtra(AddressSelectActivity.EXTRA_PRODUCT_ITEM_ID, mExtraProductItemId);
                startActivity(intent);
                break;
            }
            case R.id.layout_share_select: {
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_ToShow, null, null);
                Intent shareIntent = new Intent(this, SharePrizeActivity.class);
                shareIntent.putExtra("imageUrl", mPrizeInfo.productImageUrl);
                shareIntent.putExtra("name", mPrizeInfo.productName);
                shareIntent.putExtra("number", mPrizeInfo.periodId);
                shareIntent.putExtra("orderId", mPrizeInfo.orderId);
                startActivity(shareIntent);
                break;
            }
            case R.id.layout_lookdetail:
//                Intent intent = new Intent(this, Share.class);
//                startActivity(intent);
                break;
        }
    }

    private void doLoadInfo() {
        Map<String, Object> param = new HashMap<>();
        param.put("item_id", mExtraProductItemId);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRIZE_INFO, param, new Network.JsonCallBack<PojoPrizeInfo>() {
            @Override
            public void onSuccess(PojoPrizeInfo prizeInfo) {
                HBLog.d(TAG + " onSuccess " + prizeInfo);
                if (!isFinishing()) {
                    mLoadingView.setVisibility(View.GONE);
                    mContentView.setVisibility(View.VISIBLE);
                    initUIWithData(prizeInfo);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                mLoadingView.setVisibility(View.GONE);
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public Class<PojoPrizeInfo> getObjectClass() {
                return PojoPrizeInfo.class;
            }
        });
        mLoadingView.setVisibility(View.VISIBLE);
        mContentView.setVisibility(View.GONE);
        mNetworkErrorLayout.setVisibility(View.GONE);
    }

    private void updateUI() {
        initUIWithData(mPrizeInfo);
    }

    private void initUIWithData(PojoPrizeInfo prizeInfo) {
        this.mPrizeInfo = prizeInfo;
        Glide.with(this).load(prizeInfo.productImageUrl).into(mProductImageView);
        mProductNameTextView.setText(prizeInfo.productName);
        mProductTimesTextView.setText(String.valueOf(prizeInfo.participateTimes));

        //DateUtil.time2ss(prizeInfo.prizeTime * 1000)
        mAwardDateTextView.setText(prizeInfo.prizeTimeShow);

        //中奖  已晒单
        if (prizeInfo.prizeStatus == HttpProtocol.PRODUCT_STATE.ONSALE
                || prizeInfo.prizeStatus == HttpProtocol.PRODUCT_STATE.SOLDOUT
                || prizeInfo.prizeStatus == HttpProtocol.PRODUCT_STATE.AWARDING) {
            mProductStateTextView.setText(R.string.product_state_doing);
        } else {
            if (TextUtils.isEmpty(prizeInfo.shareId)) {
                mProductStateTextView.setText(R.string.product_state_done);
            } else {
                mProductStateTextView.setText(R.string.product_state_share);
            }
        }

        if (prizeInfo.productType == 0) {
            initRealProductUI(prizeInfo);
        } else if (prizeInfo.productType == 1) {
            initVirtualProductUI(prizeInfo);
        }

    }

    private void initRealProductUI(PojoPrizeInfo prizeInfo) {
        if (TextUtils.isEmpty(prizeInfo.address)) {
            mAddressSelectLayout.setVisibility(View.VISIBLE);
            mAddressSelectedLayout.setVisibility(View.GONE);
            showRedDot(mRealSelectAddress);
        } else {
            mAddressSelectLayout.setVisibility(View.GONE);
            mAddressSelectedLayout.setVisibility(View.VISIBLE);
            mAddressTextView.setText(prizeInfo.address);
            mAddressDateTextView.setText(DateUtil.time2ss(prizeInfo.confirmAddressTime * 1000));
            //准备发货
            ((TextView) findViewById(R.id.tv_order_real_deliver)).setTextColor(getResources().getColor(R.color.red));
            showRedDot(mReadyToDeliver);
        }
        if (prizeInfo.ship_status == HttpProtocol.ORDER_STATE.INIT) {

        } else if (prizeInfo.ship_status == HttpProtocol.ORDER_STATE.SHIPPED) {
            mOrderStatusShipped.setVisibility(View.VISIBLE);
            mOrderStatusInit.setVisibility(View.GONE);
            if(prizeInfo.distributeTime>0) mOrderShippedTime.setText(DateUtil.time2ss(prizeInfo.distributeTime * 1000));
            //显示网站和订单
            mOrderStatusWeb.setText(getString(R.string.order_buy_web)+prizeInfo.purchase_url);
            mOrderStatusOrderId.setText(getString(R.string.order_orderd_id) + prizeInfo.purchase_order_code);
            //
            mReadyToDeliver.setBackgroundResource(R.drawable.grey_circle_selector);
            showRedDot(mHasReceived);
        } else if (prizeInfo.ship_status == HttpProtocol.ORDER_STATE.RECEIVED || prizeInfo.ship_status == HttpProtocol.ORDER_STATE.COMPLETE) {
            mOrderStatusShipped.setVisibility(View.VISIBLE);
            mOrderStatusInit.setVisibility(View.GONE);
            if(prizeInfo.distributeTime>0) mOrderShippedTime.setText(DateUtil.time2ss(prizeInfo.distributeTime * 1000));
            if(prizeInfo.receiveTime>0) mOrderReceivedTime.setText(DateUtil.time2ss(prizeInfo.receiveTime * 1000));


            mOrderStatusWeb.setText(getString(R.string.order_buy_web)+prizeInfo.purchase_url);
            mOrderStatusOrderId.setText(getString(R.string.order_orderd_id) + prizeInfo.purchase_order_code);
            //已收货操作
            mHasReceivedTextView.setText(R.string.order_logistics_received);
            mHasReceived.setBackgroundResource(R.drawable.grey_circle_selector);
            mReadyToDeliver.setBackgroundResource(R.drawable.grey_circle_selector);
            if(TextUtils.isEmpty(prizeInfo.shareId)){
                showRedDot(mShareToWinner);
            }else {
                mShareToWinner.setBackgroundResource(R.drawable.grey_circle_selector);
            }

        } else if (prizeInfo.ship_status == HttpProtocol.ORDER_STATE.EXCEPTION) {

            String click=getString(R.string.recharge_to_coin_result_fail_click);
            String contect=getString(R.string.recharge_to_coin_result_fail_contact_customer_service);
            SpannableStringBuilder style =new SpannableStringBuilder(click+contect);
            style.setSpan(new ForegroundColorSpan(Color.RED), click.length(), style.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView) findViewById(R.id.tv_order_real_deliver)).setText(style);

            findViewById(R.id.tv_order_real_deliver).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AwardProductActivity.this, FeedBackActivity.class);
                    intent.putExtra(FeedBackActivity.FROM_KEY, FeedBackActivity.FROM_HELP_EXCEPTION);
                    startActivity(intent);
                }
            });
        }

        if (TextUtils.isEmpty(prizeInfo.shareId)) {
            mShareSelectLayout.setVisibility(View.VISIBLE);
            mShareSelectedLayout.setVisibility(View.GONE);
        } else {
            mShareSelectLayout.setVisibility(View.GONE);
            mShareSelectedLayout.setVisibility(View.VISIBLE);
            mSharedTimeTextView.setText(DateUtil.time2ss(prizeInfo.shareTime * 1000));

        }
    }

    private void initVirtualProductUI(PojoPrizeInfo prizeInfo) {
        mVirtualProductLayout.setVisibility(View.VISIBLE);
        mRealProductLayout.setVisibility(View.GONE);
        if (prizeInfo.useMode == 0 && !mChoisePhoneCache.contains(prizeInfo.orderId)) {//未选择
            findViewById(R.id.layout_virtual_use_select).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_virtual_use_phone).setVisibility(View.GONE);
            findViewById(R.id.layout_virtual_use_coin).setVisibility(View.GONE);
            showRedDot(mSelectWay);

            String text = getString(R.string.use_mode_coin, String.valueOf(prizeInfo.exchangeCoinCount));
            ((TextView) findViewById(R.id.textview_use_mode_coin)).setText(text);
        } else {
            if (prizeInfo.useMode == EXCHANGE_COIN) {//充值到金币
                findViewById(R.id.layout_virtual_use_select).setVisibility(View.GONE);
                findViewById(R.id.layout_virtual_use_phone).setVisibility(View.GONE);
                findViewById(R.id.layout_virtual_use_coin).setVisibility(View.VISIBLE);

                if(prizeInfo.ship_status == HttpProtocol.ORDER_STATE.FAIL||prizeInfo.ship_status == HttpProtocol.ORDER_STATE.EXCEPTION){
                    //兑换失败
                    showRedDot(mUsePhoneDistributed);

                    String click=getString(R.string.recharge_to_coin_result_fail_click);
                    String contect=getString(R.string.recharge_to_coin_result_fail_contact_customer_service);
                    SpannableStringBuilder style =new SpannableStringBuilder(click+contect);
                    style.setSpan(new ForegroundColorSpan(Color.RED), click.length(), style.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((TextView) findViewById(R.id.textview_use_coin_result_desc)).setText(style);

                    ((TextView) findViewById(R.id.textview_use_coin_result)).setText(R.string.recharge_to_coin_fail);
                    ((TextView) findViewById(R.id.textview_use_coin_date)).setText(DateUtil.time2ss(prizeInfo.confirmExchangeCoinTime * 1000));

                    findViewById(R.id.textview_use_coin_result_desc).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AwardProductActivity.this, FeedBackActivity.class);
                            intent.putExtra(FeedBackActivity.FROM_KEY, FeedBackActivity.FROM_HELP_EXCEPTION);
                            startActivity(intent);
                        }
                    });
                }else if(prizeInfo.ship_status == HttpProtocol.ORDER_STATE.INIT){
//                    findViewById(R.id.ll_transaction).setVisibility(View.GONE);
                    findViewById(R.id.ll_use_coin_result_desc).setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.textview_use_coin_result)).setText(R.string.order_logistics_shippe);
                    ((TextView) findViewById(R.id.textview_use_coin_result_desc)).setText(R.string.recharge_to_coin_result);
                    showRedDot(mUsePhoneDistributed);

                }else {
                    ((TextView) findViewById(R.id.textview_use_coin_date)).setText(DateUtil.time2ss(prizeInfo.confirmExchangeCoinTime * 1000));
                    ((TextView) findViewById(R.id.textview_use_coin_result)).setText(R.string.recharge_to_coin_success);
                    ((TextView) findViewById(R.id.textview_use_coin_result_desc)).setText(R.string.recharge_to_coin_result);
                    if(TextUtils.isEmpty(prizeInfo.shareId)){
                        showRedDot(mShareToWinner);
                    }else {
                        mSelectPhoneNumber.setBackgroundResource(R.drawable.grey_circle_selector);
                        mShareToWinner.setBackgroundResource(R.drawable.grey_circle_selector);
                    }
                }

            } else if (prizeInfo.useMode == EXCHANGE_PHONE || mChoisePhoneCache.contains(prizeInfo.orderId)) {//充值到手机
                findViewById(R.id.layout_virtual_use_select).setVisibility(View.GONE);
                findViewById(R.id.layout_virtual_use_phone).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_virtual_use_coin).setVisibility(View.GONE);
                showRedDot(mSelectPhoneNumber);
                mTransactionSuccess.setBackgroundResource(R.drawable.white_circle_selector);
                //ui
                //选择时间
                //选择方式
                if(isEmptyFixed(prizeInfo.confirmExchangePhone)){//未选择手机号
                    //ui
                    findViewById(R.id.layout_recharge_phone_select).setVisibility(View.VISIBLE);
                    findViewById(R.id.layout_recharge_phone_select).setOnClickListener(mInputPhoneNumberListener);
                    findViewById(R.id.layout_recharge_phone_selected).setVisibility(View.GONE);

                    findViewById(R.id.layout_use_phone_distribute).setVisibility(View.VISIBLE);
                    findViewById(R.id.layout_use_phone_distributed).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.layout_recharge_phone_select).setVisibility(View.GONE);
                    findViewById(R.id.layout_recharge_phone_selected).setVisibility(View.VISIBLE);

                    findViewById(R.id.layout_use_phone_distribute).setVisibility(View.GONE);
                    findViewById(R.id.layout_use_phone_distributed).setVisibility(View.VISIBLE);
                    if (prizeInfo.ship_status == EXCHANGE_RESULT_SUCCESS||prizeInfo.ship_status == HttpProtocol.ORDER_STATE.COMPLETE) {
                        //充值成功
                        ((TextView) findViewById(R.id.textview_use_phone_result)).setText(R.string.recharge_to_phone_success);
                        ((TextView) findViewById(R.id.textview_use_phone_result_date)).setText(DateUtil.time2ss(prizeInfo.confirmExchangePhoneTime*1000));
                        String descText = getString(R.string.recharge_to_phone_result,prizeInfo.confirmExchangePhone);
                        ((TextView) findViewById(R.id.textview_use_phone_result_desc)).setText(descText);

                        //TODO 待改善 。。。
                        mTransactionSuccess.setBackgroundResource(R.drawable.grey_circle_selector);
                        if(TextUtils.isEmpty(prizeInfo.shareId)){
                            showRedDot(mShareToWinner);
                        }else {
                            mSelectPhoneNumber.setBackgroundResource(R.drawable.grey_circle_selector);
                            mShareToWinner.setBackgroundResource(R.drawable.grey_circle_selector);
                        }
                    } else if(prizeInfo.ship_status == HttpProtocol.ORDER_STATE.SHIPPED){//手机充值待发货状态
                        findViewById(R.id.layout_use_phone_distribute).setVisibility(View.VISIBLE);
                        findViewById(R.id.layout_use_phone_distributed).setVisibility(View.GONE);

                        findViewById(R.id.layout_recharge_phone_select).setVisibility(View.GONE);
                        findViewById(R.id.layout_recharge_phone_selected).setVisibility(View.GONE);
                        findViewById(R.id.ll_phone_number_have_input).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.textview_use_phone_result_input)).setText(prizeInfo.confirmExchangePhone);
                        showRedDot(mTransactionSuccess);
                    } else {
                        //充值失败显示到充值红点
                        showRedDot(mTransactionSuccess);

                        ((TextView) findViewById(R.id.textview_use_phone_result)).setText(R.string.recharge_to_phone_fail);
                        ((TextView) findViewById(R.id.textview_use_phone_result_date)).setText(DateUtil.time2ss(prizeInfo.confirmExchangePhoneTime*1000));
                        String click = getString(R.string.recharge_to_phone_result_fail_phonenumber_click,prizeInfo.confirmExchangePhone);
                        String contect=getString(R.string.recharge_to_coin_result_fail_contact_customer_service);
                        SpannableStringBuilder style =new SpannableStringBuilder(click+contect);
                        style.setSpan(new ForegroundColorSpan(Color.RED), click.length(), style.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ((TextView) findViewById(R.id.textview_use_phone_result_desc)).setText(style);

                        findViewById(R.id.textview_use_phone_result_desc).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AwardProductActivity.this, FeedBackActivity.class);
                                intent.putExtra(FeedBackActivity.FROM_KEY, FeedBackActivity.FROM_HELP_EXCEPTION);
                                startActivity(intent);
                            }
                        });
                    }
                }
            } else {
                findViewById(R.id.layout_virtual_use_select).setVisibility(View.GONE);
                findViewById(R.id.layout_virtual_use_phone).setVisibility(View.GONE);
                findViewById(R.id.layout_virtual_use_coin).setVisibility(View.GONE);
            }
        }

        if (TextUtils.isEmpty(prizeInfo.shareId)) {
            mShareSelectLayout.setVisibility(View.VISIBLE);
            mShareSelectedLayout.setVisibility(View.GONE);
        } else {
            mShareSelectLayout.setVisibility(View.GONE);
            mShareSelectedLayout.setVisibility(View.VISIBLE);
            mSharedTimeTextView.setText(DateUtil.time2ss(prizeInfo.shareTime * 1000));
        }

    }

    private View.OnClickListener mInputPhoneNumberListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showInputPhoneDialog();
        }
    };

    private int mSelectUseModeResId = R.id.layout_user_mode_coin;

    private View.OnClickListener mChoseUserModeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_user_mode_coin:
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Card_Select_Coin, null, null);
                    mWantExchangeCoinCheckbox.setImageResource(R.drawable.ic_checkbox_check);
                    mWantExchangePhoneCheckbox.setImageResource(R.drawable.ic_checkbox_uncheck);
                    mSelectUseModeResId = R.id.layout_user_mode_coin;
                    break;
                case R.id.layout_user_mode_phone:
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Card_Select_Phone, null, null);
                    mWantExchangeCoinCheckbox.setImageResource(R.drawable.ic_checkbox_uncheck);
                    mWantExchangePhoneCheckbox.setImageResource(R.drawable.ic_checkbox_check);
                    mSelectUseModeResId = R.id.layout_user_mode_phone;
                    break;
            }
        }
    };

    private View.OnClickListener mChoseUserModeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSelectUseModeResId == R.id.layout_user_mode_phone) {
                mChoisePhoneCache.add(mPrizeInfo.orderId);
                updateUI();
            } else {
                doExchangeCoin();
            }
        }
    };

    private void doExchangeCoin() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.commiting));
        dialog.show();
        Map<String, Object> param = new HashMap<>();
        param.put("item_id", mExtraProductItemId);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.AWARD_EXCHANGE_COIN, param, new Network.JsonCallBack<PojoPrizeInfo>() {
            @Override
            public void onSuccess(PojoPrizeInfo prizeInfo) {
                doLoadInfo();
                /*HBLog.d(TAG + " onSuccess " + prizeInfo);
                mPrizeInfo.useMode = EXCHANGE_COIN;
                mPrizeInfo.ship_status = HttpProtocol.ORDER_STATE.INIT;
                mPrizeInfo.confirmExchangeCoinTime = System.currentTimeMillis() / 1000;
                updateUI();*/
                dialog.dismiss();
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                dialog.dismiss();
                ToastUtils.showLongToast(GlobalContext.get(), R.string.recharge_to_coin_fail);
            }

            @Override
            public Class<PojoPrizeInfo> getObjectClass() {
                return PojoPrizeInfo.class;
            }
        });
    }

    private void doExchangeCard(final String phone) {
        mPrizeInfo.confirmExchangePhone=phone;
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.commiting));
        dialog.show();
        Map<String, Object> param = new HashMap<>();
        param.put("item_id", mExtraProductItemId);
        param.put("msisdn", phone);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.AWARD_RECHARGE_CARD, param, new Network.JsonCallBack<PojoPrizeInfo>() {
            @Override
            public void onSuccess(PojoPrizeInfo prizeInfo) {
                HBLog.d(TAG + " onSuccess " + prizeInfo);
                mPrizeInfo.confirmExchangePhone = phone;
                mPrizeInfo.useMode = EXCHANGE_PHONE;
//                mPrizeInfo.ship_status = EXCHANGE_RESULT_OK;
                mPrizeInfo.ship_status = HttpProtocol.ORDER_STATE.SHIPPED;
                mPrizeInfo.confirmExchangePhoneTime = System.currentTimeMillis() / 1000;
                updateUI();
                dialog.dismiss();
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                dialog.dismiss();
                ToastUtils.showLongToast(GlobalContext.get(), R.string.recharge_to_phone_fail);
                updateUI();
            }

            @Override
            public Class<PojoPrizeInfo> getObjectClass() {
                return PojoPrizeInfo.class;
            }
        });
    }

    private void showInputPhoneDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_edit_name, null);
        final EditText editText = (EditText) view.findViewById(R.id.edittext);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.recharge_phone_input_hint);
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
                    ToastUtils.showLongToast(AwardProductActivity.this, R.string.nickname_empty_hint);
                }else if (!isPhone(name)) {
                    ToastUtils.showLongToast(AwardProductActivity.this, R.string.address_edit_condition_mobile_tips);
                }else {
                    dialog.dismiss();
                    doExchangeCard(FormatVerifyUtil.checkMobileNumber(AwardProductActivity.this,name));

                }
            }
        });
        builder.create().show();
    }
    private boolean isPhone(String name){
        /*if(AppUtil.getMetaData(this,"country").equals("vn")){
            return FormatVerifyUtil.isVnMobileNO(name);
        }*/
      return   FormatVerifyUtil.isMobileNO(this,name);
    }

    private boolean isEmptyFixed(String value){
        if(TextUtils.isEmpty(value)){
            return true;
        }
        return "0".equals(value);
    }

}
