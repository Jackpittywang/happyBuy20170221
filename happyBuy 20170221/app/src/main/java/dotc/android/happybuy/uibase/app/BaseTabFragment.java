package dotc.android.happybuy.uibase.app;

import dotc.android.happybuy.uibase.component.RefreshLayout;

/**
 * Created by wangjun on 16/4/1.
 */
public abstract class BaseTabFragment extends BaseFragment {

    public abstract boolean isScrollTop();

    public abstract void onSelfDismiss();

    public abstract void onSelfShow();

    private RefreshLayout mRefreshLayout;

    public BaseTabFragment injectRefreshLayout(RefreshLayout refreshLayout){
        this.mRefreshLayout = refreshLayout;
        return this;
    }

    public RefreshLayout getRefreshLayout(){
        return mRefreshLayout;
    }

}
