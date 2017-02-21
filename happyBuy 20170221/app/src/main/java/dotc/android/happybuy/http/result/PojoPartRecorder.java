package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by wangjun on 16/3/29.
 */
public class PojoPartRecorder implements Serializable {

    public String userId;
    public String photoUrl;
    public String nickname;
    public String area;
    public long createTime;
    public String createTimeShow;
    public int totalTimes;
//    public String orderId;
    public String participateDetailUrl;
    public String userIp;

    @Override
    public String toString() {
        return "PojoPartRecorder{" +
                "userId='" + userId + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", area='" + area + '\'' +
                ", createTime=" + createTime +
                ", totalTimes=" + totalTimes +
                ", participateDetailUrl='" + participateDetailUrl + '\'' +
                ", userIp='" + userIp + '\'' +
                '}';
    }
}
