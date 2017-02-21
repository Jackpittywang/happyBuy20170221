package dotc.android.happybuy.modules.classification.adapter;

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


public class ProductClassificationAdapter extends BaseAdapter implements View.OnClickListener {

        private final String TAG = this.getClass().getSimpleName();
        private List<PojoProduct> mPojoProductList;
        //    private ProductsFragment mProductsFragment;
        private Context mContext;
        private LayoutInflater mInflater;
        private OnProductPartListener mOnProductPartListener;
        private String mType;

        public ProductClassificationAdapter(Context context,String type,OnProductPartListener productPartListener){
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
            return mPojoProductList.size();
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
                convertView = mInflater.inflate(R.layout.listitem_classification_goods, viewGroup, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            PojoProduct pojoProduct = mPojoProductList.get(position);
            holder.nameTextView.setText(pojoProduct.productName);
            holder.progressBar.setProgress(pojoProduct.totalTimes - pojoProduct.remainTimes, pojoProduct.totalTimes);
            holder.totalTimesTextView.setText(mContext.getString(R.string.times_total)+" "+pojoProduct.totalTimes + "");
            holder.remainTimesTextView.setText(pojoProduct.remainTimes + "");
            if(pojoProduct.coins_unit == 10){
                String country= AppUtil.getMetaData(mContext,"country");
                if(country.equals("th")){
                    holder.tenFlagImageView.setImageResource(R.drawable.ic_ten_flag);
                }else if(country.equals("vn")){
                    holder.tenFlagImageView.setImageResource(R.drawable.ic_ten_flagyunan);
                }

                holder.tenFlagImageView.setVisibility(View.VISIBLE);
            } else if(pojoProduct.coins_unit== 100){
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
                    .placeholder(R.drawable.ic_classification_product_default)
                    .crossFade()
                    .into(holder.iconImageView);
            holder.partTextView.setTag(pojoProduct);
            holder.partTextView.setOnClickListener(mPartInListener);
            holder.layoutSection.setTag(pojoProduct);
            holder.layoutSection.setOnClickListener(this);

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
            intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,pojoProduct.productId);
            intent.putExtra(GoodsDetailActivity.EXTRA_ACTIVITY_FROM,GoodsDetailActivity.ACTIVITY_FROM_CLASSIFICATION);
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


            Holder(View view) {
                layoutSection = view.findViewById(R.id.layout_section);
                iconImageView= (ImageView) view.findViewById(R.id.imageview_icon);
                tenFlagImageView = (ImageView) view.findViewById(R.id.imageview_ten_flag);
                nameTextView = (TextView) view.findViewById(R.id.textview_name);
                progressBar = (ColorProgressBar) view.findViewById(R.id.progressbar);
                totalTimesTextView = (TextView) view.findViewById(R.id.textview_total_times);
                remainTimesTextView = (TextView) view.findViewById(R.id.textview_retain_times);
                partTextView = view.findViewById(R.id.textview_part);

            }
        }

        public interface OnProductPartListener {
            void onProductPartClick(View view,PojoProduct product);
        }
    }
