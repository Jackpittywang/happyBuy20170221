package dotc.android.happybuy.util;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by 陈尤岁 on 2017/1/13.
 */
public class ShortCutUtils {
    private final static String SHORTCUT_ADD_ACTION = "com.android.launcher.action.INSTALL_SHORTCUT";
    private final static String SHORTCUT_DEL_ACTION = "com.android.launcher.action.UNINSTALL_SHORTCUT";
    private final static String READ_SETTINGS_PERMISSION = "com.android.launcher.permission.READ_SETTINGS";
    private static final String PREFERENCE_KEY_FIRST_LANUCH = "IsFirstLaunch";

    public static void setUpShortCut(final Context context, final String shortCutName, final int resourceId, final Class<?> cls) {

        //判断应用是否第一次打开
        boolean isFirst = PrefUtils.getBoolean(context, PREFERENCE_KEY_FIRST_LANUCH, true);
        if (!isFirst) {
            return;
        }
        PrefUtils.putBoolean(context, PREFERENCE_KEY_FIRST_LANUCH, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                addShortCut(context, shortCutName, resourceId, cls);
            }
        }).start();
    }

    public static void addShortCut(Context context, String shortCutName, int resourceId, Class<?> cls) {
        try {

            if (hasShortcut(context)) {
                return;
            }
            Intent shortCutIntent = new Intent(SHORTCUT_ADD_ACTION);
            shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
            shortCutIntent.putExtra("duplicate", false);

            shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent()
                    .setAction(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                    .setClass(context, cls));

            ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, resourceId);
            shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

            context.sendBroadcast(shortCutIntent);
        } catch (Exception e) {

        }
    }


    public static boolean hasShortcut(Context context) {
        try {
            String AUTHORITY = getAuthorityFromPermission(context, READ_SETTINGS_PERMISSION);
            if (AUTHORITY == null) {
                //第三方启动器,未能识别,不创建快捷方式
                return true;
            }
            Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
            String appName = null;
            try {
                appName = obtatinAppName(context);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            Cursor c = context.getContentResolver().query(CONTENT_URI, new String[]{"title"}, "title=?", new String[]{appName}, null);
            if (c != null && c.getCount() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private static String getAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return null;
        }
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs == null) {
            return null;
        }
        for (PackageInfo pack : packs) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }


    private static String obtatinAppName(Context context) throws NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getApplicationLabel(packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)).toString();
    }

}
