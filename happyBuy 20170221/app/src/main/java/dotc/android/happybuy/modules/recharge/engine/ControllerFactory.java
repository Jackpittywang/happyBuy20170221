package dotc.android.happybuy.modules.recharge.engine;

import android.content.Context;

import dotc.android.happybuy.modules.recharge.controller.id.IdPaymentController;
import dotc.android.happybuy.modules.recharge.controller.th.ThPaymentController;
import dotc.android.happybuy.modules.recharge.controller.vn.VnPaymentController;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/12/17.
 */

public class ControllerFactory {

    public static PaymentController creator(Context context) {
        String country = AppUtil.getMetaData(context, "country");
        if (country.equals("th")) {
            return new ThPaymentController();
        } else if (country.equals("vn")) {
            return new VnPaymentController();
        } else if (country.equals("id")) {
            return new IdPaymentController();
        }
        return null;
    }
}
