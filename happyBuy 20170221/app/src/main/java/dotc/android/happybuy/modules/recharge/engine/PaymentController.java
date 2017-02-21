package dotc.android.happybuy.modules.recharge.engine;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoOrder;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.http.result.PojoPayItems;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.recharge.TopupActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/12/17.
 */

public abstract class PaymentController {

    protected String TAG = this.getClass().getSimpleName();
    protected TopupActivity mActivity;
    protected String mExtraFrom;


    private AlertDialog payFailedDialog;
    private AlertDialog moneyNotEnoughDialog;
    private AlertDialog paySuccessDialog;

    private ProgressDialog progressDialog;

    public void onCreate(TopupActivity activity) {
        this.mActivity = activity;
        mExtraFrom = mActivity.mExtraFrom;
    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onDestroy() {
        if (payFailedDialog != null) {
            payFailedDialog.dismiss();
        }
        if (moneyNotEnoughDialog != null) {
            moneyNotEnoughDialog.dismiss();
        }
        if (paySuccessDialog != null) {
            paySuccessDialog.dismiss();
        }

        dismissProgressDialog();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    public void setPayments(List<PojoPay> payments, int checkPosition) {

    }

    public void onChannelCheckedChanged(PojoPay channel) {

    }

    public void onFaceValueCheckedChanged(PojoPayItems newItem) {

    }

    public void startPay(View view, PojoPay channel, PojoPayItems item) {

    }

    protected void doCreateOrder(final PojoPay channel, final PojoPayItems item, final OnOrderCreateCallback callback) {
        HBLog.d(TAG + " doCreateOrder " + item);
        Map<String, Object> params = new HashMap<>();
        params.put("pay_item_id", item.id);
        params.put("amount", String.valueOf(item.coin_num));
        String url = HttpProtocol.URLS.ORDER_CREATE + channel.id;
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoOrder>() {
            @Override
            public void onSuccess(PojoOrder order) {
                HBLog.d(TAG, "doCreateOrderTask onSuccess " + order);
                if (callback != null) {
                    callback.onCreateSuccess(order);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG, "doCreateOrderTask onFailed " + code + " " + message + " " + e);
                if (callback != null) {
                    callback.onCreateFailed(code, message);
                }
            }

            @Override
            public Class<PojoOrder> getObjectClass() {
                return PojoOrder.class;
            }
        });
    }

    protected void queryPayResult(final PojoPay channel, String signture, String purchaseInfo, PojoOrder order, final OnPayResultCallback callback) {
        HBLog.d(TAG + " queryReChargeResult " + signture + " " + purchaseInfo);
        Map<String, Object> param = new HashMap<>();
        param.put("order_id", order.order_id);
        param.put("signture", signture);
        param.put("purchaseInfo", purchaseInfo);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.ORDER_RESULT + channel.id, param, new Network.JsonCallBack<PojoOrder>() {
            @Override
            public void onSuccess(PojoOrder test) {
                HBLog.d(TAG + " onSuccess " + test);
                if (callback != null) {
                    callback.onPaySuccess();
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                if (callback != null) {
                    callback.onPayFailed(code, message);
                }
            }

            @Override
            public Class<PojoOrder> getObjectClass() {
                return PojoOrder.class;
            }
        });
    }

    protected void showPayFailedDialog() {
        if (!AppUtil.isActivityDestroyed(mActivity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.recharge_result);
            builder.setMessage(R.string.order_pay_fail);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            payFailedDialog = builder.create();
            payFailedDialog.setCancelable(false);
            payFailedDialog.show();
        }
    }

    protected void showMoneyNotEnoughDialog() {
        if (!AppUtil.isActivityDestroyed(mActivity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.recharge_result);
            builder.setMessage(R.string.buy_fail_money_not_enough);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            moneyNotEnoughDialog = builder.create();
            moneyNotEnoughDialog.setCancelable(false);
            moneyNotEnoughDialog.show();
        }
    }


    protected void showPaySuccessDialog() {
        PrefUtils.putBoolean(PrefConstants.FINISHFIRSTRECHARGE.HAVE_FINISHED_FIRST_RECHARGE, true);
        if (!AppUtil.isActivityDestroyed(mActivity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.recharge_result);
            builder.setMessage(R.string.bluepay_recharge_result_hint);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mActivity.finish();
                }
            });
            paySuccessDialog = builder.create();
            paySuccessDialog.setCancelable(false);
            paySuccessDialog.show();
        }
    }

    protected interface OnOrderCreateCallback {
        void onCreateSuccess(PojoOrder order);

        void onCreateFailed(int code, String message);
    }

    protected interface OnPayResultCallback {
        void onPaySuccess();

        void onPayFailed(int code, String message);
    }

    public void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mActivity);
        }
        progressDialog.dismiss();
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
