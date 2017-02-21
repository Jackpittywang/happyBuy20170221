package dotc.android.happybuy.modules.recharge.engine;

/**
 * Created by wangjun on 16/12/17.
 */

public class Vendors {

    public final static String TAG_BILLING = "1";
    public final static String TAG_SMS = "31";
    public final static String TAG_AIS = "33";
    public final static String TAG_BANK = "35";
    public final static String TAG_DTAC = "34";
    public final static String TAG_TRUEMONEY = "32";
    public final static String TAG_LINEPAY = "36";

    //越南
    public final static String BLUEPAY_VIETTEL = "41";
    public final static String BLUEPAY_VINAPHONE = "42";
    public final static String BLUEPAY_MOBIFONE = "43";
    public final static String BLUEPAY_VTC = "44";
    public final static String BLUEPAY_HOPE = "45";
    public final static String BLUEPAY_VIETNAM_SMS = "46";

    public final static String GOOGLEPAY = "55";

    //印尼
    public final static String BLUEPAY_MOGPLAY = "51";
    public final static String BLUEPAY_OFFLINE_ATM = "52";
    public final static String BLUEPAY_OFFLINE_OTC = "53";
    public final static String BLUEPAY_INDONESIA_SMS = "54";


    public final static String VENDOR_GOOGLE = "googlepay";
    public final static String VENDOR_BLUE = "bluepay";

    public static String getVendor(String channel) {
        if (TAG_BILLING.equals(channel) || GOOGLEPAY.equals(channel)) {
            return VENDOR_GOOGLE;
        } else if (TAG_SMS.equals(channel) || TAG_AIS.equals(channel) || TAG_BANK.equals(channel)
                || TAG_DTAC.equals(channel) || TAG_TRUEMONEY.equals(channel) || TAG_LINEPAY.equals(channel)) {
            return VENDOR_BLUE;
        } else if (BLUEPAY_VIETTEL.equals(channel) || BLUEPAY_VINAPHONE.equals(channel)
                || BLUEPAY_VTC.equals(channel) || BLUEPAY_HOPE.equals(channel) || BLUEPAY_VIETNAM_SMS.equals(channel)) {
            return VENDOR_BLUE;
        } else if (BLUEPAY_MOGPLAY.equals(channel) || BLUEPAY_OFFLINE_ATM.equals(channel)
                || BLUEPAY_OFFLINE_OTC.equals(channel) || BLUEPAY_INDONESIA_SMS.equals(channel)) {
            return VENDOR_BLUE;
        }
        return null;
    }


}
