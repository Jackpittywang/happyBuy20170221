package dotc.android.happybuy.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import dotc.android.happybuy.GlobalContext;


/**
 * Toast类，只能在主线程调用
 *
 * @author dongbao.you
 */
public class ToastUtils {
    private static Toast toast;

    public static void showShortToast(Context context, CharSequence msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(GlobalContext.get(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showShortToast(Context context, int resId) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(GlobalContext.get(), resId, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLongToast(Context context, CharSequence msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(GlobalContext.get(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showLongToast(Context context, int resId) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(GlobalContext.get(), resId, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showLongCenterToast(Context context, int resId) {
        if (context == null) return;
        Toast toast = Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
