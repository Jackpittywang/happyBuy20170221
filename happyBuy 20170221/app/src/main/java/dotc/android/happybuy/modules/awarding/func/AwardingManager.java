package dotc.android.happybuy.modules.awarding.func;

import android.content.Context;
import android.content.Intent;

import dotc.android.happybuy.modules.awarding.AwardingActivity;
import dotc.android.happybuy.persist.database.DaoProxy;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;

/**
 * Created by wangjun on 16/10/8.
 */
public class AwardingManager {

    private static AwardingManager mInstance;

    public static AwardingManager getInstance(Context context){
        if(mInstance == null){
            synchronized (AwardingManager.class) {
                if (mInstance == null) {
                    mInstance = new AwardingManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private Context mContext;

    private AwardingManager(Context context){
        this.mContext = context;
    }

    private boolean triggerAwardingIfNeeded(String productId,String productItemId,String period){
        if(!DaoProxy.getInstance(mContext).getAwardDao().isExistAward(PrefUtils.getString(PrefConstants.UserInfo.UID, ""),productId,productItemId, Integer.parseInt(period))) {
            DaoProxy.getInstance(mContext).getAwardDao().saveOneAward(PrefUtils.getString(PrefConstants.UserInfo.UID, ""), productId, productItemId, Integer.parseInt(period), "");
            return true;
        }
        return false;
    }

    public boolean showAwardingDialog(Context context,String productId,String productName,String defaultImage,
                                   String productItemId,String period ,String serverTime,String awardTime){
        if(triggerAwardingIfNeeded(productId,productItemId,period)){
            Intent intent = new Intent(context,AwardingActivity.class);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_ID,productId);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_NAME,productName);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_IMAGE,defaultImage);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_ITEM_ID,productItemId);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_PERIOD,period);
            intent.putExtra(AwardingActivity.EXTRA_AWARD_TIME,Long.parseLong(awardTime));
            intent.putExtra(AwardingActivity.EXTRA_SERVER_TIME,Long.parseLong(serverTime));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public boolean showAwardedDialog(Context context,String productId,String productName,String defaultImage,
                                   String productItemId,String period){
        if(triggerAwardingIfNeeded(productId,productItemId,period)){
            Intent intent = new Intent(context,AwardingActivity.class);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_ID,productId);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_NAME,productName);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_IMAGE,defaultImage);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_ITEM_ID,productItemId);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_PERIOD,period);
            intent.putExtra(AwardingActivity.EXTRA_AWARD_TIME,System.currentTimeMillis()+3*1000);
            intent.putExtra(AwardingActivity.EXTRA_SERVER_TIME,System.currentTimeMillis());
            intent.putExtra(AwardingActivity.EXTRA_TYPE,AwardingActivity.TYPE_EXTRA_WIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public boolean remindUserShowAwardedDialog(Context context,String productId,String productName,String defaultImage,
                                     String productItemId,String period){
            Intent intent = new Intent(context,AwardingActivity.class);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_ID,productId);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_NAME,productName);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_IMAGE,defaultImage);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_ITEM_ID,productItemId);
            intent.putExtra(AwardingActivity.EXTRA_PRODUCT_PERIOD,period);
            intent.putExtra(AwardingActivity.EXTRA_AWARD_TIME,System.currentTimeMillis()+3*1000);
            intent.putExtra(AwardingActivity.EXTRA_SERVER_TIME,System.currentTimeMillis());
            intent.putExtra(AwardingActivity.EXTRA_TYPE,AwardingActivity.TYPE_EXTRA_WIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
    }


}
