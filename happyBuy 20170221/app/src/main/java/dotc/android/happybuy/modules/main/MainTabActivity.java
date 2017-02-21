package dotc.android.happybuy.modules.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.update.util.StringUtil;
import com.stat.analytics.AnalyticsSdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoAwaitingAccept;
import dotc.android.happybuy.http.result.PojoAwaitingAcceptList;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.manage.InstalledApplicationManager;
import dotc.android.happybuy.modules.awarding.func.AwardingManager;
import dotc.android.happybuy.modules.classification.ClassificationFragment;
import dotc.android.happybuy.modules.coupon.func.SoonAvailableCouponTask;
import dotc.android.happybuy.modules.home.HomeFragment;
import dotc.android.happybuy.modules.home.fragment.ProductsFragment;
import dotc.android.happybuy.modules.login.LoginActivity;
import dotc.android.happybuy.modules.login.func.AccountHelper;
import dotc.android.happybuy.modules.main.base.BaseMainFragment;
import dotc.android.happybuy.modules.main.base.FragmentHost;
import dotc.android.happybuy.modules.main.guide.GuideController;
import dotc.android.happybuy.modules.main.update.CheckUpdateTask;
import dotc.android.happybuy.modules.main.widget.TabLayout;
import dotc.android.happybuy.modules.me.MeFragment;
import dotc.android.happybuy.modules.me.MeFragment.IsLogined;
import dotc.android.happybuy.modules.show.ShowFragment;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.push.DynamicTopicManager;
import dotc.android.happybuy.push.PushHelper;
import dotc.android.happybuy.push.PushLog;
import dotc.android.happybuy.push.PushMessageDispatcher;
import dotc.android.happybuy.push.TokenManager;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.RefreshLayout;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/8/18.
 */
public class MainTabActivity extends BaseActivity implements TabLayout.OnCheckedChangeListener, ProductsFragment.GuideCallBack, IsLogined {

    private static final Class<?>[] FRAGMENT_CLASS = {HomeFragment.class, ClassificationFragment.class, ShowFragment.class, MeFragment.class};
    private final String SAVED_STATE_INDEX = "saved_state_index";
    private static int mInstanceCount = 0;
    private TabLayout mTabLayout;
    private FrameLayout mContentLayout;
    private FragmentHost mFragmentHost;
    private int mCurrentIndex;

    private FrameLayout mFirstOpen;
    private View mRedDot;

    private GuideController mGuideController;

    private long mExitTime;
    public static final int TAB_INDEX_0 = 0x00;
    public static final int TAB_INDEX_1 = 0x01;
    public static final int TAB_INDEX_2 = 0x02;
    public static final int TAB_INDEX_3 = 0x03;
    public static final String EXTRA_TAB_INDEX = "extra_tab_index";
    public static final String EXTRA_TAB_ARGS = "extra_tab_args";
    public static final String EXTRA_FCM_FROM = "extra_fcm_from";
    public static final String EXTRA_FCM_DATA = "extra_fcm_data";
    public static final String HAVA_REQUEST_FINISH_STATUS = "hava_request_finish_status";
    private int mExtraTabIndex = -1;
    private Bundle mExtraArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        mInstanceCount++;
        obtainArgsFromIntent(getIntent());
        initUI();
        initFragment(savedInstanceState);
        startGCMService();
        checkUpdate();
        checkLineIsInstalled();
        checkSoonAvailableCoupons();
        AnalyticsSdk.getInstance(this).sendRealActive();
        mGuideController = new GuideController(this);
        if (!StringUtil.isEmpty(PrefUtils.getString(PrefConstants.UserInfo.UID, ""))) {
            getAwaitingAccept();
        }
    }

    private void getAwaitingAccept() {
        Map<String, Object> param = new HashMap<>();
        param.put("uid", PrefUtils.getString(PrefConstants.UserInfo.UID, ""));
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.AWAITINGACCEPT, param,
                new Network.JsonCallBack<PojoAwaitingAcceptList>() {
                    @Override
                    public void onSuccess(PojoAwaitingAcceptList pojoAwaitingAcceptList) {
                        if (pojoAwaitingAcceptList.count == 0) {
                            return;
                        }
                        if (!AppUtil.isActivityDestroyed(MainTabActivity.this)) {
                            if (pojoAwaitingAcceptList.list.size() > 0) {
                                PojoAwaitingAccept pojoAwaitingAccept = pojoAwaitingAcceptList.list.get(0);

                                AwardingManager.getInstance(MainTabActivity.this).remindUserShowAwardedDialog(MainTabActivity.this, pojoAwaitingAccept.product_id, pojoAwaitingAccept.product_name,
                                        pojoAwaitingAccept.default_image, pojoAwaitingAccept.product_item_id, pojoAwaitingAccept.product_item);
                            }

                        }
                    }

                    @Override
                    public void onFailed(int code, String message, Exception e) {
                        if (!AppUtil.isActivityDestroyed(MainTabActivity.this)) {
//                            ToastUtils.showShortToast(NewUserGuideActivity.this,
//                                    R.string.invite_code_enter_wrong);
                        }
                    }

                    @Override
                    public Class<PojoAwaitingAcceptList> getObjectClass() {
                        return PojoAwaitingAcceptList.class;
                    }
                });
    }

    private void obtainArgsFromIntent(Intent intent) {
        if (intent != null) {
            mExtraTabIndex = intent.getIntExtra(EXTRA_TAB_INDEX, 0);
            mExtraArgs = intent.getBundleExtra(EXTRA_TAB_ARGS);

            if (intent.hasExtra(EXTRA_FCM_FROM)) {
                Analytics.sendUIEvent(AnalyticsEvents.Other.Enter_ActivityMain_Noti, null, null);
                String from = intent.getStringExtra(EXTRA_FCM_FROM);
                HashMap<String, String> data = (HashMap<String, String>) intent.getSerializableExtra(EXTRA_FCM_DATA);
                new PushMessageDispatcher(this).dispatch(from, data);
                return;
            }
        }
        Analytics.sendUIEvent(AnalyticsEvents.Other.Enter_ActivityMain, null, null);
//        mTabLayout.triggerCheck();
    }

    private void initFragment(Bundle savedInstanceState) {
        mFragmentHost = new FragmentHost(this.getSupportFragmentManager(),
                R.id.layout_content, FRAGMENT_CLASS);
        HBLog.d(TAG + " initFragment " + savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(SAVED_STATE_INDEX);
            mFragmentHost.initWithSavedInstanceState(savedInstanceState, mCurrentIndex);
            mTabLayout.setCheck(mCurrentIndex);
        } else {
            if (mExtraTabIndex > -1) {
                mTabLayout.triggerCheck(mExtraTabIndex, mExtraArgs);
            } else {
                mTabLayout.triggerCheck(mCurrentIndex, null);
            }
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.hasExtra(EXTRA_TAB_INDEX)) {
            int tabIndex = intent.getIntExtra(EXTRA_TAB_INDEX, 0);
            if (intent.hasExtra(EXTRA_TAB_ARGS)) {
                mExtraArgs = intent.getBundleExtra(EXTRA_TAB_ARGS);
                mTabLayout.triggerCheck(tabIndex, mExtraArgs);
            } else {
                mTabLayout.triggerCheck(tabIndex);
            }

        }
    }

    public void switchTabLayout(int postion) {
        mTabLayout.triggerCheck(postion);
    }

    private void initUI() {
        mRedDot = findViewById(R.id.red_dot);
        mContentLayout = (FrameLayout) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setOnCheckedChangeListener(this);
        mFirstOpen = (FrameLayout) findViewById(R.id.first_open);
        if (AccountHelper.getInstance(this).isLogin()) {
            mRedDot.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGuideController.onDestroy();
        mInstanceCount--;
    }

    public static boolean isInstanceActive() {
        return mInstanceCount > 0;
    }

    @Override
    public void onCheckedChanged(TabLayout group, int position, Bundle args) {
        HBLog.d(TAG + " onCheckedChanged position:" + position + " " + mFragmentHost.getLastIndex());
        if (position == 3 && !isLogin()) {//
            startActivity(new Intent(this, LoginActivity.class));
            mCurrentIndex = mFragmentHost.getLastIndex();
            mTabLayout.setCheck(mCurrentIndex);
        } else {
            mFragmentHost.showFragment(position, args);
            mCurrentIndex = position;
        }
        switch (position) {
            case 0:
                Analytics.sendUIEvent(AnalyticsEvents.TabLayout.Click_Tab_Home, null, null);
                break;
            case 1:
                Analytics.sendUIEvent(AnalyticsEvents.TabLayout.Click_Tab_Classification, null, null);
                break;
            case 2:
                Analytics.sendUIEvent(AnalyticsEvents.TabLayout.Click_Tab_Show, null, null);
                break;
            case 3:
                Analytics.sendUIEvent(AnalyticsEvents.TabLayout.Click_Tab_Me, null, null);
                break;
        }

    }

    @Override
    public void onTabDoubleClick(TabLayout group, int position) {
        BaseMainFragment fragment = mFragmentHost.getCurrentFragment();
        if (fragment != null) {
            fragment.onTabDoubleClick(group.getChildAt(position));
        }
        HBLog.d(TAG + " onTabDoubleClick position:" + position);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mGuideController.onKeyDown(keyCode, event)) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseMainFragment fragment = mFragmentHost.getCurrentFragment();
            if (fragment != null && fragment.onKeyDown(keyCode, event)) {
                return true;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    ToastUtils.showShortToast(this, R.string.double_click_back);
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mGuideController.dispatchTouchEvent(ev)
                || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mGuideController.dispatchKeyEvent(event)
                || super.dispatchKeyEvent(event);
    }

    private void startGCMService() {
        if (checkPlayServices()) {
            DynamicTopicManager.getInstance(this).setGpServiceAvailable(true);
            String refreshedToken = TokenManager.get(this).getFirebaseToken();
            PushLog.logD(TAG, "startGCM " + refreshedToken);
            if (refreshedToken == null) {
                return;
            }
            PushHelper.setGCMToken(this, refreshedToken);
//            FirebaseMessaging.getInstance().subscribeToTopic(PushConstance.getTopicGlobal());
//            AutologManager.subscribeUserDefinedTopic(GlobalContext.get(),PushConstance.getTopicGlobal());
        }
    }


    //test wake lock permission
    private void testPermission() {
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.WAKE_LOCK", "dotc.android.happybuy"));
        if (!permission) {
            Toast.makeText(this, "no permission", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkPlayServices() {
        try {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog(this, resultCode, 9000)
                            .show();
                }
                return false;
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_STATE_INDEX, mCurrentIndex);
        HBLog.d(TAG + " onSaveInstanceState mCurrentIndex:" + mCurrentIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            HBLog.e(TAG + " startActivityForResult " + e.getMessage());
        }
    }


    @Override
    public void onFirstGuide(RefreshLayout refreshLayout, ListView listView, List<PojoProduct> productList) {
        if (isLogin() && !AppUtil.isActivityDestroyed(this)) {
            mGuideController.showIfNeeded(refreshLayout, listView, productList);
        }

//        if (isLogin() && !PrefUtils.getBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE, true)
//                && !AppUtil.isActivityDestroyed(this)) {
//            Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Start_Guide, null, null);
//
//
////            UserTipsFragment userTipsFragment = UserTipsFragment.newInstance(x, y, pojoProduct);
////            this.getSupportFragmentManager().beginTransaction().replace(R.id.first_open, userTipsFragment).commitAllowingStateLoss();
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void chechLogin() {
        if (isLogin()) {
            mRedDot.setVisibility(View.GONE);
        }
    }

    private void checkUpdate() {
        new CheckUpdateTask().execute(GlobalContext.get());
    }

    private void checkSoonAvailableCoupons() {
        new SoonAvailableCouponTask().check();
    }


    /**
     * cys 2016年12月28日 17:05:49
     * 汇报Line是否安装，一天最多一次
     */

    private void checkLineIsInstalled() {
//        long l = PrefUtils.getLong(this, PrefConstants.AppCheckTime.LINE);
//        if(!DateUtil.checkTimeIsToday(l)||l==-1){
        InstalledApplicationManager.getInstance(this).checkPackageName("jp.naver.line.android", new InstalledApplicationManager.OnErgodicListener() {
            @Override
            public void ergodicDone(int isInstalled) {
                //发送汇报
                Analytics.sendEventOncePerDayInternalA(GlobalContext.get(), AnalyticsEvents.AppInfo.Line_Install, null, Long.valueOf(isInstalled));
            }
        }).start();
//        };
    }


}
