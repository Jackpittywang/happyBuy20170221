package dotc.android.happybuy.modules.show.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.AbtestConfigBean;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;


/**
 * Created by LiShen
 * on 2016/12/15.
 */

public class PrizeShareAlertDialog extends Dialog {
    private Activity activity;
    private TextView tvPrizeShareAlertContent;

    private PrizeShareAlertDialog(Context context) {
        super(context);
    }

    public PrizeShareAlertDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
        init();
    }

    private PrizeShareAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init() {
        setContentView(R.layout.layout_dialog_prize_share_alert);
        tvPrizeShareAlertContent = (TextView) findViewById(R.id.tvPrizeShareAlertContent);
        findViewById(R.id.btnPrizeShareAlertOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCancelable(false);
    }

    public void show(String content) {
        tvPrizeShareAlertContent.setText(content);
        super.show();
    }
}
