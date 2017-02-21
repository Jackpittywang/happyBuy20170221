package dotc.android.happybuy.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

import dotc.android.happybuy.R;

/**
 * Created by zhanqiang.mei on 2016/3/29.
 */
public class UserCenterAdapter extends FragmentPagerAdapter {
    private Context ctx;
    private List<Fragment> mPages;
    private String[] titles;

    public UserCenterAdapter(FragmentManager fragmentManager, Context context, List<Fragment> pages) {
        super(fragmentManager);
        this.ctx = context;
        mPages = pages;
        titles = ctx.getResources().getStringArray(R.array.user_center_tab);
    }

    @Override
    public Fragment getItem(int position) {
        return mPages.get(position);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
