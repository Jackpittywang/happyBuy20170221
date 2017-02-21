package dotc.android.happybuy.modules.login.func;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.push.TokenManager;
import dotc.android.happybuy.util.AppUtil;


/**
 * Created by wangzhiyuan on 2016/12/23.
 */

public class RegistManager {
    private final String TAG = this.getClass().getSimpleName();
    private static RegistManager mInstance;
    private Context mContext;
    private boolean mLogining;

    List<RegistStatusCallBack> mRegistStatusCallBack;

    private RegistManager(Context context) {
        this.mContext = context;
        mRegistStatusCallBack = new ArrayList<>();
    }


    public static RegistManager get(Context context) {
        if (mInstance == null) {
            synchronized (RegistManager.class) {
                if (mInstance == null) {
                    mInstance = new RegistManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public void addCallBack(RegistStatusCallBack registStatusCallBack) {
        mRegistStatusCallBack.add(registStatusCallBack);
    }

    public void removeCallBack(RegistStatusCallBack registStatusCallBack) {
        if (mRegistStatusCallBack.contains(registStatusCallBack)) {
            mRegistStatusCallBack.remove(registStatusCallBack);
        }

    }

    public boolean getRegistStatus() {
        return mLogining;
    }

    public void anonymousRegister() {
        mLogining = true;
        AccountHelper.getInstance(mContext).anonymousRegister(AppUtil.getDeviceId(mContext), "", listner);
    }

    AccountHelper.OnLoginResult listner = new AccountHelper.OnLoginResult() {
        @Override
        public void onLoginSucceed(int type) {
            TokenManager.get(GlobalContext.get()).uploadTokenToServerWhenLoginIfNeeded();
            mLogining = false;
            for (RegistStatusCallBack registStatusCallBack : mRegistStatusCallBack) {
                registStatusCallBack.registSucceed();
            }

        }

        @Override
        public void onLoginFailed(int type, String message, int errorcode) {
            mLogining = false;
            for (RegistStatusCallBack registStatusCallBack : mRegistStatusCallBack) {
                registStatusCallBack.registFailed();
            }
        }
    };

    public interface RegistStatusCallBack {
        void registSucceed();

        void registFailed();
    }
}
