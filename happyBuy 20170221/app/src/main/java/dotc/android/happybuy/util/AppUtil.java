package dotc.android.happybuy.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.stat.analytics.AnalyticsSdk;

import java.util.List;

public class AppUtil {

    public static int dp2px(Context context, float dp) {
        final float dentisy = context.getResources().getDisplayMetrics().density;
        return (int) (0.5f + dentisy * dp);
    }

    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getMetaInt(Context context, String key) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getInt(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getStringValue(Context context, String name, String defaultValue) {
        try {
            int resid = context.getResources().getIdentifier(name, "string", context.getPackageName());
            if (resid > 0) {
                return context.getString(resid);
            }
        } catch (Exception e) {

        }
        return defaultValue;
    }

    public static String getSymbolValue(Context context, String name, String defaultValue) {
        try {
            int resid = context.getResources().getIdentifier(name, "string", context.getPackageName());
            if (resid > 0) {
                return context.getString(resid);
            }
        } catch (Exception e) {

        }
        return defaultValue;
    }

    public static String[] getArrayValue(Context context, String name) {
        try {
            int resid = context.getResources().getIdentifier(name, "array", context.getPackageName());
            if (resid > 0) {
                return context.getResources().getStringArray(resid);
            }
        } catch (Exception e) {
        }
        return new String[0];
    }

    public static String getAndroidVersionCode() {
        return android.os.Build.VERSION.SDK;
    }

    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
        }
        return -1;
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "unknown";
        }
    }

    public static String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            if (phoneMgr != null)
                imei = phoneMgr.getDeviceId();
            return imei;
        } catch (Exception e) {
            return imei;
        }
    }

    public static String getIMSI(Context context) {
        String imsi = null;
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (phoneMgr != null)
            imsi = phoneMgr.getSubscriberId();
        return imsi;
    }


    public static int getVersionCode(Context context) {
        try {
            PackageManager pkgManager = context.getPackageManager();
            PackageInfo info = pkgManager.getPackageInfo(context.getPackageName(), 0);
            if (info != null)
                return info.versionCode;
        } catch (NameNotFoundException e) {
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager pkgManager = context.getPackageManager();
            PackageInfo info = pkgManager.getPackageInfo(context.getPackageName(), 0);
            if (info != null)
                return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSimExist(Context ctx) {
        TelephonyManager telMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telMgr.getSimState();
        return state != TelephonyManager.SIM_STATE_ABSENT;
    }

    public static String getSimSerial(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            return tm.getSimSerialNumber();
        }
        return null;
    }

    public static String getAndroidID(Context ctx) {
        try {
            return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
        }
        return "";
    }

    private static String sDeviceId;

    public static String getDeviceId(Context context) {
        if (!TextUtils.isEmpty(sDeviceId)) {
            return sDeviceId;
        }
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId();
            sDeviceId = deviceId + androidId;
//            sDeviceId = deviceId + androidId + RandomStringUtils.getRandomString(8);
            return sDeviceId;
        } catch (Exception e) {
            sDeviceId=androidId;
            return sDeviceId;
//            return RandomStringUtils.getRandomString(16);
        }
    }

    private static int sBucketId = -2;

    public static int getBucketId(Context context) {
        if (sBucketId > -2) {
            return sBucketId;
        }
        sBucketId = AnalyticsSdk.getBucketId(context);
        return sBucketId;
    }


    public static boolean isActivityDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.isFinishing();
        } else {
            return isActivityDestroyedOn17(activity);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isActivityDestroyedOn17(Activity activity) {
        return activity.isFinishing() || activity.isDestroyed();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> mPackageInfo = packageManager.getInstalledPackages(0);
        boolean flag = false;
        if (mPackageInfo != null) {
            String tempName = null;
            for (int i = 0; i < mPackageInfo.size(); i++) {
                tempName = mPackageInfo.get(i).packageName;
                if (tempName != null && tempName.equals(packageName)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}