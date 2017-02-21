package dotc.android.happybuy.modules.coupon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoCoupons;
import dotc.android.happybuy.util.DateUtil;

/**
 * Created by wangzhiyuan on 2016/7/19.
 */
public class ReadyToDistributeRedBagListAdpter extends BaseAdapter {
    private Context mContext;
    private List<PojoCoupons> mlist = new ArrayList<>();
    public static final String TAG = "AvailableRedBagListAdpter";

    private class ViewHolder {
        private ImageView redBagImageView;
        private TextView nameTextView;
        private TextView timeTextView;
        private TextView avaliableTimeTextView;
        private TextView redTypeTextView;
        private TextView sceneTextView;
        private TextView usageRule;
    }

    public ReadyToDistributeRedBagListAdpter(Context context) {
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

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_red_bag, null);
            holder.redBagImageView = (ImageView) convertView.findViewById(R.id.iv_red_bag);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.tv_name);
            holder.timeTextView = (TextView) convertView.findViewById(R.id.tv_time);
            holder.avaliableTimeTextView = (TextView) convertView.findViewById(R.id.tv_avaliable_time);
            holder.redTypeTextView = (TextView) convertView.findViewById(R.id.tv_red_type);
            holder.sceneTextView = (TextView) convertView.findViewById(R.id.tv_scene);
            holder.usageRule=(TextView) convertView.findViewById(R.id.tv_usage_rule);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //派发时间
        holder.nameTextView.setText(R.string.delivery_time);
        holder.timeTextView.setText(DateUtil.time2date(pojoCoupons.begin_time * 1000));
        holder.avaliableTimeTextView.setVisibility(View.GONE);
        holder.usageRule.setText(pojoCoupons.use_min_amount+"-"+pojoCoupons.amount+"");
        holder.redTypeTextView.setText(pojoCoupons.desc);
        holder.sceneTextView.setText(pojoCoupons.range_brief);
//        holder.usageRule.setText(pojoCoupons.gain_rule_min_amount+"-"+pojoCoupons.amount+"");


        /*Glide.with(mContext)
                .load(mlist.get(position).user_info.avatar).fitCenter()
                .into(holder.redBagImageView);*/
        return convertView;
    }


}
