package dotc.android.happybuy.modules.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.modules.main.base.BaseMainFragment;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.uibase.widget.ColorProgressBar;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.DisplayUtils;

public class UserTipsFragment extends BaseMainFragment implements View.OnClickListener {
    public static  String START_XPOINT="start_xpoint";
    public static  String START_YPOINT="start_ypoint";
    public static  String PRODUCT="product";
    private int xPoint;
    private int yPoint;
    private PojoProduct pojoProduct;

    private LinearLayout mProduct;
    private RelativeLayout mLayout;

    public ImageView iconImageView;
    public ImageView tenFlagImageView;
    public TextView nameTextView;
    public ColorProgressBar progressBar;
    public TextView totalTimesTextView;
    public TextView remainTimesTextView;
    public View partTextView;

    public static UserTipsFragment newInstance(int x, int y, PojoProduct pojoProduct) {
        UserTipsFragment fragment = new UserTipsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(START_XPOINT,x);
        bundle.putInt(START_YPOINT,y);
        bundle.putSerializable(PRODUCT,pojoProduct);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xPoint = getArguments().getInt(START_XPOINT);
        yPoint = getArguments().getInt(START_YPOINT);
        pojoProduct= (PojoProduct) getArguments().getSerializable(PRODUCT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_tips, container, false);
        mLayout = (RelativeLayout) view.findViewById(R.id.layout);
        mProduct = (LinearLayout) view.findViewById(R.id.layout_section);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0,yPoint-AppUtil.dp2px(getContext(),60)-75,0,0);
        layoutParams.topMargin = yPoint- DisplayUtils.getStatusBarHeight(getContext())-AppUtil.dp2px(getContext(),60);
        mProduct.setLayoutParams(layoutParams);
        iconImageView= (ImageView) view.findViewById(R.id.imageview_icon);
        tenFlagImageView = (ImageView) view.findViewById(R.id.imageview_ten_flag);
        nameTextView = (TextView) view.findViewById(R.id.textview_name);
        progressBar = (ColorProgressBar) view.findViewById(R.id.progressbar);
        totalTimesTextView = (TextView) view.findViewById(R.id.textview_total_times);
        remainTimesTextView = (TextView) view.findViewById(R.id.textview_retain_times);
        partTextView = view.findViewById(R.id.textview_part);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nameTextView.setText(pojoProduct.productName);
        progressBar.setProgress(pojoProduct.totalTimes - pojoProduct.remainTimes, pojoProduct.totalTimes);
        totalTimesTextView.setText(getContext().getString(R.string.times_total)+" "+pojoProduct.totalTimes + "");
        remainTimesTextView.setText(pojoProduct.remainTimes + "");
        if(pojoProduct.coins_unit == 10){
            String country=AppUtil.getMetaData(getContext(),"country");
            if(country.equals("th")){
                tenFlagImageView.setImageResource(R.drawable.ic_ten_flag);
            }else if(country.equals("vn")){
                tenFlagImageView.setImageResource(R.drawable.ic_ten_flagyunan);
            }
            tenFlagImageView.setVisibility(View.VISIBLE);
        } else if(pojoProduct.coins_unit== 100){
            String country= AppUtil.getMetaData(getContext(),"country");
            if(country.equals("th")){
                tenFlagImageView.setImageResource(R.drawable.ic_hundred_flag);
            }else if(country.equals("vn")){
                tenFlagImageView.setImageResource(R.drawable.ic_hundred_flag);
            }
            tenFlagImageView.setVisibility(View.VISIBLE);
        }
        else {
            tenFlagImageView.setVisibility(View.INVISIBLE);
        }
        Glide.with(getContext())
                .load(pojoProduct.productUrl)
                .placeholder(R.drawable.image_default_selector)
                .crossFade()
                .into(iconImageView);

        mProduct.setTag(pojoProduct);
        mProduct.setOnClickListener(this);
        mLayout.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_section:
                Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Click_Home_Guide, null, null);
                PojoProduct pojoProduct = (PojoProduct) v.getTag();
                Intent intent = new Intent(getContext(), GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,pojoProduct.productId);
                intent.putExtra(GoodsDetailActivity.USER_GUIDE,true);
                startActivity(intent);
                getFragmentManager().beginTransaction().hide(UserTipsFragment.this).commitAllowingStateLoss();
                break;
            case R.id.layout:
                Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Click_Home_Gray, null, null);
                break;

        }

    }



}
