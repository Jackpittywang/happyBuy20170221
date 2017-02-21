package dotc.android.happybuy.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.bluepay.pay.BlueMessage;
import com.bluepay.pay.BluePay;
import com.bluepay.pay.IPayCallback;
import com.bluepay.pay.PublisherCode;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.recharge.vendor.bluepay.BluePayEnv;
import dotc.android.happybuy.uibase.app.BaseActivity;

/**
 * Created by wangjun on 16/5/3.
 */
public class LINEPayResultActivity extends BaseActivity {

    private String mOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderId = BluePayEnv.mLinePayOrderIdCache;
        HBLog.d(TAG+" onCreate mOrderId:"+mOrderId);
        if(TextUtils.isEmpty(mOrderId)){
            finish();
            return;
        }
        setContentView(R.layout.activity_linepay);
        queryTransResult(mOrderId);
    }

    private void queryTransResult(String t_id){
        BluePay.getInstance().queryTrans(this, new IPayCallback() {
            @Override
            public void onFinished(BlueMessage msg) {
                HBLog.d(TAG+" queryTransResult onFinished "+msg);
                if (msg.getCode() == 200|| msg.getCode() == 201) {
                    showBluePaySuccess();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_line_success, null, null);
                } else {
                    showRechargeFailed();
                    Analytics.sendUIEvent(AnalyticsEvents.Recharge.Pay_line_fail, String.valueOf(msg.getCode()), null);
                }

            }
        }, t_id, PublisherCode.PUBLISHER_LINE, 1);
    }

    private void showBluePaySuccess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.recharge_result);
        builder.setMessage(R.string.bluepay_recharge_result_hint);
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

    private void showRechargeFailed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.recharge_result);
        builder.setMessage(R.string.order_pay_fail);
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
