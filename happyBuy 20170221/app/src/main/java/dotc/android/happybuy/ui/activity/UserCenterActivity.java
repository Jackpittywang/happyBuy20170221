package dotc.android.happybuy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoUserInfo;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.recharge.RechargeActivity;
import dotc.android.happybuy.modules.setting.SettingActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.adapter.TabFragmentAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.RefreshLayout;
import dotc.android.happybuy.uibase.component.TabListViewPager;
import dotc.android.happybuy.modules.userprofile.fragment.MyJoinFragment;
import dotc.android.happybuy.modules.userprofile.fragment.ShareRecordFragment;
import dotc.android.happybuy.modules.userprofile.fragment.WinRecordFragment;
import dotc.android.happybuy.ui.help.PopupWindowProsessor;
import dotc.android.happybuy.modules.me.widget.MyJoinDropDownView;
import dotc.android.happybuy.uibase.widget.RepackTabLayout;

/**
 * 个人中心
 * Created by zhanqiang.mei on 2016/3/29.
 */
public class UserCenterActivity extends BaseActivity implements View.OnClickListener,RefreshLayout.OnRefreshListener{

    public static final String EXTRA_TAB = "extra_tab";
    public static final int TAB_INDEX_0 = 0x00;
    public static final int TAB_INDEX_1 = 0x01;
    public static final int TAB_INDEX_2 = 0x02;

    private int mExtraTabIndex;

    private final int REQUEST_CODE_UPDATE_PROFILE = 0x00;
    private HBToolbar mToolbar;
    private RefreshLayout mParentLayout;
    private ImageView mImgHead;
    private TextView mTVUserName;
    private TextView mCoinTextView;
//    private LinearLayout mCoupons;

    private TextView mBtnPay;
    private TabListViewPager mViewPager;
    private TextView mTVFirstTab;

    private PojoUserInfo mUserInfo;

    private PopupWindowProsessor mPopupWindowP;
    private TabFragmentAdapter mUserCenterAdapter;
    private MyJoinFragment mMyJoinFragment;
    private List<BaseTabFragment> mPages = new ArrayList<>();

    private int mJoinType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        readExtraFromIntent();
        initActionbar();
        mPopupWindowP = new PopupWindowProsessor(UserCenterActivity.this);
        initViews();
        initData();
//        mParentLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                mParentLayout.getViewTreeObserver().removeOnPreDrawListener(this);
//                mParentLayout.startRefreshing();
//                return true;
//            }
//        });
    }

    private void readExtraFromIntent(){
        if(getIntent()!=null){
            mExtraTabIndex = getIntent().getIntExtra(EXTRA_TAB,TAB_INDEX_0);
        }
    }


    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.user_center);
        mToolbar.setDisplayHomeAsUpEnabled(true);

        mToolbar.setRightItem(R.drawable.icon_setting, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {

                startActivity(new Intent(UserCenterActivity.this, SettingActivity.class));
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Settings, null,null);
            }
        });
    }

    private void initViews() {
//        mCoupons= (LinearLayout) findViewById(R.id.ll_coupons);
        mParentLayout = (RefreshLayout) findViewById(R.id.layout_refresh);
        mImgHead = (ImageView) findViewById(R.id.icon_user);
        mTVUserName = (TextView) findViewById(R.id.tv_name);
        mCoinTextView = (TextView) findViewById(R.id.textview_coin);
        mBtnPay = (TextView) findViewById(R.id.btn_pay);
        mViewPager = (TabListViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        final RepackTabLayout tabLayout = (RepackTabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mTVFirstTab = new TextView(this);
        LinearLayout.LayoutParams layoutParams=
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mTVFirstTab.setLayoutParams(layoutParams);
        mTVFirstTab.setText(getString(R.string.all_in));
        mTVFirstTab.setGravity(Gravity.CENTER);
        mTVFirstTab.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mTVFirstTab.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_arrow_down), null);
        tabLayout.getTabAt(0).setCustomView(mTVFirstTab);
        mTVFirstTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 0) {
                    showDropDownMenu(tabLayout, mJoinType);
                } else {
                    tabLayout.getTabAt(0).select();
                    mViewPager.setCurrentItem(0);
                }
            }
        });
        tabLayout.setOnTabSelectedListener(new RepackTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(RepackTabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mTVFirstTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                } else {
                    mTVFirstTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorText));
                }
                mViewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_AllOrder,null,null);
                        break;
                    case 1:
                        Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_WinRecord,null,null);
                        break;
                    case 2:
                        Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_ShowRecord,null,null);
                        break;
                }
            }

            @Override
            public void onTabUnselected(RepackTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(RepackTabLayout.Tab tab) {

            }
        });

        mImgHead.setOnClickListener(this);
        mBtnPay.setOnClickListener(this);
        mParentLayout.addOnRefreshListener(this);
//        mCoupons.setOnClickListener(this);
    }

    private boolean showDropDownMenu(final View finalView, final int joinType) {
        if( mPopupWindowP.isShowing() ) {
            return true ;
        }

        MyJoinDropDownView settingDropDown = new MyJoinDropDownView(UserCenterActivity.this);
        settingDropDown.setOnSettingDropDownClickListener(new MyJoinDropDownView.OnSettingDropDownClickListener() {

            @Override
            public void onSettingDropDown(int action) {
                if (action == MyJoinDropDownView.OnSettingDropDownClickListener.ACTION_ALL_IN) {
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Choose_AllOrder,null,null);
                    mJoinType = 0;
                    mTVFirstTab.setText(getString(R.string.all_in));
                    if (mMyJoinFragment != null) {
                        mMyJoinFragment.setParticipateType(MyJoinFragment.TYPE_ALL);
                    }
                } else if (action == MyJoinDropDownView.OnSettingDropDownClickListener.ACTION_ON_GOING) {
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Choose_InProcess,null,null);
                    mJoinType = 1;
                    mTVFirstTab.setText(getString(R.string.on_going));
                    if (mMyJoinFragment != null) {
                        mMyJoinFragment.setParticipateType(MyJoinFragment.TYPE_ONSALE);
                    }
                } else if (action == MyJoinDropDownView.OnSettingDropDownClickListener.ACTION_ALREADY_ANNOUNCED) {
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Choose_Finished,null,null);
                    mJoinType = 2;
                    mTVFirstTab.setText(getString(R.string.already_announced));
                    if (mMyJoinFragment != null) {
                        mMyJoinFragment.setParticipateType(MyJoinFragment.TYPE_AWARDED);
                    }
                }
                mPopupWindowP.dismiss();
            }
        }, joinType);
        mPopupWindowP.setView(settingDropDown);
        mPopupWindowP.showAsDropDown(finalView, 0, 0);
        return true;
    }

    private void setupViewPager(TabListViewPager mViewPager) {
        String uid = PrefUtils.getString(PrefConstants.Network.uid, "");
        mPages.clear();
        mMyJoinFragment = new MyJoinFragment();
        mPages.add(mMyJoinFragment);
        mPages.add(WinRecordFragment.newInstance(uid));
        mPages.add(ShareRecordFragment.newInstance(uid));
        mUserCenterAdapter = new TabFragmentAdapter(getSupportFragmentManager(), mPages);
        String[] titles = getResources().getStringArray(R.array.user_center_tab);
        mUserCenterAdapter.setPageTitles(titles);
        mViewPager.setTabAdapter(mUserCenterAdapter);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initData() {
        mViewPager.setCurrentItem(mExtraTabIndex,false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        doRefreshInfoIfNecessary();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        HBLog.d(TAG + " onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);

//        if(requestCode == REQUEST_CODE_UPDATE_PROFILE){
//            if(resultCode == Activity.RESULT_OK){
//                mUserInfo = (PojoUserInfo) data.getSerializableExtra(MyProfileActivity.OUTPUT_EXTRA_USERINFO);
//                uploadUserInfoViews(mUserInfo);
//            }
//        }



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        HBLog.d(TAG + " onSaveInstanceState ");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_user:
                if(mUserInfo!=null){
                    Intent intent = new Intent(this,MyProfileActivity.class);
                    intent.putExtra(MyProfileActivity.EXTRA_USERINFO,mUserInfo);
                    startActivity(intent);
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Portrait,null,null);
                }
                break;
            case R.id.btn_pay:
                startActivity(new Intent(this,RechargeActivity.class));
                break;
           /* case R.id.ll_coupons:
                startActivity(new Intent(this,RedPacketActivity.class));
                break;*/
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshView) {
        doRefreshInfoIfNecessary();
    }

    private void doRefreshInfoIfNecessary(){
        String uid = PrefUtils.getString(this, PrefConstants.Network.uid, "");
        Map<String,Object> param = new HashMap<>();
        param.put("from_uid", uid);

        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.USER_INFO, param, new Network.JsonCallBack<PojoUserInfo>() {
            @Override
            public void onSuccess(PojoUserInfo userInfo) {
                HBLog.d(TAG+" doRefreshInfoIfNecessary onSuccess "+userInfo);
                if (userInfo != null) {
                    saveUserInfo(userInfo);
                    if (!isFinishing()) {
                        if (userInfo != null) {
                            uploadUserInfoViews(userInfo);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " doRefreshInfoIfNecessary onFailed " + code + " " + message + " " + e);
            }

            @Override
            public Class<PojoUserInfo> getObjectClass() {
                return PojoUserInfo.class;
            }
        });
    }

    private void uploadUserInfoViews(PojoUserInfo userInfo){
        this.mUserInfo = userInfo;
        mTVUserName.setText(userInfo.nick);
        mCoinTextView.setText(getString(R.string.lable_coin,userInfo.coin));
        Glide.with(GlobalContext.get()).load(userInfo.avatar).crossFade().into(mImgHead);
    }

    private void saveUserInfo(PojoUserInfo userInfo) {
        PrefUtils.putString(PrefConstants.UserInfo.USER_NAME, userInfo.nick);
        PrefUtils.putString(PrefConstants.UserInfo.USER_ICON_URL, userInfo.avatar);
        PrefUtils.putString(PrefConstants.UserInfo.GEO, userInfo.geo);
        PrefUtils.putString(PrefConstants.UserInfo.LEVEL, userInfo.level);
        PrefUtils.putInt(PrefConstants.UserInfo.COIN, userInfo.coin);
    }
}
