package dotc.android.happybuy.modules.boutique;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.http.result.PojoProductList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.part.PartCallBack;
import dotc.android.happybuy.modules.part.PartObject;
import dotc.android.happybuy.ui.adapter.ProductAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.listview.ListFooterView;
import dotc.android.happybuy.uibase.listview.RefreshListView;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/30.
 * 精品
 */
public class BoutiqueActivity extends BaseActivity implements ListFooterView.OnRetryClickListener,
        PartCallBack,ProductAdapter.OnProductPartListener,AbsListView.OnScrollListener{

//    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    private final int ONCE_LOAD_SIZE = 12;

//    private PojoCategory mExtraPojoCategory;
    private String mExtraCategoryName;
    private String mExtraCategoryId;
    private HBToolbar mToolbar;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View mEmptyView;
    private View mLoadingView;
    private View mContentView;

    private RefreshListView mRefreshListView;
    private ListView mListView;
    private ProductAdapter mProductAdapter;
    private PartFragment mPartFragment;

    private ListFooterView mListFooterView;
    private boolean mLoaderMoreDoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_goods);
        readExtraFromIntent();
        initActionbar();
        initUI();
        doFirstLoadingTask();
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(mExtraCategoryName);
        mToolbar.setDisplayHomeAsUpEnabled(true);
//        mToolbar.setRightItem(R.mipmap.ic_home, new HBToolbar.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view) {
//                Intent intent = new Intent(BoutiqueActivity.this,MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mToolbar.setTitle(mExtraCategory.name);
//        getSupportActionBar().setTitle(mExtraPojoCategory.name);
    }

    private void readExtraFromIntent(){
//        mExtraPojoCategory = (PojoCategory) getIntent().getSerializableExtra(EXTRA_CATEGORY);
        mExtraCategoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        mExtraCategoryId = getIntent().getStringExtra(EXTRA_CATEGORY_ID);
    }

    private void initUI() {
        mNetworkErrorLayout = (NetworkErrorLayout) findViewById(R.id.layout_network_error);
        mLoadingView = findViewById(R.id.layout_loading);
        mContentView = findViewById(R.id.layout_content);
        mEmptyView = findViewById(R.id.layout_empty_view);
        mRefreshListView = (RefreshListView) findViewById(R.id.refresh_listview);
        mListView = (ListView) findViewById(R.id.listview);
        mListFooterView = new ListFooterView(this);
        mListFooterView.setOnRetryClickListener(this);
        mListView.setOnScrollListener(this);
        mProductAdapter = new ProductAdapter(this, "boutique",this);
        mListView.addFooterView(mListFooterView);
        mListView.setAdapter(mProductAdapter);

        mRefreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshListView refreshView) {
                doRefreshTask(false);
            }
        });
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFirstLoadingTask();
            }
        });
    }

    @Override
    public void onProductPartClick(View view, PojoProduct product) {
        mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_OTHER,product,this);
        mPartFragment.show(getSupportFragmentManager());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPartFragment!=null&&mPartFragment.onBackPressed()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doFirstLoadingTask(){
        Map<String,Object> param = new HashMap<>();
        param.put("tag", mExtraCategoryId);
        param.put("offset", "0");
        param.put("limit", ""+ONCE_LOAD_SIZE);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRODUCT_LIST, param, new Network.JsonCallBack<PojoProductList>() {
            @Override
            public void onSuccess(PojoProductList list) {
                HBLog.d(TAG + " onSuccess " + list);
                mContentView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                if (list.productList.size() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mProductAdapter.updateList(list.productList);
                    updateFooterViewWhenOK(list.productList.size());
                    mEmptyView.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
//                mListView.onRefreshComplete();
            }

            @Override
            public Class<PojoProductList> getObjectClass() {
                return PojoProductList.class;
            }
        });
        mNetworkErrorLayout.setVisibility(View.GONE);
        mContentView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void doRefreshTask(boolean needRefreshUI){
        Map<String,Object> param = new HashMap<>();
        param.put("tag", mExtraCategoryId);
        param.put("offset", "0");
        param.put("limit", ""+ONCE_LOAD_SIZE);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRODUCT_LIST, param, new Network.JsonCallBack<PojoProductList>() {
            @Override
            public void onSuccess(PojoProductList list) {
                HBLog.d(TAG + " onSuccess " + list);
                mRefreshListView.onRefreshComplete();
                if (list.productList.size() > 0) {
                    mProductAdapter.updateList(list.productList);
                    updateFooterViewWhenOK(list.productList.size());
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                mRefreshListView.onRefreshComplete();
                ToastUtils.showLongToast(GlobalContext.get(), R.string.load_failed);
            }

            @Override
            public Class<PojoProductList> getObjectClass() {
                return PojoProductList.class;
            }
        });
//        if(needRefreshUI){
//            mListView.setRefreshing();
//        }
    }

    private void doLoaderMoreTask(){
//        PojoProduct pojoProduct =  mProductAdapter.getLastItem();
        Map<String,Object> param = new HashMap<>();
        param.put("tag", mExtraCategoryId);
        param.put("offset", String.valueOf(mProductAdapter.getListCount()));
        param.put("limit", ""+ONCE_LOAD_SIZE);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRODUCT_LIST, param, new Network.JsonCallBack<PojoProductList>() {
            @Override
            public void onSuccess(PojoProductList list) {
                HBLog.d(TAG + " onSuccess " + list);
                mProductAdapter.appendList(list.productList);
                updateFooterViewWhenOK(list.productList.size());
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                ToastUtils.showLongToast(GlobalContext.get(),R.string.load_failed);
                mListFooterView.setState(ListFooterView.State.RETRY);
            }

            @Override
            public Class<PojoProductList> getObjectClass() {
                return PojoProductList.class;
            }
        });
        mListFooterView.setState(ListFooterView.State.LOADING);
    }

    public void onRetryClick(View view) {
        tryLoaderMore();
    }

    private void tryLoaderMore(){
        if(!mLoaderMoreDoing&&mListFooterView.getState() == ListFooterView.State.LOADING){
            doLoaderMoreTask();
            mLoaderMoreDoing = true;
        }
    }

    private void updateFooterViewWhenOK(int onceListSize){
        mLoaderMoreDoing = false;
        mLoaderMoreDoing = false;
        if(mProductAdapter.getCount()<ONCE_LOAD_SIZE){
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
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem+visibleItemCount==totalItemCount-1){
            tryLoaderMore();
        }
    }
}
