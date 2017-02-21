package dotc.android.happybuy.modules.show.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.show.func.PhotoChooser;

public class ViewChoosePhotosAdapter extends PagerAdapter {
    private Activity activity;
    private List<View> views = new ArrayList<View>();

    @SuppressLint("InflateParams")
    public ViewChoosePhotosAdapter(Activity activity) {
        this.activity = activity;
        View view;
        for (String s : PhotoChooser.Singleton.getSharePhotosChoose()) {
            view = activity.getLayoutInflater().inflate(R.layout.item_pager_view_share_photos, null);
            views.add(view);
        }
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = views.get(position);
        Glide.with(activity)
                .load(PhotoChooser.Singleton.getSharePhotosChoose().get(position))
                .error(R.drawable.ic_pic_default)
                .into((ImageView) convertView.findViewById(R.id.ivItemViewSharePhotos));
        container.addView(convertView, 0);
        return convertView;
    }

    public void refresh(){
        views.clear();
        View view;
        for (String s : PhotoChooser.Singleton.getSharePhotosChoose()) {
            view = activity.getLayoutInflater().inflate(R.layout.item_pager_view_share_photos, null);
            views.add(view);
        }
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position + 1) + "/" + views.size();
    }
}
