package mobi.dotc.socialnetworks;

import android.content.Intent;
import android.os.Bundle;

import mobi.dotc.socialnetworks.impl.OnLoadProfileListener;
import mobi.dotc.socialnetworks.impl.OnSigninListener;
import mobi.dotc.socialnetworks.impl.OnShareListener;

/**
 * Created by dongbao.you on 2015/11/24.
 */
public abstract class SocialHelper {
    public abstract SocialType getSocialType();

    /**
     * ui 生命周期
     */
    public abstract void onCreate(Bundle savedInstanceState);

    public abstract void onDestory();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void onSaveInstanceState(Bundle outState);

    /*listener*/
    private OnShareListener onShareListener;
    private OnSigninListener onSigninListener;
    private OnLoadProfileListener onLoadProfileListener;

    public OnShareListener getOnShareListener() {
        return onShareListener;
    }

    public void setOnShareListener(OnShareListener onShareListener) {
        this.onShareListener = onShareListener;
    }

    public OnSigninListener getOnSigninListener() {
        return onSigninListener;
    }

    public void setOnSigninListener(OnSigninListener onSigninListener) {
        this.onSigninListener = onSigninListener;
    }

    public OnLoadProfileListener getOnLoadProfileListener() {
        return onLoadProfileListener;
    }

    public void setOnLoadProfileListener(OnLoadProfileListener onLoadProfileListener) {
        this.onLoadProfileListener = onLoadProfileListener;
    }
}
