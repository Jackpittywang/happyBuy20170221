package dotc.android.happybuy.modules.part;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.http.result.PojoCoupons;
import dotc.android.happybuy.http.result.PojoProduct;
import dotc.android.happybuy.modules.part.adapter.RedBagPayListAdpter;
import dotc.android.happybuy.modules.part.adapter.RedBagPayListUnAvailableAdpter;
import dotc.android.happybuy.modules.part.widget.ScrollListView;
import dotc.android.happybuy.uibase.app.BaseFragment;


public class CouponsListFragment extends BaseFragment {

    public final static String EXTRA_TYPE = "extra_type";

    public final static int TYPE_ALL = 0;
    public LinearLayout mLinearLayoutBack;
    public LinearLayout mAvailableEmpty;
    public LinearLayout mUnAvailableEmpty;

    public ScrollListView mAvailableCoupons;
    public ScrollListView mUnAvailableCoupons;
    public RedBagPayListAdpter availableRedBagPayListAdpter;
    public RedBagPayListUnAvailableAdpter unAvailableRedBagPayListAdpter;
    public  OnCouponsDismissListener onCouponsDismissListener;
    private int mItemSelect=-1;

    public android.support.v4.app.Fragment oldFragment;

    public List<PojoCoupons> mAvailablePojoCouponsList = new ArrayList<>();
    public List<PojoCoupons> mUnAvailablePojoCouponsList = new ArrayList<>();

    public static CouponsListFragment newPartFragment(android.support.v4.app.Fragment fragment,List<PojoCoupons> availablePojoCouponsList,
                                                      List<PojoCoupons> unAvailablePojoCouponsList,OnCouponsDismissListener onCouponsDismissListener,int position) {
        CouponsListFragment couponsListFragment = new CouponsListFragment();
        couponsListFragment.oldFragment = fragment;
        couponsListFragment.mAvailablePojoCouponsList=availablePojoCouponsList;
        couponsListFragment.mUnAvailablePojoCouponsList=unAvailablePojoCouponsList;
        couponsListFragment.onCouponsDismissListener=onCouponsDismissListener;
        couponsListFragment.mItemSelect=position;
        return couponsListFragment;
    }

    private PojoProduct mExtraPojoProduct;


    public void show(FragmentManager fm) {
        if (this.isAdded()) {
            return;
        }
        fm.beginTransaction()
//                .setCustomAnimations(android.R.anim.fade_in, 0, 0,
//                        android.R.anim.fade_out)
                .add(android.R.id.content, this, CouponsListFragment.class.getName())
                .commitAllowingStateLoss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupons_list, container, false);
        mLinearLayoutBack = (LinearLayout) view.findViewById(R.id.ll_back);
        mAvailableCoupons = (ScrollListView) view.findViewById(R.id.lv_available_coupons);
        mUnAvailableCoupons = (ScrollListView) view.findViewById(R.id.lv_unavailable_coupons);
        mAvailableEmpty = (LinearLayout) view.findViewById(R.id.ll_available_empty);
        mUnAvailableEmpty = (LinearLayout) view.findViewById(R.id.ll_unavailable_empty);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        loadData();

    }

    private void initView() {
        availableRedBagPayListAdpter=new RedBagPayListAdpter(getContext());
        unAvailableRedBagPayListAdpter=new RedBagPayListUnAvailableAdpter(getContext());
        mAvailableCoupons.setAdapter(availableRedBagPayListAdpter);
        mUnAvailableCoupons.setAdapter(unAvailableRedBagPayListAdpter);

        mAvailableCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mItemSelect==position){
                    //如果-1表明没有选红包
                    mItemSelect=-1;
                }else {
                    Analytics.sendUIEvent(AnalyticsEvents.Coupons.Select_Buy_Coupon,null,null);
                    mItemSelect=position;
                }
                availableRedBagPayListAdpter.updateSelect(position);
            }
        });

        mLinearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                onCouponsDismissListener.onCouponsDismiss(mItemSelect);
                fragmentTransaction.remove(CouponsListFragment.this).show(oldFragment).commitAllowingStateLoss();
            }
        });
    }

    private void loadData() {
        if(mAvailablePojoCouponsList.size()>0){
            mAvailableCoupons.setVisibility(View.VISIBLE);
            mAvailableEmpty.setVisibility(View.GONE);
            availableRedBagPayListAdpter.addData(mAvailablePojoCouponsList);
            availableRedBagPayListAdpter.notifyDataSetChanged();
            availableRedBagPayListAdpter.updateSelect(mItemSelect);
        }else {
            mItemSelect=-1;
            mAvailableCoupons.setVisibility(View.GONE);
            mAvailableEmpty.setVisibility(View.VISIBLE);
        }
        if(mUnAvailablePojoCouponsList.size()>0){
            mUnAvailableCoupons.setVisibility(View.VISIBLE);
            mUnAvailableEmpty.setVisibility(View.GONE);
            unAvailableRedBagPayListAdpter.addData(mUnAvailablePojoCouponsList);
            unAvailableRedBagPayListAdpter.notifyDataSetChanged();
        }else {
            mUnAvailableCoupons.setVisibility(View.GONE);
            mUnAvailableEmpty.setVisibility(View.VISIBLE);
        }

    }


    public boolean onBackPressed() {
        if (isAdded()) {
            dismiss(false, true);
            return true;
        }
        return false;
    }

    public void dismiss(boolean paySuceess, boolean updateUI) {
        final View view = getView();
        if (view == null) {
            return;
        }
        if (getFragmentManager() == null) {
            return;
        }
        onCouponsDismissListener.onCouponsDismiss(mItemSelect);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(CouponsListFragment.this).show(oldFragment).commitAllowingStateLoss();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnCouponsDismissListener {
        void onCouponsDismiss(int position);
    }
}
