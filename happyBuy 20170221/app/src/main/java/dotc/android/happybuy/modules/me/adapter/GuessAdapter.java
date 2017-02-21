package dotc.android.happybuy.modules.me.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.bean.YouLikeItem;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.ui.adapter.ProductAdapter;

/**
 * Created by 陈尤岁 on 2016/12/14.
 */

public class GuessAdapter extends BaseAdapter{

    private List<YouLikeItem> mData;

    private String mLabel;

    public GuessAdapter(String label,List<YouLikeItem> data){
        this.mData = data;
        this.mLabel = label;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public YouLikeItem getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        Holder holder = null;
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_guess_you_like,viewGroup,false);
            holder = new Holder(view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        final YouLikeItem item = getItem(i);
        Glide.with(viewGroup.getContext()).load(item.product_img).placeholder(R.drawable.ic_product_default)
                .fitCenter().dontAnimate().into(holder.img);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.ClickGuess,GuessAdapter.this.mLabel, (long) i);
                Intent intent = new Intent(viewGroup.getContext(), GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,item.product_id);
                viewGroup.getContext().startActivity(intent);
            }
        });
        return view;
    }

    static class Holder {
        public ImageView img;

        Holder(View view) {
            img= (ImageView) view.findViewById(R.id.img_item);
        }
    }
}
