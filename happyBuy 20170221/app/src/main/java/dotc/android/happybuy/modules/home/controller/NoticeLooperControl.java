package dotc.android.happybuy.modules.home.controller;

import android.support.v4.app.Fragment;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoNewNotics;
import dotc.android.happybuy.http.result.PojoNotics;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.home.widget.NoticeTextView;

/**
 * Created by wangjun on 16/12/15.
 */

public class NoticeLooperControl {

    private final String TAG = NoticeLooperControl.class.getSimpleName();
    private Fragment mFragment;

    private NoticeTextView mNoticeTextView;

    public NoticeLooperControl(Fragment fragment) {
        this.mFragment = fragment;
        mNoticeTextView = (NoticeTextView) fragment.getView().findViewById(R.id.notice_textview);
        mNoticeTextView.setOnScrollListener(new NoticeTextView.OnScrollListener() {
            @Override
            public void onItemScrollerDone(PojoNewNotics.Notics notics) {
//                loadNoticsTest(notics == null ? -1 : notics.award_time);
                loadNoticsTest(notics == null ? -1 : 5);
            }
        });
        loadNoticsTest(-1);
    }

    public void resume() {
        mNoticeTextView.resumeAutoPlay();
    }

    public void pause() {
        mNoticeTextView.pauseAutoPlay();
    }

    public void destroy(){

    }

    private void loadNoticsTest(long lastAwardTime) {
        //php 接口
        String url = HttpProtocol.URLS.MAIN_NOTICS;
        Map<String, Object> params = new HashMap<>();
        Network.get(GlobalContext.get()).asyncPost(url, params, new Network.JsonCallBack<PojoNewNotics>() {
            @Override
            public void onSuccess(PojoNewNotics pojoNewNotics) {
                HBLog.d(TAG + " loadNotics onSuccess " + pojoNewNotics);
                if(mFragment.isAdded()){
                    mNoticeTextView.appendNotices(pojoNewNotics);
                }
            }

            @Override
            public void onFailed(int code, String message, Exception e) {
                HBLog.d(TAG + " loadNotics onFailed " + code + " " + message + " " + e);
//                Toast.makeText(GlobalContext.get(), "message" + e, Toast.LENGTH_LONG).show();
            }

            @Override
            public Class<PojoNewNotics> getObjectClass() {
                return PojoNewNotics.class;
            }
        });
    }

}
