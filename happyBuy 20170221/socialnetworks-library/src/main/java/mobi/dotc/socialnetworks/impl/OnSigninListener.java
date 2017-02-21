package mobi.dotc.socialnetworks.impl;

import mobi.dotc.socialnetworks.SocialType;

/**
 * Created by huangli on 2016/5/3.
 */
public interface OnSigninListener {
    public void onSuccess(SocialType socialType);

    public void onError(SocialType socialType, String message,int errorcode);

    public void onCancle(SocialType socialType);
}
