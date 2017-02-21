package dotc.android.happybuy.modules.recharge.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoPayItems;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/3/30.
 */
public class CoinValueClusterLayout extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private List<PojoPayItems> mFaceValueList;

    private OnItemClickListener mOnItemClickListener;
    private CheckedTextView[] mCheckTextViews;
//    private CheckedTextView mCheckedTextView;

    private int mCheckedPosition;

    public CoinValueClusterLayout(Context context) {
        super(context);
        init(context);
    }

    public CoinValueClusterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(VERTICAL);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setFaceValues(List<PojoPayItems> faceValues, int checkPosition) {
        mFaceValueList = faceValues;
        buildViews();
        if (checkPosition > 0) {
            setCheckItem(checkPosition);
        } else {
            setCheckItem(0);
        }
    }

    private void buildViews() {
        removeAllViews();
        LinearLayout linearLayout = null;
        LayoutParams layoutParams = newTimesTextViewLp();
        int rowNum = mFaceValueList.size()/3+(mFaceValueList.size()%3>0?1:0);
        mCheckTextViews = new CheckedTextView[rowNum*3];
        for (int i = 0; i < rowNum*3; i++) {
            if (i % 3 == 0) {
                linearLayout = newLinearLayout();
                addView(linearLayout, newInnerLayoutLp());
            }
            CheckedTextView textView = newTimesTextView();
            if(i<mFaceValueList.size()){
                Integer value = mFaceValueList.get(i).coin_num;
                textView.setBackgroundResource(R.drawable.bg_person_time_selector);
                textView.setText(getResources().getString(R.string.lable_coin,value));
                textView.setTag(i);
                textView.setOnClickListener(this);
            } else {
                textView.setBackgroundDrawable(null);
            }
            linearLayout.addView(textView, layoutParams);
            mCheckTextViews[i] = textView;
        }
//        requestLayout();
        requestLayout();
        invalidate();
    }

    public void setCheckItem(int position) {
        mCheckedPosition = position;
        for(int i=0;i<mCheckTextViews.length;i++){
            CheckedTextView textView = mCheckTextViews[i];
            if(i == position){
                textView.setChecked(true);
            } else {
                textView.setChecked(false);
            }
        }
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(mFaceValueList.get(position).price,position);
        }
    }

    private LinearLayout newLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(HORIZONTAL);
        return linearLayout;
    }

    private CheckedTextView newTimesTextView() {
        int padding = AppUtil.dp2px(mContext, 7);
        CheckedTextView textView = new CheckedTextView(mContext);
        textView.setTextSize(16);
        textView.setSingleLine();
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setGravity(Gravity.CENTER);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            applyTextAligment(textView);
        }
        textView.setPadding(padding, 0, padding, 0);
        return textView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void applyTextAligment(CheckedTextView textView){
        textView.setTextAlignment(TEXT_ALIGNMENT_GRAVITY);
    }

    private LayoutParams newTimesTextViewLp() {
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        layoutParams.leftMargin = AppUtil.dp2px(mContext, 10);
        layoutParams.rightMargin = AppUtil.dp2px(mContext, 10);
        return layoutParams;
    }

    private LayoutParams newInnerLayoutLp() {
        int height = AppUtil.dp2px(mContext, 40);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        layoutParams.topMargin = AppUtil.dp2px(mContext, 10);
        return layoutParams;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCheckItem(position);
    }

    public int getCheckedItem() {
        return mCheckedPosition;
    }

    public interface OnItemClickListener {
        void onItemClick(int coin,int position);
    }
}
