package dotc.android.happybuy.modules.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.modules.coupon.fragment.AvailableRedBagFragment;
import dotc.android.happybuy.modules.coupon.fragment.ReadyToDistributeRedBagFragment;
import dotc.android.happybuy.modules.coupon.fragment.UnavailableRedBagFragment;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.adapter.TabFragmentAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.TabListViewPager;
import dotc.android.happybuy.uibase.widget.RepackTabLayout;

/**
 * Created by wangzhiyuan on 2016/7/19.
 */
public class RedPacketActivity extends BaseActivity implements UnavailableRedBagFragment.FragmentCallBack2 {

    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_AVAILABLE_COUPONS_NOTIFICATION_ENTER =
            "extra_available_coupons_notification_enter";

    public static final String COUPON_TYPE_AVAILABLE = "1";
    public static final String COUPON_TYPE_READYTO = "2";
    public static final String COUPON_TYPE_UNAVAILABLE = "0";

    public static final int TAB_POSITION_0 = 0x00;
    public static final int TAB_POSITION_1 = 0x01;
    public static final int TAB_POSITION_2 = 0x02;

    private HBToolbar mToolbar;
    private int mExtraPosition;


    private TabListViewPager mViewPager;
    private TabFragmentAdapter mRedBagCenterAdapter;
    private List<BaseTabFragment> mPages = new ArrayList<>();

    private int mAvaliable = 0;
    private int mReadyTo = 0;
    private RepackTabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_packets);
        readExtraFromIntent();
        initActionbar();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void readExtraFromIntent() {
        mExtraPosition = getIntent().getIntExtra(EXTRA_POSITION, TAB_POSITION_0);

        if (getIntent().getBooleanExtra(EXTRA_AVAILABLE_COUPONS_NOTIFICATION_ENTER, false)) {
            Analytics.sendUIEvent(AnalyticsEvents.Notification.Click_NativeNoti,
                    "available_coupons_notify_click", null);
        }
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.coupons_title);
        mToolbar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setLeftItem(new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (MainTabActivity.isInstanceActive()) {
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
                }
            }
        });

    }

    private void initUI() {
        mViewPager = (TabListViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        tabLayout = (RepackTabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new RepackTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(RepackTabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Analytics.sendUIEvent(AnalyticsEvents.Coupons.Click_Coupon_Available, null, null);
                        break;
                    case 1:
                        Analytics.sendUIEvent(AnalyticsEvents.Coupons.Click_Coupon_Distributed, null, null);
                        break;
                    case 2:
                        Analytics.sendUIEvent(AnalyticsEvents.Coupons.Click_Coupon_Used, null, null);
                        break;
                }
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(RepackTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(RepackTabLayout.Tab tab) {

            }
        });
        mViewPager.setCurrentItem(mExtraPosition, false);
    }

    private void setupViewPager(TabListViewPager mViewPager) {
        String uid = PrefUtils.getString(PrefConstants.UserInfo.UID, "");
        mPages.clear();
        mPages.add(AvailableRedBagFragment.newInstance(uid));
        mPages.add(ReadyToDistributeRedBagFragment.newInstance(uid));
        mPages.add(UnavailableRedBagFragment.newInstance(uid));
        mRedBagCenterAdapter = new TabFragmentAdapter(getSupportFragmentManager(), mPages);
        String[] titles = setTitles(0, 0);
        mRedBagCenterAdapter.setPageTitles(titles);
        mViewPager.setTabAdapter(mRedBagCenterAdapter);
        mViewPager.setOffscreenPageLimit(3);
    }

    public String[] setTitles(int avaliable_red_bag, int ready_to_distribute_red_bag) {
        String avaliable = getString(R.string.avaliable_red_bag, avaliable_red_bag + "");
        String ready_to = getString(R.string.ready_to_distribute_red_bag, ready_to_distribute_red_bag + "");
        String unavaliable = getString(R.string.unavaliable_red_bag);
        String[] titles = {avaliable, ready_to, unavaliable};
        return titles;
    }


    @Override
    public void callbackAvalibleCoupons(int size, int position) {
        mAvaliable = size;
        mRedBagCenterAdapter.setPageTitles(setTitles(mAvaliable, mReadyTo));
        tabLayout.setTabsFromPagerAdapter(mRedBagCenterAdapter);
    }

    @Override
    public void callbackReadyToDistributeCoupons(int size, int position) {
        mReadyTo = size;
        mRedBagCenterAdapter.setPageTitles(setTitles(mAvaliable, mReadyTo));
        tabLayout.setTabsFromPagerAdapter(mRedBagCenterAdapter);
    }


}
