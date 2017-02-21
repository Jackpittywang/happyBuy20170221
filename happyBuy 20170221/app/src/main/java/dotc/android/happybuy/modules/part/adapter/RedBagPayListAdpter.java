package dotc.android.happybuy.modules.part.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoCoupons;
import dotc.android.happybuy.util.DateUtil;

/**
 * Created by wangzhiyuan on 2016/12/13.
 */
public class RedBagPayListAdpter extends BaseAdapter {
    private Context mContext;
    private List<PojoCoupons> mlist = new ArrayList<>();
    Set<Integer> checkPostions;
    private int mHaveSelect=-1;
    public static final String TAG = "RedBagPayListAdpter";

    private class ViewHolder {
        private ImageView redBagImageView;
        private TextView nameTextView;
        private TextView avaliableTimeTextView;
        private TextView redTypeTextView;
        private TextView usageRule;
        private ImageView checkBox;
    }

    public RedBagPayListAdpter(Context context) {
        mContext = context;
    }

    public void clearData() {
        mlist.clear();
    }

    public void addData(List<PojoCoupons> list) {
        for (PojoCoupons item : list) {
            mlist.add(item);
        }
    }
    public void updateSelect(int position){
        if(mHaveSelect==position){
            mHaveSelect=-1;
        }else {
            mHaveSelect=position;
        }
        notifyDataSetChanged();
    }

  /*  public void setPosition(int position){
        if(checkPostions.contains(position)){
            checkPostions.remove(position);
        } else {
            checkPostions.add(position);
        }
        notifyDataSetChanged();
    }*/

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public PojoCoupons getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final PojoCoupons pojoCoupons = mlist.get(position);
        // 如果缓存convertView为空，则需要创建View
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_coupons_list_in_pay, null);
            holder.checkBox = (ImageView) convertView.findViewById(R.id.checkbox);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.usageRule = (TextView) convertView.findViewById(R.id.tv_usage_rule);
            holder.avaliableTimeTextView = (TextView) convertView.findViewById(R.id.tv_avaliable_time);
            holder.redTypeTextView = (TextView) convertView.findViewById(R.id.tv_red_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.nameTextView.setText(pojoCoupons.amount);
        holder.redTypeTextView.setText(pojoCoupons.range_brief);
        holder.usageRule.setText("("+mContext.getString(R.string.use_min_amount,pojoCoupons.use_min_amount)+")");
        String startTime=DateUtil.time2date(Long.valueOf(pojoCoupons.begin_time)*1000);
        String endTime=DateUtil.time2date(Long.valueOf(pojoCoupons.end_time)*1000);
//        long days=DateUtil.time2interval(System.currentTimeMillis(),Long.valueOf(pojoCoupons.end_time)*1000);
//        if(days>=0){
//            String day=String.valueOf(days);
            holder.avaliableTimeTextView.setText(startTime+"-"+endTime);
//        }

        if(position == mHaveSelect){
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        return convertView;
    }




}
