package dotc.android.happybuy.modules.recharge.vendor.bluepay;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.modules.recharge.engine.Vendors;
import dotc.android.happybuy.modules.recharge.engine.ChannelCell;
import dotc.android.happybuy.modules.recharge.engine.BaseEnv;

/**
 * Created by wangjun on 16/12/19.
 */

public class BluePayChannelCell extends ChannelCell implements BluePayEnv.OnSetupListener {

    private BluePayEnv mBluePayEnv;
    private View mImageView;
    private View mMaskView;
    private TextView mTextView;
    private View mProgressBar;
    private Map<String, Integer> mIdMap;

    public BluePayChannelCell(Context context, BaseEnv env, PojoPay pojoPay) {
        super(context, env, pojoPay);
        mBluePayEnv = (BluePayEnv) env;
        init();
    }

    private void init(){
        mIdMap = new HashMap<>();
        mIdMap.put(Vendors.TAG_AIS, R.drawable.payment_icon_ais);
        mIdMap.put(Vendors.TAG_BANK, R.drawable.payment_icon_scb);
        mIdMap.put(Vendors.TAG_DTAC, R.drawable.payment_icon_dtac);
        mIdMap.put(Vendors.TAG_LINEPAY, R.drawable.payment_icon_linepay);
        mIdMap.put(Vendors.TAG_TRUEMONEY, R.drawable.payment_icon_truemoney);
    }

    @Override
    public View getView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_payment_blue, parent, false);
        mImageView = view.findViewById(R.id.imageview);
        mTextView = (TextView) view.findViewById(R.id.textview);
        mMaskView = view.findViewById(R.id.imageview_mask);
        mProgressBar = view.findViewById(R.id.progressbar);
        mImageView.setBackgroundResource(mIdMap.get(pay.id));
        if (!TextUtils.isEmpty(pay.desc)) {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(pay.desc);
        } else {
            mTextView.setVisibility(View.INVISIBLE);
        }
        if (mBluePayEnv.isSetupDone()) {
            mImageView.setEnabled(mBluePayEnv.isEnvEnable());
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            mImageView.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            mBluePayEnv.addSetupListener(this);
        }
        return view;
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked) {
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
