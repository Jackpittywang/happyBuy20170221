package dotc.android.happybuy.modules.userprofile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoUserInfo;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.ui.adapter.TabFragmentAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.RefreshLayout;
import dotc.android.happybuy.uibase.component.TabListViewPager;
import dotc.android.happybuy.modules.userprofile.fragment.AllParticipateFragment;
import dotc.android.happybuy.modules.userprofile.fragment.ShareRecordFragment;
import dotc.android.happybuy.modules.userprofile.fragment.WinRecordFragment;
import dotc.android.happybuy.uibase.widget.RepackTabLayout;

/**
 * 用户的个人主页信息
 *
 */
public class UserProfileActivity extends BaseActivity implements View.OnClickListener,RefreshLayout.OnRefreshListener{

    public static final String EXTRA_TAB = "extra_tab";
    public static final String EXTRA_UID = "extra_uid";
    public static final String EXTRA_NICKNAME = "extra_nickname";

    public static final int TAB_INDEX_0 = 0x00;
    public static final int TAB_INDEX_1 = 0x01;
    public static final int TAB_INDEX_2 = 0x02;

    private int mExtraTabIndex;
    private String mExtraUid;
    private String mExtraNickname;

    private HBToolbar mToolbar;
    private RefreshLayout mParentLayout;
    private ImageView mImgHead;
//    private TextView mTVUserName;

    private TabListViewPager mViewPager;

    private TabFragmentAdapter mUserCenterAdapter;
    private List<BaseTabFragment> mPages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        readExtraFromIntent();
        initActionbar();
        initViews();
        initData();
    }

    private void readExtraFromIntent(){
        mExtraTabIndex = getIntent().getIntExtra(EXTRA_TAB,TAB_INDEX_0);
        mExtraUid = getIntent().getStringExtra(EXTRA_UID);
        mExtraNickname = getIntent().getStringExtra(EXTRA_NICKNAME);
        HBLog.d(TAG+" readExtraFromIntent mExtraUid:"+mExtraUid);
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(mExtraNickname);
        mToolbar.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        mParentLayout = (RefreshLayout) findViewById(R.id.layout_refresh);
        mImgHead = (ImageView) findViewById(R.id.icon_user);
//        mTVUserName = (TextView) findViewById(R.id.tv_name);
        mViewPager = (TabListViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        final RepackTabLayout tabLayout = (RepackTabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //设置用户头像的边框颜色
//        mImgHead.setBorderColor(ContextCompat.getColor(this, R.color.white));

        mImgHead.setOnClickListener(this);
        mParentLayout.addOnRefreshListener(this);
    }

    private void setupViewPager(TabListViewPager mViewPager) {
        mPages.add(AllParticipateFragment.newInstance(mExtraUid));
        mPages.add(WinRecordFragment.newInstance(mExtraUid));
        mPages.add(ShareRecordFragment.newInstance(mExtraUid));
        mUserCenterAdapter = new TabFragmentAdapter(getSupportFragmentManager(), mPages);
        String[] titles = getResources().getStringArray(R.array.user_center_tab);
        mUserCenterAdapter.setPageTitles(titles);
        mViewPager.setTabAdapter(mUserCenterAdapter);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initData() {
        mViewPager.setCurrentItem(mExtraTabIndex,false);
        doRefreshInfoIfNecessary();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pay:
                break;
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshView) {
        doRefreshInfoIfNecessary();
    }

    private void doRefreshInfoIfNecessary(){
        Map<String,Object> param = new HashMap<>();
        param.put("from_uid", mExtraUid);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.USER_INFO, param, new Network.JsonCallBack<PojoUserInfo>() {
            @Override
            public void onSuccess(PojoUserInfo userInfo) {
                if (userInfo != null) {
                    if (!isFinishing()) {
                        updateUserInfoViews(userInfo);
                    }
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);

            }

            @Override
            public Class<PojoUserInfo> getObjectClass() {
                return PojoUserInfo.class;
            }
        });
    }

    private void updateUserInfoViews(PojoUserInfo userInfo){
        HBLog.d(TAG+" updateUserInfoViews "+userInfo);
//        this.mUserInfo = userInfo;
//        mTVUserName.setText(userInfo.nick);
        Glide.with(GlobalContext.get()).load(userInfo.avatar).placeholder(R.drawable.pic_circle_portrait_placeholder).crossFade().into(mImgHead);
    }

}
