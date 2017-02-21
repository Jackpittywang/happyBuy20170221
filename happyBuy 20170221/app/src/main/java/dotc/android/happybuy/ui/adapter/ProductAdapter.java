package dotc.android.happybuy.ui.adapter;

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
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.home.fragment.ProductsFragment;
import dotc.android.happybuy.uibase.widget.ColorProgressBar;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by huangli on 16/3/29.
 */
public class ProductAdapter extends BaseAdapter implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();
    private List<PojoProduct> mPojoProductList;
//    private ProductsFragment mProductsFragment;
    private Context mContext;
    private LayoutInflater mInflater;
    private OnProductPartListener mOnProductPartListener;
    private String mType;

    public ProductAdapter(Context context,String type,OnProductPartListener productPartListener){
//        this.mProductsFragment = fragment;
        mType = type;
        mPojoProductList = new ArrayList<>();
        mContext = context;
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

    public PojoProduct getItemById(String productId){
        for(int i=0;i<mPojoProductList.size();i++){
            if(productId.equals(mPojoProductList.get(i).productId)){
                return mPojoProductList.get(i);
            }
        }
        return null;
    }

    public void clearList(){
        mPojoProductList.clear();
        notifyDataSetChanged();
    }

    public int getListCount(){
        return mPojoProductList.size();
    }

    public PojoProduct getLastItem(){
        return mPojoProductList.get(mPojoProductList.size() - 1);
    }

    @Override
    public int getCount() {
        return mPojoProductList.size()/2+ mPojoProductList.size()%2;
    }

    @Override
    public PojoProduct getItem(int i) {
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
        holder.progressBar.setProgress(pojoProduct.totalTimes - pojoProduct.remainTimes, pojoProduct.totalTimes);
        holder.totalTimesTextView.setText(mContext.getString(R.string.times_total)+" "+pojoProduct.totalTimes + "");
        holder.remainTimesTextView.setText(pojoProduct.remainTimes + "");
        if(pojoProduct.coins_unit== 10){
            String country= AppUtil.getMetaData(mContext,"country");
            if(country.equals("th")){
                holder.tenFlagImageView.setImageResource(R.drawable.ic_ten_flag);
            }else if(country.equals("vn")){
                holder.tenFlagImageView.setImageResource(R.drawable.ic_ten_flagyunan);
            }
            holder.tenFlagImageView.setVisibility(View.VISIBLE);
        }else if(pojoProduct.coins_unit== 100){
            String country= AppUtil.getMetaData(mContext,"country");
            if(country.equals("th")){
                holder.tenFlagImageView.setImageResource(R.drawable.ic_hundred_flag);
            }else if(country.equals("vn")){
                holder.tenFlagImageView.setImageResource(R.drawable.ic_hundred_flag);
            }
            holder.tenFlagImageView.setVisibility(View.VISIBLE);
        }
        else {
            holder.tenFlagImageView.setVisibility(View.INVISIBLE);
        }

        Glide.with(mContext)
                .load(pojoProduct.productUrl)
                .placeholder(R.drawable.ic_product_default)
                .fitCenter().dontAnimate()
                .into(holder.iconImageView);
        holder.partTextView.setTag(pojoProduct);
        holder.partTextView.setOnClickListener(mPartInListener);
        holder.layoutSection.setTag(pojoProduct);
        holder.layoutSection.setOnClickListener(this);

        if(2*position+1< mPojoProductList.size()){
            PojoProduct pojoProduct1 = mPojoProductList.get(2*position+1);
            holder.layoutSection1.setVisibility(View.VISIBLE);
            holder.nameTextView1.setText(pojoProduct1.productName);
            holder.progressBar1.setProgress(pojoProduct1.totalTimes -pojoProduct1.remainTimes,pojoProduct1.totalTimes);
            holder.totalTimesTextView1.setText(mContext.getString(R.string.times_total)+" "+pojoProduct1.totalTimes + "");
            holder.remainTimesTextView1.setText(pojoProduct1.remainTimes + "");
            if(pojoProduct1.coins_unit == 10){
                String country= AppUtil.getMetaData(mContext,"country");
                if(country.equals("th")){
                    holder.tenFlagImageView1.setImageResource(R.drawable.ic_ten_flag);
                }else if(country.equals("vn")){
                    holder.tenFlagImageView1.setImageResource(R.drawable.ic_ten_flagyunan);
                }
                holder.tenFlagImageView1.setVisibility(View.VISIBLE);
            }else if(pojoProduct1.coins_unit == 100){
                String country= AppUtil.getMetaData(mContext,"country");
                if(country.equals("th")){
                    holder.tenFlagImageView1.setImageResource(R.drawable.ic_hundred_flag);
                }else if(country.equals("vn")){
                    holder.tenFlagImageView1.setImageResource(R.drawable.ic_hundred_flag);
                }
                holder.tenFlagImageView1.setVisibility(View.VISIBLE);
            }
            else {
                holder.tenFlagImageView1.setVisibility(View.INVISIBLE);
            }
            Glide.with(mContext)
                    .load(pojoProduct1.productUrl)
                    .placeholder(R.drawable.ic_product_default)
                    .fitCenter().dontAnimate()
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
        if (mType != null){
            if(mType.equals(ProductsFragment.TYPE_ALL+"")){
                Analytics.sendUIEvent(AnalyticsEvents.Tab.Click_Product_All,null,null);
            }else if (mType.equals(ProductsFragment.TYPE_TEN+"")){
                Analytics.sendUIEvent(AnalyticsEvents.Tab.Click_Product_Ten,null,null);
            }
        }
        PojoProduct pojoProduct = (PojoProduct) v.getTag();
        Intent intent = new Intent(mContext, GoodsDetailActivity.class);
        intent.putExtra(GoodsDetailActivity.EXTRA_ACTIVITY_FROM,GoodsDetailActivity.ACTIVITY_FROM_HOME+"_"+mType);
        intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,pojoProduct.productId);
        mContext.startActivity(intent);
    }

    private View.OnClickListener mPartInListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (mType != null){
                if(mType.equals(ProductsFragment.TYPE_ALL+"")){
                    Analytics.sendUIEvent(AnalyticsEvents.Tab.Click_BuyNow_All,null,null);
                }else if (mType.equals(ProductsFragment.TYPE_TEN+"")){
                    Analytics.sendUIEvent(AnalyticsEvents.Tab.Click_BuyNow_Ten,null,null);
                }
            }
            PojoProduct pojoProduct = (PojoProduct) v.getTag();
            if(mOnProductPartListener != null){
                mOnProductPartListener.onProductPartClick(v,pojoProduct);
            }
//            mProductsFragment.onPartInView(v, pojoProduct);
        }
    };

    static class Holder {
        public View layoutSection;
        public ImageView iconImageView;
        public ImageView tenFlagImageView;
        public TextView nameTextView;
        public ColorProgressBar progressBar;
        public TextView totalTimesTextView;
        public TextView remainTimesTextView;
        public View partTextView;

        public View layoutSection1;
        public ImageView iconImageView1;
        public ImageView tenFlagImageView1;
        public TextView nameTextView1;
        public ColorProgressBar progressBar1;
        public TextView totalTimesTextView1;
        public TextView remainTimesTextView1;
        public View partTextView1;

        Holder(View view) {
            layoutSection = view.findViewById(R.id.layout_section);
            iconImageView= (ImageView) view.findViewById(R.id.imageview_icon);
            tenFlagImageView = (ImageView) view.findViewById(R.id.imageview_ten_flag);
            nameTextView = (TextView) view.findViewById(R.id.textview_name);
            progressBar = (ColorProgressBar) view.findViewById(R.id.progressbar);
            totalTimesTextView = (TextView) view.findViewById(R.id.textview_total_times);
            remainTimesTextView = (TextView) view.findViewById(R.id.textview_retain_times);
            partTextView = view.findViewById(R.id.textview_part);

            layoutSection1 = view.findViewById(R.id.layout_section_1);
            iconImageView1= (ImageView) view.findViewById(R.id.imageview_icon_1);
            tenFlagImageView1 = (ImageView) view.findViewById(R.id.imageview_ten_flag_1);
            nameTextView1 = (TextView) view.findViewById(R.id.textview_name_1);
            progressBar1 = (ColorProgressBar) view.findViewById(R.id.progressbar_1);
            totalTimesTextView1 = (TextView) view.findViewById(R.id.textview_total_times_1);
            remainTimesTextView1 = (TextView) view.findViewById(R.id.textview_retain_times_1);
            partTextView1 = view.findViewById(R.id.textview_part_1);
        }
    }

    public interface OnProductPartListener {
        void onProductPartClick(View view,PojoProduct product);
    }
}
