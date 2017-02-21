package dotc.android.happybuy.modules.address;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoAddressItemList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.address.adapter.AddressListAdapter;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by huangli on 16/4/5.
 */
public class AddressCenterActivity extends Activity implements AddressListAdapter.OnChoicedNumListener{
    public static final int MODE_NORMAL = 0;
    public static final int MODE_EDIT = 1;
    private int curMode = MODE_NORMAL;
    public static final String TAG = "AddressCenterActivity";
    private ListView addresslistView;
    private AddressListAdapter addressListAdapter;
    private Button btnDelete;
    private HBToolbar hbToolbar;
    private RelativeLayout layoutEdit,layoutprogress,btnAddAddress;
    private CheckBox Cballchoice;
    private TextView tvChoicedNumtips;
    private boolean isAllowClickBtnAdd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_center);
        findViews();
        initViews();
        setListeners();
    }

    private void requestData(){
        showProgress(true);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.ADDRESS_ITEMS, null, new Network.JsonCallBack<PojoAddressItemList>() {
            @Override
            public void onSuccess(PojoAddressItemList pojoAddressItemList) {
                HBLog.d(TAG + " onSuccess " + pojoAddressItemList.toString());
                isAllowClickBtnAdd = pojoAddressItemList.allow_add;
                addressListAdapter.setPojoAddressItemList(pojoAddressItemList);
                showProgress(false);
                switchmode(MODE_NORMAL);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
                showProgress(false);
                switchmode(MODE_NORMAL);
            }

            @Override
            public Class<PojoAddressItemList> getObjectClass() {
                return PojoAddressItemList.class;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }

    private void showProgress(boolean isshow){
        if (isshow){
            layoutprogress.setVisibility(View.VISIBLE);
        }else {
            layoutprogress.setVisibility(View.GONE);
        }
    }

    private void findViews(){
        addresslistView = (ListView)findViewById(R.id.list_address);
        hbToolbar = (HBToolbar)findViewById(R.id.id_toolbar);
        layoutEdit = (RelativeLayout)findViewById(R.id.edit_layout);
        btnDelete = (Button)findViewById(R.id.btn_delete);
        Cballchoice = (CheckBox)findViewById(R.id.cb_all_choice);
        tvChoicedNumtips = (TextView)findViewById(R.id.tv_choice_num);
        layoutprogress = (RelativeLayout)findViewById(R.id.progress_layout);
    }

    private void initViews(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View footerView = inflater.inflate(R.layout.listfooter_address, null);
        addresslistView.addFooterView(footerView);
        addressListAdapter = new AddressListAdapter(this,this);
        addresslistView.setAdapter(addressListAdapter);
        hbToolbar.setTitle(R.string.address_center);
        btnAddAddress = (RelativeLayout)footerView.findViewById(R.id.layout_add);
    }

    @Override
    public void onBackPressed() {
        if (curMode == MODE_EDIT){
            switchmode(MODE_NORMAL);
            return;
        }
        super.onBackPressed();
    }

    private void deleteAddress(){
        Map<String, Object> map = new HashMap<>();
        List<Integer> deleteitemadrids =  addressListAdapter.getDeleteAddressItemAdrids();
        if (deleteitemadrids.size() == 0){
            switchmode(MODE_NORMAL);
            return;
        }
        JSONArray ids = new JSONArray();
        for (int i = 0; i < deleteitemadrids.size(); i++){
            ids.put(deleteitemadrids.get(i));
        }
        map.put("ids",ids);
//        ToastUtils.showShortToast(this,"delete ids : "+ids.toString());
        showProgress(true);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.ADDRESS_DELETE, map, new Network.JsonCallBack<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                HBLog.d(TAG + " onSuccess " + jsonObject.toString());
                ToastUtils.showShortToast(AddressCenterActivity.this, "delete success");
                requestData();
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Delete_Address,null,null);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                showProgress(false);
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), "message " + message + " Exception " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                switchmode(MODE_NORMAL);
            }

            @Override
            public Class<JSONObject> getObjectClass() {
                return JSONObject.class;
            }
        });
    }

    private void switchmode(int mode){
        curMode = mode;
        if (curMode == MODE_EDIT){
            hbToolbar.setRightTextItem(R.string.address_done_btn);
            layoutEdit.setVisibility(View.VISIBLE);
            btnAddAddress.setVisibility(View.GONE);
            addressListAdapter.setMode(AddressListAdapter.MODE_EDIT);
        }else if (curMode == MODE_NORMAL){
            hbToolbar.setRightTextItem(R.string.address_edit_btn);
            layoutEdit.setVisibility(View.GONE);
            btnAddAddress.setVisibility(View.VISIBLE);
            addressListAdapter.setMode(AddressListAdapter.MODE_NORMAL);
        }
    }

    private void setListeners(){
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllowClickBtnAdd){
                    ToastUtils.showShortToast(AddressCenterActivity.this,R.string.address_num_limit_tips);
                    return;
                }
                if(AppUtil.getMetaData(AddressCenterActivity.this,"country").equals("vn")){
                    Intent intent = new Intent(AddressCenterActivity.this,AddressEditActivityVn.class);
                    intent.putExtra("mode",AddressEditActivity.MODE_ADD);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(AddressCenterActivity.this,AddressEditActivity.class);
                    intent.putExtra("mode",AddressEditActivity.MODE_ADD);
                    startActivity(intent);
                }


            }
        });
        hbToolbar.setRightTextItem(R.string.address_edit_btn, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (curMode == MODE_NORMAL) {
                    switchmode(MODE_EDIT);
                } else if (curMode == MODE_EDIT) {
                    switchmode(MODE_NORMAL);
                }

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAddress();
            }
        });
        Cballchoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addressListAdapter.allIsChoiced(isChecked);
            }
        });
    }

    @Override
    public void onChoicedNum(int num) {
        tvChoicedNumtips.setText(getString(R.string.address_choice_num, num));
    }
}
