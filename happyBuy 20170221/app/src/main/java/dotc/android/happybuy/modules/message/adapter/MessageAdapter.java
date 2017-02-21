package dotc.android.happybuy.modules.message.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.H5URL;
import dotc.android.happybuy.http.result.PojoMessageItem;
import dotc.android.happybuy.http.result.PojoMessageList;
import dotc.android.happybuy.modules.schema.SchemeProcessor;
import dotc.android.happybuy.ui.activity.WebActivity;
import dotc.android.happybuy.util.DateUtil;

/**
 * Created by 陈尤岁 on 2016/12/15.
 */

public class MessageAdapter extends BaseAdapter{


    private List<PojoMessageItem> mData;
    public MessageAdapter(PojoMessageList pojoMessageList){
        mData = pojoMessageList.list;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public PojoMessageItem getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, final ViewGroup viewGroup) {
        Holder holder = null;
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message,viewGroup,false);
            holder = new Holder(view);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }


        final PojoMessageItem item = getItem(i);
        holder.tvMsgContent.setText(item.content);
        holder.tvMsgTitle.setText(item.title);
        holder.tvMsgTime.setText(DateUtil.getTimeAccordCurrentDate(Long.parseLong(item.create_time)*1000));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SchemeProcessor.handle(view.getContext(),item.url);
            }
        });
        return view;
    }

    static class Holder {
        public TextView tvMsgTitle;
        public TextView tvMsgContent;
        public TextView tvMsgTime;
        Holder(View view) {
            tvMsgContent = (TextView) view.findViewById(R.id.tv_msg_content);
            tvMsgTitle = (TextView) view.findViewById(R.id.tv_msg_title);
            tvMsgTime = (TextView) view.findViewById(R.id.tv_msg_time);
        }
    }

}
