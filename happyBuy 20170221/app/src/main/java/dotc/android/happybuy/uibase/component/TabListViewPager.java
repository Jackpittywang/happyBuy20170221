package dotc.android.happybuy.uibase.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.ui.adapter.TabFragmentAdapter;
import dotc.android.happybuy.uibase.app.BaseTabFragment;

/**
 * Created by wangjun on 16/3/31.
 */
public class TabListViewPager extends ViewPager {

    private final String TAG = this.getClass().getSimpleName();
    private TabFragmentAdapter mAdapter;

    public TabListViewPager(Context context) {
        super(context);
    }

    public TabListViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTabAdapter(TabFragmentAdapter adapter){
        this.mAdapter = adapter;
        setAdapter(adapter);
    }

    public boolean isScrollTop() {
        if(mAdapter!=null){
            BaseTabFragment fragment = mAdapter.getCurrentFragment();
            HBLog.d(TAG+" isScrollTop "+fragment);
            if(fragment!=null){
                return fragment.isScrollTop();
            }
        }
       return true;
    }

}
