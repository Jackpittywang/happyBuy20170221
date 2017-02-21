package dotc.android.happybuy.analytics;

import android.content.Context;
import android.support.v4.util.Pools;
import android.text.TextUtils;

import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatAppMonitor;

import com.tencent.stat.StatService;
import com.tencent.stat.common.StatConstants;

import java.util.Properties;


/**
 * MTA的适配层
 *
 * @author toby.du
 *
 */
public final class MTAUtils {
    private MTAUtils() {
    }

    private static final String LABEL = "label";
    private static final String VALUE = "value";
    private static final String NULL = "null";

    public static final int RESULT_TYPE_SUCCESS = StatAppMonitor.SUCCESS_RESULT_TYPE;
    public static final int RESULT_TYPE_FAILURE = StatAppMonitor.FAILURE_RESULT_TYPE;
    public static final int RESULT_TYPE_LOGIC_FAILURE = StatAppMonitor.LOGIC_FAILURE_RESULT_TYPE;

    private static Context sContext;

    public static void init(Context context, String appKey) {
        sContext = context.getApplicationContext();
        try {
            StatService.startStatService(sContext, appKey, StatConstants.VERSION);
        } catch (MtaSDkException e) {
            e.printStackTrace();
        }
    }

    public static void onActivityStart(Context activity) {
        StatService.onResume(activity);
    }

    public static void onActivityStop(Context activity) {
        StatService.onPause(activity);
    }

    public static void onFragmentStart(String pageName) {
        StatService.trackBeginPage(sContext, pageName);
    }

    public static void onFragmentStop(String pageName) {
        StatService.trackEndPage(sContext, pageName);
    }

    public static void sendEvent(String action, String label, String value) {
        Properties prop = buildOptParam(label, value);
        StatService.trackCustomKVEvent(sContext, action, prop);
        PooledPropertiesWrapper.recycle(prop);
    }

    public static void sendBeginEvent(String action, String label, String value) {
        Properties prop = buildOptParam(label, value);
        StatService.trackCustomBeginKVEvent(sContext, action, prop);
        PooledPropertiesWrapper.recycle(prop);
    }

    public static void sendEndEvent(String action, String label, String value) {
        Properties prop = buildOptParam(label, value);
        StatService.trackCustomEndKVEvent(sContext, action, prop);
        PooledPropertiesWrapper.recycle(prop);
    }

    private static Properties buildOptParam(String label, String value) {
        Properties prop = null;
        if (!(TextUtils.isEmpty(label) && TextUtils.isEmpty(value))) {
            prop = PooledPropertiesWrapper.obtain();
            // 重置
            prop.remove(LABEL);
            prop.remove(VALUE);

            if (!TextUtils.isEmpty(label)) {
                prop.put(LABEL, label);
            }
            if (!TextUtils.isEmpty(value)) {
                prop.put(VALUE, value);
            }
        }
        return prop;
    }

    /**
     * 接口监控
     *
     * @param interfaceName
     * @param resultType
     * @param returnCode
     * @param consumeMilliseconds
     * @param reqBytes
     * @param respBytes
     * @param sampling
     */
    public static void reportInterfaceMonitor(String interfaceName, int resultType, int returnCode,
                                              long consumeMilliseconds, long reqBytes, long respBytes, int sampling) {
        StatAppMonitor monitor = PooledStatAppMonitorWrapper.obtain();

        monitor.setInterfaceName(interfaceName);
        monitor.setResultType(resultType);
        monitor.setReturnCode(returnCode);
        monitor.setMillisecondsConsume(consumeMilliseconds);
        monitor.setReqSize(reqBytes);
        monitor.setRespSize(respBytes);
        monitor.setSampling(sampling);

        StatService.reportAppMonitorStat(sContext, monitor);

        PooledStatAppMonitorWrapper.recycle(monitor);
    }

    private static void reportException(Throwable e) {
        StatService.reportException(sContext, e);
    }

    public static void reportError(String e) {
        StatService.reportError(sContext, e);
    }

    private static final class PooledPropertiesWrapper {
        private PooledPropertiesWrapper() {
        }

        private static final Pools.Pool<Properties> sPool = new Pools.SynchronizedPool<Properties>(10);

        public static Properties obtain() {
            Properties instance = sPool.acquire();
            return (instance != null) ? instance : new Properties();
        }

        public static void recycle(Properties instance) {
            if (instance != null) {
                sPool.release(instance);
            }
        }
    }

    private static final class PooledStatAppMonitorWrapper {
        private PooledStatAppMonitorWrapper() {
        }

        private static final Pools.Pool<StatAppMonitor> sPool = new Pools.SynchronizedPool<StatAppMonitor>(10);

        public static StatAppMonitor obtain() {
            StatAppMonitor instance = sPool.acquire();
            return (instance != null) ? instance : new StatAppMonitor(NULL);
        }

        public static void recycle(StatAppMonitor instance) {
            if (instance != null) {
                sPool.release(instance);
            }
        }
    }
}
