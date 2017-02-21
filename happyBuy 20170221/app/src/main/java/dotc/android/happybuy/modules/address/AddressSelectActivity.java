package dotc.android.happybuy.modules.address;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoAddressItem;
import dotc.android.happybuy.http.result.PojoAddressItemList;
import dotc.android.happybuy.http.result.PojoNone;
import dotc.android.happybuy.http.result.PojoParticpateHistory;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.address.adapter.AddressSelectAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.component.NetworkErrorLayout;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 * 确认收获地址
 */
public class AddressSelectActivity extends BaseActivity implements OnItemClickListener,View.OnClickListener {

    public static final String EXTRA_PRODUCT_ITEM_ID = "extra_product_item_id";
    private HBToolbar mToolbar;
    private NetworkErrorLayout mNetworkErrorLayout;
    private View mLoadingView;
    private View mContentView;
    private ListView mListView;
    private View mEmptyView;
    private Button mAddButton;
    private Button mOkButton;
    private AddressSelectAdapter mAddressSelectAdapter;

    private String mExtraProductItemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_select);
        readExtraFromIntent();
        initActionbar();
        initUI();
        initData();
    }

    private void readExtraFromIntent(){
        mExtraProductItemId = getIntent().getStringExtra(EXTRA_PRODUCT_ITEM_ID);
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_address_select);
        mToolbar.setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initUI() {
        mNetworkErrorLayout = (NetworkErrorLayout) findViewById(R.id.layout_network_error);
        mLoadingView = findViewById(R.id.layout_loading);
        mContentView = findViewById(R.id.layout_content);
        mListView = (ListView) findViewById(R.id.listview);
        mOkButton = (Button) findViewById(R.id.button_ok);
        mEmptyView = findViewById(R.id.layout_empty_guide);
        mAddButton = (Button) findViewById(R.id.button_add);
        mListView.setOnItemClickListener(this);
        mOkButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);
        mNetworkErrorLayout.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLoadInfo();
            }
        });
    }

    private void initData(){
        mAddressSelectAdapter = new AddressSelectAdapter(AddressSelectActivity.this);
        mListView.setAdapter(mAddressSelectAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doLoadInfo();
    }

    private void doLoadInfo(){
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.ADDRESS_ITEMS, null, new Network.JsonCallBack<PojoAddressItemList>() {
            @Override
            public void onSuccess(PojoAddressItemList itemList) {
                HBLog.d(TAG + " onSuccess " + itemList.toString());
                if(itemList.list.size() ==0){
                    mEmptyView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    mOkButton.setVisibility(View.GONE);
                } else {
                    mAddressSelectAdapter.updateList(itemList.list);
                    mEmptyView.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mOkButton.setVisibility(View.VISIBLE);
                }
//                mAddressSelectAdapter.updateSelect(0);
                mLoadingView.setVisibility(View.GONE);
                mContentView.setVisibility(View.VISIBLE);
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Add_Address_Prize,null,null);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
                mLoadingView.setVisibility(View.GONE);
                mNetworkErrorLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public Class<PojoAddressItemList> getObjectClass() {
                return PojoAddressItemList.class;
            }
        });
        mLoadingView.setVisibility(View.VISIBLE);
        mContentView.setVisibility(View.GONE);
        mNetworkErrorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAddressSelectAdapter.updateSelect(position);
    }

    private void doConfirmAddressTask(View view){
        PojoAddressItem item = mAddressSelectAdapter.getSelectItem();

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.commiting));
        Map<String,Object> params = new HashMap<>();
        params.put("address_id",String.valueOf(item.id));
//        params.put("order_id",mExtraParticipateItem.awardOrderId);
//        params.put("product_id",mExtraParticipateItem.productId);
        params.put("item_id",mExtraProductItemId);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.ADDRESS_CONFIRM, params, new Network.JsonCallBack<PojoNone>() {
            @Override
            public void onSuccess(PojoNone list) {
                HBLog.d(TAG + " onSuccess " + list);
                if(!AppUtil.isActivityDestroyed(AddressSelectActivity.this)){
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    ToastUtils.showLongToast(GlobalContext.get(), R.string.commiting_success);
                    finish();
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                if(!AppUtil.isActivityDestroyed(AddressSelectActivity.this)){
                    ToastUtils.showLongToast(GlobalContext.get(), R.string.commiting_failed);
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public Class<PojoNone> getObjectClass() {
                return PojoNone.class;
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_ok:
                doConfirmAddressTask(v);
                break;
            case R.id.button_add:
                if(AppUtil.getMetaData(this,"country").equals("vn")){
                    Intent intent = new Intent(this,AddressEditActivityVn.class);
                    intent.putExtra("mode",AddressEditActivity.MODE_ADD);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(this,AddressEditActivity.class);
                    intent.putExtra("mode",AddressEditActivity.MODE_ADD);
                    startActivity(intent);
                }
                break;
        }

    }
}
