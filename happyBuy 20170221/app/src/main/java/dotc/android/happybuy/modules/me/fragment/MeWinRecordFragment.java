package dotc.android.happybuy.modules.me.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoParticpateHistory;
import dotc.android.happybuy.http.result.PojoParticpateHistoryList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.me.base.BaseMeTabFragment;
import dotc.android.happybuy.modules.me.manager.GuessYouLikeManager;
import dotc.android.happybuy.modules.me.widget.MeRefreshLayout;
import dotc.android.happybuy.modules.prize.AwardProductActivity;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.ui.adapter.WinRecordAdapter;
import dotc.android.happybuy.uibase.listview.ListFooterView;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/4/1.
 */
public class MeWinRecordFragment extends BaseMeTabFragment implements AdapterView.OnItemClickListener,
        MeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener,ListFooterView.OnRetryClickListener{

    private final int ONCE_LOAD_SIZE = 12;
    public final static String EXTRA_UID = "extra_uid";

    private MeRefreshLayout mParentLayout;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View mEmptyView;
    //    private View mLoadingView;
    private View mContentView;
    private ListView mListView;
    private ListFooterView mListFooterView;
    private WinRecordAdapter mWinRecordAdapter;
    private GuessYouLikeManager mGuessManager;

    private boolean mLoaderMoreDoing;
    private boolean mIsAddFooterView;
    private boolean mSelfShow;

    private String mLastSortKey = "";

    private String mExtraUid;

    public static MeWinRecordFragment newInstance(String uid) {
        MeWinRecordFragment fragment = new MeWinRecordFragment();
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
        View view = inflater.inflate(R.layout.fragment_winning_record, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mNetworkErrorLayout = (NetworkErrorLayout) view.findViewById(R.id.layout_network_error);
//        mLoadingView = view.findViewById(R.id.layout_loading);
        mContentView = view.findViewById(R.id.layout_content);
        mEmptyView = view.findViewById(R.id.list_emptyview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HBLog.d(TAG+" onActivityCreated ======== "+getParentFragment());
        mParentLayout = (MeRefreshLayout) getParentFragment().getView().findViewById(R.id.layout_me_refresh);
        mParentLayout.addOnRefreshListener(this);
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentLayout.startRefreshing();
            }
        });
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
//        mListView.setOnScrollListener(this);
        mListFooterView = new ListFooterView(getContext());
        mListFooterView.setOnRetryClickListener(this);
        mWinRecordAdapter = new WinRecordAdapter(getContext(), new ArrayList<PojoParticpateHistory>());
        mListView.addFooterView(mListFooterView);
        mListView.setAdapter(mWinRecordAdapter);
        mGuessManager = new GuessYouLikeManager("TabPrice");
        doPullRefreshTask(false);
    }

    @Override
    public void onResume() {
        super.onResume();
//        refreshData();
    }

    @Override
    public boolean isScrollTop() {
        if(mListView.getVisibility() == View.VISIBLE&&mListView.getCount()>0){
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
        param.put("status", String.valueOf(HttpProtocol.USER_PARTICIPATE_STATUS.WIN));
        param.put("from_uid", String.valueOf(mExtraUid));
        param.put("page_size", String.valueOf(ONCE_LOAD_SIZE));
        param.put("last_numb", "");
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PARTICPATE_HISTORY, param, new Network.JsonCallBack<PojoParticpateHistoryList>() {
            @Override
            public void onSuccess(PojoParticpateHistoryList list) {
                HBLog.d(TAG + " onSuccess " + list);
                if(needUpdateParentLayout){
                    mParentLayout.onRefreshComplete();
                }
                if (mWinRecordAdapter.getCount() == 0) {
                    if (list.list.size() > 0) {
                        mWinRecordAdapter.updateList(list.list);
                        mEmptyView.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        updateFooterViewWhenOK(list.list.size());
                        mLastSortKey = list.last_numb;
                    } else {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mGuessManager.addGuessLayout((LinearLayout)mEmptyView,R.string.list_winner_empty_hint);
                        mListView.setVisibility(View.GONE);
                    }
                } else {
                    if (list.list.size() > 0) {
                        mWinRecordAdapter.updateList(list.list);
                        updateFooterViewWhenOK(list.list.size());
                        mLastSortKey = list.last_numb;
                    } else {
                        //toast
                    }
                }
//                mWinRecordAdapter.updateList(list.list);
//                updateFooterViewWhenOK(list.productList.size());
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                if(needUpdateParentLayout){
                    mParentLayout.onRefreshComplete();
                }
                if (mWinRecordAdapter.getCount() == 0) {
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
        if(mNetworkErrorLayout.getVisibility() == View.VISIBLE){
            mNetworkErrorLayout.setVisibility(View.GONE);
        }
        if(mEmptyView.getVisibility() == View.VISIBLE){
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void doLoaderMoreTask(){
        Map<String,Object> param = new HashMap<>();
//        param.put("tag", mExtraPojoCategory.id);
        param.put("status", String.valueOf(HttpProtocol.USER_PARTICIPATE_STATUS.WIN));
        param.put("from_uid", String.valueOf(mExtraUid));
        param.put("page_size",String.valueOf(ONCE_LOAD_SIZE));
        param.put("last_numb", String.valueOf(mLastSortKey));
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PARTICPATE_HISTORY, param, new Network.JsonCallBack<PojoParticpateHistoryList>() {
            @Override
            public void onSuccess(PojoParticpateHistoryList list) {
                HBLog.d(TAG + " onSuccess " + list);
                if (list.list.size() > 0) {
                    mWinRecordAdapter.appendList(list.list);
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
            public Class<PojoParticpateHistoryList> getObjectClass() {
                return PojoParticpateHistoryList.class;
            }
        });
        mListFooterView.setState(ListFooterView.State.LOADING);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PojoParticpateHistory history = mWinRecordAdapter.getItem(position);
        if(history.award_uid.equals(getUid())){
            Intent intent = new Intent(getActivity(), AwardProductActivity.class);
            intent.putExtra(AwardProductActivity.EXTRA_PRODUCT_ITEM_ID, history.product_item_id);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getContext(), GoodsDetailActivity.class);
            intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,history.product_id);
            intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ITEM_ID,history.product_item_id);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh(MeRefreshLayout refreshView) {
        HBLog.d(TAG+" startRefreshing ---mSelfShow:"+mSelfShow);
        if(mSelfShow){
            doPullRefreshTask(true);
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

    @Override
    public void startRefreshing() {
        mParentLayout.startRefreshing();
    }

    private void updateFooterViewWhenOK(int onceListSize){
        mLoaderMoreDoing = false;
        if(mWinRecordAdapter.getCount()<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.EMPTY);
        } else if(onceListSize<ONCE_LOAD_SIZE){
            mListFooterView.setState(ListFooterView.State.END);
        } else {
            mListFooterView.setState(ListFooterView.State.LOAD);
        }
    }
}
