package dotc.android.happybuy.modules.setting.invite.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoSharePlatform;

/**
 * Created by LiShen
 * on 2016/12/6.
 */

public class SharePlatformAdapter extends BaseAdapter {

    private List<PojoSharePlatform> data;
    private Activity activity;

    public SharePlatformAdapter(Activity activity) {
        this.activity = activity;
        data = new ArrayList<>();
        int[] icons = {
                R.drawable.ic_share_line,
                R.drawable.ic_share_facebook,
                R.drawable.ic_share_messenger,
                R.drawable.ic_share_sms,
                R.drawable.ic_share_mail,
        };
        String[] titles = activity.getResources().getStringArray(R.array.share_platforms);
        PojoSharePlatform sharePlatform;
        for (int i = 0; i < titles.length; i++) {
            sharePlatform = new PojoSharePlatform();
            sharePlatform.index = i;
            sharePlatform.icon = icons[i];
            sharePlatform.title = titles[i];
            data.add(sharePlatform);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PojoSharePlatform getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).hashCode();
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.item_grid_share_platform,
                    viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivItemSharePlatform.setImageDrawable(
                activity.getResources().getDrawable(getItem(i).icon));
        holder.tvItemSharePlatform.setText(getItem(i).title);

        return convertView;
    }

    class ViewHolder {
        TextView tvItemSharePlatform;
        ImageView ivItemSharePlatform;

        public ViewHolder(View view) {
            tvItemSharePlatform = (TextView) view.findViewById(R.id.tvItemSharePlatform);
            ivItemSharePlatform = (ImageView) view.findViewById(R.id.ivItemSharePlatform);
        }
    }
}
