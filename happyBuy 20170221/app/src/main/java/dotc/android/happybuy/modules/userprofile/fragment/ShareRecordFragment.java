package dotc.android.happybuy.modules.userprofile.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoShowItemList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.show.adapter.ShowProductListAdapter;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.listview.ListFooterView;
import dotc.android.happybuy.uibase.component.RefreshLayout;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.DisplayUtils;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by zhanqiang.mei on 2016/3/29.
 */
public class ShareRecordFragment extends BaseTabFragment implements RefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener,ListFooterView.OnRetryClickListener{

    public final static String EXTRA_UID = "extra_uid";
    private final int ONCE_LOAD_SIZE = 10;
    private RefreshLayout mParentLayout;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View mContentView;
    private View mEmptyView;
    private ListView mListView;
    private ListFooterView mListFooterView;
    private ShowProductListAdapter mShowProductListAdapter;

    private boolean mLoaderMoreDoing;
    private boolean mIsAddFooterView;
    private boolean mSelfShow;
    private String mLastSortKey = "";
    private String mExtraUid;

    public static ShareRecordFragment newInstance(String uid) {
        ShareRecordFragment fragment = new ShareRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_UID, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtraUid = getArguments().getString(EXTRA_UID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_record, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mNetworkErrorLayout = (NetworkErrorLayout) view.findViewById(R.id.layout_network_error);
        mContentView = view.findViewById(R.id.layout_content);
        mEmptyView = view.findViewById(R.id.list_emptyview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentLayout = (RefreshLayout) getActivity().findViewById(R.id.layout_refresh);
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
        mShowProductListAdapter = new ShowProductListAdapter(getContext());
        mListView.addFooterView(mListFooterView);
        mListView.setAdapter(mShowProductListAdapter);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#f5f3f3")));
        mListView.setDividerHeight(DisplayUtils.dp2Px(GlobalContext.get(), 10));
        doPullRefreshTask(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean isScrollTop() {
        if(mListView.getCount()>0){
            View view = mListView.getChildAt(mListView.getFirstVisiblePosition());
            return (view != null) && (view.getTop() == 0);
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

    public void doPullRefreshTask(final boolean needUpdateParentLayout){
        Map<String,Object> param = new HashMap<>();
        param.put("from_uid",mExtraUid);
        param.put("last_numb","");
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.SHARE_USER_LIST, param, new Network.JsonCallBack<PojoShowItemList>() {
            @Override
            public void onSuccess(PojoShowItemList pojoShowItemList) {
                HBLog.d(TAG + " onSuccess " + pojoShowItemList);
                if(needUpdateParentLayout){
                    mParentLayout.onRefreshComplete();
                }
                if (mShowProductListAdapter.getCount() == 0) {
                    if (pojoShowItemList.list.size() > 0) {
                        mShowProductListAdapter.clearData();
                        mShowProductListAdapter.addData(pojoShowItemList.list);
                        mShowProductListAdapter.notifyDataSetChanged();
                        mEmptyView.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        updateFooterViewWhenOK(pojoShowItemList.list.size());
                        mLastSortKey = pojoShowItemList.last_numb;
                    } else {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    }
                } else {
                    if (pojoShowItemList.list.size() > 0) {
                        mShowProductListAdapter.clearData();
                        mShowProductListAdapter.addData(pojoShowItemList.list);
                        mShowProductListAdapter.notifyDataSetChanged();
                        updateFooterViewWhenOK(pojoShowItemList.list.size());
                        mLastSortKey = pojoShowItemList.last_numb;
                    } else {
                        HBLog.d(TAG + " onSuccess ------------- " );
                        //toast
                    }
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                if(needUpdateParentLayout){
                    mParentLayout.onRefreshComplete();
                }
                if (mShowProductListAdapter.getCount() == 0) {
                    mNetworkErrorLayout.setVisibility(View.VISIBLE);
                } else {
                    if(e!=null){
                        ToastUtils.showLongToast(GlobalContext.get(), R.string.network_exception);
                    }
                }
            }

            @Override
            public Class<PojoShowItemList> getObjectClass() {
                return PojoShowItemList.class;
            }
        });
        if(mNetworkErrorLayout.getVisibility() == View.VISIBLE){
            mNetworkErrorLayout.setVisibility(View.GONE);
        }
        if(mEmptyView.getVisibility() == View.VISIBLE){
            mEmptyView.setVisibility(View.GONE);
        }
    }
    public void refreshData(){
        doPullRefreshTask(true);
//        mNetworkErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh(RefreshLayout refreshView) {
        if(mSelfShow){
            refreshData();
        }
    }

    private void tryLoaderMore(){
        if(!mLoaderMoreDoing&&mListFooterView.getState() == ListFooterView.State.LOAD){
            doLoaderMoreTask();
            mLoaderMoreDoing = true;
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


    private void updateFooterViewWhenOK(int onceListSize){
        mLoaderMoreDoing = false;
        if(mShowProductListAdapter.getCount()<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.EMPTY);
        } else if(onceListSize<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.END);
        } else {
            mListFooterView.setState(ListFooterView.State.LOAD);
        }
    }

    private void doLoaderMoreTask(){
        Map<String,Object> param = new HashMap<>();
        param.put("from_uid",mExtraUid);
        param.put("last_numb",mLastSortKey);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.SHARE_USER_LIST, param, new Network.JsonCallBack<PojoShowItemList>() {
            @Override
            public void onSuccess(PojoShowItemList list) {
                HBLog.d(TAG + " onSuccess " + list);
                if (list.list.size() > 0) {
                    mShowProductListAdapter.addData(list.list);
                    mShowProductListAdapter.notifyDataSetChanged();
                    mLastSortKey = list.last_numb;
                } else {

                }
                updateFooterViewWhenOK(list.list.size());
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                ToastUtils.showLongToast(GlobalContext.get(), message);
            }

            @Override
            public Class<PojoShowItemList> getObjectClass() {
                return PojoShowItemList.class;
            }
        });
        mListFooterView.setState(ListFooterView.State.LOADING);
    }
}
