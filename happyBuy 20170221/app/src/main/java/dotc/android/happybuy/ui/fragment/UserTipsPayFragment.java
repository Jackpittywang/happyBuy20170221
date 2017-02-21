package dotc.android.happybuy.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoNone;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.part.PartCallBack;
import dotc.android.happybuy.modules.part.PartFragment;
import dotc.android.happybuy.modules.part.PartObject;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.push.DynamicTopicManager;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.pay.PayActivity;
import dotc.android.happybuy.modules.recharge.RechargeActivity;
import dotc.android.happybuy.uibase.app.BaseFragment;
import dotc.android.happybuy.util.DisplayUtils;
import dotc.android.happybuy.util.ToastUtils;

public class UserTipsPayFragment extends BaseFragment implements PartCallBack {
    public static  String START_XPOINT="start_xpoint";
    public static  String START_YPOINT="start_ypoint";
    public static  String PRODUCT="product";
    public static  String EXTRA_TIMES="extra_times";
    private int xPoint;
    private int yPoint;
    private PojoProduct pojoProduct;
    private int mExtraTime;

    private RelativeLayout mProduct;
   private ImageView mGifImage;

    private PartFragment mPartFragment;

    public static UserTipsPayFragment newInstance(int x, int y, PojoProduct product,int mExtraTimes) {
        UserTipsPayFragment fragment = new UserTipsPayFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(START_XPOINT,x);
        bundle.putInt(START_YPOINT,y);
        bundle.putSerializable(PRODUCT,product);
        bundle.putInt(EXTRA_TIMES,mExtraTimes);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xPoint = getArguments().getInt(START_XPOINT);
        yPoint = getArguments().getInt(START_YPOINT);
        pojoProduct= (PojoProduct) getArguments().getSerializable(PRODUCT);
        mExtraTime = getArguments().getInt(EXTRA_TIMES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_tips, container, false);
        mProduct = (RelativeLayout) view.findViewById(R.id.layout_section);
        mGifImage = (ImageView) view.findViewById(R.id.iv_gif);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mProduct.getLayoutParams();
        layoutParams.topMargin = yPoint- DisplayUtils.getStatusBarHeight(getContext());
        layoutParams.leftMargin = xPoint;
        mProduct.setLayoutParams(layoutParams);

//        layoutParams.setMargins(xPoint,yPoint- PrefUtils.getInt(MainTabActivity.STATUSBARHEIGHT,0),0,0);
//        Log.d("fffffff",PrefUtils.getInt(MainTabActivity.STATUSBARHEIGHT,0)+"");
//        mProduct.setLayoutParams(layoutParams);
        mProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Click_Pay_Guide, null, null);
                doPayPressed(v);
            }
        });
        view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Click_Pay_Gray, null, null);
            }
        });
        return view;
    }
    private void doPayPressed(View v) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.order_paying));
        Map<String,Object> params = new HashMap<>();
        params.put("product_item_id",pojoProduct.productItemId);
        params.put("count",String.valueOf(mExtraTime));
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.BUY, params, new Network.JsonCallBack<PojoNone>() {
            @Override
            public void onSuccess(PojoNone list) {
                Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Finish_Guide, null, null);
                HBLog.d(TAG + " onSuccess " + list);
                PrefUtils.putBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE,true);
                setUserFinishNewbieGuide();
                dialog.dismiss();
                int coin = PrefUtils.getInt(PrefConstants.UserInfo.COIN, 0) - mExtraTime;
                PrefUtils.putInt(PrefConstants.UserInfo.COIN, coin);
                ToastUtils.showLongCenterToast(GlobalContext.get(), R.string.order_pay_done);
                DynamicTopicManager.getInstance(GlobalContext.get()).trigger(pojoProduct);

                Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID,pojoProduct.productId);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ITEM_ID,pojoProduct.productItemId);
                intent.putExtra(GoodsDetailActivity.SHOW_ORDER,true);
                startActivity(intent);
                Intent data = new Intent();
                data.putExtra(PayActivity.UPDATEUI,false);
                getActivity().setResult(Activity.RESULT_OK,data);
                getActivity().finish();
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                Analytics.sendUIEvent(AnalyticsEvents.ProductGuide.Finish_Guide_Error, null, null);
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

    private void showPayFailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                intent.putExtra(RechargeActivity.EXTRA_ACTIVITY_FROM,RechargeActivity.ACTIVITY_FROM_PAY);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    private void showProductTimeoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.product_timeout);
        builder.setMessage(R.string.buy_fail_period_out);
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                getActivity().finish();
//            }
//        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID, pojoProduct.productId);
//                intent.putExtra(GoodsDetailActivity.USER_GUIDE,true);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.create().show();
    }

    private void showTimesLessDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.order_pay_fail);
        builder.setMessage(R.string.buy_fail_times_not_enough);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), GoodsDetailActivity.class);
                intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID, pojoProduct.productId);
//                intent.putExtra(GoodsDetailActivity.USER_GUIDE,true);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(this).load(R.drawable.bg_buttom_gif).asGif().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mGifImage);
    }


    @Override
    public void onPartCallBack(boolean paySuceess,PartObject partObject){

    }
}
