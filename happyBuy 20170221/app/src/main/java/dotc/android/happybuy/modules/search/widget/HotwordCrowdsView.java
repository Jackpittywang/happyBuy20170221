package dotc.android.happybuy.modules.search.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoHotword;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/5/12.
 */
public class HotwordCrowdsView extends LinearLayout implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private List<PojoHotword> mHotwordList;

    private int mViewsHorizontalMargin;
    private int mViewsVerticalMargin;
    //private int mViewMeasureDelta;
    private int mViewPadding;
    private int mViewHeight;

    private OnItemClickListener onItemClickListener;


    public HotwordCrowdsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HotwordCrowdsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        setOrientation(VERTICAL);
        mViewsHorizontalMargin = AppUtil.dp2px(context,10);
        mViewsVerticalMargin = AppUtil.dp2px(context,10);
        mViewPadding = AppUtil.dp2px(context,28);
        mViewHeight = AppUtil.dp2px(context,30);
    }

    public void setDataStrings(List<PojoHotword> hotwordList){
        mHotwordList = hotwordList;
        generateView();
    }

    private void generateView(){
        removeAllViews();
        LinearLayout linearLayout = buildRowCellLinearLayout();
        addView(linearLayout, buildRowLayoutLp(true));
        int rowWidth = 0;
        for(int i = 0;i < mHotwordList.size();i++){
            PojoHotword hotword = mHotwordList.get(i);
            TextView view = buildTextViewStyle();
            view.setText(hotword.name);
            view.setTag(hotword);
            view.setOnClickListener(this);

            int textViewWidth = (int) obtainTextViewWidth(view);
            if(textViewWidth+rowWidth<getWidth()){
                linearLayout.addView(view,buildRowCellLp());
                rowWidth += textViewWidth;
            } else {
                linearLayout = buildRowCellLinearLayout();
                addView(linearLayout,buildRowLayoutLp(false));
                linearLayout.addView(view,buildRowCellLp());
                rowWidth = textViewWidth;
            }
        }
    }

    private LinearLayout buildRowCellLinearLayout(){
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(HORIZONTAL);
        return linearLayout;
    }

    private TextView buildTextViewStyle(){
        TextView textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(R.color.colorText));
        textView.setTextSize(14);
        textView.setSingleLine();
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setBackgroundResource(R.drawable.bg_hotword);
        return textView;
    }

    private LayoutParams buildRowCellLp(){
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, mViewHeight);
        layoutParams.rightMargin = mViewsHorizontalMargin;
        return layoutParams;
    }

    private LayoutParams buildRowLayoutLp(boolean firstLine){
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if(!firstLine){
            layoutParams.topMargin = mViewsVerticalMargin;
        }
        return layoutParams;
    }

    private float obtainTextViewWidth(TextView textView){
        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        return paint.measureText(textView.getText().toString())+ mViewsHorizontalMargin+mViewPadding;
    }

    private void logDebug(String message){
        HBLog.d(TAG + " " + message);
    }

    @Override
    public void onClick(View view) {
        PojoHotword hotword = (PojoHotword) view.getTag();
        logDebug("onClick " + hotword);
        if(onItemClickListener!=null){
            onItemClickListener.onItemClick(view,hotword);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, PojoHotword hotword);
    }

}
