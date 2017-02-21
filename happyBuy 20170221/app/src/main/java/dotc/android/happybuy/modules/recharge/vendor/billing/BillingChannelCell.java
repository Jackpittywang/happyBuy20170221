package dotc.android.happybuy.modules.recharge.vendor.billing;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.modules.recharge.engine.ChannelCell;
import dotc.android.happybuy.modules.recharge.engine.BaseEnv;

/**
 * Created by wangjun on 16/12/19.
 */

public class BillingChannelCell extends ChannelCell implements BillingEnv.OnSetupListener {

    private BillingEnv mBillingEnv;
    private View mImageView;
    private View mMaskView;
    private TextView mTextView;
    private View mProgressBar;

    public BillingChannelCell(Context context, BaseEnv env, PojoPay pojoPay) {
        super(context, env, pojoPay);
        mBillingEnv = (BillingEnv) env;
    }

    @Override
    public View getView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_payment,parent,false);
        mImageView = view.findViewById(R.id.imageview);
        mTextView = (TextView) view.findViewById(R.id.textview);
        mMaskView = view.findViewById(R.id.imageview_mask);
        mProgressBar = view.findViewById(R.id.progressbar);
        if (!TextUtils.isEmpty(pay.desc)) {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(pay.desc);
        } else {
            mTextView.setVisibility(View.INVISIBLE);
        }

        if(mBillingEnv.isSetupDone()){
            mImageView.setEnabled(mBillingEnv.isEnvEnable());
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            mImageView.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            mBillingEnv.addSetupListener(this);
        }
        return view;
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked){
            mMaskView.setVisibility(View.VISIBLE);
        } else {
            mMaskView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSetupFinish(boolean enable) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mImageView.setEnabled(enable);
    }


}
