package dotc.android.happybuy.modules.recharge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import dotc.android.happybuy.R;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/12/17.
 */

public class DialogHelper {

    public static void showPayFailedDialog(Activity activity) {
        if (!AppUtil.isActivityDestroyed(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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

    public static void showMoneyNotEnoughDialog(Activity activity) {
        if (!AppUtil.isActivityDestroyed(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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

    public static void showPaySuccessDialog(final Activity activity) {
        if (!AppUtil.isActivityDestroyed(activity)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.recharge_result);
            builder.setMessage(R.string.recharge_success);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    activity.finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public static Dialog createLoadingDialog(Activity activity){
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.query_order_result));
        dialog.setCancelable(false);
        return dialog;
    }

}
