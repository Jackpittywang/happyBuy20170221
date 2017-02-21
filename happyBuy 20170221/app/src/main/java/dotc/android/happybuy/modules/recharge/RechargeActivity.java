package dotc.android.happybuy.modules.recharge;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.IPayCallback;
import com.bluepay.pay.PublisherCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.ConfigManager;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoOrder;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.http.result.PojoPayItems;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.recharge.vendor.billing.BillingEnv;
import dotc.android.happybuy.modules.recharge.vendor.bluepay.BluePayEnv;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.activity.WebActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.modules.recharge.widget.CoinPaymentMethodImageLayout;
import dotc.android.happybuy.modules.recharge.widget.CoinValueClusterLayout;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/3/28.
 * instead of TopupActivity
 */
@Deprecated
public class RechargeActivity extends BaseActivity implements View.OnClickListener {

    public final static String EXTRA_ACTIVITY_FROM = "extra_activity_from";
    public static final String ACTIVITY_FROM_HOME = "Home";
    public static final String ACTIVITY_FROM_PERSONAL = "PersonalCenter";
    public static final String ACTIVITY_FROM_PAY = "pay";
    public static final String ACTIVITY_FROM_ACTIVE = "active";
    private static final int REQUEST_CODE_SMS = 100;

    public String mExtraFrom;

    private HBToolbar mToolbar;

    private CoinValueClusterLayout mCoinValueClusterLayout;
    private TextView mPriceTextView;
    private TextView mButton;

    private TextView mIknow;
    private  LinearLayout mTips;

    private String mCheckedTag;
    private CoinPaymentMethodImageLayout paymentLayout;
    private PojoOrder mPojoOrder;
    private BillingEnv mBillingEnv;
    private BluePayEnv mBluePayEnv;





//    private final String SKU_GAS = "coin1";
    private final String TAG_SMS = "31";
    private final String TAG_BILLING = "1";
    private final String TAG_AIS = "33";
    private final String TAG_BANK = "35";
    private final String TAG_DTAC = "34";
    private final String TAG_TRUEMONEY = "32";
    private final String TAG_LINEPAY = "36";

    //越南
    private final String BLUEPAY_VIETTEL = "41";
    private final String BLUEPAY_VINAPHONE= "42";
    private final String BLUEPAY_MOBIFONE = "43";
    private final String BLUEPAY_VTC ="44";
    private final String BLUEPAY_HOPE = "45";
    private final String BLUEPAY_VIETNAM_SMS = "46";

    private final String GOOGLEPAY ="55";

    //印尼
    private final String BLUEPAY_MOGPLAY = "51";
    private final  String BLUEPAY_OFFLINE_ATM = "52";
    private final  String BLUEPAY_OFFLINE_OTC ="53";
    private final  String BLUEPAY_INDONESIA_SMS ="54";


    private List<PojoPayItems> billingConfig;
    private List<PojoPayItems> smsConfig;
    private List<PojoPayItems> bankConfig;
    private List<PojoPayItems> aisConfig;
    private List<PojoPayItems> dtacConfig;
    private List<PojoPayItems> truemoneyConfig;
    private List<PojoPayItems> linepayConfig;

    //越南
    private List<PojoPayItems> viettelConfig;
    private List<PojoPayItems> vinaphoneConfig;
    private List<PojoPayItems> mobifoneConfig;
    private List<PojoPayItems> vtcConfig;
    private List<PojoPayItems> hopeConfig;
    private List<PojoPayItems> vietnamSmsConfig;
    private List<PojoPayItems> googlePayConfig;

    //印尼
    private List<PojoPayItems> mogplayConfig;
    private List<PojoPayItems> atmConfig;
    private List<PojoPayItems> otcConfig;
    private List<PojoPayItems> indonesiaSmsConfig;

    private List<PojoPay> pojoPays;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readExtraFromIntent();
        setContentView(R.layout.activity_recharge2);
        country=AppUtil.getMetaData(this,"country");
        initActionbar();
        initUI();
        initData();
        requestPermissionIfNeeded();

//        queryReChargeInfo();
    }
    private void readExtraFromIntent() {
        if(getIntent().hasExtra(EXTRA_ACTIVITY_FROM)){
            mExtraFrom = getIntent().getStringExtra(EXTRA_ACTIVITY_FROM);
        } else {
            mExtraFrom = "unknown";
        }
    }
    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_recharge);
        mToolbar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setRightItem(R.drawable.ic_instruction, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                String url = ConfigManager.get(RechargeActivity.this).getH5Config().rechargeInstructions;
                Intent intent = new Intent(RechargeActivity.this, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(url));
                intent.putExtra(WebActivity.EXTRA_TITLE, getString(R.string.activity_recharge_instruction));
                startActivity(intent);
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Click_Recharge_Directions, null, null);
            }
        });
    }

    private void initData(){
        mBillingEnv = new BillingEnv(this);
        mBluePayEnv = new BluePayEnv(this);

//        billingConfig = ConfigManager.get(this).getRechargeConfig().billingConfig;
//        smsConfig = ConfigManager.get(this).getRechargeConfig().smsConfig;
//        bankConfig = ConfigManager.get(this).getRechargeConfig().bankConfig;
//        aisConfig = ConfigManager.get(this).getRechargeConfig().aisConfig;
//        truemoneyConfig = ConfigManager.get(this).getRechargeConfig().truemoneyConfig;
//        dtacConfig = ConfigManager.get(this).getRechargeConfig().dtacConfig;
//        linepayConfig = ConfigManager.get(this).getRechargeConfig().linepayConfig;

        if(ConfigManager.get(this).getRechargeConfig().pojoPays.size()>0){
            pojoPays = ConfigManager.get(this).getRechargeConfig().pojoPays;
            paymentLayout.setCountValues(pojoPays,0);
        }
//        country=AppUtil.getMetaData(this,"country");
        if(country.equals("th")){
            initDataTh();
        }else if(country.equals("vn")){
            initDataVn();
        }else if(country.equals("id")){
            initDataId();
        }

        if(pojoPays.size()>0){
            onPaymentChanged(pojoPays.get(0).id);
        }
    }

    private void initDataTh(){
        if(ConfigManager.get(this).getRechargeConfig().smsConfig.size()>0){
            smsConfig=ConfigManager.get(this).getRechargeConfig().smsConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().billingConfig.size()>0){
            billingConfig=ConfigManager.get(this).getRechargeConfig().billingConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().bankConfig.size()>0){
            bankConfig=ConfigManager.get(this).getRechargeConfig().bankConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().truemoneyConfig.size()>0){
            truemoneyConfig=ConfigManager.get(this).getRechargeConfig().truemoneyConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().dtacConfig.size()>0){
            dtacConfig=ConfigManager.get(this).getRechargeConfig().dtacConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().linepayConfig.size()>0){
            linepayConfig=ConfigManager.get(this).getRechargeConfig().linepayConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().aisConfig.size()>0){
            aisConfig=ConfigManager.get(this).getRechargeConfig().aisConfig;
        }
    }

    private void initDataVn(){
        if(ConfigManager.get(this).getRechargeConfig().viettelConfig.size()>0){
            viettelConfig=ConfigManager.get(this).getRechargeConfig().viettelConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().vinaphoneConfig.size()>0){
            vinaphoneConfig=ConfigManager.get(this).getRechargeConfig().vinaphoneConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().mobifoneConfig.size()>0){
            mobifoneConfig=ConfigManager.get(this).getRechargeConfig().mobifoneConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().vtcConfig.size()>0){
            vtcConfig=ConfigManager.get(this).getRechargeConfig().vtcConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().hopeConfig.size()>0){
            hopeConfig=ConfigManager.get(this).getRechargeConfig().hopeConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().vietnamSmsConfig.size()>0){
            vietnamSmsConfig=ConfigManager.get(this).getRechargeConfig().vietnamSmsConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().googlePayConfig.size()>0){
            googlePayConfig=ConfigManager.get(this).getRechargeConfig().googlePayConfig;
        }
    }

    private void initDataId(){
        if(ConfigManager.get(this).getRechargeConfig().mogplayConfig.size()>0){
            mogplayConfig=ConfigManager.get(this).getRechargeConfig().mogplayConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().atmConfig.size()>0){
            atmConfig=ConfigManager.get(this).getRechargeConfig().atmConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().otcConfig.size()>0){
            otcConfig=ConfigManager.get(this).getRechargeConfig().otcConfig;
        }
        if(ConfigManager.get(this).getRechargeConfig().indonesiaSmsConfig.size()>0){
            indonesiaSmsConfig=ConfigManager.get(this).getRechargeConfig().indonesiaSmsConfig;
        }
    }


    private void initUI() {
        mIknow= (TextView) findViewById(R.id.tips_iknow);
        mIknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTips.setVisibility(View.GONE);
            }
        });
        mTips= (LinearLayout) findViewById(R.id.ll_tips);
        mCoinValueClusterLayout = (CoinValueClusterLayout) findViewById(R.id.layout_coin_value);
        mPriceTextView = (TextView) findViewById(R.id.textview_price);
//        paymentLayout = (LinearLayout) findViewById(R.id.layout_payment_channel);
        paymentLayout = (CoinPaymentMethodImageLayout) findViewById(R.id.layout_payment_channel);
        /*for(int i=0;i<paymentLayout.getChildCount();i++){
            ViewGroup view = (ViewGroup) paymentLayout.getChildAt(i);
            for(int m=0;m<view.getChildCount();m++){
                ImageView imageView = (ImageView) view.getChildAt(m);
                String tag = (String) imageView.getTag();
                if(!TextUtils.isEmpty(tag)){
                    mCheckboxs.add(imageView);
                    imageView.setOnClickListener(mCheckBoxListener);
                }
            }
        }*/

        mButton = (TextView) findViewById(R.id.button_recharge);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Click_Recharge, mExtraFrom, null);
                doPayPressed(v);

            }
        });
        mCoinValueClusterLayout.setOnItemClickListener(new CoinValueClusterLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int coin, int position) {
//                mPriceTextView.setText(coin + "฿");
                mPriceTextView.setText(coin + Languages.getInstance().getSymbol());
//                updateCoinHint(recharge);
            }
        });

        paymentLayout.setOnItemClickListener(new CoinPaymentMethodImageLayout.OnItemImageClickListener() {
            @Override
            public void onItemClick(View view,List<ImageView> mCheckboxs) {
                for (int i=0;i<mCheckboxs.size();i++){
                    ImageView imageView = mCheckboxs.get(i);
                    if(imageView.equals(view)){
                        setCheckedImage(imageView,true);
                    } else {
                        setCheckedImage(imageView,false);
                    }
                }
                if(view.getTag().equals(TAG_DTAC)||view.getTag().equals(TAG_AIS)||view.getTag().equals(TAG_TRUEMONEY)){
                    //显示提示
                    mTips.setVisibility(View.VISIBLE);
                }else {
                    mTips.setVisibility(View.GONE);
                }
                onPaymentChanged((String) view.getTag());

            }
        });
    }

    private void requestPermissionIfNeeded(){
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS}, REQUEST_CODE_SMS);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        HBLog.d(TAG + " onRequestPermissionsResult requestCode:" + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_SMS:
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

    public void doPayPressed(final View view){

//        String country=AppUtil.getMetaData(this,"country");
        if(country.equals("th")){
            doPayPressedTh(view);
        }else if(country.equals("vn")){
            doPayPressedVn(view);
        }else if(country.equals("id")){
            doPayPressedId(view);
        }
    }

    private void doPayPressedTh(View view){
        String analyEvent;
        if(TAG_SMS.equals(mCheckedTag)){
            analyEvent=AnalyticsEvents.Recharge.Pay_sms;
//            payByBluePaySms();
            showBluepaySmsDialog(smsConfig,mCheckedTag);
        } else if(TAG_BILLING.equals(mCheckedTag)){
            analyEvent=AnalyticsEvents.Recharge.Pay_google;
            payByBilling();
        } else if(TAG_AIS.equals(mCheckedTag)){
            analyEvent=AnalyticsEvents.Recharge.Pay_12call;
            payByBluePay12CallCard();
        } else if(TAG_BANK.equals(mCheckedTag)){
            analyEvent=AnalyticsEvents.Recharge.Pay_bank;
//            payByBluePayBank();
            showBluepaySmsDialog(bankConfig,mCheckedTag);
        } else if(TAG_DTAC.equals(mCheckedTag)){
            analyEvent=AnalyticsEvents.Recharge.Pay_happy;
            payByBluePayHappyCard();
        } else if(TAG_TRUEMONEY.equals(mCheckedTag)){
            analyEvent=AnalyticsEvents.Recharge.Pay_true;
            payByBluePayTrueMoneyCard();
        } else if(TAG_LINEPAY.equals(mCheckedTag)){
            analyEvent=AnalyticsEvents.Recharge.Pay_line;
            payByBluePayLine();
        } else {
            analyEvent=AnalyticsEvents.Recharge.Pay_unknown;
        }
        Analytics.sendUIEvent(analyEvent, null, null);
    }

    private void doPayPressedVn(View view){
        String analyEvent;
        if(BLUEPAY_VIETTEL.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_bank;
            payByBluePayViettel();
        } else if(BLUEPAY_VINAPHONE.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_happy;
            payByBluePayVinaphone();
        } else if(BLUEPAY_MOBIFONE.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_true;
            payByBluePayMobifone();
        } else if(BLUEPAY_VTC.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_line;
            payByBluePayVtc();
        }else if(BLUEPAY_HOPE.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_line;
            payByBluePayHope();
        }else if(BLUEPAY_VIETNAM_SMS.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_line;
//            payByBluePayVietnamSms();
            showBluepaySmsDialog(vietnamSmsConfig,mCheckedTag);
        }else if(GOOGLEPAY.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_line;
            payByGooglePay();
        }
//        Analytics.sendUIEvent(analyEvent, null, null);

    }

    private void doPayPressedId(View view){
        if(BLUEPAY_MOGPLAY.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_line;
            payByBluePayMogplay();
        }else if(BLUEPAY_OFFLINE_ATM.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_line;
            payByOfflineAtm();
        }else if(BLUEPAY_OFFLINE_OTC.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_line;
            payByOfflineOtc();
        }else if(BLUEPAY_INDONESIA_SMS.equals(mCheckedTag)){
//            mRechargeTag=AnalyticsEvents.UserCenter.Pay_line;
            payByIndonesiaSms();
        }
//        Analytics.sendUIEvent(mRechargeTag, null, null);
    }

    private void payByBilling(){
        int position = mCoinValueClusterLayout.getCheckedItem();
        if(mBillingEnv.isEnvEnable()){//
            doCreateOrderTask(billingConfig.get(position),TAG_BILLING);
        } else {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_fail, "env not ok", null);
            showRechargeFailed();
        }
    }
    private void payByGooglePay(){
        int position = mCoinValueClusterLayout.getCheckedItem();
        if(mBillingEnv.isEnvEnable()){//
            doCreateOrderTask(googlePayConfig.get(position),GOOGLEPAY);
        } else {
            showRechargeFailed();
        }
    }

    private void payByBluePaySms(){
        HBLog.d(TAG+" payByBluePaySms");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = smsConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {

                    mBluePayEnv.payBySms(order.order_id, bluePay.price * 100, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_sms_success, mExtraFrom, null);
                            } else if (blueMessage.getCode() == 601) {
                                showMoneyNotEnough();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_sms_fail, String.valueOf(blueMessage.getCode()), null);
                            } else {
                                showRechargeFailed();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_sms_fail, String.valueOf(blueMessage.getCode()), null);
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_SMS,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_sms_fail, "-1", null);
        }
    }

    private void payByBluePayBank(){
        HBLog.d(TAG+" payByBluePayBank");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = bankConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {

                    mBluePayEnv.payByBank(order.order_id, bluePay.price * 100, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " ");
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank_success, mExtraFrom, null);
                            } else if (blueMessage.getCode() == 601) {
                                showMoneyNotEnough();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank_fail, String.valueOf(blueMessage.getCode()), null);
                            } else {
                                showRechargeFailed();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank_fail, String.valueOf(blueMessage.getCode()), null);
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_BANK,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank_fail, "-1", null);
        }
    }

    private void payByBluePayHappyCard(){
        HBLog.d(TAG+" payByBluePayHappyCard ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = dtacConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_HAPPY, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_happy_success, mExtraFrom, null);
                            } else {
                                showRechargeFailed();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_happy_fail,String.valueOf(blueMessage.getCode()), null);
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_HAPPY,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_happy_fail,"-1", null);
        }
    }

    private void payByBluePay12CallCard(){
        HBLog.d(TAG+" payByBluePay12CallCard ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = aisConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_12CALL, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_success, mExtraFrom, null);
                            } else {
                                showRechargeFailed();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, String.valueOf(blueMessage.getCode()), null);
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_12CALL,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, "-1", null);
        }
    }

    private void payByBluePayTrueMoneyCard(){
        HBLog.d(TAG+" payByBluePayTrueMoneyCard ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = truemoneyConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_TRUEMONEY, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_true_success, mExtraFrom, null);
                            } else {
                                showRechargeFailed();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_true_fail, String.valueOf(blueMessage.getCode()), null);
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_TRUEMONEY,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_true_fail, "-1", null);
        }
    }

    private void payByBluePayLine(){
        HBLog.d(TAG+" payByBluePayLine ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = linepayConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByLINE(order.order_id,bluePay.price* 100, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
//                                handleBluePaySuccess();//no handle
                            } else {
                                showRechargeFailed();
                                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_line_fail, String.valueOf(blueMessage.getCode()), null);
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_LINE,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_line_fail, "-1", null);
        }
    }

    private void payByBluePayViettel(){
        HBLog.d(TAG+" payByBluePayViettel ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = viettelConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_VIETTEL, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_VIETTEL,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void payByBluePayVinaphone(){
        HBLog.d(TAG+" payByBluePayVinaphone ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = vinaphoneConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_VINAPHONE, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_VINAPHONE,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void payByBluePayMobifone(){
        HBLog.d(TAG+" payByBluePayMobifone ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = mobifoneConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_MOBIFONE, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_MOBIFONE,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void payByBluePayVtc(){
        HBLog.d(TAG+" payByBluePayVtc ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = vtcConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_VTC, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_VTC,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void payByBluePayHope(){
        HBLog.d(TAG+" payByBluePayHope ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = hopeConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_HOPE, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_HOPE,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void payByBluePayVietnamSms(){
        HBLog.d(TAG+" payByBluePayHope ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = vietnamSmsConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByVietnamSms(order.order_id, bluePay.price, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_VIETNAM_SMS,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void payByBluePayMogplay(){
        HBLog.d(TAG+" payByBluePayMogplay ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = mogplayConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_MOGPLAY, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_MOGPLAY,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void payByOfflineAtm(){
        HBLog.d(TAG+" payByOfflineAtm ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = atmConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByOfflineAtm(order.order_id, bluePay.price, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_OFFLINE_ATM,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }
    private void payByOfflineOtc(){
        HBLog.d(TAG+" payByOfflineOtc ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = otcConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByOfflineOtc(order.order_id, bluePay.price, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_OFFLINE_OTC,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void payByIndonesiaSms(){
        HBLog.d(TAG+" payByIndonesiaSms ");
        if(mBluePayEnv.isEnvEnable()){
            int position = mCoinValueClusterLayout.getCheckedItem();
            final PojoPayItems bluePay = indonesiaSmsConfig.get(position);
            doCreateOrderTask(new OnPendingExecutor() {
                @Override
                public void onExecutePay(final PojoOrder order) {
                    mBluePayEnv.payByIndonesiaSms(order.order_id, bluePay.price, new IPayCallback() {
                        @Override
                        public void onFinished(BlueMessage blueMessage) {
                            HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                            if (isBluepaySuccess(blueMessage)) {
                                handleBluePaySuccess();
                            } else {
                                showRechargeFailed();
                            }
                        }
                    });
                }
            },HttpProtocol.Payment.BLUEPAY_INDONESIA_SMS,bluePay.id,bluePay.price);
        } else {
            showRechargeFailed();
        }
    }

    private void startGooglePay(final PojoPayItems recharge,final String type){
        mBillingEnv.doPay(recharge.cp_product_id, mPojoOrder.order_id, new BillingEnv.OnPayCallBack() {
            @Override
            public void onSuccess(String signture, String purchaseInfo) {
                HBLog.d(TAG + " doPay onSuccess");
                queryReChargeResult(recharge, signture, purchaseInfo, "",type);
            }

            @Override
            public void onFailed(int code,String message) {
                HBLog.d(TAG + " doPay onFailed");
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_fail, message, (long) code);
                showRechargeFailed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HBLog.d(TAG+" onDestroy ");
        mBillingEnv.destroy();
        mBluePayEnv.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        HBLog.d(TAG + " onActivityResult resultCode:" + resultCode);
        if (!mBillingEnv.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            HBLog.d(TAG + " onActivityResult handled by IABUtil.");
        }
    }

    private void doCreateOrderTask(final OnPendingExecutor pendingExecutor,int bluepayType,String itemId,double price){
        HBLog.d(TAG + " doCreateOrderTask ");
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
//        dialog.setTitle();
        dialog.setMessage(getString(R.string.order_creating));
        Map<String,Object> params = new HashMap<>();
        params.put("pay_item_id", itemId==null?"":itemId);
        params.put("amount", "" + price);
        params.put("pay_type", String.valueOf(bluepayType));
        String url = HttpProtocol.URLS.INIT_ORDER+bluepayType;
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoOrder>() {
            @Override
            public void onSuccess(PojoOrder pojo) {
                HBLog.d(TAG + " test onSuccess " + pojo);
                mPojoOrder = pojo;
                dialog.dismiss();
                pendingExecutor.onExecutePay(pojo);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " test onFailed " + code + " " + message + " " + e);
                dialog.dismiss();
                showRechargeFailed();
            }

            @Override
            public Class<PojoOrder> getObjectClass() {
                return PojoOrder.class;
            }
        });
        dialog.show();
    }

    private void doCreateOrderTask(final PojoPayItems recharge,final String type){
        HBLog.d(TAG + " doCreateOrderTask "+recharge);
        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setTitle();
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.order_creating));
        Map<String,Object> params = new HashMap<>();
        params.put("pay_item_id", recharge.id);//"11110001"
        params.put("amount", "" + recharge.coin_num);
//        String url = "http://192.168.3.222:3201/v1/payment/initOrder/?pay=Googleplay&debug=1&uid=5704978&token=debug&pay_item_id=1&amount=2";
        String url = HttpProtocol.URLS.ORDER_CREATE+type;
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoOrder>() {
            @Override
            public void onSuccess(PojoOrder pojo) {
                HBLog.d(TAG + " doCreateOrderTask onSuccess " + pojo);
                mPojoOrder = pojo;
                dialog.dismiss();
                startGooglePay(recharge,type);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " doCreateOrderTask onFailed " + code + " " + message + " " + e);
                dialog.dismiss();
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_fail, message, (long) code);
                showRechargeFailed();
            }

            @Override
            public Class<PojoOrder> getObjectClass() {
                return PojoOrder.class;
            }
        });
        dialog.show();
    }


    private void queryReChargeResult(final PojoPayItems recharge,String signture,String purchaseInfo,String orderid,String type){
        HBLog.d(TAG + " queryReChargeResult " + signture + " " + purchaseInfo);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.query_order_result));
        dialog.setCancelable(false);
        dialog.show();
        Map<String,Object> param = new HashMap<>();
        param.put("order_id", mPojoOrder.order_id);
        param.put("signture", signture);
        param.put("purchaseInfo", purchaseInfo);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.ORDER_RESULT+type, param, new Network.JsonCallBack<PojoOrder>() {
            @Override
            public void onSuccess(PojoOrder test) {
                HBLog.d(TAG + " onSuccess " + test);
                dialog.dismiss();
                handleGooglePayRechargeSuccess();
                int coin = PrefUtils.getInt(PrefConstants.UserInfo.COIN, 0) + recharge.coin_num;
                PrefUtils.putInt(PrefConstants.UserInfo.COIN, coin);
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_success, mExtraFrom, null);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                dialog.dismiss();
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_fail, message, (long) code);
                showRechargeFailed();
            }

            @Override
            public Class<PojoOrder> getObjectClass() {
                return PojoOrder.class;
            }
        });
    }

    private void queryReChargeResult(String orderid) {
        HBLog.d(TAG + " queryReChargeResult " + " " + orderid);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.query_order_result));
        dialog.show();
        Map<String,Object> param = new HashMap<>();
        param.put("order_id", mPojoOrder.order_id);
//        param.put("signture",signture);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.ORDER_RESULT2, param, new Network.JsonCallBack<PojoOrder>() {
            @Override
            public void onSuccess(PojoOrder test) {
                HBLog.d(TAG + " onSuccess " + test);
                dialog.dismiss();
                handleGooglePayRechargeSuccess();
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                dialog.dismiss();
                showRechargeFailed();
            }

            @Override
            public Class<PojoOrder> getObjectClass() {
                return PojoOrder.class;
            }
        });
    }


    private void handleGooglePayRechargeSuccess() {
        PrefUtils.putBoolean(PrefConstants.FINISHFIRSTRECHARGE.HAVE_FINISHED_FIRST_RECHARGE,true);
        if(!AppUtil.isActivityDestroyed(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.recharge_result);
            builder.setMessage(R.string.recharge_success);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void handleBluePaySuccess(){
        PrefUtils.putBoolean(PrefConstants.FINISHFIRSTRECHARGE.HAVE_FINISHED_FIRST_RECHARGE,true);
        if(!AppUtil.isActivityDestroyed(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.recharge_result);
            builder.setMessage(R.string.bluepay_recharge_result_hint);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void showRechargeFailed(){
        if(!AppUtil.isActivityDestroyed(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.recharge_result);
            builder.setMessage(R.string.order_pay_fail);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void showMoneyNotEnough(){
        if(!AppUtil.isActivityDestroyed(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.recharge_result);
            builder.setMessage(R.string.buy_fail_money_not_enough);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void showBluepaySmsDialog(List<PojoPayItems> config,final String tag){
        int position = mCoinValueClusterLayout.getCheckedItem();
        final PojoPayItems bluePay = config.get(position);
        String rechargeText=getString(R.string.recharge_dialog_text,String.valueOf(bluePay.price),String.valueOf(bluePay.coin_num));
        if(!AppUtil.isActivityDestroyed(this)){
            final Dialog rechagrDialog=new Dialog(this,R.style.RechargeDialog);
            rechagrDialog.setContentView(R.layout.recharge_dialog);
            TextView textView=(TextView) rechagrDialog.findViewById(R.id.tv_recharge_text);
            textView.setText(rechargeText);
            rechagrDialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TAG_SMS.equals(tag)){
                        payByBluePaySms();
                    } else if(TAG_BANK.equals(tag)){
                        payByBluePayBank();
                    }else if(BLUEPAY_VIETNAM_SMS.equals(tag)){
                        payByBluePayVietnamSms();
                    }
                    rechagrDialog.dismiss();
                }
            });
            rechagrDialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rechagrDialog.dismiss();
                }
            });
            rechagrDialog.setCancelable(false);
            rechagrDialog.show();

        }
    }

    private void onPaymentChanged(String tag){
        mCheckedTag = tag;
//        String country=AppUtil.getMetaData(this,"country");
        if(country.equals("th")){
            onPayChangeTh(tag);
        }else if(country.equals("vn")){
            onPayChangeVn(tag);
        }else if(country.equals("id")){
            onPayChangeId(tag);
        }

    }

    private void onPayChangeTh(String tag){
        if(TAG_SMS.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(smsConfig,0);
        } else if(TAG_BILLING.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(billingConfig,0);
        } else if(TAG_AIS.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(aisConfig,0);
        } else if(TAG_BANK.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(bankConfig,0);
        } else if(TAG_DTAC.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(dtacConfig,0);
        } else if(TAG_TRUEMONEY.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(truemoneyConfig,0);
        } else if(TAG_LINEPAY.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(linepayConfig,0);
        }
    }

    private void onPayChangeVn(String tag){
        if(BLUEPAY_VIETTEL.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(viettelConfig,0);
        } else if(BLUEPAY_VINAPHONE.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(vinaphoneConfig,0);
        } else if(BLUEPAY_MOBIFONE.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(mobifoneConfig,0);
        } else if(BLUEPAY_VTC.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(vtcConfig,0);
        } else if(BLUEPAY_HOPE.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(hopeConfig,0);
        }else if(BLUEPAY_VIETNAM_SMS.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(vietnamSmsConfig,0);
        }else if(GOOGLEPAY.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(googlePayConfig,0);
        }
    }

    private void onPayChangeId(String tag){
        if(BLUEPAY_MOGPLAY.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(mogplayConfig,0);
        }else if(BLUEPAY_OFFLINE_ATM.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(atmConfig,0);
        }else if(BLUEPAY_OFFLINE_OTC.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(otcConfig,0);
        }else if(BLUEPAY_INDONESIA_SMS.equals(tag)){
            mCoinValueClusterLayout.setFaceValues(indonesiaSmsConfig,0);
        }
    }


    private void setCheckedImage(ImageView imageview,boolean checked){
        String tag = (String) imageview.getTag();
//        String country=AppUtil.getMetaData(this,"country");
        if(country.equals("th")){
            setCheckedImageTh(imageview,checked,tag);
        }else if(country.equals("vn")){
            setCheckedImageVn(imageview,checked,tag);
        }else if(country.equals("id")){
            setCheckedImageId(imageview,checked,tag);
        }
    }
    private void setCheckedImageTh(ImageView imageview,boolean checked,String tag){
        if(TAG_SMS.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_sms_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_sms_p);
            }
        } else if(TAG_BILLING.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_billing_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_billing_p);
            }
        } else if(TAG_AIS.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_ais_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_ais_p);
            }
        } else if(TAG_BANK.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_bank_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_bank_p);
            }
        } else if(TAG_DTAC.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_dtac_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_dtac_p);
            }
        } else if(TAG_TRUEMONEY.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_truemoney_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_truemoney_p);
            }
        } else if(TAG_LINEPAY.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_line_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_line_p);
            }
        }
    }

    private void setCheckedImageVn(ImageView imageview,boolean checked,String tag){
        if(BLUEPAY_VIETTEL.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_viettel_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_viettel_p);
            }
        }else if(BLUEPAY_VINAPHONE.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_vinaphone_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_vinaphone_p);
            }
        }else if(BLUEPAY_MOBIFONE.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_mobifone_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_mobifone_p);
            }
        }else if(BLUEPAY_VTC.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_vtc_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_vtc_p);
            }
        }else if(BLUEPAY_VIETNAM_SMS.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_vietnam_sms_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_vietnam_sms_p);
            }
        }else if(BLUEPAY_HOPE.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_hope_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_hope_p);
            }
        }else if(GOOGLEPAY.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_billing_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_billing_p);
            }
        }
    }

    private void setCheckedImageId(ImageView imageview,boolean checked,String tag){
        if(BLUEPAY_MOGPLAY.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_line_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_line_p);
            }
        }else if(BLUEPAY_OFFLINE_ATM.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_line_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_line_p);
            }
        }else if(BLUEPAY_OFFLINE_OTC.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_line_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_line_p);
            }
        }else if(BLUEPAY_INDONESIA_SMS.equals(tag)){
            if(checked){
                imageview.setImageResource(R.drawable.ic_payment_line_c);
            } else {
                imageview.setImageResource(R.drawable.ic_payment_line_p);
            }
        }
    }

    private boolean isBluepaySuccess(BlueMessage msg){
        return msg.getCode() == 200 || msg.getCode() == 201;
    }

    @Override
    public void onClick(View v) {

    }

    private interface OnPendingExecutor{
        void onExecutePay(PojoOrder order);
    }

    class WrapView {
        public View rootLayout;
        public ImageView checkBox;

        public WrapView(int rootResId,int checkResId){
            rootLayout = findViewById(rootResId);
            checkBox = (ImageView) findViewById(checkResId);
        }

        public void setChecked(boolean checked){
            if(checked){
                checkBox.setImageResource(R.drawable.ic_checkbox_check);
            } else {
                checkBox.setImageResource(R.drawable.ic_checkbox_uncheck);
            }
        }


    }
}


