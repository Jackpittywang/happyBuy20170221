package build;

import dotc.android.happybuy.http.Env;

/**
 * Created by wangjun on 16/9/9.
 */
public class Environment {

    public static final Env ENV = Env.TEST;
    public static final boolean LOG_ENABLE = true;
    public static final String APP_ID = "1";
    public static final String PRD_ID = "1";

    public static final String PHP_USR_CENTER_URL = "http://192.168.40.234:3236/";
    public static final String PHP_URL = "http://192.168.40.234:3201/v1/";
    public static final String PHP_H5_URL = "http://192.168.40.234:3201/";
    public static final String C_REG_URL = "http://192.168.5.222:14080/v1/";
    public static final String C_CACHE_URL = "http://192.168.5.222:21080/v1/";
    public static final String CONFIG_URL = "http://192.168.5.222:12204/v3/config";
    public static final String ABTEST_URL = "http://192.168.5.222:37330";
    public static final String REPORT_URL = "http://192.168.5.222:11011";

}
