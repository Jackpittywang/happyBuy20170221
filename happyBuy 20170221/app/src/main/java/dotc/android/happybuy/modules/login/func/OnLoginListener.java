package dotc.android.happybuy.modules.login.func;

/**
 * Created by zhanqiang.mei on 2016/3/30.
 */

import java.util.HashMap;

/**
 * 第三方登录操作过程中会回调这个接口中的方法，不同方法衔接第
 * 三方登录与用户应用登录/注册的逻辑，故使用第三方登录时一定要实
 * 现本接口的不同方法，否则第三方登录是没有意义的。
 */
@Deprecated
public interface OnLoginListener {

    /**
     * 登录完成调用此接口，返回登录者在第三方社交平台上的用户数据。实现此方法时要根据
     * res参数中的用户资料，结合开发者应用的用户系统数据，判断此登录者是否需要先注册，
     * 如果需要注册，则返回true
     */
    boolean onLogin(String platform, HashMap<String, Object> res);

    void onCancel();
}
