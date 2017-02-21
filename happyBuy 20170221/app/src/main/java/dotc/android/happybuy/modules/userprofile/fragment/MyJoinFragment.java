package dotc.android.happybuy.modules.userprofile.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.modules.userprofile.fragment.ChildParticipateFragment;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.component.RefreshLayout;

/**
 * Created by zhanqiang.mei on 2016/3/29.
 */
public class MyJoinFragment extends BaseTabFragment implements RefreshLayout.OnRefreshListener{

    public final static int TYPE_ALL = HttpProtocol.USER_PARTICIPATE_STATUS.ALL;
    public final static int TYPE_ONSALE = HttpProtocol.USER_PARTICIPATE_STATUS.ONSALE;
    public final static int TYPE_AWARDED = HttpProtocol.USER_PARTICIPATE_STATUS.AWARDED;

    private RefreshLayout mParentLayout;

    private int mParticipateType = TYPE_ALL;
    private boolean mSelfShow;

//    private FragmentManager mFragmentManager;
    private Map<Integer,ChildParticipateFragment> mFragments;
    private int mLastType = -1;
    private ChildParticipateFragment mCurrentFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_join, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentLayout = getRefreshLayout();
        if(mParentLayout==null){
            mParentLayout = (RefreshLayout) getParentFragment().getView().findViewById(R.id.layout_refresh);
        }
        mParentLayout.addOnRefreshListener(this);

        mFragments = new HashMap<>();
//        mFragmentManager = getChildFragmentManager();

        showFragment(mParticipateType);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean isScrollTop() {
        if(mCurrentFragment!=null){
            return mCurrentFragment.isScrollTop();
        }
        return true;
    }

    @Override
    public void onSelfDismiss() {
        mSelfShow = false;
    }

    @Override
    public void onSelfShow() {
        mSelfShow = true;
    }

    @Override
    public void onRefresh(RefreshLayout refreshView) {
        if(mSelfShow){
            if(mCurrentFragment!=null){
                mCurrentFragment.doRefreshTask();
            } else {
                mParentLayout.onRefreshComplete();
            }
        }
    }

    public void setParticipateType(int type) {
        if(mParticipateType==type){
            return;
        }
        this.mParticipateType = type;
        showFragment(type);
    }

    private void showFragment(int type) {
        if (mCurrentFragment == null) {
            ChildParticipateFragment fr = newFragment(type);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.layout_fragment, fr, fr.getFragmentName())
                    .commitAllowingStateLoss();
            mCurrentFragment = fr;
        } else {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();//.beginTransaction();
            ft.hide(mCurrentFragment);
            ChildParticipateFragment nextFragment = mFragments.get(type);
            if (nextFragment != null) {
                ft.show(nextFragment);
            } else {
                nextFragment = newFragment(type);
                mFragments.put(type,nextFragment);
                ft.add(R.id.layout_fragment, nextFragment, nextFragment.getFragmentName());
            }
            ft.commitAllowingStateLoss();
            mCurrentFragment = nextFragment;
        }
        mLastType = type;
    }

    private ChildParticipateFragment newFragment(int type){
        String uid = PrefUtils.getString(PrefConstants.UserInfo.UID, "");
        ChildParticipateFragment fragment =  ChildParticipateFragment.newInstance(type,uid);
        fragment.injectRefreshLayout(mParentLayout);
        return fragment;
    }
}
