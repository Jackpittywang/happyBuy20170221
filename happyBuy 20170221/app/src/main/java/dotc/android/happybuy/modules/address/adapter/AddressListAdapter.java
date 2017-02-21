package dotc.android.happybuy.modules.address.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoAddressItem;
import dotc.android.happybuy.http.result.PojoAddressItemList;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.address.AddressEditActivity;
import dotc.android.happybuy.modules.address.AddressEditActivityVn;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by huangli on 16/4/5.
 */
public class AddressListAdapter extends BaseAdapter{
    public interface OnChoicedNumListener{
        void onChoicedNum(int num);
    }
    private class AddressItem{
        public boolean isdelete;
        public int addressId;
        public String address;
        public String mobile;
        public String name;
        public String state;
        public String city;
        public String ward;
        public String zipcode;
        public AddressItem(int addressId,String address,String mobile,String name,
                           String state,String city,String zipcode,String ward,boolean isdelete){
            this.addressId =addressId;
            this.address = address;
            this.mobile = mobile;
            this.name = name;
            this.state = state;
            this.city = city;
            this.zipcode = zipcode;
            this.isdelete = isdelete;
            this.ward = ward;
        }
    }
    private OnChoicedNumListener onChoicedNumListener;
    private List<AddressItem> addressItems = new ArrayList<>();
    private Context mContext;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_EDIT = 1;
    private int curMode = MODE_NORMAL;
    public void setPojoAddressItemList(PojoAddressItemList pojoAddressItemList){
        addressItems.clear();
        for (PojoAddressItem item : pojoAddressItemList.list){
            addressItems.add(new AddressItem(item.id,item.address,item.mobile,item.name,item.state,item.city,item.zipcode,item.ward,false));
        }
        if (onChoicedNumListener != null){
            onChoicedNumListener.onChoicedNum(getChoicedNum());
        }
        notifyDataSetChanged();
    }

    public int getChoicedNum(){
        int i = 0;
        for (AddressItem item : addressItems){
            if (item.isdelete){
                i++;
            }
        }
        return i;
    }

    public void allIsChoiced(boolean ischoiced){
        for (AddressItem item : addressItems){
            item.isdelete = ischoiced;
        }
        if (onChoicedNumListener != null) {
            onChoicedNumListener.onChoicedNum(getChoicedNum());
        }
        notifyDataSetChanged();
    }

    public AddressListAdapter(Context context,OnChoicedNumListener onChoicedNumListener){
        mContext = context;
        this.onChoicedNumListener = onChoicedNumListener;
    }

    public List<Integer>  getDeleteAddressItemAdrids(){
        List<Integer> list = new ArrayList<>();
        for (AddressItem item : addressItems){
            if (item.isdelete){
                list.add(item.addressId);
            }
        }
        return list;
    }

//    public void addAddressItem(PojoAddressItem item){
//        addressItems.add(new AddressItem(item.id,item.address,item.mobile,item.name,false));
//        notifyDataSetChanged();
//    }
    private class ViewHolder{
        TextView tvName;
        TextView tvAddress;
        TextView tvNum;
        ImageView ivEdit;
        CheckBox checkBox;
    }

    public void setMode(int mode){
        curMode = mode;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return addressItems.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_address, null);
            holder.tvName = (TextView)convertView.findViewById(R.id.name);
            holder.tvAddress = (TextView)convertView.findViewById(R.id.address);
            holder.tvNum = (TextView)convertView.findViewById(R.id.num);
            holder.ivEdit = (ImageView)convertView.findViewById(R.id.iv_edit);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        switch (curMode){
            case MODE_NORMAL:
                holder.checkBox.setVisibility(View.GONE);
                holder.ivEdit.setVisibility(View.VISIBLE);
                break;
            case MODE_EDIT:
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.ivEdit.setVisibility(View.INVISIBLE);
                break;
        }
        holder.tvName.setText(addressItems.get(position).name);
        holder.tvAddress.setText(addressItems.get(position).address);
        holder.tvNum.setText(addressItems.get(position).mobile);
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressItem addressItem = addressItems.get(position);
                Intent intent;
                if(AppUtil.getMetaData(mContext,"country").equals("vn")){
                    intent = new Intent(mContext, AddressEditActivityVn.class);
                    intent.putExtra(AddressEditActivity.EXTRA_WARD, addressItem.ward);
                }else {
                    intent = new Intent(mContext, AddressEditActivity.class);
                }
                intent.putExtra(AddressEditActivity.EXTRA_NAME, addressItem.name);
                intent.putExtra(AddressEditActivity.EXTRA_MOBILE, addressItem.mobile);
                intent.putExtra(AddressEditActivity.EXTRA_ADDRESS, addressItem.address);
                intent.putExtra(AddressEditActivity.EXTRA_PROVINCE, addressItem.state);
                intent.putExtra(AddressEditActivity.EXTRA_DISTRICT, addressItem.city);
                intent.putExtra(AddressEditActivity.EXTRA_POSTCODE, addressItem.zipcode);
                intent.putExtra("mode",AddressEditActivity.MODE_MODIFY);
                intent.putExtra(AddressEditActivity.EXTRA_ID,addressItem.addressId+"");
                mContext.startActivity(intent);
            }
        });
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(addressItems.get(position).isdelete);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HBLog.i("AddressListAdapter position "+position +" isChecked "+isChecked);
                addressItems.get(position).isdelete = isChecked;
                if (onChoicedNumListener != null) {
                    onChoicedNumListener.onChoicedNum(getChoicedNum());
                }
            }
        });
        return convertView;
    }
}
