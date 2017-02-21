package dotc.android.happybuy.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import dotc.android.happybuy.uibase.app.BaseTabFragment;

/**

 */
public class TabFragmentAdapter extends FragmentPagerAdapter {
    private String[] mPageTitles;
    private int curPos = -1;
    private WeakReference<BaseTabFragment> mCurTabReference;
    private BaseTabFragment mCurrentPrimaryItem;
    private List<BaseTabFragment> mFragment;

    public TabFragmentAdapter(FragmentManager fragmentManager, List<BaseTabFragment> pages) {
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
        if(object!=null && object instanceof BaseTabFragment){
            BaseTabFragment listener = (BaseTabFragment) object;
            if(mCurTabReference!=null&&mCurTabReference.get()!=null){
                if(mCurTabReference.get()!= object){
                    mCurTabReference.get().onSelfDismiss();
                    listener.onSelfShow();
                    mCurTabReference = new WeakReference<BaseTabFragment>(listener);
                    mCurrentPrimaryItem = (BaseTabFragment) object;
                }
            } else {
                listener.onSelfShow();
                mCurTabReference = new WeakReference<BaseTabFragment>(listener);
                mCurrentPrimaryItem = (BaseTabFragment) object;
            }
        } else {
            mCurTabReference = null;
        }
    }

    public BaseTabFragment getCurrentFragment(){
        return mCurrentPrimaryItem;
    }


}
