package dotc.android.happybuy.modules.pay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.dialog.RateActivity;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoCouponsItem;
import dotc.android.happybuy.http.result.PojoNone;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.home.NewUserGuideActivity;
import dotc.android.happybuy.modules.recharge.RechargeActivity;
import dotc.android.happybuy.modules.recharge.TopupActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.push.DynamicTopicManager;
import dotc.android.happybuy.ui.adapter.AvailableRedBagGrideListAdpter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.ui.fragment.UserTipsPayFragment;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 */
public class PayActivity extends BaseActivity {

    public static final String EXTRA_PRODUCT = "extra_product";
    public static final String EXTRA_TIMES = "extra_times";
    public static final String UPDATEUI = "updateui";

    private HBToolbar mToolbar;
    private Button mButton;
    private TextView mPayAmountTextView;
    private TextView mAccoutBalanceTextView;
    private TextView mDutaction;
    private ImageView mArrow;

    private PojoProduct mExtraProduct;
    private int mExtraTimes;

    private GridView mGridView;
    private AvailableRedBagGrideListAdpter mAvailableRedBagGrideListAdpter;
    private LinearLayout mRedBagSelect;
    private String lastNum = "0";
    public static final int PAGE_SIZE = 10;
    private boolean mShowRedBagSelect=true;
    private int mItemSelect=-1;
    public boolean isNewUser;
    private int[] location = new int[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        readExtraFromIntent();
        initActionbar();
        initUI();
        initCouponsGridView();
        loadCouponsData();
        initData();

        mPayAmountTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mButton.getLocationInWindow(location);
                isUserGuild();
                mPayAmountTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void isUserGuild(){
        if(isNewUser&&!AppUtil.isActivityDestroyed(this)){
            UserTipsPayFragment userTipsPayFragment= UserTipsPayFragment.newInstance(location[0],location[1],mExtraProduct,mExtraTimes);
            this.getSupportFragmentManager().beginTransaction().replace(R.id.first_open,userTipsPayFragment).commitAllowingStateLoss();
        }
    }
    private void readExtraFromIntent(){
        isNewUser = getIntent().getBooleanExtra(GoodsDetailActivity.USER_GUIDE,false);
        mExtraProduct = (PojoProduct) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        mExtraTimes = getIntent().getIntExtra(EXTRA_TIMES,1);
    }

    @Override
    public void onBackPressed() {
        if(isNewUser){
            return;
        }
        super.onBackPressed();
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_pay);
        mToolbar.setDisplayHomeAsUpEnabled(true);
    }

    private void initUI() {
        mArrow = (ImageView) findViewById(R.id.iv_arrow);
        mDutaction = (TextView) findViewById(R.id.tv_deduction);
        mPayAmountTextView = (TextView) findViewById(R.id.textview_amount);
        mAccoutBalanceTextView = (TextView) findViewById(R.id.textview_balance);
        mButton = (Button) findViewById(R.id.button_pay);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPayPressed(v);
            }
        });

    }

    private void initData(){
        int coin = PrefUtils.getInt(PrefConstants.UserInfo.COIN, 0);
        mPayAmountTextView.setText(getString(R.string.lable_coin, mExtraTimes));
        mAccoutBalanceTextView.setText(getString(R.string.lable_coin, coin));
    }


    private void doPayPressed(View v) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.order_paying));
        Map<String,Object> params = new HashMap<>();
        if(mItemSelect==-1){
            params.put("user_coupon_id","");
        }else {
            params.put("user_coupon_id",mAvailableRedBagGrideListAdpter.getItem(mItemSelect).id);
        }
        params.put("product_item_id",mExtraProduct.productItemId);
        params.put("count",String.valueOf(mExtraTimes));
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.BUY, params, new Network.JsonCallBack<PojoNone>() {
            @Override
            public void onSuccess(PojoNone list) {
                if(!PrefUtils.getBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE,true) ){
//                    PrefUtils.putBoolean(MainTabActivity.HAVA_REQUEST_FINISH_STATUS,true);
                    PrefUtils.putBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE,true);
                    setUserFinishNewbieGuide();
                }
                HBLog.d(TAG + " onSuccess " + list);
                dialog.dismiss();
                int coin;
                if(mItemSelect==-1){
                   coin = PrefUtils.getInt(PrefConstants.UserInfo.COIN, 0) - mExtraTimes;
                }else {
                   coin = PrefUtils.getInt(PrefConstants.UserInfo.COIN, 0) - mExtraTimes+Integer.parseInt(mAvailableRedBagGrideListAdpter.getItem(mItemSelect).amount) ;
                }
//                int coin = PrefUtils.getInt(PrefConstants.UserInfo.COIN, 0) - mExtraTimes+Integer.parseInt(mAvailableRedBagGrideListAdpter.getItem(mItemSelect).amount) ;
                PrefUtils.putInt(PrefConstants.UserInfo.COIN, coin);
                ToastUtils.showLongCenterToast(GlobalContext.get(), R.string.order_pay_done);
                DynamicTopicManager.getInstance(GlobalContext.get()).trigger(mExtraProduct);
                Intent data = new Intent();
                data.putExtra(UPDATEUI,true);
                setResult(RESULT_OK,data);
                int count=PrefUtils.getInt(PrefConstants.CommentTime.PAY_FINISHED_COMMENT_TIME,0);
                //次数限制 默认为1
                if(count<AbConfigManager.getInstance(GlobalContext.get()).getConfig().rate_app.after_buy_times) {
//                if(count<PrefUtils.getInt(PrefConstants.CommentTime.PAY_FINISHED_LIMIT_COMMENT_TIME,1)) {
                    PrefUtils.putInt(PrefConstants.CommentTime.PAY_FINISHED_COMMENT_TIME, count+1);
                    Intent comment = new Intent(PayActivity.this, RateActivity.class);
                    startActivity(comment);
                }
                finish();
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                dialog.dismiss();
                if (code == HttpProtocol.BUY_CODE.MONEY_NOT_ENOUGH) {
                    showPayFailDialog();
                } else if (code == HttpProtocol.BUY_CODE.SOLDOUT || code == HttpProtocol.BUY_CODE.NOPRODUCT) {
                    showProductTimeoutDialog();
                } else if (code == HttpProtocol.BUY_CODE.TIMES_NOT_ENOUGH) {
                    showTimesLessDialog();
                } else {
                    ToastUtils.showLongToast(GlobalContext.get(), R.string.order_pay_fail);
                }
            }

            @Override
            public Class<PojoNone> getObjectClass() {
                return PojoNone.class;
            }
        });
        dialog.show();
    }

    private void showPayFailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.order_pay_fail);
        builder.setMessage(R.string.buy_fail_money_not_enough);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.recharge_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(PayActivity.this, TopupActivity.class);
                intent.putExtra(TopupActivity.EXTRA_ACTIVITY_FROM,TopupActivity.ACTIVITY_FROM_PAY);
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }

    private void showProductTimeoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.product_timeout);
        builder.setMessage(R.string.buy_fail_period_out);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(PayActivity.this, GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID, mExtraProduct.productId);
                startActivity(intent);
                finish();
            }
        });
        builder.create().show();
    }

    private void showTimesLessDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.order_pay_fail);
        builder.setMessage(R.string.buy_fail_times_not_enough);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void initCouponsGridView(){
        mRedBagSelect= (LinearLayout) findViewById(R.id.ll_red_bag_select);
        mGridView = (GridView)findViewById(R.id.gridview_list);
        mRedBagSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShowRedBagSelect) {
                    mGridView.setVisibility(View.GONE);
                    mShowRedBagSelect = false;
                    Animation animation=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.accrow_rotate);
                    animation.setFillAfter(true);
                    mArrow.startAnimation(animation);

                } else {
                    mShowRedBagSelect = true;
                    mGridView.setVisibility(View.VISIBLE);
                    Animation animation=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.accrow_rotate_close);
                    mArrow.startAnimation(animation);

                }
            }
        });

        initOnItemClick();
    }

    private  void initOnItemClick(){
        mAvailableRedBagGrideListAdpter = new AvailableRedBagGrideListAdpter(getApplicationContext());
        mGridView.setAdapter(mAvailableRedBagGrideListAdpter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mItemSelect==position){
                    //如果-1表明没有选红包
                    mItemSelect=-1;
                    mDutaction.setText(getString(R.string.cut_coin,0+""));
                }else {
                    mItemSelect=position;
                    mDutaction.setText(getString(R.string.cut_coin,mAvailableRedBagGrideListAdpter.getItem(position).amount));
                }
                mAvailableRedBagGrideListAdpter.updateSelect(position);
            }
        });
    }

    private void loadCouponsData(){
        Map<String,Object> params = new HashMap<>();
        params.put("product_id",mExtraProduct.productId);
        params.put("product_item_id",mExtraProduct.productItemId);
        params.put("purchase_times",mExtraTimes);
//        params.put("purchase_times",1000);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PREPAY, params, new Network.JsonCallBack<PojoCouponsItem>() {
            @Override
            public void onSuccess(PojoCouponsItem pojoCouponsItem) {
                HBLog.d(TAG + " onSuccess " + pojoCouponsItem);
                /*if (lastNum.equals("") || lastNum.equals("0")) {
                    HBLog.i(TAG + "这是一次刷新操作,清空list");
                    mAvailableRedBagGrideListAdpter.clearData();
                    if (pojoCouponsItem.list.size() == 0) {
                        mRedBagSelect.setVisibility(View.GONE);
                        mGridView.setVisibility(View.GONE);
                    }
                }*/
                if ( (lastNum.equals("") || lastNum.equals("0"))) {
                    mAvailableRedBagGrideListAdpter.clearData();
                    PrefUtils.putInt(GlobalContext.get(),PrefConstants.UserInfo.COIN, pojoCouponsItem.coin_count);
                    mAccoutBalanceTextView.setText(getString(R.string.lable_coin, pojoCouponsItem.coin_count));
                    if (pojoCouponsItem.list.size() == 0) {
                        mRedBagSelect.setVisibility(View.GONE);
                        mGridView.setVisibility(View.GONE);
                        mItemSelect=-1;
                    }else {
                        mItemSelect=0;
                        mRedBagSelect.setVisibility(View.VISIBLE);
                        mGridView.setVisibility(View.VISIBLE);
                        HBLog.i(TAG + "加载数据到list");
                        mAvailableRedBagGrideListAdpter.addData(pojoCouponsItem.list);
                        mAvailableRedBagGrideListAdpter.notifyDataSetChanged();
                        mDutaction.setText(getString(R.string.cut_coin,mAvailableRedBagGrideListAdpter.getItem(0).amount));
                    }

                } else {
                    HBLog.i(TAG + "由于请求的lastNum 和 请求到的last_numb 相同，所以不做加载");
                }

            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
                mRedBagSelect.setVisibility(View.GONE);
                mGridView.setVisibility(View.GONE);
                mItemSelect=-1;
            }

            @Override
            public Class<PojoCouponsItem> getObjectClass() {
                return PojoCouponsItem.class;
            }
        });

    }

    private void setUserFinishNewbieGuide() {
        String uid = PrefUtils.getString(GlobalContext.get(), PrefConstants.UserInfo.UID, "");
        Map<String,Object> params = new HashMap<>();
        params.put("from_uid", uid);
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.FINISHNEWBIEGUIDE, params, new Network.JsonCallBack<PojoNone>() {
            @Override
            public void onSuccess(PojoNone list) {
                HBLog.d(TAG + " onSuccess " + list);

            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
            }

            @Override
            public Class<PojoNone> getObjectClass() {
                return PojoNone.class;
            }
        });
    }

}
