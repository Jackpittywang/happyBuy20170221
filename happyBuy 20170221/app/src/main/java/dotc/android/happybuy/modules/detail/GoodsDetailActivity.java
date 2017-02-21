package dotc.android.happybuy.modules.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoPartRecorder;
import dotc.android.happybuy.http.result.PojoPartRecorderList;
import dotc.android.happybuy.http.result.PojoProductDetail;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.guide.GuideController;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.detail.adapter.GoodsDetailAdapter;
import dotc.android.happybuy.modules.part.PartCallBack;
import dotc.android.happybuy.modules.part.PartObject;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.listview.ListFooterView;
import dotc.android.happybuy.uibase.listview.RefreshListView;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.modules.detail.listheader.GoodsDetailListHeader;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/3/29.
 */
public class GoodsDetailActivity extends BaseActivity implements ListFooterView.OnRetryClickListener,
        PartCallBack,GoodsDetailListHeader.OnRefreshListener,GoodsDetailListHeader.OnParticipateListener,
        AbsListView.OnScrollListener{

    public final static String EXTRA_ACTIVITY_FROM = "extra_activity_from";
    public static final String ACTIVITY_FROM_HOME = "Home";
//    public static final String ACTIVITY_FROM_SEARCH = "Search";
    public static final String ACTIVITY_FROM_CLASSIFICATION = "Classification";
    public static final String ACTIVITY_FROM_USER_CENTER = "UserCenter";
    public static final String ACTIVITY_FROM_ACTIVE = "Active";

    public static final String USER_GUIDE = "user_guide";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_PRODUCT_ITEM_ID = "extra_product_item_id";
    public static final String SHOW_ORDER = "show_order";
    private final int ONCE_LOAD_SIZE = 12;
    public static int mInstanceCount;

    private NetworkErrorLayout mNetworkErrorLayout;
    private View mLoadingView;
    private View mContentView;
    private View mNotFoundProductView;
    private HBToolbar mToolbar;
    private View mPartLayout;
    private RefreshListView mRefreshListView;
    private ListView mListView;
    private RelativeLayout layoutNewPeriod;
    private Button btnNextPeriod;
//    private PullToRefreshListView mListView;//PullToRefreshListView
    private ListFooterView mListFooterView;
    private boolean mLoaderMoreDoing;
    private boolean mIsAddFooterView;
    private GoodsDetailListHeader mGoodsDetailListHeader;
    private GoodsDetailAdapter mGoodsDetailAdapter;

    private PojoProductDetail mPojoProductDetail;
    private String mLastItemSortKey;
    private PartFragment mPartFragment;

    private String mExtraFrom;
    private String mExtraProductId;
    private String mExtraProductItemId;

    private GuideController mGuideController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstanceCount++;
        setContentView(R.layout.activity_goods_detail);
        initActionbar();
        readExtraFromIntent();
        initUI();
        setViewsListener();
        doFirstLoadingInfo();
        mGuideController = new GuideController(this);
        if(TextUtils.isEmpty(mExtraProductItemId)){
            Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Show_Latest_Details, mExtraFrom, parseToLong(mExtraProductId));
        } else {
            Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Show_Details, mExtraFrom, parseToLong(mExtraProductItemId));
        }
       /* mBetNow.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBetNow.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });*/
    }

    private long parseToLong(String value){
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e){}
        return -1l;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mGuideController.dispatchKeyEvent(event)
                || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mGuideController.dispatchTouchEvent(ev)
                || super.dispatchTouchEvent(ev);
    }

    private void readExtraFromIntent(){
        mExtraFrom = getIntent().getStringExtra(EXTRA_ACTIVITY_FROM);
        mExtraProductId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        mExtraProductItemId = getIntent().getStringExtra(EXTRA_PRODUCT_ITEM_ID);
        HBLog.d(TAG + " readExtraFromIntent " + mExtraProductId + " " + mExtraProductItemId);
        Log.d(TAG, "detail extra " + mExtraProductId + " " + mExtraProductItemId);
        if(mExtraProductId == null){
            mExtraProductId = "";
        }
        if(mExtraProductItemId == null){
            mExtraProductItemId = "";
        }
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_goods_detail);
        mToolbar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setRightItem(R.drawable.ic_home, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                Intent intent = new Intent(GoodsDetailActivity.this, MainTabActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        mToolbar.setLeftItem(new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                handlePopActivtity();
            }
        });
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initUI() {
        mPartLayout = findViewById(R.id.layout_part);
        mNetworkErrorLayout = (NetworkErrorLayout) findViewById(R.id.layout_network_error);
        mLoadingView = findViewById(R.id.layout_loading);
        mContentView = findViewById(R.id.layout_content);
        mNotFoundProductView = findViewById(R.id.textview_product_not_found);
        btnNextPeriod = (Button)findViewById(R.id.btn_new_period);

        layoutNewPeriod = (RelativeLayout)findViewById(R.id.layout_new_period);

        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_listview);
        mListView = (ListView) findViewById(R.id.listview);
        mGoodsDetailListHeader = new GoodsDetailListHeader(this);
        mGoodsDetailListHeader.setOnRefreshListener(this);
        mGoodsDetailListHeader.setOnParticipateListener(this);
        mListFooterView = new ListFooterView(this);
        mListView.addHeaderView(mGoodsDetailListHeader);
        mListView.addFooterView(mListFooterView);

        mGoodsDetailAdapter = new GoodsDetailAdapter(GoodsDetailActivity.this);
//        mGoodsDetailListHeader.setFillData(goodsDetail);
        mListView.setAdapter(mGoodsDetailAdapter);
    }

    private void setViewsListener(){
        mListFooterView.setOnRetryClickListener(this);
        mRefreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshListView refreshView) {
                doRefreshTask();
            }
        });
        mListView.setOnScrollListener(this);
        mPartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.ProductDetail.Click_BuyNow, "ProductDetails", null);
                mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_OTHER, mPojoProductDetail.product, GoodsDetailActivity.this);
                mPartFragment.show(getSupportFragmentManager());
            }
        });
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFirstLoadingInfo();
            }
        });
        btnNextPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoodsDetailActivity.this, GoodsDetailActivity.class);
                intent.putExtra(EXTRA_PRODUCT_ID, mExtraProductId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGuideController.onDestroy();
        mInstanceCount--;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPartFragment!=null&&mPartFragment.onBackPressed()) {
                return true;
            }
        }
        if(mGuideController.onKeyDown(keyCode,event)){
            return true;
        }

        if(keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0){
            handlePopActivtity();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handlePopActivtity(){
        if(getMyApplication().getStackTaskCount()==1){
            Intent intent = new Intent(GoodsDetailActivity.this, MainTabActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void doFirstLoadingInfo(){
        Map<String,Object> param = new HashMap<>();
        param.put("productId", mExtraProductId);//"11110001"
        param.put("productItemId", mExtraProductItemId);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.GOODS_DETAIL, param, new Network.JsonCallBack<PojoProductDetail>() {
            @Override
            public void onSuccess(PojoProductDetail goodsDetail) {
                HBLog.d(TAG + " onSuccess " + goodsDetail);
                if (!AppUtil.isActivityDestroyed(GoodsDetailActivity.this)) {
                    mPojoProductDetail = goodsDetail;
                    mLastItemSortKey = mPojoProductDetail.latestParticipateRecords.last_numb;
                    mGoodsDetailListHeader.setFillData(goodsDetail);
                    mGoodsDetailAdapter.updateList(goodsDetail.latestParticipateRecords.list);
                    mLoadingView.setVisibility(View.GONE);
                    mContentView.setVisibility(View.VISIBLE);
                    updateFooterViewWhenOK(goodsDetail.latestParticipateRecords.list.size());
                    updatePartView();
                    HBLog.i(TAG+" latestProductItemId "+goodsDetail.product.latestProductItemId+" mExtraProductItemId "+mExtraProductItemId);
                    showJoinNewPeriod(goodsDetail);
//                    mBetNow.getLocationOnScreen(location);
                    mGuideController.showIfNeeded(goodsDetail);
//                    isUserGuild();

                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                if (code == 2||code == 3) {//product not found
                    mNotFoundProductView.setVisibility(View.VISIBLE);
                } else {
                    mNetworkErrorLayout.setVisibility(View.VISIBLE);
                }
                mLoadingView.setVisibility(View.GONE);
            }

            @Override
            public Class<PojoProductDetail> getObjectClass() {
                return PojoProductDetail.class;
            }
        });
        mLoadingView.setVisibility(View.VISIBLE);
        mContentView.setVisibility(View.INVISIBLE);
        mNetworkErrorLayout.setVisibility(View.GONE);

    }

    private void doRefreshTask(){
        final String productItemId = mPojoProductDetail!=null?mPojoProductDetail.product.productItemId:mExtraProductItemId;
        Map<String,Object> param = new HashMap<>();
        param.put("productId", mExtraProductId);//"11110001"
        param.put("productItemId", productItemId);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.GOODS_DETAIL, param, new Network.JsonCallBack<PojoProductDetail>() {
            @Override
            public void onSuccess(PojoProductDetail goodsDetail) {
                HBLog.d(TAG + " onSuccess " + goodsDetail);
                if (!AppUtil.isActivityDestroyed(GoodsDetailActivity.this)) {
                    mPojoProductDetail = goodsDetail;
                    mLastItemSortKey = mPojoProductDetail.latestParticipateRecords.last_numb;
                    mGoodsDetailListHeader.setFillData(goodsDetail);
                    mGoodsDetailAdapter.updateList(goodsDetail.latestParticipateRecords.list);
                    mRefreshListView.onRefreshComplete();
                    updateFooterViewWhenOK(goodsDetail.latestParticipateRecords.list.size());
                    updatePartView();
                    showJoinNewPeriod(goodsDetail);
                    if(mScroolToFirstItem){
                        mScroolToFirstItem = false;
                        if(mGoodsDetailAdapter.getCount()>0){
                            mListView.setSelection(1);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                mRefreshListView.onRefreshComplete();

            }

            @Override
            public Class<PojoProductDetail> getObjectClass() {
                return PojoProductDetail.class;
            }
        });
    }


    private void showJoinNewPeriod(PojoProductDetail goodsDetail){
        //是否显示参与新一期
        if (!TextUtils.isEmpty(mExtraProductItemId)
//                && !mExtraProductItemId.equals("")
                && !TextUtils.isEmpty(goodsDetail.product.latestProductItemId)
                && !goodsDetail.product.latestProductItemId.equals("0")
                && !goodsDetail.product.latestProductItemId.equals(mExtraProductItemId)
                ){
            layoutNewPeriod.setVisibility(View.VISIBLE);
        }
        else if(goodsDetail.product.status== HttpProtocol.PRODUCT_STATE.AWARDING||goodsDetail.product.status== HttpProtocol.PRODUCT_STATE.AWARD){
            layoutNewPeriod.setVisibility(View.VISIBLE);
        }
        else{
            layoutNewPeriod.setVisibility(View.GONE);
        }
    }

    private void doLoadMoreTask(){
        HBLog.d(TAG + " doLoadMoreTask ");
        PojoPartRecorder pojoPartRecorder =  mGoodsDetailAdapter.getLastItem();
        Map<String,Object> param = new HashMap<>();
        param.put("product_item_id", mPojoProductDetail.product.productItemId);
        param.put("count", ""+ONCE_LOAD_SIZE);
        param.put("last_numb", String.valueOf(mLastItemSortKey));
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRODUCT_PARTICIPATE_LIST, param, new Network.JsonCallBack<PojoPartRecorderList>() {
            @Override
            public void onSuccess(PojoPartRecorderList goodsDetail) {
                mLastItemSortKey = goodsDetail.last_numb;
                mGoodsDetailAdapter.appendList(goodsDetail.list);
                updateFooterViewWhenOK(goodsDetail.list.size());
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                mListFooterView.setState(ListFooterView.State.RETRY);
            }

            @Override
            public Class<PojoPartRecorderList> getObjectClass() {
                return PojoPartRecorderList.class;
            }
        });
        mListFooterView.setState(ListFooterView.State.LOADING);
    }

    public void onRetryClick(View view) {
        tryLoaderMore();
    }

    private void tryLoaderMore(){
        if(!mLoaderMoreDoing&&mListFooterView.getState() == ListFooterView.State.LOAD){
            doLoadMoreTask();
            mLoaderMoreDoing = true;
        }
    }

    private void updatePartView(){
        if(mPojoProductDetail.product.remainTimes>0){
            mPartLayout.setVisibility(View.VISIBLE);
        } else {
            mPartLayout.setVisibility(View.GONE);
        }
    }

    private void updateFooterViewWhenOK(int onceListSize){
        mLoaderMoreDoing = false;
        if(mGoodsDetailAdapter.getCount()<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.EMPTY);
        } else if(onceListSize<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.END);
        } else {
            mListFooterView.setState(ListFooterView.State.LOAD);
        }
    }

    @Override
    public void onPartCallBack(boolean paySuceess,PartObject partObject) {
//        doRefreshTask();
        if(paySuceess){
            mRefreshListView.startRefreshing();
        }
    }

    @Override
    public void onStartRefreshUI() {
        mRefreshListView.startRefreshing();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem+visibleItemCount==totalItemCount-1){
            tryLoaderMore();
        }
    }

    @Override
    public void onParticipate(View view) {
        mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_OTHER,mPojoProductDetail.product,this);
        mPartFragment.show(getSupportFragmentManager());
    }

    private boolean mScroolToFirstItem;

    public void refreshingAndScrollToFirst(){
        mScroolToFirstItem = true;
        mRefreshListView.startRefreshing();

    }
}
