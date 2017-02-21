package dotc.android.happybuy.modules.address.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoAddressItem;

/**
 *
 */
public class AddressSelectAdapter extends BaseAdapter{

    private List<PojoAddressItem> mAddressItems;
    private Context mContext;
    private int mSelectPosition;

    public AddressSelectAdapter(Context context){
        this.mContext = context;
        mAddressItems  = new ArrayList<>();
    }

    public void updateList(List<PojoAddressItem> addressList){
        mAddressItems.clear();
        mAddressItems.addAll(addressList);
        notifyDataSetChanged();
    }

    public void updateSelect(int position){
        mSelectPosition = position;
        notifyDataSetChanged();
    }

    public PojoAddressItem getSelectItem(){
        return mAddressItems.get(mSelectPosition);
    }

    @Override
    public int getCount() {
        return mAddressItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mAddressItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_address_select, null);
            holder.tvName = (TextView)convertView.findViewById(R.id.name);
            holder.tvAddress = (TextView)convertView.findViewById(R.id.address);
            holder.tvNum = (TextView)convertView.findViewById(R.id.num);
            holder.checkBox = (ImageView)convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PojoAddressItem addressItem = mAddressItems.get(position);
        holder.tvName.setText(addressItem.name);
        holder.tvAddress.setText(addressItem.address);
        holder.tvNum.setText(addressItem.mobile);

        if(position == mSelectPosition){
            holder.checkBox.setImageResource(R.drawable.ic_checkbox_check);
        } else {
            holder.checkBox.setImageResource(R.drawable.ic_checkbox_uncheck);
        }
        return convertView;
    }

    private class ViewHolder{
        TextView tvName;
        TextView tvAddress;
        TextView tvNum;
        ImageView checkBox;
    }
}
