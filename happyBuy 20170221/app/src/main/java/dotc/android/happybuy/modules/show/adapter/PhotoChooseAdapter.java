package dotc.android.happybuy.modules.show.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.show.PhotoChooseActivity;
import dotc.android.happybuy.modules.show.func.PhotoChooser;
import dotc.android.happybuy.uibase.widget.SplitScreenSquareImageView;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by LiShen
 * on 2016/12/13.
 */

public class PhotoChooseAdapter extends BaseAdapter {
    private List<String> data;
    private boolean check[];
    private PhotoChooseActivity activity;
    private int selectNumMax;

    public PhotoChooseAdapter(PhotoChooseActivity activity) {
        this.activity = activity;
        data = new ArrayList<>();
        check = new boolean[data.size()];

        selectNumMax = PhotoChooser.Singleton.getSharePhotoMaxNum()
                - PhotoChooser.Singleton.getSharePhotosChoose().size();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
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
            convertView = activity.getLayoutInflater().inflate(R.layout.item_grid_photo_choose,
                    parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (check[position]) {
            holder.ivItemPhotoChooseCheck.setImageDrawable(activity.getResources().
                    getDrawable(R.drawable.ic_photo_choose_checked));
        } else {
            holder.ivItemPhotoChooseCheck.setImageDrawable(activity.getResources().
                    getDrawable(R.drawable.ic_photo_choose_uncheck));
        }

        Glide.with(activity).load(new File(data.get(position))).into(holder.ivPhotoChoose);

        return convertView;
    }

    public void setCheck(int position) {
        check[position] = !check[position];
        int i = 0;
        for (boolean b : check) {
            if (b)
                i++;
        }
        if (i > selectNumMax) {
            i = selectNumMax;
            activity.showAlertDialog(activity.getString(R.string.can_only_select_n_photos,
                    String.valueOf(PhotoChooser.Singleton.getSharePhotoMaxNum())));
            check[position] = false;
        }
        activity.setChooseNum(i);
        notifyDataSetChanged();
    }

    public void setPhotoFolder(int index) {
        data = PhotoChooser.Singleton.getPhotoFolderList().get(index).photos;
        check = new boolean[data.size()];
        notifyDataSetChanged();
    }

    public void completeSelection() {
        for (int i = 0; i < check.length; i++) {
            if (check[i])
                PhotoChooser.Singleton.addSharePhoto(data.get(i));
        }
    }

    class ViewHolder {
        private SplitScreenSquareImageView ivPhotoChoose;
        private ImageView ivItemPhotoChooseCheck;

        public ViewHolder(View view) {
            ivPhotoChoose = (SplitScreenSquareImageView) view.findViewById(R.id.ivPhotoChoose);
            ivItemPhotoChooseCheck = (ImageView) view.findViewById(R.id.ivItemPhotoChooseCheck);
        }
    }
}
