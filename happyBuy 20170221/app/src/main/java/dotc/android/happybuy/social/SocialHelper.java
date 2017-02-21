package dotc.android.happybuy.social;

import android.content.Intent;
import android.os.Bundle;

import dotc.android.happybuy.social.impl.OnProfileLoadListener;


/**
 * Created by huangli on 16/5/9.
 */
@Deprecated
public abstract class SocialHelper {

    public abstract SocialType getSocialType();

    /**
     * ui 生命周期
     */
    public abstract void onCreate(Bundle savedInstanceState);
    public abstract void onDestory();
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /*listener*/
    private OnProfileLoadListener onProfileLoadListener;

    public OnProfileLoadListener getOnProfileLoadListener() {
        return onProfileLoadListener;
    }

    public void setOnProfileLoadListener(OnProfileLoadListener onProfileLoadListener) {
        this.onProfileLoadListener = onProfileLoadListener;
    }
}
