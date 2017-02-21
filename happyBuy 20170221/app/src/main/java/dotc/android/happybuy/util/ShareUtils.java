package dotc.android.happybuy.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by LiShen on 2016/12/4.
 * Share on different platforms
 */

public class ShareUtils {
    private Activity activity;
    private CallbackManager callbackManager;

    public ShareUtils(Activity activity) {
        this.activity = activity;
        callbackManager = CallbackManager.Factory.create();
    }

    public boolean smsShare(String content) {
        return smsShare("", content);
    }

    public boolean smsShare(String phoneNum, String content) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" +
                    replaceNull(phoneNum)));
            intent.putExtra("sms_body", replaceNull(content));
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean emailShare(String title, String content) {
        return emailShare("", title, content);
    }

    public boolean emailShare(String address, String title, String content) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + replaceNull(address)));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, replaceNull(title));
            emailIntent.putExtra(Intent.EXTRA_TEXT, replaceNull(content));
            activity.startActivity(emailIntent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void shareLinkInFacebook(String title, String contentUrl, String imageUrl, String desc,
                                    int requestCode, FacebookCallback<Sharer.Result> facebookCallback) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(replaceNull(contentUrl)))
                .setContentTitle(replaceNull(title))
                .setImageUrl(Uri.parse(replaceNull(imageUrl)))
                .setQuote(replaceNull(desc))
                .build();
        facebookShareLink(linkContent, requestCode, facebookCallback);
    }

    public void shareLinkInMessenger(String title, String contentUrl, String imageUrl, String desc,
                                     int requestCode, FacebookCallback<Sharer.Result> facebookCallback) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(replaceNull(contentUrl)))
                .setContentTitle(replaceNull(title))
                .setImageUrl(Uri.parse(replaceNull(imageUrl)))
                .setQuote(replaceNull(desc))
                .build();
        messengerShareLink(linkContent, requestCode, facebookCallback);
    }

    public void facebookShareLink(ShareLinkContent shareLinkContent, int requestCode,
                                  FacebookCallback<Sharer.Result> facebookCallback) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, facebookCallback, requestCode);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.show(shareLinkContent);
        }
    }

    public void facebookSharePhoto(SharePhotoContent sharePhotoContent, int requestCode,
                                   FacebookCallback<Sharer.Result> facebookCallback) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, facebookCallback, requestCode);
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareDialog.show(sharePhotoContent);
        }
    }

    public void facebookShareVideo(ShareVideoContent shareVideoContent, int requestCode,
                                   FacebookCallback<Sharer.Result> facebookCallback) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, facebookCallback, requestCode);
        if (ShareDialog.canShow(ShareVideoContent.class)) {
            shareDialog.show(shareVideoContent);
        }
    }

    public void facebookShareMultiMedia(ShareContent shareContent, int requestCode,
                                        FacebookCallback<Sharer.Result> facebookCallback) {
        ShareDialog shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, facebookCallback, requestCode);
        if (ShareDialog.canShow(ShareContent.class)) {
            shareDialog.show(shareContent);
        }
    }

    public void messengerShareLink(ShareLinkContent shareLinkContent, int requestCode,
                                   FacebookCallback<Sharer.Result> facebookCallback) {
        MessageDialog messageDialog = new MessageDialog(activity);
        messageDialog.registerCallback(callbackManager, facebookCallback, requestCode);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            messageDialog.show(shareLinkContent);
        }
    }

    public void messengerSharePhoto(SharePhotoContent sharePhotoContent, int requestCode,
                                    FacebookCallback<Sharer.Result> facebookCallback) {
        MessageDialog messageDialog = new MessageDialog(activity);
        messageDialog.registerCallback(callbackManager, facebookCallback, requestCode);
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            messageDialog.show(sharePhotoContent);
        }
    }

    public void messengerShareVideo(ShareVideoContent shareVideoContent, int requestCode,
                                    FacebookCallback<Sharer.Result> facebookCallback) {
        MessageDialog messageDialog = new MessageDialog(activity);
        messageDialog.registerCallback(callbackManager, facebookCallback, requestCode);
        if (ShareDialog.canShow(ShareVideoContent.class)) {
            messageDialog.show(shareVideoContent);
        }
    }

    public void messengerShareMultiMedia(ShareContent shareContent, int requestCode,
                                         FacebookCallback<Sharer.Result> facebookCallback) {
        MessageDialog messageDialog = new MessageDialog(activity);
        messageDialog.registerCallback(callbackManager, facebookCallback, requestCode);
        if (ShareDialog.canShow(ShareContent.class)) {
            messageDialog.show(shareContent);
        }
    }


    public boolean shareTextInLine(String title, String content) {
        try {
            ComponentName componentName = new ComponentName("jp.naver.line.android"
                    , "jp.naver.line.android.activity.selectchat.SelectChatActivity");
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, replaceNull(title));
            shareIntent.putExtra(Intent.EXTRA_TEXT, replaceNull(content));
            shareIntent.setComponent(componentName);
            activity.startActivity(Intent.createChooser(shareIntent, ""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String replaceNull(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
