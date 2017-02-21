package dotc.android.happybuy.modules.me.widget;

import android.graphics.Color;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.ui.help.ISizeSensitive;

/**
 * Created by Avazu on 2016/3/29.
 */
public class MyJoinDropDownView extends FrameLayout implements ISizeSensitive {

    LinearLayout layoutAllIn ;
    LinearLayout layoutOnGoing ;
    LinearLayout layoutAlreadyAnnounced ;
    TextView mImgAllIn;
    TextView mImgOnGoing;
    TextView mImgAlreadyAnnounced;

    OnSettingDropDownClickListener mOnSettingDropDownClickListener ;
    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if( view == layoutAllIn  ) {
                notifyOnDorpDownClick(OnSettingDropDownClickListener.ACTION_ALL_IN);
            } else if( view == layoutOnGoing ) {
                notifyOnDorpDownClick(OnSettingDropDownClickListener.ACTION_ON_GOING);
            } else if( view == layoutAlreadyAnnounced ) {
                notifyOnDorpDownClick(OnSettingDropDownClickListener.ACTION_ALREADY_ANNOUNCED);
            }
        }
    };

    public interface OnSettingDropDownClickListener {
        int ACTION_ALL_IN = 1 ;
        int ACTION_ON_GOING = 2 ;
        int ACTION_ALREADY_ANNOUNCED = 3 ;
        void onSettingDropDown(int action);
    }

    public void setOnSettingDropDownClickListener(OnSettingDropDownClickListener listener, int joinType) {
        this.mOnSettingDropDownClickListener = listener ;
        if (joinType == 0)
        {
            mImgAllIn.setTextColor(getResources().getColor(R.color.standard_red_normal));
            mImgOnGoing.setTextColor(getResources().getColor(R.color.black_light));
            mImgAlreadyAnnounced.setTextColor(getResources().getColor(R.color.black_light));
        }
        else if (joinType == 1) {
            mImgAllIn.setTextColor(getResources().getColor(R.color.black_light));
            mImgOnGoing.setTextColor(getResources().getColor(R.color.standard_red_normal));
            mImgAlreadyAnnounced.setTextColor(getResources().getColor(R.color.black_light));

        }
        else {
            mImgAllIn.setTextColor(getResources().getColor(R.color.black_light));
            mImgOnGoing.setTextColor(getResources().getColor(R.color.black_light));
            mImgAlreadyAnnounced.setTextColor(getResources().getColor(R.color.standard_red_normal));

        }
    }

    public MyJoinDropDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public MyJoinDropDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public MyJoinDropDownView(Context context) {
        super(context);
        this.init();
    }

    private void notifyOnDorpDownClick(int action) {
        if( mOnSettingDropDownClickListener != null ) {
            mOnSettingDropDownClickListener.onSettingDropDown(action);
        }
    }
    private void init() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        FrameLayout.LayoutParams layoutParams=
                new FrameLayout.LayoutParams(width/3, LinearLayout.LayoutParams.WRAP_CONTENT);

        LayoutInflater.from(getContext()).inflate(R.layout.layout_my_join_dropdown, this);

        LinearLayout mllContent = (LinearLayout) findViewById(R.id.ll_content);
        mllContent.setLayoutParams(layoutParams);
        layoutAllIn = (LinearLayout) findViewById(R.id.layout_all_in);
        layoutOnGoing = (LinearLayout) findViewById(R.id.layout_on_going);
        layoutAlreadyAnnounced = (LinearLayout) findViewById(R.id.layout_already_announced);
        mImgAllIn = (TextView) findViewById(R.id.tv_all_in);
        mImgOnGoing = (TextView) findViewById(R.id.tv_on_going);
        mImgAlreadyAnnounced = (TextView) findViewById(R.id.tv_already_announced);

        layoutAllIn.setOnClickListener(mOnClickListener);
        layoutOnGoing.setOnClickListener(mOnClickListener);
        layoutAlreadyAnnounced.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getViewHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public int getViewWidth() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public Point location(int type) {
        return null;
    }

}
