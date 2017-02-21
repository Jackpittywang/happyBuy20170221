package dotc.android.happybuy.modules.home.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.result.PojoNewNotics;
import dotc.android.happybuy.http.result.PojoNotics;
import dotc.android.happybuy.modules.schema.SchemaActivity;
import dotc.android.happybuy.ui.activity.WebActivity;

/**
 * Created by huangli on 16/4/13.
 */
public class NoticeTextView extends RelativeLayout implements View.OnClickListener{
    private final int WHAT_AUTO = 0x00;
    private final int AUTO_TIME = 3000;
    private TextView text1,text2;
    private ImageView iVicon;
//    private Queue<PojoNotics.Notics> noticsQueue;
//    private PojoNotics.Notics currentNotics;
    private String labelCongratulate;
    private String labelObtain;
    private OnScrollListener listener;

    private long lastConsumeFinishTimestamp;

    private PojoNewNotics.Notics currentNotics;
    private Queue<PojoNewNotics.Notics> noticsQueue;
    private int times=AUTO_TIME;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(noticsQueue.size()>0){
//                PojoNotics.Notics notics = noticsQueue.remove();
                PojoNewNotics.Notics notics=noticsQueue.remove();
                currentNotics = notics;
//                String text = notics.nick+" "+labelObtain+" "+notics.name;
                String text = notics.title;
                setAnimText(text);
            } else {
                if(System.currentTimeMillis() - lastConsumeFinishTimestamp>60*1000){
                    if(listener!=null){
                        listener.onItemScrollerDone(currentNotics);
                    }
                    lastConsumeFinishTimestamp = System.currentTimeMillis();
                }
            }
            Message message = handler.obtainMessage(WHAT_AUTO);
            handler.sendMessageDelayed(message, times);
        }
    };
    public NoticeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_autotext, this);
        findViews();
        init();
    }

    private void init(){
        setOnClickListener(this);
        labelCongratulate = getResources().getString(R.string.label_congratulate);
        labelObtain = getResources().getString(R.string.label_obtain);
        noticsQueue = new ConcurrentLinkedQueue<>();
    }

    private void setAnimText(String text){
        if (text1.getVisibility() == VISIBLE){
            anim(text,text1,text2);
        } else {
            anim(text,text2,text1);
        }
    }

    private void anim(final String str,final TextView tv1,final TextView tv2){
        final ObjectAnimator objectAnimatorTlyUp = ObjectAnimator.ofFloat(tv1, "translationY", 0, -30).setDuration(500);
        final ObjectAnimator objectAnimatorAlpUp = ObjectAnimator.ofFloat(tv1, "alpha", 1f, 0f).setDuration(500);
        final AnimatorSet animatorSetUp = new AnimatorSet();
        animatorSetUp.playTogether(objectAnimatorTlyUp,objectAnimatorAlpUp);
        final ObjectAnimator objectAnimatorTlyDown = ObjectAnimator.ofFloat(tv2, "translationY", 30, 0).setDuration(500);
        final ObjectAnimator objectAnimatorAlpDown = ObjectAnimator.ofFloat(tv2, "alpha", 0f, 1f).setDuration(500);
        final AnimatorSet animatorSetDown = new AnimatorSet();
        animatorSetDown.playTogether(objectAnimatorTlyDown,objectAnimatorAlpDown);
        animatorSetUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tv1.setVisibility(GONE);
            }
        });
        animatorSetUp.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv2.setText(str);
                tv2.setVisibility(VISIBLE);
                animatorSetDown.start();
            }
        }, 200);
    }

    public void setLeftIcon(int resid){
        iVicon.setImageResource(resid);
    }

    public void startAutoPlay() {
        stopAutoPlay();
        Message message = handler.obtainMessage(WHAT_AUTO);
        handler.sendMessageDelayed(message, 0);
    }

    public void stopAutoPlay(){
        if(handler.hasMessages(WHAT_AUTO)){
            handler.removeMessages(WHAT_AUTO);
        }
    }

    public void pauseAutoPlay(){
        stopAutoPlay();
    }

    public void resumeAutoPlay(){
        if(this.noticsQueue.size()>0){
            startAutoPlay();
        }
    }


    public void setOnScrollListener(OnScrollListener listener){
        this.listener = listener;
    }

    public void appendNotices(PojoNewNotics pojoNewNotics) {
        times=Integer.parseInt(pojoNewNotics.roll_time)*1000;
        noticsQueue.addAll(pojoNewNotics.list);
        startAutoPlay();
    }

    private void findViews() {
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        iVicon = (ImageView)findViewById(R.id.iv_icon);
    }

    @Override
    public void onClick(View v) {
        if(currentNotics!=null){
            if(!TextUtils.isEmpty(currentNotics.url)){
                if(currentNotics.url.startsWith("http")){
                    Intent intent = new Intent(getContext(), WebActivity.class);
                    intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(currentNotics.url));
                    getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), SchemaActivity.class);
                    intent.putExtra(SchemaActivity.EXTRA_DATA_STRING,currentNotics.url);
                    getContext().startActivity(intent);
                }
            }
        }
    }

    public interface OnScrollListener{
//        void onItemScrollerDone(PojoNotics.Notics notics);
        void onItemScrollerDone(PojoNewNotics.Notics notics);
    }
}
