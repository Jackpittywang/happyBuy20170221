package dotc.android.happybuy.modules.show.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.update.util.StringUtil;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoShowItem;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.login.LoginActivity;
import dotc.android.happybuy.modules.login.func.AccountHelper;
import dotc.android.happybuy.modules.show.ShowDetailActivity;
import dotc.android.happybuy.modules.show.ShowSampleActivity;
import dotc.android.happybuy.modules.userprofile.UserProfileActivity;
import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by huangli on 16/3/31.
 */
public class ShowProductListAdapter extends BaseAdapter {
    private Context mContext;
    private List<PojoShowItem> mlist = new ArrayList<>();
    public static final String TAG = "ShowProductListAdapter";

    //当图片image里面数据为空时，设置type为2
    public static final int NONIMAGE=2;

    private int threePhotoWidthHeight;

    private class ViewHolder {
        private ImageView circleImageView;
        private TextView tvName;
        private TextView tvRewordProduct;
        private TextView tvComment;
        private TextView tvPtime;
        private GridView gridView;
        private CheckBox cbZan;
        private TextView tvZanNum;
        private RelativeLayout checkboxClickLayout;
        private ImageView imagViewOne;
        private ImageView imagViewTwo;
        private ImageView imagViewThree;
        private ImageView imagViewFour;
        private TextView tvShowWin;
        private RelativeLayout rlShow;
        private LinearLayout llShowMedal;
        private LinearLayout llShowPhotos;
    }

    public ShowProductListAdapter(Context context) {
        mContext = context;
        calcThreePhotoWidth();
    }

    public void clearData() {
        mlist.clear();
    }

    public void addData(List<PojoShowItem> list) {
        for (PojoShowItem item : list) {
            mlist.add(item);
        }
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mlist.get(position).images.length <= 0) {
            return NONIMAGE;
        }else if(mlist.get(position).images.length <= 1){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int type = getItemViewType(position);
        final PojoShowItem pojoShowItem = mlist.get(position);
        // 如果缓存convertView为空，则需要创建View
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case NONIMAGE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_show_none, null);
                    break;
                case 0:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_show_one, null);
                    holder.imagViewOne = (ImageView) convertView.findViewById(R.id.iv_img_one);
                    break;
                case 1:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_show_three, null);
                    holder.llShowPhotos = (LinearLayout) convertView.findViewById(R.id.llShowPhotos);

                    holder.imagViewOne = new ImageView(mContext);
                    holder.imagViewTwo = new ImageView(mContext);
                    holder.imagViewThree = new ImageView(mContext);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(threePhotoWidthHeight, threePhotoWidthHeight);
                    lp.setMargins(0, 0, DisplayUtils.dp2Px(mContext, 5), 0);
                    holder.llShowPhotos.addView(holder.imagViewOne, lp);
                    holder.llShowPhotos.addView(holder.imagViewTwo, lp);
                    holder.llShowPhotos.addView(holder.imagViewThree, threePhotoWidthHeight, threePhotoWidthHeight);

                    break;
//                case 2:
//                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_show_two, null);
//                    holder.imagViewOne = (ImageView) convertView.findViewById(R.id.iv_img_one);
//                    holder.imagViewTwo = (ImageView) convertView.findViewById(R.id.iv_img_two);
//                    break;
//                case 3:
//                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_show_four, null);
//                    holder.imagViewOne = (ImageView) convertView.findViewById(R.id.iv_img_one);
//                    holder.imagViewTwo = (ImageView) convertView.findViewById(R.id.iv_img_two);
//                    holder.imagViewThree = (ImageView) convertView.findViewById(R.id.iv_img_three);
//                    holder.imagViewFour = (ImageView) convertView.findViewById(R.id.iv_img_four);
//                    break;
            }
            holder.circleImageView = (ImageView) convertView.findViewById(R.id.circle_portrait);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment);
            holder.tvPtime = (TextView) convertView.findViewById(R.id.tv_ptime);
            holder.cbZan = (CheckBox) convertView.findViewById(R.id.cb_zan);
            holder.tvZanNum = (TextView) convertView.findViewById(R.id.tv_zan_num);
            holder.tvRewordProduct = (TextView) convertView.findViewById(R.id.tv_reword_product);
            holder.checkboxClickLayout = (RelativeLayout) convertView.findViewById(R.id.checkbox_click_layout);
            holder.tvShowWin = (TextView) convertView.findViewById(R.id.tvShowWin);
            holder.rlShow = (RelativeLayout) convertView.findViewById(R.id.rlShow);
            holder.llShowMedal = (LinearLayout) convertView.findViewById(R.id.llShowMedal);
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder) convertView.getTag();
           /* holder = (ViewHolder) convertView.getTag();
            ((ShowProductPicGridAdpter) holder.gridView.getAdapter()).setPicurls(pojoShowItem.images);
            ((ShowProductPicGridAdpter) holder.gridView.getAdapter()).notifyDataSetChanged();*/
        }
//        holder.gridView.setAdapter( new ShowProductPicGridAdpter(mContext, pojoShowItem.images));
//        holder.gridView.setClickable(false);
//        holder.gridView.setPressed(false);
//        holder.gridView.setEnabled(false);
        switch (type) {
            case NONIMAGE:
                break;
            case 0:
                Glide.with(mContext).load(mlist.get(position).images[0]).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_pic_big_default).into(holder.imagViewOne);
                holder.imagViewOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
                        intent.putExtra("pics", mlist.get(position).images);
                        intent.putExtra("position", 0);
                        mContext.startActivity(intent);
                    }
                });
                break;
            case 1:
                if (mlist.get(position).images.length >= 3) {
                    holder.imagViewOne.setVisibility(View.VISIBLE);
                    holder.imagViewTwo.setVisibility(View.VISIBLE);
                    holder.imagViewThree.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(mlist.get(position).images[0]).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewOne);
                    Glide.with(mContext).load(mlist.get(position).images[1]).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewTwo);
                    Glide.with(mContext).load(mlist.get(position).images[2]).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewThree);
                } else if (mlist.get(position).images.length >= 2) {
                    holder.imagViewOne.setVisibility(View.VISIBLE);
                    holder.imagViewTwo.setVisibility(View.VISIBLE);
                    holder.imagViewThree.setVisibility(View.INVISIBLE);
                    Glide.with(mContext).load(mlist.get(position).images[0]).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewOne);
                    Glide.with(mContext).load(mlist.get(position).images[1]).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewTwo);
                } else {
                    holder.imagViewOne.setVisibility(View.INVISIBLE);
                    holder.imagViewTwo.setVisibility(View.INVISIBLE);
                    holder.imagViewThree.setVisibility(View.INVISIBLE);
                }

                holder.imagViewOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
                        intent.putExtra("pics", mlist.get(position).images);
                        intent.putExtra("position", 0);
                        mContext.startActivity(intent);
                    }
                });
                holder.imagViewTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
                        intent.putExtra("pics", mlist.get(position).images);
                        intent.putExtra("position", 1);
                        mContext.startActivity(intent);
                    }
                });
                holder.imagViewThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
                        intent.putExtra("pics", mlist.get(position).images);
                        intent.putExtra("position", 2);
                        mContext.startActivity(intent);
                    }
                });
                break;
//            case 2:
//                Glide.with(mContext).load(mlist.get(position).images[0]).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewOne);
//                Glide.with(mContext).load(mlist.get(position).images[1]).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewTwo);
//                holder.imagViewOne.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
//                        intent.putExtra("pics", mlist.get(position).images);
//                        intent.putExtra("position", 0);
//                        mContext.startActivity(intent);
//                    }
//                });
//                holder.imagViewTwo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
//                        intent.putExtra("pics", mlist.get(position).images);
//                        intent.putExtra("position", 1);
//                        mContext.startActivity(intent);
//                    }
//                });
//                break;
//            case 3:
//                Glide.with(mContext).load(mlist.get(position).images[0]).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewOne);
//                Glide.with(mContext).load(mlist.get(position).images[1]).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewTwo);
//                Glide.with(mContext).load(mlist.get(position).images[2]).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewThree);
//                Glide.with(mContext).load(mlist.get(position).images[3]).centerCrop().placeholder(R.drawable.ic_pic_default).into(holder.imagViewFour);
//                holder.imagViewOne.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
//                        intent.putExtra("pics", mlist.get(position).images);
//                        intent.putExtra("position", 0);
//                        mContext.startActivity(intent);
//                    }
//                });
//                holder.imagViewTwo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
//                        intent.putExtra("pics", mlist.get(position).images);
//                        intent.putExtra("position", 1);
//                        mContext.startActivity(intent);
//                    }
//                });
//                holder.imagViewThree.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
//                        intent.putExtra("pics", mlist.get(position).images);
//                        intent.putExtra("position", 2);
//                        mContext.startActivity(intent);
//                    }
//                });
//                holder.imagViewFour.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, ShowSampleActivity.class);
//                        intent.putExtra("pics", mlist.get(position).images);
//                        intent.putExtra("position", 3);
//                        mContext.startActivity(intent);
//                    }
//                });
//                break;
        }

        holder.tvName.setText(pojoShowItem.user_info.nick);
        if (!StringUtil.isEmpty(pojoShowItem.message)) {
            holder.tvComment.setVisibility(View.VISIBLE);
            String content = pojoShowItem.message;
            try {
                content = URLDecoder.decode(content, "utf-8");
            } catch (Exception ignore) {
            }
            holder.tvComment.setText(content);
        } else {
            holder.tvComment.setText(null);
            holder.tvComment.setVisibility(View.GONE);
        }
        holder.tvPtime.setText(pojoShowItem.create_time_str);
        holder.tvRewordProduct.setText(pojoShowItem.product_name);
        holder.tvZanNum.setText(pojoShowItem.approval_count);
        holder.cbZan.setOnCheckedChangeListener(null);
        final TextView tvZan = holder.tvZanNum;
        final CheckBox cbZan = holder.cbZan;
        holder.cbZan.setChecked(pojoShowItem.approval_status.equals("1"));
        holder.checkboxClickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountHelper.getInstance(GlobalContext.get()).isLogin()) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    mContext.startActivity(intent);
                    return;
                }
                cbZan.setChecked(!cbZan.isChecked());
            }
        });
        holder.cbZan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int zannum = Integer.parseInt(pojoShowItem.approval_count);
                if (isChecked) {
                    tvZan.setText(++zannum + "");
                } else {
                    tvZan.setText(--zannum + "");
                }
                pojoShowItem.approval_count = String.valueOf(zannum);
                zan(pojoShowItem);
            }
        });
//        Glide.with(mContext)
//                .load(mlist.get(position).user_info.avatar).fitCenter().placeholder(R.drawable.ic_default_portrait)
//                .into(holder.circleImageView);
        Glide.with(mContext)
                .load(urlDecode(mlist.get(position).user_info.avatar))
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.pic_circle_portrait_placeholder)
                .into(new BitmapImageViewTarget(holder.circleImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.circleImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
        View.OnClickListener winClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.MainActivity.Click_Product, "show", null);
                Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID, mlist.get(position).product_id);
                mContext.startActivity(intent);
            }
        };
        holder.tvRewordProduct.setOnClickListener(winClickListener);
        holder.tvShowWin.setOnClickListener(winClickListener);
        View.OnClickListener userCenterListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.MainActivity.Click_User_Details, "show", null);
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.EXTRA_UID, mlist.get(position).uid);
                intent.putExtra(UserProfileActivity.EXTRA_NICKNAME, mlist.get(position).user_info.nick);
                mContext.startActivity(intent);
            }
        };

        holder.tvName.setOnClickListener(userCenterListener);
        holder.circleImageView.setOnClickListener(userCenterListener);
        holder.rlShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.MainActivity.Click_show_details, "show", null);
                Intent intent = new Intent(mContext, ShowDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ShowDetailActivity.KEY_SHOW_DETAIL_ITEM, mlist.get(position));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        if (mlist.get(position).type != null && mlist.get(position).type.equals("2")) {
            // good sharing
            holder.llShowMedal.setVisibility(View.VISIBLE);
        } else {
            holder.llShowMedal.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private void zan(PojoShowItem pojoShowItem) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("shareover_id", pojoShowItem.id);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.SHARE_ZAN, map, new Network.JsonCallBack<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                HBLog.d(TAG + " onSuccess " + jsonObject.toString());
//                ToastUtils.showShortToast(mContext,"zan success");
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
//                ToastUtils.showShortToast(mContext, "zan fail");
            }

            @Override
            public Class<JSONObject> getObjectClass() {
                return JSONObject.class;
            }
        });
    }

    private String urlDecode(String str) {
        String result;
        try {
            result = URLDecoder.decode(str, "utf-8");
        } catch (Exception ignore) {
            return str;
        }
        return result;
    }

    private void calcThreePhotoWidth() {
        double screenWidth = DisplayUtils.getScreenWidth(mContext);
        double blank = DisplayUtils.dp2Px(mContext, 30);
        threePhotoWidthHeight = (int) ((screenWidth - blank) / 3.0);
    }
}
