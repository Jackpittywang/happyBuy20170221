package dotc.android.happybuy.modules.home.obj;

import java.io.Serializable;

import dotc.android.happybuy.http.result.PojoAwardEvent;

/**
 * Created by wangjun on 16/8/24.
 */
public class AwardEventsObj implements Serializable {
    public PojoAwardEvent awardEvent;
    public long diffTime;

    public AwardEventsObj(PojoAwardEvent awardItems, long diffTime) {
        this.awardEvent = awardItems;
        this.diffTime = diffTime;
    }

    @Override
    public String toString() {
        return "AwardEventsObj{" +
                "awardEvent=" + awardEvent +
                ", diffTime=" + diffTime +
                '}';
    }
}
