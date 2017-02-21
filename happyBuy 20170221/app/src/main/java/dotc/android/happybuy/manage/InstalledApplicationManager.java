package dotc.android.happybuy.manage;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by 陈尤岁 on 2016/12/28.
 */

public class InstalledApplicationManager implements Runnable {

    private static InstalledApplicationManager INSTANCE;

    private final int INSTALLED = 0;

    private final int UNINSTALL = 1;

    private WeakReference<Context> mContext = null;

    private Handler mHandler;

    private String mCheckPackageName = "";

    private OnErgodicListener mListener;

    private InstalledApplicationManager(Context context) {
        mContext = new WeakReference<Context>(context);
        mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mListener != null) {
                    mListener.ergodicDone(msg.what);
                }
            }
        };
    }

    public static InstalledApplicationManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (InstalledApplicationManager.class) {
                INSTANCE = new InstalledApplicationManager(context);
            }
        }
        return INSTANCE;
    }

    public InstalledApplicationManager checkPackageName(String packageName, OnErgodicListener listener) {
        mCheckPackageName = packageName;
        mListener = listener;
        return this;
    }

    public void start(){
        new Thread(this).start();
    }

    @Override
    public void run() {
        if(TextUtils.isEmpty(mCheckPackageName)){
            Log.e(InstalledApplicationManager.class.getName(),"checkPackageName == null");
            return;
        }
        Context context = mContext.get();
        if (context != null) {
            List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
            if (installedPackages != null && installedPackages.size() > 0) {
                for (PackageInfo p :
                        installedPackages) {
                    if (mCheckPackageName.equalsIgnoreCase(p.packageName)) {
                        mHandler.sendEmptyMessage(INSTALLED);
                        return;
                    }
                }
                mHandler.sendEmptyMessage(UNINSTALL);
            }
        }
    }

    public interface OnErgodicListener {
        void ergodicDone(int isInstalled);
    }
}
