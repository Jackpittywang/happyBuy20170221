package dotc.android.happybuy.modules.show.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.show.SharePrizeActivity;
import dotc.android.happybuy.modules.show.func.PhotoChooser;

import static android.view.LayoutInflater.from;

/**
 * Created by LiShen
 * on 16/12/14.
 */
public class SharePhotoChooseAdapter extends RecyclerView.Adapter<SharePhotoChooseAdapter.ViewHolder> {

    private static final String END_ADD_MARK = "end_add_mark";

    private List<String> data;
    private SharePrizeActivity activity;

    public SharePhotoChooseAdapter(SharePrizeActivity activity) {
        this.activity = activity;
        data = new ArrayList<>();
        data.addAll(PhotoChooser.Singleton.getSharePhotosChoose());
        if (data.size() < PhotoChooser.Singleton.getSharePhotoMaxNum()) {
            data.add(END_ADD_MARK);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(from(activity).inflate(R.layout.item_grid_share_photo_choose, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (data.get(holder.getAdapterPosition()).equals(END_ADD_MARK)) {
            holder.ivItemSharePhotoChooseDelete.setVisibility(View.GONE);
            holder.ivItemSharePhotoChoose.setImageDrawable(activity.getResources().getDrawable(
                    R.drawable.ic_grey_share_photo_choose_add));
            holder.ivItemSharePhotoChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.showShareDialog();
                }
            });
        } else {
            Glide.with(activity)
                    .load(new File(data.get(holder.getAdapterPosition())))
                    .error(R.drawable.ic_pic_default)
                    .into(holder.ivItemSharePhotoChoose);
            holder.ivItemSharePhotoChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.viewSharePhoto(holder.getAdapterPosition());
                }
            });
            holder.ivItemSharePhotoChooseDelete.setVisibility(View.VISIBLE);
            holder.ivItemSharePhotoChooseDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoChooser.Singleton.removeSharePhoto(holder.getAdapterPosition());
                    refresh();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void refresh() {
        data = new ArrayList<>();
        data.addAll(PhotoChooser.Singleton.getSharePhotosChoose());
        if (data.size() < PhotoChooser.Singleton.getSharePhotoMaxNum()) {
            data.add(END_ADD_MARK);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivItemSharePhotoChoose;
        private ImageView ivItemSharePhotoChooseDelete;

        public ViewHolder(View view) {
            super(view);
            ivItemSharePhotoChoose = (ImageView)
                    view.findViewById(R.id.ivItemSharePhotoChoose);
            ivItemSharePhotoChooseDelete = (ImageView)
                    view.findViewById(R.id.ivItemSharePhotoChooseDelete);
        }
    }

}
