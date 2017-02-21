package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoEventStatus implements Serializable{

    public String awardNumber;
    public long awardTime;
    public PojoPrizeUser prizeUser;
    public String productItemId;
    public int status;

    @Override
    public String toString() {
        return "PojoEventStatus{" +
                "awardNumber='" + awardNumber + '\'' +
                ", awardTime=" + awardTime +
                ", prizeUser=" + prizeUser +
                ", productItemId='" + productItemId + '\'' +
                ", status=" + status +
                '}';
    }
}
