package dotc.android.happybuy.modules.recharge.vendor.billing;

import android.app.Activity;
import android.content.Intent;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;

import java.util.concurrent.atomic.AtomicInteger;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.recharge.engine.BaseEnv;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/4/6.
 */
public class BillingEnv extends BaseEnv {

    private final String TAG = this.getClass().getSimpleName();
    private final int RC_REQUEST = 10001;
    private Activity mActivity;
    private IabHelper mHelper;
    private boolean mEnvEnable;

    private String mProductId;
    private AtomicInteger mFlag = new AtomicInteger(0);
    private OnPayCallBack mOnPayCallBack;
    private OnSetupListener mOnSetupListener;
    private boolean mSetupDone;
    //    private final String SKU_GAS = "android.test.purchased";

    public BillingEnv(Activity activity){
        mActivity = activity;
        init();
    }

    private void init(){
        mHelper = new IabHelper(mActivity, GlobalContext.get().getString(R.string.billing_id));
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                HBLog.d(TAG + " initPay onIabSetupFinished " + result + " "+ result.isSuccess());
                mSetupDone = true;
                if(mOnSetupListener!=null){
                    mOnSetupListener.onSetupFinish(result.isSuccess());
                }
                if (!result.isSuccess()) {
                    return;
                }
                if (mHelper == null) return;
                mEnvEnable = true;
//                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

    }

    public void addSetupListener(OnSetupListener listener){
        mOnSetupListener = listener;
    }

    public boolean isSetupDone(){
        return mSetupDone;
    }

    public boolean isEnvEnable(){
        return mEnvEnable;
    }

    public void destroy(){
        try{
            if (mHelper != null) {
                mHelper.dispose();
                mHelper = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    public void doPay(String productId,String payload,OnPayCallBack callBack){
        HBLog.d(TAG + " doPay productId "+productId);
        mFlag.incrementAndGet();
        this.mProductId = productId;
        mOnPayCallBack = callBack;
        try{
            mHelper.launchPurchaseFlow(mActivity, productId, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            HBLog.d(TAG + " onIabPurchaseFinished: " + result + ", purchase: " + purchase+" "+Thread.currentThread().getName());
            if (mHelper == null) return;
            if (result.isFailure()) {
                if(result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED){
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
                ToastUtils.showLongToast(GlobalContext.get(), R.string.order_pay_fail);
                dispactchPayFailedListener(result.getResponse(),result.getMessage());
                return;
            }
            HBLog.d(TAG + " Purchase successful "+purchase.getSku());
            dispatchPaySuccessListener(purchase.getSignature(),purchase.getOriginalJson());
            if (purchase.getSku().equals(mProductId)) {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            HBLog.d(TAG + " onConsumeFinished: " + purchase + ", result: " + result+" "+Thread.currentThread().getName());
            if (mHelper == null) return;
            if (result.isSuccess()) {

            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            HBLog.d(TAG + " onQueryInventoryFinished "+result+" " + inventory +" "+Thread.currentThread().getName());
//            mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                HBLog.d(TAG + " Query inventory ,but result is failure");
                return;
            }
            Purchase gasPurchase = inventory.getPurchase(mProductId);
            HBLog.d(TAG + " onQueryInventoryFinished "+gasPurchase);
            if(gasPurchase!=null){
                mHelper.consumeAsync(gasPurchase,mConsumeFinishedListener);
            } else {
//                ToastUtils.showLongToast(GlobalContext.get(),result.getMessage());
            }
        }
    };

    private void dispatchPaySuccessListener(String signture,String purchaseInfo){
        if(mFlag.intValue()>0){
            mFlag.decrementAndGet();
            if(mOnPayCallBack!=null){
                mOnPayCallBack.onSuccess(signture,purchaseInfo);
            }
        }
    }

    private void dispactchPayFailedListener(int code,String message){
        if(mFlag.intValue()>0) {
            mFlag.decrementAndGet();
            if(mOnPayCallBack!=null){
                mOnPayCallBack.onFailed(code,message);
            }
        }
    }

    public interface OnPayCallBack{
        void onSuccess(String signture, String purchaseInfo);
        void onFailed(int code, String message);
    }

    public interface OnSetupListener {
        void onSetupFinish(boolean enable);
    }
}
