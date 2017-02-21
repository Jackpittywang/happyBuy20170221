package dotc.android.happybuy.modules.home.adapter;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.result.PojoAd;
import dotc.android.happybuy.modules.schema.SchemaActivity;
import dotc.android.happybuy.modules.schema.SchemeProcessor;
import dotc.android.happybuy.ui.activity.WebActivity;

/**
 * Created by wangjun on 16/8/22.
 */
public class BannerAdapter extends PagerAdapter {

    private List<PojoAd> mAdList;
    private List<WeakReference<ImageView>> mImages;

    public BannerAdapter() {
        mAdList = new ArrayList<>();
        mImages = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mAdList.size();

    }

    public void setData(List<PojoAd> data) {
        mAdList.clear();
        mAdList.addAll(data);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView = null;
        if (!mImages.isEmpty()) {
            ArrayList<WeakReference<ImageView>> usedReference = new ArrayList<>();
            for (WeakReference<ImageView> imageViewWeakReference : mImages) {
                ImageView img = imageViewWeakReference.get();
                if (img != null) {
                    imageView = img;
                    usedReference.add(imageViewWeakReference);
                    break;
                } else {
                    usedReference.add(imageViewWeakReference);
                }
            }
            mImages.removeAll(usedReference);
        }

        if (imageView == null) {
            imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

//        imageView.setImageBitmap(null);

        Glide.with(imageView.getContext()).load(mAdList.get(position).picture).fitCenter().into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.HomeFragment.Click_Banner_Main,position+1+"",null);
                String clickUrl = mAdList.get(position).clickUrl;
                SchemeProcessor.handle(v.getContext(),clickUrl);
            }
        });

        container.addView(imageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mImages.add(new WeakReference<>((ImageView) object));
    }
}
