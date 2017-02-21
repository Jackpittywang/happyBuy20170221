package dotc.android.happybuy.modules.show.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

public class PrizeShareTipDialog extends Dialog {
    private Activity activity;

    private boolean check = false;
    private ImageView ivPrizeShareTipCheck;

    private PrizeShareTipDialog(Context context) {
        super(context);
    }

    public PrizeShareTipDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
        init();
    }

    private PrizeShareTipDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init() {
        setContentView(R.layout.layout_dialog_prize_share_tip);
        TextView btnPrizeShareTipStartShare = (TextView) findViewById(R.id.btnPrizeShareTipStartShare);
        LinearLayout llPrizeShareTipCheck = (LinearLayout) findViewById(R.id.llPrizeShareTipCheck);
        ivPrizeShareTipCheck = (ImageView) findViewById(R.id.ivPrizeShareTipCheck);

        btnPrizeShareTipStartShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check) {
                    PrefUtils.putBoolean(activity,
                            PrefConstants.SharePrize.TIP_DIALOG_NO_LONGER_PROMPT, true);
                }
                dismiss();
            }
        });
        llPrizeShareTipCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check) {
                    check = false;
                    ivPrizeShareTipCheck.setImageDrawable(activity.getResources().
                            getDrawable(R.drawable.ic_grey_unchecked));
                } else {
                    check = true;
                    ivPrizeShareTipCheck.setImageDrawable(activity.getResources().
                            getDrawable(R.drawable.ic_blue_checked));
                }
            }
        });
        setCancelable(false);
    }
}
