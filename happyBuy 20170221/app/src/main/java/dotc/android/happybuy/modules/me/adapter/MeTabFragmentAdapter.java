package dotc.android.happybuy.modules.me.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import dotc.android.happybuy.modules.me.base.BaseMeTabFragment;

/**

 */
public class MeTabFragmentAdapter extends FragmentPagerAdapter {
    private String[] mPageTitles;
    private int curPos = -1;
    private WeakReference<BaseMeTabFragment> mCurTabReference;
    private BaseMeTabFragment mCurrentPrimaryItem;
    private List<BaseMeTabFragment> mFragment;

    public MeTabFragmentAdapter(FragmentManager fragmentManager, List<BaseMeTabFragment> pages) {
        super(fragmentManager);
        this.mFragment = pages;
    }

    public void setPageTitles(String[] titles){
        mPageTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(mPageTitles!=null){
            return mPageTitles[position];
        }
        return null;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if(curPos!=position){
            curPos = position;
        }
        if(object!=null && object instanceof BaseMeTabFragment){
            BaseMeTabFragment listener = (BaseMeTabFragment) object;
            if(mCurTabReference!=null&&mCurTabReference.get()!=null){
                if(mCurTabReference.get()!= object){
                    mCurTabReference.get().onSelfDismiss();
                    listener.onSelfShow();
                    mCurTabReference = new WeakReference<BaseMeTabFragment>(listener);
                    mCurrentPrimaryItem = (BaseMeTabFragment) object;
                }
            } else {
                listener.onSelfShow();
                mCurTabReference = new WeakReference<BaseMeTabFragment>(listener);
                mCurrentPrimaryItem = (BaseMeTabFragment) object;
            }
        } else {
            mCurTabReference = null;
        }
    }

    public BaseMeTabFragment getCurrentFragment(){
        return mCurrentPrimaryItem;
    }


}
