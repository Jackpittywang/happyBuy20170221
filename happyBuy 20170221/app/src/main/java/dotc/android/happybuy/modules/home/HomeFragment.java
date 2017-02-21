package dotc.android.happybuy.modules.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.HomeActive;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoBanner;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.home.controller.BannerControl;
import dotc.android.happybuy.modules.home.controller.NoticeLooperControl;
import dotc.android.happybuy.modules.home.fragment.ProductsFragment;
import dotc.android.happybuy.modules.home.widget.PortalBannerContainer;
import dotc.android.happybuy.modules.home.widget.PortalButtonContainer;
import dotc.android.happybuy.modules.main.base.BaseMainFragment;
import dotc.android.happybuy.modules.message.MessageActivity;
import dotc.android.happybuy.modules.search.SearchActivity;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.ui.adapter.TabFragmentAdapter;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.component.RefreshLayout;
import dotc.android.happybuy.uibase.component.TabListViewPager;
import dotc.android.happybuy.uibase.widget.RepackTabLayout;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/8/18.
 */
public class HomeFragment extends BaseMainFragment implements RepackTabLayout.OnTabSelectedListener,RefreshLayout.OnRefreshListener {
    public static String KEY_FRIST_RUN = "frist_run_key";
    public final static String EXTRA_TYPE = "extra_type";
    public final static String EXTRA_INDEX = "extra_index";
    public static final int TAB_INDEX_0 = 0x00;
    public static final int TAB_INDEX_1 = 0x01;

    private RefreshLayout mParentLayout;
    private TabListViewPager mViewPager;
    private RepackTabLayout mTabLayout;

    private TabFragmentAdapter productsFragmentAdapter;

    private PortalButtonContainer mPortalButtonContainer;
    private PortalBannerContainer mPortalBannerContainer;

    private TextView mViewpagerIndicator1;
    private TextView mViewpagerIndicator2;

    private BannerControl mBannerControl;
    private NoticeLooperControl mNoticeLooperControl;
    private int mExtraTabIndex = -1;
    private boolean isfrist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            mExtraTabIndex = getArguments().getInt(EXTRA_INDEX);
        }
    }

    @Override
    public void onNewIntent(Bundle argument) {
        super.onNewIntent(argument);
        if(argument!=null){
            mExtraTabIndex = argument.getInt(EXTRA_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mTabLayout = (RepackTabLayout) view.findViewById(R.id.layout_tab);
        mViewPager = (TabListViewPager) view.findViewById(R.id.viewpager);
        mParentLayout = (RefreshLayout) view.findViewById(R.id.layout_refresh);
        mViewpagerIndicator1 = (TextView) view.findViewById(R.id.viewpager_indicator_1);
        mViewpagerIndicator2 = (TextView) view.findViewById(R.id.viewpager_indicator_2);
        mPortalButtonContainer = (PortalButtonContainer) view.findViewById(R.id.portal_button_container);
        mPortalBannerContainer = (PortalBannerContainer) view.findViewById(R.id.portal_banner_container);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isfrist = PrefUtils.getBoolean(GlobalContext.get(),KEY_FRIST_RUN,true);
        setListeners();
        initData();
        loadBanner();
        checkUser();
        Analytics.sendUIEvent(AnalyticsEvents.MainTab.Home_Page_Show, null,null);
    }
    private void checkUser(){
        if(isfrist){
            Analytics.sendUIEvent(AnalyticsEvents.LoginGuide.Show_Newuser_Dialog, null, null);
            /*Intent intent = new Intent(getActivity(),NewUserGuideActivity.class);
            intent.putExtra(NewUserGuideActivity.START_TYPE,NewUserGuideActivity.TYPE_0);
            startActivity(intent);*/
            Intent exit = new Intent(getActivity(),NewUserGuideActivity.class);
            exit.putExtra(NewUserGuideActivity.START_TYPE,NewUserGuideActivity.TYPE_2);
            startActivity(exit);
            PrefUtils.putBoolean(KEY_FRIST_RUN,false);
        }
    }

    private void setListeners() {
        mParentLayout.addOnRefreshListener(this);
        getView().findViewById(R.id.layout_search).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        getView().findViewById(R.id.imageview_news).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                mNoticeLooperControl.loadNotics();
                HBLog.d(TAG,"-------"+AbConfigManager.getInstance(getActivity()).getConfig().guess_you_like);
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initData() {
        mBannerControl = new BannerControl(getView());
        mNoticeLooperControl = new NoticeLooperControl(this);
        List<BaseTabFragment> pages = new ArrayList<>();
        BaseTabFragment productsFragment1 = ProductsFragment.newInstance(ProductsFragment.TYPE_ALL);
        BaseTabFragment productsFragment2 = ProductsFragment.newInstance(ProductsFragment.TYPE_TEN);
        pages.add(productsFragment1);
        pages.add(productsFragment2);
        productsFragmentAdapter = new TabFragmentAdapter(getChildFragmentManager(), pages);
        String country=AppUtil.getMetaData(getActivity(),"country");
        if(country.equals("th")){
            String[] titles = new String[]{getString(R.string.main_tab_all), getString(R.string.main_tab_tens)};
            productsFragmentAdapter.setPageTitles(titles);
        }else if(country.equals("vn")){
            String[] titles = new String[]{getString(R.string.main_tab_all), getString(R.string.main_tab_tens_for_vn)};
            productsFragmentAdapter.setPageTitles(titles);
        }
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setTabAdapter(productsFragmentAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(RepackTabLayout.MODE_FIXED);

        mTabLayout.setOnTabSelectedListener(this);
//

        HomeActive homeInfo= AbConfigManager.getInstance(getActivity()).getConfig().home_active;
        mPortalButtonContainer.setItem(homeInfo.portal_button);
        mPortalBannerContainer.setItem(homeInfo.portal_banner,homeInfo.banner_width,homeInfo.banner_height);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBannerControl.resume();
        mNoticeLooperControl.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBannerControl.pause();
        mNoticeLooperControl.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBannerControl.destroy();
        mNoticeLooperControl.destroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        ProductsFragment productsFragment = (ProductsFragment) productsFragmentAdapter.getCurrentFragment();
        HBLog.d(TAG + " onKeyDown " + productsFragment);
        return productsFragment != null && productsFragment.onBackPressed();
    }

    @Override
    public void onTabSelected(RepackTabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
        showIndicator(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                Analytics.sendUIEvent(AnalyticsEvents.Tab.Click_Tab_All, null, null);
                break;
            case 1:
                Analytics.sendUIEvent(AnalyticsEvents.Tab.Click_Tab_Ten, null, null);
                break;
        }
    }

    @Override
    public void onTabUnselected(RepackTabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(RepackTabLayout.Tab tab) {
    }

    public void loadBanner() {
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.MAIN_HEADER, null, new Network.JsonCallBack<PojoBanner>() {
            @Override
            public void onSuccess(PojoBanner pojoAds) {
//                HBLog.d(TAG + " loadBanner onSuccess " + pojoAds);
                if(!isRemoving()&&!isDetached()){
                    mBannerControl.updateBannerAdapter(pojoAds.adInfos);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " loadBanner onFailed " + code + " " + message + " " + e);
                ToastUtils.showLongToast(GlobalContext.get(), R.string.network_exception);
            }

            @Override
            public Class<PojoBanner> getObjectClass() {
                return PojoBanner.class;
            }
        });
    }

    private void showIndicator(int position){
        switch (position) {
            case 0:
                mViewpagerIndicator1.setVisibility(View.VISIBLE);
                mViewpagerIndicator2.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mViewpagerIndicator1.setVisibility(View.INVISIBLE);
                mViewpagerIndicator2.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onRefresh(RefreshLayout refreshView) {
        loadBanner();
    }

}