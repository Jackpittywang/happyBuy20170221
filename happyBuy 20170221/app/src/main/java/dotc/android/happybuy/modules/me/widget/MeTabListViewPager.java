package dotc.android.happybuy.modules.me.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.me.adapter.MeTabFragmentAdapter;
import dotc.android.happybuy.modules.me.base.BaseMeTabFragment;

/**
 * Created by wangjun on 16/3/31.
 */
public class MeTabListViewPager extends ViewPager {

    private final String TAG = this.getClass().getSimpleName();
    private MeTabFragmentAdapter mAdapter;

    public MeTabListViewPager(Context context) {
        super(context);
    }

    public MeTabListViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTabAdapter(MeTabFragmentAdapter adapter){
        this.mAdapter = adapter;
        setAdapter(adapter);
    }

    public boolean isScrollTop() {
        if(mAdapter!=null){
            BaseMeTabFragment fragment = mAdapter.getCurrentFragment();
            HBLog.d(TAG+" isScrollTop "+fragment);
            if(fragment!=null){
                return fragment.isScrollTop();
            }
        }
       return true;
    }

}
