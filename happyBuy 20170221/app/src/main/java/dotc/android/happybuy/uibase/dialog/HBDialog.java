package dotc.android.happybuy.uibase.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import dotc.android.happybuy.R;


/**
 * Created by wangjun on 16/5/3.
 */
public class HBDialog extends Dialog {

    private final Window mWindow;
    public AlertController mController;

    public HBDialog(Context context) {
        super(context, R.style.hb_custom_dialog);
        mWindow = getWindow();
        mWindow.setBackgroundDrawableResource(R.color.dialog_bg_color);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.5f;
//        lp.dimAmount = 1f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mController = new AlertController(context, this, getWindow());
    }

    public class AlertController {
        public final Context mContext;
        public final LayoutInflater mInflater;
        public final Dialog mDialogInterface;
        public View mCustomView;

        public TextView mTitleTextView;
        public TextView mLeftButton;
        public TextView mRightButton;
        public TextView mSingleButton;

        public TextView mMessageTextView;

        public AlertController(Context context, Dialog di, Window window) {
            mContext = context;
            mDialogInterface = di;
            mWindow.requestFeature(Window.FEATURE_NO_TITLE);
            mWindow.setContentView(R.layout.layout_custom_dialog);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            mCustomView = mWindow.findViewById(R.id.custom);
            mTitleTextView = (TextView) findViewById(R.id.title);
            mMessageTextView = (TextView) findViewById(R.id.message);

            mLeftButton = (TextView) mWindow.findViewById(R.id.left_btn_text);
            mRightButton = (TextView) mWindow.findViewById(R.id.right_btn_text);
            mSingleButton = (TextView) mWindow.findViewById(R.id.single_btn_text);

        }

    }

    public static class AlertParams {

        public final Context mContext;
        public final LayoutInflater mInflater;

        public CharSequence mTitleText;

        public CharSequence mLeftText;
        public CharSequence mRightText;
        public CharSequence mMessageText;

        public OnClickListener mLeftBtnOnClickListener;
        public OnClickListener mRightBtnOnClickListener;

        public View mView;

        public AlertParams(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void apply(final AlertController controller) {
            if(TextUtils.isEmpty(mTitleText)){
                controller.mTitleTextView.setVisibility(View.GONE);
            } else {
                controller.mTitleTextView.setText(mTitleText);
            }

            controller.mMessageTextView.setText(mMessageText);
            controller.mLeftButton.setText(mLeftText);
            controller.mRightButton.setText(mRightText);

            controller.mLeftButton.setOnClickListener(new View.OnClickListener(){
                  @Override
                  public void onClick(View v) {
                      if (mLeftBtnOnClickListener != null) {
                          mLeftBtnOnClickListener.onClick(controller.mDialogInterface, 0);
                      }
                      controller.mDialogInterface.dismiss();
                  }
              });
            controller.mRightButton.setOnClickListener(new View.OnClickListener(){
                   @Override
                   public void onClick(View v) {
                       if (mRightBtnOnClickListener != null) {
                           mRightBtnOnClickListener.onClick(controller.mDialogInterface, 1);
                       }
                       controller.mDialogInterface.dismiss();
                   }
               });
        }
    }

    public static class Builder {

        private AlertParams P = null;

        private Context mContext;

        public Builder(Context context) {
            mContext = context;
            P = new AlertParams(mContext);
        }

        public HBDialog create() {
            final HBDialog dialog = new HBDialog(P.mContext);
            P.apply(dialog.mController);
            return dialog;
        }

        public HBDialog show() {
            HBDialog dialog = create();
            dialog.show();
            return dialog;
        }

        public Builder setTitle(CharSequence title) {
            P.mTitleText = title;
            return this;
        }

        public Builder setTitle(int titleId) {
            P.mTitleText = mContext.getString(titleId);
            return this;
        }

        /*
         */
        public Builder setRightButton(int textId, final OnClickListener listener) {
            P.mRightText = mContext.getString(textId);
            P.mRightBtnOnClickListener = listener;
            return this;
        }

        /*
         */
        public Builder setLeftButton(int textId, final OnClickListener onClickListener) {
            P.mLeftText = mContext.getString(textId);
            P.mLeftBtnOnClickListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(int textId, OnClickListener listener) {
            setLeftButton(textId, listener);
            return this;
        }

        public Builder setPositiveButton(int textId, OnClickListener listener) {
            setRightButton(textId, listener);
            return this;
        }

        public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            P.mLeftText = text;
            P.mLeftBtnOnClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            P.mRightText = text;
            P.mRightBtnOnClickListener = listener;
            return this;
        }

        public Builder setMessage(int textId) {
            P.mMessageText = mContext.getString(textId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            P.mMessageText = message;
            return this;
        }

        public Builder setListItem(CharSequence message) {
            P.mMessageText = message;
            return this;
        }

        public Builder setView(View view) {
            P.mView = view;
            return this;
        }
    }
}
