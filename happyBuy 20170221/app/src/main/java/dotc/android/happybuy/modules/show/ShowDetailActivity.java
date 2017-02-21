package dotc.android.happybuy.modules.show;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.net.URLDecoder;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.result.PojoShowItem;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.userprofile.UserProfileActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.widget.ScreenWidthAutoHeightImageView;
import dotc.android.happybuy.util.DateUtil;
import dotc.android.happybuy.util.DisplayUtils;


public class ShowDetailActivity extends BaseActivity {
    public static final String KEY_SHOW_DETAIL_ITEM = "key_show_detail_item";

    private HBToolbar tbShowDetail;
    private LinearLayout llShowDetail;
    private LinearLayout llShowDetailMedal;
    private ImageView ivShareDetailShareUserPortrait;
    private TextView tvShareDetailShareUserName;
    private TextView tvShareDetailShareTime;
    private TextView tvShareDetailAwardName;
    private TextView tvShareDetailAwardItem;
    private TextView tvShareDetailAwardTimes;
    private TextView tvShareDetailAwardCode;
    private TextView tvShareDetailAwardTime;
    private TextView tvShareDetailViewDetail;
    private TextView tvShareDetailContent;

    private PojoShowItem showDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        findView();
        initData();
        setListener();
        initView();
    }

    private void setLayout() {
        setContentView(R.layout.activity_show_detail);
    }

    private void findView() {
        tbShowDetail = (HBToolbar) findViewById(R.id.tbShowDetail);
        llShowDetail = (LinearLayout) findViewById(R.id.llShowDetail);
        llShowDetailMedal = (LinearLayout) findViewById(R.id.llShowDetailMedal);
        ivShareDetailShareUserPortrait = (ImageView) findViewById(R.id.ivShareDetailShareUserPortrait);
        tvShareDetailShareUserName = (TextView) findViewById(R.id.tvShareDetailShareUserName);
        tvShareDetailShareTime = (TextView) findViewById(R.id.tvShareDetailShareTime);
        tvShareDetailAwardName = (TextView) findViewById(R.id.tvShareDetailAwardName);
        tvShareDetailAwardItem = (TextView) findViewById(R.id.tvShareDetailAwardItem);
        tvShareDetailAwardTimes = (TextView) findViewById(R.id.tvShareDetailAwardTimes);
        tvShareDetailAwardCode = (TextView) findViewById(R.id.tvShareDetailAwardCode);
        tvShareDetailAwardTime = (TextView) findViewById(R.id.tvShareDetailAwardTime);
        tvShareDetailViewDetail = (TextView) findViewById(R.id.tvShareDetailViewDetail);
        tvShareDetailContent = (TextView) findViewById(R.id.tvShareDetailContent);
    }

    private void initData() {
        showDetail = (PojoShowItem) getIntent().getExtras().getSerializable(KEY_SHOW_DETAIL_ITEM);
        if (showDetail == null) {
            finish();
        }
    }

    private void setListener() {

    }

    private void initView() {
        tbShowDetail.setDisplayHomeAsUpEnabled(true);
        tbShowDetail.setTitle(R.string.activity_show_detail);

        Glide.with(this)
                .load(urlDecode(showDetail.user_info.avatar))
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.pic_circle_portrait_placeholder)
                .into(new BitmapImageViewTarget(ivShareDetailShareUserPortrait) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.
                                        create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ivShareDetailShareUserPortrait.setImageDrawable(circularBitmapDrawable);
                    }
                });
        tvShareDetailShareUserName.setText(showDetail.user_info.nick);
        View.OnClickListener userCenterListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowDetailActivity.this, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.EXTRA_UID,showDetail.uid);
                intent.putExtra(UserProfileActivity.EXTRA_NICKNAME,showDetail.user_info.nick);
                startActivity(intent);
            }
        };
        ivShareDetailShareUserPortrait.setOnClickListener(userCenterListener);
        tvShareDetailShareUserName.setOnClickListener(userCenterListener);
        tvShareDetailShareTime.setText(showDetail.create_time_str);
        tvShareDetailAwardName.setText(getString(R.string.prize_name, showDetail.product_name));
        tvShareDetailAwardItem.setText(getString(R.string.lable_period_name,
                String.valueOf(showDetail.product_item)));
        tvShareDetailAwardTimes.setText(getString(R.string.lable_period_times,
                String.valueOf(showDetail.participate_count)));
        tvShareDetailAwardCode.setText(getString(R.string.lable_win_number,
                String.valueOf(showDetail.award_code)));
        if (showDetail.type != null && showDetail.type.equals("2")) {
            llShowDetailMedal.setVisibility(View.VISIBLE);
        } else {
            llShowDetailMedal.setVisibility(View.INVISIBLE);
        }
        long awardTime = 0;
        try {
            awardTime = Long.parseLong(showDetail.award_time) * 1000;
        } catch (Exception ignore) {
        }
        tvShareDetailAwardTime.setText(getString(R.string.award_time, DateUtil.time2ss(awardTime)));
        if (TextUtils.isEmpty(showDetail.message)) {
            tvShareDetailContent.setVisibility(View.GONE);
        } else {
            tvShareDetailContent.setVisibility(View.VISIBLE);
            String content = showDetail.message;
            try {
                content = URLDecoder.decode(content, "utf-8");
            } catch (Exception ignore) {
            }
            tvShareDetailContent.setText(content);
        }
        tvShareDetailViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.MainActivity.Click_Product, "show_details", null);
                Intent intent = new Intent(ShowDetailActivity.this, GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID, showDetail.product_id);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ITEM_ID, showDetail.product_item_id);
                startActivity(intent);
            }
        });

        initPhotos();
    }

    private void initPhotos() {
        if (showDetail.images == null || showDetail.images.length == 0) {
            return;
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, DisplayUtils.dp2Px(this, 10));

        for (int position = 0; position < showDetail.images.length; position++) {
            int[] widthHeight = getWidthHeightFromServer(position);

            ScreenWidthAutoHeightImageView imageView =
                    new ScreenWidthAutoHeightImageView(ShowDetailActivity.this, widthHeight[0], widthHeight[1], 10);

            llShowDetail.addView(imageView, layoutParams);

            Glide.with(ShowDetailActivity.this)
                    .load(showDetail.images[position])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_pic_default)
                    .into(imageView);
            final int finalPosition = position;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowDetailActivity.this, ShowSampleActivity.class);
                    intent.putExtra("pics", showDetail.images);
                    intent.putExtra("position", finalPosition);
                    startActivity(intent);
                }
            });
        }
    }

    private int[] getWidthHeightFromServer(int position) {
        int[] result = {960, 960};
        try {
            if (showDetail.new_images != null && showDetail.new_images.size() > 0
                    && showDetail.new_images.get(position) != null
                    && showDetail.new_images.get(position).w > 0
                    && showDetail.new_images.get(position).h > 0) {
                result[0] = showDetail.new_images.get(position).w;
                result[1] = showDetail.new_images.get(position).h;
            } else {
                String url = showDetail.images[position];
                HBLog.w(url);
                String[] temp = url.split("_");
                String widthHeight = temp[temp.length - 1];
                widthHeight = widthHeight.split("\\.")[0];
                temp = widthHeight.split("x");
                result[0] = Integer.parseInt(temp[0]);
                result[1] = Integer.parseInt(temp[1]);
            }
        } catch (Exception ignore) {
        }
        return result;
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
}
