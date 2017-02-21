package dotc.android.happybuy.modules.recharge.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoPay;
import dotc.android.happybuy.modules.recharge.engine.ChannelCell;


public class ChannelClusterLayout extends GridLayout implements View.OnClickListener {

    private Context mContext;
    private List<PojoPay> mPayments;
    private List<ChannelCell> mChannelCells;

    private OnItemCheckedListener mListener;

    private int mCheckedPosition = -1;

    public ChannelClusterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mChannelCells = new ArrayList<>();
        float width = 110 + 2*2;//topup_cell_margin
        float height = 70+2*2;
        setAspectRatio(width * 1.0f / height);
    }

    public void setOnItemCheckedListener(OnItemCheckedListener listener) {
        this.mListener = listener;
    }

    public void addChannelCell(ChannelCell channelCell, int position){
        View view = channelCell.getView(this);
        view.setTag(position);
        view.setOnClickListener(this);
        addView(view);
        mChannelCells.add(channelCell);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        performCheck(position);
    }

    public void performCheck(int position){
        if (mCheckedPosition != position) {
            if(mListener!=null){
                mListener.onItemCheckedChanged(position,mCheckedPosition);
            }
            setChecked(position);
        }
    }

    public void setChecked(int position) {
        if (mCheckedPosition > -1) {
            ChannelCell channelCell = mChannelCells.get(mCheckedPosition);
            channelCell.setChecked(false);
        }
        ChannelCell channelCell = mChannelCells.get(position);
        channelCell.setChecked(true);
        mCheckedPosition = position;
    }

    public interface OnItemCheckedListener{
        void onItemCheckedChanged(int newPos,int oldPos);
    }

}
