package dotc.android.happybuy.modules.home.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.bean.HomeActive;
import dotc.android.happybuy.modules.schema.SchemaActivity;
import dotc.android.happybuy.modules.schema.SchemeProcessor;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/12/13.
 */

public class PortalBannerContainer extends LinearLayout {

    private List<HomeActive.PortalBanner> mBannerList;

    public PortalBannerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        setOrientation(VERTICAL);
    }

    public void setItem(List<HomeActive.PortalBanner> bannerList, int itemWidth, int itemHeight){
        this.mBannerList = bannerList;
        removeAllViews();
        int columnCount = 2;
        for(int m=0;m<bannerList.size()/columnCount;m++){
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(HORIZONTAL);

            for(int n=m*columnCount;n<(m+1)*columnCount;n++){
                FitWidthImageView imageView = createItemView(itemWidth*1.0f/itemHeight);
                bindView(imageView,bannerList.get(n), n);
                layout.addView(imageView,getImageViewLP());
            }
            addView(layout,getRowLayoutLP());
        }
    }

    private FitWidthImageView createItemView(float aspectRatio){
        FitWidthImageView imageView = new FitWidthImageView(getContext());
        imageView.setAspectRatio(aspectRatio);
        return imageView;
    }

    private void bindView(FitWidthImageView imageView, final HomeActive.PortalBanner banner,final int i){
        Glide.with(getContext())
                .load(banner.pic_url)
                .placeholder(R.drawable.ic_portalbanner_default)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.HomeFragment.Click_Banner_Small,i+1+"",null);
                SchemeProcessor.handle(getContext(),banner.click_url);
            }
        });
    }

    private LayoutParams getImageViewLP(){
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT,1);
        layoutParams.leftMargin = AppUtil.dp2px(getContext(),2);
        layoutParams.rightMargin = AppUtil.dp2px(getContext(),2);
        return layoutParams;
    }

    private LayoutParams getRowLayoutLP(){
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = AppUtil.dp2px(getContext(),2);
        layoutParams.bottomMargin = AppUtil.dp2px(getContext(),2);
        return layoutParams;
    }

}
