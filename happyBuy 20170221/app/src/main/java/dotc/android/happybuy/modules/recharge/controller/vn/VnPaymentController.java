package dotc.android.happybuy.modules.recharge.controller.vn;

import android.content.Intent;
import android.view.View;

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

public class VnPaymentController extends PaymentController {

    private BillingEnv mBillingClient;
    private BluePayEnv mBluePayEnv;

    private ChannelClusterLayout mPaymentClusterLayout;

    @Override
    public void onCreate(TopupActivity activity) {
        super.onCreate(activity);
        mBillingClient = new BillingEnv(activity);
        mBluePayEnv = new BluePayEnv(activity);
        mPaymentClusterLayout = (ChannelClusterLayout) activity.findViewById(R.id.layout_payment_channel);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mBillingClient.destroy();
        mBluePayEnv.destroy();
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
    public void startPay(final View view, final PojoPay channel, final PojoPayItems item) {

        doCreateOrder(view, channel, item);
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

    private void startSdkPay(View view, PojoPay channel, PojoPayItems item, PojoOrder order) {
        HBLog.d(TAG, "startSdkPay " + channel.id);
        if (Vendors.GOOGLEPAY.equals(channel.id)) {
            doPayByGoogle(view, channel, item, order);
        } else if (Vendors.BLUEPAY_VIETTEL.equals(channel.id)) {
            doPayByBluePayViettel(view, channel, item, order);
        } else if (Vendors.BLUEPAY_VINAPHONE.equals(channel.id)) {
            doPayByBluePayVinaphone(view, channel, item, order);
        } else if (Vendors.BLUEPAY_MOBIFONE.equals(channel.id)) {
            doPayByBluePayMobifone(view, channel, item, order);
        } else if (Vendors.BLUEPAY_VTC.equals(channel.id)) {
            doPayByBluePayVtc(view, channel, item, order);
        } else if (Vendors.BLUEPAY_HOPE.equals(channel.id)) {
            doPayByBluePayHope(view, channel, item, order);
        } else if (Vendors.BLUEPAY_VIETNAM_SMS.equals(channel.id)) {
            doPayByBluePaySms(view, channel, item, order);
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

    private void doPayByBluePayViettel(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
//            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_VIETTEL, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePayVinaphone(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
//            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_VINAPHONE, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePayMobifone(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
//            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_MOBIFONE, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePayVtc(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
//            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_VTC, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePayHope(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
//            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByCashcard(order.order_id, PublisherCode.PUBLISHER_HOPE, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

    private void doPayByBluePaySms(View view, final PojoPay channel, PojoPayItems item, final PojoOrder order) {
        if (!mBluePayEnv.isEnvEnable()) {
            showPayFailedDialog();
//            Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, "env fail", -1l);
            return;
        }
        mBluePayEnv.payByVietnamSms(order.order_id, item.price, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage blueMessage) {
                HBLog.d(TAG + " onFinished " + blueMessage.getDesc() + " " + Thread.currentThread().getName());
                if (BluePayEnv.isBluepaySuccess(blueMessage)) {
                    showPaySuccessDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_success, mExtraFrom, null);
                } else {
                    showPayFailedDialog();
//                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_12call_fail, String.valueOf(blueMessage.getCode()), null);
                }
            }
        });
    }

}
