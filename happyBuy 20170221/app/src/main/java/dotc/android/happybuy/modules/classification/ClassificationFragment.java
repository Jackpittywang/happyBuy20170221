package dotc.android.happybuy.modules.classification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.ConfigManager;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoCategory;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.http.result.PojoProductList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.classification.adapter.ProductClassificationAdapter;
import dotc.android.happybuy.modules.main.base.BaseMainFragment;
import dotc.android.happybuy.modules.part.PartCallBack;
import dotc.android.happybuy.modules.part.PartObject;
import dotc.android.happybuy.modules.search.SearchActivity;
import dotc.android.happybuy.ui.adapter.CategoryListAdapter;
import dotc.android.happybuy.uibase.listview.ListFooterView;
import dotc.android.happybuy.uibase.listview.RefreshListView;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/8/22.
 */
public class ClassificationFragment extends BaseMainFragment implements ListFooterView.OnRetryClickListener,ProductClassificationAdapter.OnProductPartListener,
        PartCallBack,AbsListView.OnScrollListener{
    private ListView mCategorylistView;
    private ListView mClassificationlistView;
    private CategoryListAdapter categoryListAdapter;
    private ProductClassificationAdapter mCategoryGoodsAdapter;

    private PartFragment mPartFragment;
    private RefreshListView mRefreshListView;
    private String mExtraCategoryId;
    private ListFooterView mListFooterView;

    private boolean mLoaderMoreDoing;

    private View mEmptyView;
    private View mLoadingView;
    private View mContentView;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View mSearch;

    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    private final int ONCE_LOAD_SIZE = 12;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classification, container, false);

        mNetworkErrorLayout = (NetworkErrorLayout) view.findViewById(R.id.layout_network_error);
        mLoadingView = view.findViewById(R.id.layout_loading);
        mContentView = view.findViewById(R.id.layout_content);
        mEmptyView = view.findViewById(R.id.layout_empty_view);
        mSearch = view.findViewById(R.id.ll_search);


        mCategorylistView = (ListView)view.findViewById(R.id.category_list);
        mRefreshListView = (RefreshListView) view.findViewById(R.id.refresh_listview);
        mClassificationlistView = (ListView)view.findViewById(R.id.lv_classification);
        categoryListAdapter = new CategoryListAdapter(getActivity());
        mCategorylistView.setAdapter(categoryListAdapter);

        mClassificationlistView.setOnScrollListener(this);
        mCategoryGoodsAdapter = new ProductClassificationAdapter(getActivity(), "other",this);
        mListFooterView = new ListFooterView(getContext());
        mListFooterView.setOnRetryClickListener(this);

        mClassificationlistView.addFooterView(mListFooterView);
        mClassificationlistView.setAdapter(mCategoryGoodsAdapter);

        mRefreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshListView refreshView) {
                doFirstLoadingTask();
            }
        });
        getCategorys();
        setListeners();
        doFirstLoadingTask();
        return view;
    }

    private void getCategorys(){
        List<PojoCategory> list = ConfigManager.get(getActivity()).getCategoryConfig().categories;
        categoryListAdapter.setPojoCategoryList(list);
        mExtraCategoryId=categoryListAdapter.getPojoCategoryList().get(0).id;
    }

    private void setListeners(){
        mCategorylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<PojoCategory> pojoCategoryList = categoryListAdapter.getPojoCategoryList();
                if (pojoCategoryList != null){
                    categoryListAdapter.updateSelect(position);
                    PojoCategory pojoCategory = pojoCategoryList.get(position);
                    mExtraCategoryId=pojoCategory.id;
                    doFirstLoadingTask();
//                    Analytics.sendUIEvent(AnalyticsEvents.DrawerCategory.Click_Drawer_Item,pojoCategoryList.get(position).name,null);
                    Analytics.sendUIEvent(AnalyticsEvents.ClickClassification.Click_Classification,position+"",null);
                }
            }

        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefreshListView.startRefreshing();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Analytics.sendUIEvent(AnalyticsEvents.MainTab.Category_Page_Show, null,null);
    }

    @Override
    public void onProductPartClick(View view, PojoProduct product) {
        mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_OTHER,product,this);
        mPartFragment.show(getFragmentManager());
    }


    private void doFirstLoadingTask(){
        Map<String,Object> param = new HashMap<>();
        param.put("tag", mExtraCategoryId);
        param.put("offset", "0");
        param.put("limit", ""+ONCE_LOAD_SIZE);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRODUCT_LIST, param, new Network.JsonCallBack<PojoProductList>() {
            @Override
            public void onSuccess(PojoProductList list) {
//                HBLog.d(TAG + " onSuccess " + list);
                mNetworkErrorLayout.setVisibility(View.GONE);
                mContentView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                if (list.productList.size() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mClassificationlistView.setVisibility(View.GONE);
                } else {
                    mRefreshListView.onRefreshComplete();
                    mCategoryGoodsAdapter.updateList(list.productList);
                    updateFooterViewWhenOK(list.productList.size());
                    mEmptyView.setVisibility(View.GONE);
                    mClassificationlistView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                mRefreshListView.onRefreshComplete();
                ToastUtils.showLongToast(GlobalContext.get(),R.string.load_failed);
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
            }

            @Override
            public Class<PojoProductList> getObjectClass() {
                return PojoProductList.class;
            }
        });
//        mNetworkErrorLayout.setVisibility(View.GONE);
//        mContentView.setVisibility(View.INVISIBLE);
//        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPartCallBack(boolean paySuceess,PartObject partObject){
        if(paySuceess){
            mRefreshListView.startRefreshing();
        }
    }

    @Override
    public void onRetryClick(View view) {
        tryLoaderMore();
    }
    private void tryLoaderMore(){
        if(!mLoaderMoreDoing&&mListFooterView.getState() == ListFooterView.State.LOAD){
            doLoaderMoreTask();
            mLoaderMoreDoing = true;
        }
    }
    private void updateFooterViewWhenOK(int onceListSize){
        mLoaderMoreDoing = false;
        mLoaderMoreDoing = false;
        if(mCategoryGoodsAdapter.getCount()<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.EMPTY);
        } else if(onceListSize<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.END);
        } else {
            mListFooterView.setState(ListFooterView.State.LOAD);
        }
    }

    private void doLoaderMoreTask(){
        Map<String,Object> param = new HashMap<>();
        param.put("tag", mExtraCategoryId);
        param.put("offset", String.valueOf(mCategoryGoodsAdapter.getListCount()));
        param.put("limit", ""+ONCE_LOAD_SIZE);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRODUCT_LIST, param, new Network.JsonCallBack<PojoProductList>() {
            @Override
            public void onSuccess(PojoProductList list) {
                mNetworkErrorLayout.setVisibility(View.GONE);
                HBLog.d(TAG + " onSuccess " + list);
                mCategoryGoodsAdapter.appendList(list.productList);
                updateFooterViewWhenOK(list.productList.size());
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
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
