package dotc.android.happybuy.uibase.component;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dotc.android.happybuy.R;

/**
 * Created by wangjun on 16/3/9.
 */
public class HBToolbar extends RelativeLayout implements View.OnClickListener{

    private Context mContext;
    private FrameLayout homeAsUpLayout;
    private FrameLayout rightLayout;
    private ImageView homeAsUpImageView;
    private TextView titleTextView,rightTextView;
    private ImageView rightImageView;

    private CharSequence titleText;
    private CharSequence subtitleText;
    private OnItemClickListener mAsUpOnItemClickListener;
    private OnItemClickListener mRightItemOnItemClickListener;

    public HBToolbar(Context context) {
        super(context);
        init(context);
    }

    public HBToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HBToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        LayoutInflater.from(context).inflate(R.layout.layout_toolbar, this);
        homeAsUpLayout = (FrameLayout) findViewById(R.id.toolbar_as_up_frame);
        rightLayout = (FrameLayout) findViewById(R.id.toolbar_right_frame);
        homeAsUpImageView = (ImageView) findViewById(R.id.toolbar_as_up);
        titleTextView = (TextView) findViewById(R.id.toolbar_title);
        rightImageView = (ImageView) findViewById(R.id.toolbar_right_image);
        rightTextView = (TextView)findViewById(R.id.toolbar_right_tv);

        homeAsUpLayout.setOnClickListener(this);
        rightLayout.setOnClickListener(this);
    }

    public void setTitle(int resId) {
        setTitle(getContext().getText(resId));
    }

    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            titleTextView.setText(title);
            titleText = title;
        }
    }
    //default true
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp){
        if(showHomeAsUp){
            homeAsUpLayout.setVisibility(View.VISIBLE);
        } else {
            homeAsUpLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void setRightTextItem(int strResId,OnItemClickListener listener){
        rightTextView.setText(strResId);
        rightTextView.setVisibility(View.VISIBLE);
        rightLayout.setVisibility(View.VISIBLE);
        mRightItemOnItemClickListener = listener;
    }

    public void setRightTextItem(int strResId){
        rightTextView.setText(strResId);
        rightTextView.setVisibility(View.VISIBLE);
        rightLayout.setVisibility(View.VISIBLE);
    }

    public void setLeftItem(@DrawableRes int resId,OnItemClickListener listener) {
        homeAsUpImageView.setImageResource(resId);
        mAsUpOnItemClickListener = listener;
    }

    public void setLeftItem(OnItemClickListener listener) {
        mAsUpOnItemClickListener = listener;
    }

    public void setRightItem(@DrawableRes int resId,OnItemClickListener listener) {
        rightImageView.setImageResource(resId);
        rightImageView.setVisibility(View.VISIBLE);
        rightLayout.setVisibility(View.VISIBLE);
        mRightItemOnItemClickListener = listener;
    }

    public void setRightItem(View view,OnItemClickListener listener){
        rightLayout.removeAllViews();
        rightLayout.addView(view);
        rightLayout.setVisibility(View.VISIBLE);
        mRightItemOnItemClickListener = listener;
    }

    public void onAsUpItemClick(){
        if(mAsUpOnItemClickListener!=null){
            mAsUpOnItemClickListener.onItemClick(homeAsUpLayout);
        } else {
            Context context = getContext();
            if(context instanceof Activity){
                ((Activity) context).finish();
            }
        }
    }

    public void onRightItemClick(){
        if(mRightItemOnItemClickListener!=null){
            mRightItemOnItemClickListener.onItemClick(rightLayout);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_as_up_frame:
                onAsUpItemClick();
                break;
            case R.id.toolbar_right_frame:
                onRightItemClick();
                break;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view);
    }

}
