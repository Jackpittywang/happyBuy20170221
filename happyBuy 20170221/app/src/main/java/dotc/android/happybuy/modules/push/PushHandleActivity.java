package dotc.android.happybuy.modules.push;

import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;

import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.awarding.AwardingActivity;
import dotc.android.happybuy.modules.coupon.RedPacketActivity;
import dotc.android.happybuy.modules.login.LoginActivity;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.me.MeFragment;
import dotc.android.happybuy.push.PushMessageDispatcher;
import dotc.android.happybuy.push.PushProtocol;
import dotc.android.happybuy.uibase.app.BaseActivity;

/**
 * Created by wangjun on 16/11/11.
 */

public class PushHandleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent()!=null){
            String action = getIntent().getAction();
            HBLog.d(TAG,"onCreate action:"+action);
            if("click.action.my_coupon".equals(action)){
                String from = getIntent().getExtras().getString("from");
                HashMap<String,String> data = bundleToMap(getIntent().getExtras());
                handleCouponNotiClick(from,data);
            } else if("click.action.awarding".equals(action)){
                String from = getIntent().getExtras().getString("from");
                HashMap<String,String> data = bundleToMap(getIntent().getExtras());
                handleAwardingNotiClick(from,data);
            } else if("click.action.awarded".equals(action)){
                String from = getIntent().getExtras().getString("from");
                HashMap<String,String> data = bundleToMap(getIntent().getExtras());
                handleAwardedNotiClick(from,data);
            } else {
                finish();
            }
        } else {
            HBLog.e(TAG+" onCreate handle empty intent");
            finish();
        }
    }

    private HashMap<String,String> bundleToMap(Bundle extras){
        HashMap<String,String> map = new HashMap<>();
        for(String key:extras.keySet()){
            map.put(key,String.valueOf(extras.get(key)));
        }
        return map;
    }

    private void handleCouponNotiClick(String from,HashMap<String,String> data){
        Analytics.sendUIEvent(AnalyticsEvents.Push.Coupon_Receive_Noti, null,null);
        if(isLogin()){
            Intent intent=new Intent(this, RedPacketActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent=new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void handleAwardingNotiClick(String from,HashMap<String,String> data){
        HBLog.e(TAG+" handleAwardingNotiClick from:"+from+" "+data);
        Analytics.sendUIEvent(AnalyticsEvents.Push.Awarding_Receive_Noti, data.get(PushProtocol.Awarding.PRODUCT_ITEM_ID),null);
        if(new PushMessageDispatcher(this).handleAwardingMessage(from,data)){
            finish();
        } else {
            if(MainTabActivity.isInstanceActive()){
                finish();
            } else {
                startActivity(new Intent(this,MainTabActivity.class));
                finish();
            }
        }
    }

    private void handleAwardedNotiClick(String from,HashMap<String,String> data) {
        HBLog.e(TAG + " handleAwardedNotiClick from:" + from + " " + data);
        Analytics.sendUIEvent(AnalyticsEvents.Push.Awarded_Receive_Noti, data.get(PushProtocol.Awarded.PRODUCT_ITEM_ID),null);
        Intent intent = new Intent(this,MainTabActivity.class);
        intent.putExtra(MainTabActivity.EXTRA_TAB_INDEX,MainTabActivity.TAB_INDEX_3);
        Bundle args = new Bundle();
        args.putInt(MeFragment.EXTRA_INDEX,MeFragment.TAB_INDEX_1);
        intent.putExtra(MainTabActivity.EXTRA_TAB_ARGS,args);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
