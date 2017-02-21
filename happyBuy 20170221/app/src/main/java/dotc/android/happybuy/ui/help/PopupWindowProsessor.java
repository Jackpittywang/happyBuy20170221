package dotc.android.happybuy.ui.help;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

/**
 * Created by Avazu on 2016/3/29.
 */
public class PopupWindowProsessor {

    static final String TAG = "PopupWindowProsessor";

    private int mHeight;

    private int mWidth;

    private Context mContext;

    private View mRootView;

    private ProxyPopupWindow mPopupWindow;

    private ISizeSensitive mSensitive;

    private boolean mIsShowing;

    public PopupWindowProsessor(Context context) {

        this.mContext = context;

    }

    private OnDismissListener mSettedListener;
    private OnDismissListener listener = new OnDismissListener() {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            mIsShowing = false;
            if (mSettedListener != null) {
                mSettedListener.onDismiss();
            }
        }
    };

    public void dimBackground(boolean isDim) {

        Activity activity = (Activity) this.mContext;
        WindowManager.LayoutParams wlp = activity.getWindow().getAttributes();
        if (isDim) {
            wlp.alpha = (float) 0.7;
        } else {
            wlp.alpha = 1;
        }
        activity.getWindow().setAttributes(wlp);

    }

    private void initPopupWindow() {

        this.mPopupWindow = new ProxyPopupWindow(mContext);
        mPopupWindow.setContentView(mRootView);
        mPopupWindow.setWidth(mWidth);
        mPopupWindow.setHeight(mHeight);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.canUse = true;
        mPopupWindow.setOnDismissListener(listener);

        mIsShowing = false;
    }

    public void setOnDismissListener(OnDismissListener dismissListener) {
        mPopupWindow.canUse = true;
        this.mSettedListener = dismissListener;
        mPopupWindow.setOnDismissListener(listener);
    }

    public android.widget.PopupWindow setView(View view) {

        if (!(view instanceof ISizeSensitive)) {

            throw new RuntimeException("View must be instanceof ISizeSensitive.");

        }

        this.mRootView = view;

        mSensitive = (ISizeSensitive) view;

        this.mWidth = mSensitive.getViewWidth();

        this.mHeight = mSensitive.getViewHeight();

        this.initPopupWindow();

        return this.mPopupWindow;

    }

    public void showAtLocation(View parentView, int type) {
        try {
            if (mIsShowing) {
                return;
            }
            Point point = mSensitive.location(type);
            mPopupWindow.canUse = true;
            mPopupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, point.x, point.y);
            mIsShowing = true;
        }catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void showAsDropDown(View anchroView, int offsetX, int offsetY) {
        try {
            if (mIsShowing) {
                return;
            }
            mPopupWindow.canUse = true;
            mPopupWindow.showAsDropDown(anchroView, offsetX, offsetY);
            mIsShowing = true;
        }catch( Exception e ) {

        }
    }

    public boolean isShowing() {
        return mIsShowing ;
    }
    public void dismiss() {
        if (!mIsShowing) {

            return;
        }
        mPopupWindow.canUse = true;
        mPopupWindow.dismiss();
        mIsShowing = false;
    }

}
