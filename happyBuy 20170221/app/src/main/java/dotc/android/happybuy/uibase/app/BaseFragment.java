package dotc.android.happybuy.uibase.app;

import android.content.Intent;
import android.support.v4.app.Fragment;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.modules.login.func.UserTokenManager;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.modules.login.LoginActivity;

/**
 * Created by wangjun on 16/3/28.
 */
public class BaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();

    public void startActivityIfLogined(Intent intent){
        if (isLogin()) {
            startActivity(intent);
        } else {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }

    public void startActivityForResultIfLogined(Intent intent,int requestCode){
        if (isLogin()) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }

    public boolean isLogin(){
        return UserTokenManager.getInstance(GlobalContext.get()).isTokenValid();
    }

    public String getUid(){
        return PrefUtils.getString(PrefConstants.Network.uid, "");
    }

}
