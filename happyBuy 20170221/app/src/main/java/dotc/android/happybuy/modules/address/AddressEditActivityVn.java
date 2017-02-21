package dotc.android.happybuy.modules.address;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.persist.location.LocationManagerVn;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.FormatVerifyUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by huangli on 16/4/5.
 */
public class AddressEditActivityVn extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_MOBILE = "extra_mobile";
    public static final String EXTRA_ADDRESS = "extra_address";
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_PROVINCE = "extra_province";
    public static final String EXTRA_DISTRICT = "extra_district";
    public static final String EXTRA_POSTCODE = "extra_postcode";
    public static final String EXTRA_WARD = "extra_ward";


    public static int MODE_ADD = 0;
    public static int MODE_MODIFY = 1;
    private int curmode = MODE_ADD;

    private HBToolbar idToolbar;
    private RelativeLayout btnSave;
    private RelativeLayout layoutprogress;
    private TextView mProvinceTextView;
    private TextView mDistrictTextView;
    private TextView mPostcodeTextView;

    private View mProvinceLayout;
    private View mDistrictLayout;
    private View mPostcodeLayout;

    private String mExtraName;
    private String mExtraMobile;
    private String mExtraAddress;
    private String mExtraId;
    private String mExtraProvince;
    private String mExtraDistrict;
    private String mExtraPostcode;

    private LocationManagerVn mLocationManager;
    private LocationManagerVn.Region mUserOptProvince;
    private LocationManagerVn.Region mUserOptDistrict;
    private String mUserOptPostcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit_vn);
        Intent intent = getIntent();
        curmode = intent.getIntExtra("mode", MODE_ADD);
        mExtraName = intent.getStringExtra(EXTRA_NAME);
        mExtraMobile = intent.getStringExtra(EXTRA_MOBILE);
        mExtraAddress = intent.getStringExtra(EXTRA_ADDRESS);
        mExtraId = intent.getStringExtra(EXTRA_ID);
        mExtraProvince = intent.getStringExtra(EXTRA_PROVINCE);
        mExtraDistrict = intent.getStringExtra(EXTRA_DISTRICT);
        mExtraPostcode = intent.getStringExtra(EXTRA_WARD);

        initUI();
        mLocationManager = new LocationManagerVn(this);
        initExtraUI();
    }

    private void initUI() {
        idToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        idToolbar.setTitle(R.string.address_edit);
        btnSave = (RelativeLayout) findViewById(R.id.layout_save_address_btn);
        layoutprogress = (RelativeLayout) findViewById(R.id.progress_layout);
        mProvinceTextView = (TextView) findViewById(R.id.textview_province);
        mDistrictTextView = (TextView) findViewById(R.id.textview_district);
        mPostcodeTextView = (TextView) findViewById(R.id.textview_postcode);

        mProvinceLayout = findViewById(R.id.layout_province);
        mDistrictLayout = findViewById(R.id.layout_district);
        mPostcodeLayout = findViewById(R.id.layout_postcode);

        mProvinceLayout.setOnClickListener(this);
        mDistrictLayout.setOnClickListener(this);
        mPostcodeLayout.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void initExtraUI(){
        if (mExtraName != null) {
            setEditName(mExtraName);
        }
        if (mExtraMobile != null) {
            setEditMobile(mExtraMobile);
        }
        if (mExtraAddress != null) {
            setEditAddress(mExtraAddress);
        }

        if(!TextUtils.isEmpty(mExtraProvince)){
            mUserOptProvince = mLocationManager.newRegion(mExtraProvince);
            if(mUserOptProvince!=null){
                mProvinceTextView.setText(mExtraProvince);
            }
        }
        if(!TextUtils.isEmpty(mExtraDistrict)){
            mUserOptDistrict = mLocationManager.newRegion(mExtraDistrict);
            if(mUserOptDistrict!=null){
                mDistrictTextView.setText(mExtraDistrict);
            }
        }
        if(!TextUtils.isEmpty(mExtraPostcode)){
            mPostcodeTextView.setText(mExtraPostcode);
            mUserOptPostcode = mExtraPostcode;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.closeDatabase();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_province:
                showProvinceSelector(v);
                break;
            case R.id.layout_district:
                showDistrictSelector(v);
                break;
            case R.id.layout_postcode:
                showPostcodeSelector(v);
                break;
            case R.id.layout_save_address_btn:
                doSaveViewClick(v);
                break;
        }
    }

    private void showProvinceSelector(final View view) {
        view.setEnabled(false);
        new AsyncTask<String, Integer, List<LocationManagerVn.Region>>() {
            @Override
            protected List<LocationManagerVn.Region> doInBackground(String... params) {
                return mLocationManager.getProvince();
            }
            @Override
            protected void onPostExecute(final List<LocationManagerVn.Region> strings) {
                HBLog.d(TAG + "onPostExecute " + strings);
                new AlertDialog.Builder(AddressEditActivityVn.this)
                        .setAdapter(new DialogLisAdapter(GlobalContext.get(), strings), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LocationManagerVn.Region region = strings.get(which);
                                        dialog.dismiss();
                                        if(mUserOptProvince==null||!mUserOptProvince.enValue.equals(region.enValue)){
                                            mUserOptProvince = region;
                                            mProvinceTextView.setText(region.enValue);

                                            mUserOptDistrict = null;
                                            mDistrictTextView.setText("");
                                            mUserOptPostcode = null;
                                            mPostcodeTextView.setText("");
                                        }
                                    }
                                }).create().show();
                view.setEnabled(true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showDistrictSelector(final View view) {
        if(mUserOptProvince == null){
            ToastUtils.showLongToast(this,R.string.location_province_empty_hint);
            return;
        }
        view.setEnabled(false);
        new AsyncTask<String, Integer, List<LocationManagerVn.Region>>() {
            @Override
            protected List<LocationManagerVn.Region> doInBackground(String... params) {
                return mLocationManager.getDistrict(mUserOptProvince);
            }
            @Override
            protected void onPostExecute(final List<LocationManagerVn.Region> strings) {
                HBLog.d(TAG + "onPostExecute " + strings);
                new AlertDialog.Builder(AddressEditActivityVn.this)
                        .setAdapter(new DialogLisAdapter(GlobalContext.get(), strings), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LocationManagerVn.Region region = strings.get(which);
                                        dialog.dismiss();
                                        if(mUserOptDistrict==null||!mUserOptDistrict.enValue.equals(region.enValue)){
                                            mUserOptDistrict = region;
                                            mDistrictTextView.setText(mUserOptDistrict.enValue);

                                            mUserOptPostcode = null;
                                            mPostcodeTextView.setText("");
                                        }
                                    }
                                }).create().show();
                view.setEnabled(true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showPostcodeSelector(final View view) {
        if(mUserOptProvince == null){
            ToastUtils.showLongToast(this,R.string.location_province_empty_hint);
            return;
        }
        if(mUserOptDistrict == null){
            ToastUtils.showLongToast(this,R.string.location_district_empty_hint);
            return;
        }
        view.setEnabled(false);
        new AsyncTask<String, Integer, List<String>>() {
            @Override
            protected List<String> doInBackground(String... params) {
                return mLocationManager.getPostcode(mUserOptProvince, mUserOptDistrict);
            }
            @Override
            protected void onPostExecute(final List<String> strings) {
                HBLog.d(TAG + "onPostExecute " + strings);
                new AlertDialog.Builder(AddressEditActivityVn.this)
                        .setAdapter(new DialogPostcodeAdapter(GlobalContext.get(), strings), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mUserOptPostcode = strings.get(which);
                                        dialog.dismiss();
                                        mPostcodeTextView.setText(mUserOptPostcode);
                                    }
                                }).create().show();
                view.setEnabled(true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void doSaveViewClick(View view) {
        if (!isOkCondition()) {
            return;
        }
        showProgress(true);
        String name = getEditName().getText().toString();
        String mobile = getEditNum().getText().toString();
        String address = getEditAddress().getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("mobile", mobile);
        map.put("address", address);
        map.put("state", mUserOptProvince.enValue);
        map.put("city", mUserOptDistrict.enValue);
        map.put("ward", mUserOptPostcode);
        String url = null;
        if (curmode == MODE_ADD) {
            url = HttpProtocol.URLS.ADDRESS_ADD;
        } else {
            url = HttpProtocol.URLS.ADDRESS_MODIFY;
            map.put("id", mExtraId);
        }
        Network.get(GlobalContext.get()).asyncPost(url, map, new Network.JsonCallBack<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                showProgress(false);
                HBLog.d(TAG + " onSuccess " + jsonObject.toString());
                ToastUtils.showShortToast(AddressEditActivityVn.this, "add success");
                Analytics.sendUIEvent(AnalyticsEvents.UserCenter.Add_Address_Edit, "PersonalCenter", null);
                finish();
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                showProgress(false);
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
            }

            @Override
            public Class<JSONObject> getObjectClass() {
                return JSONObject.class;
            }
        });
    }

    private boolean isOkCondition() {
        String name = getEditName().getText().toString();
        if (name == null || name.equals("")) {
            ToastUtils.showShortToast(this, R.string.address_edit_condition_name_tips);
            return false;
        }
        if(mUserOptProvince ==null){
            ToastUtils.showLongToast(this, R.string.location_province_empty_hint);
            return false;
        }
        if(mUserOptDistrict ==null){
            ToastUtils.showLongToast(this,R.string.location_district_empty_hint);
            return false;
        }
        /*if(mUserOptPostcode ==null){
            ToastUtils.showLongToast(this,R.string.location_postcode_empty_hint);
            return false;
        }*/
        String mobile = getEditNum().getText().toString();
        if (!FormatVerifyUtil.isMobileNO(this,mobile)) {
            ToastUtils.showShortToast(this, R.string.address_edit_condition_mobile_tips);
            return false;
        }
        String address = getEditAddress().getText().toString();
        if (address == null || address.equals("")) {
            ToastUtils.showShortToast(this, R.string.address_edit_condition_address_tips);
            return false;
        }

        return true;
    }

    private void setEditName(String name) {
        ((EditText) findViewById(R.id.edit_name)).setText(name);
        ((EditText) findViewById(R.id.edit_name)).setSelection(name.length());
    }

    private void setEditMobile(String mobile) {
        ((EditText) findViewById(R.id.edit_num)).setText(mobile);
        ((EditText) findViewById(R.id.edit_num)).setSelection(mobile.length());
    }

    private void showProgress(boolean isshow) {
        if (isshow) {
            layoutprogress.setVisibility(View.VISIBLE);
        } else {
            layoutprogress.setVisibility(View.GONE);
        }
    }

    private void setEditAddress(String address) {
        ((EditText) findViewById(R.id.edit_address)).setText(address);
        ((EditText) findViewById(R.id.edit_address)).setSelection(address.length());
    }

    private EditText getEditName() {
        return (EditText) findViewById(R.id.edit_name);
    }

    private EditText getEditNum() {
        return (EditText) findViewById(R.id.edit_num);
    }

    private EditText getEditAddress() {
        return (EditText) findViewById(R.id.edit_address);
    }

    class DialogLisAdapter extends BaseAdapter {

        private List<LocationManagerVn.Region> mLists;
        private Context mContext;
        private LayoutInflater mInflater;

        public DialogLisAdapter(Context context, List<LocationManagerVn.Region> list) {
            mLists = list;
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return mLists.size();
        }

        @Override
        public Object getItem(int i) {
            return mLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.dialog_listitem_text, viewGroup, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final LocationManagerVn.Region language = mLists.get(position);
            holder.textView.setText(language.enValue);

            return convertView;
        }

        class Holder {
            TextView textView;
            public Holder(View view) {
                textView = (TextView) view.findViewById(R.id.textview_1);
            }
        }
    }

    class DialogPostcodeAdapter extends BaseAdapter {

        private List<String> mLists;
        private Context mContext;
        private LayoutInflater mInflater;

        public DialogPostcodeAdapter(Context context, List<String> list) {
            mLists = list;
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return mLists.size();
        }

        @Override
        public Object getItem(int i) {
            return mLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.dialog_listitem_text, viewGroup, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final String language = mLists.get(position);
            holder.textView.setText(language);

            return convertView;
        }

        class Holder {
            TextView textView;
            public Holder(View view) {
                textView = (TextView) view.findViewById(R.id.textview_1);
            }
        }
    }
}