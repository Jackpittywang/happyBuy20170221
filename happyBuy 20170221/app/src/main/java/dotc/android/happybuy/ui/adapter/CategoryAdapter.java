package dotc.android.happybuy.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoCategory;


/**
 * Created by wangjun on 16/2/1.
 */
public class CategoryAdapter extends BaseAdapter {

    private final String TAG = this.getClass().getSimpleName();
    private List<PojoCategory> mPojoCategoryList;
    private Context mContext;
    private LayoutInflater mInflater;
//    private AppIconLoader mAppIconLoader;

    public CategoryAdapter(Context context, List<PojoCategory> pojoCategoryList){
        mPojoCategoryList = pojoCategoryList;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void updateList(List<PojoCategory> userInfos){
        mPojoCategoryList = userInfos;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mPojoCategoryList.size();
    }

    @Override
    public PojoCategory getItem(int i) {
        return mPojoCategoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_category, viewGroup, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final PojoCategory pojoCategory = mPojoCategoryList.get(position);

        holder.textView.setText(pojoCategory.name);
        Glide.with(mContext)
                .load(pojoCategory.icon)
//                .placeholder(R.mipmap.ic_default_image)
                .crossFade()
                .into(holder.imageView);
        return convertView;
    }

    static class Holder {
        ImageView imageView;
        TextView textView;

        public Holder(View view){
            imageView = (ImageView) view.findViewById(R.id.imageview);
            textView = (TextView) view.findViewById(R.id.textview);
        }
    }

}
