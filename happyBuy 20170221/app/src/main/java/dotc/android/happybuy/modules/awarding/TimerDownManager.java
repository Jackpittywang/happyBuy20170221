package dotc.android.happybuy.modules.awarding;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/8/30.
 */
public class TimerDownManager {

    private static String TAG = TimerDownManager.class.getSimpleName();
    private static TimerDownManager mInstance;

    public static TimerDownManager getInstance(Context context){
        if(mInstance == null){
            synchronized (TimerDownManager.class) {
                if (mInstance == null) {
                    mInstance = new TimerDownManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private Context mContext;
    private Map<String,TimerObj> mTimerMapping;

    private TimerDownManager(Context context){
        this.mContext = context;
        mTimerMapping = new HashMap<>();

    }

    public void addTimer(String productItemId,long diffTime,long awardTime){
        if(!mTimerMapping.containsKey(productItemId)){
            mTimerMapping.put(productItemId,new TimerObj(diffTime,awardTime));
        }
    }

    public TimerObj getTimer(String productItemId){
        HBLog.d(TAG+" getTimer productItemId:"+productItemId);
        print();
        return mTimerMapping.get(productItemId);
    }

    private void print(){
        if(true){
            HBLog.d(TAG+" ----------- "+mTimerMapping);
        }
    }

    public class TimerObj {
        public long createTime;
        public long diffTime;
        public long awardTime;

        public TimerObj(long diffTime, long awardTime) {
            this.diffTime = diffTime;
            this.awardTime = awardTime;
            createTime = System.currentTimeMillis();
        }

        public boolean isValid(){
            return awardTime*1000 - (System.currentTimeMillis()-diffTime) >0;
        }

        @Override
        public String toString() {
            return "TimerObj{" +
                    "diffTime=" + diffTime +
                    ", awardTime=" + awardTime +
                    '}';
        }
    }

}
