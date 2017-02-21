package dotc.android.happybuy.modules.me.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.modules.me.base.BaseMeTabFragment;
import dotc.android.happybuy.modules.me.widget.MeRefreshLayout;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by zhanqiang.mei on 2016/3/29.
 */
public class MeJoinFragment extends BaseMeTabFragment implements MeRefreshLayout.OnRefreshListener{

    public final static int TYPE_ALL = HttpProtocol.USER_PARTICIPATE_STATUS.ALL;
    public final static int TYPE_ONSALE = HttpProtocol.USER_PARTICIPATE_STATUS.ONSALE;
    public final static int TYPE_AWARDED = HttpProtocol.USER_PARTICIPATE_STATUS.AWARDED;

    private MeRefreshLayout mParentLayout;

    private int mParticipateType = TYPE_ALL;
    private boolean mSelfShow;

//    private FragmentManager mFragmentManager;
    private Map<Integer,MeChildParticipateFragment> mFragments;
    private int mLastType = -1;
    private MeChildParticipateFragment mCurrentFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_join, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentLayout = (MeRefreshLayout) getParentFragment().getView().findViewById(R.id.layout_me_refresh);
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
    public void onRefresh(MeRefreshLayout refreshView) {
        if(mSelfShow){
            if(mCurrentFragment!=null){
                mCurrentFragment.doRefreshTask();
            } else {
                mParentLayout.onRefreshComplete();
            }
        }
    }

    @Override
    public void startRefreshing() {
        if(mCurrentFragment!=null){
            mCurrentFragment.doRefreshTask();
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
            MeChildParticipateFragment fr = newFragment(type);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.layout_fragment, fr, fr.getFragmentName())
                    .commitAllowingStateLoss();
            mCurrentFragment = fr;
        } else {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();//.beginTransaction();
            ft.hide(mCurrentFragment);
            MeChildParticipateFragment nextFragment = mFragments.get(type);
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

    private MeChildParticipateFragment newFragment(int type){
        String uid = PrefUtils.getString(PrefConstants.UserInfo.UID, "");
        MeChildParticipateFragment fragment = MeChildParticipateFragment.newInstance(type,uid);
//        fragment.injectRefreshLayout(mParentLayout);
        return fragment;
    }
}
