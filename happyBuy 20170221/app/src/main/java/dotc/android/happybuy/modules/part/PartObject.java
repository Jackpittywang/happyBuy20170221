package dotc.android.happybuy.modules.part;

import dotc.android.happybuy.http.result.PojoPaySuccess;
import dotc.android.happybuy.http.result.PojoProduct;

/**
 * Created by wangjun on 17/1/5.
 */

public class PartObject {

    public PojoProduct inputProduct;
    public PojoPaySuccess outputPayResult;

    public PartObject(PojoProduct inputProduct, PojoPaySuccess outputPayResult) {
        this.inputProduct = inputProduct;
        this.outputPayResult = outputPayResult;
    }
}
