package dotc.android.happybuy.modules.show;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.HashMap;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoShowItemList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.show.adapter.ShowProductListAdapter;
import dotc.android.happybuy.modules.main.base.BaseMainFragment;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by wangjun on 16/8/18.
 */
public class ShowFragment extends BaseMainFragment {

    private HBToolbar idToolbar;
    private PullToRefreshListView mPullToRefreshListView;
    private ShowProductListAdapter mShowProductListAdapter;
    private ListView mlistView;
    private View layoutprogress;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View emptydataView;

    public static final int PAGE_SIZE = 10;
    private String lastNum = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mExtraParticipateType = getArguments().getInt(EXTRA_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        mNetworkErrorLayout = (NetworkErrorLayout) view.findViewById(R.id.layout_network_error);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        layoutprogress = view.findViewById(R.id.layout_loading);
        idToolbar = (HBToolbar) view.findViewById(R.id.id_toolbar);
        emptydataView = view.findViewById(R.id.layout_empty);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListeners();
        showProgress(true);
        loadData();
        Analytics.sendUIEvent(AnalyticsEvents.MainTab.Show_Page_Show, null, null);
    }

    private void setListeners() {
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                lastNum = "0";
                HBLog.i(TAG + "请求 lastNum " + lastNum);
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                HBLog.i(TAG + "请求 lastNum " + lastNum);
                loadData();
            }
        });
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastNum = "0";
                HBLog.i(TAG + "请求 lastNum " + lastNum);
                loadData();
            }
        });
    }


    private void loadData() {
        HashMap<String, Object> map = new HashMap<>();
        String url = HttpProtocol.URLS.SHARE_OVER_LIST;
        map.put("last_numb", lastNum);
        map.put("page_size", PAGE_SIZE);
        HBLog.i(TAG + "last_numb " + lastNum);
        Network.get(GlobalContext.get()).asyncPost(url, map, new Network.JsonCallBack<PojoShowItemList>() {
            @Override
            public void onSuccess(PojoShowItemList pojoShowItemList) {
                HBLog.i(TAG + "请求到 last_numb " + pojoShowItemList.last_numb);
                if (lastNum.equals("") || lastNum.equals("0")) {
                    HBLog.i(TAG + "这是一次刷新操作,清空list");
                    mShowProductListAdapter.clearData();
                    if (pojoShowItemList.list.size() == 0) {
                        emptydataView.setVisibility(View.VISIBLE);      //空提示
                    }
                }
                if (!lastNum.equals(pojoShowItemList.last_numb) || (lastNum.equals("") || lastNum.equals("0"))) {
                    HBLog.i(TAG + "加载数据到list");
                    lastNum = pojoShowItemList.last_numb;
                    mShowProductListAdapter.addData(pojoShowItemList.list);
                    mShowProductListAdapter.notifyDataSetChanged();
                } else {
                    HBLog.i(TAG + "由于请求的lastNum 和 请求到的last_numb 相同，所以不做加载");
                }
                mPullToRefreshListView.onRefreshComplete();
                showProgress(false);

                //禁用下拉(当服务器实际返回数据的条数小于希望的条数情况下，禁止再次下拉请求)
                if (pojoShowItemList.list.size() < PAGE_SIZE) {
                    //禁止
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    //不禁止
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                }
                mPullToRefreshListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), R.string.network_exception, Toast.LENGTH_LONG).show();
                mPullToRefreshListView.onRefreshComplete();
                showProgress(false);
                mPullToRefreshListView.setVisibility(View.GONE);
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
                emptydataView.setVisibility(View.GONE);
            }

            @Override
            public Class<PojoShowItemList> getObjectClass() {
                return PojoShowItemList.class;
            }
        });
        mNetworkErrorLayout.setVisibility(View.GONE);
        emptydataView.setVisibility(View.GONE);
    }

    private void showProgress(boolean isshow) {
        if (isshow) {
            layoutprogress.setVisibility(View.VISIBLE);
        } else {
            layoutprogress.setVisibility(View.GONE);
        }
    }

    private void initView() {
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mShowProductListAdapter = new ShowProductListAdapter(getContext());
        mlistView = mPullToRefreshListView.getRefreshableView();
        mlistView.setAdapter(mShowProductListAdapter);
        idToolbar.setTitle(R.string.activity_show);
        idToolbar.setDisplayHomeAsUpEnabled(false);
        mlistView.setDivider(new ColorDrawable(Color.parseColor("#f5f3f3")));
        mlistView.setDividerHeight(DisplayUtils.dp2Px(GlobalContext.get(), 10));

//        idToolbar.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Intent shareIntent = new Intent(getActivity(), SharePrizeActivity.class);
//                getActivity().startActivity(shareIntent);
//                return true;
//            }
//        });
    }

}
