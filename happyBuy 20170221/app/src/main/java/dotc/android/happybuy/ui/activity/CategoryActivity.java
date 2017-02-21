package dotc.android.happybuy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoCategory;
import dotc.android.happybuy.http.result.PojoCategoryList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.boutique.BoutiqueActivity;
import dotc.android.happybuy.ui.adapter.CategoryAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;

/**
 * Created by wangjun on 16/3/29.
 * 商品分类
 *
 */
@Deprecated
public class CategoryActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private HBToolbar mToolbar;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View mLoadingView;
    private View mContentView;

    private GridView mGridView;
    private CategoryAdapter mCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initActionbar();
        initUI();
        doLoadInfo();
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_goods_category);
        mToolbar.setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initUI() {
        mNetworkErrorLayout = (NetworkErrorLayout) findViewById(R.id.layout_network_error);
        mLoadingView = findViewById(R.id.layout_loading);
        mContentView = findViewById(R.id.layout_content);
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(this);
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                doLoadInfo();
            }
        });
    }

    private void doLoadInfo(){
        Map<String,Object> param = new HashMap<>();
//        param.put("id", "haha");
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.CATEGORY, param, new Network.JsonCallBack<PojoCategoryList>() {
            @Override
            public void onSuccess(PojoCategoryList pojoCategoryList) {
                HBLog.d(TAG + " onSuccess " + pojoCategoryList);
                mCategoryAdapter = new CategoryAdapter(CategoryActivity.this, pojoCategoryList.categories);
                mGridView.setAdapter(mCategoryAdapter);
                mLoadingView.setVisibility(View.GONE);
                mContentView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                mLoadingView.setVisibility(View.GONE);
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public Class<PojoCategoryList> getObjectClass() {
                return PojoCategoryList.class;
            }
        });
        mLoadingView.setVisibility(View.VISIBLE);
        mContentView.setVisibility(View.GONE);
        mNetworkErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PojoCategory pojoCategory = mCategoryAdapter.getItem(position);
        Intent intent = new Intent(this,BoutiqueActivity.class);
        intent.putExtra(BoutiqueActivity.EXTRA_CATEGORY_ID, pojoCategory.id);
        intent.putExtra(BoutiqueActivity.EXTRA_CATEGORY_NAME, pojoCategory.name);
        startActivity(intent);
    }
}
