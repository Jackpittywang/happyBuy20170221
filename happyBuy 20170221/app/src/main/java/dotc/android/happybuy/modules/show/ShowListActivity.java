package dotc.android.happybuy.modules.show;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.HashMap;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoShowItemList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.show.adapter.ShowProductListAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by huangli on 16/3/31.
 */
public class ShowListActivity extends BaseActivity{
    public static final String TAG = "ShowListActivity";

    public static final int TYPE_SHOW_LIST_ALL = 0;
    public static final int TYPE_SHOW_LIST_PRODUCT = 1;
    public static final int TYPE_SHOW_LIST_USER = 2;

    public static final int PAGE_SIZE = 10;

    private HBToolbar idToolbar;
    private PullToRefreshListView mPullToRefreshListView;
    private ShowProductListAdapter mShowProductListAdapter;
    private ListView mlistView;
    private RelativeLayout layoutprogress;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View emptydataView;
    private String lastNum = "0";
    private ListType listType = new ListType();

    private class ListType{
        public int listType = TYPE_SHOW_LIST_ALL;
        public String fromUid = "";
        public String productId = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        findViews();
        initView();
        setListeners();
        showProgress(true);
        getIntentdata();
        loadData();
    }

    private void getIntentdata(){
        Intent intent = getIntent();
        if (intent != null){
            int type = intent.getIntExtra("type",TYPE_SHOW_LIST_ALL);
            String from_uid = intent.getStringExtra("from_uid");
            String product_id = intent.getStringExtra("product_id");
            listType.listType = type;
            if (from_uid != null){
                listType.fromUid = from_uid;
            }
            if (product_id != null){
                listType.productId = product_id;
            }
            HBLog.i(TAG+"获取intent type "+type+" from_uid "+from_uid+" product_id "+product_id);
        }
    }


    private void setListeners(){
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                lastNum = "0";
                HBLog.i(TAG+"请求 lastNum "+lastNum);
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                HBLog.i(TAG+"请求 lastNum "+lastNum);
                loadData();
            }
        });
//        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ToastUtils.showShortToast(ShowListActivity.this, "item click");
//                Intent intent = new Intent(ShowListActivity.this, ShowDetailActivity.class);
//                startActivity(intent);
//            }
//        });
    }



    private void loadData(){
        HashMap<String,Object> map = new HashMap<>();
        String url;
        switch (listType.listType){
            case TYPE_SHOW_LIST_ALL:
                url = HttpProtocol.URLS.SHARE_OVER_LIST;
                HBLog.i(TAG+"请求 list 类型 TYPE_SHOW_LIST_ALL");
                break;
            case TYPE_SHOW_LIST_PRODUCT:
                url = HttpProtocol.URLS.SHARE_PRODUCT_LIST;
                HBLog.i(TAG+"请求 list 类型 TYPE_SHOW_LIST_PRODUCT product id "+listType.productId);
                map.put("product_id",listType.productId);
                break;
            case TYPE_SHOW_LIST_USER:
                url = HttpProtocol.URLS.SHARE_USER_LIST;
                HBLog.i(TAG+"请求 list 类型 TYPE_SHOW_LIST_USER fromUid "+listType.fromUid);
                map.put("from_uid",listType.fromUid);
                break;
            default:
                url = HttpProtocol.URLS.SHARE_OVER_LIST;
                break;
        }
        map.put("last_numb", lastNum);
        map.put("page_size",PAGE_SIZE);
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
    }

    private void findViews(){
        mNetworkErrorLayout = (NetworkErrorLayout) findViewById(R.id.layout_network_error);
        mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
        layoutprogress = (RelativeLayout)findViewById(R.id.progress_layout);
        idToolbar = (HBToolbar)findViewById(R.id.id_toolbar);
        emptydataView = findViewById(R.id.layout_empty);
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
        mShowProductListAdapter = new ShowProductListAdapter(this);
        mlistView = mPullToRefreshListView.getRefreshableView();
        mlistView.setAdapter(mShowProductListAdapter);
        idToolbar.setTitle(R.string.activity_show);
        mlistView.setDivider(new ColorDrawable(Color.parseColor("#f5f3f3")));
        mlistView.setDividerHeight(DisplayUtils.dp2Px(GlobalContext.get(), 10));
    }

}
