package dotc.android.happybuy.modules.message;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

import java.util.HashMap;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.http.HttpProtocol;
import dotc.android.happybuy.http.Network;
import dotc.android.happybuy.http.result.PojoMessageList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.message.adapter.MessageAdapter;
import dotc.android.happybuy.uibase.app.BaseListViewActivity;

/**
 * Created by 陈尤岁 on 2016/12/15.
 */

public class MessageActivity extends BaseListViewActivity<PojoMessageList> {


    private int MAX_PAGE_SIZE = 10;

    @Override
    public BaseAdapter createListAdapter(PojoMessageList pojoMessageList) {
        return new MessageAdapter(pojoMessageList);
    }


    @Override
    public int createTitle() {
        return R.string.title_message;
    }

    @Override
    public int maxPageSize() {
        return MAX_PAGE_SIZE;
    }

    @Override
    public HashMap<String, Object> createParams() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("last_numb", 0);
        map.put("page_size", MAX_PAGE_SIZE);
        map.put("uid", getUid());
        return map;
    }

    @Override
    public String lastNumb() {
        return "last_numb";
    }

    @Override
    public void addMoreData(PojoMessageList pojoMessageList) {
        setCount(pojoMessageList.last_numb);
        getData().list.addAll(pojoMessageList.list);
    }

    @Override
    public String bindUrl() {
        return HttpProtocol.URLS.MESSAGECENTER;
    }

}
