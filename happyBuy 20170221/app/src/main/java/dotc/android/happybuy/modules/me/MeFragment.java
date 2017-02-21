package dotc.android.happybuy.modules.me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import dotc.android.happybuy.modules.coupon.RedPacketActivity;
import dotc.android.happybuy.modules.login.BindActivity;
import dotc.android.happybuy.modules.login.LoginActivity;
import dotc.android.happybuy.modules.login.func.AccountHelper;
import dotc.android.happybuy.modules.login.func.UserTokenManager;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.main.base.BaseMainFragment;
import dotc.android.happybuy.modules.me.adapter.MeTabFragmentAdapter;
import dotc.android.happybuy.modules.me.base.BaseMeTabFragment;
import dotc.android.happybuy.modules.me.fragment.MeJoinFragment;
import dotc.android.happybuy.modules.me.fragment.MeShareRecordFragment;
import dotc.android.happybuy.modules.me.fragment.MeWinRecordFragment;
import dotc.android.happybuy.modules.me.widget.MeRefreshLayout;
import dotc.android.happybuy.modules.me.widget.MeTabListViewPager;
import dotc.android.happybuy.modules.me.widget.MyJoinDropDownView;
import dotc.android.happybuy.modules.recharge.TopupActivity;
import dotc.android.happybuy.modules.setting.SettingActivity;
import dotc.android.happybuy.modules.userprofile.fragment.MyJoinFragment;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.activity.MyProfileActivity;
import dotc.android.happybuy.ui.help.PopupWindowProsessor;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.widget.RepackTabLayout;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/8/18.
 */
public class MeFragment extends BaseMainFragment implements View.OnClickListener,MeRefreshLayout.OnRefreshListener {

    public final static String EXTRA_INDEX = "extra_index";
    public final static String EXTRA_REFRESH = "extra_refresh";
    public static final int TAB_INDEX_0 = 0x00;
    public static final int TAB_INDEX_1 = 0x01;
    public static final int TAB_INDEX_2 = 0x02;
    private HBToolbar mToolbar;
    private MeRefreshLayout mParentLayout;
    private ImageView mImgHead;
    private ImageView imageView;
    private TextView mTVUserName;
    private TextView mCoinTextView;
    private TextView mCouponsCount;
    private LinearLayout mCoupons;
    private LinearLayout mBindTips;

    private TextView mBtnPay;
    private RepackTabLayout tabLayout;
    private MeTabListViewPager mViewPager;
    private TextView mTVFirstTab;

    private PojoUserInfo mUserInfo;
    private TextView mTvBind;

    private PopupWindowProsessor mPopupWindowP;
    private MeTabFragmentAdapter mUserCenterAdapter;
    private MeJoinFragment mMyJoinFragment;
    private List<BaseMeTabFragment> mPages = new ArrayList<>();
    public static final String HAVA_SHOWED_CHOOSE_TIPS = "hava_showed_choose_tips";
    private int mJoinType = 0;
    private IsLogined isLogined=null;

    private int mExtraTabIndex = -1;
    private boolean mExtraRefresh;
    private boolean mAccrowUp=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null){
            mExtraTabIndex = args.getInt(EXTRA_INDEX);
        }
//        mExtraParticipateType = getArguments().getInt(EXTRA_TYPE);
    }

    @Override
    public void onNewIntent(Bundle argument) {
        super.onNewIntent(argument);
        if(argument!=null){
            int tabIndex = argument.getInt(EXTRA_INDEX);
            mViewPager.setCurrentItem(tabIndex,false);

            mExtraRefresh = argument.getBoolean(EXTRA_REFRESH,false);
            if(mExtraRefresh){
                BaseMeTabFragment fragment = mUserCenterAdapter.getCurrentFragment();
                if(fragment!=null&&fragment.isAdded()){
                    fragment.startRefreshing();
                }
            }
        }
    }


    private void checkIsLogin(){
        boolean isLogin = AccountHelper.getInstance(getContext()).isLogin();
        boolean isTokenValid = AccountHelper.getInstance(getContext()).isTokenValid();
        if (isLogin || isTokenValid) {
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        mToolbar = (HBToolbar) view.findViewById(R.id.id_toolbar);
        mCoupons= (LinearLayout) view.findViewById(R.id.ll_coupons);
        mParentLayout = (MeRefreshLayout) view.findViewById(R.id.layout_me_refresh);
        mImgHead = (ImageView) view.findViewById(R.id.icon_user);
        mTVUserName = (TextView) view.findViewById(R.id.tv_name);
        mCoinTextView = (TextView) view.findViewById(R.id.textview_coin);
        mCouponsCount = (TextView) view.findViewById(R.id.tv_coupons_count);
        mBtnPay = (TextView) view.findViewById(R.id.btn_pay);
        mViewPager = (MeTabListViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (RepackTabLayout) view.findViewById(R.id.sliding_tabs);
        mTvBind = (TextView) view.findViewById(R.id.tv_bind);
        mBindTips = (LinearLayout) view.findViewById(R.id.ll_bind_tips);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPopupWindowP = new PopupWindowProsessor(getContext());

        checkIsLogin();
        initUI();
        Analytics.sendUIEvent(AnalyticsEvents.MainTab.Me_Page_Show, null,null);
//        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        doRefreshInfoIfNecessary();

    }

    private void initUI(){
        mToolbar.setTitle(R.string.user_center);
        mToolbar.setDisplayHomeAsUpEnabled(false);

        mToolbar.setRightItem(R.drawable.icon_setting, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                startActivity(new Intent(getContext(), SettingActivity.class));
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Settings, null,null);
            }
        });
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
        mTVFirstTab = new TextView(getContext());
        LinearLayout.LayoutParams mTextViewLayoutParams=
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mTVFirstTab.setLayoutParams(mTextViewLayoutParams);
        mTVFirstTab.setText(getString(R.string.all_in));
        mTVFirstTab.setGravity(Gravity.CENTER);
        mTVFirstTab.setTextColor(ContextCompat.getColor(GlobalContext.get(), R.color.colorPrimary));
//        mTVFirstTab.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(GlobalContext.get(), R.drawable.ic_arrow_down), null);

        LinearLayout mFirstView=new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams2=
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mFirstView.setLayoutParams(layoutParams2);
        mFirstView.setOrientation(LinearLayout.HORIZONTAL);
        mFirstView.setGravity(Gravity.CENTER);
        mFirstView.addView(mTVFirstTab);
        imageView=new ImageView(getContext());
        LinearLayout.LayoutParams mImageViewLayoutParams=
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mImageViewLayoutParams.setMargins(10,0,0,0);
        imageView.setLayoutParams(mImageViewLayoutParams);
        imageView.setImageDrawable(ContextCompat.getDrawable(GlobalContext.get(), R.drawable.ic_arrow_down));
        mFirstView.addView(imageView);


        tabLayout.getTabAt(0).setCustomView(mFirstView);
        mFirstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 0) {
                    if(!mAccrowUp){
                        Animation animation= AnimationUtils.loadAnimation(GlobalContext.get(),R.anim.accrow_rotate);
                        animation.setFillAfter(true);
                        imageView.startAnimation(animation);
                        mAccrowUp=true;
                        showDropDownMenu(tabLayout, mJoinType);
                    }
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
                    mTVFirstTab.setTextColor(ContextCompat.getColor(GlobalContext.get(), R.color.colorPrimary));
                } else {
                    mTVFirstTab.setTextColor(ContextCompat.getColor(GlobalContext.get(), R.color.colorText));
                }
                mViewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()){
                    case 0:
                        imageView.setImageDrawable(ContextCompat.getDrawable(GlobalContext.get(), R.drawable.ic_arrow_down));
                        Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_AllOrder,null,null);
                        break;
                    case 1:
                        imageView.setImageDrawable(ContextCompat.getDrawable(GlobalContext.get(), R.drawable.ic_arrow_down_gray));
                        Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_WinRecord,null,null);
                        break;
                    case 2:
                        imageView.setImageDrawable(ContextCompat.getDrawable(GlobalContext.get(), R.drawable.ic_arrow_down_gray));
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
        mCoupons.setOnClickListener(this);
        mTvBind.setOnClickListener(this);
        mTVUserName.setOnClickListener(this);
    }

    private boolean showDropDownMenu(final View finalView, final int joinType) {
        if( mPopupWindowP.isShowing() ) {
            return true ;
        }

        MyJoinDropDownView settingDropDown = new MyJoinDropDownView(getContext());
        settingDropDown.setOnSettingDropDownClickListener(new MyJoinDropDownView.OnSettingDropDownClickListener() {

            @Override
            public void onSettingDropDown(int action) {
                if(isVisible()&&!AppUtil.isActivityDestroyed(getActivity())){
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
                }
                mPopupWindowP.dismiss();
            }
        }, joinType);
        mPopupWindowP.setView(settingDropDown);
        mPopupWindowP.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(mAccrowUp){
                    Animation animation=AnimationUtils.loadAnimation(GlobalContext.get(),R.anim.accrow_rotate_close);
                    imageView.startAnimation(animation);
                    mAccrowUp=false;
                }
            }
        });
        mPopupWindowP.showAsDropDown(finalView, 0, 0);
        return true;
    }

    private void setupViewPager(MeTabListViewPager mViewPager) {
        String uid = PrefUtils.getString(PrefConstants.UserInfo.UID, "");
        mPages.clear();
        mMyJoinFragment = new MeJoinFragment();
        mPages.add(mMyJoinFragment);
        mPages.add(MeWinRecordFragment.newInstance(uid));
        mPages.add(MeShareRecordFragment.newInstance(uid));
        mUserCenterAdapter = new MeTabFragmentAdapter(getChildFragmentManager(), mPages);
        String[] titles = getResources().getStringArray(R.array.user_center_tab);
        mUserCenterAdapter.setPageTitles(titles);
        mViewPager.setTabAdapter(mUserCenterAdapter);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initData() {

        if(mExtraTabIndex>-1){
            mViewPager.setCurrentItem(mExtraTabIndex,false);
        }
        initUserInfoViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_user:{
                    Intent intent = new Intent(getContext(),MyProfileActivity.class);
                    intent.putExtra(MyProfileActivity.EXTRA_USERINFO,mUserInfo);
                    startActivity(intent);
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Portrait,null,null);
                }
                break;
            case R.id.tv_name:{
                Intent intent = new Intent(getContext(),MyProfileActivity.class);
                intent.putExtra(MyProfileActivity.EXTRA_USERINFO,mUserInfo);
                startActivity(intent);
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Portrait,null,null);
            }
            break;
            case R.id.btn_pay:
                Intent intent = new Intent(getContext(), TopupActivity.class);
                intent.putExtra(TopupActivity.EXTRA_ACTIVITY_FROM,TopupActivity.ACTIVITY_FROM_PERSONAL);
                startActivity(intent);
                /*Intent intent = new Intent(getContext(), RateActivity.class);
                startActivity(intent);*/
                break;
            case R.id.ll_coupons:
                Analytics.sendUIEvent(AnalyticsEvents.Coupons.Click_Coupon,null,null);
                startActivity(new Intent(getContext(),RedPacketActivity.class));
                break;
            case R.id.tv_bind:
                startActivity(new Intent(getContext(),BindActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh(MeRefreshLayout refreshView) {
        doRefreshInfoIfNecessary();
    }

    private void doRefreshInfoIfNecessary(){
        String uid = PrefUtils.getString(GlobalContext.get(), PrefConstants.UserInfo.UID, "");
        Map<String,Object> param = new HashMap<>();
        param.put("from_uid", uid);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.USER_INFO, param, new Network.JsonCallBack<PojoUserInfo>() {
            @Override
            public void onSuccess(PojoUserInfo userInfo) {
                HBLog.d(TAG+" doRefreshInfoIfNecessary onSuccess "+userInfo);
                if (userInfo != null) {
                    saveUserInfo(userInfo);
                    if (isAdded()) {
                        uploadUserInfoViews(userInfo);
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
        mCoinTextView.setText(String.valueOf(userInfo.coin));
        mCouponsCount.setText(String.valueOf(userInfo.coupon_count));
        if(HttpProtocol.UserType.ANONYMOUS.equals(userInfo.bind_type)){
            mTvBind.setVisibility(View.VISIBLE);
            mBindTips.setVisibility(View.VISIBLE);
        }else {
            mTvBind.setVisibility(View.GONE);
            mBindTips.setVisibility(View.GONE);
        }
        Glide.with(GlobalContext.get()).load(decodeAvatarURL(userInfo.avatar)).crossFade().placeholder(R.drawable.pic_circle_portrait_placeholder).into(mImgHead);
        /*if(!PrefUtils.getBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE,true)&&!PrefUtils.getBoolean(HAVA_SHOWED_CHOOSE_TIPS,false)){
            PrefUtils.putBoolean(HAVA_SHOWED_CHOOSE_TIPS,true);
            Intent intent = new Intent(getActivity(),NewUserGuideActivity.class);
            intent.putExtra(NewUserGuideActivity.START_TYPE,NewUserGuideActivity.TYPE_1);
            startActivity(intent);
        }*/
    }

    private void initUserInfoViews(){
        isLogined.chechLogin();
        PojoUserInfo userInfo = new PojoUserInfo();
        userInfo.coin = PrefUtils.getInt(PrefConstants.UserInfo.COIN,0);
        userInfo.avatar = PrefUtils.getString(PrefConstants.UserInfo.USER_ICON_URL,"");
        userInfo.nick = PrefUtils.getString(PrefConstants.UserInfo.USER_NAME,"");
        userInfo.geo = PrefUtils.getString(PrefConstants.UserInfo.GEO,"");
        userInfo.level = PrefUtils.getString(PrefConstants.UserInfo.LEVEL,"");
        userInfo.coupon_count=PrefUtils.getInt(PrefConstants.UserInfo.COUPON_COUNT,0);
        userInfo.type=PrefUtils.getString(PrefConstants.UserInfo.TYPE, "");
        userInfo.bind_type=PrefUtils.getString(PrefConstants.UserInfo.BIND_TYPE, "");

        uploadUserInfoViews(userInfo);
    }

    private void saveUserInfo(PojoUserInfo userInfo) {
        UserTokenManager.getInstance(GlobalContext.get()).saveUserInfo(userInfo);
//        PrefUtils.putBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE,false);
        if(userInfo.coin>0&&!userInfo.is_finish_newbieguide){
            PrefUtils.putBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE,userInfo.is_finish_newbieguide);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        isLogined=(MainTabActivity)activity;
    }

    private String decodeAvatarURL(String avatar){
        try {
            return URLDecoder.decode(avatar,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return avatar;
    }

    public interface IsLogined {
        void chechLogin();
    }
}
