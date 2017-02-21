package dotc.android.happybuy.modules.detail.listheader;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoParticipateInfo;
import dotc.android.happybuy.http.result.PojoParticpateHistory;
import dotc.android.happybuy.http.result.PojoPrizeUser;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.http.result.PojoProductDetail;
import dotc.android.happybuy.http.result.PreviousProduct;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.awarding.TimerDownManager;
import dotc.android.happybuy.modules.awarding.func.AwardingManager;
import dotc.android.happybuy.modules.userprofile.UserProfileActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.modules.show.ShowListActivity;
import dotc.android.happybuy.ui.activity.WebActivity;
import dotc.android.happybuy.uibase.widget.ColorProgressBar;
import dotc.android.happybuy.uibase.widget.IndicatorView;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.DateUtil;
import dotc.android.happybuy.util.ToastUtils;


/**
 * Created by wangjun on 16/3/29.
 */
public class GoodsDetailListHeader extends LinearLayout implements ViewPager.OnPageChangeListener,View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private Activity mActivity;
    private ViewPager mViewPager;
    private IndicatorView mIndicatorView;
    private ImageView mTenFlagImageView;
    private TextView mPeriodStateTextView;
    private TextView mPeriodLabelTextView;
    private TextView mCoinsUnitTextView;
    private TextView mGoodsNameTextView;
    private TextView mGoodsDesTextView;
    private View mImageTextDetailView;
    private View mTimelineShareView;
    private View mHistoryLookView;

    private View mParticateInfoView;
    private View mNoParticateInfoView;
    private TextView mParticateTimesTextView;
    private TextView mParticateNumberTextView;
    private View mParticateNumberLayout;

    //state begin
    private View mStateDoingView;
    private ColorProgressBar mColorProgressBar;
    private TextView mTotalTimesTextView;
    private TextView mRetainTimesTextView;
    private View mCaclDetailView;
    private View mHistoryCaclDetailView;
    private View mParticipateDetailView;
    private View mHistoryParticipateDetailView;

    private View mStateAwardingView;
    private TextView mAwardingTextView;
    private TextView mAwarding2TextView;

    private View mStateDoneView;
    private ImageView mAwardPortraitImageView;
    private TextView mAwardNumberTextView;
    private TextView mAwardNicknameTextView;
    private TextView mAwardDateTextView;
    private TextView mAwardParticateTimesTextView;

    //state end

    private ImageAdapter mImageAdapter;
    private PojoProductDetail mGoodsDetail;
    private OnRefreshListener mOnRefreshListener;
    private OnParticipateListener mOnParticipateListener;
    private CountDownTimer mCountDownTimer;
    private long mDiffTimestamp;
    private ImageView mPreviousProductHeadView;
//    private ImageView mPreviousProductHeadView;
    private TextView mPreviousProductUserId;
    private TextView mPreviousProductUserData;
    private TextView mPreviousProductUserTimes;

    public GoodsDetailListHeader(Activity context) {
        super(context);
        init(context);
    }

    private void init(Activity context){
        this.mActivity = context;
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.listheader_goods_detail, this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mIndicatorView = (IndicatorView) findViewById(R.id.indicator_view);
        mTenFlagImageView = (ImageView) findViewById(R.id.imageview_ten_flag);
        mPeriodStateTextView = (TextView) findViewById(R.id.textview_period_state);
        mPeriodLabelTextView = (TextView) findViewById(R.id.textview_period);
        mCoinsUnitTextView = (TextView) findViewById(R.id.coins_unit);

        mGoodsNameTextView = (TextView) findViewById(R.id.textview_goodsname);
        mGoodsDesTextView = (TextView) findViewById(R.id.textview_goodsdes);

        mParticateInfoView = findViewById(R.id.layout_detail_particate);
        mNoParticateInfoView = findViewById(R.id.layout_detail_no_particate);
        mParticateTimesTextView = (TextView) findViewById(R.id.textview_particate_times);
        mParticateNumberTextView = (TextView) findViewById(R.id.textview_particate_number);
        mParticateNumberLayout = findViewById(R.id.layout_particate_number);

        mStateDoingView = findViewById(R.id.layout_status_doing);
        mStateDoneView = findViewById(R.id.layout_status_done);
        mColorProgressBar = (ColorProgressBar) findViewById(R.id.progressbar);
        mTotalTimesTextView = (TextView) findViewById(R.id.textview_total_times);
        mRetainTimesTextView = (TextView) findViewById(R.id.textview_retain_times);
        mCaclDetailView = findViewById(R.id.textview_calc_detail);
        mHistoryCaclDetailView = findViewById(R.id.textview_history_calc_detail);
        mParticipateDetailView = findViewById(R.id.textview_particate_detail);
        mHistoryParticipateDetailView = findViewById(R.id.textview_history_particate_detail);

        mAwardPortraitImageView = (ImageView) findViewById(R.id.imageview_award_icon);
        mAwardNicknameTextView = (TextView) findViewById(R.id.textview_award_nickname);
        mAwardNumberTextView = (TextView) findViewById(R.id.textview_luck_number);
        mAwardDateTextView = (TextView) findViewById(R.id.textview_award_date);
        mAwardParticateTimesTextView = (TextView) findViewById(R.id.textview_award_particape_times);

        mStateAwardingView = findViewById(R.id.layout_status_awarding);
        mAwardingTextView = (TextView) findViewById(R.id.textview_awarding_text1);
        mAwarding2TextView = (TextView) findViewById(R.id.textview_awarding_text2);

        mImageTextDetailView = findViewById(R.id.layout_imagetext_detail);
        mTimelineShareView = findViewById(R.id.layout_timeline_share);
        mHistoryLookView = findViewById(R.id.layout_history_look);

//        mPreviousProductHeadView= (ImageView) findViewById(R.id.iv_history_headview);
        mPreviousProductHeadView= (ImageView) findViewById(R.id.iv_history_headview);
        mPreviousProductUserId = (TextView) findViewById(R.id.tv_user_id);
        mPreviousProductUserData = (TextView) findViewById(R.id.tv_user_data);
        mPreviousProductUserTimes = (TextView) findViewById(R.id.textview_user_times);


        mImageTextDetailView.setOnClickListener(this);
        mTimelineShareView.setOnClickListener(this);
        mHistoryLookView.setOnClickListener(this);
        mCaclDetailView.setOnClickListener(this);
        mHistoryCaclDetailView.setOnClickListener(this);
        mParticipateDetailView.setOnClickListener(this);
        mHistoryParticipateDetailView.setOnClickListener(this);
        mParticateNumberLayout.setOnClickListener(this);
        mNoParticateInfoView.setOnClickListener(this);
        mAwardPortraitImageView.setOnClickListener(this);
        mPreviousProductHeadView.setOnClickListener(this);
    }

    public void setOnRefreshListener(OnRefreshListener listener){
        this.mOnRefreshListener = listener;
    }

    public void setOnParticipateListener(OnParticipateListener listener){
        mOnParticipateListener = listener;
    }

    public void setFillData(PojoProductDetail goods){
        this.mGoodsDetail = goods;
        mDiffTimestamp = System.currentTimeMillis() - (goods.server_time*1000);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
//        HBLog.d(TAG + " setDiffTimestamp server time: " + df.format(goods.server_time * 1000) + " " + mDiffTimestamp);
        fillData();
    }

    private void fillData(){
        List<View> views = new ArrayList<View>();
        for(final String url:mGoodsDetail.product.productImages){
            ImageView imageView = new ImageView(mActivity);
            Glide.with(mActivity).load(url).fitCenter().crossFade().into(imageView);
            views.add(imageView);
        }
        mImageAdapter = new ImageAdapter(views);
        mViewPager.setAdapter(mImageAdapter);
        mIndicatorView.setDotCount(views.size());
        mIndicatorView.setCurrentDot(0);
        mViewPager.addOnPageChangeListener(this);
        if(mGoodsDetail.product.coins_unit == 10){
            String country= AppUtil.getMetaData(getContext(),"country");
            if(country.equals("th")){
                mTenFlagImageView.setImageResource(R.drawable.ic_ten_flag);
            }else if(country.equals("vn")){
                mTenFlagImageView.setImageResource(R.drawable.ic_ten_flagyunan);
            }
            mTenFlagImageView.setVisibility(View.VISIBLE);
        }else if (mGoodsDetail.product.coins_unit == 100){
            String country= AppUtil.getMetaData(getContext(),"country");
            if(country.equals("th")){
                mTenFlagImageView.setImageResource(R.drawable.ic_hundred_flag);
            }else if(country.equals("vn")){
                mTenFlagImageView.setImageResource(R.drawable.ic_hundred_flag);
            }
            mTenFlagImageView.setVisibility(View.VISIBLE);
        }
        else {
            mTenFlagImageView.setVisibility(View.INVISIBLE);
        }

        if(mGoodsDetail.product.status == HttpProtocol.PRODUCT_STATE.ONSALE
                ||mGoodsDetail.product.status == HttpProtocol.PRODUCT_STATE.SOLDOUT
                ||mGoodsDetail.product.status == HttpProtocol.PRODUCT_STATE.AWARDING){
            mPeriodStateTextView.setText(R.string.product_state_doing);
        } else {
            mPeriodStateTextView.setText(R.string.product_state_done);
        }
        mPeriodLabelTextView.setText(getResources().getString(R.string.label_period_,mGoodsDetail.product.periodId));
        if(mGoodsDetail.product.coins_unit!=1){
            mCoinsUnitTextView.setVisibility(View.VISIBLE);
            mCoinsUnitTextView.setText(getResources().getString(R.string.coins_unit,mGoodsDetail.product.coins_unit));
        }else {
            mCoinsUnitTextView.setVisibility(View.GONE);
        }

        mGoodsNameTextView.setText(mGoodsDetail.product.productName);
        mGoodsDesTextView.setText(mGoodsDetail.product.productDesc);
        fillParticateInfoView();
        fillStateInfoView();
    }

    private void fillStateInfoView(){
        final PojoProduct product = mGoodsDetail.product;
        if(product.status == HttpProtocol.PRODUCT_STATE.AWARDING){
            mStateDoingView.setVisibility(View.GONE);
            mStateAwardingView.setVisibility(View.VISIBLE);
            mStateDoneView.setVisibility(View.GONE);
            long awardTime = product.awardTime*1000;
//            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
//            HBLog.d(TAG+" bindCountDown award time: at " + df.format(awardTime)+" "+mDiffTimestamp);
            if(awardTime >= System.currentTimeMillis()-mDiffTimestamp){
                mAwardingTextView.setText(R.string.award_count_down);
                mAwarding2TextView.setText("");
                if(mCountDownTimer!=null){
                    mCountDownTimer.cancel();
                }
                TimerDownManager.getInstance(GlobalContext.get()).addTimer(product.productItemId,mDiffTimestamp,product.awardTime);
                long time = awardTime - (System.currentTimeMillis()-mDiffTimestamp);
                mCountDownTimer = new CountDownTimer(time,10) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mAwarding2TextView.setText(DateUtil.formateCountDownTime(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        HBLog.d(TAG+" onFinish ");
                        mAwardingTextView.setText(R.string.computing);
                        mAwarding2TextView.setText("");
                        doQueryAwardResult(product.productItemId);
                        if(mOnRefreshListener!=null){
                            mOnRefreshListener.onStartRefreshUI();
                        }
                    }
                };
                mCountDownTimer.start();
            } else {
                mAwardingTextView.setText(R.string.computing);
                mAwarding2TextView.setText("");
            }
            String image = product.productImages.size()>0?product.productImages.get(0):"";
//            AwardingManager.getInstance(mActivity).showAwardingDialog(mActivity,product.productId,product.productName,
//                    image,product.productItemId, product.periodId,mGoodsDetail.server_time+"",product.awardTime+"");
        } else if(product.status == HttpProtocol.PRODUCT_STATE.AWARD
                ||product.status == HttpProtocol.PRODUCT_STATE.SHARD) {
            PojoPrizeUser prizeUser = mGoodsDetail.prizeUser;
            mStateDoingView.setVisibility(View.GONE);
            mStateAwardingView.setVisibility(View.GONE);
            mStateDoneView.setVisibility(View.VISIBLE);
            Glide.with(mActivity).load(prizeUser.photoUrl).crossFade().placeholder(R.drawable.pic_circle_portrait_placeholder).into(mAwardPortraitImageView);
            mAwardNumberTextView.setText(product.awardNumber);
            mAwardNicknameTextView.setText(prizeUser.nickname);
            mAwardDateTextView.setText(product.awardTimeStr);
            mAwardParticateTimesTextView.setText(mActivity.getString(R.string.lable_period_times,String.valueOf(prizeUser.totalTimes)));

        } else {
            mStateDoingView.setVisibility(View.VISIBLE);
            mStateAwardingView.setVisibility(View.GONE);
            mStateDoneView.setVisibility(View.GONE);
            mColorProgressBar.setProgress(product.totalTimes -product.remainTimes,product.totalTimes);
            mTotalTimesTextView.setText(String.valueOf(mGoodsDetail.product.totalTimes));
            mRetainTimesTextView.setText(String.valueOf(mGoodsDetail.product.remainTimes));
            //上期中奖纪录
            PreviousProduct previousProduct = mGoodsDetail.previousProduct;
            if(previousProduct==null||previousProduct.equals("")||previousProduct.prizeUser==null){
                findViewById(R.id.ll_previous_product).setVisibility(GONE);
            } else {
                Glide.with(mActivity).load(previousProduct.prizeUser.photoUrl).placeholder(R.drawable.pic_circle_portrait_placeholder).crossFade().into(mPreviousProductHeadView);
                mPreviousProductUserTimes.setText(mActivity.getString(R.string.lable_period_times,String.valueOf(previousProduct.prizeUser.totalTimes)));
//                mPreviousProductUserTimes.setText(previousProduct.prizeUser.totalTimes+"");
                mPreviousProductUserId.setText(previousProduct.prizeUser.nickname);
                mPreviousProductUserData.setText(previousProduct.awardTimeStr);
            }

        }
    }
    private void doQueryAwardResult(final String productItemId){
        HBLog.d(TAG + " doQueryAwardResult " + productItemId);
        Map<String,Object> param = new HashMap<>();
        param.put("item_id", productItemId);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.AWARD_RESULT, param, new Network.JsonCallBack<PojoParticpateHistory>() {
            @Override
            public void onSuccess(PojoParticpateHistory history) {
                HBLog.d(TAG + " doQueryAwardResult onSuccess " + history);
                if(history.product_item_status==HttpProtocol.PRODUCT_STATE.AWARD
                        &&history.award_uid.equals(PrefUtils.getString(PrefConstants.UserInfo.UID, ""))){
                    PojoProduct product = mGoodsDetail.product;
                    String image = product.productImages.size()>0?product.productImages.get(0):"";
                    AwardingManager.getInstance(GlobalContext.get()).showAwardedDialog(GlobalContext.get(),product.productId,
                            product.productName,image,product.productItemId,product.periodId);
//                    WinningManager.getInstance(GlobalContext.get()).showAwardDialog();
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " doQueryAwardResult onFailed " + code + " " + message + " " + e);
                ToastUtils.showLongToast(GlobalContext.get(), message);
            }

            @Override
            public Class<PojoParticpateHistory> getObjectClass() {
                return PojoParticpateHistory.class;
            }
        });
    }
    private void fillParticateInfoView(){
        PojoParticipateInfo participateInfo = mGoodsDetail.participateInfo;
        if(participateInfo!=null&&participateInfo.participateTimes>0){
            mParticateInfoView.setVisibility(View.VISIBLE);
            mNoParticateInfoView.setVisibility(View.GONE);
            mParticateTimesTextView.setText(String.valueOf(participateInfo.participateTimes));
            mParticateNumberTextView.setText(buildNumberString(participateInfo.participateCodes));
        } else {
            mParticateInfoView.setVisibility(View.GONE);
            mNoParticateInfoView.setVisibility(View.VISIBLE);
        }
    }

    private String buildNumberString(List<String> codes){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<codes.size();i++){
            stringBuilder.append(codes.get(i));
            stringBuilder.append("  ");
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mIndicatorView.setCurrentDot(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_imagetext_detail: {
                Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Click_Product_Details,null,null);
                Intent intent = new Intent(mActivity, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TITLE, getResources().getString(R.string.image_text_detail));
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(mGoodsDetail.imageTextUrl));
                mActivity.startActivity(intent);
                break;
            }
            case R.id.layout_timeline_share:
                Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Click_Show,null,null);
                Intent shareintent = new Intent(mActivity, ShowListActivity.class);
                shareintent.putExtra("type",ShowListActivity.TYPE_SHOW_LIST_PRODUCT);
                shareintent.putExtra("product_id",mGoodsDetail.product.productId);
                mActivity.startActivity(shareintent);
                break;
            case R.id.layout_history_look:{
                Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Click_Past,null,null);
                Intent intent = new Intent(mActivity, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TITLE, getResources().getString(R.string.history_look));
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(mGoodsDetail.historyAwardUrl));
                mActivity.startActivity(intent);
                break;
            }

            case R.id.textview_calc_detail: {
                Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Click_Lottery_Details,"Past",null);
                Intent intent = new Intent(mActivity, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TITLE, getResources().getString(R.string.calc_detail));
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(mGoodsDetail.calculationDetailUrl));
                mActivity.startActivity(intent);
                break;
            }
            case R.id.textview_history_calc_detail: {
                if(!mGoodsDetail.previousProduct.calculationDetailUrl.equals("")){
                    Analytics.sendUIEvent(AnalyticsEvents.ProductDetails.Click_Lottery_Details,"last",null);
                    Intent intent = new Intent(mActivity, WebActivity.class);
                    intent.putExtra(WebActivity.EXTRA_TITLE, getResources().getString(R.string.calc_detail));
                    intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(mGoodsDetail.previousProduct.calculationDetailUrl));
                    mActivity.startActivity(intent);
                }
                break;
            }
            case R.id.textview_particate_detail: {
                Analytics.sendUIEvent(AnalyticsEvents.ProductDetails.Click_Buy_Details,"last",null);
                Intent intent = new Intent(mActivity, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TITLE, getResources().getString(R.string.part_detail));
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(mGoodsDetail.prizeUser.participateDetailUrl));
                mActivity.startActivity(intent);
                break;
            }
            case R.id.textview_history_particate_detail: {
                Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Click_Buy_Details_Has_Open,"Past",null);
                Intent intent = new Intent(mActivity, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TITLE, getResources().getString(R.string.part_detail));
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(mGoodsDetail.previousProduct.prizeUser.participateDetailUrl));
                mActivity.startActivity(intent);
                break;
            }
            case R.id.layout_particate_number:
                Intent intent = new Intent(mActivity, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TITLE, getResources().getString(R.string.part_detail));
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(mGoodsDetail.participateInfo.participateDetailUrl));
                mActivity.startActivity(intent);
                break;
            case R.id.layout_detail_no_particate:
                if(mOnParticipateListener!=null){
                    mOnParticipateListener.onParticipate(v);
                }
                break;
            case R.id.iv_history_headview:
                Intent userProfileIntent = new Intent(mActivity, UserProfileActivity.class);
                userProfileIntent.putExtra(UserProfileActivity.EXTRA_UID, mGoodsDetail.previousProduct.prizeUser.userId);
                userProfileIntent.putExtra(UserProfileActivity.EXTRA_NICKNAME, mGoodsDetail.previousProduct.prizeUser.nickname);
                mActivity.startActivity(userProfileIntent);
                break;
            case R.id.imageview_award_icon:
                Intent prizeUserIntent = new Intent(mActivity, UserProfileActivity.class);
                prizeUserIntent.putExtra(UserProfileActivity.EXTRA_UID, mGoodsDetail.prizeUser.userId);
                prizeUserIntent.putExtra(UserProfileActivity.EXTRA_NICKNAME, mGoodsDetail.prizeUser.nickname);
                mActivity.startActivity(prizeUserIntent);
                break;
        }
    }

    private class ImageAdapter extends PagerAdapter {

        private List<View> views;

        public ImageAdapter(List<View> views){
            this.views = views;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager)container).addView(views.get(position));
            return views.get(position);
        }
    }

    public interface OnRefreshListener{
        void onStartRefreshUI();
    }

    public interface OnParticipateListener{
        void onParticipate(View view);
    }

}
