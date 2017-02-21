package dotc.android.happybuy.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import dotc.android.happybuy.R;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.setting.feedback.FeedBackActivity;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.app.ToolsActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.uibase.widget.OldProgressBar;

/**
 * Created by wangjun on 16/3/17.
 */
public class WebActivity extends ToolsActivity {

    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_NO_TITLE = "extra_no_title";
    private HBToolbar mToolbar;
    private FrameLayout mFrameLayout;
    private OldProgressBar mOldProgressBar;
    private WebView mWebView;
    private String mExtraUrl;
    private String mExtraTitle;
    private boolean mExtraNoTitle;

    public static final String EXTRA_TYPE_KEY = "type";

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_FEEDBACK = 1;

    public int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainArgsFromIntent(getIntent());
        setContentView(R.layout.activity_web);
        initActionbar();
        initWebView();
        addViewsListener();
        mWebView.loadUrl(mExtraUrl);
        stylize(getIntent());
    }

    private void stylize(Intent intent){
        type = intent.getIntExtra(EXTRA_TYPE_KEY,TYPE_NORMAL);
        switch (type){
            case TYPE_FEEDBACK:
                if (mToolbar != null){
                    mToolbar.setRightItem(R.drawable.ic_costmer_service, new HBToolbar.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view) {
                            Intent intent = new Intent(WebActivity.this, FeedBackActivity.class);
                            intent.putExtra(FeedBackActivity.FROM_KEY,FeedBackActivity.FROM_HELP_CENTER);
                            startActivity(intent);
                        }
                    });
                }
                break;
        }
    }

    private void obtainArgsFromIntent(Intent intent) {
        mExtraUrl = intent.getStringExtra(EXTRA_URL);
        mExtraTitle = intent.getStringExtra(EXTRA_TITLE);
        mExtraNoTitle = intent.getBooleanExtra(EXTRA_NO_TITLE, false);
        HBLog.d(TAG+" obtainArgsFromIntent "+mExtraUrl);
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setDisplayHomeAsUpEnabled(true);
        if(!TextUtils.isEmpty(mExtraTitle)){
            mToolbar.setTitle(mExtraTitle);
        }
        if(mExtraNoTitle){
            mToolbar.setVisibility(View.GONE);
        }
        mToolbar.setLeftItem(new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (!handleBackHistory()) {
                    finish();
                }
            }
        });
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if(!TextUtils.isEmpty(mExtraTitle)){
//            getSupportActionBar().setTitle(mExtraTitle);
//        }
    }

    private void initWebView() {
        mFrameLayout = (FrameLayout) findViewById(R.id.layout_web);
        mOldProgressBar = (OldProgressBar) findViewById(R.id.progressbar);
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir()
                .getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);

        //
        webSettings.setBuiltInZoomControls(false);
//
//		webSettings.setPluginsEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }


    private void addViewsListener() {
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
//		mWebView.onResume();
        super.onResume();
    }
    @Override
    protected void onPause() {
//		mWebView.onPause();
        super.onPause();
    }
    /**
     * 处理webview的回退
     *
     * @return
     */
    protected boolean handleBackHistory() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (handleBackHistory()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        try {
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
            mWebView.clearView();
            mWebView.onPause();
            mWebView.destroy();
            mWebView = null;
//		mFrameLayout.removeAllViews();
            super.onDestroy();
            if(ToolsActivity.mInstanceCount == 0){
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e){

        }
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url != null && url.equals("about:blank"))return false;
            if(!URLUtil.isValidUrl(url)){
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onConsoleMessage(String message, int lineNumber,
                                     String sourceID) {
            HBLog.i(TAG + " javascript output: " + message);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if(TextUtils.isEmpty(mExtraTitle)){
                mToolbar.setTitle(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            HBLog.i(TAG + " onProgressChanged newProgress: " + newProgress);
            mOldProgressBar.setProgress(newProgress,100);
            if(newProgress==100){
                mOldProgressBar.setVisibility(View.GONE);
            } else {
                mOldProgressBar.setVisibility(View.VISIBLE);
            }
        }
    };

}
