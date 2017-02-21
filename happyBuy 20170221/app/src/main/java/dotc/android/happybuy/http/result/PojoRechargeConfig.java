package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 *
 */
public class PojoRechargeConfig implements Serializable{
    public PojoBillingConfig billing;
    public PojoBluePayConfig bluepay_sms;
    public PojoBluePayConfig bluepay_truemoney;
    public PojoBluePayConfig bluepay_12call;
    public PojoBluePayConfig bluepay_happy;
    public PojoBluePayConfig bluepay_bank;
    public PojoBluePayConfig bluepay_linepay;
}
