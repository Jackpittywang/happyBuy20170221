package dotc.android.happybuy.modules.show.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoPhotoFolder;
import dotc.android.happybuy.modules.show.func.PhotoChooser;

/**
 * Created by LiShen
 * on 2016/12/14.
 */

public class PhotoFolderChooseAdapter extends BaseAdapter {
    private Activity activity;
    private List<PojoPhotoFolder> data;

    public PhotoFolderChooseAdapter(Activity activity) {
        this.activity = activity;
        data = PhotoChooser.Singleton.getPhotoFolderList();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PojoPhotoFolder getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.item_list_photo_folder,
                    parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvItemPhotoFolderName.setText(getItem(position).folderName);
        holder.tvItemPhotoFolderNum.setText(activity.getString(R.string.photo_folder_files_num,
                String.valueOf(getItem(position).photoNum)));
        Glide.with(activity).load(new File(data.get(position).iconPhoto)).error(R.drawable.ic_pic_default)
                .into(holder.ivItemPhotoFolder);
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivItemPhotoFolder;
        private TextView tvItemPhotoFolderName;
        private TextView tvItemPhotoFolderNum;

        public ViewHolder(View view) {
            ivItemPhotoFolder = (ImageView) view.findViewById(R.id.ivItemPhotoFolder);
            tvItemPhotoFolderName = (TextView) view.findViewById(R.id.tvItemPhotoFolderName);
            tvItemPhotoFolderNum = (TextView) view.findViewById(R.id.tvItemPhotoFolderNum);
        }
    }
}
