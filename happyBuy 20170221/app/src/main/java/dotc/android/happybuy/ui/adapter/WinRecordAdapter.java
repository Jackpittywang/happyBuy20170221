package dotc.android.happybuy.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoCategory;
import dotc.android.happybuy.http.result.PojoParticpateHistory;


/**
 * Created by wangjun on 16/2/1.
 */
public class WinRecordAdapter extends BaseAdapter {

    private final String TAG = this.getClass().getSimpleName();
    private List<PojoParticpateHistory> mPojoCategoryList;
    private Context mContext;
    private LayoutInflater mInflater;
//    private AppIconLoader mAppIconLoader;

    public WinRecordAdapter(Context context, List<PojoParticpateHistory> pojoCategoryList){
        mPojoCategoryList = pojoCategoryList;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void updateList(List<PojoParticpateHistory> userInfos){
        mPojoCategoryList = userInfos;
        notifyDataSetChanged();
    }

    public void appendList(List<PojoParticpateHistory> pojoCategoryList){
        mPojoCategoryList.addAll(pojoCategoryList);
        notifyDataSetChanged();
    }

    public int getCount() {
        if (mPojoCategoryList != null) {
            return mPojoCategoryList.size();
        }
        return 0;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_win_record, viewGroup, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final PojoParticpateHistory history = mPojoCategoryList.get(position);

        holder.titleTextView.setText(history.product_name);
        holder.periodView.setText(history.period+"");
        holder.winnerextView.setText(mContext.getString(R.string.lable_winner,history.award_nickname));
        holder.timesTextView.setText(mContext.getString(R.string.lable_period_times,history.buy_item_count));
        holder.numberTextView.setText(mContext.getString(R.string.lable_win_number, history.award_code));
        if (history.is_shareover.equals("0")){
            holder.isshareTextView.setText(R.string.share_tips_notyet);
        }else {
            holder.isshareTextView.setText(R.string.share_tips_already);
        }
        Glide.with(mContext).load(history.default_image).placeholder(R.drawable.ic_me_product_default).crossFade().into(holder.imageView);
        return convertView;
    }

    static class Holder {
        ImageView imageView;
        TextView titleTextView;
        TextView winnerextView;
        TextView timesTextView;
        TextView numberTextView;
        TextView periodView;
        TextView isshareTextView;
        public Holder(View view){
            imageView = (ImageView) view.findViewById(R.id.imageview);
            titleTextView = (TextView) view.findViewById(R.id.textview_title);
            winnerextView = (TextView) view.findViewById(R.id.textview_winner);
            timesTextView = (TextView) view.findViewById(R.id.textview_times);
            numberTextView = (TextView) view.findViewById(R.id.textview_number);
            periodView = (TextView) view.findViewById(R.id.textview_period);
            isshareTextView = (TextView)view.findViewById(R.id.tvisshared);
            isshareTextView.setVisibility(View.VISIBLE);
        }
    }

}
