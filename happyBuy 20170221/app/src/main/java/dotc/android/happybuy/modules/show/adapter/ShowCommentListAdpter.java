package dotc.android.happybuy.modules.show.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoComment;
import dotc.android.happybuy.uibase.widget.CircleImageView;

/**
 * Created by huangli on 16/3/31.
 */
public class ShowCommentListAdpter extends BaseAdapter{
    private Context mContext;
    private List<PojoComment> mlist = new ArrayList<>();
    public ShowCommentListAdpter(Context context){
        mContext = context;
    }

    private class ViewHolder{
        private CircleImageView circleImageView;
        private TextView tvUserName;
        private TextView tvPtime;
        private TextView tvComment;
    }

    public void addData(List<PojoComment> list){
        for (PojoComment pojoComment : list){
            mlist.add(pojoComment);
        }
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        PojoComment pojoComment = mlist.get(position);
        if (convertView == null){
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.item_list_comment, null);
            holder = new ViewHolder();
            holder.tvComment = (TextView)convertView.findViewById(R.id.tv_comment);
            holder.tvPtime = (TextView)convertView.findViewById(R.id.tv_ptime);
            holder.tvUserName = (TextView)convertView.findViewById(R.id.tv_user_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tvComment.setText(pojoComment.comment);
        holder.tvUserName.setText(pojoComment.userName);
        holder.tvPtime.setText(pojoComment.ptime);
        return convertView;
    }
}
