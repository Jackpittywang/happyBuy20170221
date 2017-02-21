package dotc.android.happybuy.push;

import java.io.Serializable;

/**
 * Created by wangjun on 16/9/8.
 */
public class Topic implements Serializable {
    public String topic;
    public long createTime;

    public Topic(String topic, long createTime) {
        this.topic = topic;
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topic='" + topic + '\'' +
                ", createTime=" + createTime +
                '}';
    }

}
