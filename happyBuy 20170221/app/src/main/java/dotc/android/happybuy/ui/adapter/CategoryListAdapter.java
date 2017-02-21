package dotc.android.happybuy.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoCategory;
import dotc.android.happybuy.http.result.PojoProduct;

/**
 * Created by huangli on 16/5/12.
 */
public class CategoryListAdapter extends BaseAdapter{

    private Context mContext;
    private List<PojoCategory> mlist;
    private int mHaveSelect=0;

    private class ViewHolder{
        RelativeLayout relativeLayout;
        ImageView imageView;
        TextView textView;
    }

    public CategoryListAdapter(Context context){
        mContext = context;
        mlist = new ArrayList<>();
    }

    public List<PojoCategory> getPojoCategoryList(){
        return mlist;
    }

    public void setPojoCategoryList(List<PojoCategory> list){
        mlist.clear();
        mlist.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mlist != null){
            return mlist.size();
        }else{
            return 0;
        }
    }

    public void updateSelect(int position){
//        if(mHaveSelect==position){
//            mHaveSelect=-1;
//        }else {
//            mHaveSelect=position;
//        }
        mHaveSelect=position;
        notifyDataSetChanged();
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
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.item_list_category, parent, false);
            viewHolder.relativeLayout= (RelativeLayout) convertView.findViewById(R.id.rl_content);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_category);
            viewHolder.textView = (TextView)convertView.findViewById(R.id.tv_text);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(position == mHaveSelect){
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.bg_classification);
        }else {
            viewHolder.relativeLayout.setBackgroundColor(0xFFEEEEEE);
        }

        viewHolder.textView.setText(mlist.get(position).name);
        Glide.with(mContext)
                .load(mlist.get(position).icon).fitCenter().placeholder(R.drawable.category_nol)
                .into(viewHolder.imageView);
        return convertView;
    }
}
