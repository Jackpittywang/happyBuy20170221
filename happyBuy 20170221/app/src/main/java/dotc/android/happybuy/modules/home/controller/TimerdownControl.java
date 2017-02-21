//package dotc.android.happybuy.modules.home.timerdown;
//
//import android.animation.Animator;
//import android.animation.ValueAnimator;
//import android.content.Intent;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import dotc.android.happybuy.GlobalContext;
//import dotc.android.happybuy.R;
//import dotc.android.happybuy.analytics.Analytics;
//import dotc.android.happybuy.analytics.AnalyticsEvents;
//import dotc.android.happybuy.http.HttpProtocol;
//import dotc.android.happybuy.http.Network;
//import dotc.android.happybuy.http.result.PojoAwardEvent;
//import dotc.android.happybuy.http.result.PojoAwardEvents;
//import dotc.android.happybuy.http.result.PojoEventStatus;
//import dotc.android.happybuy.log.HBLog;
//import dotc.android.happybuy.modules.awarding.TimerDownManager;
//import dotc.android.happybuy.modules.home.obj.AwardEventsObj;
//import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
//import dotc.android.happybuy.modules.home.widget.GoodsAwardStatusView;
//import dotc.android.happybuy.modules.home.widget.TimerdownScrollLayout;
//import dotc.android.happybuy.uibase.anim.WrapAnimatorListener;
//import dotc.android.happybuy.util.DateUtil;
//
///**
// * Created by wangjun on 16/8/23.
// */
//public class TimerdownControl {
//
//    private final String TAG = this.getClass().getSimpleName();
//
//    private View mParentView;
//    private TimerdownScrollLayout mTimerdownLayout;
////    private TextView mTimerDownTextView;
//    private GoodsAwardStatusView mGoodsAwardStatusView;
//    private ImageView mHighlightImageView;
//    private ValueAnimator mHightLightAnimator;
//
//    private boolean mPause;
//    private boolean mDestroy;
//    private final int INTERVAL = 10*1000;
//    private CountDownTimer mCountDownTimer;
//    private boolean mBoostUpdateData = true;
//
//    private List<PojoAwardEvent> mAwardEventList = new ArrayList<>();
//    private List<AwardEventsObj> mAwardEventsQueue = new ArrayList<>();
//    private Set<String> mEventIds = new HashSet<>();
//    private int mPosition = -1;
//
//    private int mMaxCount = 10;
//    private boolean mUserInteractive = false;
////    private boolean mAutoSnapNext = false;
//
//    private final int WHAT_FETCH_DATA = 0x00;
//    private final int WHAT_SNAP_NEXT = 0x01;
//    private final int WHAT_SNAP_TO_AWARDING = 0x02;
//    private final int WHAT_USER_INTERACTIVE = 0x03;
//
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            HBLog.d(TAG+" handleMessage "+msg.what);
//            if(msg.what == WHAT_FETCH_DATA){
//                loadEvent();
//                sendEmptyMessageDelayed(WHAT_FETCH_DATA,INTERVAL);
//            } else if(msg.what == WHAT_SNAP_NEXT){
//                if(!mUserInteractive){
//                    mTimerdownLayout.snapToNext();
//                } else {
//                    startSnapToNext();
//                }
//            } else if(msg.what == WHAT_SNAP_TO_AWARDING){
//                snapToAwardingPosition();
//            } else if (msg.what == WHAT_USER_INTERACTIVE){
//                mUserInteractive = false;
////                startSnapToNext();
//            }
//        }
//    };
//
//    private TimerdownScrollLayout.OnPageChangeListener mAwardingSlideListener = new TimerdownScrollLayout.OnPageChangeListener(){
//        @Override
//        public void onPageScrolled(int position, float positionOffset) {
//
////            mGoodsAwardStatusView.setAlpha((1-positionOffset)/2+0.5f);
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            HBLog.d(TAG+" onPageSelected position:"+position);
//            AwardEventsObj event = mTimerdownLayout.getEvent(position);
//            if(event!=null){
//                updateEventUI(position,event);
//            }
//        }
//    };
//
//    private TimerdownScrollLayout.OnTouchListener mTouchListeners = new TimerdownScrollLayout.OnTouchListener(){
//
//        @Override
//        public void onTouchRelease() {
//            HBLog.d(TAG+" onTouchRelease ---");
//            mHandler.sendEmptyMessageDelayed(WHAT_USER_INTERACTIVE,3*1000);
//        }
//
//        @Override
//        public void onTouchDown() {
//            Analytics.sendUIEvent(AnalyticsEvents.IndexCountDown.Click_Home_Count, null, null);
//            HBLog.d(TAG+" onTouchDown ---");
//            mUserInteractive = true;
//            if(mHandler.hasMessages(WHAT_USER_INTERACTIVE)){
//                mHandler.removeMessages(WHAT_USER_INTERACTIVE);
//            }
//        }
//    };
//
//    private TimerdownScrollLayout.ItemClickListener mItemClickListener = new TimerdownScrollLayout.ItemClickListener() {
//        @Override
//        public void onItemClick(View view, int position) {
//            AwardEventsObj event = mTimerdownLayout.getEvent(position);
//            if(event!=null){
//                Intent intent = new Intent(view.getContext(), GoodsDetailActivity.class);
//                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ITEM_ID,event.awardEvent.id);
//                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,event.awardEvent.product_id);
//                view.getContext().startActivity(intent);
//            }
//        }
//    };
//
//    public TimerdownControl(View parentLayout){
//        mTimerdownLayout = (TimerdownScrollLayout) parentLayout.findViewById(R.id.scrolllayout_timerdown);
//        mGoodsAwardStatusView = (GoodsAwardStatusView) parentLayout.findViewById(R.id.layout_goods_status);
//        mHighlightImageView = (ImageView) parentLayout.findViewById(R.id.imageview_highlight);
//
//        mParentView = parentLayout;
//        mTimerdownLayout.addOnPageChangeListener(mAwardingSlideListener);
//        mTimerdownLayout.addOnTouchListener(mTouchListeners);
//        mTimerdownLayout.addOnItemClickListener(mItemClickListener);
//        mTimerdownLayout.setLooper(true);
//        loadEvent();
//    }
//
//    public void pause(){
//        mPause = true;
//        if(mHandler.hasMessages(WHAT_FETCH_DATA)){
//            mHandler.removeMessages(WHAT_FETCH_DATA);
//        }
//        if(mHightLightAnimator!=null&&mHightLightAnimator.isStarted()){
//            mHightLightAnimator.cancel();
//        }
//        stopSnapToNext();
//    }
//
//    public void resume(){
//        mDestroy = false;
//        mPause = false;
//        mHandler.sendEmptyMessageDelayed(WHAT_FETCH_DATA,INTERVAL/5);
//
////        startSnapToNext();
//    }
//
//    public void destroy(){
//        mTimerdownLayout.removeOnPageChangeListener(mAwardingSlideListener);
//        mTimerdownLayout.removeOnTouchListener(mTouchListeners);
//        mTimerdownLayout.removeOnItemClickListener(mItemClickListener);
//        mDestroy = true;
//    }
//
//    public void refresh(){
//        if(mHandler.hasMessages(WHAT_FETCH_DATA)){
//            mHandler.removeMessages(WHAT_FETCH_DATA);
//            mHandler.sendEmptyMessage(WHAT_FETCH_DATA);
//        }
//    }
//
//    private void updateEventUI(final int position,final AwardEventsObj eventsObj){
//        mPosition = position;
//        HBLog.d(TAG+" updateEventUI ＝＝＝＝＝＝ "+position+" "+eventsObj);
//        if(mCountDownTimer!=null){
//            mCountDownTimer.cancel();
//        }
//        if(eventsObj.awardEvent.status == HttpProtocol.PRODUCT_STATE.AWARDING){
////            long time = event.award_time*1000;
//            long time = eventsObj.awardEvent.award_time*1000 - (System.currentTimeMillis()-eventsObj.diffTime);
//            HBLog.d(TAG+" updateEventUI ＝＝＝＝＝＝ time:"+time);
//            stopSnapToNext();
//            if(time>10){
//                mCountDownTimer = new CountDownTimer(time,10) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        long minute = (millisUntilFinished%(60*60*1000))/(60*1000);
//                        long second = (millisUntilFinished%(60*1000))/1000;
//                        long millSecond = (millisUntilFinished%100);
//                        mGoodsAwardStatusView.setTimerDown(minute,second,millSecond);
//                    }
//                    @Override
//                    public void onFinish() {
//                        HBLog.d(TAG+" onFinish ");
//                        mGoodsAwardStatusView.setQuering();
//                        queryAwardResult(position,eventsObj);
//                    }
//                };
//                mCountDownTimer.start();
//            } else {
//                mGoodsAwardStatusView.setQuering();
//                queryAwardResult(position,eventsObj);
//            }
//        } else {
//            mGoodsAwardStatusView.setAwardedResult(eventsObj.awardEvent.nick);
//            startSnapToNext();
//        }
//    }
//
//    private void queryAwardResult(final int position,final AwardEventsObj eventsObj){
//        String url = HttpProtocol.URLS.EVENT_STATUS;
//        Map<String,Object> params = new HashMap<>();
//        params.put("productItemId",eventsObj.awardEvent.id);
//        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoEventStatus>() {
//            @Override
//            public void onSuccess(PojoEventStatus eventStatus) {
////                HBLog.d(TAG + " queryAwardResult onSuccess " + eventStatus);
//                if(eventStatus.prizeUser!=null){
//                    if(position == mPosition){
//                        mGoodsAwardStatusView.setAwardedResultWithAnim(eventStatus.prizeUser.nickname);
//                    }
//                    setAndReplaceEvent(eventStatus);
//                }
//                doHighlightAnim();
//                startSnapToNext(); //delay to next
//            }
//
//            @Override
//            public void onFailed(int code, String message, Exception e) {
//                HBLog.d(TAG + " queryAwardResult onFailed " + code + " " + message + " " + e);
////                mTimerdownLayout.snapToNext();
//                startSnapToNext();
//            }
//
//            @Override
//            public Class<PojoEventStatus> getObjectClass() {
//                return PojoEventStatus.class;
//            }
//        });
//    }
//
//    private void setAndReplaceEvent(PojoEventStatus eventStatus){
//        int pos = -1;
//        for(int i=0;i<mAwardEventsQueue.size();i++){
//            AwardEventsObj obj = mAwardEventsQueue.get(i);
//            if(obj.awardEvent.id.equals(eventStatus.productItemId)){
//                pos = i;
//                break;
//            }
//        }
//        HBLog.d(TAG+" setAndReplaceEvent pos:"+pos);
//        if(pos>-1){
//            AwardEventsObj obj = mAwardEventsQueue.get(pos);
//            obj.awardEvent.status = eventStatus.status;
//            obj.awardEvent.award_time = eventStatus.awardTime;
//            obj.awardEvent.nick = eventStatus.prizeUser.nickname;
//            obj.awardEvent.award_uid = eventStatus.prizeUser.userId;
//            mAwardEventsQueue.set(pos,obj);
//        }
//    }
//
//    private void snapToAwardingPosition(){
//        int position = findAwardingPosition();
//        mTimerdownLayout.setSelection(position,true);
//    }
//
//    private void startSnapToNext(){
//        HBLog.d(TAG+" startSnapToNext ");
//        stopSnapToNext();
//        mHandler.sendEmptyMessageDelayed(WHAT_SNAP_NEXT,2*1000);
//    }
//
//    private void stopSnapToNext(){
//        if(mHandler.hasMessages(WHAT_SNAP_NEXT)){
//            mHandler.removeMessages(WHAT_SNAP_NEXT);
//        }
//    }
//
//    private void doHighlightAnim(){
//        HBLog.d(TAG+" doHighlightAnim ------------");
//        mHightLightAnimator = ValueAnimator.ofFloat(0,1f).setDuration(2*1000);
//        mHightLightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float animateValue = (float) animation.getAnimatedValue();
//                mHighlightImageView.setTranslationX(mHighlightImageView.getWidth()*2*animateValue);
////                mHighlightImageView.setTranslationY(-mHighlightImageView.getHeight()*2*animateValue);
//            }
//        });
//        mHightLightAnimator.addListener(new WrapAnimatorListener(){
//            @Override
//            public void onAnimationStart(Animator animation) {
//                mHighlightImageView.setVisibility(View.VISIBLE);
//                HBLog.d(TAG+" doHighlightAnim onAnimationStart");
//            }
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mHighlightImageView.setVisibility(View.INVISIBLE);
//                HBLog.d(TAG+" doHighlightAnim onAnimationEnd");
//            }
//        });
//        mHightLightAnimator.setTarget(mHighlightImageView);
//        mHightLightAnimator.start();
//    }
//
//    private void loadEvent(){
//        HBLog.d(TAG + " loadEvent ");
//        String url = HttpProtocol.URLS.MAIN_EVENT;
//        Map<String,Object> params = new HashMap<>();
//        params.put("size",String.valueOf(mMaxCount));
//        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoAwardEvents>() {
//            @Override
//            public void onSuccess(PojoAwardEvents events) {
////                HBLog.d(TAG + " loadEvent onSuccess " + events);
//                if(!mDestroy){
//                    update(events);
//                }
//                if(mBoostUpdateData){
//                    mBoostUpdateData = false;
//                }
//            }
//
//            @Override
//            public void onFailed(int code, String message, Exception e) {
//                HBLog.d(TAG + " loadEvent onFailed " + code + " " + message + " " + e);
////                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public Class<PojoAwardEvents> getObjectClass() {
//                return PojoAwardEvents.class;
//            }
//        });
//    }
//
//    private int findAwardingPosition(){
//        for(int i=0;i<mAwardEventsQueue.size();i++){
//            PojoAwardEvent event = mAwardEventsQueue.get(i).awardEvent;
//            if(event.status == HttpProtocol.PRODUCT_STATE.AWARDING){
//                return i;
//            }
//        }
//        return 0;
//    }
//
//    private void update(PojoAwardEvents events){
//        if(!mUserInteractive&&events.list.size()>0
//                &&isListChanged(events.list,mAwardEventList)){
//            long diffTime = System.currentTimeMillis() - events.server_time*1000;
//            mAwardEventList = events.list;
//            List<AwardEventsObj> eventList = filterEvents(events.list,diffTime);
//            mAwardEventsQueue.clear();
//            mAwardEventsQueue.addAll(eventList);
////                buildNewsLooperEvents();
//            rebuildEventIds();
//            int start = findAwardingPosition();
//            HBLog.d(TAG+"  ------------- start："+start);
//            if(start>mPosition){//
//                mTimerdownLayout.setItems(mAwardEventsQueue,start);
//            } else {
//                mTimerdownLayout.setItems(mAwardEventsQueue,mPosition);
//                if(!mBoostUpdateData){
//                    startSnapToNext();
//                }
//            }
//
//            for(AwardEventsObj eventsObj:mAwardEventsQueue){
//                if(eventsObj.awardEvent.status == HttpProtocol.PRODUCT_STATE.AWARDING){
//                    String productItemId = eventsObj.awardEvent.id;
//                    long awardTime = eventsObj.awardEvent.award_time;
//                    TimerDownManager.getInstance(GlobalContext.get()).addTimer(productItemId,diffTime,awardTime);
//                }
//            }
//
//            if(mAwardEventsQueue.size() == 0){
//                mParentView.setVisibility(View.GONE);
//            } else {
//                mParentView.setVisibility(View.VISIBLE);
//            }
//        } else {
////            if(!mBoostUpdateData){
////                startSnapToNext();
////            }
////            List<AwardEventsObj> eventList = filterAddedEvents(events.list,diffTime);
////            if(eventList.size()>0){//hava new data ,append it
////                mAwardEventsQueue.addAll(eventList);
////                rebuildEventIds();
////                mTimerdownLayout.appendItem(eventList);
////            }
//        }
//
//    }
//
//    private void buildNewsLooperEvents(){
//        if(mAwardEventsQueue.size()<=mMaxCount){
//            return;
//        }
//        List<AwardEventsObj> needDelList = new ArrayList<>();
//        for(int i=0;i<mAwardEventsQueue.size()-mMaxCount;i++){
//            needDelList.add(mAwardEventsQueue.get(i));
//        }
//
//        mAwardEventsQueue.removeAll(needDelList);
//    }
//
//    private List<AwardEventsObj> filterEvents(List<PojoAwardEvent> events,long diffTime){
//        List<AwardEventsObj> eventList = new ArrayList<>();
//        for(PojoAwardEvent event:events){
////            if(!mEventIds.contains(event.id)){ }
//            eventList.add(new AwardEventsObj(event,diffTime));
//        }
//        return eventList;
//    }
//
//    private boolean isListChanged(List<PojoAwardEvent> events,List<PojoAwardEvent> previousEvents){
//        if(previousEvents!=null&&events.size() == previousEvents.size()){
//            for(int i=0;i<events.size();i++){
//                PojoAwardEvent event = events.get(i);
//                PojoAwardEvent previousEvent = previousEvents.get(i);
//                if(!event.id.equals(previousEvent.id)||event.status != previousEvent.status){
//                    return true;
//                }
//            }
//            return false;
//        }
//        return true;
//    }
//
//    private List<AwardEventsObj> filterAddedEvents(List<PojoAwardEvent> events,long diffTime){
//        List<AwardEventsObj> eventList = new ArrayList<>();
//        for(PojoAwardEvent event:events){
//            if(!mEventIds.contains(event.id)){
//                eventList.add(new AwardEventsObj(event,diffTime));
//            }
//        }
//        return eventList;
//    }
//
//    private void rebuildEventIds(){
//        for(AwardEventsObj obj:mAwardEventsQueue){
//            mEventIds.add(obj.awardEvent.id);
//        }
//    }
//
//}
