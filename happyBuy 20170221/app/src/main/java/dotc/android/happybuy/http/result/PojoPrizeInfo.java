package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoPrizeInfo implements Serializable{
    public String address;
//    public String addressId;
//    public String addressName;

    public long confirmAddressTime;
    public long confirmGoodsTime;
    public long distributeTime;

    public int ship_status;
    public String mobile;
    public String orderId;
    public String participateTimes;
    public String periodId;
    public int prizeStatus;
    public long prizeTime;
    public String prizeTimeShow;
    public String prizeType;
    public String productId;
    public String productImageUrl;
    public String productName;
    public String shareId;
    public long shareTime;
    public String purchase_url;
    public String purchase_order_code;
    public long receiveTime;



    //add for virtual product
    public int productType;//0 实物 1 虚拟
    public double productPrice;//商品价格
    public int exchangeCoinCount;//商品价格
    public long confirmUseModeTime;//确认使用方式时间
    public int useMode = 0;//使用方式0 未定, 3 发放实物 , 1 换金币，2 冲话费 //pre   0 发放实物 1 换金币，2 冲话费
    public String confirmExchangePhone;//确认兑换的手机号
    public long confirmExchangePhoneTime;//确认兑换手机号时间
    public int exchangePhoneResultCode;//兑换手机话费结果状态码

    public long confirmExchangeCoinTime;//确认兑换金币时间
    public int exchangeCoinResultCode;//兑换金币结果状态码

    @Override
    public String toString() {
        return "PojoPrizeInfo{" +
                "address='" + address + '\'' +
                ", confirmAddressTime='" + confirmAddressTime + '\'' +
                ", confirmGoodsTime='" + confirmGoodsTime + '\'' +
                ", distributeTime='" + distributeTime + '\'' +
                ", ship_status='" + ship_status + '\'' +
                ", mobile='" + mobile + '\'' +
                ", orderId='" + orderId + '\'' +
                ", participateTimes='" + participateTimes + '\'' +
                ", periodId='" + periodId + '\'' +
                ", prizeStatus=" + prizeStatus +
                ", prizeTime='" + prizeTime + '\'' +
                ", prizeType='" + prizeType + '\'' +
                ", productId='" + productId + '\'' +
                ", productImageUrl='" + productImageUrl + '\'' +
                ", productName='" + productName + '\'' +
                ", shareId='" + shareId + '\'' +
                ", shareTime='" + shareTime + '\'' +
                ", receiveTime='" + receiveTime + '\'' +
                '}';
    }
}
