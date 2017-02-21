package dotc.android.happybuy.modules.main.base;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import dotc.android.happybuy.log.HBLog;

/**
 */
public class FragmentHost {
    private final String TAG = this.getClass().getSimpleName();
    private Class<?>[] mFragmentClass;

    BaseMainFragment mFragments[];

    int mLastIndex = -1;

    private FragmentManager mFragmentManager;
    private int mViewId;

    public FragmentHost(FragmentManager fm, int viewId, Class<?>[] fragmentClass) {
        mFragmentClass = fragmentClass;
        mFragments = new BaseMainFragment[fragmentClass.length];
        mFragmentManager = fm;
        mViewId = viewId;
    }

    public void initWithSavedInstanceState(Bundle savedInstanceState,int lastIndex){
        HBLog.d(TAG + " initWithSavedInstanceState lastIndex:"+lastIndex);
        if (savedInstanceState != null) {
            for(int i=0;i<mFragments.length;i++){
                Class cls = mFragmentClass[i];
                mFragments[i] = (BaseMainFragment) mFragmentManager.findFragmentByTag(cls.getName());
                HBLog.d(TAG + " initWithSavedInstanceState i:"+i+" "+mFragments[i]);
            }

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            for(int i=0;i<mFragments.length;i++){
                BaseMainFragment fragment = mFragments[i];
                if(fragment!=null&&i!=lastIndex){
                    transaction.hide(fragment);
                }
            }
            transaction.commitAllowingStateLoss();
            mLastIndex = lastIndex;
        }
    }

    public void showFragment(int index,Bundle args) {
        HBLog.d(TAG + " showFragment index:"+index+" mLastIndex:"+mLastIndex);
        if(mLastIndex == index){
            if(args!=null){
                BaseMainFragment fragment = mFragments[index];
                if(fragment!=null){
                    fragment.onNewIntent(args);
                }
            }
            return;
        }
        if (mLastIndex == -1) {
            BaseMainFragment fr = getFragment(index,args);
            mFragmentManager.beginTransaction()
                    .add(mViewId, fr, mFragmentClass[index].getName())
                    .commitAllowingStateLoss();
        } else {
            BaseMainFragment nextFragment = mFragments[index];
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.hide(mFragments[mLastIndex]);
            if (nextFragment != null) {
                nextFragment.onNewIntent(args);
                ft.show(nextFragment);
                if (nextFragment instanceof IOnFragmentShowListener) {
                    ((IOnFragmentShowListener) nextFragment).onShow();
                }
            } else {
                nextFragment = getFragment(index,args);
                ft.add(mViewId, nextFragment, mFragmentClass[index].getName());
            }
            ft.commitAllowingStateLoss();
        }

        if(mLastIndex != -1){
            Fragment lastFragment = mFragments[mLastIndex];
            if(lastFragment!=null&&lastFragment instanceof IOnFragmentShowListener){
                ((IOnFragmentShowListener) lastFragment).onHide();
            }

        }
        mLastIndex = index;
    }

    private BaseMainFragment getFragment(int index,Bundle arg) {
        try {
            BaseMainFragment fr = (BaseMainFragment) mFragmentClass[index].newInstance();
            fr.setArguments(arg);
            mFragments[index] = fr;
            return fr;
        } catch (Exception e) {
            HBLog.w(TAG + " getFragment ", e);
        }
        return null;
    }

    public BaseMainFragment getCurrentFragment() {
        if(mLastIndex >-1){
            return mFragments[mLastIndex];
        }
        return null;
    }

    public int getLastIndex() {
        return mLastIndex;
    }

    public void detachUnusedFragment() {
        HBLog.d(TAG+" detachUnusedFragment ");
        final FragmentTransaction ft = mFragmentManager.beginTransaction();
        boolean needsCommit = false;
        for (int i = 0; i < 3; i++) {
            if (i == mLastIndex) {
                continue;
            }
            if (mFragments[i] != null) {
                ft.detach(mFragments[i]);
                mFragments[i] = null;
                needsCommit = true;
            }
        }
        if (needsCommit) {
            ft.commit();
        }
    }

    public interface IOnFragmentShowListener {
        void onShow();

        void onHide();
    }

}
