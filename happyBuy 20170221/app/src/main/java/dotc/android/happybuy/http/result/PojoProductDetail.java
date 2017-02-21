package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangjun on 16/3/29.
 */
public class PojoProductDetail implements Serializable {

    public PojoProduct product;
    public PojoPartRecorderList latestParticipateRecords;

    public String calculationDetailUrl;
    public String historyAwardUrl;
    public String imageTextUrl;
    public PojoParticipateInfo participateInfo;
    public PojoPrizeUser prizeUser;
    public long server_time;
    public PreviousProduct previousProduct;

}
