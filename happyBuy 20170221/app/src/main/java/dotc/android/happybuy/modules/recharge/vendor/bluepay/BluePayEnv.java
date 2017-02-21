package dotc.android.happybuy.modules.recharge.vendor.bluepay;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.bluepay.data.Config;
import com.bluepay.interfaceClass.BlueInitCallback;
import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.BluePay;
import com.bluepay.pay.Client;
import com.bluepay.pay.IPayCallback;
import com.bluepay.pay.LoginResult;
import com.bluepay.pay.PublisherCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.recharge.engine.BaseEnv;

/**
 * Created by wangjun on 16/5/3.
 */
public class BluePayEnv extends BaseEnv {

    public static boolean isBluepaySuccess(BlueMessage msg) {
        return msg.getCode() == 200 || msg.getCode() == 201;
    }

    private final String TAG = this.getClass().getSimpleName();
    private Activity mActivity;
    private BluePay mBluePay;
    private boolean mEnvEnable;
    private boolean mSetupDone;

    private List<OnSetupListener> mSetupListeners;
    public static String mLinePayOrderIdCache;

    public BluePayEnv(Activity activity) {
        this.mActivity = activity;
        mSetupListeners = new ArrayList<>();
        init();
    }

    private void init() {
        mBluePay = BluePay.getInstance();
        Client.init(mActivity, new BlueInitCallback() {
            @Override
            public void initComplete(String loginResult, String resultDesc) {
                HBLog.d(TAG + " initComplete " + loginResult + " " + resultDesc);
                mSetupDone = true;
                if (loginResult.equals(LoginResult.LOGIN_SUCCESS)) {
                    mEnvEnable = true;
                    BluePay.setLandscape(false);
                    BluePay.setShowCardLoading(true);//该方法设置使用cashcard时是否使用sdk的loading框
                } else if (loginResult.equals(LoginResult.LOGIN_FAIL)) {
                    mEnvEnable = false;
                } else {
                    mEnvEnable = false;
                }
                Looper looper = Looper.myLooper();
                if(looper !=null&&looper == Looper.getMainLooper()){
                    dispatchListener(mEnvEnable);
                } else {
                    postOnMainThread(mEnvEnable);
                }
            }
        });
    }

    private void postOnMainThread(final boolean enable){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                dispatchListener(enable);
            }
        });
    }

    public boolean isSetupDone() {
        return mSetupDone;
    }

    public void addSetupListener(OnSetupListener listener) {
        mSetupListeners.add(listener);
    }

    private void dispatchListener(boolean enable) {
        for (int i = 0, z = mSetupListeners.size(); i < z; i++) {
            OnSetupListener listener = mSetupListeners.get(i);
            if (listener != null) {
                listener.onSetupFinish(enable);
            }
        }
    }

    public boolean isEnvEnable() {
        return mEnvEnable;
    }

    public void destroy() {
        Client.exit();
        mSetupListeners.clear();
    }

    public void payByBank(String orderId, int price, final IPayCallback callBack) {
//        String billingID = ClientHelper.generateTid();
        HBLog.d(TAG + " payByBank orderId:" + orderId + " price:" + price);
        mBluePay.payByBank(mActivity, orderId, "THB", price + "", "PropsName", true, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage + " " + Thread.currentThread().getName());
                callBack.onFinished(blueMessage);
            }
        });

    }

    public void payBySms(String orderId, int price, final IPayCallback callBack) {
//        String billingID = ClientHelper.generateTid();
        HBLog.d(TAG + " payBySms orderId:" + orderId + " " + price);
        mBluePay.payBySMS(mActivity, orderId, "THB",
                price + "", 0, "propsName", true, new IPayCallback() {
                    @Override
                    public void onFinished(BlueMessage blueMessage) {
                        HBLog.d(TAG + " onFinished " + blueMessage + " " + Thread.currentThread().getName());
                        callBack.onFinished(blueMessage);
                    }
                });
    }

    public void payByCashcard(String orderId, String cardType, final IPayCallback callBack) {
//        String billingID = ClientHelper.generateTid();

        String userID = UUID.randomUUID().toString();
        if (userID.length() > 10)
            userID = userID.substring(0, 10);
        mBluePay.payByCashcard(mActivity, userID + "", orderId + "", "PropsName",
                cardType, "", null, new IPayCallback() {
                    @Override
                    public void onFinished(BlueMessage blueMessage) {
                        int code = blueMessage.getCode();
                        HBLog.d(TAG + " onFinished " + code + " " + blueMessage + " " + Thread.currentThread().getName());
                        callBack.onFinished(blueMessage);
                    }
                });
    }

    public void payByLINE(String orderId, int price, final IPayCallback callBack) {
        HBLog.d(TAG + " payByLINE orderId:" + orderId);
//        String billingID = ClientHelper.generateTid();
//        final String lineT_id = billingID;
        String userID = UUID.randomUUID().toString();
        mLinePayOrderIdCache = orderId;
        /***
         * 为了更好的体验scheme请确保独一无二。
         * "blue://pay" 对应LinePayActivity 的scheme，请参考manifest。
         */
        mBluePay.payByWallet(mActivity, userID, orderId,
                Config.K_CURRENCY_THB, price + "", "Red Diamond",
                PublisherCode.PUBLISHER_LINE, "gogobuypay://best.bluepay.asia", true, new IPayCallback() {
                    @Override
                    public void onFinished(BlueMessage blueMessage) {
                        HBLog.d(TAG + " onFinished " + blueMessage + " " + Thread.currentThread().getName());
                        callBack.onFinished(blueMessage);
                    }
                });
    }

    public void payByIndonesiaSms(String orderId, int price, final IPayCallback callBack) {
//        String billingID = ClientHelper.generateTid();
        HBLog.d(TAG + " payByIndonesiaSms orderId:" + orderId + " " + price);
        mBluePay.payBySMS(mActivity, orderId, Config.K_CURRENCY_THB,
                price + "", 0, "propsName", true, new IPayCallback() {
                    @Override
                    public void onFinished(BlueMessage blueMessage) {
                        HBLog.d(TAG + " onFinished " + blueMessage + " " + Thread.currentThread().getName());
                        callBack.onFinished(blueMessage);
                    }
                });
    }

    public void payByOfflineOtc(String orderId, int price, final IPayCallback callBack) {
//        String billingID = ClientHelper.generateTid();
       /* HBLog.d(TAG+" payByOfflineOtc orderId:"+orderId+" "+price);
        mBluePay.payByOffline(mActivity,orderId,"Customer",
                "IDR",price + "","propsName",PublisherCode.PUBLISHER_OFFLINE_OTC, true , new IPayCallback() {
                    @Override
                    public void onFinished(BlueMessage blueMessage) {
                        HBLog.d(TAG + " onFinished " + blueMessage + " " + Thread.currentThread().getName());
                        callBack.onFinished(blueMessage);
                    }
                });*/

    }

    public void payByOfflineAtm(String orderId, int price, final IPayCallback callBack) {
//        String billingID = ClientHelper.generateTid();
       /* HBLog.d(TAG+" payByOfflineAtm orderId:"+orderId+" "+price);
        mBluePay.payByOffline(mActivity,orderId,"Customer",
                "IDR",price + "","500THB=2500xsssu",PublisherCode.PUBLISHER_OFFLINE_ATM, true , new IPayCallback() {
                    @Override
                    public void onFinished(BlueMessage blueMessage) {
                        HBLog.d(TAG + " onFinished " + blueMessage + " " + Thread.currentThread().getName());
                        callBack.onFinished(blueMessage);
                    }
                });*/

    }

    public void payByVietnamSms(String orderId, int price, final IPayCallback callBack) {
//        String billingID = ClientHelper.generateTid();
        HBLog.d(TAG + " payByIndonesiaSms orderId:" + orderId + " " + price);
        mBluePay.payBySMS(mActivity, orderId, Config.K_CURRENCY_VND,
                price + "", 0, "propsName", true, new IPayCallback() {
                    @Override
                    public void onFinished(BlueMessage blueMessage) {
                        HBLog.d(TAG + " onFinished " + blueMessage + " " + Thread.currentThread().getName());
                        callBack.onFinished(blueMessage);
                    }
                });
    }

    public interface OnPayCallBack {
        void onSuccess(int code, BlueMessage blueMessage);

        void onFailed();
    }

    public interface OnSetupListener {
        void onSetupFinish(boolean enable);
    }

}
