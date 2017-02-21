package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangjun on 16/3/30.
 */
public class PojoParticipateInfo implements Serializable {

    public String participateDetailUrl;
    public List<String> participateCodes;
    public int participateTimes;
    public int status;

}
