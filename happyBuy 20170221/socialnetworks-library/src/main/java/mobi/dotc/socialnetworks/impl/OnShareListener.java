package mobi.dotc.socialnetworks.impl;

import mobi.dotc.socialnetworks.SocialType;

/**
 * Created by dongbao.you on 2015/11/24.
 */
public interface OnShareListener {
    public void onSuccess(SocialType socialType);

    public void onError(SocialType socialType, String message);

    public void onCancle(SocialType socialType);
}
