package dotc.android.happybuy.modules.userprofile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoParticpateHistory;
import dotc.android.happybuy.http.result.PojoParticpateHistoryList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.ui.adapter.ParticipateRecordAdapter;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.listview.ListFooterView;
import dotc.android.happybuy.uibase.component.RefreshLayout;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.ToastUtils;

/**
 *
 */
public class ChildParticipateFragment extends BaseTabFragment implements AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener,ListFooterView.OnRetryClickListener {

    public final static String EXTRA_TYPE = "extra_type";
    public final static String EXTRA_UID = "extra_uid";
    private final int ONCE_LOAD_SIZE = 12;

    private RefreshLayout mParentLayout;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View mLoadingView;
    private View mContentView;
    private ListView mListView;
    private View mEmptyView;
    private ListFooterView mListFooterView;

    private boolean mLoaderMoreDoing;
//    private boolean mIsAddFooterView;

    private int mExtraParticipateType;
    private String mExtraUid;
    private ParticipateRecordAdapter mRecordAdapter;
    private boolean mSelfShow = true;
    private String mLastSortKey = "";


    public static ChildParticipateFragment newInstance(int type,String uid) {
        ChildParticipateFragment fragment = new ChildParticipateFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TYPE, type);
        bundle.putString(EXTRA_UID, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtraParticipateType = getArguments().getInt(EXTRA_TYPE);
        mExtraUid = getArguments().getString(EXTRA_UID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_participate, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mNetworkErrorLayout = (NetworkErrorLayout) view.findViewById(R.id.layout_network_error);
        mLoadingView = view.findViewById(R.id.layout_loading);
        mContentView = view.findViewById(R.id.layout_content);
        mEmptyView = view.findViewById(R.id.list_emptyview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentLayout = (RefreshLayout) getActivity().findViewById(R.id.layout_refresh);

        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentLayout.startRefreshing();
            }
        });
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);
        mListFooterView = new ListFooterView(getContext());
        mListFooterView.setOnRetryClickListener(this);

        mRecordAdapter = new ParticipateRecordAdapter(this);
        mListView.addFooterView(mListFooterView);
        mListView.setAdapter(mRecordAdapter);
        doPullRefreshTask(false);
//        initDataWithFirstLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecordAdapter.clearTimer();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (mExtraParticipateType){
            case HttpProtocol.USER_PARTICIPATE_STATUS.ALL:
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Product,"AllOrder",null);
                break;
            case HttpProtocol.USER_PARTICIPATE_STATUS.ONSALE:
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Product,"InProcess",null);
                break;
            case HttpProtocol.USER_PARTICIPATE_STATUS.AWARDED:
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Click_Product,"Finished",null);
                break;
            default:
                break;
        }
        PojoParticpateHistory particpateHistory = mRecordAdapter.getItem(i);
        Intent intent = new Intent(getContext(), GoodsDetailActivity.class);
        intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,particpateHistory.product_id);
        intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ITEM_ID,particpateHistory.product_item_id);
        startActivity(intent);
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
    public boolean isScrollTop() {
        if(mContentView.getVisibility() == View.VISIBLE){
            if(mListView.getCount()>0){
                View view = mListView.getChildAt(mListView.getFirstVisiblePosition());
                return (view != null) && (view.getTop() == 0);
            }
        }
        return true;
    }

    @Override
    public void onSelfDismiss() {
        mSelfShow = false;
    }

    @Override
    public void onSelfShow() {
        mSelfShow = true;
    }

    public void doRefreshTask(){
        doPullRefreshTask(true);
    }

    private void doPullRefreshTask(final boolean needUpdateParentLayout){
        Map<String,Object> param = new HashMap<>();
        param.put("status", String.valueOf(mExtraParticipateType));
        param.put("from_uid", String.valueOf(mExtraUid));
        param.put("page_size",String.valueOf(ONCE_LOAD_SIZE));
        param.put("last_numb","");
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PARTICPATE_HISTORY, param, new Network.JsonCallBack<PojoParticpateHistoryList>() {
            @Override
            public void onSuccess(PojoParticpateHistoryList list) {
//                HBLog.d(TAG + " doPullRefreshTask onSuccess " + list);
                if(needUpdateParentLayout){
                    mParentLayout.onRefreshComplete();
                }
                mRecordAdapter.setDiffTimestamp(list.server_time);
                if(mRecordAdapter.getCount()==0){
                    if (list.list.size() > 0) {
                        mRecordAdapter.updateList(list.list);
                        mEmptyView.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        updateFooterViewWhenOK(list.list.size());
                        mLastSortKey = list.last_numb;
                    } else {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                        mLastSortKey = "";
                    }
                } else {
                    if (list.list.size() > 0) {
                        mRecordAdapter.updateList(list.list);
                        updateFooterViewWhenOK(list.list.size());
                        mLastSortKey = list.last_numb;
                    } else {
                        mLastSortKey = "";
                        //toast
                    }
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " doPullRefreshTask onFailed " + code + " " + message + " " + e);
                if(needUpdateParentLayout){
                    mParentLayout.onRefreshComplete();
                }
                if(mRecordAdapter.getCount()==0){
                    mNetworkErrorLayout.setVisibility(View.VISIBLE);
                } else {
                    if(e!=null){
                        ToastUtils.showLongToast(GlobalContext.get(), R.string.network_exception);
                    }
                }
            }

            @Override
            public Class<PojoParticpateHistoryList> getObjectClass() {
                return PojoParticpateHistoryList.class;
            }
        });

        mNetworkErrorLayout.setVisibility(View.GONE);
        if(mEmptyView.getVisibility() == View.VISIBLE){
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void doLoaderMoreTask(){
        Map<String,Object> param = new HashMap<>();
//        param.put("tag", mExtraPojoCategory.id);
        param.put("status", String.valueOf(mExtraParticipateType));
        param.put("from_uid", String.valueOf(mExtraUid));
        param.put("page_size",String.valueOf(ONCE_LOAD_SIZE));
        param.put("last_numb",String.valueOf(mLastSortKey));
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PARTICPATE_HISTORY, param, new Network.JsonCallBack<PojoParticpateHistoryList>() {
            @Override
            public void onSuccess(PojoParticpateHistoryList list) {
                HBLog.d(TAG + " doLoaderMoreTask onSuccess " + list);
                if(list.list.size()>0){
                    mRecordAdapter.appendList(list.list);
                    mLastSortKey = list.last_numb;
                } else {

                }
                updateFooterViewWhenOK(list.list.size());
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " doLoaderMoreTask onFailed " + code + " " + message + " " + e);
                ToastUtils.showLongToast(GlobalContext.get(), message);
            }

            @Override
            public Class<PojoParticpateHistoryList> getObjectClass() {
                return PojoParticpateHistoryList.class;
            }
        });
        mListFooterView.setState(ListFooterView.State.LOADING);
    }

    private void tryLoaderMore(){
        if(!mLoaderMoreDoing&&mListFooterView.getState() == ListFooterView.State.LOAD){
            doLoaderMoreTask();
            mLoaderMoreDoing = true;
        }
    }

    @Override
    public void onRetryClick(View view) {
        tryLoaderMore();
    }

    private void updateFooterViewWhenOK(int onceListSize){
        mLoaderMoreDoing = false;
        if(mRecordAdapter.getCount()<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.EMPTY);
        } else if(onceListSize<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.END);
        } else {
            mListFooterView.setState(ListFooterView.State.LOAD);
        }
    }

    public String getFragmentName(){
        return getClass().getSimpleName()+"-"+mExtraParticipateType;
    }

}
