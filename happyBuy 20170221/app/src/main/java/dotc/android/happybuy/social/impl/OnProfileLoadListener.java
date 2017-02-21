package dotc.android.happybuy.social.impl;


import dotc.android.happybuy.social.ProfileInfo;
import dotc.android.happybuy.social.SocialType;

/**
 * Created by huangli on 2016/5/3.
 */
@Deprecated
public interface OnProfileLoadListener {
   void onProfileLoadedSuccess(SocialType socialType , ProfileInfo profileInfo);

   void onProfileLoadedError(SocialType socialType, String message, int errorcode);
}
