package dotc.android.happybuy.modules.recharge.engine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import dotc.android.happybuy.http.result.PojoPay;

/**
 * Created by wangjun on 16/12/19.
 */

public abstract class ChannelCell {

    protected Context context;
    protected BaseEnv env;
    protected PojoPay pay;

    public ChannelCell(Context context, BaseEnv env, PojoPay pojoPay){
        this.context = context;
        this.env = env;
        this.pay = pojoPay;
    }

    public View getView(ViewGroup parent){

        return null;
    }

    public abstract void setChecked(boolean checked);
}
