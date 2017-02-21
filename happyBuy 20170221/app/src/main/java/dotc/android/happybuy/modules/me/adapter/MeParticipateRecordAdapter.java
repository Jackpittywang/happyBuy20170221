package dotc.android.happybuy.modules.me.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoParticpateHistory;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.awarding.func.AwardingManager;
import dotc.android.happybuy.modules.me.fragment.MeChildParticipateFragment;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.push.PushMessageDispatcher;
import dotc.android.happybuy.uibase.widget.ColorProgressBar;
import dotc.android.happybuy.util.DateUtil;
import dotc.android.happybuy.util.ToastUtils;


/**
 * Created by wangjun on 16/2/1.
 */
public class MeParticipateRecordAdapter extends BaseAdapter {

    private final String TAG = this.getClass().getSimpleName();
    private List<PojoParticpateHistory> mPojoCategoryList;
    private MeChildParticipateFragment mFragment;
    private Context mContext;
    private LayoutInflater mInflater;

    private final int VIEW_TYPE_ONSALE = 0x00;//进行中
    private final int VIEW_TYPE_AWARDING = 0x01;//开奖中
    private final int VIEW_TYPE_AWARDED = 0x02;//已揭晓
//    private AppIconLoader mAppIconLoader;
    private long mUpdateTimestamp;
    private Map<String,AwardCountDownTimer> mCountDownTimerMap;
    private long mServerTime;
    private long mDiffTimestamp;

    public MeParticipateRecordAdapter(MeChildParticipateFragment fragment){
        mPojoCategoryList = new ArrayList<>();
        mContext = fragment.getContext();
        this.mFragment = fragment;
        mInflater = LayoutInflater.from(mContext);
        mCountDownTimerMap = new HashMap<>();
    }

//    public ParticipateRecordAdapter(Context context, List<PojoParticpateHistory> pojoCategoryList){
//        mPojoCategoryList = pojoCategoryList;
//        mContext = context;
//        mInflater = LayoutInflater.from(mContext);
//    }

    public void setDiffTimestamp(long serverTime){
//        HBLog.d(TAG+" setDiffTimestamp "+System.currentTimeMillis()+" "+serverTime);
        mServerTime = serverTime;
        mDiffTimestamp = System.currentTimeMillis() - (serverTime*1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        HBLog.d(TAG+" setDiffTimestamp server time: " + df.format(serverTime*1000)+" "+mDiffTimestamp);
    }

    public void updateList(List<PojoParticpateHistory> pojoCategoryList){
        mPojoCategoryList = pojoCategoryList;
        mUpdateTimestamp = System.currentTimeMillis();
        notifyDataSetChanged();
    }

    public void appendList(List<PojoParticpateHistory> pojoCategoryList){
        mPojoCategoryList.addAll(pojoCategoryList);
        notifyDataSetChanged();
    }

    public PojoParticpateHistory getLastItem(){
        return mPojoCategoryList.get(mPojoCategoryList.size()-1);
    }

    public int getCount() {
        return mPojoCategoryList.size();
    }

    @Override
    public PojoParticpateHistory getItem(int i) {
        return mPojoCategoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        PojoParticpateHistory particpateHistory = getItem(position);
        if(particpateHistory.product_item_status == HttpProtocol.PRODUCT_STATE.ONSALE
                ||particpateHistory.product_item_status == HttpProtocol.PRODUCT_STATE.SOLDOUT){
            return VIEW_TYPE_ONSALE;
        } else if(particpateHistory.product_item_status == HttpProtocol.PRODUCT_STATE.AWARDING){
            return VIEW_TYPE_AWARDING;
        }
        return VIEW_TYPE_AWARDED;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int viewtype = getItemViewType(position);
        if(viewtype == VIEW_TYPE_ONSALE){
            return getOnSaleView(position,convertView,viewGroup);
        } else if(viewtype == VIEW_TYPE_AWARDING){
            return getAwardingView(position, convertView, viewGroup);
        } else if(viewtype == VIEW_TYPE_AWARDED){
            return getAwardedView(position, convertView, viewGroup);
        }
        return null;
    }

    private View getOnSaleView(int position, View convertView, ViewGroup viewGroup){
        OnSaleViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_participate_onsale, viewGroup, false);
            holder = new OnSaleViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (OnSaleViewHolder) convertView.getTag();
        }
        final PojoParticpateHistory history = mPojoCategoryList.get(position);
        Glide.with(mContext).load(history.default_image).placeholder(R.drawable.ic_me_product_default).crossFade().into(holder.imageView);
        holder.titleTextView.setText(history.product_name);
        holder.periodView.setText(history.period+"");
        holder.timesTextView.setText(String.valueOf(history.buy_item_count));

        holder.progressBar.setProgress(history.total_units - history.remain_units, history.total_units);
        holder.totalTimesTextView.setText(history.total_units +"");
        holder.remainTimesTextView.setText(history.remain_units + "");
        return convertView;
    }

    private View getAwardingView(int position, View convertView, ViewGroup viewGroup){
        AwardingViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_participate_awarding, viewGroup, false);
            holder = new AwardingViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (AwardingViewHolder) convertView.getTag();
        }
        final PojoParticpateHistory history = mPojoCategoryList.get(position);
        Glide.with(mContext).load(history.default_image).placeholder(R.drawable.ic_me_product_default).crossFade().into(holder.imageView);
        holder.titleTextView.setText(history.product_name);
        holder.periodView.setText(history.period+"");
        holder.timesTextView.setText(String.valueOf(history.buy_item_count));

        bindCountDown(holder,history);
//        holder.countDownTextView.setText();
        return convertView;
    }

    private View getAwardedView(int position, View convertView, ViewGroup viewGroup){
        AwardedViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_participate_award, viewGroup, false);
            holder = new AwardedViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (AwardedViewHolder) convertView.getTag();
        }
        final PojoParticpateHistory history = mPojoCategoryList.get(position);

        holder.titleTextView.setText(history.product_name);
        holder.periodView.setText(history.period+"");
        holder.myPartTimeTextView.setText(mContext.getString(R.string.lable_period_times,history.buy_item_count));
        holder.winnerextView.setText(mContext.getString(R.string.lable_winner,history.award_nickname));
        holder.timesTextView.setText(mContext.getString(R.string.lable_period_times,history.award_buy_count));
        holder.numberTextView.setText(mContext.getString(R.string.lable_win_number, history.award_code));
        Glide.with(mContext).load(history.default_image).placeholder(R.drawable.ic_me_product_default).crossFade().into(holder.imageView);

        if(mFragment.getUid().equals(history.award_uid)){
            holder.winImageView.setVisibility(View.VISIBLE);
        } else {
            holder.winImageView.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class AwardedViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView winnerextView;
        TextView timesTextView;
        TextView numberTextView;
        TextView periodView;
        TextView myPartTimeTextView;
        ImageView winImageView;
        public AwardedViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.imageview);
            titleTextView = (TextView) view.findViewById(R.id.textview_title);
            winnerextView = (TextView) view.findViewById(R.id.textview_winner);
            timesTextView = (TextView) view.findViewById(R.id.textview_times);
            numberTextView = (TextView) view.findViewById(R.id.textview_number);
            periodView = (TextView) view.findViewById(R.id.textview_period);
            myPartTimeTextView = (TextView) view.findViewById(R.id.textview_my_times);
            winImageView = (ImageView) view.findViewById(R.id.img_winning);
        }
    }

    static class AwardingViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView timesTextView;
        TextView numberTextView;
        TextView periodView;

        View awardingSetlayout;
        View awardinglayout;
        View computinglayout;
        TextView countDownTextView;

        public AwardingViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.imageview);
            titleTextView = (TextView) view.findViewById(R.id.textview_title);
            timesTextView = (TextView) view.findViewById(R.id.textview_times);
            numberTextView = (TextView) view.findViewById(R.id.textview_number);
            periodView = (TextView) view.findViewById(R.id.textview_period);

            awardingSetlayout = view.findViewById(R.id.layout_awarding_set);
            awardinglayout = view.findViewById(R.id.layout_awarding);
            computinglayout = view.findViewById(R.id.textview_computing);
            countDownTextView = (TextView) view.findViewById(R.id.textview_count_down);

        }
    }

    static class OnSaleViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView periodView;
        TextView timesTextView;
        ColorProgressBar progressBar;
        TextView totalTimesTextView;
        TextView remainTimesTextView;

        public OnSaleViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.imageview);
            titleTextView = (TextView) view.findViewById(R.id.textview_title);
            timesTextView = (TextView) view.findViewById(R.id.textview_times);
            periodView = (TextView) view.findViewById(R.id.textview_period);
            progressBar = (ColorProgressBar) view.findViewById(R.id.progressbar);
            totalTimesTextView = (TextView) view.findViewById(R.id.textview_total_times);
            remainTimesTextView = (TextView) view.findViewById(R.id.textview_retain_times);
        }
    }

    public void clearTimer(){
        for(String key:mCountDownTimerMap.keySet()){
            AwardCountDownTimer countDownTimer = mCountDownTimerMap.get(key);
            if(!countDownTimer.isFinish()){
                countDownTimer.cancel();
            }
        }
    }

    private void bindCountDown(AwardingViewHolder viewHolder,PojoParticpateHistory particpateHistory){
        viewHolder.awardingSetlayout.setTag(particpateHistory);
        long awardTime = Long.parseLong(particpateHistory.award_time)*1000;
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        HBLog.d(TAG+" bindCountDown award time: at " + df.format(awardTime)+" "+mDiffTimestamp);
        if(awardTime < System.currentTimeMillis()-mDiffTimestamp){
            viewHolder.awardinglayout.setVisibility(View.GONE);
            viewHolder.computinglayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.awardinglayout.setVisibility(View.VISIBLE);
            viewHolder.computinglayout.setVisibility(View.GONE);

            AwardCountDownTimer countDownTimer = mCountDownTimerMap.get(particpateHistory.product_item_id);
            if(countDownTimer==null){
                long time = awardTime - (System.currentTimeMillis()-mDiffTimestamp);
                HBLog.d(TAG+" bindCountDown time:"+time);
                countDownTimer = new AwardCountDownTimer(viewHolder,particpateHistory,time);
                countDownTimer.start();
            }
        }

//        AwardingManager.getInstance(mContext).showAwardingDialog(mContext,particpateHistory.product_id,particpateHistory.product_name,
//                particpateHistory.default_image,particpateHistory.product_item_id,particpateHistory.period,particpateHistory.award_time,
//                String.valueOf(mServerTime));
    }

    class AwardCountDownTimer extends CountDownTimer {

        private AwardingViewHolder viewHolder;
        private PojoParticpateHistory particpateHistory;
        private boolean finished;

        public AwardCountDownTimer(AwardingViewHolder viewHolder,PojoParticpateHistory particpateHistory,long timestamp) {
            super(timestamp, 10);
            this.viewHolder = viewHolder;
            this.particpateHistory = particpateHistory;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            PojoParticpateHistory item = (PojoParticpateHistory) viewHolder.awardingSetlayout.getTag();
            if(item.product_item_id == particpateHistory.product_item_id){
//                long minute = (millisUntilFinished%(60*60*1000))/(60*1000);
//                long second = (millisUntilFinished%(60*1000))/1000;
//                long millSecond = (millisUntilFinished%1000);
//                String timeString = minute+":"+second+":"+millSecond;
                viewHolder.countDownTextView.setText(DateUtil.formateCountDownTime(millisUntilFinished));
            }
        }

        @Override
        public void onFinish() {
            finished = true;
            HBLog.d(TAG+" onFinish ");
            PojoParticpateHistory item = (PojoParticpateHistory) viewHolder.awardingSetlayout.getTag();
            if(item.product_item_id == particpateHistory.product_item_id){
                viewHolder.awardinglayout.setVisibility(View.GONE);
                viewHolder.computinglayout.setVisibility(View.VISIBLE);
            } else {
                viewHolder.awardinglayout.setVisibility(View.VISIBLE);
                viewHolder.computinglayout.setVisibility(View.GONE);
            }
            doQueryAwardResult(particpateHistory);
        }

        boolean isFinish(){
            return finished;
        }
    }

    private void doQueryAwardResult(final PojoParticpateHistory particpateHistory){
        HBLog.d(TAG + " doQueryAwardResult " + particpateHistory);
        Map<String,Object> param = new HashMap<>();
        param.put("item_id", particpateHistory.product_item_id);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.AWARD_RESULT, param, new Network.JsonCallBack<PojoParticpateHistory>() {
            @Override
            public void onSuccess(PojoParticpateHistory history) {
                HBLog.d(TAG + " doQueryAwardResult onSuccess " + history);
                int position = findPosition(particpateHistory);
                if(position>-1&&mFragment!=null&&!mFragment.isRemoving()&&!mFragment.isDetached()){
//                    mPojoCategoryList.remove(position);
//                    mPojoCategoryList.add(position, history);
                    //
                    PojoParticpateHistory pph = mPojoCategoryList.get(position);
                    pph.award_uid = history.award_uid;
                    pph.award_buy_count = history.buy_item_count;//attention
                    pph.award_code = history.award_code;
                    pph.award_nickname = history.award_nickname;
                    pph.award_time = history.award_time;
                    pph.award_user_avatar = history.award_user_avatar;
                    pph.product_item_status = history.product_item_status;
                    pph.remain_units = history.remain_units;
                    if(history.product_item_status==HttpProtocol.PRODUCT_STATE.AWARD
                            &&history.award_uid.equals(PrefUtils.getString(PrefConstants.UserInfo.UID, ""))){
                        AwardingManager.getInstance(mContext).showAwardedDialog(mContext,history.product_id,history.product_name,history.default_image,history.product_item_id,history.period);
                    }
                    notifyDataSetChanged();
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

    private int findPosition(PojoParticpateHistory particpateHistory){
        for(int i=0;i<mPojoCategoryList.size();i++){
            PojoParticpateHistory history = mPojoCategoryList.get(i);
            if(history.product_item_id.equals(particpateHistory.product_item_id)){
                return i;
            }
        }
        return -1;
    }

}
