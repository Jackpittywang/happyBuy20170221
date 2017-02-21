package dotc.android.happybuy.modules.recharge.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoPayItems;

/**
 * Created by wangjun on 16/12/16.
 */
public class FaceValueClusterLayout extends GridLayout implements View.OnClickListener {

    private Context mContext;
    private List<PojoPayItems> mFaceValues;
    private OnItemCheckedListener mListener;

    private int mCheckedPosition = -1;
    private LayoutInflater mInflater;


    public FaceValueClusterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        float width = 110 + 2*2;//topup_cell_margin
        float height = 70+2*2;
        setAspectRatio(width * 1.0f / height);
    }

    public void setOnItemCheckedListener(OnItemCheckedListener listener) {
        this.mListener = listener;
    }

    public void setFaceValues(List<PojoPayItems> faceValues){
        setFaceValues(faceValues,0);
    }

    public void setFaceValues(List<PojoPayItems> faceValues, int checkPosition) {
        mFaceValues = faceValues;
        removeAllViews();
        for (int position = 0; position < faceValues.size(); position++) {
            PojoPayItems faceValue = faceValues.get(position);
            View view = getChildView(position,faceValue);
            view.setOnClickListener(this);
            addView(view);
        }
        mCheckedPosition = -1;
        performCheck(checkPosition);
    }

    private View getChildView(int position, PojoPayItems faceValue) {
        if (faceValue.give_coin > 0) {
            return getDiscountChildView(position,faceValue);
        } else {
            return getDefaultChildView(position,faceValue);
        }
    }

    private View getDiscountChildView(int position, PojoPayItems faceValue){
        View view = mInflater.inflate(R.layout.listitem_facevalue_discount, this, false);
        view.setTag(position);
        TextView descrition = (TextView) view.findViewById(R.id.textview_desc);
        TextView textView1 = (TextView) view.findViewById(R.id.textview1);
        TextView textView2 = (TextView) view.findViewById(R.id.textview2);
        if(TextUtils.isEmpty(faceValue.desc)){
//            descrition.setVisibility(View.INVISIBLE);
        } else {
            descrition.setText(faceValue.desc);
//            descrition.setVisibility(View.VISIBLE);
        }
        textView1.setText(getResources().getString(R.string.lable_coin,faceValue.coin_num));
        textView2.setText("+"+getResources().getString(R.string.lable_coin,faceValue.give_coin));
        return view;
    }

    private View getDefaultChildView(int position, PojoPayItems faceValue){
        View view = mInflater.inflate(R.layout.listitem_facevalue, this, false);
        view.setTag(position);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(getResources().getString(R.string.lable_coin,faceValue.coin_num));
        return view;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        performCheck(position);
    }

    public void performCheck(int position){
        if (mCheckedPosition != position) {
            if(mListener!=null){
                mListener.onItemCheckedChanged(position,mCheckedPosition);
            }
            setChecked(position);
        }
    }


    public void setChecked(int position) {
        if (mCheckedPosition >= 0) {
            View maskView = findMaskView(mCheckedPosition);
            maskView.setVisibility(View.INVISIBLE);
        }

        View maskView = findMaskView(position);
        maskView.setVisibility(View.VISIBLE);
        mCheckedPosition = position;
    }

    private View findMaskView(int position){
        return getChildAt(position).findViewById(R.id.imageview_mask);
    }

    public interface OnItemCheckedListener{
        void onItemCheckedChanged(int newPos,int oldPos);
    }

}
