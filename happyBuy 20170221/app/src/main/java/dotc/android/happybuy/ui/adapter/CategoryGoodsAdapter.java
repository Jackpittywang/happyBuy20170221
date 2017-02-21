package dotc.android.happybuy.ui.adapter;

import android.app.Activity;
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
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.uibase.widget.ColorProgressBar;

/**
 * Created by wangjun on 16/3/29.
 */
public class CategoryGoodsAdapter extends BaseAdapter implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private List<PojoProduct> mPojoProductList;
    private Activity mContext;
    private LayoutInflater mInflater;
    private OnProductPartListener mOnProductPartListener;

    public CategoryGoodsAdapter(Activity activity,OnProductPartListener productPartListener ){
        mPojoProductList = new ArrayList<>();
        mContext = activity;
        mInflater = LayoutInflater.from(mContext);
        this.mOnProductPartListener = productPartListener;
    }

    public void updateList(List<PojoProduct> pojoProductList){
        mPojoProductList.clear();
        mPojoProductList.addAll(pojoProductList);
        notifyDataSetChanged();
    }

    public void appendList(List<PojoProduct> pojoProductList){
        mPojoProductList.addAll(pojoProductList);
        notifyDataSetChanged();
    }

    public void clearList(){
        mPojoProductList.clear();
        notifyDataSetChanged();
    }

    public int getListCount(){
        return mPojoProductList.size();
    }

    public PojoProduct getLastItem(){
        return mPojoProductList.get(mPojoProductList.size()-1);
    }

    @Override
    public int getCount() {
        return mPojoProductList.size()/2+ mPojoProductList.size()%2;
    }

    @Override
    public Object getItem(int i) {
        return mPojoProductList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_category_goods, viewGroup, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        PojoProduct pojoProduct = mPojoProductList.get(2*position);
        holder.nameTextView.setText(pojoProduct.productName);
        holder.progressBar.setProgress(pojoProduct.totalTimes -pojoProduct.remainTimes,pojoProduct.totalTimes);
        holder.totalTimesTextView.setText(pojoProduct.totalTimes +"");
        holder.remainTimesTextView.setText(pojoProduct.remainTimes + "");
        Glide.with(mContext)
                .load(pojoProduct.productUrl)
                .fitCenter()
                .crossFade()
                .into(holder.iconImageView);
        holder.partTextView.setTag(pojoProduct);
        holder.partTextView.setOnClickListener(mPartInListener);
        holder.layoutSection.setTag(pojoProduct);
        holder.layoutSection.setOnClickListener(this);

        if(2*position+1< mPojoProductList.size()){
            PojoProduct pojoProduct1 = mPojoProductList.get(2*position + 1);
            holder.layoutSection1.setVisibility(View.VISIBLE);
            holder.nameTextView1.setText(pojoProduct1.productName);
            holder.progressBar1.setProgress(pojoProduct1.totalTimes -pojoProduct1.remainTimes,pojoProduct1.totalTimes);
            holder.totalTimesTextView1.setText(pojoProduct1.totalTimes + "");
            holder.remainTimesTextView1.setText(pojoProduct1.remainTimes + "");
            Glide.with(mContext)
                    .load(pojoProduct1.productUrl)
                    .fitCenter()
                    .crossFade()
                    .into(holder.iconImageView1);
            holder.partTextView1.setTag(pojoProduct1);
            holder.partTextView1.setOnClickListener(mPartInListener);
            holder.layoutSection1.setTag(pojoProduct1);
            holder.layoutSection1.setOnClickListener(this);
        } else {
            holder.layoutSection1.setVisibility(View.INVISIBLE);
            holder.layoutSection1.setOnClickListener(null);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        PojoProduct pojoProduct = (PojoProduct) v.getTag();
        Intent intent = new Intent(mContext, GoodsDetailActivity.class);
        intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,pojoProduct.productId);
        mContext.startActivity(intent);
    }

    private View.OnClickListener mPartInListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            PojoProduct pojoProduct = (PojoProduct) v.getTag();
            if(mOnProductPartListener != null){
                mOnProductPartListener.onProductPartClick(v,pojoProduct);
            }
//            mContext.onPartInView(v, pojoProduct);
        }
    };

    public interface OnProductPartListener {
        void onProductPartClick(View view,PojoProduct product);
    }

    static class Holder {
        public View layoutSection;
        public ImageView iconImageView;
        public TextView nameTextView;
        public ColorProgressBar progressBar;
        public TextView totalTimesTextView;
        public TextView remainTimesTextView;
        public View partTextView;

        public View layoutSection1;
        public ImageView iconImageView1;
        public TextView nameTextView1;
        public ColorProgressBar progressBar1;
        public TextView totalTimesTextView1;
        public TextView remainTimesTextView1;
        public View partTextView1;

        Holder(View view) {
            layoutSection = view.findViewById(R.id.layout_section);
            iconImageView= (ImageView) view.findViewById(R.id.imageview_icon);
            nameTextView = (TextView) view.findViewById(R.id.textview_name);
            progressBar = (ColorProgressBar) view.findViewById(R.id.progressbar);
            totalTimesTextView = (TextView) view.findViewById(R.id.textview_total_times);
            remainTimesTextView = (TextView) view.findViewById(R.id.textview_retain_times);
            partTextView = view.findViewById(R.id.textview_part);

            layoutSection1 = view.findViewById(R.id.layout_section_1);
            iconImageView1= (ImageView) view.findViewById(R.id.imageview_icon_1);
            nameTextView1 = (TextView) view.findViewById(R.id.textview_name_1);
            progressBar1 = (ColorProgressBar) view.findViewById(R.id.progressbar_1);
            totalTimesTextView1 = (TextView) view.findViewById(R.id.textview_total_times_1);
            remainTimesTextView1 = (TextView) view.findViewById(R.id.textview_retain_times_1);
            partTextView1 = view.findViewById(R.id.textview_part_1);
        }
    }

}
