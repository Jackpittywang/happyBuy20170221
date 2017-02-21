package dotc.android.happybuy.modules.home.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.http.result.PojoProductList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.part.PartCallBack;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.modules.part.PartObject;
import dotc.android.happybuy.ui.adapter.ProductAdapter;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.listview.ListFooterView;
import dotc.android.happybuy.uibase.component.RefreshLayout;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.ToastUtils;

/**
 */
public class ProductsFragment extends BaseTabFragment implements RefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener,ListFooterView.OnRetryClickListener,PartCallBack,ProductAdapter.OnProductPartListener {

    public final static String EXTRA_TYPE = "extra_type";
    public final static int TYPE_ALL = HttpProtocol.TAG.ALL;
    public final static int TYPE_TEN = HttpProtocol.TAG.TEN;
    private final int ONCE_LOAD_SIZE = 20;

    private RefreshLayout mParentLayout;
    private NetworkErrorLayout mNetworkErrorLayout;
//    private View mLoadingView;
    private ListView mListView;
    private ListFooterView mListFooterView;
    private View mEmptyView;
    private View mLoadingView;
    private View mContentView;

    private ProductAdapter mProductsAdapter;
    private PartFragment mPartFragment;
    private String mExtraType;
    private boolean mSelfShow = false;
    private boolean mLoaderMoreDoing;
    private int[] location = new int[2];
    private GuideCallBack mGuideCallBack;
    private boolean mHavaData = false;
    private PojoProduct pojoProduct;

    public static ProductsFragment newInstance(int type) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TYPE, type + "");
        fragment.setArguments(bundle);
        return fragment;
    }

    public ProductsFragment injectRefreshLayout(RefreshLayout refreshLayout){
        this.mParentLayout = refreshLayout;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtraType = getArguments().getString(EXTRA_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mNetworkErrorLayout = (NetworkErrorLayout) view.findViewById(R.id.layout_network_error);
        mLoadingView = view.findViewById(R.id.layout_loading);
        mContentView = view.findViewById(R.id.layout_content);
        mEmptyView = view.findViewById(R.id.layout_empty_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mParentLayout = (RefreshLayout) getActivity().findViewById(R.id.layout_refresh);
        if(mParentLayout == null){
            mParentLayout = (RefreshLayout) getParentFragment().getView().findViewById(R.id.layout_refresh);
        }
        mParentLayout.addOnRefreshListener(this);
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentLayout.startRefreshing();
            }
        });

        mListView.setOnScrollListener(this);
        mListFooterView = new ListFooterView(getContext());
        mListFooterView.setOnRetryClickListener(this);
        mProductsAdapter = new ProductAdapter(getActivity(),mExtraType,this);
        mListView.addFooterView(mListFooterView);
        mListView.setAdapter(mProductsAdapter);
        doPullRefreshTask(false);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public boolean isScrollTop() {
        if(mListView.getVisibility() == View.VISIBLE&&mListView.getCount()>0){
            View view = mListView.getChildAt(mListView.getFirstVisiblePosition());
            return (view != null) && (view.getTop() == 0);
        }
        return true;
    }

    public void onSelfShow(){
        logDebug("onSelfShow");
        mSelfShow = true;
    }

    public void onSelfDismiss(){
        logDebug("onSelfDismiss");
        mSelfShow = false;
    }

    @Override
    public void onRefresh(RefreshLayout refreshView) {
        if(mSelfShow){
            doPullRefreshTask(true);
        }
    }

    public boolean onBackPressed() {
        HBLog.d(TAG + " onBackPressed " + mPartFragment);
        return mPartFragment != null && mPartFragment.onBackPressed();
    }


    @Override
    public void onProductPartClick(View view, PojoProduct product) {
        int type = Integer.parseInt(mExtraType);
        FragmentActivity activity = getActivity();
        switch (type){
            case TYPE_ALL:
                mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_ALL,product,this);
                mPartFragment.show(activity.getSupportFragmentManager());
                break;
            case TYPE_TEN:
                mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_TEN,product,this);
                mPartFragment.show(activity.getSupportFragmentManager());
                break;
            default:
                break;
        }
    }

    /**
     * 请求服务器产品数据
     */
    public void doPullRefreshTask(final boolean needUpdateParentLayout){
        logDebug("doPullRefreshTask needUpdateParentLayout:" + needUpdateParentLayout);
        Map<String,Object> param = new HashMap<>();
        param.put("tag", String.valueOf(mExtraType));
        param.put("offset", "0");
        param.put("limit", ""+ONCE_LOAD_SIZE);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRODUCT_LIST, param, new Network.JsonCallBack<PojoProductList>() {
            @Override
            public void onSuccess(PojoProductList list) {
                mHavaData=true;
                if(list.productList.size()>0){
                    pojoProduct=list.productList.get(0);
                }
//                logDebug("doPullRefreshTask onSuccess " + list);
                if(needUpdateParentLayout){
                    mParentLayout.onRefreshComplete();
                }
                mContentView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                if (mProductsAdapter.getCount() == 0) {
                    if (list.productList.size() > 0) {
                        mProductsAdapter.updateList(list.productList);
                        mEmptyView.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        updateFooterViewWhenOK(list.productList.size());
                    } else {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    }
                } else {
                    if (list.productList.size() > 0) {
                        mProductsAdapter.updateList(list.productList);
                        updateFooterViewWhenOK(list.productList.size());
                    } else {
                    }
                }


                if(mExtraType.equals(String.valueOf(TYPE_ALL))&& mSelfShow&&list.productList.size()>0){
                    mGuideCallBack.onFirstGuide(mParentLayout,mListView,list.productList);
                }
            }


            @Override
            public void onFailed(int code, String message, Exception e) {
                logDebug("doPullRefreshTask onFailed " + code + " " + message + " " + e);
                if(needUpdateParentLayout){
                    mParentLayout.onRefreshComplete();
                }
                mLoadingView.setVisibility(View.GONE);
                if (mProductsAdapter.getCount() == 0) {
                    mNetworkErrorLayout.setVisibility(View.VISIBLE);
                    mContentView.setVisibility(View.GONE);
                } else {
                    if(e!=null){
                        ToastUtils.showLongToast(GlobalContext.get(), R.string.network_exception);
                    }
                }
            }

            @Override
            public Class<PojoProductList> getObjectClass() {
                return PojoProductList.class;
            }
        });
        if(mNetworkErrorLayout.getVisibility() == View.VISIBLE){
            mNetworkErrorLayout.setVisibility(View.GONE);
        }
        if(mEmptyView.getVisibility() == View.VISIBLE){
            mEmptyView.setVisibility(View.GONE);
        }
        if(!needUpdateParentLayout&&mProductsAdapter.getCount() ==0){
            mLoadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem+visibleItemCount==totalItemCount-1){
            tryLoaderMore();
        }
    }

    @Override
    public void onRetryClick(View view) {
        tryLoaderMore();
    }

    private void tryLoaderMore(){
        logDebug("tryLoaderMore ");
        if(!mLoaderMoreDoing&&mListFooterView.getState() == ListFooterView.State.LOAD){
            doLoaderMoreTask();
            mLoaderMoreDoing = true;
        }
    }

    private void doLoaderMoreTask(){
        int offset = mProductsAdapter.getListCount();
        logDebug("doLoaderMoreTask offset:"+offset);
        Map<String,Object> param = new HashMap<>();
        param.put("tag", String.valueOf(mExtraType));
        param.put("offset", String.valueOf(offset));
        param.put("limit", ""+ONCE_LOAD_SIZE);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PRODUCT_LIST, param, new Network.JsonCallBack<PojoProductList>() {
            @Override
            public void onSuccess(PojoProductList list) {
                logDebug("doLoaderMoreTask onSuccess " + list);
                mProductsAdapter.appendList(list.productList);
                updateFooterViewWhenOK(list.productList.size());
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                logDebug("doLoaderMoreTask onFailed " + code + " " + message + " " + e);
                if(e!=null){
                    ToastUtils.showLongToast(GlobalContext.get(), R.string.network_exception);
                }
                mListFooterView.setState(ListFooterView.State.RETRY);
            }

            @Override
            public Class<PojoProductList> getObjectClass() {
                return PojoProductList.class;
            }
        });
        mListFooterView.setState(ListFooterView.State.LOADING);
    }

    private void updateFooterViewWhenOK(int onceListSize){
        mLoaderMoreDoing = false;
        if(mProductsAdapter.getListCount()<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.EMPTY);
        } else if(onceListSize<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.END);
        } else {
            mListFooterView.setState(ListFooterView.State.LOAD);
        }
    }

    private void logDebug(String message){
        if(true){
            HBLog.d(TAG + "" + hashCode() + " " + message);
        }
    }

    @Override
    public void onPartCallBack(boolean paySuceess,PartObject partObject){
        if(paySuceess){
            if(mListFooterView.getState() != ListFooterView.State.END){
                mParentLayout.startRefreshing();
            } else {
                PojoProduct product = mProductsAdapter.getItemById(partObject.inputProduct.productId);
                product.remainTimes = Integer.parseInt(partObject.outputPayResult.remain_units);
                mProductsAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mGuideCallBack = (MainTabActivity) activity;
    }

    public interface GuideCallBack {
        void onFirstGuide(RefreshLayout refreshLayout,ListView listView, List<PojoProduct> productList);
    }
}
