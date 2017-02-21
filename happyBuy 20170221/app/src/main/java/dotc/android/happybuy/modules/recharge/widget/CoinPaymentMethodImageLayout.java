package dotc.android.happybuy.modules.recharge.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.util.AppUtil;


public class CoinPaymentMethodImageLayout extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private List<PojoPay> mFaceValueList;

    private OnItemImageClickListener mOnItemImageClickListener;
    private CheckedTextView[] mCheckTextViews;
    private ImageView[] mCheckImageViews;
//    private CheckedTextView mCheckedTextView;

    private int mCheckedPosition;
    private List<ImageView> mCheckboxs;

    public CoinPaymentMethodImageLayout(Context context) {
        super(context);
        init(context);
    }

    public CoinPaymentMethodImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(VERTICAL);
    }

    public void setOnItemClickListener(OnItemImageClickListener listener) {
        this.mOnItemImageClickListener = listener;
    }

    public void setCountValues(List<PojoPay> faceValues, int checkPosition) {
        mFaceValueList = faceValues;
        buildViews();
    }

    private void buildViews() {
        removeAllViews();
        LinearLayout linearLayout = null;
        LayoutParams layoutParams = newTimesTextViewLp();
        int rowNum = mFaceValueList.size()/3+(mFaceValueList.size()%3>0?1:0);
        mCheckImageViews=new ImageView[rowNum*3];
//        mCheckTextViews = new CheckedTextView[rowNum*3];
        mCheckboxs = new ArrayList<>();
        for (int i = 0; i < rowNum*3; i++) {
            if (i % 3 == 0) {
                linearLayout = newLinearLayout();
                addView(linearLayout, newInnerLayoutLp());
            }
//            CheckedTextView textView = newTimesTextView();
            ImageView textView=newTimesImageView();
            if(i<mFaceValueList.size()){
                PojoPay pojoPay = mFaceValueList.get(i);
                switch (Integer.valueOf(pojoPay.id)){
                    case HttpProtocol.Payment.BLUEPAY_BILLING:
                        if(i==0){
                                textView.setImageResource(R.drawable.ic_payment_billing_c);
                        }else{
                            textView.setImageResource(R.drawable.ic_payment_billing_p);
                        }
                            break;
                    case  HttpProtocol.Payment.BLUEPAY_SMS:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_sms_c);
                        }else{
                            textView.setImageResource(R.drawable.ic_payment_sms_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_TRUEMONEY:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_truemoney_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_truemoney_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_12CALL:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_ais_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_ais_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_HAPPY:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_dtac_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_dtac_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_BANK:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_bank_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_bank_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_LINE:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_line_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_line_p);
                        }
                        break;

                    //越南
                    case  HttpProtocol.Payment.BLUEPAY_VIETTEL:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_viettel_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_viettel_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_VINAPHONE:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_vinaphone_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_vinaphone_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_MOBIFONE:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_mobifone_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_mobifone_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_VTC:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_vtc_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_vtc_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_VIETNAM_SMS:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_vietnam_sms_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_vietnam_sms_p);
                        }
                        break;
                    case  HttpProtocol.Payment.BLUEPAY_HOPE:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_hope_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_hope_p);
                        }
                        break;
                    case HttpProtocol.Payment.GOOGLEPAY:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_billing_c);
                        }else{
                            textView.setImageResource(R.drawable.ic_payment_billing_p);
                        }
                        break;

                    //印尼
                    case  HttpProtocol.Payment.BLUEPAY_MOGPLAY:
                        if(i==0){
                            textView.setImageResource(R.drawable.ic_payment_line_c);
                        }else {
                            textView.setImageResource(R.drawable.ic_payment_line_p);
                        }
                        break;
                }
                textView.setTag(pojoPay.id);
                mCheckedPosition=i;
                textView.setOnClickListener(this);

            } else {
                textView.setBackgroundDrawable(null);
            }
            linearLayout.addView(textView, layoutParams);
            mCheckImageViews[i]=textView;
            mCheckboxs.add(textView);
        }
//        requestLayout();
        requestLayout();
        invalidate();
    }

    private LinearLayout newLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(HORIZONTAL);
        return linearLayout;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private CheckedTextView newTimesTextView() {
        int padding = AppUtil.dp2px(mContext, 0);
        CheckedTextView textView = new CheckedTextView(mContext);
        textView.setGravity(Gravity.CENTER);
        textView.setTextAlignment(TEXT_ALIGNMENT_GRAVITY);
        textView.setPadding(padding, 0, padding, 0);
        return textView;
    }

    private ImageView newTimesImageView() {
        int padding = AppUtil.dp2px(mContext, 0);
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(padding, 0, padding, 0);
        return imageView;
    }

    private LayoutParams newTimesTextViewLp() {
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        layoutParams.leftMargin = AppUtil.dp2px(mContext, 10);
        layoutParams.rightMargin = AppUtil.dp2px(mContext, 10);
        return layoutParams;
    }

    private LayoutParams newInnerLayoutLp() {
        int height = AppUtil.dp2px(mContext, 63);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        layoutParams.topMargin = AppUtil.dp2px(mContext, 10);
        return layoutParams;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemImageClickListener != null) {
            mOnItemImageClickListener.onItemClick(v, mCheckboxs);
        }
    }

    public int getCheckedItem() {
        return mCheckedPosition;
    }

    public interface OnItemImageClickListener {
        void onItemClick(View view,List<ImageView> mCheckboxs);
    }
}
