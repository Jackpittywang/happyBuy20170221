package dotc.android.happybuy.modules.recharge.controller.th;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.IPayCallback;
import com.bluepay.pay.PublisherCode;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.result.PojoOrder;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.http.result.PojoPayItems;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.recharge.TopupActivity;
import dotc.android.happybuy.modules.recharge.engine.ChannelCell;
import dotc.android.happybuy.modules.recharge.engine.PaymentController;
import dotc.android.happybuy.modules.recharge.engine.Vendors;
import dotc.android.happybuy.modules.recharge.vendor.billing.BillingChannelCell;
import dotc.android.happybuy.modules.recharge.vendor.billing.BillingEnv;
import dotc.android.happybuy.modules.recharge.vendor.bluepay.BluePayChannelCell;
import dotc.android.happybuy.modules.recharge.vendor.bluepay.BluePayEnv;
import dotc.android.happybuy.modules.recharge.widget.ChannelClusterLayout;

/**
 * Created by wangjun on 16/12/17.
 */

public class ThPaymentController extends PaymentController {

    private static final int REQUEST_CODE_SMS = 100;

    private PojoOrder mPojoOrder;
    private BillingEnv mBillingClient;
    private BluePayEnv mBluePayEnv;

    private boolean mCardTypeHint;

    private ChannelClusterLayout mPaymentClusterLayout;

    private AlertDialog cardTypeHintDialog;
    private Dialog rechagrDialog;

    @Override
    public void onCreate(TopupActivity activity) {
        super.onCreate(activity);
        mBillingClient = new BillingEnv(activity);
        mBluePayEnv = new BluePayEnv(activity);
        mPaymentClusterLayout = (ChannelClusterLayout) activity.findViewById(R.id.layout_payment_channel);
        requestPermissionIfNeeded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBillingClient.destroy();
        mBluePayEnv.destroy();
        if (cardTypeHintDialog != null) {
            cardTypeHintDialog.dismiss();
        }
        if (rechagrDialog != null) {
            rechagrDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBillingClient.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setPayments(List<PojoPay> payments, int checkPosition) {
        mPaymentClusterLayout.removeAllViews();
        for (int position = 0; position < payments.size(); position++) {
            PojoPay pay = payments.get(position);
            mPaymentClusterLayout.addChannelCell(newChildCell(position, pay), position);
        }
        mPaymentClusterLayout.performCheck(checkPosition < 0 ? 0 : checkPosition);
    }

    private ChannelCell newChildCell(int position, PojoPay pay) {
        String vendor = Vendors.getVendor(pay.id);
        if (Vendors.VENDOR_GOOGLE.equals(vendor)) {
            return new BillingChannelCell(mActivity, mBillingClient, pay);
        } else if (Vendors.VENDOR_BLUE.equals(vendor)) {
            return new BluePayChannelCell(mActivity, mBluePayEnv, pay);
        }
        return null;
    }

    @Override
    public void onChannelCheckedChanged(PojoPay channel) {
        if (isCardPayChannel(channel.id)) {

        }
    }

    @Override
    public void onFaceValueCheckedChanged(PojoPayItems newItem) {

    }

    @Override
    public void startPay(final View view, final PojoPay channel, final PojoPayItems item) {
        if (Vendors.TAG_SMS.equals(channel.id)) {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_sms, null, null);
        } else if (Vendors.TAG_BILLING.equals(channel.id)) {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google, null, null);
        } else if (Vendors.TAG_AIS.equals(channel.id)) {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call, null, null);
        } else if (Vendors.TAG_BANK.equals(channel.id)) {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank, null, null);
        } else if (Vendors.TAG_DTAC.equals(channel.id)) {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_happy, null, null);
        } else if (Vendors.TAG_TRUEMONEY.equals(channel.id)) {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_true, null, null);
        } else if (Vendors.TAG_LINEPAY.equals(channel.id)) {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_line, null, null);
        } else {
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_unknown, null, null);
        }

        if (!mCardTypeHint && isCardPayChannel(channel.id)) {
            mCardTypeHint = true;
            showCardTypeHint(view, channel, item);
        } else {
            doCreateOrder(view, channel, item);
        }
    }

    private void showCardTypeHint(final View view, final PojoPay channel, final PojoPayItems item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.recharge_tips);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.recharge_iknow, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doCreateOrder(view, channel, item);
            }
        });
        cardTypeHintDialog = builder.create();
        cardTypeHintDialog.show();
    }

    private void doCreateOrder(final View view, final PojoPay channel, final PojoPayItems item) {
        showProgressDialog(mActivity.getString(R.string.order_creating));
        doCreateOrder(channel, item, new OnOrderCreateCallback() {
            @Override
            public void onCreateSuccess(PojoOrder order) {
                dismissProgressDialog();
                startSdkPay(view, channel, item, order);
            }

            @Override
            public void onCreateFailed(int code, String message) {
                dismissProgressDialog();
                showPayFailedDialog();
            }
        });
    }

    private boolean isCardPayChannel(String channel) {
        return Vendors.TAG_DTAC.equals(channel) || Vendors.TAG_AIS.equals(channel) || Vendors.TAG_TRUEMONEY.equals(channel);
    }

    private void requestPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.SEND_SMS);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, REQUEST_CODE_SMS);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        HBLog.d(TAG + " onRequestPermissionsResult requestCode:" + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    private void startSdkPay(View view, PojoPay channel, PojoPayItems item, PojoOrder order) {
        HBLog.d(TAG, "startSdkPay " + channel.id);
        if (Vendors.TAG_BILLING.equals(channel.id)) {
            doPayByGoogle(view, channel, item, order);
        } else if (Vendors.TAG_AIS.equals(channel.id)) {
            doPayByBluePay12Call(view, channel, item, order);
        } else if (Vendors.TAG_BANK.equals(channel.id)) {
            showTipBeforePayByBank(view, channel, item, order);
        } else if (Vendors.TAG_DTAC.equals(channel.id)) {
            doPayByBluePayHappy(view, channel, item, order);
        } else if (Vendors.TAG_TRUEMONEY.equals(channel.id)) {
            doPayByBluePayTrueMoney(view, channel, item, order);
        } else if (Vendors.TAG_LINEPAY.equals(channel.id)) {
            doPayByBluePayLine(view, channel, item, order);
        } else if (Vendors.TAG_SMS.equals(channel.id)) {
            //remove
        }
    }

    private void doPayByGoogle(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        HBLog.d(TAG, "doPayByGoogle ");
        if (!mBillingClient.isEnvEnable()) {
            showPayFailedDialog();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_fail, "env fail", -1l);
            return;
        }
        mBillingClient.doPay(item.cp_product_id, order.order_id, new BillingEnv.OnPayCallBack() {
            @Override
            public void onSuccess(String signture, String purchaseInfo) {
                showProgressDialog(mActivity.getString(R.string.query_order_result));
                queryPayResult(channel, signture, purchaseInfo, order, new OnPayResultCallback() {
                    @Override
                    public void onPaySuccess() {
                        dismissProgressDialog();
                        showPaySuccessDialog();
                        Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_success, mExtraFrom, null);
                    }

                    @Override
                    public void onPayFailed(int code, String message) {
                        dismissProgressDialog();
                        Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_fail, message, (long) code);
                        showPayFailedDialog();
                    }
                });
            }

            @Override
            public void onFailed(int code, String message) {
                Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_google_fail, message, (long) code);
                showPayFailedDialog();
            }
        });

    }

    private void showTipBeforePayByBank(View view, final PojoPay channel, final PojoPayItems item, final PojoOrder order) {
        rechagrDialog = new Dialog(mActivity, R.style.RechargeDialog);
        rechagrDialog.setContentView(R.layout.recharge_dialog);
        TextView textView = (TextView) rechagrDialog.findViewById(R.id.tv_recharge_text);
        String rechargeText = mActivity.getString(R.string.recharge_dialog_text, String.valueOf(item.price), String.valueOf(item.coin_num));
        textView.setText(rechargeText);
        rechagrDialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechagrDialog.dismiss();
                doPayByBluepayBank(v, channel, item, order);
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

    private void doPayByBluepayBank(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByBank(order.order_id, item.price * 100, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " ");
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank_success, mExtraFrom, null);
                } else if (blueMessage.getCode() == 601) {
                    showMoneyNotEnoughDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank_fail, String.valueOf(blueMessage.getCode()), null);
                } else {
                    showPayFailedDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_bank_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePayHappy(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_happy_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_HAPPY, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_happy_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_happy_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePay12Call(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_12CALL, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePayTrueMoney(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_true_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_TRUEMONEY, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_true_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_true_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePayLine(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_line_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByLINE(order.order_id, item.price * 100, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    //ignore
                } else {
                    showPayFailedDialog();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_line_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }


}
