package dotc.android.happybuy.modules.coupon.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoCouponsItem;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.coupon.RedPacketActivity;
import dotc.android.happybuy.modules.coupon.adapter.UnavailableRedBagListAdpter;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by wangzhiyuan on 2016/7/19.
 */
public class UnavailableRedBagFragment extends BaseTabFragment {

    private final int ONCE_LOAD_SIZE = 12;
    public final static String EXTRA_UID = "extra_uid";

    private String mLastSortKey = "";
    private String mExtraUid;
    public static final int PAGE_SIZE = 10;
    private PullToRefreshListView mPullToRefreshListView;
    private UnavailableRedBagListAdpter mUnavailableRedBagListAdpter;
    private ListView mlistView;
    private RelativeLayout layoutprogress;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View emptydataView;
    private String lastNum = "0";
    FragmentCallBack2 mFragmentCallBack=null;
    private int position= RedPacketActivity.TAB_POSITION_0;

    public static UnavailableRedBagFragment newInstance(String uid) {
        UnavailableRedBagFragment fragment = new UnavailableRedBagFragment();
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
        View view = inflater.inflate(R.layout.fragment_red_bag, container, false);
        findViews(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListeners();
        showProgress(true);
        loadData(false);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private void setListeners(){
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                lastNum = "0";
                HBLog.i(TAG + "请求 lastNum " + lastNum);
                loadData(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                HBLog.i(TAG + "请求 lastNum " + lastNum);
                loadData(true);
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentCallBack= (RedPacketActivity) activity;
    }

   /* private void loadData(final boolean isRefresh){
        HashMap<String,Object> map = new HashMap<>();
        String url;
        url = HttpProtocol.URLS.SHARE_OVER_LIST;
        map.put("last_numb", lastNum);
        map.put("page_size",PAGE_SIZE);
        HBLog.i(TAG + "last_numb " + lastNum);
        Network.get(GlobalContext.get()).asyncPost(url, map, new Network.JsonCallBack<PojoShowItemList>() {
            @Override
            public void onSuccess(PojoShowItemList pojoShowItemList) {
                if(isRefresh){
                }
//                mFragmentCallBack.callbackAvalibleCoupons(pojoShowItemList.list.size(),position);
                HBLog.i(TAG + "请求到 last_numb " + pojoShowItemList.last_numb);
                if (lastNum.equals("") || lastNum.equals("0")) {
                    HBLog.i(TAG + "这是一次刷新操作,清空list");
                    mUnavailableRedBagListAdpter.clearData();
                    if (pojoShowItemList.list.size() == 0) {
                        emptydataView.setVisibility(View.VISIBLE);      //空提示
                    }
                }
                if (!lastNum.equals(pojoShowItemList.last_numb) || (lastNum.equals("") || lastNum.equals("0"))) {
                    HBLog.i(TAG + "加载数据到list");
                    lastNum = pojoShowItemList.last_numb;
                    mUnavailableRedBagListAdpter.addData(pojoShowItemList.list);
                    mUnavailableRedBagListAdpter.notifyDataSetChanged();
                } else {
                    HBLog.i(TAG + "由于请求的lastNum 和 请求到的last_numb 相同，所以不做加载");
                }
                mPullToRefreshListView.onRefreshComplete();
                showProgress(false);

                //禁用下拉(当服务器实际返回数据的条数小于希望的条数情况下，禁止再次下拉请求)
                if (pojoShowItemList.list.size() < PAGE_SIZE){
                    //禁止
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }else {
                    //不禁止
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
                mPullToRefreshListView.onRefreshComplete();
                showProgress(false);
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
    }*/

    private void loadData(final boolean isRefresh){
        String url;
        url = HttpProtocol.URLS.COUPONS;
        Map<String,Object> param = new HashMap<>();
//        param.put("type", String.valueOf(HttpProtocol.USER_PARTICIPATE_STATUS.WIN));
        param.put("status",RedPacketActivity.COUPON_TYPE_UNAVAILABLE);
        Network.get(GlobalContext.get()).asyncPost(url, param, new Network.JsonCallBack<PojoCouponsItem>() {
            @Override
            public void onSuccess(PojoCouponsItem pojoCouponsItem) {
                /*if(!isRefresh){
                    mFragmentCallBack.callbackReadyToDistributeCoupons(pojoCouponsItem.list.size(),position);
                }*/
                HBLog.i(TAG + "请求到 last_numb " + pojoCouponsItem.list);
                if (lastNum.equals("") || lastNum.equals("0")) {
                    HBLog.i(TAG + "这是一次刷新操作,清空list");
                    mUnavailableRedBagListAdpter.clearData();
                    if (pojoCouponsItem.list.size() == 0) {
                        emptydataView.setVisibility(View.VISIBLE);      //空提示
                    }
                }
                mUnavailableRedBagListAdpter.addData(pojoCouponsItem.list);
                mUnavailableRedBagListAdpter.notifyDataSetChanged();
                mPullToRefreshListView.onRefreshComplete();
                showProgress(false);

                //禁用下拉(当服务器实际返回数据的条数小于希望的条数情况下，禁止再次下拉请求)
                if (pojoCouponsItem.list.size() < PAGE_SIZE){
                    //禁止
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }else {
                    //不禁止
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
                mPullToRefreshListView.onRefreshComplete();
                showProgress(false);
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
                emptydataView.setVisibility(View.GONE);
            }

            @Override
            public Class<PojoCouponsItem> getObjectClass() {
                return PojoCouponsItem.class;
            }
        });
        mNetworkErrorLayout.setVisibility(View.GONE);
        emptydataView.setVisibility(View.GONE);
    }

    private void findViews(View view){
        mNetworkErrorLayout = (NetworkErrorLayout) view.findViewById(R.id.layout_network_error);
        mPullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.pull_refresh_list);
        layoutprogress = (RelativeLayout)view.findViewById(R.id.progress_layout);
        emptydataView = view.findViewById(R.id.layout_empty);
    }

    private void showProgress(boolean isshow){
        if (isshow){
            layoutprogress.setVisibility(View.VISIBLE);
        }else {
            layoutprogress.setVisibility(View.GONE);
        }
    }

    private void initView(){
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        mUnavailableRedBagListAdpter = new UnavailableRedBagListAdpter(getContext());
        mlistView = mPullToRefreshListView.getRefreshableView();
        mlistView.setAdapter(mUnavailableRedBagListAdpter);
        mlistView.setDivider(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        mlistView.setDividerHeight(DisplayUtils.dp2Px(getActivity(), 0));
        /*mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public boolean isScrollTop() {
        return false;
    }

    @Override
    public void onSelfDismiss() {

    }

    @Override
    public void onSelfShow() {

    }


    public interface FragmentCallBack2 {
        void callbackAvalibleCoupons(int size, int position);
        void callbackReadyToDistributeCoupons(int size, int position);
    }
}
