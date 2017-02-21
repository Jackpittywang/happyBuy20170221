package dotc.android.happybuy.modules.part;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.update.util.StringUtil;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoCoupons;
import dotc.android.happybuy.http.result.PojoCouponsItem;
import dotc.android.happybuy.http.result.PojoNone;
import dotc.android.happybuy.http.result.PojoPaySuccess;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.detail.GoodsDetailActivity;
import dotc.android.happybuy.modules.login.func.AccountHelper;
import dotc.android.happybuy.modules.part.guide.GuideController;
import dotc.android.happybuy.modules.part.widget.TimesClusterLayout;
import dotc.android.happybuy.modules.pay.TransactionSuccessfulActivity;
import dotc.android.happybuy.modules.recharge.TopupActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.push.DynamicTopicManager;
import dotc.android.happybuy.uibase.app.BaseFragment;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 * 参与
 */
public class PartFragment extends BaseFragment implements View.OnClickListener, View.OnTouchListener, CouponsListFragment.OnCouponsDismissListener {

    private static final String EXTRA_GOODS = "extra_goods";
    public final static String EXTRA_TYPE = "extra_type";
    public final static String AVAILABLE_STATUS = "1";

    public final static int TYPE_ALL = 0;
    public final static int TYPE_TEN = 1;
    public final static int TYPE_OTHER = 3;
    public final static int TYPE_USER_TIPS = 4;

    private final static int MSG_LONG_PRESS_ADD_START = 11;
    private final static int MSG_LONG_PRESS_MINUS_START = 12;
    private final static int MSG_LONG_PRESS_ADD = 13;
    private final static int MSG_LONG_PRESS_MINUS = 14;

    public MainHandler mainHandler;

    public HandlerThread longPressAddThread;
    public WorkHandler longPressAddHandler;
    public HandlerThread longPressMinusThread;
    public WorkHandler longPressMinusHandler;

    private boolean longPressAddEnabled;
    private boolean longPressMinusEnabled;

    private int mType;
    private int mMaxTimes;


    private String lastNum = "0";
    private List<PojoCoupons> pojoCouponsList = new ArrayList<>();
    private List<PojoCoupons> mAvailablePojoCouponsList = new ArrayList<>();
    private List<PojoCoupons> mUnAvailablePojoCouponsList = new ArrayList<>();
    private TextView mAccoutBalanceTextView;
    private TextView mCoinsUnitTextView;
    private int mItemSelect = -1;
    private int mCouponAmount = 0;

    public static PartFragment newPartFragment(int type, PojoProduct pojoProduct, PartCallBack dismissListener) {
        PartFragment as = new PartFragment();
        final Bundle args = new Bundle();
        args.putSerializable(EXTRA_GOODS, pojoProduct);
        args.putInt(EXTRA_TYPE, type);
        as.setArguments(args);
        as.mOnDismissListener = dismissListener;
        return as;
    }

    private PojoProduct mExtraPojoProduct;

    private GuideController mGuideController;

    private TextView mDecreaseTextView;
    private TextView mIncreaseTextView;
    private EditText mTimesEditText;
    private TimesClusterLayout mTimesClusterLayout;
    private TextView mPartButton;
    private ImageView mGoodsImageView;
    private TextView mTips;
    private TextView mTvCouponsCount;
    private TextView mTvTotal;
    private View mTopView;
    private View mBottomView;
    private PartCallBack mOnDismissListener;
    private View mContext;
    private ImageView mGifImage;
    private CouponsListFragment couponsListFragment;

    private boolean mTimesEditTextChanged;
    private Activity mActivity;
    private RelativeLayout mCouponsList;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String times = mTimesEditText.getText().toString().trim();
            if(StringUtil.isEmpty(times)){
                mTvTotal.setText(getString(R.string.bet_now_totle, 0 + ""));
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mTimesEditTextChanged) {
                return;
            }
            if (mExtraPojoProduct != null) {
                try {
                    int inputtimes = Integer.parseInt(s.toString());
                    if (inputtimes > mExtraPojoProduct.remainTimes || inputtimes > mMaxTimes) {
                        if (mExtraPojoProduct.remainTimes > mMaxTimes) {

                            mTimesEditTextChanged = true;
                            mTimesEditText.setText(String.valueOf(mMaxTimes));
                            mTimesEditText.setSelection(mTimesEditText.getText().length());
                            mTimesEditTextChanged = false;

                            ToastUtils.showLongToast(getActivity(), getResources().getString(R.string.good_detail_join_input_max,
                                    String.valueOf(mMaxTimes)));
                        } else {

                            mTimesEditTextChanged = true;
                            mTimesEditText.setText(String.valueOf(mExtraPojoProduct.remainTimes));
                            mTimesEditText.setSelection(mTimesEditText.getText().length());
                            mTimesEditTextChanged = false;

                            ToastUtils.showLongToast(getActivity(), getResources().getString(R.string.good_detail_join_input_overall,
                                    String.valueOf(mExtraPojoProduct.remainTimes)));
                        }
                    }
                    if (inputtimes <= 0) {

                        mTimesEditTextChanged = true;
                        mTimesEditText.setText(String.valueOf(mExtraPojoProduct.minTimes));
                        mTimesEditText.setSelection(mTimesEditText.getText().length());
                        mTimesEditTextChanged = false;

                        ToastUtils.showLongToast(getActivity(), getResources().getString(R.string.good_detail_join_input_overall,
                                String.valueOf(mExtraPojoProduct.minTimes)));
                    }
                    String times = mTimesEditText.getText().toString().trim();
                    if (pojoCouponsList.size() > 0) {
                        mAvailablePojoCouponsList.clear();
                        mUnAvailablePojoCouponsList.clear();
                        for (PojoCoupons pojoCoupons : pojoCouponsList) {
                            if (AVAILABLE_STATUS.equals(pojoCoupons.status)) {
                                if (Integer.parseInt(pojoCoupons.use_min_amount) <= Integer.parseInt(times) * mExtraPojoProduct.coins_unit) {
                                    mAvailablePojoCouponsList.add(pojoCoupons);
                                } else {
                                    mUnAvailablePojoCouponsList.add(pojoCoupons);
                                }
                            } else {
                                mUnAvailablePojoCouponsList.add(pojoCoupons);
                            }
                        }
                        if (mAvailablePojoCouponsList.size() > 0) {
                            mItemSelect = 0;
                            mTvCouponsCount.setTextColor(getResources().getColor(R.color.red));
                            mTvCouponsCount.setText("-" + mAvailablePojoCouponsList.get(0).amount + "coins");
                            mCouponAmount = Integer.parseInt(mAvailablePojoCouponsList.get(0).amount);
                        } else {
                            mItemSelect = -1;
                            mTvCouponsCount.setTextColor(Color.parseColor("#999999"));
                            mTvCouponsCount.setText(R.string.no_available);
                            mCouponAmount = 0;
                        }
                    }
                    int total = Integer.parseInt(times) * mExtraPojoProduct.coins_unit - mCouponAmount;
                    mTvTotal.setText(getString(R.string.bet_now_totle, total + ""));

                } catch (Exception ignore) {

                }
            }
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    public void show(FragmentManager fm) {
        if (this.isAdded()) {
            return;
        }
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, 0, 0,
                        android.R.anim.fade_out)
                .add(android.R.id.content, this, PartFragment.class.getName())
                .addToBackStack(null).commitAllowingStateLoss();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtraPojoProduct = (PojoProduct) getArguments().getSerializable(EXTRA_GOODS);
        getArguments().getString("position", "0");
        mType = getArguments().getInt(EXTRA_TYPE);
        HBLog.d(TAG + " onCreate " + mExtraPojoProduct);
        mMaxTimes = mExtraPojoProduct.maxTimes;
//        mMaxTimes = 400000;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_part, container, false);
        mTvCouponsCount = (TextView) view.findViewById(R.id.tv_coupons_count);
        mAccoutBalanceTextView = (TextView) view.findViewById(R.id.textview_balance);
        mCoinsUnitTextView = (TextView) view.findViewById(R.id.coins_unit);

        mTvTotal = (TextView) view.findViewById(R.id.tv_total);

        mCouponsList = (RelativeLayout) view.findViewById(R.id.rl_coupons_list);
        mContext = view.findViewById(R.id.rl_context);
        mGifImage = (ImageView) view.findViewById(R.id.iv_gif);
        mTips = (TextView) view.findViewById(R.id.iv_tips);
        mDecreaseTextView = (TextView) view.findViewById(R.id.textview_decrease);
        mIncreaseTextView = (TextView) view.findViewById(R.id.textview_increase);
        mTimesEditText = (EditText) view.findViewById(R.id.edittext_times);
        mTimesClusterLayout = (TimesClusterLayout) view.findViewById(R.id.layout_times_cluster);
        mPartButton = (TextView) view.findViewById(R.id.button_part);
        mGoodsImageView = (ImageView) view.findViewById(R.id.imageview_goods);
        mTopView = view.findViewById(R.id.view_top);
        mBottomView = view.findViewById(R.id.layout_bottom);
        mTimesEditText.addTextChangedListener(textWatcher);
        mCouponsList.setOnClickListener(this);
        mDecreaseTextView.setOnClickListener(this);
        mIncreaseTextView.setOnClickListener(this);
        mDecreaseTextView.setOnTouchListener(this);
        mIncreaseTextView.setOnTouchListener(this);
        mTimesClusterLayout.setOnItemClickListener(new TimesClusterLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int times) {
                setInputTimes(times);
            }
        });
        mPartButton.setOnClickListener(this);
        mTopView.setOnClickListener(this);
        mBottomView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setViewsLayoutDelay();
        loadCouponsData();
        mGuideController = new GuideController(this);
//        loadData();
    }

    private void setViewsLayoutDelay() {
        mTimesClusterLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTimesClusterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                LinearLayout.LayoutParams dcLp = (LinearLayout.LayoutParams) mDecreaseTextView.getLayoutParams();
                dcLp.width = mTimesClusterLayout.getChildWidth();
                mDecreaseTextView.setLayoutParams(dcLp);

                LinearLayout.LayoutParams icLp = (LinearLayout.LayoutParams) mIncreaseTextView.getLayoutParams();
                icLp.width = mTimesClusterLayout.getChildWidth();
                mIncreaseTextView.setLayoutParams(icLp);
            }
        });

    }

    private void initView() {
        if (mType == TYPE_USER_TIPS) {
            mTips.setVisibility(View.VISIBLE);
        }

        mTimesClusterLayout.setTimes(mExtraPojoProduct.timesList, mExtraPojoProduct.remainTimes, mExtraPojoProduct.minTimes, mExtraPojoProduct.maxTimes);
        if (mExtraPojoProduct.coins_unit != 1) {
            mCoinsUnitTextView.setVisibility(View.VISIBLE);
            mCoinsUnitTextView.setText(getResources().getString(R.string.coins_unit, mExtraPojoProduct.coins_unit));
        } else {
            mCoinsUnitTextView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mExtraPojoProduct.productUrl)) {
            Glide.with(this).load(mExtraPojoProduct.productUrl)
                    .fitCenter().crossFade().into(mGoodsImageView);
        } else {
            String url = mExtraPojoProduct.productImages.get(0);
            Glide.with(this).load(url).fitCenter().crossFade().into(mGoodsImageView);
        }
        //默认份数配置为正常范围时显示默认为主，否则显示最小份数。当默认份数大于剩余份数时 ，显示剩余份数
        if (mExtraPojoProduct.defaultTimes > 0 && mExtraPojoProduct.defaultTimes < mExtraPojoProduct.totalTimes) {
            if (mExtraPojoProduct.remainTimes < mExtraPojoProduct.defaultTimes) {
                mTimesEditText.setText(String.valueOf(mExtraPojoProduct.remainTimes));
            } else {
                mTimesEditText.setText(String.valueOf(mExtraPojoProduct.defaultTimes));
            }
        } else {
            mTimesEditText.setText(String.valueOf(mExtraPojoProduct.minTimes));
        }
        mTimesEditText.setSelection(mTimesEditText.getText().length());

        mainHandler = new MainHandler(this, Looper.getMainLooper());
        int total = Integer.parseInt(mTimesEditText.getText().toString().trim()) * mExtraPojoProduct.coins_unit - mCouponAmount;
        mTvTotal.setText(getString(R.string.bet_now_totle, total + ""));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGuideController.onDestroy();
    }

    private void loadCouponsData() {
        Map<String, Object> params = new HashMap<>();
        params.put("product_id", mExtraPojoProduct.productId);
        params.put("product_item_id", mExtraPojoProduct.productItemId);

        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.PREPAY, params, new Network.JsonCallBack<PojoCouponsItem>() {
            @Override
            public void onSuccess(PojoCouponsItem pojoCouponsItem) {
                if (isAdded() && !isDetached()) {
                    HBLog.i("isAdded: " + isAdded() + "isisDetached: " + isDetached());
                    HBLog.d(TAG + " onSuccess " + pojoCouponsItem);
                    mAccoutBalanceTextView.setText(getString(R.string.lable_coin, pojoCouponsItem.coin_count));
                    for (PojoCoupons pojoCoupons : pojoCouponsItem.list) {
                        pojoCouponsList.add(pojoCoupons);
                    }
                    String times = mTimesEditText.getText().toString().trim();
                    if (pojoCouponsList.size() > 0) {
                        mAvailablePojoCouponsList.clear();
                        mUnAvailablePojoCouponsList.clear();
                        for (PojoCoupons pojoCoupons : pojoCouponsList) {
                            if (AVAILABLE_STATUS.equals(pojoCoupons.status)) {
                                try{
                                    if (Integer.parseInt(pojoCoupons.use_min_amount) <= Integer.parseInt(times) * mExtraPojoProduct.coins_unit) {
                                        mAvailablePojoCouponsList.add(pojoCoupons);
                                    } else {
                                        mUnAvailablePojoCouponsList.add(pojoCoupons);
                                    }
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                }
                            } else {
                                mUnAvailablePojoCouponsList.add(pojoCoupons);
                            }
                        }
                        if (mAvailablePojoCouponsList.size() > 0) {
                            mItemSelect = 0;
                            mTvCouponsCount.setTextColor(getResources().getColor(R.color.red));
                            mTvCouponsCount.setText("-" + mAvailablePojoCouponsList.get(0).amount + "coins");
                        } else {
                            mItemSelect = -1;
                            mTvCouponsCount.setTextColor(Color.parseColor("#999999"));
                            mTvCouponsCount.setText(R.string.no_available);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                if (isAdded() && !isDetached()) {
                    HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                }
            }

            @Override
            public Class<PojoCouponsItem> getObjectClass() {
                return PojoCouponsItem.class;
            }
        });
    }

    public boolean onBackPressed() {
        HBLog.d(TAG + " onBackPressed ");
        if (couponsListFragment != null && couponsListFragment.onBackPressed()) {
            return true;
        }
        if (mGuideController != null && mGuideController.handleBackPressed()) {
            return true;
        }
        if (isAdded()) {
            dismiss(false, null, true);
            return true;
        }
        return false;
    }

    public void dismiss(boolean paySuceess, PojoPaySuccess payResult, boolean updateUI) {
        final View view = getView();
        if (view == null) {
            return;
        }
        if (getFragmentManager() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive(mTimesEditText)) {
            getView().requestFocus();
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputMethodManager.restartInput(mTimesEditText);
        }

        getFragmentManager().popBackStack();
        if (mOnDismissListener != null && updateUI) {
            mOnDismissListener.onPartCallBack(paySuceess, new PartObject(mExtraPojoProduct, payResult));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_decrease:
                setInputTimes(getInputTimes() - mExtraPojoProduct.minTimes);
                break;
            case R.id.textview_increase:
                setInputTimes(getInputTimes() + mExtraPojoProduct.minTimes);
                break;
            case R.id.button_part:
                if (mType == TYPE_USER_TIPS) {
                    Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Click_Select_Guide, null, null);
                }
                doCreateOrder();

                break;
            case R.id.view_top:
                if (mType != TYPE_USER_TIPS) {
                    Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Click_Select_Gray, null, null);
                    dismiss(false, null, true);
                }
                break;
            case R.id.layout_bottom:
                break;
            case R.id.rl_coupons_list:
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive(mTimesEditText)) {
                    getView().requestFocus();
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    inputMethodManager.restartInput(mTimesEditText);
                }
                Analytics.sendUIEvent(AnalyticsEvents.Coupons.Click_Buy_Coupon, null, null);

                //红包列表
                couponsListFragment = CouponsListFragment.newPartFragment(this, mAvailablePojoCouponsList, mUnAvailablePojoCouponsList, this, mItemSelect);
                couponsListFragment.show(getFragmentManager());
                getFragmentManager().beginTransaction().hide(this).commitAllowingStateLoss();
                break;

        }
    }


    private void doPayPressed(final int times) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.order_paying));
        Map<String, Object> params = new HashMap<>();
        if (mItemSelect == -1) {
            params.put("user_coupon_id", "");
        } else {
            params.put("user_coupon_id", mAvailablePojoCouponsList.get(mItemSelect).id);
        }
        params.put("product_item_id", mExtraPojoProduct.productItemId);
        params.put("count", String.valueOf(times));
        Network.get(GlobalContext.get()).asyncPost(HttpProtocol.URLS.BUY, params, new Network.JsonCallBack<PojoPaySuccess>() {
            @Override
            public void onSuccess(PojoPaySuccess list) {
                try {
                    HBLog.d(TAG + " onSuccess " + list);
                    dialog.dismiss();
                    if (isAdded() && !AppUtil.isActivityDestroyed(mActivity)) {
                        Intent intent = new Intent(mActivity, TransactionSuccessfulActivity.class);
                        intent.putExtra(TransactionSuccessfulActivity.BET_TIMES, times + "");
                        intent.putExtra(TransactionSuccessfulActivity.REMAIN_TIMES, list.remain_units);
                        intent.putExtra(TransactionSuccessfulActivity.IS_NEED_SHOW_GUIDE, mGuideController.isNeedShowGuide());
                        startActivity(intent);
                        getFragmentManager().popBackStack();
                        mOnDismissListener.onPartCallBack(true, new PartObject(mExtraPojoProduct, list));
                    }
                    DynamicTopicManager.getInstance(GlobalContext.get()).trigger(mExtraPojoProduct);
                    finishGuideIfNecessary();
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " onFailed " + code + " " + message + " " + e);
                dialog.dismiss();
                if (isAdded() && !AppUtil.isActivityDestroyed(mActivity)) {
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
                if (mGuideController.isNeedShowGuide()) {
                    Analytics.sendABTestUIEvent(AnalyticsEvents.ProductGuide.Finish_Guide_Error, "", null);
                }
                finishGuideIfNecessary();
            }

            @Override
            public Class<PojoPaySuccess> getObjectClass() {
                return PojoPaySuccess.class;
            }
        });
        dialog.show();
    }

    private void finishGuideIfNecessary() {
        if (!PrefUtils.getBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE, true)) {
            PrefUtils.putBoolean(PrefConstants.Guide.IS_FINISH_NEWBIEGUIDE, true);
            setServerUserFinishNewbieGuide();
        }
    }

    private void setServerUserFinishNewbieGuide() {
        String uid = PrefUtils.getString(GlobalContext.get(), PrefConstants.UserInfo.UID, "");
        Map<String, Object> params = new HashMap<>();
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

    private void showPayFailDialog() {
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
                try {
                    Intent intent = new Intent(mActivity, TopupActivity.class);
                    intent.putExtra(TopupActivity.EXTRA_ACTIVITY_FROM, TopupActivity.ACTIVITY_FROM_PAY);
                    startActivity(intent);
                    getFragmentManager().popBackStack();
                } catch (Exception e) {

                }

            }
        });
        builder.create().show();
    }

    private void showProductTimeoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                try {
                    Intent intent = new Intent(mActivity, GoodsDetailActivity.class);
                    intent.putExtra(GoodsDetailActivity.EXTRA_PRODUCT_ID, mExtraPojoProduct.productId);
                    startActivity(intent);
                    getFragmentManager().popBackStack();
                } catch (Exception e) {

                }

            }
        });
        builder.create().show();
    }

    private void showTimesLessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (v.getId() == R.id.textview_increase) {
                    if (longPressAddThread == null || !longPressAddThread.isAlive()) {
                        longPressAddThread = new HandlerThread("longPressAddThread", 10);
                        longPressAddThread.start();
                        longPressAddHandler = new WorkHandler(this, longPressAddThread.getLooper());
                    }
                    longPressAddHandler.obtainMessage(MSG_LONG_PRESS_ADD_START).sendToTarget();
                    longPressAddEnabled = true;

                }
                if (v.getId() == R.id.textview_decrease) {
                    if (longPressMinusThread == null || !longPressMinusThread.isAlive()) {
                        longPressMinusThread = new HandlerThread("longPressMinusThread", 10);
                        longPressMinusThread.start();
                        longPressMinusHandler = new WorkHandler(this, longPressMinusThread.getLooper());
                    }
                    longPressMinusHandler.obtainMessage(MSG_LONG_PRESS_MINUS_START).sendToTarget();
                    longPressMinusEnabled = true;

                }
                break;
            case MotionEvent.ACTION_UP:
                if (v.getId() == R.id.textview_increase) {
                    longPressAddEnabled = false;
                }
                if (v.getId() == R.id.textview_decrease) {
                    longPressMinusEnabled = false;
                }
                break;
        }
        return false;
    }

    private int getInputTimes() {
        int times = 0;
        try {
            times = Integer.parseInt(mTimesEditText.getText().toString().trim());
            if (mExtraPojoProduct.minTimes == 10) {             //如果是10元区,需要自动补满10
                if (times % 10 != 0) {
                    times += 10 - (times % 10);
                    if (times > mExtraPojoProduct.remainTimes || times > mMaxTimes) {     //如果补满10后比剩余的还大,那么调整为剩余值
                        if (mExtraPojoProduct.remainTimes > mMaxTimes) {
                            times = mMaxTimes;
                        } else {
                            times = mExtraPojoProduct.remainTimes;
                        }
                    } else {
                        if (AccountHelper.getInstance(GlobalContext.get()).isLogin()) {
                            ToastUtils.showLongToast(getActivity(), R.string.good_detail_join_auto_fill_tips);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return times;
        }
        return times;
    }

    public void setInputTimes(int times) {
        if (times >= mExtraPojoProduct.minTimes && times <= mExtraPojoProduct.remainTimes) {
            mTimesEditText.setText(String.valueOf(times));
            mTimesEditText.setSelection(mTimesEditText.getText().length());
        }
    }

    private void doCreateOrder() {
        int times = getInputTimes();
        if (times == 0) {
            return;
        }
        doPayPressed(times);

    }

    @Override
    public void onDestroy() {
        if (longPressAddThread != null && longPressAddThread.isAlive()) {
            longPressAddThread.quit();
        }
        if (longPressMinusThread != null && longPressMinusThread.isAlive()) {
            longPressMinusThread.quit();
        }
        super.onDestroy();
    }

    @Override
    public void onCouponsDismiss(int position) {
        mItemSelect = position;
        if (mItemSelect == -1) {
            if (mAvailablePojoCouponsList.size() > 0) {
                mTvCouponsCount.setTextColor(Color.parseColor("#999999"));
                mTvCouponsCount.setText(R.string.no_select);
                mCouponAmount = 0;
                int total = Integer.parseInt(mTimesEditText.getText().toString().trim()) * mExtraPojoProduct.coins_unit - mCouponAmount;
                mTvTotal.setText(getString(R.string.bet_now_totle, total + ""));
            }
        } else {
            mTvCouponsCount.setTextColor(getResources().getColor(R.color.red));
            mTvCouponsCount.setText("-" + mAvailablePojoCouponsList.get(position).amount + "coins");
            mCouponAmount = Integer.parseInt(mAvailablePojoCouponsList.get(position).amount);
            int total = Integer.parseInt(mTimesEditText.getText().toString().trim()) * mExtraPojoProduct.coins_unit - mCouponAmount;
            mTvTotal.setText(getString(R.string.bet_now_totle, total + ""));
        }


    }

    private void handleMainMessage(Message msg) {
        if (msg.what == MSG_LONG_PRESS_ADD) {
            setInputTimes(getInputTimes() + mExtraPojoProduct.minTimes);
        }
        if (msg.what == MSG_LONG_PRESS_MINUS) {
            setInputTimes(getInputTimes() - mExtraPojoProduct.minTimes);
        }
    }

    private void handleWorkMessage(Message msg) {
        if (msg.what == MSG_LONG_PRESS_ADD_START) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                longPressAddEnabled = false;
            }
            while (longPressAddEnabled) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    longPressAddEnabled = false;
                }
                if (mainHandler != null) {
                    mainHandler.obtainMessage(MSG_LONG_PRESS_ADD).sendToTarget();
                }
            }
        }
        if (msg.what == MSG_LONG_PRESS_MINUS_START) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                longPressMinusEnabled = false;
            }
            while (longPressMinusEnabled) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    longPressMinusEnabled = false;
                }
                if (mainHandler != null) {
                    mainHandler.obtainMessage(MSG_LONG_PRESS_MINUS).sendToTarget();
                }
            }
        }
    }

    private static class MainHandler extends Handler {
        private final WeakReference<PartFragment> weakReference;

        private MainHandler(PartFragment fragment, Looper Looper) {
            super(Looper);
            weakReference = new WeakReference<PartFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            PartFragment fragment = weakReference.get();
            if (fragment == null || msg == null) {
                return;
            }
            fragment.handleMainMessage(msg);

        }
    }

    private static class WorkHandler extends Handler {
        private final WeakReference<PartFragment> weakReference;

        private WorkHandler(PartFragment fragment, Looper Looper) {
            super(Looper);
            weakReference = new WeakReference<PartFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            PartFragment fragment = weakReference.get();
            if (fragment == null || msg == null) {
                return;
            }
            fragment.handleWorkMessage(msg);
        }
    }
}
