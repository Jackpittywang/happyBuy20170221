package dotc.android.happybuy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.modules.main.base.BaseMainFragment;
import dotc.android.happybuy.modules.part.PartCallBack;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.modules.part.PartObject;

public class UserTipsGoodsDetailFragment extends BaseMainFragment implements PartCallBack {
    public static  String START_XPOINT="start_xpoint";
    public static  String START_YPOINT="start_ypoint";
    public static  String PRODUCT="product";
    private int xPoint;
    private int yPoint;
    private PojoProduct pojoProduct;

    private RelativeLayout mProduct;
    private ImageView mGifImage;

    private PartFragment mPartFragment;

    public static UserTipsGoodsDetailFragment newInstance(int x, int y,PojoProduct product) {
        UserTipsGoodsDetailFragment fragment = new UserTipsGoodsDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(START_XPOINT,x);
        bundle.putInt(START_YPOINT,y);
        bundle.putSerializable(PRODUCT,product);
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
        View view = inflater.inflate(R.layout.fragment_goods_detail_tips, container, false);
        mProduct = (RelativeLayout) view.findViewById(R.id.layout_section);
        mGifImage= (ImageView) view.findViewById(R.id.iv_gif);
       /* LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(xPoint,yPoint- PrefUtils.getInt(MainTabActivity.STATUSBARHEIGHT,0),0,0);
        mProduct.setLayoutParams(layoutParams);*/
        mProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(UserTipsGoodsDetailFragment.this).commitAllowingStateLoss();
                Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Click_Detail_Guide, null, null);
                mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_USER_TIPS, pojoProduct, UserTipsGoodsDetailFragment.this);
                mPartFragment.show(getFragmentManager());
            }
        });
        view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Click_Detail_Gray, null, null);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(this).load(R.drawable.bg_buttom_gif).asGif().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mGifImage);
    }


    @Override
    public void onPartCallBack(boolean paySuceess,PartObject partObject) {

    }
}
