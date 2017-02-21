package dotc.android.happybuy.modules.me.base;

import dotc.android.happybuy.uibase.app.BaseFragment;

/**
 * Created by wangjun on 16/4/1.
 */
public abstract class BaseMeTabFragment extends BaseFragment {

    public abstract boolean isScrollTop();

    public abstract void onSelfDismiss();

    public abstract void onSelfShow();

    public void startRefreshing(){}

}
