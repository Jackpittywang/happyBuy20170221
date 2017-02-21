package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangjun on 16/3/28.
 */
public class PojoProduct implements Serializable {

    public String productId;
    public String productItemId;
    public int defaultTimes;
    public int minTimes;
    public int maxTimes;
    public int remainTimes;
    public int totalTimes;
    public String periodId;
    public String productName;
    public String productDesc;
    public String productUrl;//c++
    public String default_image;//php
    public List<String> productImages;
    public List<Integer> timesList;
    public int status;
    public String awardNumber;
    public long awardTime;
    public String awardTimeStr;
    public String latestPeriodId;
    public String latestProductItemId;
    public int coins_unit;

    @Override
    public String toString() {
        return "PojoProduct{" +
                "productId='" + productId + '\'' +
                ", productItemId='" + productItemId + '\'' +
                ", defaultTimes=" + defaultTimes +
                ", minTimes=" + minTimes +
                ", maxTimes=" + maxTimes +
                ", remainTimes=" + remainTimes +
                ", totalTimes=" + totalTimes +
                ", periodId='" + periodId + '\'' +
                ", productName='" + productName + '\'' +
                ", productDesc='" + productDesc + '\'' +
                ", productUrl='" + productUrl + '\'' +
                ", default_image='" + default_image + '\'' +
                ", productImages=" + productImages +
                ", timesList=" + timesList +
                ", status=" + status +
                ", awardNumber='" + awardNumber + '\'' +
                ", awardTime=" + awardTime +
                ", awardTimeStr='" + awardTimeStr + '\'' +
                ", latestPeriodId='" + latestPeriodId + '\'' +
                ", latestProductItemId='" + latestProductItemId + '\'' +
                ", coins_unit='" + coins_unit + '\'' +
                '}';
    }
}
