package dotc.android.happybuy.modules.main.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/1/18.
 */
public class TabLayout extends LinearLayout implements View.OnClickListener {


    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;

    private RadioButton mHomeRadioButton;
    private RadioButton mClassificationRadioButton;
    private RadioButton mShowRadioButton;
    private RadioButton mMeRadioButton;
    private RadioButton[] mRadioButtons;

    private OnCheckedChangeListener mOnCheckedChangeListener;

//    private int mCurrentCheckRadioButtonId;
    private int mPreservedPosition = -1;
    private int mCurrentCheckPosition;
    private long mLastClickTimestamp;

    public TabLayout(Context context) {
        super(context);
        init(context);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.layout_main_tab, this);
        mHomeRadioButton = (RadioButton) findViewById(R.id.tab_home);
        mClassificationRadioButton = (RadioButton) findViewById(R.id.tab_classification);
        mShowRadioButton = (RadioButton) findViewById(R.id.tab_show);
        mMeRadioButton = (RadioButton) findViewById(R.id.tab_me);

        mRadioButtons = new RadioButton[]{mHomeRadioButton, mClassificationRadioButton,mShowRadioButton,mMeRadioButton};
        for(int i=0;i<mRadioButtons.length;i++){
            mRadioButtons[i].setTag(i);
            mRadioButtons[i].setOnClickListener(this);
        }
        setDefaultCheckTab();
    }

    private void setDefaultCheckTab() {
        if(mPreservedPosition>-1){
            mCurrentCheckPosition = mPreservedPosition;
            setCheck(mCurrentCheckPosition);
        } else {
            mCurrentCheckPosition = 0;
            mHomeRadioButton.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (mCurrentCheckPosition != position) {
            Checkable checkable = (Checkable) v;
            checkable.setChecked(true);
            HBLog.d(TAG + " onClick " + checkable+" position:"+position);
            doOtherRadioButtonUnCheck(checkable);
            mCurrentCheckPosition = position;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, position,null);
            }
        } else {
            if (System.currentTimeMillis() - mLastClickTimestamp < 2000) {
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onTabDoubleClick(this, position);
                }
            }
            mLastClickTimestamp = System.currentTimeMillis();
        }
    }

    private void doOtherRadioButtonUnCheck(Checkable checkRadioButton) {
        for (Checkable radioButton : mRadioButtons) {
            if (radioButton != checkRadioButton) {
                radioButton.setChecked(false);
            }
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public void triggerCheck(int position){
        triggerCheck(position,null);
    }

    public void triggerCheck(int position, Bundle args){
        setCheck(position);
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, position,args);
        }
    }

    /*
    *  update check widget ui
    * */
    public void setCheck(int position) {
        if(position<0){
            return;
        }
        if (mCurrentCheckPosition != position) {
            RadioButton checkRadioButton = mRadioButtons[position];
            checkRadioButton.setChecked(true);
            doOtherRadioButtonUnCheck(checkRadioButton);

            mCurrentCheckPosition = position;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        HBLog.d(TAG+" onSaveInstanceState "+mCurrentCheckPosition);
        SavedState ss = new SavedState(superState);
        ss.position = mCurrentCheckPosition;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mPreservedPosition = ss.position;
        HBLog.d(TAG+" onRestoreInstanceState mPreservedPosition:"+mPreservedPosition);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(TabLayout group, int position,Bundle args);
        void onTabDoubleClick(TabLayout group, int position);
    }

    static class SavedState extends BaseSavedState {
        int position;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            position = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
