package dotc.android.happybuy.modules.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoHotword;
import dotc.android.happybuy.http.result.PojoHotwordList;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.http.result.PojoProductList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.part.PartCallBack;
import dotc.android.happybuy.modules.part.PartObject;
import dotc.android.happybuy.ui.adapter.ProductAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.modules.search.widget.HotwordCrowdsView;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 * 搜索
 *
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener,
        ProductAdapter.OnProductPartListener,PartCallBack,View.OnTouchListener {

//    private View mLoadingView;
//    private View mContentView;
    private View mEmptyView;
    private View mHotwordView;

    private EditText mInputEditText;
    private View mClearImageView;
    private View mProgressBar;
    private View mSearchButton;
    private ListView mListView;
    private HotwordCrowdsView mHotwordCrowdsView;

    private ProductAdapter mProductsAdapter;
    private PartFragment mPartFragment;

    private String mLastSearchKey;
    private RelativeLayout mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initUI();
        addViewsListener();
        initData();
        loadHotwordTask();
        showSoftInput();
    }
    private void showSoftInput(){
        mInputEditText.setFocusable(true);
        mInputEditText.setFocusableInTouchMode(true);
        mInputEditText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager)mInputEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mInputEditText, 0);
            }
        },998);

    }


    private void initUI() {
        mInputEditText = (EditText) findViewById(R.id.edit_input);
        mListView = (ListView) findViewById(R.id.listview);
        mHotwordCrowdsView = (HotwordCrowdsView) findViewById(R.id.crowds_hotword);
        mContentView = (RelativeLayout) findViewById(R.id.layout_content);
        mEmptyView = findViewById(R.id.layout_empty_view);
        mHotwordView = findViewById(R.id.layout_hotword);
        mSearchButton = findViewById(R.id.layout_search);
        mClearImageView = findViewById(R.id.imageview_clear);
        mProgressBar = findViewById(R.id.progressbar);

        mHotwordView.setOnTouchListener(this);
        mListView.setOnTouchListener(this);
        mEmptyView.setOnTouchListener(this);

        mInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mInputEditText.isFocused()){
                    Analytics.sendUIEvent(AnalyticsEvents.ProductSearch.Click_Bar_Search, null, null);
                }
            }
        });

    }



    private void hideSoftInput(View view,EditText editText){
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        imm.restartInput(editText);
    }


    private void addViewsListener(){
        mSearchButton.setOnClickListener(this);
        mClearImageView.setOnClickListener(this);
        findViewById(R.id.layout_back).setOnClickListener(this);
        mHotwordCrowdsView.setOnItemClickListener(new HotwordCrowdsView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, PojoHotword hotword) {
                mInputEditText.setText(hotword.name);
                mInputEditText.setSelection(hotword.name.length());
                mLastSearchKey = hotword.name;
                doSearchTask(mInputEditText, hotword.name);
                Analytics.sendUIEvent(AnalyticsEvents.ProductSearch.Click_Search_HotWord, hotword.name, null);
            }
        });
        mInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(mInputEditText.getText().toString().trim())) {
                    mListView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    mHotwordView.setVisibility(View.VISIBLE);
                    mClearImageView.setVisibility(View.INVISIBLE);
                } else {
                    mClearImageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initData(){
        mProductsAdapter = new ProductAdapter(this,"search",this);
        mListView.setAdapter(mProductsAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_search:
                hideSoftInput(mSearchButton,mInputEditText);
                doSearchViewClick(v);
                break;
            case R.id.layout_back:
                finish();
                break;
            case R.id.imageview_clear:
                mInputEditText.setText("");
                mHotwordView.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPartFragment!=null&&mPartFragment.onBackPressed()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doSearchViewClick(View view){
        String text = mInputEditText.getText().toString().trim();
        if(TextUtils.isEmpty(text)){
            ToastUtils.showLongToast(this,R.string.input_search_empty);
            return;
        }
        Analytics.sendUIEvent(AnalyticsEvents.ProductSearch.Click_Search_Butten, text, null);
        mLastSearchKey = text;
        doSearchTask(view,text);
    }

    private void doSearchTask(final View view, final String text){
        Map<String,Object> param = new HashMap<>();
        param.put("keyword", String.valueOf(text));
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.SEARCH, param, new Network.JsonCallBack<PojoProductList>() {
            @Override
            public void onSuccess(PojoProductList list) {
                view.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                mClearImageView.setVisibility(View.VISIBLE);
                if (isFinishing()||!text.equals(mLastSearchKey)) {
                    return;
                }
                if (list.productList.size() > 0) {
                    mProductsAdapter.updateList(list.productList);
                    mListView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                } else {
                    mListView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                mHotwordView.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG+" onFailed code:"+code+" message:"+message+" "+e);
                view.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                mClearImageView.setVisibility(View.VISIBLE);
                if (e != null) {
                    ToastUtils.showLongToast(GlobalContext.get(), R.string.network_exception);
                } else if(code == -2){
                    if(mProductsAdapter.getCount()==0){
                        mListView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        ToastUtils.showLongToast(GlobalContext.get(), R.string.search_result_empty);
                    }
                }
            }

            @Override
            public Class<PojoProductList> getObjectClass() {
                return PojoProductList.class;
            }
        });
        view.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        mClearImageView.setVisibility(View.GONE);
    }

    private void loadHotwordTask(){
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.HOT_WORD, null, new Network.JsonCallBack<PojoHotwordList>() {
            @Override
            public void onSuccess(PojoHotwordList hotword) {
                if (isFinishing()) {
                    return;
                }
                mHotwordCrowdsView.setDataStrings(hotword.list);
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed code:" + code + " message:" + message + " " + e);
//                mProgressBar.setVisibility(View.GONE);
//                mClearImageView.setVisibility(View.VISIBLE);
                if (e != null) {
                    ToastUtils.showLongToast(GlobalContext.get(), R.string.network_exception);
                }
            }

            @Override
            public Class<PojoHotwordList> getObjectClass() {
                return PojoHotwordList.class;
            }
        });
    }

    @Override
    public void onProductPartClick(View view, PojoProduct product) {
        mPartFragment = PartFragment.newPartFragment(PartFragment.TYPE_OTHER, product, this);
        mPartFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onPartCallBack(boolean paySuceess,PartObject partObject){
        if(paySuceess){
            doSearchTask(mSearchButton,mLastSearchKey);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(v.getId()){
            case R.id.layout_hotword:
                hideSoftInput(mHotwordView,mInputEditText);
                break;
            case R.id.listview:
                hideSoftInput(mListView,mInputEditText);
                break;
            case R.id.layout_empty_view:
                hideSoftInput(mEmptyView,mInputEditText);
                break;
        }
        return false;
    }
}
