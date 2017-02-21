package dotc.android.happybuy.modules.main.base;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.uibase.app.BaseFragment;

/**
 * Created by wangjun on 16/4/1.
 */
public abstract class BaseMainFragment extends BaseFragment {



    public MainTabActivity getParentActivity(){
       return (MainTabActivity) getActivity();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public void onTabDoubleClick(View view){

    }

    public void onNewIntent(Bundle argument){
        HBLog.d(TAG+" onNewIntent "+argument);

    }


}
