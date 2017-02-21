package dotc.android.happybuy.uibase.app;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoBaseList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by 陈尤岁 on 2016/12/15.
 */

public abstract class BaseListViewActivity<T extends PojoBaseList> extends BaseActivity {


    private HBToolbar idToolbar;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mlistView;
    private View layoutprogress;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View emptydataView;
    private String mLastNumb = "0";
    private BaseAdapter mAdapter;
    private T mData;
    private Class classType;
    private boolean isFirstLoad = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        resolveClassInfo();
        initView();
        initListener();
        showProgress(true);
        loadData();
    }

    private void initListener() {
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mLastNumb = "0";
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }
        });
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                mLastNumb = "0";
                loadData();
            }
        });
    }

    private void resolveClassInfo() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        classType = (Class) params[0];
    }

    private void loadData() {
        final HashMap<String, Object> map = createParams();
        map.put(lastNumb(),mLastNumb);
        String url = bindUrl();
        Network.get(GlobalContext.get()).asyncPost(url, map, new Network.JsonCallBack<T>() {
            @Override
            public void onSuccess(T t) {
                //first load or refresh
                if (mLastNumb.equals("0")||mLastNumb.equals("")) {
                    isFirstLoad = true;
                    mData = null;
                    mData = t;
                    mAdapter = createListAdapter(mData);
                    mlistView.setAdapter(mAdapter);
                    if(mData.length()==0){
                        emptydataView.setVisibility(View.VISIBLE);
                    }
                    mLastNumb = t.lastNumb();
                }else{
                    isFirstLoad = false;
                }

                if(!mLastNumb.equals(t.lastNumb())&&!isFirstLoad){
                    mLastNumb = t.lastNumb();
                    addMoreData(t);
                    mAdapter.notifyDataSetChanged();
                }
                mPullToRefreshListView.onRefreshComplete();
                showProgress(false);
                if(t.length()<maxPageSize()){
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }else{
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                }

            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                mPullToRefreshListView.onRefreshComplete();

                if(mAdapter==null||mAdapter.getCount()==0){
                    mNetworkErrorLayout.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(GlobalContext.get(), R.string.network_exception, Toast.LENGTH_LONG).show();
                }
                showProgress(false);

                emptydataView.setVisibility(View.GONE);
            }

            @Override
            public Class<T> getObjectClass() {
                return classType;
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

        mNetworkErrorLayout = (NetworkErrorLayout) findViewById(R.id.layout_network_error);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        layoutprogress = findViewById(R.id.layout_loading);
        idToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        emptydataView = findViewById(R.id.layout_empty);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mlistView = mPullToRefreshListView.getRefreshableView();
        idToolbar.setTitle(createTitle());
        idToolbar.setDisplayHomeAsUpEnabled(true);
        mlistView.setDivider(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        mlistView.setDividerHeight(DisplayUtils.dp2Px(GlobalContext.get(), 1));
    }


    public abstract BaseAdapter createListAdapter(T t);

    public abstract int createTitle();

    public abstract int maxPageSize();

    public abstract HashMap<String, Object> createParams();

    public abstract String lastNumb();

    public T getData() {
        return mData;
    }

    public void setCount(String lastNumb){
        mLastNumb = lastNumb;
    }
    ;
    public abstract void addMoreData(T t);

    public abstract String bindUrl();

}
