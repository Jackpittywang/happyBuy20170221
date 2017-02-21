package dotc.android.happybuy.uibase.listview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/3/9.
 */
public class ListFooterView extends LinearLayout implements View.OnClickListener{
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private LinearLayout mContentView;

    private View emptyLayout;
    private View loadingLayout;
    private View loadLayout;
    private TextView retryView;
    private View endLayout;
    private State mState = State.EMPTY;
    private OnRetryClickListener mListener;
    private int height;
    private boolean isShow;

    public ListFooterView(Context context) {
        super(context);
        init(context);
    }

    public ListFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        setOrientation(VERTICAL);
        height  = mContext.getResources().getDimensionPixelOffset(R.dimen.footerview_height);
//        LayoutInflater.from(context).inflate(R.layout.listfooter_loading, this);
//        emptyLayout = findViewById(R.id.layout_empty);
//        loadingLayout = findViewById(R.id.layout_loading);
//        endLayout = findViewById(R.id.layout_end);
//        retryView = (TextView) findViewById(R.id.textview_retry);
//        retryView.setOnClickListener(this);
        initView(context);
    }

    private void initView(Context context) {
        mContentView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.listfooter_loading, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        addView(mContentView,lp);
        emptyLayout = mContentView.findViewById(R.id.layout_empty);
        loadingLayout = mContentView.findViewById(R.id.layout_loading);
        loadLayout = mContentView.findViewById(R.id.textview_load);
        endLayout = mContentView.findViewById(R.id.layout_end);
        retryView = (TextView) mContentView.findViewById(R.id.textview_retry);
        retryView.setOnClickListener(this);
        setGravity(Gravity.TOP);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener){
        mListener = listener;
    }

    public void hide() {
        HBLog.d(TAG + " hide");
        if(isShow){
            LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
            lp.height = 0;
            mContentView.setLayoutParams(lp);
            isShow = false;
        }
    }

    /**
     * show footer
     */
    public void show() {
        HBLog.d(TAG + " show " + height);
        if(!isShow){
            LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
            lp.height = height;//LayoutParams.WRAP_CONTENT;
            mContentView.setLayoutParams(lp);
            isShow = true;
        }
    }

    public State getState(){
        return mState;
    }

    public void setState(State state){
        this.mState = state;
        HBLog.d(TAG+" setState "+mState);
        if(mState == State.LOAD){
            loadingLayout.setVisibility(View.GONE);
            loadLayout.setVisibility(View.VISIBLE);
            retryView.setVisibility(View.GONE);
            endLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
            show();
        } else if(mState == State.LOADING){
            loadingLayout.setVisibility(View.VISIBLE);
            loadLayout.setVisibility(View.GONE);
            retryView.setVisibility(View.GONE);
            endLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
            show();
        } else if(mState == State.RETRY){
            loadingLayout.setVisibility(View.GONE);
            loadLayout.setVisibility(View.GONE);
            retryView.setVisibility(View.VISIBLE);
            endLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
            show();
        } else if(mState == State.END){
            loadingLayout.setVisibility(View.GONE);
            loadLayout.setVisibility(View.GONE);
            retryView.setVisibility(View.GONE);
            endLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            show();
        } else {
            loadingLayout.setVisibility(View.GONE);
            loadLayout.setVisibility(View.GONE);
            retryView.setVisibility(View.GONE);
            endLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview_retry:
                if(mListener!=null){
                    mListener.onRetryClick(v);
                }
                break;
        }
    }

    public enum State{
        EMPTY,LOAD,LOADING,RETRY, END
    }

    public interface OnRetryClickListener{
        void onRetryClick(View view);
    }

}
