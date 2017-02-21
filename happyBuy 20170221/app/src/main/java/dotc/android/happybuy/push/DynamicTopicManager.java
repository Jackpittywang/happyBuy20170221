package dotc.android.happybuy.push;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.log.HBLog;
import mobi.andrutil.autolog.AutologManager;

/**
 * Created by wangjun on 16/8/23.
 */
public class DynamicTopicManager {
    private static String TAG = DynamicTopicManager.class.getSimpleName();
    private static DynamicTopicManager mInstance;

    public static DynamicTopicManager getInstance(Context context){
        if(mInstance == null){
            synchronized (DynamicTopicManager.class) {
                if (mInstance == null) {
                    mInstance = new DynamicTopicManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private Context mContext;
    private boolean mGpServiceAvailable;
    private Map<String,Topic> mTables;

    private DynamicTopicManager(Context context){
        this.mContext = context;
        mTables = new HashMap<>();
        restoreTableFromPersist();
        filterExpiredTopic();
    }

    public void setGpServiceAvailable(boolean available){
        mGpServiceAvailable = available;
    }

    public void trigger(PojoProduct product){
        String topicName = topicName(product.productItemId);
        PushLog.logD(TAG,"trigger topic:"+topicName);
        if(!mTables.containsKey(topicName)){
            if(subscribeTopic(topicName)){
                Topic topic = new Topic(topicName,System.currentTimeMillis());
                mTables.put(topicName,topic);
                storeTableToPersist();
            }
        }
    }

    public void markFinish(String productItemId){
        String topicName = topicName(productItemId);
        PushLog.logD(TAG,"markFinish topic:"+topicName);
        if(mTables.containsKey(topicName)){
            unsubscribeTopic(topicName);
            mTables.remove(topicName);
            storeTableToPersist();
        }
    }

    public boolean isDynamicTopic(String topicName){
        if(topicName.startsWith("/topics/udt_")){
            String topic = topicName.substring("/topics/udt_".length());
            return mTables.containsKey(topic);
        }
        return false;
    }

    private void restoreTableFromPersist(){
        String json = getSharedPreferences().getString("tables",null);
        if(!TextUtils.isEmpty(json)){
            Gson gson = new GsonBuilder().create();
            mTables = gson.fromJson(json, new TypeToken<Map<String, Topic>>() { }.getType());
        }
        PushLog.logD(TAG," restoreTableFromPersist "+mTables);
    }

    private void storeTableToPersist(){
        Gson gson = new GsonBuilder().create();
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("tables", gson.toJson(mTables));
        editor.commit();
    }

    private void filterExpiredTopic(){
        long expireTime = 7*24*60*60*1000;
        List<String> expireTopics = new ArrayList<>();
        for(String topicName:mTables.keySet()){
            Topic topic = mTables.get(topicName);
            if(System.currentTimeMillis() - topic.createTime >expireTime){
                expireTopics.add(topicName);
            }
        }

        for(String key:expireTopics){
            mTables.remove(key);
            unsubscribeTopic(key);
        }
        storeTableToPersist();
    }

    private String topicName(String productItemId){
        return "prev_award"+"_"+productItemId;///topics/udt_
    }

    private boolean subscribeTopic(String topic){
        PushLog.logD(TAG,"subscribeTopic topic:"+topic+" "+mGpServiceAvailable);
        try {
            if(mGpServiceAvailable){
//                FirebaseMessaging.getInstance().subscribeToTopic(topic);
                AutologManager.subscribeUserDefinedTopic(mContext,topic);
            }
        } catch (Exception e){
            PushLog.logW(TAG,"subscribeTopic error:"+e.getMessage());
            return false;
        }
//        String[] topics= AutologManager.getUserDefinedTopics(mContext);
//
//        for(int i=0;i<topics.length;i++){
//            PushLog.logD(TAG,"all topics i:"+i+" "+topics[i]);
//        }
        return true;
    }

    private boolean unsubscribeTopic(String topic){
        PushLog.logD(TAG,"unsubscribeTopic topic:"+topic+" "+mGpServiceAvailable);
        try {
            if(mGpServiceAvailable){
//                FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                AutologManager.unsubscribeUserDefinedTopic(mContext, topic);
            }
        } catch (Exception e){
            return false;
        }
        return true;
    }

    private static SharedPreferences getSharedPreferences(){
        return GlobalContext.get().getSharedPreferences("d_topic", Context.MODE_PRIVATE);
    }
}
