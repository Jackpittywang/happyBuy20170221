package dotc.android.happybuy.modules.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.dialog.RateActivity;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.me.MeFragment;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

public class TransactionSuccessfulActivity extends Activity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    public static String BET_TIMES="bet_times";
    public static String REMAIN_TIMES="remain_times";
    public static String IS_NEED_SHOW_GUIDE="is_need_show_guide";


    private String mBetTimes;
    private String mRemainTimes;
    private boolean mIsNeedShowGuide;
    public TextView betTimes;
    public TextView remainTimes;
    public TextView  mTextContinue;
    public TextView mTextSeeOrder;
    public ImageView mImageClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        HBLog.d(TAG + " onCreate");
        setContentView(R.layout.transaction_successful_dialog);
        readExtraFromIntent();
        initUI();
        this.setFinishOnTouchOutside(false);
    }
    private void readExtraFromIntent(){
        if(getIntent()!=null){
            mRemainTimes = getIntent().getStringExtra(REMAIN_TIMES);
            mBetTimes = getIntent().getStringExtra(BET_TIMES);
            mIsNeedShowGuide = getIntent().getBooleanExtra(IS_NEED_SHOW_GUIDE,false);
        }

    }

    private void initUI() {
        betTimes=(TextView) findViewById(R.id.tv_bet_times);
        remainTimes=(TextView)findViewById(R.id.tv_remain_times);
        mTextContinue=(TextView)findViewById(R.id.tv_continue);
        mTextSeeOrder=(TextView)findViewById(R.id.tv_see_order);
        mImageClose=(ImageView)findViewById(R.id.iv_close);

        showView();
    }

    private void showView() {
        betTimes.setText(mBetTimes);
        remainTimes.setText(mRemainTimes);
        mTextContinue.setOnClickListener(this);
        mTextSeeOrder.setOnClickListener(this);
        mImageClose.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_continue:{
                if(!mIsNeedShowGuide) {
                    int count=PrefUtils.getInt(PrefConstants.CommentTime.PAY_FINISHED_COMMENT_TIME,1);
                    //配置第几次弹五星好评框
                    if(count== AbConfigManager.getInstance(GlobalContext.get()).getConfig().rate_app.after_buy_times) {
                        Intent comment = new Intent(GlobalContext.get(), RateActivity.class);
                        startActivity(comment);
                    }
                    PrefUtils.putInt(PrefConstants.CommentTime.PAY_FINISHED_COMMENT_TIME, count+1);
                }
                finish();
            }
            break;
            case R.id.iv_close:
                if(!mIsNeedShowGuide) {
                    int count=PrefUtils.getInt(PrefConstants.CommentTime.PAY_FINISHED_COMMENT_TIME,1);
                    //配置第几次弹五星好评框
                    if(count==AbConfigManager.getInstance(GlobalContext.get()).getConfig().rate_app.after_buy_times) {
                        Intent comment = new Intent(GlobalContext.get(), RateActivity.class);
                        startActivity(comment);
                    }
                    PrefUtils.putInt(PrefConstants.CommentTime.PAY_FINISHED_COMMENT_TIME, count+1);
                }
                finish();
                break;
            case R.id.tv_see_order:
                Intent intent = new Intent(TransactionSuccessfulActivity.this,MainTabActivity.class);
                intent.putExtra(MainTabActivity.EXTRA_TAB_INDEX,MainTabActivity.TAB_INDEX_3);
                Bundle args = new Bundle();
                args.putInt(MeFragment.EXTRA_INDEX,MeFragment.TAB_INDEX_0);
                args.putBoolean(MeFragment.EXTRA_REFRESH,true);
                intent.putExtra(MainTabActivity.EXTRA_TAB_ARGS,args);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
