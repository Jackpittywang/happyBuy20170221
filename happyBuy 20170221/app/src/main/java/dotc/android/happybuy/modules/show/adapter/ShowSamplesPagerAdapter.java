package dotc.android.happybuy.modules.show.adapter;

import android.annotation.SuppressLint;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.show.ShowSampleActivity;
import dotc.android.happybuy.uibase.widget.TouchImageView;

/**
 * Created by LiShen on 16/12/29.
 */
public class ShowSamplesPagerAdapter extends PagerAdapter {
    private ShowSampleActivity activity;
    private String[] images;
    private List<View> views = new ArrayList<View>();

    @SuppressLint("InflateParams")
    public ShowSamplesPagerAdapter(ShowSampleActivity activity, String[] images) {
        this.activity = activity;
        this.images = images;
        View view;
        for (String s : this.images) {
            view = activity.getLayoutInflater().inflate(R.layout.item_pager_show_view_images, null);
            views.add(view);
        }
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = views.get(position);
        TouchImageView tivItemShowViewImages = (TouchImageView) convertView.findViewById(R.id.tivItemShowViewImages);
        final ProgressBar pbItemShowViewImages = (ProgressBar) convertView.findViewById(R.id.pbItemShowViewImages);
        tivItemShowViewImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        Glide.with(activity)
                .load(images[position])
                .placeholder(R.color.black)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_pic_default)
                .into(new GlideDrawableImageViewTarget(tivItemShowViewImages) {
                    @Override
                    public void onStart() {
                        super.onStart();
                        pbItemShowViewImages.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        pbItemShowViewImages.setVisibility(View.GONE);
                    }
                });
        container.addView(convertView);
        return convertView;
    }
}
