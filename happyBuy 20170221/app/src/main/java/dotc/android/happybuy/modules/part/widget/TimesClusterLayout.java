package dotc.android.happybuy.modules.part.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/3/30.
 */
public class TimesClusterLayout extends ViewGroup implements View.OnClickListener {

    private Context mContext;
    private final static int COLUMN = 4;

    private int mChildWidth;
    private int mHorizontalMargin;


    private List<Integer> mTimesList;
    private int mRemainTimes;
    private int mMinTimes;
    private int mMaxTimes;
    private OnItemClickListener mOnItemClickListener;

    public TimesClusterLayout(Context context) {
        super(context);
        init(context);
    }

    public TimesClusterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mHorizontalMargin = AppUtil.dp2px(context,14);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public int getChildWidth(){
        return mChildWidth;
    }

    public void setTimes(List<Integer> timesList,int remainTimes,int minTimes,int maxTimes){
        mTimesList = timesList;
        mRemainTimes = remainTimes;
        mMinTimes = minTimes;
        mMaxTimes = maxTimes;
        buildViews();
    }

    private void buildViews(){
        removeAllViews();
        for(int i=0;i<mTimesList.size();i++){
            TextView textView = newTimesTextView();
//            textView.setTextColor(getResources().getColor(R.color.part_times_color));
            if(mTimesList.get(i)>mRemainTimes||mTimesList.get(i)>mMaxTimes
                    ||mTimesList.get(i)<mMinTimes){
                textView.setEnabled(false);
                textView.setTextColor(0x66333333);
            } else {
                textView.setEnabled(true);
                textView.setTextColor(0xff333333);
            }
            textView.setText(String.valueOf(mTimesList.get(i)));
            textView.setTag(mTimesList.get(i));
            textView.setOnClickListener(this);
            addView(textView);
        }
    }

    private TextView newTimesTextView(){
        TextView textView = new TextView(mContext);
        textView.setTextSize(14);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.bg_person_time_selector);
        return textView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mChildWidth = (width - (COLUMN -1)*mHorizontalMargin) / COLUMN;
        int cellWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY);
        int cellHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(cellWidthMeasureSpec, cellHeightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(left, 0, left + mChildWidth, view.getMeasuredHeight());
            left = left + mChildWidth + mHorizontalMargin;
        }
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener!=null){
            int times = (int) v.getTag();
            mOnItemClickListener.onItemClick(v,times);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int times);
    }
}
