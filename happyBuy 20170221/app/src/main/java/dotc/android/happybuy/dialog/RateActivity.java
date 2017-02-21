package dotc.android.happybuy.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import android.widget.TextView;



import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;

import dotc.android.happybuy.log.HBLog;

import dotc.android.happybuy.util.AppUtil;

public class RateActivity extends Activity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private TextView mCommentContent;
    private TextView mCommentTitle;

    private TextView mLaterToComment;
    private TextView mToComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        HBLog.d(TAG + " onCreate");
        setContentView(R.layout.activity_rate);
        initUI();
        Analytics.sendUIEvent(AnalyticsEvents.CommentDialog.Show_Dialog, null, null);
        this.setFinishOnTouchOutside(false);
    }


    private void initUI() {
        mCommentTitle = (TextView) findViewById(R.id.tv_comment_title);
        mCommentContent = (TextView) findViewById(R.id.tv_comment_content);
        mLaterToComment = (TextView) findViewById(R.id.tv_later_to_comment);
        mToComment = (TextView) findViewById(R.id.tv_to_comment);
        mLaterToComment.setOnClickListener(this);
        mToComment.setOnClickListener(this);

        showView();
    }

    private void showView() {
                Analytics.sendUIEvent(AnalyticsEvents.CommentDialog.Show_Dialog, null, null);
                mCommentContent.setText(AbConfigManager.getInstance(GlobalContext.get()).getConfig().rate_app.content.getText());
                mCommentTitle.setText(AbConfigManager.getInstance(GlobalContext.get()).getConfig().rate_app.title.getText());


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_later_to_comment:
                Analytics.sendUIEvent(AnalyticsEvents.CommentDialog.Click_Dialog_No, null, null);
                finish();
                break;
            case R.id.tv_to_comment:
                Analytics.sendUIEvent(AnalyticsEvents.CommentDialog.Click_Dialog_Yes, null, null);
                String clickUrlBrowser =  AppUtil.isAppInstalled(this,"com.android.vending") ? "market://details?id=go.android.gogobuy" : "https://play.google.com/store/apps/details?id=go.android.gogobuy";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(clickUrlBrowser));
                startActivity(intent);
                finish();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {

    }





}
