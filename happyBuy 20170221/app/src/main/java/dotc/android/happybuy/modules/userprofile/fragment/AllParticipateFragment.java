package dotc.android.happybuy.modules.userprofile.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.uibase.app.BaseTabFragment;
import dotc.android.happybuy.uibase.component.RefreshLayout;

/**
 *
 */
public class AllParticipateFragment extends BaseTabFragment implements RefreshLayout.OnRefreshListener{

    public final static int TYPE_ALL = HttpProtocol.USER_PARTICIPATE_STATUS.ALL;
    public final static String EXTRA_UID = "extra_uid";
    private RefreshLayout mParentLayout;

    private int mParticipateType = TYPE_ALL;
    private boolean mSelfShow;

    private FragmentManager mFragmentManager;
    private ChildParticipateFragment mCurrentFragment;

    private String mExtraUid;

    public static AllParticipateFragment newInstance(String uid) {
        AllParticipateFragment fragment = new AllParticipateFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_UID, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExtraUid = getArguments().getString(EXTRA_UID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_join, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentLayout = (RefreshLayout) getActivity().findViewById(R.id.layout_refresh);
        mParentLayout.addOnRefreshListener(this);

        mFragmentManager = getChildFragmentManager();

        mCurrentFragment = ChildParticipateFragment.newInstance(mParticipateType,mExtraUid);
        mFragmentManager.beginTransaction()
                .add(R.id.layout_fragment, mCurrentFragment, mCurrentFragment.getFragmentName())
                .commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean isScrollTop() {
        return mCurrentFragment.isScrollTop();
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
            mCurrentFragment.doRefreshTask();
        }
    }

}
