package dotc.android.happybuy.modules.detail.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.result.PojoPartRecorder;
import dotc.android.happybuy.modules.userprofile.UserProfileActivity;
import dotc.android.happybuy.ui.activity.WebActivity;

/**
 * Created by wangjun on 16/3/29.
 */
public class GoodsDetailAdapter extends BaseAdapter {

    private final String TAG = this.getClass().getSimpleName();
    private List<PojoPartRecorder> mPojoPartRecorderList;
    private Context mContext;
    private LayoutInflater mInflater;

    public GoodsDetailAdapter(Context context){
        mPojoPartRecorderList = new ArrayList<>();
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void updateList(List<PojoPartRecorder> pojoPartRecorders){
        mPojoPartRecorderList.clear();
        mPojoPartRecorderList.addAll(pojoPartRecorders);
        notifyDataSetChanged();
    }

    public void appendList(List<PojoPartRecorder> pojoPartRecorders){
        mPojoPartRecorderList.addAll(pojoPartRecorders);
        notifyDataSetChanged();
    }

    public void clearList(){
        mPojoPartRecorderList.clear();
        notifyDataSetChanged();
    }

    public PojoPartRecorder getLastItem(){
        return mPojoPartRecorderList.get(mPojoPartRecorderList.size()-1);
    }

    @Override
    public int getCount() {
        return mPojoPartRecorderList.size();
    }

    @Override
    public PojoPartRecorder getItem(int i) {
        return mPojoPartRecorderList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_part_recorder, viewGroup, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final PojoPartRecorder recorder = mPojoPartRecorderList.get(position);
        holder.nicknameTextView.setText(recorder.nickname);
        holder.localTextView.setText(recorder.area+ " " +recorder.userIp);
        holder.dateTextView.setText(recorder.createTimeShow);//DateUtil.time2sss(recorder.createTime*1000)
        holder.timesTextView.setText(recorder.totalTimes+"");
        Glide.with(mContext).load(recorder.photoUrl).placeholder(R.drawable.pic_circle_portrait_placeholder).crossFade().into(holder.portraitImageView);
        holder.portraitImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.EXTRA_UID, recorder.userId);
                intent.putExtra(UserProfileActivity.EXTRA_NICKNAME, recorder.nickname);
                mContext.startActivity(intent);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Click_Buy_Details_From_History,null,null);
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra(WebActivity.EXTRA_TITLE, mContext.getResources().getString(R.string.part_detail));
                intent.putExtra(WebActivity.EXTRA_URL, H5URL.get(recorder.participateDetailUrl));
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    static class Holder {
        public ImageView portraitImageView;
        public TextView nicknameTextView;
        public TextView localTextView;
        public TextView dateTextView;
        public TextView timesTextView;

        Holder(View view) {
            portraitImageView= (ImageView) view.findViewById(R.id.image_portrait);
            nicknameTextView = (TextView) view.findViewById(R.id.textview_name);
            localTextView = (TextView) view.findViewById(R.id.textview_local);
            dateTextView = (TextView) view.findViewById(R.id.textview_date);
            timesTextView = (TextView) view.findViewById(R.id.textview_times);
        }
    }

}
