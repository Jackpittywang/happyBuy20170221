package dotc.android.happybuy.ui.help;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

public class ProxyPopupWindow extends PopupWindow {

    boolean canUse;

    public ProxyPopupWindow(Context context) {
        super(context);
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        // TODO Auto-generated method stub
        if (canUse) {
            super.setOnDismissListener(onDismissListener);
            canUse = false;
            return;
        }
        throw new RuntimeException("Warning,Depracated!,Please use PopupWindowProcessor.setOnDismissListener() instead.");
    }

    //	@Override
    //	public void dismiss() {
    //		// TODO Auto-generated method stub
    //		if( canUse ) {
    //			super.dismiss();
    //			canUse = false ;
    //			return ;
    //		}
    //		throw new RuntimeException("Warning,Depracated!,Please use PopupWindowProcessor.setOnDismissListener() instead.");
    //	}

    @Override
    public void showAsDropDown(View anchor) {
        // TODO Auto-generated method stub
        if (canUse) {
            super.showAsDropDown(anchor);
            canUse = false;
            return;
        }
        throw new RuntimeException("Warning,Depracated!,Please use PopupWindowProcessor.setOnDismissListener() instead.");
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        // TODO Auto-generated method stub
        if (canUse) {
            super.showAsDropDown(anchor, xoff, yoff);
            canUse = false;
            return;
        }
        throw new RuntimeException("Warning,Depracated!,Please use PopupWindowProcessor.setOnDismissListener() instead.");
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        // TODO Auto-generated method stub
        if (canUse) {
            super.showAtLocation(parent, gravity, x, y);
            canUse = false;
            return;
        }
        throw new RuntimeException("Warning,Depracated!,Please use PopupWindowProcessor.setOnDismissListener() instead.");
    }
}
