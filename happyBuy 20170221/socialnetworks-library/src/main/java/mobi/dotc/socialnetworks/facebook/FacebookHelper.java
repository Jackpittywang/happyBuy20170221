package mobi.dotc.socialnetworks.facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mobi.dotc.socialnetworks.R;
import mobi.dotc.socialnetworks.SocialHelper;
import mobi.dotc.socialnetworks.SocialType;

/**
 * Singleton class for managing Facebook Login APIs
 * Created by brian.dang on 5/8/2015.
 */
public class FacebookHelper extends SocialHelper {
    public static final String TAG = "Account.FacebookHelper";

    private static final String PERMISSION = "publish_actions";

    private FacebookCallback<Sharer.Result> shareCallback; // Tied to Share Callbacks
    private CallbackManager callbackManager; // Tied to FB Login Button (Login/Logout)
    private ProfileTracker profileTracker;

    private Activity myActivity;

    private PendingAction pendingAction = PendingAction.NONE;
    private ArrayList<SharePhoto> sharePhotos;
    private ShareLinkContent linkContent;

    public enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE;
    }

    public FacebookHelper(final Activity activity) {
        this.myActivity = activity;
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(myActivity.getApplicationContext());
        }
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.Facebook;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        initShareCallback(myActivity);
        initLoginCallback(myActivity);

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    if (getOnLoadProfileListener() != null) {
                        getOnLoadProfileListener().onSuccess(SocialType.Facebook);
                    }
                }
                // It's possible that we were waiting for Profile to be populated in order to
                // post a status update.
                handlePendingAction();
            }
        };

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }
    }

    @Override
    public void onDestory() {
        profileTracker.stopTracking();
        signout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    /**
     * Initializes FB Share Callback
     *
     * @param activity
     */
    private void initShareCallback(final Activity activity) {
        // Init Share Callback
        shareCallback = new FacebookCallback<Sharer.Result>() {
            @Override
            public void onCancel() {
                Log.d(TAG, "FacebookCallback<Sharer.Result>.onCancel()");
                getOnShareListener().onCancle(getSocialType());
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, String.format("FacebookCallback<Sharer.Result>.onError(): %s", error.toString()));
                getOnShareListener().onError(getSocialType(), error.getMessage());
            }

            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG, "FacebookCallback<Sharer.Result>.onSuccess()");
                if (result.getPostId() != null) {
                    getOnShareListener().onSuccess(getSocialType());
                }
            }
        };
    }

    /**
     * Initializes FB Login Callback
     *
     * @param activity
     */
    private void initLoginCallback(final Activity activity) {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "FacebookCallback<LoginResult>.onSuccess()");
                        handlePendingAction();
                        printProfileInfo();
                        if (getOnSigninListener() != null) {
                            getOnSigninListener().onSuccess(getSocialType());
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "FacebookCallback<LoginResult>.onCancel()");
                        if (pendingAction != PendingAction.NONE) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        if (getOnSigninListener() != null) {
                            getOnSigninListener().onCancle(getSocialType());
                        }
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "FacebookCallback<LoginResult>.onError()" + exception.toString());
                        if (pendingAction != PendingAction.NONE
                                && exception instanceof FacebookAuthorizationException) {
                            showAlert();
                            pendingAction = PendingAction.NONE;
                        }
                        if (getOnSigninListener() != null) {
                            getOnSigninListener().onError(getSocialType(), exception.getMessage(),-1);
                        }
                    }

                    private void showAlert() {
                        new AlertDialog.Builder(activity)
                                .setTitle(R.string.cancelled)
                                .setMessage(R.string.permission_not_granted)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                });
    }

    public void signin(List<String> perms) {
        Log.d(TAG, "signin()");
        LoginManager.getInstance().logInWithReadPermissions(
                myActivity,
                perms);
    }

    /**
     * Programatically logs out of the Facebook instance.
     */
    public void signout() {
        Log.d(TAG, "signout()");
        revokeAllPermissions();
    }

    /**
     * Determines if this account/token can post a status/photo.
     *
     * @return true/false
     */
    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    public Profile getProfile() {
        Profile profile = Profile.getCurrentProfile();
        return profile;
    }

    public boolean isTokenExpired() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            return accessToken.isExpired();
        }
        return true;
    }

    public String getToken() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            return accessToken.getToken();
        }
        return "";
    }

    public String getUserId() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            return accessToken.getUserId();
        }
        return "";
    }

    public void revokeAllPermissions() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null)
            return;

        LoginManager.getInstance().logOut();
//        GraphRequest request = GraphRequest.newGraphPathRequest(
//                accessToken,
//                "/me/permissions/",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        LoginManager.getInstance().logOut();
//                    }
//                }
//        );
//        request.setHttpMethod(HttpMethod.DELETE);
//        request.executeAsync();
    }

    public void printProfileInfo() {
        Log.d(TAG, "printProfileInfo()");

        Profile profile = getProfile();
        if (profile != null) {
            // Profile
            Log.d(TAG, "First Name: " + profile.getFirstName());
            Log.d(TAG, "Last Name: " + profile.getLastName());
            Log.d(TAG, "Middle Name: " + profile.getMiddleName());
            Log.d(TAG, "Name: " + profile.getName());
            Log.d(TAG, "ID: " + profile.getId());
            Log.d(TAG, "Link URI: " + profile.getLinkUri().toString());
            Log.d(TAG, "Profile Picture URI: " + profile.getProfilePictureUri(450, 450).toString());
            Log.d(TAG, "Describe Contents: " + profile.describeContents());
            Log.d(TAG, "ToString(): " + profile.toString());
        }
    }

    public void printLargeLogText(String myTag, String text) {
        int chunkSize = 3500;
        if (text.length() > chunkSize) {
            Log.d(myTag, "s.length = " + text.length());
            int chunkCount = text.length() / chunkSize;
            for (int i = 0; i <= chunkCount; i++) {
                int max = chunkSize * (i + 1);
                if (max >= text.length())
                    Log.d(myTag, "chunk #" + i + " of " + chunkCount + ": " + text.substring(chunkSize * i));
                else
                    Log.d(myTag, "chunk #" + i + " of " + chunkCount + ": " + text.substring(chunkSize * i, max));
            }
        } else {
            Log.d(myTag, text);
        }
    }

    // share
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but
        // we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                postPhotos(sharePhotos, null);
                break;
            case POST_STATUS_UPDATE:
                postLink(linkContent, null);
                break;
        }
    }

    /**
     * 分享照片
     *
     * @param photos
     * @param callback
     */
    public void sharePhotos(ArrayList<SharePhoto> photos, FacebookCallback<Sharer.Result> callback) {
        sharePhotos = photos;

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingAction = PendingAction.POST_PHOTO;
            if (hasPublishPermission()) {
                // We can do the action right away.
                postPhotos(photos, callback);
                return;
            } else {
                // We need to get new permissions, then complete the action when
                // we get called back.
                LoginManager.getInstance().logInWithPublishPermissions(myActivity, Arrays.asList(PERMISSION));
                return;
            }
        }

        boolean allowNoToken = ShareDialog.canShow(SharePhotoContent.class);
        if (allowNoToken) {
            pendingAction = PendingAction.POST_PHOTO;
            postPhotos(photos, null);
        }
    }

    /**
     * 分享链接
     *
     * @author dongbao.you
     * @date 2015年4月20日
     */
    public void shareLink(ShareLinkContent linkContent, FacebookCallback<Sharer.Result> callback) {
        this.linkContent = linkContent;

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingAction = PendingAction.POST_PHOTO;
            if (hasPublishPermission()) {
                // We can do the action right away.
                postLink(linkContent, callback);
                return;
            } else {
                // We need to get new permissions, then complete the action when
                // we get called back.
                LoginManager.getInstance().logInWithPublishPermissions(myActivity, Arrays.asList(PERMISSION));
                return;
            }
        }

        boolean allowNoToken = ShareDialog.canShow(ShareLinkContent.class);
        if (allowNoToken) {
            pendingAction = PendingAction.POST_PHOTO;
            postLink(linkContent, callback);
        }
    }

    /**
     * 邀请安装
     *
     * @author dongbao.you
     * @date 2015年4月20日
     */
    public void showInviteDialog(FacebookCallback<AppInviteDialog.Result> appInviteCallback, String appLinkUrl, String previewImageUrl) {

        AppInviteDialog appInviteDialog = new AppInviteDialog(myActivity);
        appInviteDialog.registerCallback(callbackManager, appInviteCallback);

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            appInviteDialog.show(myActivity, content);
        }
    }

    /**
     * 发送分享照片
     *
     * @param callback
     * @author dongbao.you
     * @date 2015年4月20日
     */
    private void postPhotos(ArrayList<SharePhoto> photos, FacebookCallback<Sharer.Result> callback) {
        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().setPhotos(photos).build();
        // Can we present the share dialog for photos?
        boolean allowNoToken = ShareDialog.canShow(SharePhotoContent.class);
        if (allowNoToken) {
            showShareDialog(sharePhotoContent, null);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, callback);
        } else {
            pendingAction = PendingAction.POST_PHOTO;
        }
    }

    /**
     * 显示分享对话框
     *
     * @param callback
     * @author dongbao.you
     * @date 2015年4月20日
     */
    private void showShareDialog(SharePhotoContent sharePhotoContent,
                                 FacebookCallback<Sharer.Result> callback) {
        if (callback == null) {
            callback = shareCallback;
        }
        ShareDialog shareDialog = new ShareDialog(myActivity);
        shareDialog.registerCallback(callbackManager, callback);
        shareDialog.show(sharePhotoContent);
    }

    /**
     * 分享链接
     *
     * @author dongbao.you
     * @date 2015年4月20日
     */
    private void showShareDialog(ShareLinkContent linkContent, FacebookCallback<Sharer.Result> callback) {
        if (callback == null) {
            callback = shareCallback;
        }
        ShareDialog shareDialog = new ShareDialog(myActivity);
        shareDialog.registerCallback(callbackManager, callback);
        shareDialog.show(linkContent);
    }

    /**
     * 发送分享链接
     *
     * @author dongbao.you
     * @date 2015年4月20日
     */
    private void postLink(ShareLinkContent linkContent, FacebookCallback<Sharer.Result> callback) {
        if (callback == null) {
            callback = shareCallback;
        }
        Profile profile = Profile.getCurrentProfile();
        // Can we present the share dialog for regular links?
        boolean canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
        if (canPresentShareDialog) {
            showShareDialog(linkContent, callback);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, callback);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    /**
     * 跳转到fb主页点赞，若本地已安装fb客户端，优先打开fb客户端，否则打开浏览器
     *
     * @return
     */
    public void like(String appPage, String mainPage) {
        try {
            try {
                Uri uri = Uri.parse(appPage);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                myActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Uri uri = Uri.parse(mainPage);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                myActivity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String PENDING_ACTION_BUNDLE_KEY =
            "mobi.dotc.socialnetworks.facebook:PendingAction";

}
